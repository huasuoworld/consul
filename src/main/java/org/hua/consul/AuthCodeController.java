package com.hua.controller.api;

import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.google.code.kaptcha.Constants;

import com.hua.constant.Cros;
import com.hua.interfaces.redis.RedisService;
import com.hua.utils.AuthCode;
import com.hua.vo.ResultTo;
/**
 * 验证码接口
 * @author 
 *
 */
@CrossOrigin(allowCredentials="true",origins=Cros.CORS_URL)
@Controller
@RequestMapping("api/authCode")
public class AuthCodeController {
	
	@Autowired
	private RedisService redisService;
	private static final Logger LOG = LoggerFactory.getLogger(AuthCodeController.class);

	private static String key = Constants.KAPTCHA_SESSION_KEY;
	
	@RequestMapping("checkKaptchaImge")
	@ResponseBody
	public ResultTo checkKaptchaImge(HttpServletRequest request, HttpServletResponse response){
	   
	   String newcode = request.getParameter("code");
	   String openid = request.getParameter("openid");
	   String oldcode =  this.redisService.get(key+"_"+openid);
	   LOG.info("******************之前验证码是: " + oldcode + "******************"+"新验证码："+newcode);
	   if(newcode.equals(oldcode)){
	    return ResultTo.newSuccessResultTO("成功", null); 
	   }else{
	       return ResultTo.newFailResultTO("动态验证码校验失败", null);
	   }
	  
	    
	}
	@RequestMapping("getKaptchaImage")
	public ModelAndView getKaptchaImage(HttpServletRequest request, HttpServletResponse response) throws Exception {
	        String openid = request.getParameter("openid");
//		String capText = redisService.get(key);
//		String code = redisService.get(key+"_"+phone);
//                if(StringUtils.isEmpty(capText)){
	         String skey = key+"_"+openid;
	         
	         LOG.info("******************key是: " + skey + "******************");
                    response.setDateHeader("Expires", 0);
                    
                    // Set standard HTTP/1.1 no-cache headers.
                    response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
                    
                    // Set IE extended HTTP/1.1 no-cache headers (use addHeader).
                    response.addHeader("Cache-Control", "post-check=0, pre-check=0");
                    
                    // Set standard HTTP/1.0 no-cache header.
                    response.setHeader("Pragma", "no-cache");
                    
                    // return a jpeg
                    response.setContentType("image/jpeg");
                    
//                }

		// create the text for the image
		String capText = AuthCode.createText();

		// store the text in the session
		redisService.setByOutOfTime(skey, capText, 2 * 60);

		// create the image with the text
		BufferedImage bi = AuthCode.createImage(capText);
		ServletOutputStream out = response.getOutputStream();

		// write the data out
		ImageIO.write(bi, "jpg", out);
		try {
			out.flush();
		} finally {
			out.close();
		}
		return null;
	}
}
