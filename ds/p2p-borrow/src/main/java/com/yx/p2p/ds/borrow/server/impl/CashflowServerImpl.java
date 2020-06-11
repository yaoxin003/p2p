package com.yx.p2p.ds.borrow.server.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.yx.p2p.ds.model.borrow.Cashflow;
import com.yx.p2p.ds.server.CashflowServer;
import com.yx.p2p.ds.service.CashflowService;
import com.yx.p2p.ds.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @description:现金流
 * @author: yx
 * @date: 2020/06/04/8:04
 */
@Service//dubbo注解，暴露服务
@Component//dubbo需要@Component注解，否则无法识别该服务
public class CashflowServerImpl implements CashflowServer {

    @Autowired
    private CashflowService cashflowService;

    //arriveDate 还款到账日期
    //currentPage 开始页编号
    //pageSize 每页条数
    public List<Cashflow> getCashflowListByPage(Date arriveDate, Integer currentPage, Integer pageSize){
        return cashflowService.getCashflowListByPage(arriveDate, currentPage, pageSize);
    }

    //arriveDate 还款到账日期
    public Integer getCashflowListCount(Date arriveDate){
        return cashflowService.getCashflowListCount(arriveDate);
    }

    // @param: totalCount 总记录数
    // @param: pageSize 页大小（每页记录数）
    // @return: 总页数
    public Integer getPageCountByTotalCountAndPageSize(Integer totalCount,Integer pageSize){
        return PageUtil.getPageCount(totalCount, pageSize);
    }
}
