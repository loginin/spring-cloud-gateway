package com.ilang.ad.gatewaytest.hystrix;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/hystrix")
public class HystrixFallback {

	public String fallback() {
		return "fallback response.";
	}

	@RequestMapping("/fallback")
	public Map<String, String> fallbackJson() {
		Map<String, String> ret = new HashMap<>();
		ret.put("success", "Sorry, we are in hole.");
		return ret;
	}

}
