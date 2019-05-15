package com.asiainfo.mysso.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * TODO
 * 
 * @author chenzq  
 * @date 2019年2月4日 下午12:07:47
 * @version V1.0
 */
@SpringBootApplication
@ServletComponentScan("com.asiainfo.mysso.client.filter")
public class AnotherClientApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(new Object[] { AnotherClientApplication.class });
        app.setAdditionalProfiles(new String[] { "clientsso" });
        app.run(args);
    }
}
