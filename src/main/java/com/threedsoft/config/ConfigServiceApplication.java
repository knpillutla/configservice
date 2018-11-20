package com.threedsoft.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.config.server.EnableConfigServer;
import org.springframework.cloud.config.server.environment.CompositeEnvironmentRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.threedsoft.config.service.ConfigServiceImpl;

@EnableConfigServer
@SpringBootApplication
@ConditionalOnMissingBean(CompositeEnvironmentRepository.class)
@EnableAutoConfiguration
@EnableScheduling
@EnableJpaAuditing
@EntityScan(
        basePackageClasses = {ConfigServiceApplication.class, Jsr310JpaConverters.class}
)
public class ConfigServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigServiceApplication.class, args);
    }

	@Bean
	public ConfigServiceImpl dbEnvironmentRepository() {
		ConfigServiceImpl dbEnvironmentRepository = new ConfigServiceImpl();
		return dbEnvironmentRepository;
	}
//	@Bean
//	@ConditionalOnProperty(value="spring.cloud.config.server.monitor.gogs.enabled", havingValue="true", matchIfMissing=true)
/*	public CompositePropertyPathNotificationExtractor compositePropertyPathNotificationExtractor() {
		GithubPropertyPathNotificationExtractor gitExtractor = new GithubPropertyPathNotificationExtractor();
		
		return new CompositePropertyPathNotificationExtractor(null);
	}
*/   
}