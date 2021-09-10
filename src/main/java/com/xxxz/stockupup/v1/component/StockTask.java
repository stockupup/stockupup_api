package com.xxxz.stockupup.v1.component;

import com.xxxz.stockupup.v1.model.Stock;
import com.xxxz.stockupup.v1.service.StockUpUpService;
import com.xxxz.stockupup.v1.utils.NumberUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @autor jiangll
 * @date 2021/9/7
 * 定时任务
 */
@Slf4j
@Component
@EnableScheduling
public class StockTask {
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    StockUpUpService stockUpUpService;

    /**
     * 每天12:01，15:01更新今日收益
     */
    @Scheduled(cron = "0 1 12,15,19,23 * * ?")
    public void updateTodayProfit() {
        log.info("-- 更新今日收益 --\n");
        List<Stock> stocks_1 = stockUpUpService.getStockByStatus(1);
        List<String> stockCodes = stocks_1.stream().map(Stock::getStock_code).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(stockCodes)) {
            List<Double> price = getPresentPriceByCodes(stockCodes);
            for (int i = 0; i < stockCodes.size(); i++) {
                //TODO 待优化
                Stock stock = stocks_1.get(i);
                Query query = new Query(Criteria.where("_id").is(stock.get_id()));
                Update update = new Update().set("total_profit", NumberUtil.retainTwo(stock.getTrans() * (price.get(i) - stock.getCost())))
                        .set("profit", NumberUtil.retainTwo(stock.getTotal_profit() + stock.getClearance_profit() - stock.getYesterday_profit()));
                mongoTemplate.updateFirst(query, update, Stock.class);
            }
        }

        //更新清仓收益
        List<Stock> stocks_0 = stockUpUpService.getTodayClearStock();
        if (CollectionUtils.isNotEmpty(stocks_0)) {
            stocks_0.forEach(stock -> {
                        //TODO 待优化
                        Query query = new Query(Criteria.where("_id").is(stock.get_id()));
                        Update update = new Update().set("profit", NumberUtil.retainTwo(stock.getClearance_profit() - stock.getYesterday_profit()));
                        mongoTemplate.updateFirst(query, update, Stock.class);
                    }
            );
        }
    }

    /**
     * 每天23.59清零今日收益
     */
    @Scheduled(cron = "0 59 23 * * ?")
    public void clearTodayProfit() {
        log.info("-- 清零今日收益 --\n");
        List<Stock> stocks_1 = stockUpUpService.getStockByStatus(1);
        if (CollectionUtils.isNotEmpty(stocks_1)) {
            stocks_1.forEach(stock -> {
                //TODO 待优化
                Query query = new Query(Criteria.where("_id").is(stock.get_id()));
                Update update = new Update()
                        .set("yesterday_profit", stock.getProfit())
                        .set("profit", 0.00)
                        .set("yd_cost", stock.getCost());
                mongoTemplate.updateFirst(query, update, Stock.class);
            });
        }
        List<Stock> stocks_0 = stockUpUpService.getTodayClearStock();
        if (CollectionUtils.isNotEmpty(stocks_0)) {
            stocks_0.forEach(stock -> {
                //TODO 待优化
                Query query = new Query(Criteria.where("_id").is(stock.get_id()));
                Update update = new Update()
                        .set("yesterday_profit", stock.getProfit())
                        .set("profit", 0.00);
                mongoTemplate.updateFirst(query, update, Stock.class);
            });
        }
    }

    /**
     * 获取股票最新价格
     */
    private List<Double> getPresentPriceByCodes(List<String> codes) {
        List<Double> result = new ArrayList<>(codes.size());
        String s1 = restTemplate.getForObject(
                "https://hq.sinajs.cn/?list=" + StringUtils.join(codes, ",")
                , String.class);
        String[] s2 = s1.split("\\n");
        for (String s2s : s2) {
            String[] s3 = s2s.split(",");
            result.add(Double.valueOf(s3[3]));
        }
        return result;
    }
}
