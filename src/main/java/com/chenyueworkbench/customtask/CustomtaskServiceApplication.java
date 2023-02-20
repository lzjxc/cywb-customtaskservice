package com.chenyueworkbench.customtask;

import java.util.TimeZone;
import javax.annotation.PostConstruct;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;

@SpringBootApplication
@RefreshScope
@EnableBinding(Source.class)
public class CustomtaskServiceApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(CustomtaskServiceApplication.class, args);
	}

	@PostConstruct
	public void init(){
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
	}

}
