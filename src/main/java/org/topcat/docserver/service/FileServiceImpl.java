package org.topcat.docserver.service;

import com.caucho.hessian.client.HessianProxyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.topcat.docserver.model.UploadResult;
import org.topcat.docserver.model.UploadSource;

import java.net.MalformedURLException;

public class FileServiceImpl extends HessianServiceProxyBase<FileService> implements FileService {

    private static final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    @Override
    public UploadResult store(UploadSource file) {
        for (int i = 0; i < retry; i++) {
            FileService service = getNextService();
            try {
                return service.store(file);
            } catch (Exception e) {
                if (logger.isErrorEnabled()) {
                    logger.error("connect to file server error", e);
                }
            }
        }
        throw new IllegalStateException("FileService is not enable.");
    }

    @Override
    public void remove(String path) {
        for (int i = 0; i < retry; i++) {
            FileService service = getNextService();
            try {
                service.remove(path);
                return;
            } catch (Exception e) {
                if (logger.isErrorEnabled()) {
                    logger.error("connect to file server error", e);
                }
            }
        }
        throw new IllegalStateException("FileService is not enable.");
    }

    @Override
    public FileService getService(HessianProxyFactory factory, String url) {
        try {
            return (FileService) factory.create(FileService.class, url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

}
