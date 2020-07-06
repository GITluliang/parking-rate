package cn.com.hopson.hopsonone.parking.rate;

import cn.com.hopson.hopsonone.parking.rate.entity.Rule;
import cn.com.hopson.hopsonone.parking.rate.entity.RuleDetail;
import cn.com.hopson.hopsonone.parking.rate.mapper.RuleDetailMapper;
import cn.com.hopson.hopsonone.parking.rate.mapper.RuleMapper;
import cn.com.hopson.hopsonone.parking.rate.service.IParkStandardService;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.sun.jmx.snmp.Timestamp;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.CopyOnWriteArrayList;

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
	public void demo2() {
		DateTime inDateTime = DateTime.parse("2020-10-09 00:00:00", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
		DateTime inDateTime1 = DateTime.parse("2020-10-08 23:59:59", DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss"));
		System.out.println(inDateTime.getMillis());
		System.out.println(inDateTime1.getMillis());
		System.out.println(inDateTime1.getMillis() - inDateTime.getMillis());
	}

	@Test
	void test() {
		BigDecimal fee = parkStandardService.fee(11, "2020-01-01 21:00:00", 15);
		System.out.println(fee);
//
//		//DateToStringBeginOrEnd(new Date(), false);
////		DateTime startOrEnd = getStartOrEnd(DateTime.now(), 22, 7, true);
////		System.out.println(startOrEnd);

//		int[] a = {20,2};
//		int[] b = {3,12};
//		int[] c = {13,16};
//		int[] d = {17,19};
//		int[] a1 = {17,19};
//		int[] a2 = {17,19};
//
//		List<int[]> list = new CopyOnWriteArrayList<>();
//		list.add(a);
//		list.add(b);
//		list.add(c);
//		list.add(d);
//
//		//int num = 21 ;
//
////		for (int[] num : list) {
////			if(num[0] > num[1]) {
////				System.out.println(num[0] + " " + num[1]);
////			}
////		}
//
//		list.forEach(System.out::println);
	}



	public String DateToStringBeginOrEnd(Date date,Boolean flag) {
		String time = null;
		SimpleDateFormat dateformat1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		Calendar calendar1 = Calendar.getInstance();
		//获取某一天的0点0分0秒 或者 23点59分59秒
		if (flag == true) {
			calendar1.setTime(date);
			calendar1.set(calendar1.get(Calendar.YEAR), calendar1.get(Calendar.MONTH), calendar1.get(Calendar.DAY_OF_MONTH),
					0, 0, 0);
			Date beginOfDate = calendar1.getTime();
			time = dateformat1.format(beginOfDate);
			System.out.println(time);
		}else{
			Calendar calendar2 = Calendar.getInstance();
			calendar2.setTime(date);
			calendar1.set(calendar2.get(Calendar.YEAR), calendar2.get(Calendar.MONTH), calendar2.get(Calendar.DAY_OF_MONTH),
					23, 59, 59);
			Date endOfDate = calendar1.getTime();
			time = dateformat1.format(endOfDate);
			System.out.println(time);
		}
		return time;
	}

}
