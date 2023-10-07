package com.zelazobeton.cognitiveexercises;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
//@ComponentScan(basePackages = {"com.zelazobeton"})
@EntityScan(basePackages = {"com.zelazobeton.cognitiveexercises"})
@EnableJpaRepositories(basePackages = {"com.zelazobeton.cognitiveexercises"})
@ImportResource("classpath:caching.xml")
public class CognitiveExercisesBasicService {

	public static void main(String[] args) {
		SpringApplication.run(CognitiveExercisesBasicService.class, args);
	}
}
