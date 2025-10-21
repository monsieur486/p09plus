package com.mr486.msrisque.configuration;

import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignRetryConfiguration {

  @Bean
  public Retryer feignRetryer() {
    // period (ms), maxPeriod (ms), maxAttempts
    // => jusqu'à 3 tentatives avec backoff entre 100ms et 1s
    return new Retryer.Default(100, 1000, 3);
  }
}

