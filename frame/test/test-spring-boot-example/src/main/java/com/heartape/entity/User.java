package com.heartape.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.*;

/**
 * user实体类
 * <li>columnList只能填写实体类字段，而不是数据库字段
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class User {
    @TableId
    private Integer id;
    @TableField
    private String name;
}
