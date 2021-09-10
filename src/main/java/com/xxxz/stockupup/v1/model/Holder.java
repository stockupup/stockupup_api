package com.xxxz.stockupup.v1.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * @autor jiangll
 * @date 2021/9/7
 * 用户
 */
@Data
@NoArgsConstructor
public class Holder implements Serializable {
    private static final long serialVersionUID = 4598896534901196701L;

    private String _id;
    private String holder_id;
    private String holder_name;
    private Date create_time;
    private Long create_ts;

    private Double history_profit = 0.00; //历史收益


    public Holder(String holder_name) {
        this.holder_name = holder_name;
    }
}
