package org.hua.consul;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties()
public class AppProp {

	private String consul_test;

	public String getConsul_test() {
		return consul_test;
	}

	public void setConsul_test(String consul_test) {
		this.consul_test = consul_test;
	}
	
	
}
