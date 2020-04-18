package com.yx.p2p.ds.invest.mapper;

import com.yx.p2p.ds.model.Invest;
import com.yx.p2p.ds.vo.InvestVo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/06/18:04
 */
public interface InvestMapper extends Mapper<Invest> {

    public List<InvestVo> queryInvestVoListByPagination(
            @Param("investVo") InvestVo investVo ,@Param("offset") int offset, @Param("limit") int limit);

    public Integer queryInvestVoCount(@Param("investVo") InvestVo investVo);


}
