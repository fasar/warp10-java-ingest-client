package com.github.fasar.wijc.core;

import lombok.Value;

@Value
public class Warp10TSConfiguration {

    public static Warp10TSConfiguration defaultConfiguration = new Warp10TSConfiguration(EngineTimeUnit.MICROSECONDS);
    EngineTimeUnit timeUnit;

    public enum EngineTimeUnit {
        SECONDS, MILLISECONDS, MICROSECONDS, NANOSECONDS
    }

}
