package com.yx.p2p.ds.invest.service.impl;

import com.yx.p2p.ds.enums.investproduct.InvestTypeEnum;
import com.yx.p2p.ds.invest.mapper.InvestMapper;
import com.yx.p2p.ds.model.invest.Invest;
import com.yx.p2p.ds.model.invest.Transfer;
import com.yx.p2p.ds.service.invest.FeeService;
import com.yx.p2p.ds.util.BigDecimalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @description:费用服务Service
 * @author: yx
 * @date: 2020/06/21/19:13
 */
@Service
public class FeeServiceImpl implements FeeService {

    @Autowired
    private InvestMapper investMapper;

    BigDecimal zero = BigDecimal.ZERO;

    //折扣费
    public BigDecimal discountFee(Invest invest, Transfer transfer){
        BigDecimal discountFee = zero;
        if(invest.getInvestType().equals(InvestTypeEnum.FIXED.getState())){
            discountFee = this.fixedDiscountFee(invest, transfer);
        }else if(invest.getInvestType().equals(InvestTypeEnum.NO_FIXED.getState())){
            discountFee = this.noFixedDiscountFee(invest, transfer);
        }
        return discountFee;
    }


    //固定期限：折扣费=转让金额-本金-收益
    private BigDecimal fixedDiscountFee(Invest invest, Transfer transfer){
        BigDecimal discountFee = transfer.getTransferAmt().subtract(transfer.getInvestAmt())
                .subtract(invest.getProfit());
        return discountFee;
    }

    //非固定期限：折扣费
    private BigDecimal noFixedDiscountFee(Invest invest, Transfer transfer){
        return zero;
    }

    //加急费
    public BigDecimal expressFee(Invest invest, Transfer transfer){
        BigDecimal expressFee = zero;
        if(invest.getInvestType().equals(InvestTypeEnum.FIXED.getState())){
            expressFee = this.fixedExpressFee(invest, transfer);
        }else if(invest.getInvestType().equals(InvestTypeEnum.NO_FIXED.getState())){
            expressFee = this.noFixedExpressFee(invest, transfer);
        }
        return expressFee;
    }

    //固定期限：加急费
    private BigDecimal fixedExpressFee(Invest invest, Transfer transfer){
        return zero;
    }

    //非固定期限：加急费
    private BigDecimal noFixedExpressFee(Invest invest, Transfer transfer){
        return BigDecimal.ZERO;
    }

    //服务费
    public BigDecimal serviceFee(Invest invest, Transfer transfer){
        BigDecimal serviceFee = zero;
        if(invest.getInvestType().equals(InvestTypeEnum.FIXED.getState())){
            serviceFee = this.fixedServiceFee(invest, transfer);
        }else if(invest.getInvestType().equals(InvestTypeEnum.NO_FIXED.getState())){
            serviceFee = this.noFixedServiceFee(invest, transfer);
        }
        return serviceFee;
    }

    //固定期限：服务费
    private BigDecimal fixedServiceFee(Invest invest, Transfer transfer){
        return zero;
    }

    //非固定期限：服务费(根据客户投资总金额计算，<10万为2%，>=10万且<100万为1.5%,>=100万为1%)
    private BigDecimal noFixedServiceFee(Invest invest, Transfer transfer){
        BigDecimal sumAmt = investMapper.querySumInvestAmt(invest.getCustomerId());
        BigDecimal feeRate = zero;
        if(sumAmt.compareTo( new BigDecimal("100000")) == -1){// <10万为2%
            feeRate = new BigDecimal("0.02");
        }else if(sumAmt.compareTo( new BigDecimal("100000")) == 1 &&
                sumAmt.compareTo( new BigDecimal("1000000")) == -1 ){// >=10万且<100万为1.5%
            feeRate = new BigDecimal("0.15");
        }else{// >=100万为1%
            feeRate = new BigDecimal("0.01");
        }
        BigDecimal serviceFee = BigDecimalUtil.round2In45(transfer.getInvestAmt().multiply(feeRate));
        return serviceFee;
    }

    //赎回金额
    public BigDecimal redeemAmt(Invest invest, Transfer transfer){
        BigDecimal redeemAmt = zero;
        if(invest.getInvestType().equals(InvestTypeEnum.FIXED.getState())){
            redeemAmt = this.fixedRedeemAmt(invest, transfer);
        }else if(invest.getInvestType().equals(InvestTypeEnum.NO_FIXED.getState())){
            redeemAmt = this.noFixedRedeemAmt(invest, transfer);
        }
        return redeemAmt;
    }

    //固定期限：赎回金额=投资金额+收益
    private BigDecimal fixedRedeemAmt(Invest invest, Transfer transfer){
        return invest.getInvestAmt().add(invest.getProfit());
    }

    //非固定期限：赎回金额=转让金额-加急费-服务费
    private BigDecimal noFixedRedeemAmt(Invest invest, Transfer transfer){
        return transfer.getTransferAmt().subtract(transfer.getExpressFee())
                .subtract(transfer.getServiceFee());
    }
}
