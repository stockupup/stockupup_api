package com.xxxz.stockupup.v1.controller;

import com.xxxz.stockupup.v1.component.StockTask;
import com.xxxz.stockupup.v1.model.Stock;
import com.xxxz.stockupup.v1.service.StockUpUpService;
import com.xxxz.stockupup.v1.utils.NumberUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
     * 更新昨日收益，只做一次
     */
    @PostMapping("/updateYesterdayProfit")
    public String updateYesterdayProfit() {
        String[] holderNames = {"志军", "老姜", "正正", "强哥", "靖威"};
        for (String holderName : holderNames) {
            List<Stock> stocks = stockUpUpService.getStockByHolder_nameAndStatus(holderName, 1);
            List<String> stockCodes = stocks.stream().map(Stock::getStock_code).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(stockCodes)) {
                List<Double> price = stockTask.getPresentPriceByCodes(stockCodes);
                for (int i = 0; i < stockCodes.size(); i++) {
                    //TODO 待优化
                    Stock stock = stocks.get(i);
                    Query query = new Query(Criteria.where("_id").is(stock.get_id()));
                    Update update = new Update().set("total_profit", NumberUtil.retainTwo(stock.getTrans() * (price.get(i) - stock.getCost())))
                            .set("yesterday_profit", NumberUtil.retainTwo(stock.getTrans() * (price.get(i) - stock.getCost())));
                    mongoTemplate.updateFirst(query, update, Stock.class);
                }
            }
        }
        return "update yesterday profit success";
    }
}
