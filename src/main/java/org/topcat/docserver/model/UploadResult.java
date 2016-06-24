package org.topcat.docserver.model;

import java.io.Serializable;

public class UploadResult implements Serializable {

	private static final long serialVersionUID = -8227277408478690545L;

	/**
	 * 是否成功
	 */
	private boolean success = true;

	/**
	 * 错误描述
	 */
	private String errorInfo;

	/**
	 * 成功存储后文件访问路径
	 */
	private String path;

	/**
	 * 成功存储后分配的id
	 */
	private String newId;

	public void setErrorInfo(String errorInfo) {
		this.errorInfo = errorInfo;
		if (errorInfo != null) {
			this.setSuccess(false);
		}
	}

	public void setPath(String path) {
		this.path = path;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getErrorInfo() {
		return errorInfo;
	}

	public String getPath() {
		return path;
	}

	public String getNewId() {
		return newId;
	}

	public void setNewId(String newId) {
		this.newId = newId;
	}

}
