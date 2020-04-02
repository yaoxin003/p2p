package com.yx.p2p.ds.crm.service.impl;

import com.yx.p2p.ds.constant.SysConstant;
import com.yx.p2p.ds.helper.BeanHelper;
import com.yx.p2p.ds.util.DateUtil;
import com.yx.p2p.ds.crm.mapper.CrmMapper;
import com.yx.p2p.ds.model.Crm;
import com.yx.p2p.ds.service.CrmService;
import com.yx.p2p.ds.vo.CrmVo;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;
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

    public List<Crm> getCrmListByPagination(CrmVo crmVo, Integer currentPage, Integer pageSize){
        logger.debug("【currentPage=】" + currentPage + ",pageSize=" + pageSize);
        Integer offset = (currentPage-1) * pageSize;
        logger.debug("offset=" + offset + ",limit=" + pageSize);
        return crmMapper.queryCrmListByPagination(crmVo,offset,pageSize);
    }

    public Integer queryCrmCount(CrmVo crmVo){
        return crmMapper.queryCrmCount(crmVo);
    }

    public Integer add(CrmVo crmVo){
        logger.debug("【crmVo=】" + crmVo);
        Crm crm = this.buildAddCrmModel(crmVo);
        return crmMapper.insert(crm);
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

    public Integer deleteBatchByIdArr(Integer[] idArr){
        return crmMapper.deleteBatchByIdArr(idArr);
    }

}
