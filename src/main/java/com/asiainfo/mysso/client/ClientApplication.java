package com.asiainfo.mysso.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

/**
 * TODO
 * 
 * @author chenzq  
 * @date 2019年5月12日 下午12:14:16
 * @version V1.0
 * @Copyright: Copyright(c) 2019 jaesonchen.com Inc. All rights reserved.
 */
@SpringBootApplication
@ServletComponentScan("com.asiainfo.mysso.client.filter")
public class ClientApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(new Object[] { ClientApplication.class });
        app.setAdditionalProfiles(new String[] { "client" });
        app.run(args);
    }
}
