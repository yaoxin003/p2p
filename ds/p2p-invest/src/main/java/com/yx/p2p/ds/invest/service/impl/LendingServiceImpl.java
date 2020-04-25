package com.yx.p2p.ds.invest.service.impl;

import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.enums.lending.LendingTypeEnum;
import com.yx.p2p.ds.helper.BeanHelper;
import com.yx.p2p.ds.invest.mapper.LendingMapper;
import com.yx.p2p.ds.model.invest.Lending;
import com.yx.p2p.ds.mq.InvestMQVo;
import com.yx.p2p.ds.service.LendingService;
import com.yx.p2p.ds.util.LoggerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

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
}
