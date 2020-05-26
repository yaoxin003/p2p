package com.yx.p2p.ds.server;

import com.yx.p2p.ds.model.account.MasterAcc;

/**
 * @description:
 * @author: yx
 * @date: 2020/05/22/15:58
 */
public interface AccountServer {

    public MasterAcc getMasterAccByCustomerId(Integer customerId);
}
