package org.hua.consul;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppPropS {


	@Value("${logging.path}")
	private String loggingPath;

	public String getLoggingPath() {
		return loggingPath;
	}

	public void setLoggingPath(String loggingPath) {
		this.loggingPath = loggingPath;
	}
	
	
}
