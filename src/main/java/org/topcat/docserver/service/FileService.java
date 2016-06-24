package org.topcat.docserver.service;


import org.topcat.docserver.model.UploadResult;
import org.topcat.docserver.model.UploadSource;

public interface FileService {
	/**
	 * 上传文件
	 * 
	 * @param file
	 * @return
	 */
	UploadResult store(UploadSource file);

	/**
	 * 根据path(store方法返回的FileStoreResult.SavedFile.idUrlPath或者fkUrlPath)删除文件
	 * 
	 * @param path
	 */
	void remove(String path);
}
