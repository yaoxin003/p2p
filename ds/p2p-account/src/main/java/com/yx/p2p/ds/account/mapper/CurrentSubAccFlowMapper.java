package com.yx.p2p.ds.account.mapper;

import com.yx.p2p.ds.model.account.CurrentSubAccFlow;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/23/11:51
 */
public interface CurrentSubAccFlowMapper extends Mapper<CurrentSubAccFlow>{
    public void insertBatchCurrentSubAccFlow(List<CurrentSubAccFlow> currentSubAccFlowList);
}
