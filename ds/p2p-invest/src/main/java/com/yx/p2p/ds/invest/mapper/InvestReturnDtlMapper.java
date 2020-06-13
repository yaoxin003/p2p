package com.yx.p2p.ds.invest.mapper;

import com.yx.p2p.ds.model.invest.InvestReturn;
import com.yx.p2p.ds.model.invest.InvestReturnDtl;
import com.yx.p2p.ds.service.mapper.MyInsertListMapper;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: yx
 * @date: 2020/06/04/9:53
 */
public interface InvestReturnDtlMapper extends Mapper<InvestReturnDtl>,MyInsertListMapper<InvestReturnDtl> {

    public Integer queryInvestReturnGroupListCount(Date arriveDate);

    public List<InvestReturn> queryInvestReturnGroupList(@Param("arriveDate") Date arriveDate,
                                                         @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);

}
