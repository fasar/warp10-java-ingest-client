package com.github.fasar.wijc.core;

import com.github.fasar.wijc.core.error.ServiceException;

public interface TsAppender {
    <T> void appendObject(TsIdentifier id, long ts, T message) throws ServiceException;

    void appendString(TsIdentifier id, long ts, String message) throws ServiceException;

    void append(TsIdentifier id, long ts, double value) throws ServiceException;

    void append(TsIdentifier id, long[] ts, double[] value) throws ServiceException;

    void appendMultivariate(TsIdentifier id, long ts, MultivariateAwareEntity object) throws ServiceException;

}
