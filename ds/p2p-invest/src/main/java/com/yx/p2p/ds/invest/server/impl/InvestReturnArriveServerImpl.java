package com.yx.p2p.ds.invest.server.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.model.invest.InvestDebtVal;
import com.yx.p2p.ds.server.InvestReturnArriveServer;
import com.yx.p2p.ds.service.invest.InvestDebtValService;
import com.yx.p2p.ds.service.invest.InvestReturnArriveService;
import com.yx.p2p.ds.service.invest.ReturnLendingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.List;

/**
 * @description:投资回款到账
 * @author: yx
 * @date: 2020/06/11/16:36
 */
@Service
@Component//dubbo需要@Component注解，否则无法识别该服务
public class InvestReturnArriveServerImpl implements InvestReturnArriveServer {

    @Qualifier(value="investReturnArriveByDebtDateValDBServiceImpl")
    @Autowired
    private InvestReturnArriveService investReturnArriveService;

    @Autowired
    private ReturnLendingService returnLendingService;

    @Autowired
    private InvestDebtValService investDebtValService;

    //处理投资还款到账，插入投资回款数据
    public Result dealInvestReturn(Date arriveDate){
        return investReturnArriveService.dealInvestReturn(arriveDate);
    }

    //插入出借单并批量发送投资撮合请求
    public Result dealInvestReturnLending(Date arriveDate){
        return returnLendingService.dealInvestReturnLending(arriveDate);
    }

    public Integer getInvestDebtValCount(Date arriveDate){
        return investDebtValService.getInvestDebtValCount(arriveDate);
    }

    public List<InvestDebtVal> getInvestDebtValPageList(Date arriveDate, int page, int rows){
        return investDebtValService.getInvestDebtValPageList(arriveDate, page, rows);
    }

    @Override
    public Integer getInvestDebtValReturnAmtCount(Date arriveDate) {
        return investDebtValService.getInvestDebtValReturnAmtCount(arriveDate);
    }

    @Override
    public List<InvestDebtVal> getInvestDebtValReturnAmtPageList(Date arriveDate, int page, int rows) {
        return investDebtValService.getInvestDebtValReturnAmtPageList(arriveDate, page, rows);
    }

}
