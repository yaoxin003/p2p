package com.yx.p2p.ds.account.mapper;

import com.yx.p2p.ds.model.account.ClaimSubAccFlow;
import com.yx.p2p.ds.service.mapper.MyInsertListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @description:债权分户流水
 * @author: yx
 * @date: 2020/05/06/18:28
 */
public interface ClaimSubAccFlowMapper extends Mapper<ClaimSubAccFlow>,MyInsertListMapper<ClaimSubAccFlow>{

}
