package com.hua.sso.service.impl;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.hua.sso.service.TicketManagerService;
import com.hua.sso.util.CertificateCoder;

@Service("ticketManagerService")
public class TicketManagerServiceImpl implements TicketManagerService {
	
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	private String certificatePath = "xxx.cer";

	@Override
	public void removeTicket(JSONObject jsonObj)  throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public String getTicket(JSONObject jsonObj)  throws Exception {
		String str = jsonObj.getString("data");
		String key = CertificateCoder.decryptByPuKey(str, certificatePath);
		String value = redisTemplate.opsForValue().get(key);
		if(value == null) {
			return "key>>" + key + ">value>>" + value;
		}
		return value;
	}

}
