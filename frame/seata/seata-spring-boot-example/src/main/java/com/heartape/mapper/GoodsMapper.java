package com.heartape.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heartape.entity.Goods;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@DS("goods-ds")
public interface GoodsMapper extends BaseMapper<Goods> {
}
