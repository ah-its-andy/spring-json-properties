package io.standardcore.environment;

public class EnvironmentReaderSelector {
    public static EnvironmentReader select(String scheme){
        switch (scheme){
            case "file": return new FileEnvironmentReader();
            case "embeded": return new EmbededEnvironmentReader();
            default: return null;
        }
    }
}
