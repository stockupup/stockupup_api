package com.xxxz.stockupup.v1.controller;

import com.xxxz.stockupup.v1.model.Holder;
import com.xxxz.stockupup.v1.service.HolderService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @autor jiangll
 * @date 2021/9/7
 * 持仓人
 */
@RestController
@RequestMapping("/v1/holder")
public class HolderController {
    @Autowired
    MongoTemplate mongoTemplate;
    @Autowired
    HolderService holderService;

    /**
     * 新增 holder
     */
    @PostMapping("/add")
    public Map add(@RequestBody Holder paramHoder) {
        paramHoder = holderService.add(paramHoder);
        List<Holder> holders = mongoTemplate.findAll(Holder.class);

        Map<String, Object> result = new HashMap<>(3);
        result.put("data", holders);
        result.put("msg", "ok");
        result.put("rid", paramHoder.getHolder_id());
        return result;
    }

    /**
     * 查询 holder
     */
    @GetMapping("/query")
    public Map query(@RequestParam(required = false) String rid) {
        List<Holder> holders = mongoTemplate.findAll(Holder.class);
        Map<String, Object> result = new HashMap<>(3);
        result.put("data", holders);
        result.put("msg", "ok");
        result.put("rid", StringUtils.isBlank(rid) ? "" : rid);
        return result;
    }

}
