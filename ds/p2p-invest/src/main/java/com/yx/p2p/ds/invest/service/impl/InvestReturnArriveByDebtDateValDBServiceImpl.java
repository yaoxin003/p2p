package com.yx.p2p.ds.invest.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.helper.BeanHelper;
import com.yx.p2p.ds.model.borrow.DebtDateValue;
import com.yx.p2p.ds.model.invest.*;
import com.yx.p2p.ds.server.DebtDateValueServer;
import com.yx.p2p.ds.service.invest.*;
import com.yx.p2p.ds.util.BigDecimalUtil;
import com.yx.p2p.ds.util.PageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.*;

/**
 * @description:投资回款
 * @author: yx
 * @date: 2020/06/04/14:39
 */
@Service
public class InvestReturnArriveByDebtDateValDBServiceImpl implements InvestReturnArriveService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    static final int PAGESIZE = 500;
    @Reference
    private DebtDateValueServer debtDateValueServer;

    @Autowired
    private InvestService investService;

    @Autowired
    private InvestReturnService investReturnService;

    @Autowired
    private ReturnLendingService returnLendingService;

    @Autowired
    private InvestDebtValService investDebtValService;

    //处理投资还款到账，插入投资回款数据
    @Transactional
    public Result dealInvestReturn(Date arriveDate) {
        logger.debug("【使用DebtDateValue处理投资回款数据】");
        logger.debug("【处理投资还款到账，插入投资回款数据】arriveDate=" + arriveDate);
        Result result = Result.error();
        //1.分页查询现金流获得borrowid（按还款到账日期查询）
        Integer debtDateValueTotalCount = debtDateValueServer.queryDebtDateValuePageCount(arriveDate);
        logger.debug("【债务每日价值总数量】debtDateValueTotalCount=" + debtDateValueTotalCount);
        Integer pageCount = PageUtil.getPageCount(debtDateValueTotalCount, PAGESIZE);
        logger.debug("【债务每日价值总页数】pageCount=" + pageCount);
        for (int i = 1; i <= pageCount; i++) {
            List<DebtDateValue> debtDateValueList = debtDateValueServer.queryDebtDateValuePageList(arriveDate, i, PAGESIZE);
            logger.debug("【分页后债务每日价值集合】debtDateValueList=" + debtDateValueList);
            //2.根据1中borrowid查询投资持有债权
            List<Integer> returnBorrowIdList = new ArrayList<>();
            List<Integer> addAmtBorrowIdList = new ArrayList<>();
            Map<Integer, BigDecimal> borrowIdReturnAmtMap = new HashMap<>();//map(borrowId,returnAmt)
            Map<Integer, BigDecimal> borrowIdAddAmtMap = new HashMap<>();//map(borrowId,addAmt)
            BigDecimal zero = BigDecimal.ZERO;
            for (DebtDateValue debtDateValue : debtDateValueList) {
                Integer borrowId = debtDateValue.getBorrowId();
                //债务增值处理：
                BigDecimal addAmt = debtDateValue.getAddAmt();
                borrowIdAddAmtMap.put(borrowId,addAmt);
                addAmtBorrowIdList.add(borrowId);
                //还款处理：
                BigDecimal returnAmt = debtDateValue.getReturnAmt();
                if(returnAmt.compareTo(zero) == 1){
                    returnBorrowIdList.add(borrowId);
                    borrowIdReturnAmtMap.put(borrowId, returnAmt);
                }
            }
            //债务增值处理：
            result = this.addInvestDebtValDtlList(arriveDate,addAmtBorrowIdList,borrowIdAddAmtMap);
           //还款处理：
           result = this.addInvestReturnDtlList(arriveDate,returnBorrowIdList,borrowIdReturnAmtMap);
        }
        //6.分页插入投资债权价值
        result = this.addInvestDebtValListByPage(arriveDate);
        //6.分页插入投资回款，生成出借单发送撮合
        result = this.addInvestReturnListByPage(arriveDate);

        return result;
    }

    private Result addInvestDebtValDtlList(Date arriveDate, List<Integer> addAmtBorrowIdList,
                                        Map<Integer, BigDecimal> borrowIdAddAmtMap) {
        Result result = Result.error();
        addAmtBorrowIdList = this.filterAddAmtBorrowIdList(arriveDate,addAmtBorrowIdList);
        if(!addAmtBorrowIdList.isEmpty()){
            //查询投资债权
            List<InvestClaim> investClaimList = investService.getInvestClaimList(addAmtBorrowIdList);
            List<InvestDebtValDtl> allInvestDebtValDtlList = new ArrayList<>();//所有投资还款明细集合
            //3.按borrowid生成map(borrowId,List<投资回款明细>)
            Map<Integer, List<InvestDebtValDtl>> borrowInvestDebtValDtlListMap = new HashMap<>();//map(borrowId,List<InvestDebtValDtl>)
            result = this.buildBorrowInvestDebtValDtlListMap(arriveDate,
                    borrowIdAddAmtMap,allInvestDebtValDtlList,borrowInvestDebtValDtlListMap,investClaimList);
            //4.计算map中List<InvestDebtValDtl>的addAmt
            result = this.computeAddAmt(borrowInvestDebtValDtlListMap, borrowIdAddAmtMap);
            //5.插入“投资回款明细”
            investDebtValService.insertInvestDebtValDtlList(allInvestDebtValDtlList);

        }
        result = Result.success();
        return result;
    }

    private Result computeAddAmt(Map<Integer, List<InvestDebtValDtl>> borrowInvestDebtValDtlListMap,
                                 Map<Integer, BigDecimal> borrowIdAddAmtMap) {
        logger.debug("【计算map中List<InvestDebtValDtl>的增值】入参：borrowInvestDebtValDtlListMap="
                + borrowInvestDebtValDtlListMap + ",borrowIdAddAmtMap=" + borrowIdAddAmtMap);
        Result result = Result.error();
        for (Integer borrowId : borrowInvestDebtValDtlListMap.keySet()) {
            List<InvestDebtValDtl> investDebtValDtls = borrowInvestDebtValDtlListMap.get(borrowId);
            int size = investDebtValDtls.size();
            BigDecimal remainAddAmt = borrowIdAddAmtMap.get(borrowId);
            if (size > 1) {//持有债权的投资大于1
                int count = 1;
                for (InvestDebtValDtl investDebtValDtl : investDebtValDtls) {
                    remainAddAmt = this.computeRemainAddAmt(count, size, remainAddAmt, investDebtValDtl);//处理一分钱问题
                    ++count;
                }
            }else{
                investDebtValDtls.get(0).setHoldAddAmt(remainAddAmt);
            }
            logger.debug("【计算map中List<InvestDebtValDtl>的增值】结果borrowId="
                    + borrowId + ",investDebtValDtls=" + investDebtValDtls);
        }
        result = Result.success();
        return result;
    }

    private BigDecimal computeRemainAddAmt(int count, int size, BigDecimal remainAddAmt,
                                           InvestDebtValDtl investDebtValDtl) {
        if(count < size){
            BigDecimal addAmt = this.computeHoldAddAmt(investDebtValDtl);
            investDebtValDtl.setHoldAddAmt(addAmt);
            remainAddAmt = remainAddAmt.subtract(addAmt);
        }else{
            investDebtValDtl.setHoldAddAmt(remainAddAmt);//最后一笔=还款-前几笔的和
        }
        return remainAddAmt;
    }

    private Result buildBorrowInvestDebtValDtlListMap(Date arriveDate, Map<Integer, BigDecimal> borrowIdAddAmtMap,
     List<InvestDebtValDtl> allInvestDebtValDtlList, Map<Integer, List<InvestDebtValDtl>> borrowInvestDebtValDtlListMap,
                                                      List<InvestClaim> investClaimList) {
        Result result = Result.error();
        for (InvestClaim investClaim : investClaimList) {
            Integer borrowId = investClaim.getBorrowId();
            InvestDebtValDtl investDebtValDtl = new InvestDebtValDtl();
            investDebtValDtl.setIdStr("next value for MYCATSEQ_P2P_INVEST_DEBT_VAL_DTL");
            investDebtValDtl.setArriveDate(arriveDate);
            investDebtValDtl.setBorrowId(borrowId);
            investDebtValDtl.setInvestId(investClaim.getInvestId());
            investDebtValDtl.setAddAmt(borrowIdAddAmtMap.get(borrowId));
            investDebtValDtl.setHoldShare(investClaim.getHoldShare());
            investDebtValDtl.setInvestCustomerId(investClaim.getInvestCustomerId());
            investDebtValDtl.setInvestCustomerName(investClaim.getInvestCustomerName());
            BeanHelper.setAddDefaultField(investDebtValDtl);
            allInvestDebtValDtlList.add(investDebtValDtl);
            this.putBorrowInvestDebtValDtlListMap(borrowInvestDebtValDtlListMap, investDebtValDtl);
        }
        result = Result.success();
        return result;
    }

    private void putBorrowInvestDebtValDtlListMap(Map<Integer, List<InvestDebtValDtl>> borrowInvestDebtValDtlListMap,
          InvestDebtValDtl investDebtValDtl) {
        Integer borrowId = investDebtValDtl.getBorrowId();
        List<InvestDebtValDtl> investDebtValDtlList = null;
        Object investDebtValDtlListObj = null;
        if( (investDebtValDtlListObj=borrowInvestDebtValDtlListMap.get(borrowId)) == null){
            investDebtValDtlList = new ArrayList<>();
            investDebtValDtlList.add(investDebtValDtl);
            borrowInvestDebtValDtlListMap.put(borrowId,investDebtValDtlList);
        }else{
            investDebtValDtlList = (List<InvestDebtValDtl>)investDebtValDtlListObj;
            investDebtValDtlList.add(investDebtValDtl);
        }
    }


    private Result addInvestReturnDtlList(Date arriveDate, List<Integer> returnBorrowIdList,
                                          Map<Integer, BigDecimal> borrowIdReturnAmtMap) {
        Result result = Result.error();
        // 1.过滤已经生成InvestReturnDtl数据
        returnBorrowIdList = this.filterReturnAmtBorrowIdList(arriveDate,returnBorrowIdList);
        if(!returnBorrowIdList.isEmpty()){
            //查询投资债权
            List<InvestClaim> investClaimList = investService.getInvestClaimList(returnBorrowIdList);
            List<InvestReturnDtl> allInvestReturnDtlList = new ArrayList<>();//所有投资还款明细集合
            //3.按borrowid生成map(borrowId,List<投资回款明细>)
            Map<Integer, List<InvestReturnDtl>> borrowInvestReturnDtlListMap = new HashMap<>();//map(borrowId,List<InvestReturnDtl>)
            result = this.buildBorrowInvestReturnDtlListMap(arriveDate,
                    borrowIdReturnAmtMap,allInvestReturnDtlList,borrowInvestReturnDtlListMap,investClaimList);
            //4.计算map中List<投资回款明细>的回款金额
            result = this.computeReturnAmt(borrowInvestReturnDtlListMap, borrowIdReturnAmtMap);
            //5.插入“投资回款明细”
            investReturnService.insertInvestReturnDtlList(allInvestReturnDtlList);
        }
        result = Result.success();
        return result;
    }

    private List<Integer> filterAddAmtBorrowIdList(Date arriveDate, List<Integer> addAmtBorrowIdList) {
        List<InvestDebtValDtl> investDebtValDtlList = investDebtValService.getInvestDebtValDtlList(arriveDate, addAmtBorrowIdList);
        List<Integer> dbBorrowIdList = new ArrayList<>();
        for (InvestDebtValDtl investDebtValDtl : investDebtValDtlList) {
            dbBorrowIdList.add(investDebtValDtl.getBorrowId());
        }
        addAmtBorrowIdList.removeAll(dbBorrowIdList);
        return addAmtBorrowIdList;
    }

    //过滤已经生成InvestReturnDtl数据
    private List<Integer> filterReturnAmtBorrowIdList(Date arriveDate,List<Integer> borrowIdList) {
        List<InvestReturnDtl> investReturnDtlList = investReturnService.getInvestReturnDtlList(arriveDate, borrowIdList);
        List<Integer> dbBorrowIdList = new ArrayList<>();
        for (InvestReturnDtl investReturnDtl : investReturnDtlList) {
            dbBorrowIdList.add(investReturnDtl.getBorrowId());
        }
        borrowIdList.removeAll(dbBorrowIdList);
        logger.debug("【过滤已经生成InvestReturnDtl数据】borrowIdList=" + borrowIdList);
        return borrowIdList;
    }

    //生成map(borrowId,List<投资回款明细>)
    private Result buildBorrowInvestReturnDtlListMap(Date arriveDate,Map<Integer, BigDecimal> borrowIdReturnAmtMap,
          List<InvestReturnDtl> allInvestReturnDtlList,Map<Integer, List<InvestReturnDtl>> borrowInvestReturnDtlListMap,
                                                     List<InvestClaim> investClaimList) {
        Result result = Result.error();
        for (InvestClaim investClaim : investClaimList) {
            Integer borrowId = investClaim.getBorrowId();
            InvestReturnDtl investReturnDtl = new InvestReturnDtl();
            investReturnDtl.setArriveDate(arriveDate);
            investReturnDtl.setBorrowId(borrowId);
            investReturnDtl.setInvestId(investClaim.getInvestId());
            investReturnDtl.setReturnAmt(borrowIdReturnAmtMap.get(borrowId));
            investReturnDtl.setHoldShare(investClaim.getHoldShare());
            investReturnDtl.setInvestCustomerId(investClaim.getInvestCustomerId());
            investReturnDtl.setInvestCustomerName(investClaim.getInvestCustomerName());
            BeanHelper.setAddDefaultField(investReturnDtl);
            allInvestReturnDtlList.add(investReturnDtl);
            this.putBorrowInvestReturnDtlListMap(borrowInvestReturnDtlListMap, investReturnDtl);
        }
        return result;
    }

    //计算map中List<投资回款明细>的回款金额
    private Result computeReturnAmt(Map<Integer, List<InvestReturnDtl>> borrowInvestReturnDtlListMap,
                                    Map<Integer, BigDecimal> borrowIdReturnAmtMap) {
        logger.debug("【计算map中List<投资回款明细>的回款金额】入参：borrowInvestReturnDtlListMap="
            + borrowInvestReturnDtlListMap + ",borrowIdReturnAmtMap=" + borrowIdReturnAmtMap);
        Result result = Result.error();
        for (Integer borrowId : borrowInvestReturnDtlListMap.keySet()) {
            List<InvestReturnDtl> investReturnDtls = borrowInvestReturnDtlListMap.get(borrowId);
            int size = investReturnDtls.size();
            BigDecimal remainReturnAmt = borrowIdReturnAmtMap.get(borrowId);
            if (size > 1) {//持有债权的投资大于1
                int count = 1;
                for (InvestReturnDtl investReturnDtl : investReturnDtls) {
                    remainReturnAmt = this.computeRemainReturnAmt(count, size, remainReturnAmt, investReturnDtl);//处理一分钱问题
                    ++count;
                }
            }else{
                investReturnDtls.get(0).setHoldReturnAmt(remainReturnAmt);
            }
            logger.debug("【计算map中List<投资回款明细>的回款金额】结果borrowId="
                    + borrowId + ",investReturnDtls=" + investReturnDtls);
        }
        return result;
    }

    //分页插入投资债权价值，生成出借单发送撮合
    private Result addInvestDebtValListByPage(Date arriveDate) {
        Result result = Result.error();
        Integer investReturnGroupListCount = investDebtValService.getInvestDebtValGroupListCount(arriveDate);
        int groupPageCount = PageUtil.getPageCount(investReturnGroupListCount, PAGESIZE);
        for(int i=1; i<=groupPageCount; i++){
            List<InvestDebtVal> investDebtValGroupList =
                    investDebtValService.getInvestDebtValGroupList(arriveDate, i, PAGESIZE);
            //过滤已插入的InvestDebtVal
            investDebtValGroupList = this.filterInvestDebtValList(arriveDate,investDebtValGroupList);
            if(!investDebtValGroupList.isEmpty()){
                for (InvestDebtVal investDebtVal : investDebtValGroupList) {
                    investDebtVal.setIdStr("next value for MYCATSEQ_P2P_INVEST_DEBT_VAL");
                    investDebtVal.setArriveDate(arriveDate);
                    BeanHelper.setAddDefaultField(investDebtVal);
                }
                investDebtValService.insertInvestDebtValList(investDebtValGroupList);
            }
        }
        result = Result.success();
        return result;
    }

    private List<InvestDebtVal> filterInvestDebtValList(Date arriveDate, List<InvestDebtVal> paramInvestDebtValList) {
        List<Integer> investIdList = new ArrayList<>();
        for (InvestDebtVal investDebtVal : paramInvestDebtValList) {
            investIdList.add(investDebtVal.getInvestId());
        }
        List<InvestDebtVal> dbInvestDebtValList = investReturnService.getInvestDebtValList(arriveDate, investIdList);
        //生成参数Map<investId,InvestDebtVal>
        Map<Integer,InvestDebtVal> paramInvestDebtValMap =  new HashMap<>();
        for (InvestDebtVal investDebtVal : paramInvestDebtValList) {
            paramInvestDebtValMap.put(investDebtVal.getInvestId(),investDebtVal);
        }
        //生成数据库Map<investId,InvestDebtVal>
        Map<Integer,InvestDebtVal> dbInvestDebtValMap = new HashMap<>();
        for (InvestDebtVal investDebtVal : dbInvestDebtValList) {
            dbInvestDebtValMap.put(investDebtVal.getInvestId(),investDebtVal);
        }
        //过滤参数Map与数据库Map中重复数据：
        //查找重复investId
        List<Integer> existsInvestIdList = new ArrayList<>();
        for (Integer paramInvestId : paramInvestDebtValMap.keySet()) {
            if(dbInvestDebtValMap.containsKey(paramInvestId)){
                existsInvestIdList.add(paramInvestId);
            }
        }
        //删除参数map中数据
        for (Integer existsInvestId : existsInvestIdList) {
            paramInvestDebtValMap.remove(existsInvestId);
        }
        //组装新的paramMap
        List<InvestDebtVal> resultInvestDebtValList = new ArrayList<>();
        for (Integer paramInvestId : paramInvestDebtValMap.keySet()) {
            resultInvestDebtValList.add(paramInvestDebtValMap.get(paramInvestId));
        }

        //清空缓存
        paramInvestDebtValMap = null;
        dbInvestDebtValMap = null;
        paramInvestDebtValList = null;
        dbInvestDebtValList = null;
        existsInvestIdList = null;
        logger.debug("【过滤后已经生成InvestDebtVal数据】resultInvestDebtValList=" + resultInvestDebtValList);
        return resultInvestDebtValList;
    }

    //分页插入投资回款，生成出借单发送撮合
    private Result addInvestReturnListByPage(Date arriveDate) {
        Result result = Result.error();
        Integer investReturnGroupListCount = investReturnService.getInvestReturnGroupListCount(arriveDate);
        int groupPageCount = PageUtil.getPageCount(investReturnGroupListCount, PAGESIZE);
        for(int i=1; i<=groupPageCount; i++){
            List<InvestReturn> investReturnGroupList =
                    investReturnService.getInvestReturnGroupList(arriveDate, i, PAGESIZE);
            //过滤已插入的InvestReturn
            investReturnGroupList = this.filterInvestReturnList(arriveDate,investReturnGroupList);
            if(!investReturnGroupList.isEmpty()){
                for (InvestReturn investReturn : investReturnGroupList) {
                    investReturn.setArriveDate(arriveDate);
                    BeanHelper.setAddDefaultField(investReturn);
                }
                investReturnService.insertInvestReturnList(investReturnGroupList);
            }
        }
        result = Result.success();
        return result;
    }

    //过滤已经生成InvestReturn数据
    private List<InvestReturn> filterInvestReturnList(Date arriveDate, List<InvestReturn> paramInvestReturnList) {
        List<Integer> investIdList = new ArrayList<>();
        for (InvestReturn investReturn : paramInvestReturnList) {
            investIdList.add(investReturn.getInvestId());
        }
        List<InvestReturn> dbInvestReturnList = investReturnService.getInvestReturnList(arriveDate, investIdList);
        //生成参数Map<investId,InvestReturn>
        Map<Integer,InvestReturn> paramInvestReturnMap =  new HashMap<>();
        for (InvestReturn paramInvestReturn : paramInvestReturnList) {
            paramInvestReturnMap.put(paramInvestReturn.getInvestId(),paramInvestReturn);
        }
        //生成数据库Map<investId,InvestReturn>
        Map<Integer,InvestReturn> dbInvestReturnMap = new HashMap<>();
        for (InvestReturn dbInvestReturn : dbInvestReturnList) {
            dbInvestReturnMap.put(dbInvestReturn.getInvestId(),dbInvestReturn);
        }
        //过滤参数Map与数据库Map中重复数据：
        //查找重复investId
        List<Integer> existsInvestIdList = new ArrayList<>();
        for (Integer paramInvestId : paramInvestReturnMap.keySet()) {
            if(dbInvestReturnMap.containsKey(paramInvestId)){
                existsInvestIdList.add(paramInvestId);
            }
        }
        //删除参数map中数据
        for (Integer existsInvestId : existsInvestIdList) {
            paramInvestReturnMap.remove(existsInvestId);
        }
        //组装新的paramMap
        List<InvestReturn> resultInvestReturnList = new ArrayList<>();
        for (Integer paramInvestId : paramInvestReturnMap.keySet()) {
            resultInvestReturnList.add(paramInvestReturnMap.get(paramInvestId));
        }

        //清空缓存
        paramInvestReturnMap = null;
        dbInvestReturnMap = null;
        paramInvestReturnList = null;
        dbInvestReturnList = null;
        existsInvestIdList = null;
        logger.debug("【过滤后已经生成InvestReturn数据】resultInvestReturnList=" + resultInvestReturnList);
        return resultInvestReturnList;
    }

    //处理持有还款金额一分钱问题
    private BigDecimal computeRemainReturnAmt(Integer count, Integer size, BigDecimal remainReturnAmt, InvestReturnDtl investReturnDtl) {
        if(count < size){
            BigDecimal holdAmt = this.computeHoldReturnAmt(investReturnDtl);
            investReturnDtl.setHoldReturnAmt(holdAmt);
            remainReturnAmt = remainReturnAmt.subtract(holdAmt);
        }else{
            investReturnDtl.setHoldReturnAmt(remainReturnAmt);//最后一笔=还款-前几笔的和
        }
        return remainReturnAmt;
    }

    private BigDecimal computeHoldAddAmt(InvestDebtValDtl investDebtValDtl){
        BigDecimal holdAddAmt = investDebtValDtl.getAddAmt().multiply(investDebtValDtl.getHoldShare());
        holdAddAmt = BigDecimalUtil.round2In45(holdAddAmt);
        return holdAddAmt;
    }

    private BigDecimal computeHoldReturnAmt(InvestReturnDtl investReturnDtl){
        BigDecimal holdAmt = investReturnDtl.getReturnAmt().multiply(investReturnDtl.getHoldShare());
        holdAmt = BigDecimalUtil.round2In45(holdAmt);
        return holdAmt;
    }

    private void putBorrowInvestReturnDtlListMap(Map<Integer, List<InvestReturnDtl>> borrowInvestReturnDtlListMap,
                                                 InvestReturnDtl investReturnDtl) {
        Integer borrowId = investReturnDtl.getBorrowId();
        List<InvestReturnDtl> investReturnDtlList = null;
        Object investReturnDtlListObj = null;
        if( (investReturnDtlListObj=borrowInvestReturnDtlListMap.get(borrowId)) == null){
            investReturnDtlList = new ArrayList<>();
            investReturnDtlList.add(investReturnDtl);
            borrowInvestReturnDtlListMap.put(borrowId,investReturnDtlList);
        }else{
            investReturnDtlList = (List<InvestReturnDtl>)investReturnDtlListObj;
            investReturnDtlList.add(investReturnDtl);
        }
    }

}
