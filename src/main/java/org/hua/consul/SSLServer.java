package com.hua.sso.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.TrustManagerFactory;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.hua.sso.constant.Constant;
import com.hua.sso.service.TicketManagerService;
import com.hua.sso.service.impl.TicketManagerServiceImpl;

/** 
 * 
 */  
@Component("sslServer")
@Scope("prototype")
public class SSLServer extends Thread {  
	
	private static final Logger LOG = LoggerFactory.getLogger(SSLServer.class);
  
    private TicketManagerService ticketManagerService;
    private SSLServerSocket sslServerSocket;
    
    public SSLServerSocket getSslServerSocket() {
		return sslServerSocket;
	}

	public void setSslServerSocket(SSLServerSocket sslServerSocket) {
		this.sslServerSocket = sslServerSocket;
	}

	/** 
     * 启动程序 
     *  
     * @param args 
     */  
    public static void main1(String[] args) {  
    	SSLServer server = new SSLServer();
        Thread thread = new Thread(server);  
        thread.start();  
        System.out.println("server start");
    }  
    
    public synchronized void start() {  
    	try {
    		SSLServerSocket sslServerSocket = getSslServerSocket();;
            if (sslServerSocket == null) {  
                System.out.println("ERROR");  
                return;  
            }  
            ticketManagerService = ApplicationContextHolder.getBean(TicketManagerServiceImpl.class);
            while (true) {  
                try {  
                    Socket s = sslServerSocket.accept();  
                    InputStream input = s.getInputStream();  
                    OutputStream output = s.getOutputStream();  
      
                    BufferedInputStream bis = new BufferedInputStream(input);  
                    BufferedOutputStream bos = new BufferedOutputStream(output);  
                    String message = "";
                    StringBuffer readBuff = new StringBuffer();
                    byte[] contents = new byte[1024];
                    int bytesRead = 0;
                    while((bytesRead = bis.read(contents)) != -1) { 
                    	String str = new String(contents, 0, bytesRead);
                    	readBuff.append(str);              
                    	System.out.println(str + ">>" + bytesRead);
                    	if(bytesRead < 1024) {
                    		break;
                    	}
                    }
//                    String str = new String(buffer);
                    LOG.info("------receive:--------"+readBuff.toString()+ " | " + readBuff.toString().length());  
                    
                    try {
                    	JSONObject jsonObj = new JSONObject(readBuff.toString());
                    	String serviceName = jsonObj.getString("serviceName");
                    	String methodName = jsonObj.getString("methodName");
                    	if("ticketManagerService".equals(serviceName)) {
                    		if("removeTicket".equals(methodName)) {
                    			ticketManagerService.removeTicket(jsonObj);
                    		} else if("getTicket".equals(methodName)) {
                    			message = ticketManagerService.getTicket(jsonObj);
                    		}
                    	}
    				} catch (Exception e) {
    					LOG.error(e.getMessage(), e);
    					message = e.getMessage();
    				}
    				
                    bos.write(message.getBytes());  
                    bos.flush();  
      
                    s.close();  
                    input.close();
                    output.close();
                    bis.close();
                    bos.close();
                } catch (Exception e) {  
                	LOG.error(e.getMessage(), e); 
                }  
            }  
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
    }  
    public SSLServerSocket sslServerSocket() {
    	try {  
            SSLContext ctx = SSLContext.getInstance("SSL");  
  
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");  
            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");  
  
            KeyStore ks = KeyStore.getInstance("JKS");  
            KeyStore tks = KeyStore.getInstance("JKS");  
  
            ks.load(
            		new FileInputStream(Constant.sso.getProperty(Constant.KEY_STORE_PATH)), 
            		Constant.sso.getProperty(Constant.SERVER_KEY_STORE_PASSWORD).toCharArray());  
            tks.load(
            		new FileInputStream(Constant.sso.getProperty(Constant.KEY_STORE_PATH)), 
            		Constant.sso.getProperty(Constant.SERVER_KEY_STORE_PASSWORD).toCharArray());  
  
            kmf.init(ks, Constant.sso.getProperty(Constant.SERVER_TRUST_KEY_STORE_PASSWORD).toCharArray());  
            tmf.init(tks);  
            ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);  
  
            SSLServerSocket serverSocket = (SSLServerSocket) ctx.getServerSocketFactory()
            		.createServerSocket(Integer.valueOf(Constant.sso.getProperty(Constant.DEFAULT_PORT)));  
            serverSocket.setNeedClientAuth(true);  
            return serverSocket;
        } catch (Exception e) {  
        	LOG.error(e.getMessage(), e);
        	return null;
        } 
    }
  
    public void run() {
    	setSslServerSocket(sslServerSocket());
        start();  
    }  
}  

