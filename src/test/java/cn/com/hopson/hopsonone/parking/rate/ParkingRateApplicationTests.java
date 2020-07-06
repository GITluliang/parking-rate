package cn.com.hopson.hopsonone.parking.rate;

import cn.com.hopson.hopsonone.parking.rate.entity.Rule;
import cn.com.hopson.hopsonone.parking.rate.entity.RuleDetail;
import cn.com.hopson.hopsonone.parking.rate.mapper.RuleDetailMapper;
import cn.com.hopson.hopsonone.parking.rate.mapper.RuleMapper;
import cn.com.hopson.hopsonone.parking.rate.service.IParkStandardService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
class ParkingRateApplicationTests {

	@Autowired
	private RuleMapper ruleMapper;
	@Autowired
	private RuleDetailMapper detailMapper;
	@Test
	void contextLoads() {
		Rule rule = ruleMapper.selectOne(Wrappers.<Rule>lambdaQuery().eq(Rule::getMarketId, 12));
		List<RuleDetail> ruleDetails = detailMapper.selectList(Wrappers.<RuleDetail>lambdaQuery().eq(RuleDetail::getMarketId, 12));
		System.out.println(rule);
		ruleDetails.forEach(System.out::println);
	}


	@Autowired
	private IParkStandardService parkStandardService;
	@Test
	void demo () {
	/*	BigDecimal fee = parkStandardService.fee(10, null, 1, "");
		System.out.println(fee);*/

		DateTime inDateTime = DateTime.parse("2020-10-9 8:7:11", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
		//最终开始时间点 = 入场时间 + 免费时长
		DateTime startTime = inDateTime.plusMinutes(30);
		//最终结束时间点 = 最终开始时间点 + 最终时长
		DateTime endTime = startTime.plusMinutes(100);

//		System.out.println(inDateTime);
//		System.out.println(startTime);
//		System.out.println(endTime);
//		System.out.println((endTime.getMillis() - startTime.getMillis())/1000/60);

		System.out.println(startTime);
		System.out.println(startTime.getYear());		//年
		System.out.println(startTime.getMonthOfYear());	//月
		System.out.println(startTime.getDayOfMonth());	//天
		System.out.println(startTime.getHourOfDay());	//获取小时
		System.out.println(startTime.getMinuteOfHour());	//获取分钟
		System.out.println(startTime.getSecondOfMinute());	//获取秒
		System.out.println(startTime.getMillisOfSecond());	//获取毫秒


	}

	@Test
	void test() {
		//BigDecimal fee = parkStandardService.feeFixedHours(11, "2020-01-01 01:00:00", 15);
		BigDecimal fee = parkStandardService.feeOutTime(11, "2020-01-01 01:00:00", "2020-01-01 11:00:00");
		System.out.println(fee);

	}

}
