package com.hua.service.impl;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.hua.constant.Constant;
import com.hua.po.Mapping;
import com.hua.po.Page;
import com.hua.service.MappingService;
import com.hua.service.TicketManagerService;
import com.hua.util.CertificateCoder;
import com.hua.util.SSLClient;

@Service("TicketManagerService")
public class TicketManagerServiceImpl implements TicketManagerService {
	
	private final static Logger LOG = LoggerFactory.getLogger(CgbStoreTicketManagerServiceImpl.class);

	private String certificatePath = "finance.cer";
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	@Autowired
	private SSLClient sslClient;
	@Autowired
	private MappingService mappingService;
	 
	@Override
	public String checkAuth(String requestBody, String requestId) {
		String stKey = null;
		try {
//			String str = RequestUtil.getRequestBody(request);
			LOG.info("入参：" + requestBody);
			Map<String, String> requestMap = Page.objectMapper.readValue(requestBody, Map.class);
			
			JSONObject jsonObj = new JSONObject(requestMap);
			boolean pass = hasSession(jsonObj);
			//如果用户登录过，直接返回
			if(pass) {
				return requestMap.get(Constant.ST);
			} 
			//如果没有登录过，则拿ticket key 去sso校验，并获取用户数据，生成登录信息
			else {
				stKey = hasTicket(jsonObj);
				String password = "123456";  
			    String alias = "localhost";  
			    String keyStorePath = "finance.keystore"; 
				String stKeyEncode = CertificateCoder.encryptByPrKey(stKey.getBytes(), alias, password);
				JSONObject obj = new JSONObject();
				obj.put("key", Constant.ST);
				obj.put("value", stKeyEncode);
				redisTemplate.opsForValue().set("cookie" + requestId, obj.toString(), Constant.EXPIRE_TIME_IN_MINUTES, TimeUnit.MINUTES);
			}
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return stKey;
	}

	/**
	 * 根据ticket key 去sso服务获取用户数据
	 */
	@Override
	public String hasTicket(JSONObject requestObj) {
		try {
//			String isLoggedin = requestObj.getString("isLoggedin");
			String ticket = requestObj.getString("ticket");
			
			JSONObject jsonObj = new JSONObject();
        	jsonObj.put("serviceName", "ticketManagerService");
        	jsonObj.put("methodName", "getTicket");
        	jsonObj.put("data", ticket);
        	
        	String ticketEncode = sslClient.process(jsonObj);
        	String ticketValue = CertificateCoder.decryptByPuKey(ticketEncode);
        	if(StringUtils.isEmpty(ticketValue)) {
        		return null;
        	}
        	String ticketKey = CertificateCoder.decryptByPuKey(ticket);
        	
        	String stKey = Constant.ST + ticketKey;
        	String stValue = ticketValue;
        	if(!StringUtils.isEmpty(stValue)) {
        		redisTemplate.opsForValue().set(stKey, stValue, Constant.EXPIRE_TIME_IN_MINUTES, TimeUnit.MINUTES);
        	}
			return stKey;
		} catch (Exception e) {
			LOG.error("参数解密失败", e);
			return null;
		}
	}

	/** 
	 * 检查用户是否登录过
	 */
	@Override
	public boolean hasSession(JSONObject requestObj) {
		boolean pass = false;
		try {
			boolean hasKey = requestObj.has(Constant.ST);
			if(hasKey) {
				String st = requestObj.getString(Constant.ST);
				if(StringUtils.isNotEmpty(st)) {
					String stKey = CertificateCoder.decryptByPuKey(st);
					String stValue = redisTemplate.opsForValue().get(stKey);
					LOG.info("hasSession:" + stValue);
					if(!StringUtils.isEmpty(stValue)) {
						pass = true;
					}
				} 
			}
		} catch (Exception e) {
			LOG.error("参数解密失败", e);
		}
		return pass;
	}

	@Override
	public boolean hasRole(String requestBody, ProceedingJoinPoint pjp, String requestId) {
		boolean pass = false;
		String st = null;
		try {
			JSONObject requestObj = new JSONObject(requestBody);
			boolean hasKey = requestObj.has(Constant.ST);
			if(hasKey) {
				st = requestObj.getString(Constant.ST);
			} else {
				String json = redisTemplate.opsForValue().get("cookie" + requestId);
				JSONObject obj = new JSONObject(json);
				st = obj.getString("value");
			}
			
			if(StringUtils.isNotEmpty(st)) {
				String stKey = CertificateCoder.decryptByPuKey(st);
				String stValue = redisTemplate.opsForValue().get(stKey);
				LOG.info("hasSession:" + stValue);
				if(!StringUtils.isEmpty(stValue)) {
					Map<String, String> accountMap = Page.objectMapper.readValue(stValue, Map.class);
					Mapping mapping = new Mapping();
					mapping.setClass_("class " + pjp.getTarget().getClass().getName());
					mapping.setMethod(pjp.getSignature().getName());
					mapping.setAuthorize_name(accountMap.get("auth"));
					Integer hasRole = mappingService.checkRole(mapping);
					if(hasRole > 0) {
						return true;
					}
				}
			} 
		} catch (Exception e) {
			LOG.error("参数解密失败", e);
		}
		return pass;
	}
	
	
}
