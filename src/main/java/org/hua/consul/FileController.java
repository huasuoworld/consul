package com.hua.controller.api;

import java.io.InputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hua.constant.Cros;
import com.hua.interfaces.common.FileService;
import com.hua.po.FileParam;
import com.hua.utils.BASE64Util;


@CrossOrigin(allowCredentials="true", origins=Cros.CORS_URL)
@Controller
public class FileController {
	
	private final static Logger LOG = LoggerFactory.getLogger(FileController.class);
	
	@Autowired
	private FileService fileService;

	@RequestMapping("api/file/upload")
	@ResponseBody
	public String upload(FileParam upload) {
		FileParam outFile = null;
		try {
			outFile = fileService.upload(upload);
			return outFile.getFullname();
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return null;
		}
	}
	
	@RequestMapping(method=RequestMethod.GET, path="api/file/download")
	public HttpEntity<byte[]> download(String fullname) {
		InputStream is = null;
		try {
			FileParam file = new FileParam();
			file.setFullname(fullname);
			FileParam download = fileService.download(file);
			
			//创建临时文件
			byte[] imageByteArray = BASE64Util.decodeImage(download.getBase64file());
			/** assume that below line gives you file content in byte array **/
		    // prepare response
		    HttpHeaders header = new HttpHeaders();
		    header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fullname);
		    return new HttpEntity<byte[]>(imageByteArray, header);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return null;
		} finally {
			try {
				if(is != null) {
					is.close();
				}
			} catch (Exception e2) {
				LOG.error(e2.getMessage(), e2);
			}
		}
	}
	
	@RequestMapping(method=RequestMethod.GET, path="api/file/lookup")
	public void lookup(String fullname, HttpServletResponse resposne) {
		InputStream is = null;
		ServletOutputStream os = null;
		try {
			FileParam file = new FileParam();
			file.setFullname(fullname);
			FileParam download = fileService.download(file);
			
			//创建临时文件
			byte[] imageByteArray = BASE64Util.decodeImage(download.getBase64file());
			/** assume that below line gives you file content in byte array **/
		    os = resposne.getOutputStream();
		    os.write(imageByteArray);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		} finally {
			try {
				if(is != null) {
					is.close();
				}
			} catch (Exception e2) {
				LOG.error(e2.getMessage(), e2);
			}
			try {
				if(os != null) {
					os.close();
				}
			} catch (Exception e2) {
				LOG.error(e2.getMessage(), e2);
			}
		}
	}
}
