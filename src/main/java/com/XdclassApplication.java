package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.WebApplicationInitializer;

@SpringBootApplication
@ServletComponentScan
@EnableElasticsearchRepositories(basePackages = "com.noon.shop.es")
@EnableJpaRepositories(basePackages = {"com.noon.shop.pojo","com.noon.shop.dao"})
public class XdclassApplication extends SpringBootServletInitializer implements WebApplicationInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(XdclassApplication.class);
	}

	public static void main(String[] args) throws Exception {

		System.setProperty("es.set.netty.runtime.available.processors", "false");
		SpringApplication.run(XdclassApplication.class, args);
	}


}
