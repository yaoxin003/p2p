package com.yx.p2p.ds.server;

import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.vo.CrmVo;

/**
 * @description: Dubbo接口
 * @author: yx
 * @date: 2020/04/04/10:03
 */
public interface CrmServer{
    public Integer addCrm(CrmVo crmVo);
}
