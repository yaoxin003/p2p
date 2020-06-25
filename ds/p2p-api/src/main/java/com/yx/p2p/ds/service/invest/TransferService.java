package com.yx.p2p.ds.service.invest;

import com.yx.p2p.ds.easyui.Result;
import com.yx.p2p.ds.model.match.FinanceMatchRes;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: yx
 * @date: 2020/05/09/15:28
 */
public interface TransferService {

    //转让赎回申请
    public Result transferApply(Integer investId,Date arriveDate);

    //投资债权交割
    public Result changeInvestclaim(Map<String,Object> claimMap);

    //投资提现申请
    public Result withdrawApply(Integer investId);

    //获得转让协议文本
    public Map<String,Object> getTransferContractText(Integer investId);
}
