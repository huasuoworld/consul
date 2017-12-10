package com.hua.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManagerFactory;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.hua.constant.Constant;
import com.hua.service.KeyValueService;  
  
/** 
 * SSL Client 
 *  
 */  
@Component("sslClient")
@Scope("prototype")
public class SSLClient {  
	
	private final static Logger LOG = LoggerFactory.getLogger(SSLClient.class);
    /** 
     * 启动客户端程序 
     *  
     * @param args 
     */  
    public static void main1(String[] args) {  
    	SSLClient client = new SSLClient();
    	String ticket = "sdsadasds";
    	JSONObject obj = new JSONObject();
    	obj.put("serviceName", "ticketManagerService");
    	obj.put("methodName", "getTicket");
    	obj.put("data", ticket);
    	
        String str = client.process(obj); 
        System.out.println(str);
    }  
   
    /**
     * step1 拿到票据去sso服务器认证
     * step2 认证完成收到用户信息解密后存放到session，并将key存入cookie
     * @param ticket
     * @return
     */
    public String process(JSONObject jsonObj) {  
    	SSLSocket sslSocket = sslSocket();
        if (sslSocket == null) {  
        	LOG.error("ERROR");  
            return null;  
        }  
        try {  
        	
            InputStream input = sslSocket.getInputStream();  
            OutputStream output = sslSocket.getOutputStream();  
  
            BufferedInputStream bis = new BufferedInputStream(input);  
            BufferedOutputStream bos = new BufferedOutputStream(output);  
  
            bos.write(jsonObj.toString().getBytes());  
            bos.flush();  
            StringBuffer readBuff = new StringBuffer();
            byte[] contents = new byte[1024];
            int bytesRead = 0;
            while((bytesRead = bis.read(contents)) != -1) { 
            	String str = new String(contents, 0, bytesRead);
            	readBuff.append(str);              
            	if(bytesRead < 1024) {
            		break;
            	}
            }
            String ticketValue = readBuff.toString();
            LOG.info(ticketValue + " | " + ticketValue.length());  
            
            bis.close();
            bos.close();
            input.close();
            output.close();
            sslSocket.close();
            
        	return ticketValue;
        } catch (IOException e) {  
        	LOG.error(e.getMessage(), e); 
        	return null;
        }  
    }  
  
    public SSLSocket sslSocket() {  
        try {  
        	KeyValueService keyValueService = ApplicationContextHolder.getBean(KeyValueService.class);
            //获取SSlContext对象  
            SSLContext ctx = SSLContext.getInstance("SSL");  
            //JSSE密钥管理器KeyManagerFactory对象  
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");  
            //信任管理器TrustManagerFactory对象  
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");  
            //密钥和证书的存储设施  
            KeyStore ks = KeyStore.getInstance("JKS");  
            KeyStore tks = KeyStore.getInstance("JKS");  
            //载入keystore  
            String key_store_path = keyValueService.get(Constant.FINANCE_KEYSTORE_JKS);
            char[] server_key_store_password = keyValueService.get(Constant.FINANCE_KEYSTORE_PASSWORD).toCharArray();
//            char[] server_trust_key_store_password = keyValueService.get("finance.server_trust_key_store_password").toCharArray();
            ks.load(new FileInputStream(key_store_path), server_key_store_password);  
            tks.load(new FileInputStream(key_store_path), server_key_store_password);  
            //KeyManagerFactory对象初始化  
            kmf.init(ks, server_key_store_password);  
            //TrustManagerFactory对象初始化  
            tmf.init(tks);  
            //SSLContext对象初始化  
            ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);  
            //创建连接sslSocket对象  
            SSLSocket sslSocket = (SSLSocket) ctx.getSocketFactory().createSocket(keyValueService.get(Constant.FINANCE_DEFAULT_HOST), 
            		Integer.valueOf(keyValueService.get(Constant.FINANCE_DEFAULT_PORT)));  
            return sslSocket;
        } catch (Exception e) {  
        	LOG.error(e.getMessage(), e); 
        	return null;
        }  
    }  
  
}  
