package com.yx.p2p.ds.crm.service.impl;

import com.alibaba.fastjson.JSON;
import com.yx.p2p.ds.constant.SysConstant;
import com.yx.p2p.ds.helper.BeanHelper;
import com.yx.p2p.ds.service.util.RedisUtil;
import com.yx.p2p.ds.util.DateUtil;
import com.yx.p2p.ds.crm.mapper.CrmMapper;
import com.yx.p2p.ds.model.Crm;
import com.yx.p2p.ds.service.CrmService;
import com.yx.p2p.ds.vo.CrmVo;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import tk.mybatis.mapper.entity.Example;
import java.lang.reflect.InvocationTargetException;
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
    private CrmMapper crmMapper;

    @Autowired
    private RedisUtil redisUtil;

    public List<Crm> getCrmListByPagination(CrmVo crmVo, Integer currentPage, Integer pageSize){
        logger.debug("【currentPage=】" + currentPage + ",pageSize=" + pageSize);
        Integer offset = (currentPage-1) * pageSize;
        logger.debug("offset=" + offset + ",limit=" + pageSize);
        return crmMapper.queryCrmListByPagination(crmVo,offset,pageSize);
    }

    public Integer queryCrmCount(CrmVo crmVo){
        return crmMapper.queryCrmCount(crmVo);
    }

    /**
     * 1.使用身份证号判断客户是否存在
     * 2.客户不存在添加客户，返回值为1
     * 3.客户存在，返回值为0
     * @param crmVo
     * @return 返回值1：添加客户成功，返回值0：客户已存在或添加客户失败
     */
    public Integer add(CrmVo crmVo){
        Integer result = 0;
        logger.debug("【crmVo=】" + crmVo);
        //1.使用身份证判断客户是否存在
        boolean flag = this.checkCrmByIdCard(crmVo);
        if(!flag){
            //2.客户不存在，添加客户
            result = this.addCrm(crmVo);
        }
        return result;
    }


    public CrmVo getCrmVoById(Integer id){
        //1.查询参数
        Crm crmParam = new Crm();
        crmParam.setId(id);
        //2.查询数据库
        Crm crm = crmMapper.selectOne(crmParam);
        //3.封装前台对象
        //3.1拷贝属性
        CrmVo crmVo = new CrmVo();
        try {
            BeanUtils.copyProperties(crmVo,crm);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        //3.2特殊值设置
        String birthdayStr = DateUtil.date2Str(crm.getBirthday());
        crmVo.setBirthdayStr(birthdayStr);
        return crmVo;
    }

    public Integer update(CrmVo crmVo){
        logger.debug("【crmVo=】" + crmVo);
        Crm crm = this.buildUpdateCrmModel(crmVo);
        Example example = new Example(Crm.class);
        example.createCriteria().andEqualTo("id",crm.getId());
        return crmMapper.updateByExampleSelective(crm,example);
    }

    public Integer deleteBatchByIdArr(Integer[] idArr){
        return crmMapper.deleteBatchByIdArr(idArr);
    }

    /**
     * 使用身份证判断客户是否存在（缓存和数据库）
     * 1.查询缓存，判断客户是否存在
     * 2.缓存中不存在，查询数据库，数据库存在返回false，数据库不存在返回true
     * 3.缓存中存在，返回false
     * @param crmVo
     * @return
     */
    private boolean checkCrmByIdCard(CrmVo crmVo) {
        boolean flag = false;
        String idCard = "";
        if(crmVo != null && StringUtils.isNoneBlank( idCard=crmVo.getIdCard())){
            flag = this.checkCrmByIdCardInCache(idCard);
            if(!flag){//缓存中不存在
                flag = this.checkCrmByIdCardInDB(idCard);
            }
        }
        return flag;
    }

    /**
     * 在缓存中使用身份证检查客户是否存在
     * @param idCard
     * @return
     */
    private boolean checkCrmByIdCardInDB(String idCard) {
        boolean flag = false;
        List<Crm> crmList = this.queryCrmListByIdCard(idCard);
        if(crmList!=null && !crmList.isEmpty()){
            flag = true;
        }
        logger.debug("【flag2=】" + flag);
        return flag;
    }

    /**
     * 在数据库中使用身份证查询客户
     * @param idCard
     * @return
     */
    private List<Crm> queryCrmListByIdCard(String idCard) {
        Crm crm = new Crm();
        crm.setIdCard(idCard);
        List<Crm> resCrms = crmMapper.select(crm);
        logger.debug("【resCrms=】" + resCrms);
        return resCrms;
    }

    /**
     * 在缓存中使用身份证检查客户是否存在
     * @param idCard
     * @return
     */
    private boolean checkCrmByIdCardInCache(String idCard) {
        boolean flag = false;
        Jedis cacheJedis = null;
        try{
            cacheJedis = redisUtil.getCacheJedis();
            String cacheKey = SysConstant.CACHE_KEY_PREFIX_CRM_INFO_IDCARD + idCard;
            String crmJSON = cacheJedis.get(cacheKey);
            if(StringUtils.isNotBlank(crmJSON)){
                flag = true;
            }
        }catch(Exception e){
            logger.error(e.toString(),e);
        }finally{
            if(cacheJedis != null){
                cacheJedis.close();
            }
        }
        logger.debug("【flag1=】" + flag);
        return flag;
    }

    /**
        * @description: 添加客户
        * 1.将CrmVo对象构建成Crm对象
        * 2.向数据库添加客户
        * 3.添加缓存中
        * @author:  YX
        * @date:    2020/04/04 15:18
        * @param: crmVo
        * @return: java.lang.Integer
        * @throws:
        */
    private Integer addCrm(CrmVo crmVo) {
        Integer result = 0;
        try{
            //1.构建Crm对象
            Crm crm = this.buildAddCrmModel(crmVo);
            //2.添加客户
            result = crmMapper.insert(crm);
            //3..添加缓存
            String cacheKey = SysConstant.CACHE_KEY_PREFIX_CRM_INFO_IDCARD + crm.getIdCard();
            this.setCache(cacheKey,crm);
        }catch (Exception e){
            logger.error(e.toString(),e);
        }
        return result;
    }

    private <T> void setCache(String cacheKey,T t) {
        Jedis cacheJedis = null;
        try{
            cacheJedis = redisUtil.getCacheJedis();
            String cacheValue = JSON.toJSONString(t);
            cacheJedis.set(cacheKey,cacheValue);
        }catch(Exception e){
            logger.error(e.toString(),e);
        }finally{
            if(cacheJedis != null){
                cacheJedis.close();
            }
        }
    }

    /*
        * @description: 构建Crm对象
        * 1.设置时间和操作人
        * 2.对象拷贝
        * 3.特殊值设置 （Date类型）
        * @author:  YX
        * @date:    2020/04/01 6:41
        * @param: crmVo
        * @return: com.yx.p2p.ds.model.Crm
        */
    private Crm buildAddCrmModel(CrmVo crmVo) {
        Crm crm = new Crm();
        //1.设置时间和操作人
        BeanHelper.setDefaultTimeField(crmVo,"createTime","updateTime");
        Map<String,Integer> operatorMap = new HashMap<>();
        operatorMap.put("creator",SysConstant.operator);
        operatorMap.put("reviser",SysConstant.operator);
        BeanHelper.setDefaultOperatorField(crmVo,operatorMap);
        //对象拷贝
        try {
            BeanUtils.copyProperties(crm,crmVo);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        //特殊值设置
        crm.setBirthday(DateUtil.str2Date(crmVo.getBirthdayStr()));
        return crm;
    }

    private Crm buildUpdateCrmModel(CrmVo crmVo) {
        Crm crm = new Crm();
        //1.设置时间和操作人
        BeanHelper.setDefaultTimeField(crmVo,"updateTime");
        Map<String,Integer> operatorMap = new HashMap<>();
        operatorMap.put("reviser",SysConstant.operator);
        BeanHelper.setDefaultOperatorField(crmVo,operatorMap);

        //对象拷贝
        try {
            BeanUtils.copyProperties(crm,crmVo);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        //特殊值设置
        crm.setBirthday(DateUtil.str2Date(crmVo.getBirthdayStr()));
        return crm;
    }
}
