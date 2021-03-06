package com.asiainfo.mysso.sso.filter;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

/**
 * TODO
 * 
 * @author       zq
 * @date         2017年11月13日  上午9:16:42
 * Copyright: 	  北京亚信智慧数据科技有限公司
 */
public abstract class AbstractSsoFilter implements Filter {

    final Logger logger = LoggerFactory.getLogger(getClass());
    private List<String> skipUrls = new ArrayList<>();
    private List<String> skipSuffixs = new ArrayList<>();
    private String ssoLoginUrl = "";
    private String ssoTicketUrl = "";
    private RestTemplate template = new RestTemplate();

    @Override
    public void init(FilterConfig config) throws ServletException {
        
        String skipUrl = config.getInitParameter("skip-url");
        if (!StringUtils.isEmpty(skipUrl)) {
            for (String url : skipUrl.split(",")) {
                skipUrls.add(url.trim());
            }
        }
        String skipSuffix = config.getInitParameter("skip-suffix");
        if (!StringUtils.isEmpty(skipSuffix)) {
            for (String suffix : skipSuffix.split(",")) {
                skipSuffixs.add(suffix.trim());
            }
        }
        String ssoLoginServer = config.getInitParameter("sso-login-server");
        Assert.notNull(ssoLoginServer, "sso login server can not be null!");
        ssoLoginServer += ssoLoginServer.endsWith("/") ? "" : "/";
        this.ssoLoginUrl = ssoLoginServer + "login";
        this.ssoTicketUrl = ssoLoginServer + "ticket";
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        //是否忽略请求
        if (this.isSkipRequest(req)) {
            logger.info("{} is skipped!", req.getRequestURI());
            chain.doFilter(request, response);
            return;
        }
        //是否登陆(session中含有本地化的登陆信息)
        if (this.isLogin(req)) {
            logger.info("user has login in!");
            chain.doFilter(request, response);
            return;
        }
        //是否sso返回ticket
        String ticket = req.getParameter("sso-ticket");
        if (!StringUtils.isEmpty(ticket)) {
            //调用sso restful接口验证ticket
            logger.info("ticket from sso server: ticket={}", ticket);
            String userId = template.getForObject(this.ssoTicketUrl + "/{ticket}", String.class, ticket);
            Assert.notNull(userId, "userId is null!");
            logger.info("local system login userId={}", userId);
            this.doLogin(userId, req, res);
            chain.doFilter(request, response);
            return;
        }
        //redirect to sso login
        StringBuilder loginUrl = new StringBuilder();
        loginUrl.append(this.ssoLoginUrl);
        loginUrl.append("?callback=").append(URLEncoder.encode(req.getRequestURL().toString(), "UTF-8"));
        logger.info("redirect to sso server: url={}", loginUrl.toString());
        res.sendRedirect(loginUrl.toString());
    }
    
    @Override
    public void destroy() {

    }
    
    /**
     * request请求对应的session是否已在本地系统登陆（通常是指在本地容器session中是否存在某个属性）
     *
     * @param request
     * @return
     */
    protected abstract boolean isLogin(HttpServletRequest request);

    /**
     * 使用sso返回的userId在本地系统中进行登陆动作，以便下次访问时判断是否已本地登陆（通常是在本地容器session中设置某个属性，保存userId或者相关的权限对象）
     *
     * @param userId
     * @param request
     * @param response
     */
    protected abstract void doLogin(String userId, HttpServletRequest request, HttpServletResponse response);
    
    /**
     * 是否忽略请求
     * 
     * @param request
     * @return
     */
    protected boolean isSkipRequest(HttpServletRequest request) {
        
        String uri = request.getRequestURI();
        for (String url : this.skipUrls) {
            if (uri.startsWith(url)) {
                return true;
            }
        }
        for (String suffix : this.skipSuffixs) {
            if (uri.endsWith(suffix)) {
                return true;
            }
        }
        return false;
    }
}
