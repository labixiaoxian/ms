package com.xiaoxian.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * Created by XiaoXian on 2020/11/29.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@ToString
public class Stock {

    private Integer id;

    private String name;

    private Integer count;

    private Integer sale;

    private Integer version;
}
