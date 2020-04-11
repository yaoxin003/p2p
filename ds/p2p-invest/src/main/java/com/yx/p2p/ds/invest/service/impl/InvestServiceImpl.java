package com.yx.p2p.ds.invest.service.impl;

import com.yx.p2p.ds.constant.SysConstant;
import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.enums.investproduct.InvestTypeEnum;
import com.yx.p2p.ds.helper.BeanHelper;
import com.yx.p2p.ds.invest.mapper.InvestMapper;
import com.yx.p2p.ds.model.Invest;
import com.yx.p2p.ds.model.InvestProduct;
import com.yx.p2p.ds.service.InvestProductService;
import com.yx.p2p.ds.service.InvestService;
import com.yx.p2p.ds.util.BigDecimalUtil;
import com.yx.p2p.ds.util.DateUtil;
import com.yx.p2p.ds.util.LoggerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/06/18:03
 */
@Service
public class InvestServiceImpl implements InvestService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private InvestMapper investMapper;

    @Autowired
    private InvestProductService investProductService;

    /**
        * @description: 添加新投资
        * @author:  YX
        * @date:    2020/04/10 11:59
        * @param: invest
        * @return: com.yx.p2p.ds.model.Invest
        */
    public Result addNewInvest(Invest invest){
        Result result = Result.success();
        try{
            //1.查询投资产品
            InvestProduct investProduct =
                    investProductService.getInvestProductById(invest.getInvestProductId());
            //2.封装新投资
            result = this.buildNewInvest(investProduct,invest);
            //3.插入数据库
            result = this.addInvestInDB(invest);
        }catch(Exception e){
            result = LoggerUtil.addExceptionLog(e,logger);
        }
        return result;
    }

    private Result addInvestInDB(Invest invest){
        Result result = Result.success();
        try{
            //设置时间和操作人
            BeanHelper.setDefaultTimeField(invest,"createTime","updateTime");
            Map<String,Integer> operatorMap = new HashMap<>();
            operatorMap.put("creator",SysConstant.operator);
            operatorMap.put("reviser",SysConstant.operator);
            BeanHelper.setDefaultOperatorField(invest,operatorMap);

            investMapper.insert(invest);
            result.setTarget(invest);
            logger.debug("【invest.insert.db.invest=】" + invest);
        }catch (Exception e){
            result = LoggerUtil.addExceptionLog(e,logger);
        }
        return result;
    }

    private Result buildNewInvest(InvestProduct investProduct,Invest invest) {
        Result result = Result.success();
        invest.setStartDate(new Date());//开始时间
        result = this.buildEndDate(investProduct,invest);//结束时间
        result = this.buildProfit(investProduct,invest);//计算收益
        logger.debug("【buildNewInvest.invest=】" + invest);
        return result;
    }

    /**
        * @description:
     * 前台页面已经计算过，后台也计算一次，验证收益是否相同
     *  投资类型1-固定期限：保留2位四舍五入
     *  收益=round(日收益*投资天数,2)，日收益=round(投资金额*年化收益率/365,2)
     *  invest.profit= round(
     *      round(invest.investAmt*investProduct.yearIrr/365,2)*investProduct.dayCount
     *  ,2)
     *  投资类型2-非固定期限：先按“投资类型1-固定期限”计算，实际按赎回日计算
        * @author:  YX
        * @date:    2020/04/10 15:29
        * @param: investProduct
        * @param: invest
        * @return: void
        * @throws:
        */
    private Result buildProfit(InvestProduct investProduct, Invest invest) {
        Result result = Result.success();
        //日收益
        BigDecimal dayProfit = invest.getInvestAmt().multiply(investProduct.getYearIrr())
                .divide(new BigDecimal("365"), SysConstant.BIGDECIMAL_DIVIDE_ROUNDMODE,BigDecimal.ROUND_HALF_UP);
        //收益
        BigDecimal profit = BigDecimalUtil.round2In45(
                dayProfit.multiply(new BigDecimal(investProduct.getDayCount()))
        );//保留2位四舍五入
        BigDecimal beforeProfit = invest.getProfit();
        if(profit.compareTo(beforeProfit) != 0){
            String msg = "前后台收益不同：前台计算收益："+ beforeProfit + "后台计算收益：" + profit;
            result = Result.error(msg);
            logger.debug(msg);
        }else{
            String msg = "前后台收益相同：前台计算收益："+ beforeProfit + "后台计算收益：" + profit;
            result = Result.error(msg);
            logger.debug(msg);
        }
        return result;
    }

        /**
         * @description:
         * 设置投资结束日期endDate
         * 投资类型1-固定期限：非null，当前日期+InvestProduct.dayCount
         *  投资类型2-非固定期限：null
        * @author:  YX
        * @date:    2020/04/10 15:30
        * @param: null
        * @return:
        * @throws:
        */
    private Result buildEndDate(InvestProduct investProduct,Invest invest) {
        Result result = Result.success();
        try{
            if(investProduct.getInvestType().equals(InvestTypeEnum.FIXED.getState())){
                invest.setEndDate(DateUtil.add(invest.getStartDate(),investProduct.getDayCount()+1));
            }else{
                invest.setEndDate(null);
            }
        }catch (Exception e){
            result = LoggerUtil.addExceptionLog(e,logger);
        }
        return result;
    }

}
