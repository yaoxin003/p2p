package com.yx.p2p.ds.service.crm;

import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.model.crm.Customer;
import com.yx.p2p.ds.vo.CustomerVo;

import java.util.List;

/**
 * @description: 业务接口
 * @author: yx
 * @date: 2020/03/28/12:44
 */
public interface CrmService {

    public Integer queryCustomerCount(CustomerVo customerVo);

    public List<Customer> getCustomerListByPagination(CustomerVo customerVo, Integer currentPage, Integer pageSize);

    public Result add(CustomerVo customerVo);

    public Result update(CustomerVo customerVo);

    public CustomerVo getCustomerVoById(Integer id);

    public Integer deleteBatch(Integer[] idArr,String[] idCardArr);

    public List<Customer> getCustomerListByIdCardInCache(CustomerVo customerVo);

    public void addCache(List<Customer> customerList);

    public Customer getCustomerByIdInDB(Integer id);

}
