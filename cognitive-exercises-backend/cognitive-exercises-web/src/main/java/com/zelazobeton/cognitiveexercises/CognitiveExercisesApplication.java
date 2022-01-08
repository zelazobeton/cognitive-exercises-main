package com.zelazobeton.cognitiveexercises;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"com.zelazobeton"})
@EntityScan(basePackages = {"com.zelazobeton.cognitiveexercises.domain"})
@EnableJpaRepositories(basePackages = {"com.zelazobeton.cognitiveexercises.repository"})
public class CognitiveExercisesApplication {

	public static void main(String[] args) {
		SpringApplication.run(CognitiveExercisesApplication.class, args);
	}
}
