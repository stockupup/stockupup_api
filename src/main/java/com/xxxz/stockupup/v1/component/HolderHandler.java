package com.xxxz.stockupup.v1.component;

import com.xxxz.stockupup.v1.contanst.StockContanst;
import com.xxxz.stockupup.v1.model.Holder;
import com.xxxz.stockupup.v1.service.HolderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @autor jiangll
 * @date 2021/9/9
 * 初始化用户数据
 */
@Slf4j
@Component
public class HolderHandler implements ApplicationRunner {
    @Autowired
    HolderService holderService;

    @Override
    public void run(ApplicationArguments args) {
        log.info("-- 初始化用户数据(无则添加) --");
        List<String> holderNames = holderService.getMongoTemplate().findAll(Holder.class)
                .stream().map(Holder::getHolder_name).collect(Collectors.toList());
        for (String holderName : StockContanst.HolderInitData) {
            if (!holderNames.contains(holderName)) {
                holderService.add(new Holder(holderName));
            }
        }
        holderNames = null;
    }
}
