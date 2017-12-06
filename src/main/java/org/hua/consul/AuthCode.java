package com.hua.util;

import java.awt.image.BufferedImage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;

public class AuthCode {

	private static final Logger LOG = LoggerFactory.getLogger(AuthCode.class);

	private static DefaultKaptcha captchaProducer;

	private static Config config;

	static {
		config = new Config(com.hua.conf.Config.authCode);
		captchaProducer = new DefaultKaptcha();
		captchaProducer.setConfig(config);
	}

	public static String createText() {
		String str = "";
		try {
			str = captchaProducer.createText();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return str;
	}

	public static BufferedImage createImage(String capText) {
		BufferedImage bufferedImage = null;
		try {
			bufferedImage = captchaProducer.createImage(capText);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
		return bufferedImage;
	}

}
