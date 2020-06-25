package com.yx.p2p.ds.invest.mapper;

import com.yx.p2p.ds.model.invest.Invest;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.math.BigDecimal;
import java.util.List;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/06/18:04
 */
public interface InvestMapper extends Mapper<Invest> {

    public List<Invest> queryInvestListByPagination(
            @Param("invest") Invest invest ,@Param("offset") int offset, @Param("limit") int limit);

    public Integer queryInvestCount(@Param("invest") Invest invest);

    public BigDecimal querySumInvestAmt(Integer customerId);

}
