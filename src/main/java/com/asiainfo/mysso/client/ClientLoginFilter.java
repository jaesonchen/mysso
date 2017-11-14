package com.asiainfo.mysso.client;

import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.asiainfo.mysso.sso.filter.AbstractSsoFilter;

/**
 * TODO
 * 
 * @author       zq
 * @date         2017年11月13日  下午2:45:52
 * Copyright: 	  北京亚信智慧数据科技有限公司
 */
@WebFilter(
        filterName = "loginFilter", 
        urlPatterns = "/*",
        initParams = {
                @WebInitParam(name = "skip-url", value = "/client/index.jsp"),
                @WebInitParam(name = "skip-suffix", value = ".js,.gif,.jpg,.bmp,.png,.css,.ico"),
                @WebInitParam(name = "sso-login-server", value = "http://localhost:8080/")})
public class ClientLoginFilter extends AbstractSsoFilter {

    public static final String SESSION_NAME = "session_userid";
    
    /* 
     * TODO
     * @param request
     * @return
     * @see com.asiainfo.mysso.sso.filter.AbstractSsoFilter#isLogin(javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected boolean isLogin(HttpServletRequest request) {
        
        HttpSession session = request.getSession(false);
        if (null == session) {
            return false;
        }
        return null != session.getAttribute(SESSION_NAME);
    }

    /* 
     * TODO
     * @param userId
     * @param request
     * @param response
     * @see com.asiainfo.mysso.sso.filter.AbstractSsoFilter#doLogin(java.lang.String, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    protected void doLogin(String userId, HttpServletRequest request, HttpServletResponse response) {
        request.getSession().setAttribute(SESSION_NAME, userId);
    }
}
