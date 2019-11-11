package com.ilang.ad.gatewaytest.nacos;

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.naming.NamingService;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@RefreshScope
public class NamingConfig {

	@NacosInjected
	NamingService namingService;

	@PostConstruct
	public void init() {
		System.out.println(this.namingService);
	}

}
