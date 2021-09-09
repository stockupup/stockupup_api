package com.xxxz.stockupup.v1.controller;

import com.xxxz.stockupup.v1.model.Stock;
import com.xxxz.stockupup.v1.service.StockUpUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @autor jiangll
 * @date 2021/9/7
 */
@RestController
@RequestMapping("/v1/stock")
public class StockUpUpController {

    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    StockUpUpService stockUpUpService;

    /**
     * 更新持仓数据
     */
    @PostMapping("/update")
    public String update(@RequestBody Stock stockParam) {
        if(null == stockParam.getTrans() || null == stockParam.getCost()){
            return "update success";
        }
        stockUpUpService.update(stockParam);
        return "update success";
    }

    /**
     * 清仓
     */
    @PostMapping("/clearance")
    public String clearance(@RequestBody Stock stockParam) {
        stockUpUpService.clearance(stockParam);
        return "clearance success";
    }

    /**
     * 持仓情况
     */
    @GetMapping("/query")
    public Map query(@RequestParam(required = false) String holder_id) {
        return stockUpUpService.query(holder_id);
    }
}
