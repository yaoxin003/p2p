package com.yx.p2p.ds.borrow.service.impl;

import com.yx.p2p.ds.borrow.mongo.borrow.DebtDailyValue;
import com.yx.p2p.ds.borrow.service.DebtDailyValueService;
import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.model.borrow.Borrow;
import com.yx.p2p.ds.model.borrow.Cashflow;
import com.yx.p2p.ds.service.util.p2p.CashFlowVo;
import com.yx.p2p.ds.service.util.p2p.NpvUtil;
import com.yx.p2p.ds.service.util.p2p.P2PDateUtil;
import com.yx.p2p.ds.util.BigDecimalUtil;
import com.yx.p2p.ds.util.LoggerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/** 暂不使用MogoDB，而是使用Mycat对应DebtDateValueServiceImpl
 * @description:计算债务每日价值
 * @author: yx
 * @date: 2020/05/19/15:05
 */
@Service
public class DebtDailyValueServiceImpl implements DebtDailyValueService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    /*@Autowired
    private MongoTemplate mongoTemplate;*/

    public Result addBatchDebtDailyValue(Borrow borrow,List<Cashflow> cashflows){
        logger.debug("MongoDB【批量插入债务每日价值】入参：cashflows=" + cashflows);
        Result result = Result.error();
        try{
            List<DebtDailyValue> debtDailyValueList = this.buildDebtDailyValueList(borrow,cashflows);
           // mongoTemplate.insertAll(debtDailyValueList);
        }catch(Exception e){
            result = LoggerUtil.addExceptionLog(e,logger);
        }
        result = Result.success();
        return result;
    }

    private List<DebtDailyValue> buildDebtDailyValueList(Borrow borrow,List<Cashflow> cashflows) {
        List<CashFlowVo> cashFlowVoList = this.buildCashFlowVoList(cashflows);
        List<DebtDailyValue> debtDailyValueList = new ArrayList<>();
        try {
            Date beginOccurDate = cashFlowVoList.get(0).getOccurDate();
            int count = P2PDateUtil.diff(beginOccurDate,
                    cashFlowVoList.get(cashFlowVoList.size()-1).getOccurDate());
            Date occurDate = beginOccurDate;
            for(int j=0;j <= count+1; j++){
                BigDecimal npv = NpvUtil.calNpv(occurDate, cashFlowVoList);
                DebtDailyValue debtDailyValue = this.buildMongoDebtDailyValue(borrow,occurDate,
                        BigDecimalUtil.round2In45(npv));
                occurDate = P2PDateUtil.addDays(occurDate,1);
                debtDailyValueList.add(debtDailyValue);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
       return debtDailyValueList;
    }

    private List<CashFlowVo> buildCashFlowVoList(List<Cashflow> cashflows) {
        List<CashFlowVo> cashFlowVoList = new ArrayList<>();
        int i = 1;
        for (Cashflow cashflow : cashflows) {
            CashFlowVo cashFlowVo = new CashFlowVo();
            cashFlowVo.setOccurDate(cashflow.getTradeDate());
            BigDecimal monthPayment = cashflow.getMonthPayment();
            if(i == 1){
                cashFlowVo.setAmount(BigDecimal.ZERO.subtract(monthPayment));//首期为借款金额，在计算npv时需要一个负值
            }else{
                cashFlowVo.setAmount(monthPayment);//月供，每期还款金额，正值
            }
            cashFlowVoList.add(cashFlowVo);
            i++;
        }
        logger.debug("【构建计算npv现金流】cashFlowVoList=" + cashFlowVoList);
        return cashFlowVoList;
    }

    private DebtDailyValue buildMongoDebtDailyValue(Borrow borrow,Date occurDate, BigDecimal npv) {
        DebtDailyValue debtDailyValue = new DebtDailyValue();
        /*debtDailyValue.setId(ObjectId.get());
        debtDailyValue.setCreateTime(new Date());
        debtDailyValue.setDaily(occurDate);
        debtDailyValue.setBorrowId(borrow.getId());
        debtDailyValue.setValue(npv.doubleValue());*/
        return debtDailyValue;
    }

    /**
        * @description: 分页查询某日债务价值集合
        * @author:  YX
        * @date:    2020/05/20 10:27
        * @param: daily 日期(年月日)
        * @param: page 第几页，首页编号1
        * @param: rows 每页记录数
        * @return: java.util.List<com.yx.p2p.ds.mongo.borrow.DebtDailyValue>
        */
    public List<DebtDailyValue> queryDebtDailyValuePageList(Date daily,Integer page,Integer rows){
        logger.debug("MongoDB【分页查询债务某日价值】入参daily=" + daily);
       /* Criteria criteria = new Criteria().andOperator(
                Criteria.where("daily").is(daily)
        );
        PageRequest pageRequest = PageRequest.of(page-1,rows,Sort.by(Sort.Direction.ASC,"id"));
        Query query = Query.query(criteria).with(pageRequest);
        List<DebtDailyValue> debtDailyValueList = mongoTemplate.find(query,DebtDailyValue.class);*/
        List<DebtDailyValue> debtDailyValueList = null;
        logger.debug("MongoDB【分页查询债务某日价值】结果debtDailyValueList=" + debtDailyValueList);
        return debtDailyValueList;
    }

    //查询债务某日价值数量
    public Integer queryDebtDailyValuePageCount(Date daily){
        logger.debug("MongoDB【数量查询债务某日价值】入参daily=" + daily);
       /* Criteria criteria = new Criteria().andOperator(
                Criteria.where("daily").is(daily)
        );
        Query query = Query.query(criteria);
        long count = mongoTemplate.count(query, DebtDailyValue.class);*/
        long count = 0;
        logger.debug("MongoDB【数量查询债务某日价值】count=" + count);
        return (int)count;
    }


}
