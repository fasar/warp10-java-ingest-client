package com.github.fasar.wijc.core;

import com.github.fasar.wijc.core.error.ServiceException;

public class NoTsAppenderImpl implements TsAppender {
    @Override
    public <T> void appendObject(TsIdentifier id, long ts, T message) throws ServiceException {
        // Do nothing
    }

    @Override
    public void appendString(TsIdentifier id, long ts, String message) throws ServiceException {
        // Do nothing
    }

    @Override
    public void append(TsIdentifier id, long ts, double value) throws ServiceException {
        // Do nothing
    }

    @Override
    public void append(TsIdentifier id, long[] ts, double[] value) throws ServiceException {
        // Do nothing
    }

    @Override
    public void appendMultivariate(TsIdentifier id, long ts, MultivariateAwareEntity object) throws ServiceException {
        // Do nothing
    }
}
