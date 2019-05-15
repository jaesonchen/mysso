package com.asiainfo.mysso.sso;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * TODO
 * 
 * @author       zq
 * @date         2017年11月12日  下午4:32:48
 * Copyright: 	  北京亚信智慧数据科技有限公司
 */
@SpringBootApplication
@ComponentScan("com.asiainfo.mysso.sso.login")
public class SsoApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(new Object[] { SsoApplication.class });
        app.setAdditionalProfiles(new String[] { "server" });
        app.run(args);
    }
}
