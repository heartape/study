package com.heartape.controller;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heartape.entity.Orders;
import com.heartape.entity.Goods;
import com.heartape.mapper.OrdersMapper;
import com.heartape.mapper.GoodsMapper;
import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
public class BusinessController {

    @Autowired
    private OrdersMapper ordersMapper;

    @Autowired
    private GoodsMapper goodsMapper;

    @PostMapping("/buy")
    @GlobalTransactional(rollbackFor = Exception.class)
    // @Transactional
    public String buy(@RequestParam Integer uid, @RequestParam Integer goodsId, @RequestParam Integer number) {
        Goods goods = goodsMapper.selectById(goodsId);

        LambdaUpdateWrapper<Goods> goodsWrapper = Wrappers.lambdaUpdate();
        goodsWrapper.eq(Goods::getId, goodsId).setSql("stock = stock -" + number);
        goodsMapper.update(null, goodsWrapper);

        BigDecimal total = goods.getPrice().multiply(new BigDecimal(number));

        Orders orders = new Orders(null, uid, total);
        ordersMapper.insert(orders);

        return "成功";
    }

}
