package com.xxxz.stockupup.v1.service;

import com.xxxz.stockupup.v1.model.Holder;
import com.xxxz.stockupup.v1.model.HolderStock;
import com.xxxz.stockupup.v1.model.Stock;
import com.xxxz.stockupup.v1.utils.DateUtil;
import com.xxxz.stockupup.v1.utils.NumberUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @autor jiangll
 * @date 2021/9/7
 */
@Service
public class StockUpUpService {
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    HolderService holderService;

    /**
     * 更新持仓数据
     */
    public void update(Stock stockParam) {
        Query query = new Query(
                Criteria.where("holder_id").is(stockParam.getHolder_id())
                        .and("stock_code").is(stockParam.getStock_code())
                        .and("status").is(1)
        );
        Stock stock = mongoTemplate.findOne(query, Stock.class);
        if (stock != null) {
            Update update = new Update().set("cost", stockParam.getCost())
                                        .set("trans", stockParam.getTrans())
                                        .set("modify_time", new Date())
                                        .set("modify_ts", System.currentTimeMillis());
            if (stockParam.getTrans() == 0) {
                update.set("status", 0);
            }
            mongoTemplate.updateFirst(query, update, Stock.class);
        } else {
            stock = stockParam;
            stock.set_id(UUID.randomUUID().toString().replaceAll("-", ""));
            stock.setCreate_time(new Date());
            stock.setCreate_ts(System.currentTimeMillis());
            stock.setModify_time(stock.getCreate_time());
            stock.setModify_ts(stock.getCreate_ts());
            mongoTemplate.save(stock);
        }
    }

    /**
     * 清仓
     */
    public void clearance(Stock stockParam) {
        Query query = new Query(
                Criteria.where("holder_id").is(stockParam.getHolder_id())
                        .and("stock_code").is(stockParam.getStock_code())
                        .and("status").is(1)
        );
        Update update = new Update().set("clearance_profit", null == stockParam.getProfit() ? 0.00 : stockParam.getProfit())
                                    .set("total_profit", null == stockParam.getProfit() ? 0.00 : stockParam.getProfit())
                                    .set("trans", 0)
                                    .set("status", 0);
        mongoTemplate.updateFirst(query, update, Stock.class);
    }

    /**
     * 持仓情况
     */
    public Map query(String holder_id) {
        Map<String, Object> result = new HashMap<>(4);
        result.put("msg", "ok");
        if (StringUtils.isBlank(holder_id)) {
            List<Holder> holders = mongoTemplate.findAll(Holder.class);
            List<HolderStock> data = new ArrayList<>(holders.size());
            holders.forEach(
                    holder -> {
                        HolderStock holderStock = transStocks(holder.getHolder_id(), holder.getHolder_name());
                        data.add(holderStock);
                    }
            );
            result.put("data", data);
            result.put("rid", "");
            result.put("total_count", data.size());
        } else {
            List<HolderStock> data = new ArrayList<>(1);
            HolderStock holderStock = transStocks(holder_id, null);
            data.add(holderStock);
            result.put("data", data);
            result.put("rid", holder_id);
            result.put("total_count", 1);
        }
        return result;
    }

    /**
     * 将个人持股情况整合成持仓情况
     */
    private HolderStock transStocks(String holder_id, String holder_name) {
        Holder holder = holderService.getHolderById(holder_id);
        List<Stock> stocks_all = mongoTemplate.find(new Query(Criteria.where("holder_id").is(holder_id)),
                                                      Stock.class);
        List<Stock> stocks_0 = stocks_all.stream()
                                         .filter(stock -> stock.getStatus() == 0
                                                 && stock.getModify_ts() > DateUtil.getTodayTs())
                                         .collect(Collectors.toList());
        List<Stock> stocks_0_y = stocks_all.stream()
                                           .filter(stock -> stock.getStatus() == 0
                                                   && stock.getModify_ts() > DateUtil.getYesterdayTs()
                                                   && stock.getModify_ts() < DateUtil.getTodayTs())
                                           .collect(Collectors.toList());
        List<Stock> stocks_1 = stocks_all.stream()
                                         .filter(stock -> stock.getStatus() == 1)
                                         .collect(Collectors.toList());
        HolderStock holderStock = new HolderStock();
        holderStock.setHolder_id(holder_id);
        holderStock.setHolder_name(StringUtils.isBlank(holder_name) ? holder.getHolder_name() : holder_name);
        holderStock.setStocks(stocks_1);
        //清仓收益 ： 当日清仓股票的总收益
        holderStock.setClearance_profit(NumberUtil.retainTwo(stocks_0.stream().mapToDouble(Stock::getClearance_profit).sum()));
        //总收益 ： 历史收益 + 所有买卖收益
        holderStock.setTotal_profit(NumberUtil.retainTwo(holder.getHistory_profit()
                                    + stocks_all.stream().mapToDouble(Stock::getTotal_profit).sum()));
        //昨日收益 持仓股票的昨日收益 + 昨日清仓股票的收益 + 今日清仓股票的昨日收益
        holderStock.setYesterday_profit(NumberUtil.retainTwo(stocks_1.stream().mapToDouble(Stock::getYesterday_profit).sum()
                                        + stocks_0_y.stream().mapToDouble(Stock::getYesterday_profit).sum())
                                        + stocks_0.stream().mapToDouble(Stock::getYesterday_profit).sum());
        //今日收益 总收益 + 清仓收益 - 昨日持仓收益
        holderStock.setProfit(NumberUtil.retainTwo(stocks_1.stream().mapToDouble(Stock::getTotal_profit).sum()
                                + holderStock.getClearance_profit()
                                - stocks_1.stream().mapToDouble(Stock::getYesterday_profit).sum())
                                - stocks_0.stream().mapToDouble(Stock::getYesterday_profit).sum());
        //由于前端将total_profit当作昨日累计收益使用，所以这样赋值
        holderStock.setTotal_profit(NumberUtil.retainTwo(holderStock.getTotal_profit() - holderStock.getProfit()));
        return holderStock;
    }

    /**
     * 根据状态获取持仓股票
     */
    public List<Stock> getStockByStatus(int status) {
        return mongoTemplate.find(new Query(Criteria.where("status").is(status)),
                                    Stock.class);
    }

    /**
     * 获取当日清仓的股票
     */
    public List<Stock> getTodayClearStock() {
        return mongoTemplate.find(new Query(Criteria.where("status").is(0)
                                                .and("modify_ts").gt(DateUtil.getTodayTs())),
                                    Stock.class);
    }

    /**
     * 获取昨日清仓的股票（数据量上来之后在使用）
     */
    private List<Stock> getYesterdayClearStock(String holder_id) {
        Criteria criteria = new Criteria();
        criteria.andOperator(Criteria.where("holder_id").is(holder_id)
                                     .and("status").is(0)
                                     .and("modify_ts").gt(DateUtil.getYesterdayTs()),
                            Criteria.where("modify_ts").lt(DateUtil.getTodayTs()));
        return mongoTemplate.find(new Query(criteria),
                                    Stock.class);
    }
}
