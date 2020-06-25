package com.yx.p2p.ds.service.invest;

import com.yx.p2p.ds.model.invest.Invest;
import com.yx.p2p.ds.model.invest.Transfer;

import java.math.BigDecimal;

/**
 * @description:
 * @author: yx
 * @date: 2020/06/22/9:19
 */
public interface FeeService {

    //折扣费
    public BigDecimal discountFee(Invest invest, Transfer transfer);

    //加急费
    public BigDecimal expressFee(Invest invest, Transfer transfer);

    //服务费
    public BigDecimal serviceFee(Invest invest, Transfer transfer);

    //赎回金额
    public BigDecimal redeemAmt(Invest invest, Transfer transfer);
}
