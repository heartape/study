package com.heartape.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class Orders {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer uid;
    private BigDecimal amount;
}
