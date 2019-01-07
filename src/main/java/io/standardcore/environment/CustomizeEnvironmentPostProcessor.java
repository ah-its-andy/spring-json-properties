package io.standardcore.environment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CustomizeEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application)  {
        System.out.println("CustomizeEnvironmentPostProcessor has been called.");

        try{
            loadEmbeded(environment, application);
        } catch (IOException e){
            throw new RuntimeException(e);
        }

        String scheme = environment.getProperty(DistributedNameConst.ENV_DISTRIBUTED_CONFIGURATION_SCHEME);
        String uri = environment.getProperty(DistributedNameConst.ENV_DISTRIBUTED_CONFIGURATION_URI);
        if(StringUtils.isEmpty(uri)) uri = "appsettings.json";

        try{
            if(!StringUtils.isEmpty(scheme)) {
                EnvironmentReader environmentReader = EnvironmentReaderSelector.select(scheme);
                if(environmentReader == null) return;
                System.out.println("Loading " + scheme + "  " + uri + " with " + environmentReader.getClass().getCanonicalName() + " .");
                InputStream inputStream = environmentReader.read(environment, uri);
                JsonConfigurationMerger.merge("distribute_appsettings", inputStream, environment, application);
            }
            loadSystemEnv(environment, application);
        } catch (IOException e){
            throw new RuntimeException(e);
        }

        System.out.println("Customize environment has been load.");
    }

    private void loadEmbeded(ConfigurableEnvironment environment, SpringApplication application) throws IOException {
        EnvironmentReader environmentReader = EnvironmentReaderSelector.select("embeded");
        System.out.println("Loading embeded  appsettings.json with " + environmentReader.getClass().getCanonicalName() + " .");
        InputStream inputStream = environmentReader.read(environment, "appsettings.json");;
        JsonConfigurationMerger.merge("appsettings", inputStream, environment, application);

        String env = getStandardCoreEnvironmentValue(environment, application);

        String envFileName = "appsettings." + env + ".json";
        InputStream envStream = environmentReader.read(environment, envFileName);
        if(envStream != null) {
            System.out.println("Loading embeded  " + envFileName +" with " + environmentReader.getClass().getCanonicalName() + " .");
            JsonConfigurationMerger.merge("appsettings_" + env, envStream, environment, application);
        }
    }

    private String getStandardCoreEnvironmentValue(ConfigurableEnvironment environment, SpringApplication application){
        String value = (String) environment.getSystemEnvironment().getOrDefault(DistributedNameConst.ENV_STANDARDCORE_ENVIRONMENT_NAME, "development");
        if(StringUtils.isEmpty(value)) return "development";
        return value.trim().toLowerCase();
    }

    private void loadSystemEnv(ConfigurableEnvironment environment, SpringApplication application){
        Map<String, Object> properties = new HashMap<>();
        List<Map.Entry<String, Object>> variables = environment.getSystemEnvironment().entrySet()
                .stream().filter(x -> x.getKey().startsWith("FNARG"))
                .collect(Collectors.toList());
        if(variables.isEmpty()) return;

        System.out.println("Merging " + String.valueOf(variables.size()) + " from System Environment Variables");

        for(Map.Entry<String, Object> entry : variables){
            String key = entry.getKey().substring(5, entry.getKey().length()).replace("_", ".");
            properties.put(key, entry.getValue());
        }

        PropertySource<?> propertySource = new MapPropertySource("SystemEnvMergeAppsettings", properties);
        environment.getPropertySources().addFirst(propertySource);
    }


    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
