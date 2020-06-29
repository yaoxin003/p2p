package com.yx.p2p.ds.timer.biz.service;

import com.yx.p2p.ds.easyui.Result;
import java.util.Date;

public interface CashflowJobService {

    //借款人还款支付
    public Result borrowReturnPayment(Date returnDate);

}
