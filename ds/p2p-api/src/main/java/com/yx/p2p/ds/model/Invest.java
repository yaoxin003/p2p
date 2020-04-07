package com.yx.p2p.ds.model;

import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * @description:
 * @author: yx
 * @date: 2020/04/06/17:56
 */
//@Table(name="p2p_invest")
public class Invest extends BaseModel implements Serializable {
    @Id//使用tkmybatis.selectByPrimaryKey方法时需要该字段，否则会将所有字段都当做where条件
    private Integer id;
}
