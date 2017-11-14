package com.asiainfo.mysso.sso.model;

import java.io.Serializable;

/**
 * TODO
 * 
 * @author       zq
 * @date         2017年11月12日  下午4:55:03
 * Copyright: 	  北京亚信智慧数据科技有限公司
 */
public class LoginUser implements Serializable {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    private String userName;
    private String password;
    private String callback;
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getCallback() {
        return callback;
    }
    public void setCallback(String callback) {
        this.callback = callback;
    }
    @Override
    public String toString() {
        return "LoginUser [userName=" + userName + ", password=" + password + ", callback=" + callback + "]";
    }
}
