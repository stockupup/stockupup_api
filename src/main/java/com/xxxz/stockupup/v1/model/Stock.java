package com.xxxz.stockupup.v1.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @autor jiangll
 * @date 2021/9/7
 * 用户持股情况
 */
@Data
public class Stock implements Serializable {
    private static final long serialVersionUID = 2676516622066475929L;

    private String _id;
    private Double cost;                  //成本
    private String holder_id;           //持仓人id
    private String holder_name;         //持仓人名字
    private Integer status = 1;             //状态 1-持仓，0-已清仓
    private Integer trans;              //股数

    private String stock_id;            //股票id
    private String stock_code;          //股票代码
    private String stock_name;          //股票名称

    private Double clearance_profit = 0.00;     //清仓收益
    private Double total_profit = 0.00;         //总收益
    private Double yesterday_profit = 0.00;     //昨日收益
    private Double profit = 0.00;                //今日收益

    private String currency = "CNY";             //币种
    private Double yd_cost = 0.00;              //昨日成本
    private String date;

    private Date create_dt;
    private Date create_time;
    private Long create_ts;
    private Date modify_time;
    private Long modify_ts;
}
