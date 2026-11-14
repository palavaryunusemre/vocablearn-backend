package com.vocablearn;

import com.vocablearn.security.JwtProperties;
import org.flywaydb.core.Flyway;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class)
public class VocabLearnApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(VocabLearnApplication.class);
		app.addListeners(migrateOnEnvironmentPrepared());
		app.run(args);
	}

	/**
	 * Spring Boot 4.1 dropped the bundled Flyway auto-configuration, so
	 * migrations are run here against the fully-resolved Environment
	 * (profiles/overrides included) before the ApplicationContext -- and
	 * Hibernate's schema validation -- is created.
	 */
	private static ApplicationListener<ApplicationEnvironmentPreparedEvent> migrateOnEnvironmentPrepared() {
		return event -> {
			ConfigurableEnvironment env = event.getEnvironment();
			if (!env.getProperty("spring.flyway.enabled", Boolean.class, true)) {
				return;
			}
			Flyway.configure()
					.dataSource(
							env.getRequiredProperty("spring.datasource.url"),
							env.getRequiredProperty("spring.datasource.username"),
							env.getRequiredProperty("spring.datasource.password"))
					.locations("classpath:db/migration")
					.baselineOnMigrate(true)
					.load()
					.migrate();
		};
	}

}
