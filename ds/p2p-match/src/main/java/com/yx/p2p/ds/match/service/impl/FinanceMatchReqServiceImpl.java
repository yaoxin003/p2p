package com.yx.p2p.ds.match.service.impl;

import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.enums.match.MatchRemarkEnum;
import com.yx.p2p.ds.helper.BeanHelper;
import com.yx.p2p.ds.model.match.FinanceMatchReq;
import com.yx.p2p.ds.model.match.FinanceMatchRes;
import com.yx.p2p.ds.model.match.InvestMatchReq;
import com.yx.p2p.ds.service.FinanceMatchReqService;
import com.yx.p2p.ds.util.BigDecimalUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;

/**
 * @description:融资撮合请求
 * @author: yx
 * @date: 2020/05/11/9:10
 */
@Service
public class FinanceMatchReqServiceImpl implements FinanceMatchReqService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * @description:处理融资撮合请求
     * @author:  YX
     * @date:    2020/05/10 18:59
     * @param: financeMatchReq 融资撮合请求（借款或转让）
     * @param: resNoMatchInvestReqList 未撮合投资集合
     * @param: resMatchedInvestReqList 已撮合投资集合
     * @param: resFinanceMatchResList 融资撮合结果
     * @return: com.yx.p2p.ds.easyui.Result
     */
    public Result dealFinanceMatch(FinanceMatchReq financeMatchReq,List<InvestMatchReq> resNoMatchInvestReqList,
                                   List<InvestMatchReq> resMatchedInvestReqList,List<FinanceMatchRes> resFinanceMatchResList){
        logger.debug("【准备进入融资撮合方法中】");
        Result result = Result.error();
        BigDecimal zero = BigDecimal.ZERO;
        String fOrderSn = financeMatchReq.getFinanceOrderSn();
        BigDecimal remainShare = new BigDecimal("1");
        int count = 0;
        Iterator<InvestMatchReq> iterator = resNoMatchInvestReqList.iterator();
        while(iterator.hasNext()){
            InvestMatchReq investReq = iterator.next();
            logger.debug("【借款撮合】第" + ++count + "次撮合，fOrderSn=" + fOrderSn);
            BigDecimal borrowWaitAmt = financeMatchReq.getWaitAmt();
            logger.debug("【借款撮合】fOrderSn=" + fOrderSn + "，借款待撮合金额=" + borrowWaitAmt);
            if(borrowWaitAmt.compareTo(zero) == 1){//需要借款待撮合
                BigDecimal investWaitAmt = investReq.getWaitAmt();
                BigDecimal subAmt =  borrowWaitAmt.subtract(investWaitAmt);//差额=借款待撮合-投资待撮合
                logger.debug("【借款撮合】fOrderSn=" + fOrderSn + "，差额=" + subAmt);
                //构建借款撮合结果
                FinanceMatchRes financeMatchRes = this.buildAddFinanceMatchRes(investReq,financeMatchReq);
                resFinanceMatchResList.add(financeMatchRes);
                if(subAmt.compareTo(zero) == 1){//未撮完
                    logger.debug("【借款撮合】fOrderSn=" + fOrderSn + "，进入未撮完条件，撮合前：剩余借款" + borrowWaitAmt);
                    financeMatchRes.setTradeAmt(investWaitAmt);
                    financeMatchReq.setWaitAmt(subAmt);//融资剩余金额
                    investReq.setWaitAmt(zero);//投资剩余金额
                    //计算撮合比例（需要先计算出交易金额和借款待撮合金额）
                    remainShare = this.calMatchShareAndReturnRemainShare(financeMatchRes,financeMatchReq,remainShare);
                    logger.debug("【借款撮合】fOrderSn=" + fOrderSn + "，未撮完，撮合后：剩余借款" + financeMatchReq.getWaitAmt());
                }else if(subAmt.compareTo(zero) == 0){//撮合完成
                    logger.debug("【借款撮合】fOrderSn=" + fOrderSn + "，进入撮正好条件，撮合前：剩余借款" + borrowWaitAmt);
                    financeMatchRes.setTradeAmt(investWaitAmt);
                    financeMatchReq.setWaitAmt(zero);
                    investReq.setWaitAmt(zero);
                    //计算撮合比例（需要先计算出交易金额和借款待撮合金额）
                    remainShare = this.calMatchShareAndReturnRemainShare(financeMatchRes,financeMatchReq,remainShare);
                    logger.debug("【借款撮合】fOrderSn=" + fOrderSn + "，撮正好，撮合后：剩余借款" + financeMatchReq.getWaitAmt());
                }else{//撮多了
                    logger.debug("【借款撮合】fOrderSn=" + fOrderSn + "，进入撮多了条件，撮合前：剩余借款" + borrowWaitAmt);
                    financeMatchRes.setTradeAmt(borrowWaitAmt);
                    financeMatchReq.setWaitAmt(zero);
                    investReq.setWaitAmt(investWaitAmt.subtract(borrowWaitAmt));
                    //计算撮合比例（需要先计算出交易金额和借款待撮合金额）
                    remainShare = this.calMatchShareAndReturnRemainShare(financeMatchRes,financeMatchReq,remainShare);
                    logger.debug("【借款撮合】fOrderSn=" + fOrderSn + "，撮多了，撮合后：待撮合金额" + financeMatchReq.getWaitAmt());
                }
            }else if(borrowWaitAmt.compareTo(zero) == 0){//撮合成功
                break;
            }else if(borrowWaitAmt.compareTo(zero) == -1){//撮合异常
                logger.debug("【借款撮合】fOrderSn=" + fOrderSn + "，撮合异常，待撮合金额=" + borrowWaitAmt);
                break;
            }
            //处理投资撮合请求
            if(investReq.getWaitAmt().compareTo(zero) == 0){
                iterator.remove();
            }
            if(!resMatchedInvestReqList.contains(investReq)){
                resMatchedInvestReqList.add(investReq);
            }
            //一定不能省去该段代码（比如：若借款金额和投资金额相同，则需要有这段代码）
            if(financeMatchReq.getWaitAmt().compareTo(zero) != 0){//撮合失败，
                logger.debug("【借款撮合】fOrderSn=" + fOrderSn + "，撮合失败，投资人金额不足");
                result = Result.error("【借款撮合失败，投资不足】借款未撮合金额：" + financeMatchReq.getWaitAmt());
            }else{//撮合成功
                logger.debug("【借款撮合】fOrderSn=" + fOrderSn + "，撮合成功");
                result = Result.success();
            }
        }
        return result;
    }

    private FinanceMatchRes buildAddFinanceMatchRes(InvestMatchReq investReq, FinanceMatchReq financeMatchReq) {
        FinanceMatchRes financeMatchRes = new FinanceMatchRes();
        financeMatchRes.setInvestCustomerId(investReq.getInvestCustomerId());
        financeMatchRes.setInvestCustomerName(investReq.getInvestCustomerName());
        financeMatchRes.setFinanceCustomerId(financeMatchReq.getFinanceCustomerId());
        financeMatchRes.setFinanceCustomerName(financeMatchReq.getFinanceCustomerName());
        financeMatchRes.setFinanceAmt(financeMatchReq.getFinanceAmt());
        financeMatchRes.setBorrowProductId(financeMatchReq.getBorrowProductId());
        financeMatchRes.setBorrowProductName(financeMatchReq.getBorrowProductName());
        financeMatchRes.setBorrowYearRate(financeMatchReq.getBorrowYearRate());
        financeMatchRes.setFinanceBizId(financeMatchReq.getFinanceBizId());
        financeMatchRes.setFinanceOrderSn(financeMatchReq.getFinanceOrderSn());
        financeMatchRes.setInvestBizId(investReq.getInvestBizId());
        financeMatchRes.setInvestMatchId(investReq.getId());
        financeMatchRes.setInvestOrderSn(investReq.getInvestOrderSn());
        financeMatchRes.setRemark(MatchRemarkEnum.BORROW.getDesc());
        BeanHelper.setAddDefaultField(financeMatchRes);
        return financeMatchRes;
    }

    //计算撮合比例并返回剩余撮合比例（需要先计算出交易金额和借款待撮合金额）
    // 非最后一次撮合：交易金额/借款金额
    //最后一次撮合：1-前面撮合比例
    private BigDecimal calMatchShareAndReturnRemainShare(FinanceMatchRes financeMatchRes, FinanceMatchReq borrowMatchReq, BigDecimal remainShare) {
        BigDecimal share = BigDecimal.ZERO;
        if(borrowMatchReq.getWaitAmt().compareTo(BigDecimal.ZERO) == 0){
            share = remainShare;
        }else{
            share = BigDecimalUtil.divide2(financeMatchRes.getTradeAmt(),borrowMatchReq.getFinanceAmt());
            remainShare = remainShare.subtract(share);
        }
        financeMatchRes.setMatchShare(share);
        return remainShare;
    }

}
