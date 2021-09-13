package com.xxxz.stockupup.v1.service;

import com.xxxz.stockupup.v1.model.Holder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

/**
 * @autor jiangll
 * @date 2021/9/7
 */
@Service
public class HolderService {
    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * 添加用户
     */
    public Holder add(Holder paramHoder) {
        Holder holder = mongoTemplate.findOne(new Query(Criteria.where("holder_name").is(paramHoder.getHolder_name())),
                                                Holder.class);
        if (holder == null) {
            paramHoder.set_id(UUID.randomUUID().toString().replaceAll("-", ""));
            paramHoder.setCreate_time(new Date());
            paramHoder.setCreate_ts(System.currentTimeMillis());
            paramHoder.setHolder_id(UUID.randomUUID().toString().replaceAll("-", ""));

            mongoTemplate.save(paramHoder);
            holder = paramHoder;
        }
        return holder;
    }

    /**
     * 根据id查询实体
     */
    public Holder getHolderById(String holderId) {
        return mongoTemplate.findOne(new Query(Criteria.where("holder_id").is(holderId)), Holder.class);
    }
}
