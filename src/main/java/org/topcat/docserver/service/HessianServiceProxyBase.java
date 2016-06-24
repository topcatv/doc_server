/*
 * Ezhitou Inc.
 * Copyright (c) 2006-2012 All Rights Reserved.
 *
 * Author     :jigl
 * Version    :1.0
 * Create Date:2012-4-9
 *
 */
package org.topcat.docserver.service;

import com.caucho.hessian.client.HessianProxyFactory;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <p>Hessian远程调用服务基类，将远程调用的服务。</p>
 * <p>在应用启动时，将配置的远程调用接口一次性加载到服务器缓存中</p>
 *
 * @author jigl
 * @version $Id: HessianServiceProxyBase.java 2012-4-9 上午09:36:31 jigl $
 */
public abstract class HessianServiceProxyBase<T> {
    private List<T> services;

    private int hessianConnectTimeout = -1; // hessian连接超时时间

    private int hessianReadTimeout = -1; // hessian读超时时间

    protected int retry = 3;  // 重试次数

    private int currentService = 0;  // 当前使用的server下标，并发同步问题忽略

    public List<T> getServices() {
        return services;
    }

    public void setServices(List<T> services) {
        this.services = services;
    }

    public int getHessianConnectTimeout() {
        return hessianConnectTimeout;
    }

    public void setHessianConnectTimeout(int hessianConnectTimeout) {
        this.hessianConnectTimeout = hessianConnectTimeout;
    }

    public int getHessianReadTimeout() {
        return hessianReadTimeout;
    }

    public void setHessianReadTimeout(int hessianReadTimeout) {
        this.hessianReadTimeout = hessianReadTimeout;
    }

    public int getRetry() {
        return retry;
    }

    public void setRetry(int retry) {
        this.retry = retry;
    }

    public int getCurrentService() {
        return currentService;
    }

    public void setCurrentService(int currentService) {
        this.currentService = currentService;
    }

    public abstract T getService(HessianProxyFactory factory, String url);

    /**
     * 将指定url的服务，实例化成对象，加载到服务中
     *
     * @param urls 远程调用服务的url
     */
    public void setHessianUrls(String urls) {
        String[] urlArray = urls.split(",");
        if (urlArray == null || urlArray.length == 0) {
            throw new IllegalArgumentException("unknow SearchHessianUrls:" + urls);
        }
        services = new ArrayList<T>();
        for (String url : urlArray) {
            if (StringUtils.isNotBlank(url)) {
                HessianProxyFactory factory = new HessianProxyFactory();
                if (this.hessianConnectTimeout != -1) {
                    factory.setConnectTimeout(hessianConnectTimeout);
                }
                if (this.hessianReadTimeout != -1) {
                    factory.setReadTimeout(hessianReadTimeout);
                }
                T service = getService(factory, StringUtils.trim(url));
                services.add(service);
            }
        }
        services = Collections.unmodifiableList(services);
    }

    /**
     * 获取服务列表中的服务实例
     *
     * @return 服务对象
     */
    protected T getNextService() {
        int next = currentService++;
        if (next >= this.services.size()) {
            next = 0;
            currentService = 0;
        }
        return this.services.get(next);
    }
}
