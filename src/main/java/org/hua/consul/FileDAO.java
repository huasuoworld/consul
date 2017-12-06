package com.hua.mongo;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.mongodb.gridfs.GridFSDBFile;

import com.hua.interfaces.common.FileCommDAO;
import com.hua.interfaces.common.KeyValueCommDAO;
import com.hua.po.FileParam;
import com.hua.utils.BASE64Util;

/**
 * mongodb存储附件，最大16M，超过16M请拆分存储
 *
 */
@Repository("fileCommDAO")
public class FileDAO implements FileCommDAO {
	
	private final static Logger LOG = LoggerFactory.getLogger(FileDAO.class);
	
	@Autowired
	private GridFsTemplate gridFsTemplate;
	@Autowired
	private KeyValueCommDAO keyValueCommDAO;

	/**
	 * 上传附件
	 * 参数:MultipartFile uploadfile
	 * */
	@Override
	public FileParam upload(FileParam upload) {
		LOG.info("FileDAO>>upload");
		String filename = UUID.randomUUID().toString().replaceAll("-", "");
		InputStream is = null;
		try {
			if(StringUtils.isEmpty(upload.getBase64file())) {
				LOG.error("文件base64不能为空！");
				return null;
			} 
			if(StringUtils.isEmpty(upload.getFullname())) {
				LOG.error("文件全名不能为空！");
				return null;
			} 
			String extension = FilenameUtils.getExtension(upload.getFullname());
			if(keyValueCommDAO.get("file.filter").indexOf(extension) < 0) {
				LOG.error("不支持该文件格式！");
				return null;
			}
			
			filename = filename + "." + extension;
			//创建临时文件
			byte[] imageByteArray = null;
			if(StringUtils.isEmpty(upload.getEncodetype())) {
				imageByteArray = BASE64Util.decodeImage(upload.getBase64file());
			} 
			else if("1".equals(upload.getEncodetype())) {
				imageByteArray = BASE64Util.decodeUrlImage(upload.getBase64file());
			}
			else if("2".equals(upload.getEncodetype())) {
				String base64file = URLDecoder.decode(upload.getBase64file(), "UTF-8");
				imageByteArray = BASE64Util.decodeUrlImage(base64file);
			}
			is = new ByteArrayInputStream(imageByteArray);
	    	gridFsTemplate.store(is, filename, extension);
	    	FileParam outfile = new FileParam();
	    	outfile.setFullname(filename);
	    	return outfile;
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return null;
		} finally {
			if( is != null ) {
				try {
					is.close();
				} catch (Exception e2) {
					LOG.error(e2.getMessage(), e2);
				}
			}
		}
	}

	/**
	 * 下载附件
	 * 参数：filename
	 * */
	@Override
	public FileParam download(FileParam download) {
		LOG.info("FileDAO>>download");
		try {
			Query query = new Query();
	        query.addCriteria(Criteria.where("filename").is(download.getFullname()));
			GridFSDBFile gridFSDBFile = gridFsTemplate.findOne(query);
	        if(gridFSDBFile == null) {
	        	LOG.error("没有找到该文件！");
	        	return null;
	        } else {
	        	FileParam outfile = new FileParam();
	        	InputStream imageInFile = null;
	        	try {
	        		imageInFile = gridFSDBFile.getInputStream();
					byte imageData[] = new byte[(int) gridFSDBFile.getLength()];
					imageInFile.read(imageData);
					String base64file = BASE64Util.encodeImage(imageData);
					LOG.info("FileDAO>>download>>base64file>>" + base64file);
					outfile.setBase64file(base64file);
				} catch (Exception e) {
					LOG.error(e.getMessage(), e);
					return null;
				} finally {
					if(imageInFile != null) {
						try {
							imageInFile.close();
						} catch (Exception e2) {
							LOG.error(e2.getMessage(), e2);
						}
					}
				}
	        	
	        	return outfile;
	        }
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			return null;
		}
	}
}
