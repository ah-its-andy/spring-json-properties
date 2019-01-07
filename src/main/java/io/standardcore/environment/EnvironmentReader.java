package io.standardcore.environment;

import org.springframework.core.env.Environment;

import java.io.IOException;
import java.io.InputStream;

public interface EnvironmentReader {
    InputStream read(Environment environment, String uri) throws IOException;
}
