package com.yx.p2p.ds.crm;

import com.yx.p2p.ds.crm.mapper.CustomerMapper;
import com.yx.p2p.ds.model.crm.Customer;
import com.yx.p2p.ds.util.DateUtil;
import com.yx.p2p.ds.vo.CustomerVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @description:
 * @author: yx
 * @date: 2020/06/25/9:00
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class CustomerMapperTest {

    @Autowired
    private CustomerMapper customerMapper;

    @Test
    public void testQueryCustomerListByPagination(){
        CustomerVo customerVo = new CustomerVo();
        customerVo.setBirthday(DateUtil.str2Date("1985-07-18"));
        List<Customer> customers = customerMapper.queryCustomerListByPagination(customerVo, 0, 100);
        System.out.println("=================" + customers);
    }
}
