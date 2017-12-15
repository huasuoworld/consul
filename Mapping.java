
@Autowired  
private RequestMappingHandlerMapping requestMappingHandlerMapping;
    
@Override
	public void init() {
		try {
			List<String> checkList = new ArrayList<String>();
			List<Mapping> list = new ArrayList<Mapping>();
	        Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping.getHandlerMethods(); 
	        
	        for (Map.Entry<RequestMappingInfo, HandlerMethod> m : map.entrySet()) {
	        	Mapping mapping = new Mapping();
	            RequestMappingInfo info = m.getKey();  
	            HandlerMethod method = m.getValue();  
	            
	            if(!checkList.contains(info.getPatternsCondition().toString()) && method.getMethod().getDeclaringClass().toString().indexOf("weijiang") >= 0) {
		            mapping.setUrl(info.getPatternsCondition().toString());
		            mapping.setClass_(method.getMethod().getDeclaringClass().toString());
		            mapping.setMethod(method.getMethod().getName());
		       	 	list.add(mapping);
		       	 	checkList.add(info.getPatternsCondition().toString());
	            }
	        } 
			//清空mapping表
			truncate();
			//重新插入数据
			insert(list);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
