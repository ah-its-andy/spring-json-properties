package io.standardcore.environment;

import org.springframework.boot.SpringApplication;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.InputStreamResource;

import java.io.InputStream;

public class JsonConfigurationMerger {
    public final static void merge(String name, InputStream inputStream, ConfigurableEnvironment environment, SpringApplication application) {
        try {
            InputStreamResource inputStreamResource = new InputStreamResource(inputStream);
            JsonPropertySourceLoader jsonPropertySourceLoader = new JsonPropertySourceLoader();
            PropertySource<?> propertySource = jsonPropertySourceLoader.load(name, inputStreamResource, StandardEnvironment.DEFAULT_PROFILES_PROPERTY_NAME);
            environment.getPropertySources().addFirst(propertySource);
//            ConfigurableEnvironment configurableEnvironment = new CustomizeEnvironment(Arrays.asList(propertySource));
//            environment.merge(configurableEnvironment);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
