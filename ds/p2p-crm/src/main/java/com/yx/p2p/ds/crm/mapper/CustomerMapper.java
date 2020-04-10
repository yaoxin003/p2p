package com.yx.p2p.ds.crm.mapper;

import com.yx.p2p.ds.model.Customer;
import com.yx.p2p.ds.vo.CustomerVo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @description:
 * @author: yx
 * @date: 2020/03/28/12:49
 */
public interface CustomerMapper extends Mapper<Customer>{

    public List<Customer> queryCustomerListByPagination(@Param("customerVo") CustomerVo customerVo,
                                              @Param("offset") int offset, @Param("limit") int limit);

    public Integer queryCustomerCount(@Param("customerVo") CustomerVo customerVo);

    public Integer deleteBatchByIdArr(Integer[] array);
}
