package com.yx.p2p.ds.service;

import com.yx.p2p.ds.model.Crm;
import com.yx.p2p.ds.vo.CrmVo;

import java.util.List;

/**
 * @description: 业务接口
 * @author: yx
 * @date: 2020/03/28/12:44
 */
public interface CrmService {

    public Integer queryCrmCount(CrmVo crmVo);

    public List<Crm> getCrmListByPagination(CrmVo crmVo, Integer currentPage, Integer pageSize);

    public Integer add(CrmVo crmVo);

    public CrmVo getCrmVoById(Integer id);

    public Integer update(CrmVo crmVo);

    public Integer deleteBatch(Integer[] idArr,String[] idCardArr);

    public List<Crm> getCrmListByIdCardInCache(CrmVo crmVo);

    public void addCache(List<Crm> crmList);

    public Crm getCrmByIdInDB(Integer id);

}
