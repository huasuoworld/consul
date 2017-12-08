package com.hua.boot;

import java.io.IOException;
import java.io.StringReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.WebApplicationInitializer;

import com.bluemobi.conf.Config;
import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.agent.model.NewService;
import com.ecwid.consul.v1.kv.model.GetValue;

@Component
public class ConsulProperties implements WebApplicationInitializer {
	
	Logger LOGGER = LoggerFactory.getLogger(ConsulProperties.class);
	
	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		String host = Config.bootstrap.getProperty("spring.cloud.consul.host");
    	String key = Config.bootstrap.getProperty("spring.cloud.consul.config.data-key");
		LOGGER.info("Consul start!");
		ConsulClient consulClient = new ConsulClient(host, 8500);
		Response<GetValue> response = consulClient.getKVValue("configuration/application/" + key);
		LOGGER.info(response.getValue().getDecodedValue());
		
		Properties properties = new Properties();
		StringReader reader = new StringReader(response.getValue().getDecodedValue());
		try {
			properties.load(reader);
			for (Entry<Object, Object> m : properties.entrySet()) {
				System.setProperty((String)m.getKey(), (String)m.getValue());
			}
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
		try {
			String ip = "";
			String appName = System.getProperty("spring.application.name");
			String script = System.getProperty("consul.script");
			String httpUrl = System.getProperty("consul.httpurl");
			String ether = System.getProperty("consul.ether");
			String interval = System.getProperty("consul.interval");
			List<String> tags = Collections.singletonList(ip + System.getProperty("consul.tags"));
			
		    NetworkInterface networkInterface = NetworkInterface.getByName(ether);
		    Enumeration<InetAddress> inetAddress = networkInterface.getInetAddresses();
		    InetAddress currentAddress;
		    currentAddress = inetAddress.nextElement();
		    while(inetAddress.hasMoreElements())
		    {
		        currentAddress = inetAddress.nextElement();
		        if(currentAddress instanceof Inet4Address && !currentAddress.isLoopbackAddress())
		        {
		            ip = currentAddress.toString().replace("/", "");
		            break;
		        }
		    }
			
			NewService.Check serviceCheck = new NewService.Check();
			if(StringUtils.isNotEmpty(httpUrl)) {
				serviceCheck.setHttp(getHttpUrl(ip, httpUrl));
			} else {
				serviceCheck.setScript(script);
			}
			serviceCheck.setInterval(interval);
			
			NewService newService = new NewService();
			newService.setId(ip + appName);
			newService.setTags(tags);
			newService.setAddress(ip);
			newService.setName(ip + appName);
			newService.setEnableTagOverride(true);
			newService.setCheck(serviceCheck);
			
			consulClient.agentServiceRegister(newService);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		
		LOGGER.info("Consul start successful!");
	}
	
	private String getHttpUrl(String ip, String url) {
		String httpUrl =  System.getProperty("consul.http") + ip + ":" 
						+ System.getProperty("server.port")
						+ url;
		return httpUrl;
	}
}

