package org.nhindirect.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;


@SpringBootApplication
@EnableR2dbcRepositories("org.nhindirect.config.repository")
public class TestApplication
{
    public static void main(String[] args) 
    {
        SpringApplication.run(TestApplication.class, args);
    }  
}
