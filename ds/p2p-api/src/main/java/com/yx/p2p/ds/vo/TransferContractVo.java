package com.yx.p2p.ds.vo;

import com.yx.p2p.ds.model.invest.TransferDtl;
import com.yx.p2p.ds.model.invest.Transfer;
import com.yx.p2p.ds.model.invest.TransferDtlSaleBefore;

import java.io.Serializable;
import java.util.List;

/**
 * @description:转让合同
 * @author: yx
 * @date: 2020/05/09/13:38
 */
public class TransferContractVo implements Serializable{

    private Transfer transfer;//转让协议
    private List<TransferDtlSaleBefore> transferDtlSaleBeforeList;//转让售前协议明细
    private List<TransferDtl> transferDtlList;//转让协议明细

    public Transfer getTransfer() {
        return transfer;
    }

    public void setTransfer(Transfer transfer) {
        this.transfer = transfer;
    }

    public List<TransferDtlSaleBefore> getTransferDtlSaleBeforeList() {
        return transferDtlSaleBeforeList;
    }

    public void setTransferDtlSaleBeforeList(List<TransferDtlSaleBefore> transferDtlSaleBeforeList) {
        this.transferDtlSaleBeforeList = transferDtlSaleBeforeList;
    }

    public List<TransferDtl> getTransferDtlList() {
        return transferDtlList;
    }

    public void setTransferDtlList(List<TransferDtl> transferDtlList) {
        this.transferDtlList = transferDtlList;
    }

    @Override
    public String toString() {
        return "TransferContractVo{" +
                "transfer=" + transfer +
                ", transferDtlSaleBeforeList=" + transferDtlSaleBeforeList +
                ", transferDtlList=" + transferDtlList +
                '}' + super.toString();
    }
}
