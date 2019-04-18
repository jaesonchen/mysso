package com.asiainfo.mysso.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;

/**
 * 
 * @Description: TODO
 * @author chenzq  
 * @date 2019年2月4日 下午12:07:47
 * @version V1.0
 */
@SpringBootApplication
@ComponentScan("com.asiainfo.mysso.client.filter")
@ServletComponentScan
public class ClientApplicationSso {

    public static void main(String[] args) {

        SpringApplication app = new SpringApplication(new Object[] {ClientApplication.class});
        app.setAdditionalProfiles(new String[] {"clientsso"});
        app.run(args);
    }
}
