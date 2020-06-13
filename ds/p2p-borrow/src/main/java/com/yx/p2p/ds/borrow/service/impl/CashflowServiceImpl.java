package com.yx.p2p.ds.borrow.service.impl;

import com.yx.p2p.ds.borrow.mapper.CashflowMapper;
import com.yx.p2p.ds.model.borrow.Cashflow;
import com.yx.p2p.ds.service.borrow.CashflowService;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

/**
 * @description:现金流
 * @author: yx
 * @date: 2020/06/03/18:44
 */
@Service
public class CashflowServiceImpl implements CashflowService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private CashflowMapper cashflowMapper;

    //arriveDate 还款到账日期
    //currentPage 开始页编号
    //pageSize 每页条数
    public List<Cashflow> getCashflowListByPage(Date arriveDate, Integer currentPage, Integer pageSize){
        logger.debug("【分页查询现金流集合】入参：arriveDate=" + arriveDate
                + ",currentPage=" + currentPage + ",pageSize=" + pageSize);
        Integer offset = (currentPage-1) * pageSize;
        List<Cashflow> cashflowList = this.getCashflowListByPageDB(arriveDate, offset, pageSize);
        logger.debug("【分页查询现金流集合】结果：cashflowList=" + cashflowList);
        return cashflowList;
    }

    //arriveDate 还款到账日期
    //offset 开始记录编号，不是开始页编号
    //limit 每页条数
    private List<Cashflow> getCashflowListByPageDB(Date arriveDate, Integer offset, Integer limit){
        logger.debug("【分页查询现金流集合】入参：arriveDate=" + arriveDate
                + ",offset=" + offset + ",limit=" + limit);
        Cashflow param = new Cashflow();
        param.setArriveDate(arriveDate);
        RowBounds rowBounds = new RowBounds(offset,limit);
        List<Cashflow> cashflowList = cashflowMapper.selectByRowBounds(param, rowBounds);
        logger.debug("【分页查询现金流集合】结果：cashflowList=" + cashflowList);
        return cashflowList;
    }

    //arriveDate 还款到账日期
    public Integer getCashflowListCount(Date arriveDate){
        logger.debug("【分页查询现金流总数量】入参：arriveDate=" + arriveDate);
        Cashflow param = new Cashflow();
        param.setArriveDate(arriveDate);
        int count = cashflowMapper.selectCount(param);
        logger.debug("【分页查询现金流总数量】结果：count=" + count);
        return count;
    }
}
