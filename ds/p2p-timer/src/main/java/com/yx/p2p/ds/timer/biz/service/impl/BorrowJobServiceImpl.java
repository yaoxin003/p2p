package com.yx.p2p.ds.timer.biz.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.yx.p2p.ds.model.borrow.DebtDateValue;
import com.yx.p2p.ds.server.DebtDateValueServer;
import com.yx.p2p.ds.timer.biz.service.BorrowJobService;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;

/**
 * @description:债务每日价值
 * @author: yx
 * @date: 2020/06/15/19:31
 */
@Service
public class BorrowJobServiceImpl implements BorrowJobService {

    @Reference
    private DebtDateValueServer debtDateValueServer;

    //分页查询债务每日价值
    public List<DebtDateValue> getDebtDateValuePageList(Date daily, Integer page, Integer rows){
        return debtDateValueServer.getDebtDateValuePageList(daily, page, rows);
    }

    //查询债务每日价值数量
    public Integer getDebtDateValueCount(Date daily){
        return debtDateValueServer.getDebtDateValueCount(daily);
    }


}
