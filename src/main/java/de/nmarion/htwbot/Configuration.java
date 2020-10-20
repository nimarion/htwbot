package de.nmarion.htwbot;

import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

public class Configuration {

    public static final String DISCORD_TOKEN;
    public static final String DISCORD_PREFIX;

    static {
        final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        DISCORD_TOKEN = getenv("DISCORD_TOKEN", dotenv);
        DISCORD_PREFIX = getenv("DISCORD_PREFIX", dotenv);

        try {
            checkNull();
            LoggerFactory.getLogger(Configuration.class).info("Configuration loaded!");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static String getenv(final String name, final Dotenv dotenv) {
        if (System.getenv(name) != null) {
            return System.getenv(name);
        } else if (dotenv.get(name) != null) {
            return dotenv.get(name);
        }
        return null;
    }

    private static void checkNull() throws IllegalAccessException {
        for (Field f : Configuration.class.getDeclaredFields()) {
            LoggerFactory.getLogger(Configuration.class).debug(f.getName() + " environment variable "
                    + (f.get(Configuration.class) == null ? "is null" : "has been loaded"));
        }
    }

}