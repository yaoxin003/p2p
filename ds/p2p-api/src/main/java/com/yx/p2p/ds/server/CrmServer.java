package com.yx.p2p.ds.server;

import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.model.crm.Customer;
import com.yx.p2p.ds.vo.CustomerVo;
import java.util.List;

/**
 * @description: Dubbo接口
 * @author: yx
 * @date: 2020/04/04/10:03
 */
public interface CrmServer{

    public Result add(CustomerVo customerVo);

    public Result update(CustomerVo customerVo);

    public List<Customer> search(CustomerVo customerVo, Integer currentPage, Integer pageSize);

    public Customer getCustomerById(Integer crmId);
}
