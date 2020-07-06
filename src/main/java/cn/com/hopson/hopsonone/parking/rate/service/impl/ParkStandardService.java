package cn.com.hopson.hopsonone.parking.rate.service.impl;

import cn.com.hopson.hopsonone.parking.rate.entity.Rule;
import cn.com.hopson.hopsonone.parking.rate.entity.RuleDetail;
import cn.com.hopson.hopsonone.parking.rate.mapper.RuleDetailMapper;
import cn.com.hopson.hopsonone.parking.rate.mapper.RuleMapper;
import cn.com.hopson.hopsonone.parking.rate.service.IParkStandardService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Slf4j
public class ParkStandardService implements IParkStandardService {
    DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private RuleMapper ruleMapper;
    @Autowired
    private RuleDetailMapper detailMapper;

    @Override
    public BigDecimal feeFixedHours(Integer marketId, String in, Integer parkTime) {
        DateTime inDateTime = DateTime.parse(in, format);
        return fee(marketId, inDateTime, parkTime * 60);
    }

    /**
     * 计费
     *
     * @param marketId 商场id
     * @param in       入场时间
     * @param parkTime 停车时长（分钟）
     * @return
     */
    public BigDecimal fee(Integer marketId, DateTime in, Integer parkTime) {
        // 初始化缴费金额
        BigDecimal countPrice = new BigDecimal(0);
        Rule rule = ruleMapper.selectOne(Wrappers.<Rule>lambdaQuery().eq(Rule::getMarketId, marketId));
        List<RuleDetail> ruleDetails = detailMapper.selectList(Wrappers.<RuleDetail>lambdaQuery().eq(RuleDetail::getMarketId, marketId));
        if (rule == null || ruleDetails == null || marketId == null || parkTime <= 0) return countPrice;

        //跨整天
        if (parkTime % 1440 == 0) {
            log.info("跨整天");
            return rule.getDayCost().multiply(new BigDecimal(parkTime / 1440));
        } else {  //跨非整天
            int day = parkTime / 1440;
            parkTime = parkTime % 1440;
            countPrice = countPrice.add(rule.getDayCost().multiply(new BigDecimal(day)));
            log.info("跨非整天 day:{} parkTime:{} countPrice:{} ", day, parkTime, countPrice);
        }

        //0:分时计费 1：分时分段计费
        if (rule.getType() == 0) {
            RuleDetail ruleDetail = ruleDetails.get(0);
            BigDecimal money = timeSharing(parkTime, ruleDetail.getUnitType(), ruleDetail.getTimeInterval(), ruleDetail.getUnitPrice());
            log.info("分时计费 money {}", money);
            return countPrice.add(money.compareTo(rule.getDayCost()) == 1 ? rule.getDayCost() : money);
        } else {
            DateTime inDateTime = in;
            //最终开始时间点 = 入场时间 + 免费时长
            DateTime startTime = inDateTime.plusMinutes(rule.getFreeTime());
            //最终结束时间点 = 最终开始时间点 + 固定时长
            DateTime endTime = startTime.plusMinutes(parkTime);
            log.info("免费时长：{} 固定时长：{}", rule.getFreeTime(), parkTime);
            log.info("入场时间：{}，开始时间：{}，结束时间：{}", inDateTime, startTime, endTime);

            List<Integer> datas = new CopyOnWriteArrayList<>();
            DateTime flag = startTime;
            for (int i = 0; i <= parkTime / 60; i++) {
                datas.add(flag.getHourOfDay());
                flag = flag.plusHours(1);
            }

            RuleDetail startRuleDetail = null;    //跨段，开始点所在时间段
            RuleDetail endRuleDetail = null;      //跨段，结束点所在时间段
            List<RuleDetail> midRuleDetail = new ArrayList<>();      //跨段，中间点所在时间段，可能跨多个段
            for (RuleDetail ruleDetail : ruleDetails) {
                Integer oneIntervalStart = ruleDetail.getIntervalStart();
                Integer oneIntervalEnd = ruleDetail.getIntervalEnd();
                //分段，开始点所在时间段
                if (startTime.getHourOfDay() >= oneIntervalStart && startTime.getHourOfDay() <= oneIntervalEnd) {
                    startRuleDetail = ruleDetail;
                    for (Integer data : datas) {
                        if (data >= ruleDetail.getIntervalStart() && data <= ruleDetail.getIntervalEnd())
                            datas.remove(data);
                    }
                    log.info("开始点在哪个时间段: {} ~ {}", oneIntervalStart, oneIntervalEnd);
                }
                //分段，结束点所在时间段
                if (endTime.getHourOfDay() >= oneIntervalStart && endTime.getHourOfDay() <= oneIntervalEnd) {
                    endRuleDetail = ruleDetail;
                    for (Integer data : datas) {
                        if (data >= ruleDetail.getIntervalStart() && data <= ruleDetail.getIntervalEnd())
                            datas.remove(data);
                    }
                    log.info("结束点在哪个时间段: {} ~ {}", oneIntervalStart, oneIntervalEnd);
                }
                //分段，中间时间段
                //if (startTime.getHourOfDay() < oneIntervalStart && endTime.getHourOfDay() > oneIntervalEnd) {
                if (datas.contains(oneIntervalStart) && datas.contains(oneIntervalEnd)) {
                    midRuleDetail.add(ruleDetail);
                    log.info("中间时间段，结束点和开始点都没有的时间段: {} ~ {}", oneIntervalStart, oneIntervalEnd);
                }

                //在一个时段之内
                if (startTime.getHourOfDay() >= oneIntervalStart && startTime.getHourOfDay() <= oneIntervalEnd &&
                        endTime.getHourOfDay() >= oneIntervalStart && endTime.getHourOfDay() <= oneIntervalEnd) {
                    BigDecimal money = timeSharing(parkTime, ruleDetail.getUnitType(), ruleDetail.getTimeInterval(), ruleDetail.getUnitPrice());
                    if (money.compareTo(ruleDetail.getIntervalCost()) == 1) money = ruleDetail.getIntervalCost();
                    if (money.compareTo(rule.getDayCost()) == 1) money = rule.getDayCost();
                    return countPrice.add(money);
                }
            }

            //跨段
            for (RuleDetail result : ruleDetails) {
                //if (startTime.getHourOfDay() >= result.getIntervalStart() && endTime.getHourOfDay() > result.getIntervalEnd()) {
                if (startTime.getHourOfDay() >= result.getIntervalStart() && endTime.getHourOfDay() > result.getIntervalEnd() ||
                        startTime.getHourOfDay() < result.getIntervalStart() && endTime.getHourOfDay() <= result.getIntervalEnd()) {
                    log.info("跨段");
                    //开始点所在段
                    //开始点所在段的分钟
                    int timeStart = getTime(startTime, startRuleDetail.getIntervalEnd());
                    BigDecimal bigDecimalStart = timeSharing(timeStart, startRuleDetail.getUnitType(), startRuleDetail.getTimeInterval(), startRuleDetail.getUnitPrice());
                    //开始段金额
                    if (bigDecimalStart.compareTo(startRuleDetail.getIntervalCost()) == 1)
                        bigDecimalStart = startRuleDetail.getIntervalCost();
                    log.info("开始段金额: {}", bigDecimalStart);
                    //时段剩余时间
                    int surplus = timeStart % startRuleDetail.getTimeInterval();
                    log.info("开始时段剩余时间: timeStart {} timeInterval {} surplus {}", timeStart, startRuleDetail.getTimeInterval(), surplus);
                    //中间段
                    //中间段金额
                    BigDecimal bigDecimalMiddle = new BigDecimal(0.0);
                    if (midRuleDetail.size() != 0) {
                        midRuleDetail.sort(Comparator.comparing(e -> e.getIntervalStart()));
                        for (RuleDetail ruleDetail : midRuleDetail) {

                            DateTime startTimeMiddle = getStartTime(startTime, ruleDetail.getIntervalStart()).plusMinutes(surplus);
                            int timeMiddle = getTime(startTimeMiddle, ruleDetail.getIntervalEnd());
                            bigDecimalMiddle = bigDecimalMiddle.add(timeSharing(timeMiddle, ruleDetail.getUnitType(), ruleDetail.getTimeInterval(), ruleDetail.getUnitPrice()));
                            //最高收费
                            if (bigDecimalMiddle.compareTo(ruleDetail.getIntervalCost()) == 1)
                                bigDecimalMiddle = ruleDetail.getIntervalCost();
                            //中间时段剩余时间
                            surplus = timeMiddle % ruleDetail.getTimeInterval();
                            log.info("中间时段开始时间: {}", startTimeMiddle);
                            log.info("中间时段剩余时间: timeMiddle {} timeInterval {} surplus {}", timeMiddle, ruleDetail.getTimeInterval(), surplus);
                            log.info("中间段金额: {}", bigDecimalMiddle);
                        }
                    }

                    //结束段
                    //***********************************endTime
                    DateTime startTimeEnd = getStartTime(startTime, endRuleDetail.getIntervalStart()).plusMinutes(surplus);
                    Long timeEnd = (endTime.getMillis() - startTimeEnd.getMillis()) / 1000 / 60;
                    //结束段金额
                    BigDecimal bigDecimalEnd = timeSharing(timeEnd.intValue(), endRuleDetail.getUnitType(), endRuleDetail.getTimeInterval(), endRuleDetail.getUnitPrice());
                    if (bigDecimalEnd.compareTo(endRuleDetail.getIntervalCost()) == 1)
                        bigDecimalEnd = endRuleDetail.getIntervalCost();

                    log.info("结束段分钟：{}  结束点：{} ", timeEnd, endRuleDetail.getIntervalEnd());
                    log.info("结束段开始时间: {}", startTimeEnd);
                    log.info("结束段金额: {}", bigDecimalEnd);

                    //总计费
                    BigDecimal bigDecimalSum = bigDecimalStart.add(bigDecimalMiddle).add(bigDecimalEnd);
                    if (bigDecimalSum.compareTo(rule.getDayCost()) == 1) bigDecimalSum = rule.getDayCost();
                    log.info("分段总计费: {}", bigDecimalSum);
                    return countPrice.add(bigDecimalSum);
                }
            }
        }
        return countPrice;
    }

