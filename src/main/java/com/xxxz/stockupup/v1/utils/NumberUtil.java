package com.xxxz.stockupup.v1.utils;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @autor jiangll
 * @date 2021/9/8
 * 数值处理工具
 */
public final class NumberUtil implements Serializable {
    private static final long serialVersionUID = 2527335647394251550L;

    /**
     * 保留两位小数
     */
    public static double retainTwo(double d) {
        BigDecimal b = new BigDecimal(d);
        return b.setScale(3, BigDecimal.ROUND_HALF_EVEN).doubleValue();
    }
}
