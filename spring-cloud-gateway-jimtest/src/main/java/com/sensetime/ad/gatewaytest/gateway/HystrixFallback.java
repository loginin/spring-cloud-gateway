package com.sensetime.ad.gatewaytest.gateway;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *  * @author duanzongliang@sensetime.com  * @date 2019/10/18 14:59  
 */
@RestController
@RequestMapping("/hystrix")
public class HystrixFallback {

	@RequestMapping("/fallback")
	public String fallback() {
		return "fallback response.";
	}

}
