package com.yx.p2p.ds.server;

import com.yx.p2p.ds.model.Customer;
import com.yx.p2p.ds.vo.CustomerVo;

import java.util.List;

/**
 * @description: Dubbo接口
 * @author: yx
 * @date: 2020/04/04/10:03
 */
public interface CrmServer{

    public Integer add(CustomerVo customerVo);

    public List<Customer> search(CustomerVo customerVo, Integer currentPage, Integer pageSize);

    public Integer update(CustomerVo customerVo);

    public Customer getCrmById(Integer crmId);
}
