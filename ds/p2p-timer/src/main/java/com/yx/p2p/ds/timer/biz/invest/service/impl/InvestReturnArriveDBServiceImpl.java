package com.yx.p2p.ds.timer.biz.invest.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.helper.BeanHelper;
import com.yx.p2p.ds.model.borrow.Cashflow;
import com.yx.p2p.ds.model.invest.InvestClaim;
import com.yx.p2p.ds.model.invest.InvestReturn;
import com.yx.p2p.ds.model.invest.InvestReturnDtl;
import com.yx.p2p.ds.server.CashflowServer;
import com.yx.p2p.ds.server.InvestServer;
import com.yx.p2p.ds.service.InvestReturnArriveService;
import com.yx.p2p.ds.service.InvestReturnService;
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
public class InvestReturnArriveDBServiceImpl implements InvestReturnArriveService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    static final int PAGESIZE = 500;

    @Reference
    private CashflowServer cashflowServer;

    @Reference
    private InvestServer investServer;

    @Autowired
    private InvestReturnService investReturnService;

    //处理投资还款到账，插入投资回款数据
    @Transactional
    public Result dealInvestReturn(Date arriveDate) {
        Result result = Result.error();
        //1.分页查询现金流获得borrowid（按还款到账日期查询）
        Integer cashflowTotalCount = cashflowServer.getCashflowListCount(arriveDate);
        logger.debug("【现金流总数量】cashflowTotalCount=" + cashflowTotalCount);
        Integer pageCount = PageUtil.getPageCount(cashflowTotalCount, PAGESIZE);
        logger.debug("【现金流总页数】pageCount=" + pageCount);
        for (int i = 1; i <= pageCount; i++) {
            List<Cashflow> cashflowList = cashflowServer.getCashflowListByPage(arriveDate, i, PAGESIZE);
            logger.debug("【分页后现金流集合】cashflowList=" + cashflowList);
            //2.根据1中borrowid查询投资持有债权
            List<Integer> borrowIdList = new ArrayList<>();
            Map<Integer, BigDecimal> borrowIdReturnAmtMap = new HashMap<>();//map(borrowId,returnAmt)
            for (Cashflow cashflow : cashflowList) {
                Integer borrowId = cashflow.getBorrowId();
                borrowIdList.add(borrowId);
                borrowIdReturnAmtMap.put(borrowId, cashflow.getMonthPayment());
            }
           //过滤已经生成InvestReturnDtl数据
            borrowIdList = this.filterBorrowIdList(arriveDate,borrowIdList);
            if(!borrowIdList.isEmpty()){
                List<InvestClaim> investClaimList = investServer.getInvestClaimList(borrowIdList);
                List<InvestReturnDtl> allInvestReturnDtlList = new ArrayList<>();//所有投资还款明细集合
                //3.按borrowid生成map(borrowId,List<投资回款明细>)
                Map<Integer, List<InvestReturnDtl>> borrowInvestReturnDtlListMap = new HashMap<>();//map(borrowId,List<投资回款明细>)
                result = this.buildBorrowInvestReturnDtlListMap(arriveDate,
                        borrowIdReturnAmtMap,allInvestReturnDtlList,borrowInvestReturnDtlListMap,investClaimList);
                //4.计算map中List<投资回款明细>的回款金额
                result = this.computeReturnAmt(borrowInvestReturnDtlListMap, borrowIdReturnAmtMap);
                //5.插入“投资回款明细”
                investReturnService.insertInvestReturnDtlList(allInvestReturnDtlList);
            }
        }
        //6.分页插入投资回款，生成出借单发送撮合
        result = this.addInvestReturnListByPage(arriveDate);
        return result;
    }

    //过滤已经生成InvestReturnDtl数据
    private List<Integer> filterBorrowIdList(Date arriveDate,List<Integer> borrowIdList) {
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
            investReturnDtl.setHoldReturnAmt(remainReturnAmt);//随后一笔=还款-前几笔的和
        }
        return remainReturnAmt;
    }

    private BigDecimal computeHoldReturnAmt(InvestReturnDtl investReturnDtl){
        BigDecimal holdAmt = investReturnDtl.getReturnAmt().multiply(investReturnDtl.getHoldShare());
        holdAmt = BigDecimalUtil.round4In45(holdAmt);
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
