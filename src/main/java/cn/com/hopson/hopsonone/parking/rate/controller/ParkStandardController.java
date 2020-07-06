package cn.com.hopson.hopsonone.parking.rate.controller;


import cn.com.hopson.common.base.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

/**
 * Created by lx on 2019/07/24.
 */
@RestController
@RequestMapping("/park-standard")
@Slf4j
public class ParkStandardController {

    /**
     * 停车场时长费用查询查询
     * @return
     */
    @GetMapping("/standard/parktime/fee")
    public R parkNoPlateReverse(@RequestParam Integer marketId, @RequestParam String in, @RequestParam Integer parkTime, @RequestParam String carNo){
        log.info(">>>>停车场时长费用查询查询 ：--{},{},{},{}", marketId,in,parkTime,carNo);
        BigDecimal parkTimeFee =  null;
        return new R(parkTimeFee.doubleValue());
    }
}
