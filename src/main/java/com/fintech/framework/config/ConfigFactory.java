package com.fintech.framework.config;

import org.aeonbits.owner.ConfigCache;

public final class ConfigFactory {

    private ConfigFactory() {
        // Prevent instantiation
    }

    public static FrameworkConfig getConfig() {
        return ConfigCache.getOrCreate(FrameworkConfig.class);
    }
}
