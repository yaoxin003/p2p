package com.yx.p2p.ds.crm.mapper;

import com.yx.p2p.ds.model.Crm;
import com.yx.p2p.ds.vo.CrmVo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import java.util.List;

/**
 * @description:
 * @author: yx
 * @date: 2020/03/28/12:49
 */
public interface CrmMapper extends Mapper<Crm>{

    public List<Crm> queryCrmListByPagination(@Param("crmVo") CrmVo crmVo,
                                              @Param("offset") int offset, @Param("limit")  int limit);

    public Integer queryCrmCount(@Param("crmVo") CrmVo crmVo);

    public Integer deleteBatchByIdArr(Integer[] array);
}
