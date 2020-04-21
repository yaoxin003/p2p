package com.yx.p2p.ds.invest.service.impl;

import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.enums.lending.LendingTypeEnum;
import com.yx.p2p.ds.helper.BeanHelper;
import com.yx.p2p.ds.invest.P2pInvestApplication;
import com.yx.p2p.ds.invest.mapper.LendingMapper;
import com.yx.p2p.ds.model.Lending;
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

    private Logger logger = LoggerFactory.getLogger(Lending.class);

    @Autowired
    private LendingMapper lendingMapper;

    @Override
    public Result addNewLending(InvestMQVo investMQVo) {
        Lending lending = this.buildNewLending(investMQVo);
        Result result = this.insertDBLending(lending);
        return result;
    }

    private Result insertDBLending(Lending lending) {
        logger.debug("【准备将出借单插入数据库】lending=" + lending);
        Result result = Result.error();
        try{
            int count = lendingMapper.insert(lending);
            if(count == 1){
                result = Result.success();
            }
            logger.debug("【插入数据库：出借单】count=" + count);
        }catch(Exception e){
            result = LoggerUtil.addExceptionLog(e,logger);
        }
        return result;
    }

    private Lending buildNewLending(InvestMQVo investMQVo) {
        Lending lending = new Lending();
        lending.setInvestId(Integer.parseInt(investMQVo.getBizId()));
        lending.setAmount(investMQVo.getAmount());
        lending.setCustomerId(investMQVo.getCustomerId());
        lending.setLendingType(LendingTypeEnum.NEW_LEND.getCode());
        BeanHelper.setAddDefaultField(lending);
        return lending;
    }
}
