package com.yx.p2p.ds.invest.service.impl;

import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.enums.invest.InvestBizStateEnum;
import com.yx.p2p.ds.enums.lending.LendingBizStateEnum;
import com.yx.p2p.ds.enums.lending.LendingTypeEnum;
import com.yx.p2p.ds.helper.BeanHelper;
import com.yx.p2p.ds.invest.mapper.LendingMapper;
import com.yx.p2p.ds.model.invest.Invest;
import com.yx.p2p.ds.model.invest.InvestClaim;
import com.yx.p2p.ds.model.invest.Lending;
import com.yx.p2p.ds.mq.InvestMQVo;
import com.yx.p2p.ds.service.InvestService;
import com.yx.p2p.ds.service.LendingService;
import com.yx.p2p.ds.util.LoggerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/21/13:11
 */
@Service
public class LendingServiceImpl implements LendingService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private LendingMapper lendingMapper;

    @Autowired
    private InvestService investService;

    @Override
    public Result addNewLending(InvestMQVo investMQVo) {
        Lending lending = this.buildNewLending(investMQVo);
        Result result = this.insertDBLending(lending);
        result.setTarget(lending.getId());//后续流程使用lending.id
        return result;
    }

    private Result insertDBLending(Lending lending) {
        logger.debug("【准备将出借单插入数据库】lending=" + lending);
        Result result = Result.error();
        int count = lendingMapper.insert(lending);
        if(count == 1){
            result = Result.success();
        }
        logger.debug("【插入数据库：出借单】count=" + count);
        return result;
    }

    private Lending buildNewLending(InvestMQVo investMQVo) {
        Lending lending = new Lending();
        lending.setInvestId(Integer.parseInt(investMQVo.getBizId()));
        lending.setAmount(investMQVo.getAmount());
        lending.setCustomerId(investMQVo.getCustomerId());
        lending.setLendingType(LendingTypeEnum.NEW_LEND.getCode());
        lending.setOrderSn(investMQVo.getOrderSn());
        BeanHelper.setAddDefaultField(lending);
        return lending;
    }

    /**
     * @description: 检查出借单是否生成
     * @author:  YX
     * @date:    2020/04/22 7:13
     * @param: orderSn
     * @return: com.yx.p2p.ds.easyui.Result.status true已经生成,false未生成出借单
     */
    public Result checkNoLendingByOrderSn(String orderSn) {
        logger.debug("【检查出借单不存在】orderSn=" + orderSn);
        Result result = Result.error();
        Lending lending = this.queryLendingByOrderSn(orderSn);
        if(lending == null){
            result = Result.success();
        }
        logger.debug("【检查出借单不存在】result=" + result);
        return result;
    }

    private Lending queryLendingByOrderSn(String orderSn){
        Example example = new Example(Lending.class);
        example.createCriteria().andEqualTo("orderSn",orderSn);
        Lending lending = lendingMapper.selectOneByExample(example);
        return lending;
    }

    //检查并处理出借单满额
    public Result checkAndDealFullAmt(List<InvestClaim> matchInvestClaimList){
        logger.debug("【检查并处理出借单满额】入参：matchInvestClaimList=" + matchInvestClaimList);
        Result result = Result.error();
        Map<Integer,BigDecimal> lendingMap = new HashMap<Integer,BigDecimal>();
        //根据出借单Id，汇总撮合金额
        for (InvestClaim matchInvestClaim : matchInvestClaimList) {
            Integer lendingId = matchInvestClaim.getLendingId();
            if(lendingMap.get(lendingId) == null){
                lendingMap.put(lendingId,matchInvestClaim.getBuyAmt());
            }else{
                BigDecimal buyAmt = lendingMap.get(lendingId);
                buyAmt = buyAmt.add(matchInvestClaim.getBuyAmt());
            }
        }
        //根据验证出借单是否满额,新出借单更新投资状态为满额
        for(Integer lendingId : lendingMap.keySet()) {
            //数据库中投资持有金额
            InvestClaim param = new InvestClaim();
            param.setLendingId(lendingId);
            List<InvestClaim> dbInvestClaminList = investService.getInvestClaimList(param);
            BigDecimal dbInvestAmt = BigDecimal.ZERO;// 数据库中投资持有金额
            for (InvestClaim dbInvestClaim : dbInvestClaminList) {
                dbInvestAmt = dbInvestAmt.add(dbInvestClaim.getBuyAmt());
            }
            Lending lending = this.getLending(lendingId);
            //检查金额=数据库中出借单金额和+撮合金额
            BigDecimal checkInvestAmt = dbInvestAmt.add(lendingMap.get(lendingId));
            if(lending.getAmount().compareTo(checkInvestAmt) == 0){//出借单金额==检查金额
                //新出借单还要更新投资状态
                if(lending.getLendingType().equals(LendingTypeEnum.NEW_LEND.getCode())){
                    investService.updateInvestBizState(lending.getInvestId(),InvestBizStateEnum.FULL_AMT);//满额
                }
                //更新出借单状态，满额
                this.updateLendingBizState(lendingId, LendingBizStateEnum.FULL_AMT);
                logger.debug("【出借单满额】lendingId=" + lendingId);
            }else if(lending.getAmount().compareTo(checkInvestAmt) == -1){//异常：出借单金额<检查金额
                throw new RuntimeException("【验证出借单满额时出现异常】投资金额小于撮合金额，撮合系统的撮合金额出现错误，" +
                        "lendingId=" + lendingId);
            }else{//出借单未满额
                logger.debug("【出借单未满额】lendingId=" + lendingId);
                result = Result.success();
            }
        }
        return result;
    }

    private Lending getLending(Integer lendingId){
        Lending lending = lendingMapper.selectByPrimaryKey(lendingId);
        return lending;
    }

    private Result updateLendingBizState(Integer lendingId,LendingBizStateEnum bizStateEnum){
        Lending param = new Lending();
        param.setId(lendingId);
        param.setBizState(bizStateEnum.getState());
        BeanHelper.setUpdateDefaultField(param);
        int i = lendingMapper.updateByPrimaryKeySelective(param);
        return Result.success();
    }
}
