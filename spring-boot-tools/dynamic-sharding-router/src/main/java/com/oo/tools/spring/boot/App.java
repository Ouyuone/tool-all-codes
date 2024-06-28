package com.oo.tools.spring.boot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author oo
 */
@SpringBootApplication
public class App implements CommandLineRunner
{
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    @Value("${server.port}")
    private  Long port;

    @Value("${spring.profiles.active}")
    private  String profile;

    @Value("${spring.application.name}")
    private String applicationName;

    public static void main( String[] args )
    {
        SpringApplication.run(App.class, args);
        logger.info("Application started");
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("{} running on port {{}} with profile {{}}",applicationName, port, profile);
    }

}
