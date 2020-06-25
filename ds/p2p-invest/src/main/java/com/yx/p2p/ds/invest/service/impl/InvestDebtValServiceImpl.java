package com.yx.p2p.ds.invest.service.impl;

import com.yx.p2p.ds.invest.mapper.InvestDebtValDtlMapper;
import com.yx.p2p.ds.invest.mapper.InvestDebtValMapper;
import com.yx.p2p.ds.model.invest.InvestDebtVal;
import com.yx.p2p.ds.model.invest.InvestDebtValDtl;
import com.yx.p2p.ds.service.invest.InvestDebtValService;
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
 * @description:
 * @author: yx
 * @date: 2020/06/13/9:28
 */
@Service
public class InvestDebtValServiceImpl implements InvestDebtValService {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private InvestDebtValDtlMapper investDebtValDtlMapper;

    @Autowired
    private InvestDebtValMapper investDebtValMapper;

    public List<InvestDebtValDtl> getInvestDebtValDtlList(Date arriveDate, List<Integer> borrowIdList){
        Example example = new Example(InvestDebtValDtl.class);
        example.createCriteria().andIn("borrowId",borrowIdList)
                .andEqualTo("arriveDate",arriveDate);
        List<InvestDebtValDtl> investDebtValDtls = investDebtValDtlMapper.selectByExample(example);
        return investDebtValDtls;
    }

    @Override
    public Integer insertInvestDebtValDtlList(List<InvestDebtValDtl> allInvestDebtValDtlList) {
        logger.debug("【批量插入InvestDebtValDtl】入参：allInvestDebtValDtlList=" + allInvestDebtValDtlList);
        int count = 0;
        if(!allInvestDebtValDtlList.isEmpty() ){
            count = investDebtValDtlMapper.insertList(allInvestDebtValDtlList);
        }
        logger.debug("【插入投资债权价值明细数据】count=" + count);
        return count;
    }

    public Integer getInvestDebtValGroupListCount(Date arriveDate){
        return investDebtValDtlMapper.queryInvestDebtValGroupListCount(arriveDate);
    }

    //arriveDate 到账日期
    //currentPage 开始页编号
    //pageSize 每页条数
    public List<InvestDebtVal> getInvestDebtValGroupList(Date arriveDate, Integer currentPage, Integer pageSize){
        logger.debug("【获得按投资编号分组后的InvestDebtVal集合】入参：arriveDate=" + arriveDate
                +",currentPage=" + currentPage + ",pageSize=" + pageSize);
        int offset = PageUtil.getOffset(currentPage,pageSize);
        List<InvestDebtVal> investDebtValList = investDebtValDtlMapper.queryInvestDebtValGroupList(arriveDate, offset, pageSize);
        logger.debug("【获得按投资编号分组后的InvestDebtVal集合】结果：investDebtValList=" + investDebtValList);
        return investDebtValList;
    }

    public Integer insertInvestDebtValList(List<InvestDebtVal> investDebtValList){
        int count = 0;
        if(!investDebtValList.isEmpty() ) {
            count = investDebtValMapper.insertList(investDebtValList);
        }
        logger.debug("【插入投资回款数据】count=" + count);
        return count;
    }

    @Override
    public List<InvestDebtVal> getInvestDebtValList(Date arriveDate, List<Integer> investIdList) {
        Example example = new Example(InvestDebtVal.class);
        example.createCriteria().andIn("investId",investIdList)
                .andEqualTo("arriveDate",arriveDate);
        List<InvestDebtVal> investDebtVals = investDebtValMapper.selectByExample(example);
        return investDebtVals;
    }

    public Integer getInvestDebtValCount(Date arriveDate){
        InvestDebtVal investDebtVal = new InvestDebtVal();
        investDebtVal.setArriveDate(arriveDate);
        int count = investDebtValMapper.selectCount(investDebtVal);
        return count;
    }

    public List<InvestDebtVal> getInvestDebtValPageList(Date arriveDate,int page,int rows) {
        InvestDebtVal param = new InvestDebtVal();
        param.setArriveDate(arriveDate);
        int offset = (page - 1) * rows;
        List<InvestDebtVal> investDebtValList = investDebtValMapper.selectByRowBounds(
                param, new RowBounds(offset,rows));
        return investDebtValList;
    }

    public Integer getInvestDebtValReturnAmtCount(Date arriveDate){
        Example example = new Example(InvestDebtVal.class);
        example.createCriteria().andGreaterThan("totalHoldReturnAmt",0)
                .andEqualTo("arriveDate",arriveDate);
        int count = investDebtValMapper.selectCountByExample(example);
        return count;
    }

    public List<InvestDebtVal> getInvestDebtValReturnAmtPageList(Date arriveDate,int page,int rows) {
        Example example = new Example(InvestDebtVal.class);
        example.createCriteria().andGreaterThan("totalHoldReturnAmt",0)
                .andEqualTo("arriveDate",arriveDate);
        int offset = (page - 1) * rows;
        return investDebtValMapper.selectByExampleAndRowBounds(example,new RowBounds(offset,rows));
    }

    public List<InvestDebtValDtl> getInvestDebtValDtlList(Integer investId,Date arriveDate){
        InvestDebtValDtl param = new InvestDebtValDtl();
        param.setInvestId(investId);
        param.setArriveDate(arriveDate);
        return investDebtValDtlMapper.select(param);

    }

}
