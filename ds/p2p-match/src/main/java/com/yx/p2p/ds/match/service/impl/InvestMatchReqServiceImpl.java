package com.yx.p2p.ds.match.service.impl;

import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.enums.match.InvestMatchReqBizStateEnum;
import com.yx.p2p.ds.helper.BeanHelper;
import com.yx.p2p.ds.match.mapper.InvestMatchReqMapper;
import com.yx.p2p.ds.model.match.InvestMatchReq;
import com.yx.p2p.ds.service.match.InvestMatchReqService;
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
    public Result addInvestMatchReq(List<InvestMatchReq> mqReqList) {
        logger.debug("【插入投资撮合请求入参】mqReqList=" + mqReqList);
        Result result = Result.error();
        List<InvestMatchReq> noInsertReqList  = this.filterNoInsertInvestMatchReqList(mqReqList);
        int count = 0;
        if(!noInsertReqList.isEmpty()){
            for (InvestMatchReq req : noInsertReqList) {
                BeanHelper.setAddDefaultField(req);
            }
            count = investMatchReqMapper.insertList(noInsertReqList);
            result = Result.success();
        }
        logger.debug("【插入投资撮合请求结果】count=" + count + "，result=" + result);
        return result;
    }

    private List<InvestMatchReq> filterNoInsertInvestMatchReqList(List<InvestMatchReq> mqReqList) {
        logger.debug("【过滤没有插入的投资撮合请求集合】mqReqList=" + mqReqList);
        List<InvestMatchReq> dbReqList = this.queryInvestMatchReqList(mqReqList);
        mqReqList.removeAll(dbReqList);
        logger.debug("【过滤没有插入的投资撮合请求集合】结果mqReqList=" + mqReqList);
        return mqReqList;
    }

    private List<InvestMatchReq> queryInvestMatchReqList(List<InvestMatchReq> paramReqList) {
        logger.debug("【查询投资撮合请求集合入参】paramReqList=" + paramReqList);
        List<String> orderSnList = new ArrayList<>();
        for (InvestMatchReq investMatchReq : paramReqList) {
            orderSnList.add(investMatchReq.getInvestOrderSn());
        }
        Example example = new Example(InvestMatchReq.class);
        example.createCriteria().andIn("investOrderSn",orderSnList);
        List<InvestMatchReq> investMatchReqList = investMatchReqMapper.selectByExample(example);
        logger.debug("【查询投资撮合请求集合结果】investMatchReqList=" + investMatchReqList);
        return investMatchReqList;
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
