package io.standardcore.environment;

import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;

import java.util.List;

public class CustomizeEnvironment extends StandardEnvironment {
    private final List<PropertySource<?>> propertySources;

    public CustomizeEnvironment(List<PropertySource<?>> propertySources) {
        this.propertySources = propertySources;
    }

    @Override
    protected void customizePropertySources(MutablePropertySources propertySources) {
        for(PropertySource<?> propertySource : propertySources){
            propertySources.addLast(propertySource);
        }
    }
}
