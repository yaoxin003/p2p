package com.yx.p2p.ds.match.mapper;

import com.yx.p2p.ds.model.match.FinanceMatchRes;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.Options;
import tk.mybatis.mapper.additional.insert.InsertListMapper;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.provider.SpecialProvider;
import java.util.List;

/**
 * @description:
 * @author: yx
 * @date: 2020/05/07/19:31
 */
public interface FinanceMatchResMapper extends Mapper<FinanceMatchRes>,
        InsertListMapper<FinanceMatchRes> {


}
