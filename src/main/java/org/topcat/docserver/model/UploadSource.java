package org.topcat.docserver.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 上传对象
 * 
 * @author fish
 * 
 */
public class UploadSource implements Serializable {

	private static final long serialVersionUID = -6382565578676408526L;

	/**
	 * 文件本体
	 */
	private byte[] body;

	/**
	 * 文件名
	 */
	private String filename;

	/**
	 * 文件附加属性,比如标题,上传源路径,上传用户id等等
	 */
	private Map<String, Serializable> appends;

	public byte[] getBody() {
		return body;
	}

	public void setBody(byte[] body) {
		this.body = body;
	}

	public Map<String, Serializable> getAppends() {
		return appends;
	}

	public UploadSource append(String key, Serializable value) {
		if (appends == null) {
			appends = new HashMap<String, Serializable>();
		}
		appends.put(key, value);
		return this;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

}
