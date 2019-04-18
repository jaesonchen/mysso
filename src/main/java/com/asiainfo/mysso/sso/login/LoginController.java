package com.asiainfo.mysso.sso.login;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.asiainfo.mysso.sso.model.LoginUser;
import com.asiainfo.mysso.sso.util.CookieHelper;

/**
 * TODO
 * 
 * @author       zq
 * @date         2017年11月12日  下午4:48:40
 * Copyright: 	  北京亚信智慧数据科技有限公司
 */
@Controller
public class LoginController {

    final Logger logger = LoggerFactory.getLogger(getClass());
    public static final String SSO_TICKET_COOKIE_NAME = "SSO_TICKET";
    static final Map<String, String> loginCache = new HashMap<>();
    static final Map<String, List<String>> loginHosts = new HashMap<>();
    
    /**
     * sso登陆
     * 
     * @param user
     * @param model
     * @param response
     * @return
     */
    @RequestMapping("/sso/login")
    public String ssoLogin(LoginUser user, Map<String, Object> model, HttpServletResponse response) {
        
        logger.info("sso login action, user={}", user);
        //do login
        if (!StringUtils.isEmpty(user.getUserName())) {
            //generate ticket
            String ticket = user.getUserName() + System.currentTimeMillis();
            loginCache.put(ticket, user.getUserName());
            //add loginhosts
            this.addLoginHosts(user.getUserName(), user.getCallback());
            //add cookie
            CookieHelper.addCookie(response, SSO_TICKET_COOKIE_NAME, ticket);
            //redirect callback
            String callback = user.getCallback();
            if (!StringUtils.isEmpty(callback)) {
                StringBuilder url = new StringBuilder();
                url.append(callback);
                if (callback.indexOf("?") >= 0) {
                    url.append("&");
                } else {
                    url.append("?");
                }
                url.append("sso-ticket=").append(ticket);
                //redirect to client
                try {
                    response.sendRedirect(url.toString());
                } catch (IOException e) {
                    logger.error("error on redirect to {}, error message:\n{}", url.toString(), e);
                }
            } else {
                //forward to sso welcome
                model.put("userName", user.getUserName());
                return "/sso/welcome";
            }
        } else {
            //forward to sso login.jsp
            model.put("callback", user.getCallback());
            return "/sso/login";
        }
        return null;
    }
    
    /**
     * client登陆请求
     * 
     * @param callback
     * @param model
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/login")
    public String login(@RequestParam(name="callback", required=false) String callback, Map<String, Object> model, 
            HttpServletRequest request, HttpServletResponse response) {
        
        logger.info("client login, callback={}", callback);
        //读取cookie
        String ssoTicket = CookieHelper.findCookieValueByName(request, SSO_TICKET_COOKIE_NAME);
        model.put("callback", callback);
        //未登陆过，跳转到login.jsp页面
        if (StringUtils.isEmpty(ssoTicket)) {
            //forward to sso login.jsp
            return "/sso/login";
        //判断登陆ticket是否有效
        } else {
            if (loginCache.containsKey(ssoTicket)) {
                //redirect callback
                if (!StringUtils.isEmpty(callback)) {
                    //add loginhosts
                    this.addLoginHosts(loginCache.get(ssoTicket), callback);
                    StringBuilder url = new StringBuilder();
                    url.append(callback);
                    if (callback.indexOf("?") >= 0) {
                        url.append("&");
                    } else {
                        url.append("?");
                    }
                    url.append("sso-ticket=").append(ssoTicket);
                    //redirect to client
                    try {
                        response.sendRedirect(url.toString());
                    } catch (IOException e) {
                        logger.error("error on redirect to {}, error message:\n{}", url.toString(), e);
                    }
                } else {
                    //forward to sso welcome
                    return "/sso/welcome";
                }
            } else {
                //forward to sso login.jsp
                return "/sso/login";
            }
        }
        return null;
    }
    
    /**
     * client 登出
     * 
     * @param userName
     * @param model
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/logout/{userName}")
    public String logout(@PathVariable(name="userName") String userName, Map<String, Object> model, 
            HttpServletRequest request, HttpServletResponse response) {
        
        logger.info("sso logout, userName={}", userName);
        //sso logout
        for (Map.Entry<String, String> entry : loginCache.entrySet()) {
            if (entry.getValue().equals(userName)) {
                loginCache.remove(entry.getKey());
            }
        }
        
        List<String> host = loginHosts.get(userName);
        logger.info("sso logout, hosts={}", host);
        request.setAttribute("logout-host", host);
        //forward to sso logout
        return "/sso/logout";
    }
    
    /**
     * 校验ticket是否已登陆
     * 
     * @param ticket
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/ticket/{ticket}")
    public String ticket(@PathVariable("ticket") String ticket) {
        
        if (loginCache.containsKey(ticket)) {
            return loginCache.get(ticket);
        }
        return "";
    }
    
    /**
     * 保存登陆系统ip
     * 
     * @param userName
     * @param callback
     */
    protected void addLoginHosts(String userName, String callback) {
        
        logger.info("addLoginHosts: userName={}, callback={}", userName, callback);
        String pattern = "^((http://)|(https://))?((localhost)|([0-9]{1,3}\\.){3}[0-9]{1,3}).[0-9]{2,5}(/)";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(callback);
        if (m.find()) {
            if (!loginHosts.containsKey(userName)) {
                loginHosts.put(userName, new ArrayList<String>());
            }
            List<String> hosts = loginHosts.get(userName);
            String host = m.group();
            if (!hosts.contains(host)) {
                logger.info("add host: {}", host);
                hosts.add(host);
            }
        }
    }
    
    public static void main(String[] args) {

        Logger logger = LoggerFactory.getLogger(LoginController.class);
        String pattern = "^((http://)|(https://))?((localhost)|([0-9]{1,3}\\.){3}[0-9]{1,3}).[0-9]{2,5}(/)";
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher("http://localhost:8090/client/welcome.jsp");
        if (m.find()) {
            String host = m.group();
            logger.info("add host: {}", host);
        }
    }
}
