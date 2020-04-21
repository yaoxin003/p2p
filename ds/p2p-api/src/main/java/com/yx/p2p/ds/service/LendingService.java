package com.yx.p2p.ds.service;

import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.mq.InvestMQVo;

/**
 * @description:出借单Service
 * @author: yx
 * @date: 2020/04/21/13:12
 */
public interface LendingService {

    //添加新出借单
    public Result addNewLending(InvestMQVo investMQVo);

}
