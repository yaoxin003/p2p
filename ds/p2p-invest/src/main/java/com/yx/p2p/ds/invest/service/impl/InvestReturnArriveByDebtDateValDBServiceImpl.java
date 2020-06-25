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
        Integer debtDateValueTotalCount = debtDateValueServer.getDebtDateValueCount(arriveDate);
        logger.debug("【债务每日价值总数量】debtDateValueTotalCount=" + debtDateValueTotalCount);
        Integer pageCount = PageUtil.getPageCount(debtDateValueTotalCount, PAGESIZE);
        logger.debug("【债务每日价值总页数】pageCount=" + pageCount);
        for (int i = 1; i <= pageCount; i++) {
            List<DebtDateValue> debtDateValueList = debtDateValueServer.getDebtDateValuePageList(arriveDate, i, PAGESIZE);
            logger.debug("【分页后债务每日价值集合】debtDateValueList=" + debtDateValueList);
            //2.根据1中borrowid查询投资持有债权
            List<Integer> returnBorrowIdList = new ArrayList<>();
            List<Integer> borrowIdList = new ArrayList<>();
            Map<Integer,BigDecimal> debtValueMap = new HashMap<>();//map(borrowId,debtValue)
            Map<Integer, BigDecimal> returnAmtMap = new HashMap<>();//map(borrowId,returnAmt)
            Map<Integer, BigDecimal> addAmtMap = new HashMap<>();//map(borrowId,addAmt)
            BigDecimal zero = BigDecimal.ZERO;
            for (DebtDateValue debtDateValue : debtDateValueList) {
                Integer borrowId = debtDateValue.getBorrowId();
                //债务价值
                debtValueMap.put(borrowId,debtDateValue.getValue());
                //债务增值处理：
                BigDecimal addAmt = debtDateValue.getAddAmt();
                addAmtMap.put(borrowId,addAmt);
                borrowIdList.add(borrowId);
                //还款处理：
                BigDecimal returnAmt = debtDateValue.getReturnAmt();
                if(returnAmt.compareTo(zero) == 1){
                    returnBorrowIdList.add(borrowId);
                    returnAmtMap.put(borrowId, returnAmt);
                }
            }
            //处理InvestDebtValDtl
            result = this.addInvestDebtValDtlList(arriveDate,borrowIdList,addAmtMap,returnAmtMap,debtValueMap);
        }
        //6.分页插入投资债权价值
        result = this.addInvestDebtValListByPage(arriveDate);
        return result;
    }

    /**
        * @param: arriveDate 到账时间
        * @param: borrowIdList
        * @param: addAmtMap 债务增值金额Map
        * @param: returnAmtMap 还款金额Map
        * @param: debtValueMap 债务价值金额
        */
    private Result addInvestDebtValDtlList(Date arriveDate, List<Integer> borrowIdList,
            Map<Integer, BigDecimal> addAmtMap, Map<Integer, BigDecimal> returnAmtMap, Map<Integer,BigDecimal> debtValueMap) {
        Result result = Result.error();
        borrowIdList = this.filterBorrowIdByInvestDebtValDtl(arriveDate,borrowIdList);
        if(!borrowIdList.isEmpty()){
            //查询投资债权
            List<InvestClaim> investClaimList = investService.getInvestClaimList(borrowIdList);
            List<InvestDebtValDtl> allInvestDebtValDtlList = new ArrayList<>();//所有投资还款明细集合
            //3.按borrowid生成map(borrowId,List<投资债权价值明细>)
            Map<Integer, List<InvestDebtValDtl>> borrowInvestDebtValDtlListMap = new HashMap<>();//map(borrowId,List<InvestDebtValDtl>)
            result = this.buildBorrowInvestDebtValDtlListMap(arriveDate,addAmtMap,returnAmtMap,debtValueMap,
                    allInvestDebtValDtlList,borrowInvestDebtValDtlListMap,investClaimList);
            //4.计算map中List<InvestDebtValDtl>的addAmt
            result = this.computeAddAmtAndReturnAmt(borrowInvestDebtValDtlListMap, addAmtMap,returnAmtMap,debtValueMap);
            //5.插入“投资债权价值明细”
            investDebtValService.insertInvestDebtValDtlList(allInvestDebtValDtlList);
        }
        result = Result.success();
        return result;
    }

    private Result computeAddAmtAndReturnAmt(Map<Integer, List<InvestDebtValDtl>> borrowInvestDebtValDtlListMap,
      Map<Integer, BigDecimal> addAmtMap,Map<Integer, BigDecimal> returnAmtMap, Map<Integer,BigDecimal> debtValueMap) {
        logger.debug("【计算InvestDebtValDtl】入参：borrowInvestDebtValDtlListMap={},addAmtMap={},returnAmtMap={}"
                , borrowInvestDebtValDtlListMap,addAmtMap,returnAmtMap);
        Result result = Result.error();
        BigDecimal zero = BigDecimal.ZERO;
        for (Integer borrowId : borrowInvestDebtValDtlListMap.keySet()) {
            List<InvestDebtValDtl> investDebtValDtls = borrowInvestDebtValDtlListMap.get(borrowId);
            int size = investDebtValDtls.size();
            BigDecimal remainAddAmt = addAmtMap.get(borrowId);
            BigDecimal remainReturnAmt = zero;
            BigDecimal remainDebtValue = debtValueMap.get(borrowId);
            if(!returnAmtMap.isEmpty()){
                remainReturnAmt = returnAmtMap.get(borrowId);
            }
            if (size > 1) {//持有债权的投资大于1
                int count = 1;
                for (InvestDebtValDtl investDebtValDtl : investDebtValDtls) {
                    remainAddAmt = this.computeRemainAddAmt(count, size, remainAddAmt, investDebtValDtl);//处理一分钱问题
                    remainReturnAmt = this.computeRemainReturnAmt(count, size, remainReturnAmt, investDebtValDtl);//处理一分钱问题
                    remainDebtValue = this.computeRemainDebtValue(count, size, remainDebtValue, investDebtValDtl);//处理一分钱问题
                    ++count;
                }
            }else{
                investDebtValDtls.get(0).setHoldAddAmt(remainAddAmt);
                investDebtValDtls.get(0).setHoldReturnAmt(remainReturnAmt);
                investDebtValDtls.get(0).setHoldDebtValue(remainDebtValue);
            }
            logger.debug("【计算InvestDebtValDtl】结果borrowId={},investDebtValDtls={}"
                    , borrowId , investDebtValDtls);
        }
        result = Result.success();
        return result;
    }

    private BigDecimal computeRemainDebtValue(int count, int size, BigDecimal remainDebtValue, InvestDebtValDtl investDebtValDtl) {
        if(count < size){
            BigDecimal debtValueAmt = this.computeHoldAmt(investDebtValDtl.getDebtValue(),investDebtValDtl.getHoldShare());
            investDebtValDtl.setHoldDebtValue(debtValueAmt);
            remainDebtValue = remainDebtValue.subtract(debtValueAmt);
        }else{
            investDebtValDtl.setHoldDebtValue(remainDebtValue);//最后一笔=还款-前几笔的和
        }
        return remainDebtValue;
    }

    private BigDecimal computeRemainAddAmt(int count, int size, BigDecimal remainAddAmt,
                                           InvestDebtValDtl investDebtValDtl) {
        if(count < size){
            BigDecimal addAmt = this.computeHoldAmt(investDebtValDtl.getAddAmt(),investDebtValDtl.getHoldShare());
            investDebtValDtl.setHoldAddAmt(addAmt);
            remainAddAmt = remainAddAmt.subtract(addAmt);
        }else{
            investDebtValDtl.setHoldAddAmt(remainAddAmt);//最后一笔=还款-前几笔的和
        }
        return remainAddAmt;
    }

    private BigDecimal computeRemainReturnAmt(int count, int size, BigDecimal remainReturnAmt,
                                           InvestDebtValDtl investDebtValDtl) {
        if(count < size){
            BigDecimal returnAmt = this.computeHoldAmt(investDebtValDtl.getReturnAmt(),investDebtValDtl.getHoldShare());
            investDebtValDtl.setHoldReturnAmt(returnAmt);
            remainReturnAmt = remainReturnAmt.subtract(returnAmt);
        }else{
            investDebtValDtl.setHoldReturnAmt(remainReturnAmt);//最后一笔=还款-前几笔的和
        }
        return remainReturnAmt;
    }

    /**
        * @param: arriveDate 到账时间
        * @param: addAmtMap 债务增值Map
        * @param: returnAmtMap 债务还款Map
        * @param: debtValueMap 债务价值Map
        * @param: allInvestDebtValDtlList
        * @param: borrowInvestDebtValDtlListMap
        * @param: investClaimList
        */
    private Result buildBorrowInvestDebtValDtlListMap(Date arriveDate, Map<Integer, BigDecimal> addAmtMap,
            Map<Integer,BigDecimal> returnAmtMap,Map<Integer,BigDecimal> debtValueMap, List<InvestDebtValDtl> allInvestDebtValDtlList,
           Map<Integer, List<InvestDebtValDtl>> borrowInvestDebtValDtlListMap, List<InvestClaim> investClaimList) {
        Result result = Result.error();
        BigDecimal returnAmt = BigDecimal.ZERO;
        for (InvestClaim investClaim : investClaimList) {
            Integer borrowId = investClaim.getBorrowId();
            InvestDebtValDtl investDebtValDtl = new InvestDebtValDtl();
            investDebtValDtl.setIdStr("next value for MYCATSEQ_P2P_INVEST_DEBT_VAL_DTL");
            investDebtValDtl.setArriveDate(arriveDate);
            investDebtValDtl.setBorrowId(borrowId);
            investDebtValDtl.setInvestId(investClaim.getInvestId());
            investDebtValDtl.setDebtValue(debtValueMap.get(borrowId));
            investDebtValDtl.setAddAmt(addAmtMap.get(borrowId));
            if(!returnAmtMap.isEmpty()){
                returnAmt = returnAmtMap.get(borrowId);
            }
            investDebtValDtl.setReturnAmt(returnAmt);
            investDebtValDtl.setDebtValue(debtValueMap.get(borrowId));
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


    private List<Integer> filterBorrowIdByInvestDebtValDtl(Date arriveDate, List<Integer> borrowIdList) {
        List<InvestDebtValDtl> investDebtValDtlList = investDebtValService.getInvestDebtValDtlList(arriveDate, borrowIdList);
        List<Integer> dbBorrowIdList = new ArrayList<>();
        for (InvestDebtValDtl investDebtValDtl : investDebtValDtlList) {
            dbBorrowIdList.add(investDebtValDtl.getBorrowId());
        }
        borrowIdList.removeAll(dbBorrowIdList);
        return borrowIdList;
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
        List<InvestDebtVal> dbInvestDebtValList = investDebtValService.getInvestDebtValList(arriveDate, investIdList);
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


    private BigDecimal computeHoldAmt(BigDecimal amt,BigDecimal holdShare){
        BigDecimal holdAmt = amt.multiply(holdShare);
        holdAmt = BigDecimalUtil.round2In45(holdAmt);
        return holdAmt;
    }



}
