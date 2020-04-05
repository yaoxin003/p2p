package com.yx.p2p.ds.crm.service.impl;

import com.alibaba.fastjson.JSON;
import com.yx.p2p.ds.constant.SysConstant;
import com.yx.p2p.ds.easyui.Result;
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
        boolean flag = this.existCrmByIdCard(crmVo);
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

    /**
     1.使用新身份证号查询客户（缓存和数据库）是否存在，
     2.存在，不处理
     3.不存在，3.1删除旧缓存，3.2修改数据库数据，3.3查询数据库（获得全部字段），加入缓存中。
     * @param crmVo
     * @return
     */
    public Integer update(CrmVo crmVo){
        Result result = null;
        Integer count = 0;
        try{
            //1.使用新身份证号查询客户（缓存和数据库）是否存在，
            boolean flag = this.existCrmByIdCard(crmVo);
            if(!flag){//不存在
                //1.删除旧缓存数据
                this.deleteIdCardCache(crmVo.getIdCardOld());
                //2.更新数据库
                count = this.updateDB(crmVo);
                if(count == 1){
                    //3.数据库查询数据
                    Crm crm = this.getCrmByIdInDB(crmVo.getId());
                    //4.添加缓存
                    this.addIdCardKeyCache(crm);
                }
            }
            return count;
        }catch (Exception e){
            logger.error(e.toString(),e);
            return 0;
        }
    }

    public Crm getCrmByIdInDB(Integer id){
        logger.debug("【id=】" + id);
        Crm crm = crmMapper.selectByPrimaryKey(id);
        logger.debug("【db.crm=】" + crm);
        return crm;
    }

    private Integer updateDB(CrmVo crmVo){
        logger.debug("【crmVo=】" + crmVo);
        int count = 0;
        if(crmVo.getId() != null && crmVo.getId() != 0){
            Crm crm = this.buildUpdateCrmModel(crmVo);
            Example example = new Example(Crm.class);
            example.createCriteria().andEqualTo("id",crm.getId());
            count = crmMapper.updateByExampleSelective(crm,example);
        }else{
            logger.error("【crmVo.id=】" + crmVo.getId() + ",不能db.update");
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
        Integer count = crmMapper.deleteBatchByIdArr(idArr);
        logger.debug("【delete.db.count=】" + count);
        return count;
    }

    /**
        * @description:在缓存中使用身份证获得Crm
        * @author:  YX
        * @date:    2020/04/04 19:31
        * @param: crmVo
        * @return: java.util.List<com.yx.p2p.ds.model.Crm>
        * @throws:
        */
    @Override
    public List<Crm> getCrmListByIdCardInCache(CrmVo crmVo) {
        String idCard = crmVo.getIdCard();
        List<Crm> crmList = new ArrayList<>();
        if(StringUtils.isNotBlank(idCard)){
            Jedis cacheJedis = null;
            try{
                cacheJedis = redisUtil.getCacheJedis();
                String cacheKey = SysConstant.CACHE_KEY_PREFIX_CRM_INFO_IDCARD + idCard;
                String crmJSON = cacheJedis.get(cacheKey);
                if(StringUtils.isNotBlank(crmJSON)){
                    Crm crm = JSON.parseObject(crmJSON,Crm.class);
                    crmList.add(crm);
                }
            }catch(Exception e){
                logger.error(e.toString(),e);
            }finally{
                if(cacheJedis != null){
                    cacheJedis.close();
                }
            }
        }
        logger.debug("【cache.crmList=】" + crmList);
        return crmList;
    }


    public void addCache(List<Crm> crmList){
        for(Crm crm:crmList){
            String cacheKey = SysConstant.CACHE_KEY_PREFIX_CRM_INFO_IDCARD + crm.getIdCard();
            this.setCache(cacheKey,crm);
        }
    }

    /**
        * @description: 加入缓存
        * @author:  YX
        * @date:    2020/04/05 8:36
        * @param: crmVo
        * @return: void
        */
    public void addIdCardKeyCache(Crm crm){
        logger.debug("【before.setcache.crm=】"+ crm);
        //2.加入缓存
        String cacheKey = SysConstant.CACHE_KEY_PREFIX_CRM_INFO_IDCARD + crm.getIdCard();
        this.setCache(cacheKey,crm);
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
     * @param crmVo
     * @return true存在，false不存在
     */
    private boolean existCrmByIdCard(CrmVo crmVo) {
        boolean flag = false;
        String idCard = "";
        if(crmVo != null && StringUtils.isNotBlank( idCard=crmVo.getIdCard())){
            flag = this.existCrmByIdCardInCache(idCard);
            if(!flag){//缓存中不存在
                flag = this.existCrmByIdCardInDB(idCard);
            }
        }
        return flag;
    }

    /**
     * 在缓存中使用身份证检查客户是否存在
     * @param idCard
     * @return true存在，false不存
     */
    private boolean existCrmByIdCardInDB(String idCard) {
        boolean flag = false;
        List<Crm> crmList = this.queryCrmListByIdCard(idCard);
        if(crmList!=null && !crmList.isEmpty()){
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
     *
     * @return true存在，false不存在
     */
    private boolean existCrmByIdCardInCache(String idCard) {
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
        logger.debug("【exist.cache.flag=】" + flag);
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
            //3.添加缓存
            this.addIdCardKeyCache(crm);
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
        //1.设置时间和操作人
        BeanHelper.setDefaultTimeField(crmVo,"createTime","updateTime");
        Map<String,Integer> operatorMap = new HashMap<>();
        operatorMap.put("creator",SysConstant.operator);
        operatorMap.put("reviser",SysConstant.operator);
        BeanHelper.setDefaultOperatorField(crmVo,operatorMap);
        //对象拷贝，从crmVo拷贝到crm
        Crm crm = this.buildUpdateCrmModel(crmVo);
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
