package com.yx.p2p.ds.server;

import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.model.Crm;
import com.yx.p2p.ds.vo.CrmVo;

import java.util.List;

/**
 * @description: Dubbo接口
 * @author: yx
 * @date: 2020/04/04/10:03
 */
public interface CrmServer{

    public Integer addCrm(CrmVo crmVo);

    public List<Crm> search(CrmVo crmVo, Integer currentPage, Integer pageSize);
}
