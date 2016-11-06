package com.example;

import org.springframework.boot.context.embedded.ReactiveWebApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.ViewResolverRegistry;
import org.springframework.web.reactive.config.WebReactiveConfigurer;
import org.springframework.web.reactive.result.view.freemarker.FreeMarkerConfigurer;

/**
 * @author Brian Clozel
 */
@Configuration
public class WebConfig implements WebReactiveConfigurer {

	@Bean
	public FreeMarkerConfigurer freeMarkerConfigurer(ReactiveWebApplicationContext applicationContext) {
		FreeMarkerConfigurer configurer = new FreeMarkerConfigurer();
		configurer.setTemplateLoaderPath("classpath:/templates/");
		configurer.setResourceLoader(applicationContext);
		return configurer;
	}

	@Override
	public void configureViewResolvers(ViewResolverRegistry registry) {
		registry.freeMarker();
	}
}
