package com.yx.p2p.ds.borrow.mapper;

import com.yx.p2p.ds.model.borrow.DebtDateValue;
import com.yx.p2p.ds.service.mapper.MyInsertListMapper;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.additional.insert.InsertListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: yx
 * @date: 2020/05/30/15:22
 */
public interface DebtDateValueMapper extends Mapper<DebtDateValue> ,InsertListMapper<DebtDateValue>{

}

