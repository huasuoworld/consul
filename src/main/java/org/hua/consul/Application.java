package org.hua.consul;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Configuration
@EnableAutoConfiguration
@EnableDiscoveryClient
@RestController
@EnableConfigurationProperties(AppProp.class)
@ComponentScan(basePackages="org.hua.consul")
public class Application {
	
	@Autowired
	private AppProp appProp;

	@RequestMapping("/")
	public String home() {
		return appProp.getConsul_test();
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}