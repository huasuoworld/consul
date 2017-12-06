package com.hua;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.hua.po.Mapping;
import com.hua.po.Page;
import com.hua.service.MappingService;

/**
 * Hello world!
 *
 */
@ComponentScan(basePackages = "com.hua")
@Controller
@EnableAutoConfiguration
@ServletComponentScan
@EnableScheduling
@ImportResource(locations = { "classpath:spring-mybatis.xml", 
		"classpath:spring-redis-session.xml",
		"classpath:spring-activeMQ.xml"})
public class App {
	
	private static final Logger LOG = LoggerFactory.getLogger(App.class);
	
	@Autowired
	private MappingService mappingService;
	
	@RequestMapping("/home")
	@ResponseBody
	String home() throws JsonProcessingException {
		Mapping mapping = new Mapping();
		mapping.setPage(0);
		mapping.setPageSize(1000);
		return "hello finance! \n"+ Page.objectMapper.writeValueAsString(mappingService.search(mapping));
	}

	public static void main(String[] args) throws Exception {
		ConsulProperties consulProperties = new ConsulProperties();
		consulProperties.onStartup();
		ConfigurableApplicationContext ctx = SpringApplication.run(App.class, args);
		MappingService mappingSmervice = (MappingService) ctx.getBean("mappingService");
		mappingSmervice.init();
		LOG.info("程序启动成功>>>>>>>>>>>>>>>>>>>>>>>");
	}
	
	@Bean
	public SimpleClientHttpRequestFactory simpleClientHttpRequestFactory() {
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		factory.setConnectTimeout(5 * 1000);
		factory.setReadTimeout(5 * 1000);
		return factory;
	}
	
	@Bean
	public RestTemplate restTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setRequestFactory(simpleClientHttpRequestFactory());
		return restTemplate;
	}
}
