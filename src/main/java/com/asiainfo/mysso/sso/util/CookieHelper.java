package com.asiainfo.mysso.sso.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;


/**
 * Cookie工具类 
 * 
 * @author       zq
 * @date         2017年11月13日  上午10:35:47
 * Copyright: 	  北京亚信智慧数据科技有限公司
 */
public class CookieHelper {
    
	final static Logger logger = LoggerFactory.getLogger(CookieHelper.class);
	
	/**浏览器关闭时自动删除*/
	public final static int CLEAR_BROWSER_IS_CLOSED = -1;
	/**立即删除*/
	public final static int CLEAR_IMMEDIATELY = 0;
	/**默认的路径*/
	public final static String DEFAULT_COOKIE_PATH = "/";

	/**
	 * 根据cookieName获取Cookie值
	 * 
	 * @param request
	 * @param cookieName
	 * @return
	 */
	public static String findCookieValueByName(HttpServletRequest request, String cookieName) {
	    
	    Cookie cookie = findCookieByName(request, cookieName);
	    return null == cookie ? null : cookie.getValue();
	}
	
	/**
	 * 根据cookieName获取Cookie
	 * 
	 * @param request
	 * @param cookieName
	 * @return
	 */
	public static Cookie findCookieByName(HttpServletRequest request, String cookieName) {
	    
		Cookie[] cookies = request.getCookies();
		if (cookies == null) {
			return null;
		}
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(cookieName)) {
				return cookie;
			}
		}
		return null;
	}

	/**
	 * 根据 cookieName 清空 Cookie(默认域下)
	 * 
	 * @param response
	 * @param cookieName
	 */
	public static void clearCookieByName(HttpServletResponse response, String cookieName) {
		Cookie cookie = new Cookie(cookieName, "");
		cookie.setMaxAge(CLEAR_IMMEDIATELY);
		response.addCookie(cookie);
	}
	
	/**
	 * 清除指定doamin的所有Cookie
	 * 
	 * @param request
	 * @param response
	 * @param domain
	 * @param path
	 */
	public static void clearAllCookie(HttpServletRequest request, HttpServletResponse response, String domain, String path) {
		
	    Cookie[] cookies = request.getCookies();
		if (null == cookies) {
		    return;
		}
		for (Cookie cookie : cookies) {
			clearCookie(response, cookie.getName(), domain, path);
		}
	}

	/**
	 * 根据cookieName清除指定Cookie
	 * 
	 * @param request
	 * @param response
	 * @param cookieName
	 * @param domain
	 * @param path
	 * @return
	 */
	public static boolean clearCookieByName(HttpServletRequest request, HttpServletResponse response, 
	        String cookieName, String domain, String path) {
	    
		boolean result = false;
		Cookie cookie = findCookieByName(request, cookieName);
		if (cookie != null) {
			result = clearCookie(response, cookieName, domain, path);
		}
		return result;
	}

	/**
	 * 清除指定Cookie 等同于 clearCookieByName(...)
     * 该方法不判断Cookie是否存在,因此不对外暴露防止Cookie不存在异常.
	 * 
	 * @param response
	 * @param cookieName
	 * @param domain
	 * @param path
	 * @return
	 */
	protected static boolean clearCookie(HttpServletResponse response, String cookieName, String domain, String path) {
	    
		boolean result = false;
		try {
			Cookie cookie = new Cookie(cookieName, "");
			cookie.setMaxAge(CLEAR_IMMEDIATELY);
			cookie.setDomain(domain);
			cookie.setPath(path);
			response.addCookie(cookie);
			result = true;
		} catch (Exception e) {
			logger.error("error on clear cookie" + cookieName + "!", e);
		}
		return result;
	}

	/**
	 * 当前域下添加 Cookie, 关闭浏览器失效
	 * 
	 * @param response
	 * @param name
	 * @param value
	 */
	public static void addCookie(HttpServletResponse response, String name, String value) {
		addCookie(response, null, name, value);
	}

	/**
	 * 添加 Cookie, 关闭浏览器失效
	 * 
	 * @param response
	 * @param domain
	 * @param name
	 * @param value
	 */
	public static void addCookie(HttpServletResponse response, String domain, String name, String value) {
		addCookie(response, domain, DEFAULT_COOKIE_PATH, name, value, CLEAR_BROWSER_IS_CLOSED, false, false);
	}

	/**
	 * 添加 Cookie
	 * 
	 * @param response
	 * @param domain
	 * @param path
	 * @param name
	 * @param value
	 * @param maxAge
	 * @param httpOnly
	 * @param secured
	 */
	public static void addCookie(HttpServletResponse response, String domain, String path, String name, 
	        String value, int maxAge, boolean httpOnly, boolean secured) {
		Cookie cookie = new Cookie(name, value);
		//不设置该参数默认当前所在域
		if (!StringUtils.isEmpty(domain)) {
			cookie.setDomain(domain);
		}
		cookie.setPath(path);
		//cookie生命周期，0=立即删除、小于0=关闭浏览器删除、大于0=失效时间（单位秒）
		cookie.setMaxAge(maxAge);
		//Cookie 只在Https协议下传输
		cookie.setSecure(secured);
		//Cookie 只读设置
		if (httpOnly) {
			addHttpOnlyCookie(response, cookie);
		} else {
			response.addCookie(cookie);
		}
	}

	/**
	 * 解决 servlet 3.0 以下版本不支持 HttpOnly
	 * 
	 * @param response
	 * @param cookie
	 */
	public static void addHttpOnlyCookie(HttpServletResponse response, Cookie cookie) {
	    
		//依次取得cookie中的名称、值、最大生存时间、路径、域和是否为安全协议信息 
		String cookieName = cookie.getName();
		String cookieValue = cookie.getValue();
		int maxAge = cookie.getMaxAge();
		String path = cookie.getPath();
		String domain = cookie.getDomain();
		boolean isSecure = cookie.getSecure();
		StringBuilder sb = new StringBuilder();
		
		sb.append(cookieName + "=" + cookieValue + ";");
		if (maxAge >= 0) {
		    sb.append("Max-Age=" + cookie.getMaxAge() + ";");
		}
		if (!StringUtils.isEmpty(domain)) {
		    sb.append("domain=" + domain + ";");
		}
		if (!StringUtils.isEmpty(path)) {
			sb.append("path=" + path + ";");
		}
		if (isSecure) {
			sb.append("secure;HTTPOnly;");
		} else {
			sb.append("HTTPOnly;");
		}
		response.addHeader("Set-Cookie", sb.toString());
	}
}
