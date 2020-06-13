package com.yx.p2p.ds.service.invest;

import com.yx.p2p.ds.model.invest.InvestDebtVal;
import com.yx.p2p.ds.model.invest.InvestDebtValDtl;
import com.yx.p2p.ds.model.invest.InvestReturn;

import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: yx
 * @date: 2020/06/13/11:17
 */
public interface InvestDebtValService {

    public List<InvestDebtValDtl> getInvestDebtValDtlList(Date arriveDate, List<Integer> borrowIdList);

    public Integer insertInvestDebtValDtlList(List<InvestDebtValDtl> allInvestDebtValDtlList);

    public Integer getInvestDebtValGroupListCount(Date arriveDate);

    //arriveDate 到账日期
    //currentPage 开始页编号
    //pageSize 每页条数
    public List<InvestDebtVal> getInvestDebtValGroupList(Date arriveDate, Integer currentPage, Integer pageSize);

    public Integer insertInvestDebtValList(List<InvestDebtVal> investDebtValList);
}
