package weijiang.jiangxinDAO.boot;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ecwid.consul.v1.ConsulClient;
import com.ecwid.consul.v1.Response;
import com.ecwid.consul.v1.kv.model.GetValue;

public class ConsulProperties {
	
	Logger LOGGER = LoggerFactory.getLogger(ConsulProperties.class);
	
	public void onStartup() throws ServletException {
		System.out.println("hello world!");
		
		ConsulClient consulClient = new ConsulClient(Config.bootstrap.getProperty("spring.cloud.consul.host"), 8500);
		Response<GetValue> response = consulClient.getKVValue("configuration/application/" + Config.bootstrap.getProperty("spring.cloud.consul.config.data-key"));
		System.out.println(response.getValue().getDecodedValue());
		
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
	}
	
}
