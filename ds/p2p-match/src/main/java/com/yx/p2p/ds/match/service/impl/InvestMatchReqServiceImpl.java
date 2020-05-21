package com.yx.p2p.ds.match.service.impl;

import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.enums.match.InvestMatchReqBizStateEnum;
import com.yx.p2p.ds.helper.BeanHelper;
import com.yx.p2p.ds.match.mapper.InvestMatchReqMapper;
import com.yx.p2p.ds.model.match.InvestMatchReq;
import com.yx.p2p.ds.service.InvestMatchReqService;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @description:投资撮合请求
 * @author: yx
 * @date: 2020/04/26/14:19
 */
@Service
public class InvestMatchReqServiceImpl implements InvestMatchReqService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private InvestMatchReqMapper investMatchReqMapper;

    @Override
    public Result addInvestMatchReq(InvestMatchReq investMatchReq) {
        logger.debug("【插入投资撮合请求入参】investMatchReq=" + investMatchReq);
        Result result  = this.checkNoInvestMatchReq(investMatchReq);
        int count = 0;
        if(Result.checkStatus(result)){
            BeanHelper.setAddDefaultField(investMatchReq);
            count = investMatchReqMapper.insert(investMatchReq);
            if( count == 1){
                result = Result.success();
            }
        }
        logger.debug("【插入投资撮合请求结果】count=" + count + "，result=" + result);
        return result;
    }

    private Result checkNoInvestMatchReq(InvestMatchReq req) {
        logger.debug("【验证不存在投资撮合请求数据入参】req=" + req);
        Result result = Result.error();
        String orderSn = req.getInvestOrderSn();
        InvestMatchReq investMatchReq = this.queryDBInvestMatchReq(orderSn);
        if(investMatchReq == null){
            result = Result.success();
        }
        logger.debug("【验证不存在投资撮合请求数据结果】result=" + result);
        return result;
    }

    private InvestMatchReq queryDBInvestMatchReq(String orderSn) {
        logger.debug("【查询投资撮合请求入参】orderSn=" + orderSn);
        InvestMatchReq req = new InvestMatchReq();
        req.setInvestOrderSn(orderSn);
        InvestMatchReq investMatchReq = investMatchReqMapper.selectOne(req);
        logger.debug("【查询投资撮合请求结果】investMatchReq=" + investMatchReq);
        return investMatchReq;
    }


    public List<InvestMatchReq> getWaitMatchAmtInvestReqList() {
        List<InvestMatchReq> investReqList = this.selectWaitMatchAmtInvestReqList();
        return investReqList;
    }


    //查询数据库中投资撮合数据。
    // where条件：状态新增或撮合中，排序：按照level倒序
    private List<InvestMatchReq> selectWaitMatchAmtInvestReqList() {
        Example example = new Example(InvestMatchReq.class);
        List<String> bizStateList = new ArrayList<>();
        bizStateList.add(InvestMatchReqBizStateEnum.NEW_ADD.getState());
        bizStateList.add(InvestMatchReqBizStateEnum.MATCHING.getState());
        example.createCriteria().andIn("bizState",bizStateList)
                .andGreaterThan("waitAmt",new BigDecimal("0"));
        example.setOrderByClause("level desc");
        List<InvestMatchReq> investReqList = investMatchReqMapper.selectByExample(example);
        logger.debug("【查询数据库获得投资撮合请求】investReqList=" + investReqList);
        return investReqList;
    }
}
