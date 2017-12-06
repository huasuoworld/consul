package weijiang.jiangxinDAO.boot;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Config {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Config.class);
	
	public static Properties bootstrap;
	
	static {
		bootstrap = getProperties("bootstrap.properties");
	}

	public static Properties getProperties(String fileName){
        
        InputStreamReader is = null;
        Properties properties=new Properties();
		try {
			is = new InputStreamReader(Config.class.getClassLoader().getResourceAsStream(fileName),"UTF-8");
       
            properties.load(is);
           
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
        	 if(is!=null){
                 try {
					is.close();
				} catch (IOException e) {
					LOGGER.error(e.getMessage(), e);
				}
             }
        }
        
        return properties;
    }
}
