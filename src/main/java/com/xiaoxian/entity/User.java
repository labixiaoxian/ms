package com.xiaoxian.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author 小贤
 * @PackageName:com.xiaoxian.entity
 * @ClassName:User
 * @Description:
 * @data 2021/1/8 21:17
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Accessors(chain = true)
public class User {
    private Integer id;
    private String name;
    private String password;
}
