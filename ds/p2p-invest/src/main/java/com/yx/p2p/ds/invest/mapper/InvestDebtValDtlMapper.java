package com.yx.p2p.ds.invest.mapper;

import com.yx.p2p.ds.model.invest.InvestDebtVal;
import com.yx.p2p.ds.model.invest.InvestDebtValDtl;
import com.yx.p2p.ds.service.mapper.MyInsertListMapper;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;
import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: yx
 * @date: 2020/06/13/9:30
 */
public interface InvestDebtValDtlMapper extends Mapper<InvestDebtValDtl>, MyInsertListMapper<InvestDebtValDtl>{

    public Integer queryInvestDebtValGroupListCount(Date arriveDate);

    //arriveDate 到账日期
    //currentPage 开始页编号
    //pageSize 每页条数
    public List<InvestDebtVal> queryInvestDebtValGroupList(
            @Param("arriveDate") Date arriveDate,
            @Param("offset") Integer offset, @Param("pageSize") Integer pageSize);


}
