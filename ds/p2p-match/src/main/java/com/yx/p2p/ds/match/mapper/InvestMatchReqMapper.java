package com.yx.p2p.ds.match.mapper;

import com.yx.p2p.ds.model.match.InvestMatchReq;
import com.yx.p2p.ds.service.mapper.MyInsertListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/26/14:25
 */
public interface InvestMatchReqMapper extends Mapper<InvestMatchReq> ,MyInsertListMapper<InvestMatchReq>{

}
