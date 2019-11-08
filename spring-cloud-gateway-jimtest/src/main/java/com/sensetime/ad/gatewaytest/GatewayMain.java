package com.sensetime.ad.gatewaytest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;

/**
 *  * @author duanzongliang@sensetime.com  * @date 2019/6/10 14:26  
 */
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class,
		RedisAutoConfiguration.class, DataSourceAutoConfiguration.class })
@EnableConfigurationProperties
public class GatewayMain {

	public static void main(String[] args) throws Exception {
		ConfigurableApplicationContext ctx = SpringApplication.run(GatewayMain.class);
		System.out.println("................");
	}

}
