package xxx.filter;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.hua.service.TicketManagerService;
import com.hua.service.impl.CgbStoreTicketManagerServiceImpl;
import com.hua.util.ApplicationContextHolder;
import com.hua.util.RequestConvert;

@Aspect
@Component
public class AuthCheck {
	
	private final Logger LOG = LoggerFactory.getLogger(AuthCheck.class);
	private TicketManagerService ticketManagerService;
	@Autowired
	private RedisTemplate<String, String> redisTemplate;

	// 定义一个切入点
	@Pointcut("execution(* xxx.controller.*.*.*(..))")
	private void administrator() {

	}
	@Pointcut("execution(* xxx.controller.*.*(..))")
	private void controller() {
		
	}
	
	@Around("administrator()")
	public Object administratorAround(ProceedingJoinPoint pjp) throws Throwable {
		return check(pjp);
	}
	
	@Around("controller()")
	public Object controllerAround(ProceedingJoinPoint pjp) throws Throwable {
		return check(pjp);
	}
		
	public Object check(ProceedingJoinPoint pjp) throws Throwable {
		Object returnObj = null;
		boolean pass = false;
		
		// 接收到请求，记录请求内容
		ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = requestAttributes.getRequest();
		String requestId = request.getSession().getId();
		try {
			LOG.info("requestId:" + requestId);
			ticketManagerService = ApplicationContextHolder.getBean(CgbStoreTicketManagerServiceImpl.class);
			String requestBody = RequestConvert.requestConvertJSON(request).toString();
			LOG.info(pjp.getTarget().getClass().getName()+"|"+pjp.getSignature().getName());
			//缓存request body 数据
			LOG.info(pjp.getTarget().getClass().getName() + ">>" + pjp.getSignature().getName()+requestId );
			String stKey = ticketManagerService.checkAuth(requestBody, requestId);
			if(!StringUtils.isEmpty(stKey)) {
				pass = true;
			}
			pass = ticketManagerService.hasRole(requestBody, pjp, requestId);
		} catch (Exception e) {
			LOG.error("权限校验失败！", e);
		} 
		if(pass) {
			try {
				returnObj = pjp.proceed();
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
			} finally {
				//删除request body 及 cookie 数据
				redisTemplate.delete("cookie" + requestId);
			}
		} else {
			LOG.error("授权失败！");
		}
		return returnObj;
	}
}
