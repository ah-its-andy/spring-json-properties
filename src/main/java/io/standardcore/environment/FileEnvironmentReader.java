package io.standardcore.environment;

import org.springframework.core.env.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileEnvironmentReader implements EnvironmentReader {
    @Override
    public InputStream read(Environment environment, String uri) throws IOException {
        File file = new File(uri);
        if(!file.exists()) return null;
        InputStream input = new FileInputStream(file);
        return input;
    }
}
