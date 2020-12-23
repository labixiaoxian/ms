package com.xiaoxian.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * Created by XiaoXian on 2020/11/29.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Accessors(chain = true)
public class StockOrder {

    private Integer id;

    private Integer sid;

    private String name;

    private Date createTime;
}
