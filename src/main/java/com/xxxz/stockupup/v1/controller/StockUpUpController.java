package com.xxxz.stockupup.v1.controller;

import com.xxxz.stockupup.v1.component.StockTask;
import com.xxxz.stockupup.v1.model.Stock;
import com.xxxz.stockupup.v1.service.StockUpUpService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
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
    @Autowired
    StockTask stockTask;

    /**
     * 更新持仓数据
     */
    @PostMapping("/update")
    public String update(@RequestBody Stock stockParam) {
        if (null == stockParam.getTrans() || null == stockParam.getCost()) {
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

    /**
     * 手动更新昨日收益和持仓收益
     */
    @PostMapping("/updateYesterdayAndNowProfit")
    public String updateYesterdayAndNowProfit(@RequestBody Stock paramStock) {
        Query query = new Query(
                Criteria.where("holder_name").is(paramStock.getHolder_name())
                        .and("stock_name").is(paramStock.getStock_name())
                        .and("status").is(1)
        );
        Update update = new Update().set("total_profit", paramStock.getTotal_profit())
                                    .set("yesterday_profit", paramStock.getYesterday_profit());
        mongoTemplate.updateFirst(query, update, Stock.class);
        return "update success " + paramStock.toString();
    }

    /**
     * test entrance
     */
    @GetMapping("/task")
    public Object task(@RequestParam String type) {
        if (StringUtils.equals(type, "1")) {
            return stockTask.updateTodayProfit();
        } else if (StringUtils.equals(type, "2")) {
            return stockTask.clearTodayProfit();
        }
        return "success";
    }
}