    /**
     * 计算出时间段的金额
     *
     * @param realMinute   停车时长
     * @param unitType     0：分钟 1：小时
     * @param timeInterval 时段
     * @param unitPrice    单价
     * @return
     */
    private BigDecimal timeSharing(int realMinute, Integer unitType, Integer timeInterval, BigDecimal unitPrice) {
        //RuleDetail ruleDetail = ruleDetails.get(0);
        int intervalCount = 0;
        //0：分钟 1：小时
        if (unitType == 0) {
            int result = realMinute / timeInterval;
            intervalCount = realMinute % timeInterval == 0 ? result : result + 1;
        } else {
            intervalCount = realMinute % 60 == 0 ? realMinute / 60 : realMinute / 60 + 1;
        }
        return unitPrice.multiply(new BigDecimal(intervalCount));
    }

    /**
     * 通过传入的时间，和小时点，获取到中间的分钟差
     *
     * @param startTime 起始时间
     * @param hour      小时
     * @return
     */
    private int getTime(DateTime startTime, Integer hour) {
        String s = startTime.toString("yyyy-MM-dd ") + hour + ":59:59";
        DateTime parse = DateTime.parse(s, DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss")).plusSeconds(1);
        Long result = (parse.getMillis() - startTime.getMillis()) / 1000 / 60;
        return result.intValue();
    }

    /**
     * 通过传入的时间，和小时点，获取到新的开始时间
     *
     * @param startTime
     * @param hour
     * @return
     */
    private DateTime getStartTime(DateTime startTime, Integer hour) {
        String s = startTime.toString("yyyy-MM-dd ") + hour + ":00:00";
        return DateTime.parse(s, DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
    }
}