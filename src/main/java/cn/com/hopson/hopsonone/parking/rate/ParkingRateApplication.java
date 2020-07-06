package cn.com.hopson.hopsonone.parking.rate;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("cn.com.hopson.hopsonone.parking.rate.mapper")
public class ParkingRateApplication {

	public static void main(String[] args) {
		SpringApplication.run(ParkingRateApplication.class, args);
	}

}
