package com.yx.p2p.ds.invest.service.impl;

import com.yx.p2p.ds.invest.mapper.InvestDebtValMapper;
import com.yx.p2p.ds.invest.mapper.InvestReturnDtlMapper;
import com.yx.p2p.ds.invest.mapper.InvestReturnMapper;
import com.yx.p2p.ds.model.invest.InvestDebtVal;
import com.yx.p2p.ds.model.invest.InvestReturn;
import com.yx.p2p.ds.model.invest.InvestReturnDtl;
import com.yx.p2p.ds.service.invest.InvestReturnService;
import com.yx.p2p.ds.util.PageUtil;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;
import java.util.Date;
import java.util.List;

/**
 * @description:投资回款
 * @author: yx
 * @date: 2020/06/04/14:32
 */
@Service
public class InvestReturnServiceImpl implements InvestReturnService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private InvestReturnDtlMapper investReturnDtlMapper;

    @Autowired
    private InvestReturnMapper investReturnMapper;

    @Autowired
    private InvestDebtValMapper investDebtValMapper;

    public Integer insertInvestReturnDtlList(List<InvestReturnDtl> investReturnDtlList){
        int count = 0;
        if(!investReturnDtlList.isEmpty() ){
            count = investReturnDtlMapper.insertList(investReturnDtlList);
        }
        logger.debug("【插入投资回款明细数据】count=" + count);
        return count;
    }

    public Integer insertInvestReturnList(List<InvestReturn> investReturnList){
        int count = 0;
        if(!investReturnList.isEmpty() ) {
            count = investReturnMapper.insertList(investReturnList);
        }
        logger.debug("【插入投资回款数据】count=" + count);
        return count;
    }

    public Integer getInvestReturnGroupListCount(Date arriveDate){
        logger.debug("【获得按投资编号分组后的投资回款数量】入参：arriveDate=" + arriveDate);
        int count = investReturnDtlMapper.queryInvestReturnGroupListCount(arriveDate);
        logger.debug("【获得按投资编号分组后的投资回款数量】结果：count=" + count);
        return count;
    }

    //arriveDate 还款到账日期
    //currentPage 开始页编号
    //pageSize 每页条数
    public List<InvestReturn> getInvestReturnGroupList(Date arriveDate, Integer currentPage, Integer pageSize){
        logger.debug("【获得按投资编号分组后的投资回款集合】入参：arriveDate=" + arriveDate
                    +",currentPage=" + currentPage + ",pageSize=" + pageSize);
        int offset = PageUtil.getOffset(currentPage,pageSize);
        List<InvestReturn> investReturnList = investReturnDtlMapper.queryInvestReturnGroupList(arriveDate,offset,pageSize);
        logger.debug("【获得按投资编号分组后的投资回款集合】结果：investReturnList=" + investReturnList);
        return investReturnList;
    }

    //arriveDate 还款到账日期
    public Integer getInvestReturnListCount(Date arriveDate){
        logger.debug("【获得投资回款数量】入参：arriveDate=" + arriveDate);
        InvestReturn param = new InvestReturn();
        param.setArriveDate(arriveDate);
        int count = investReturnMapper.selectCount(param);
        logger.debug("【获得投资回款数量】结果：count=" + count);
        return count;
    }

    //arriveDate 还款到账日期
    //currentPage 开始页编号
    //pageSize 每页条数
    public List<InvestReturn> getInvestReturnList(Date arriveDate, Integer currentPage, Integer pageSize){
        logger.debug("【获得投资回款集合】入参：arriveDate=" + arriveDate
                +",currentPage=" + currentPage + ",pageSize=" + pageSize);
        int offset = PageUtil.getOffset(currentPage,pageSize);
        InvestReturn param = new InvestReturn();
        param.setArriveDate(arriveDate);
        RowBounds rowBounds = new RowBounds(offset,pageSize);
        List<InvestReturn> investReturnList = investReturnMapper.selectByRowBounds(param, rowBounds);
        logger.debug("【获得投资回款集合】结果：investReturnList=" + investReturnList);
        return investReturnList;
    }

    public List<InvestReturnDtl> getInvestReturnDtlList(Date arriveDate, List<Integer> borrowIdList){
        Example example = new Example(InvestReturnDtl.class);
        example.createCriteria()
                .andIn("borrowId",borrowIdList)
                .andEqualTo("arriveDate",arriveDate);
        List<InvestReturnDtl> investReturnDtls = investReturnDtlMapper.selectByExample(example);
        return investReturnDtls;
    }

    public List<InvestReturn> getInvestReturnList(Date arriveDate, List<Integer> investIdList){
        Example example = new Example(InvestReturn.class);
        example.createCriteria()
                .andIn("investId",investIdList)
                .andEqualTo("arriveDate",arriveDate);
        List<InvestReturn> investReturns = investReturnMapper.selectByExample(example);
        return investReturns;
    }

    @Override
    public List<InvestDebtVal> getInvestDebtValList(Date arriveDate, List<Integer> investIdList) {
        Example example = new Example(InvestDebtVal.class);
        example.createCriteria().andIn("investId",investIdList)
        .andEqualTo("arriveDate",arriveDate);
        List<InvestDebtVal> investDebtVals = investDebtValMapper.selectByExample(example);
        return investDebtVals;
    }
}
