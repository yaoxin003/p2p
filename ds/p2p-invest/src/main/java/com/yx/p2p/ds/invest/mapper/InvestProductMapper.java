package com.yx.p2p.ds.invest.mapper;

import com.yx.p2p.ds.model.invest.InvestProduct;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.BaseMapper;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/06/18:07
 */
public interface InvestProductMapper extends BaseMapper<InvestProduct> {

    public InvestProduct selectInvestProductByInvestId(@Param("investId") Integer investId);
}
