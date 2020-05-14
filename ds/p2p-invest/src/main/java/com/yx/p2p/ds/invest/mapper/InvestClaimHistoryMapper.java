package com.yx.p2p.ds.invest.mapper;

import com.yx.p2p.ds.model.invest.InvestClaimHistory;
import tk.mybatis.mapper.additional.insert.InsertListMapper;
import tk.mybatis.mapper.common.Mapper;
import java.util.List;

/**
 * @description:投资债权明细历史
 * @author: yx
 * @date: 2020/05/07/18:05
 */
public interface InvestClaimHistoryMapper extends Mapper<InvestClaimHistory>,InsertListMapper<InvestClaimHistory>{

}
