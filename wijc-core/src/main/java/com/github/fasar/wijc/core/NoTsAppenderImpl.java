package com.github.fasar.wijc.core;

public class NoTsAppenderImpl implements TsAppender {
    @Override
    public <T> void appendObject(TsIdentifier id, long ts, T message) {
        // Do nothing
    }

    @Override
    public void appendString(TsIdentifier id, long ts, String message) {
        // Do nothing
    }

    @Override
    public void append(TsIdentifier id, long ts, double value) {
        // Do nothing
    }

    @Override
    public void append(TsIdentifier id, long[] ts, double[] value) {
        // Do nothing
    }

    @Override
    public void appendMultivariate(TsIdentifier id, long ts, MultivariateAwareEntity object) {
        // Do nothing
    }
}
