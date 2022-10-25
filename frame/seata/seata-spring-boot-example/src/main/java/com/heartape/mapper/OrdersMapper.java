package com.heartape.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heartape.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@DS("orders-ds")
public interface OrdersMapper extends BaseMapper<Orders> {
}
