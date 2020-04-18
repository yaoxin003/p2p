package com.yx.p2p.ds.crm.service.impl;

import com.alibaba.fastjson.JSON;
import com.yx.p2p.ds.constant.SysConstant;
import com.yx.p2p.ds.crm.mapper.CustomerMapper;
import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.helper.BeanHelper;
import com.yx.p2p.ds.model.Customer;
import com.yx.p2p.ds.service.util.RedisUtil;
import com.yx.p2p.ds.util.DateUtil;
import com.yx.p2p.ds.service.CrmService;
import com.yx.p2p.ds.vo.CustomerVo;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: yx
 * @date: 2020/03/28/12:43
 */
@Service
public class CrmServiceImpl implements CrmService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private RedisUtil redisUtil;

    public List<Customer> getCustomerListByPagination(CustomerVo customerVo, Integer currentPage, Integer pageSize){
        logger.debug("【currentPage=】" + currentPage + ",pageSize=" + pageSize);
        Integer offset = (currentPage-1) * pageSize;
        logger.debug("offset=" + offset + ",limit=" + pageSize);
        return customerMapper.queryCustomerListByPagination(customerVo,offset,pageSize);
    }

    public Integer queryCustomerCount(CustomerVo customerVo){
        return customerMapper.queryCustomerCount(customerVo);
    }

    /**
     * 1.使用身份证号判断客户是否存在
     * 2.客户不存在添加客户，返回值为1
     * 3.客户存在，返回值为0
     * @param customerVo
     * @return 返回值1：添加客户成功，返回值0：客户已存在或添加客户失败
     */
    public Integer add(CustomerVo customerVo){
        Integer result = 0;
        logger.debug("【customerVo=】" + customerVo);
        //1.使用身份证判断客户是否存在
        boolean flag = this.existCustomerByIdCard(customerVo);
        if(!flag){
            //2.客户不存在，添加客户
            result = this.addCustomer(customerVo);
        }
        return result;
    }

    public CustomerVo getCustomerVoById(Integer id){
        //1.查询参数
        Customer customerParam = new Customer();
        customerParam.setId(id);
        //2.查询数据库
        Customer customer = customerMapper.selectOne(customerParam);
        //3.封装前台对象
        //3.1拷贝属性
        CustomerVo customerVo = new CustomerVo();
        try {
            BeanUtils.copyProperties(customerVo,customer);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        //3.2特殊值设置
        String birthdayStr = DateUtil.dateYMD2Str(customer.getBirthday());
        customerVo.setBirthdayStr(birthdayStr);
        return customerVo;
    }

    /**
     1.使用新身份证号查询客户（缓存和数据库）是否存在，
     2.存在，不处理
     3.不存在，3.1删除旧缓存，3.2修改数据库数据，3.3查询数据库（获得全部字段），加入缓存中。
     * @param customerVo
     * @return
     */
    public Integer update(CustomerVo customerVo){
        Result result = null;
        Integer count = 0;
        try{
            //1.使用新身份证号查询客户（缓存和数据库）是否存在，
            boolean flag = this.existCustomerByIdCard(customerVo);
            if(!flag){//不存在
                //1.删除旧缓存数据
                this.deleteIdCardCache(customerVo.getIdCardOld());
                //2.更新数据库
                count = this.updateDB(customerVo);
                if(count == 1){
                    //3.数据库查询数据
                    Customer customer = this.getCustomerByIdInDB(customerVo.getId());
                    //4.添加缓存
                    this.addIdCardKeyCache(customer);
                }
            }
            return count;
        }catch (Exception e){
            logger.error(e.toString(),e);
            return 0;
        }
    }

    public Customer getCustomerByIdInDB(Integer id){
        logger.debug("【id=】" + id);
        Customer customer = customerMapper.selectByPrimaryKey(id);
        logger.debug("【db.customer=】" + customer);
        return customer;
    }

    private Integer updateDB(CustomerVo customerVo){
        logger.debug("【customerVo=】" + customerVo);
        int count = 0;
        if(customerVo.getId() != null && customerVo.getId() != 0){
            Customer customer = this.buildUpdateCustomerModel(customerVo);
            Example example = new Example(Customer.class);
            example.createCriteria().andEqualTo("id",customer.getId());
            count = customerMapper.updateByExampleSelective(customer,example);
        }else{
            logger.error("【customerVo.id=】" + customerVo.getId() + ",不能db.update");
        }
        logger.debug("【update.db.count=】" + count);
        return count;
    }

    /**
        * @description:
        * 1.批量删除缓存(身份证号码)
        * 2.批量删除数据库（id）
        * @author:  YX
        * @date:    2020/04/05 16:33
        * @param: idArr
        * @return: java.lang.Integer
        * @throws:
        */
    public Integer deleteBatch(Integer[] idArr,String[] idCardArr){
        int count = 0;
        //1.删除旧缓存数据
        this.deleteBatchCacheByIdCardArr(idCardArr);
        //2.批量删除数据库（id）
        count = this.deleteBatchDBByIdArr(idArr);
        return count;
    }


    private Integer deleteBatchCacheByIdCardArr(String[] idCardArr){
        Integer count = 0;
        //1.删除旧缓存数据
        for(String idCard : idCardArr){
            this.deleteIdCardCache(idCard);
            ++count;
        }
        logger.debug("【delete.cache.count=】" + count);
        return count;
    }

    private int deleteBatchDBByIdArr(Integer[] idArr) {
        Integer count = customerMapper.deleteBatchByIdArr(idArr);
        logger.debug("【delete.db.count=】" + count);
        return count;
    }

    /**
        * @description:在缓存中使用身份证获得Customer
        * @author:  YX
        * @date:    2020/04/04 19:31
        * @param: customerVo
        * @return: java.util.List<com.yx.p2p.ds.model.Customer>
        * @throws:
        */
    @Override
    public List<Customer> getCustomerListByIdCardInCache(CustomerVo customerVo) {
        String idCard = customerVo.getIdCard();
        List<Customer> customerList = new ArrayList<>();
        if(StringUtils.isNotBlank(idCard)){
            Jedis cacheJedis = null;
            try{
                cacheJedis = redisUtil.getCacheJedis();
                String cacheKey = SysConstant.CACHE_KEY_PREFIX_CRM_INFO_IDCARD + idCard;
                String customerJSON = cacheJedis.get(cacheKey);
                if(StringUtils.isNotBlank(customerJSON)){
                    Customer customer = JSON.parseObject(customerJSON,Customer.class);
                    customerList.add(customer);
                }
            }catch(Exception e){
                logger.error(e.toString(),e);
            }finally{
                if(cacheJedis != null){
                    cacheJedis.close();
                }
            }
        }
        logger.debug("【cache.customerList=】" + customerList);
        return customerList;
    }


    public void addCache(List<Customer> customerList){
        for(Customer customer:customerList){
            String cacheKey = SysConstant.CACHE_KEY_PREFIX_CRM_INFO_IDCARD + customer.getIdCard();
            this.setCache(cacheKey,customer);
        }
    }

    /**
        * @description: 加入缓存
        * @author:  YX
        * @date:    2020/04/05 8:36
        * @param: customerVo
        * @return: void
        */
    public void addIdCardKeyCache(Customer customer){
        logger.debug("【before.setcache.customer=】"+ customer);
        //2.加入缓存
        String cacheKey = SysConstant.CACHE_KEY_PREFIX_CRM_INFO_IDCARD + customer.getIdCard();
        this.setCache(cacheKey,customer);
    }


    public Integer deleteIdCardCache(String idCard){
        Integer count = 0;
        String cacheKey = SysConstant.CACHE_KEY_PREFIX_CRM_INFO_IDCARD + idCard;
        logger.debug("【del.cache.cacheKey(idCardOld)=】"+ cacheKey);
        //删除缓存
        Jedis cacheJedis = null;
        try{
            cacheJedis = redisUtil.getCacheJedis();
            Long del = cacheJedis.del(cacheKey);
            count = del.intValue();
        }catch(Exception e){
            logger.error(e.toString(),e);
        }finally{
            if(cacheJedis != null){
                cacheJedis.close();
            }
        }
        logger.debug("【del.cache.count=】"+ count);
        return count;
    }

    /**
     * 使用身份证判断客户是否存在（缓存和数据库）
     * 1.查询缓存，判断客户是否存在
     * 2.缓存中不存在，查询数据库，数据库存在返回false，数据库不存在返回true
     * 3.缓存中存在，返回false
     * @param customerVo
     * @return true存在，false不存在
     */
    private boolean existCustomerByIdCard(CustomerVo customerVo) {
        boolean flag = false;
        String idCard = "";
        if(customerVo != null && StringUtils.isNotBlank( idCard=customerVo.getIdCard())){
            flag = this.existCustomerByIdCardInCache(idCard);
            if(!flag){//缓存中不存在
                flag = this.existCustomerByIdCardInDB(idCard);
            }
        }
        return flag;
    }

    /**
     * 在缓存中使用身份证检查客户是否存在
     * @param idCard
     * @return true存在，false不存
     */
    private boolean existCustomerByIdCardInDB(String idCard) {
        boolean flag = false;
        List<Customer> customerList = this.queryCustomerListByIdCard(idCard);
        if(customerList!=null && !customerList.isEmpty()){
            flag = true;
        }
        logger.debug("【exist.db.flag=】" + flag);
        return flag;
    }

    /**
     * 在数据库中使用身份证查询客户
     * @param idCard
     * @return
     */
    private List<Customer> queryCustomerListByIdCard(String idCard) {
        Customer customer = new Customer();
        customer.setIdCard(idCard);
        List<Customer> resCustomers = customerMapper.select(customer);
        logger.debug("【resCustomers=】" + resCustomers);
        return resCustomers;
    }

    /**
     * 在缓存中使用身份证检查客户是否存在
     * @param idCard
     *
     * @return true存在，false不存在
     */
    private boolean existCustomerByIdCardInCache(String idCard) {
        boolean flag = false;
        Jedis cacheJedis = null;
        try{
            cacheJedis = redisUtil.getCacheJedis();
            String cacheKey = SysConstant.CACHE_KEY_PREFIX_CRM_INFO_IDCARD + idCard;
            String customerJSON = cacheJedis.get(cacheKey);
            if(StringUtils.isNotBlank(customerJSON)){
                flag = true;
            }
        }catch(Exception e){
            logger.error(e.toString(),e);
        }finally{
            if(cacheJedis != null){
                cacheJedis.close();
            }
        }
        logger.debug("【exist.cache.flag=】" + flag);
        return flag;
    }

    /**
        * @description: 添加客户
        * 1.将CustomerVo对象构建成Customer对象
        * 2.向数据库添加客户
        * 3.添加缓存中
        * @author:  YX
        * @date:    2020/04/04 15:18
        * @param: customerVo
        * @return: java.lang.Integer
        * @throws:
        */
    private Integer addCustomer(CustomerVo customerVo) {
        Integer result = 0;
        try{
            //1.构建Customer对象
            Customer customer = this.buildAddCustomerModel(customerVo);
            //2.添加客户
            result = this.addCustomerInDB(customer);
            //3.添加缓存
            this.addIdCardKeyCache(customer);
        }catch (Exception e){
            logger.error(e.toString(),e);
        }
        return result;
    }

    public Integer addCustomerInDB(Customer customer){
        int count = customerMapper.insert(customer);
        logger.debug("【insert.customer.db】customer=" + customer);
        logger.debug("【insert.customer.db】count=" + count);
        return count;
    }


    private <T> void setCache(String cacheKey,T t) {
        Jedis cacheJedis = null;
        try{
            cacheJedis = redisUtil.getCacheJedis();
            String cacheValue = JSON.toJSONString(t);
            String res = cacheJedis.set(cacheKey, cacheValue);//返回结果OK
            logger.debug("【add.cache.res=】" + res);
        }catch(Exception e){
            logger.error(e.toString(),e);
        }finally{
            if(cacheJedis != null){
                cacheJedis.close();
            }
        }
    }

    /*
        * @description: 构建Customer对象
        * 1.设置时间和操作人
        * 2.对象拷贝
        * 3.特殊值设置 （Date类型）
        * @author:  YX
        * @date:    2020/04/01 6:41
        * @param: customerVo
        * @return: com.yx.p2p.ds.model.Customer
        */
    private Customer buildAddCustomerModel(CustomerVo customerVo) {
        //1.设置时间，操作人，状态
        BeanHelper.setAddDefaultField(customerVo);

        //对象拷贝，从customerVo拷贝到customer
        Customer customer = this.buildUpdateCustomerModel(customerVo);
        //特殊值设置
        customer.setBirthday(DateUtil.str2Date(customerVo.getBirthdayStr()));
        return customer;
    }


    private Customer buildUpdateCustomerModel(CustomerVo customerVo) {
        Customer customer = new Customer();
        //1.设置时间，操作人
        BeanHelper.setUpdateDefaultField(customerVo);
        //对象拷贝
        try {
            BeanUtils.copyProperties(customer,customerVo);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        //特殊值设置
        customer.setBirthday(DateUtil.str2Date(customerVo.getBirthdayStr()));
        return customer;
    }
}
