package io.standardcore.environment;

import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;

public class EmbededEnvironmentReader implements EnvironmentReader {
    @Override
    public InputStream read(Environment environment, String uri) throws IOException {
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        if(StringUtils.isEmpty(uri)) return classloader.getResourceAsStream("appsettings.json");
        else return classloader.getResourceAsStream(uri);
    }
}
