package com.asiainfo.mysso.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * TODO
 * 
 * @author       zq
 * @date         2017年11月12日  下午4:35:06
 * Copyright: 	  北京亚信智慧数据科技有限公司
 */
@SpringBootApplication
@ComponentScan("com.asiainfo.mysso.client")
@ServletComponentScan
public class ClientApplication {

    /** 
     * TODO
     * 
     * @param args
     */
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(new Object[] {ClientApplication.class});
        app.setAdditionalProfiles(new String[] {"client"});
        app.run(args);
    }
}
