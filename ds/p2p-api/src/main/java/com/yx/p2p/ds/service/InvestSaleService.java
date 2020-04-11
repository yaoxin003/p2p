package com.yx.p2p.ds.service;

import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.model.Invest;
import com.yx.p2p.ds.vo.InvestVo;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/11/15:28
 */
public interface InvestSaleService  {

    public Result addPayment(InvestVo investVo);
}
