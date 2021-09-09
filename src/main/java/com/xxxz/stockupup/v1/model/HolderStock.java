package com.xxxz.stockupup.v1.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @autor jiangll
 * @date 2021/9/7
 * 用户持仓情况
 */
@Data
public class HolderStock implements Serializable {
    private static final long serialVersionUID = -2818253407954461754L;

    private String currency = "CNY";         // 币种
    private String holder_id;        // 持仓人id
    private String holder_name;      // 持仓人名字
    private List<Stock> stocks;      // 持股详情
    private Double profit = 0.00;            // 今日收益
    private Double clearance_profit = 0.00; // 清仓收益
    private Double total_profit = 0.00;     // 总收益
    private Double yesterday_profit = 0.00; // 昨日收益
}
