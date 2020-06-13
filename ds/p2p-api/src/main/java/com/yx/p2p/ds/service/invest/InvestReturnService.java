package com.yx.p2p.ds.service.invest;

import com.yx.p2p.ds.model.invest.InvestClaim;
import com.yx.p2p.ds.model.invest.InvestDebtVal;
import com.yx.p2p.ds.model.invest.InvestReturn;
import com.yx.p2p.ds.model.invest.InvestReturnDtl;

import java.util.Date;
import java.util.List;

/**
 * @description:
 * @author: yx
 * @date: 2020/06/04/14:34
 */
public interface InvestReturnService {

    public Integer insertInvestReturnDtlList(List<InvestReturnDtl> investReturnDtlList);

    public Integer insertInvestReturnList(List<InvestReturn> investReturnList);

    public Integer getInvestReturnGroupListCount(Date arriveDate);

    //arriveDate 还款到账日期
    //currentPage 开始页编号
    //pageSize 每页条数
    public List<InvestReturn> getInvestReturnGroupList(Date arriveDate, Integer currentPage, Integer pageSize);

    //arriveDate 还款到账日期
    public Integer getInvestReturnListCount(Date arriveDate);

    //arriveDate 还款到账日期
    //currentPage 开始页编号
    //pageSize 每页条数
    public List<InvestReturn> getInvestReturnList(Date arriveDate, Integer currentPage, Integer pageSize);

    public List<InvestReturnDtl> getInvestReturnDtlList(Date arriveDate, List<Integer> borrowIdList);

    public List<InvestReturn> getInvestReturnList(Date arriveDate, List<Integer> investIdList);

    public List<InvestDebtVal> getInvestDebtValList(Date arriveDate, List<Integer> investIdList);
}
