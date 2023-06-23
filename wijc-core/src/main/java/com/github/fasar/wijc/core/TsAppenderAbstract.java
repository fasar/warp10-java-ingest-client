package com.github.fasar.wijc.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fasar.wijc.core.error.ServiceException;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.function.Function;

public abstract class TsAppenderAbstract implements TsAppender {

    private final ObjectMapper mapper;
    private final Function<Long, String> mapTsToWarp10Ts;

    public TsAppenderAbstract(Warp10TSConfiguration configuration, ObjectMapper mapper) {
        this.mapper = mapper;
        switch (configuration.getTimeUnit()) {
            case SECONDS:
                mapTsToWarp10Ts = (ts) -> Long.toString(ts / 1000);
                break;
            case MILLISECONDS:
                mapTsToWarp10Ts = (ts) -> Long.toString(ts);
                break;
            case MICROSECONDS:
                mapTsToWarp10Ts = (ts) -> Long.toString(ts * 1_000);
                break;
            case NANOSECONDS:
                mapTsToWarp10Ts = (ts) -> Long.toString(ts * 1_000_000);
                break;
            default:
                throw new RuntimeException("Unknown TS format: " + configuration.getTimeUnit());
        }
    }

    private static void mapIdToWarp10(TsIdentifier id, StringBuilder sb) {
        boolean isFirst = true;
        for (Map.Entry<String, String> entry : id.getLabels().entrySet()) {
            if (!isFirst)
                sb.append(",");
            else
                isFirst = false;
            sb.append(entry.getKey());
            sb.append("=");
            sb.append(entry.getValue());
        }
    }

    private String convertToWarp10Line(TsIdentifier id, long ts, String value) {
        StringBuilder sb = new StringBuilder();
        sb.append(mapTsToWarp10Ts.apply(ts));
        sb.append("// ");
        sb.append(id.getClassName());
        sb.append("{");
        if (id.getLabels() != null) {
            mapIdToWarp10(id, sb);
        }
        sb.append("}");

        if (id.getTags() != null && id.getTags().size() > 0) {
            sb.append("{");
            mapIdToWarp10(id, sb);
            sb.append("}");
        }
        sb.append(" ");
        sb.append(value);
        sb.append("\r\n");
        return sb.toString();
    }

    @Override
    public <T> void appendObject(TsIdentifier id, long ts, T message) throws ServiceException {
        try {
            String json = mapper.writeValueAsString(message);
            appendString(id, ts, json);
        } catch (Exception e) {
            throw new ServiceException("Failed to serialize object: " + e.getMessage(), e);
        }
    }

    @Override
    public void appendString(TsIdentifier id, long ts, String message) throws ServiceException {
        String warp10GtsValue = encodeString(message);
        String inQuotes = "'" + warp10GtsValue + "'";
        String s = convertToWarp10Line(id, ts, inQuotes);
        try {
            appendObject(s);
        } catch (Exception e) {
            throw new ServiceException("Failed to send data: " + e.getMessage(), e);
        }
    }

    private String encodeString(String message) {
        // encoded message UTF-8 string as URL encoded
        String res = URLEncoder.encode(message, StandardCharsets.UTF_8);
        return res;
    }

    @Override
    public void append(TsIdentifier id, long ts, double value) throws ServiceException {
        String warpValue = mapValueToStr(value);
        try {
            String warp10Line = convertToWarp10Line(id, ts, warpValue);
            appendObject(warp10Line);
        } catch (Exception e) {
            throw new ServiceException("Failed to send data: " + e.getMessage(), e);
        }
    }

    private String mapValueToStr(double value) {
        if (Double.isFinite(value)) {
            String valueStr = String.valueOf(value);
            if (!valueStr.contains(".")) {
                valueStr += ".0";
            }
            return valueStr;
        }
        return "NaN";
    }

    @Override
    public void append(TsIdentifier id, long[] ts, double[] value) throws ServiceException {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ts.length; i++) {
            String valueData = mapValueToStr(value[i]);
            String warp10Line = convertToWarp10Line(id, ts[i], valueData);
            sb.append(warp10Line);
            sb.append("\r\n");
        }
        try {
            appendObject(sb.toString());
        } catch (Exception e) {
            throw new ServiceException("Failed to send data: " + e.getMessage(), e);
        }
    }

    @Override
    public void appendMultivariate(TsIdentifier id, long ts, MultivariateAwareEntity object) throws ServiceException {
        String valueData = object.toMultiVariate();
        String s = convertToWarp10Line(id, ts, valueData);
        try {
            appendObject(s);
        } catch (Exception e) {
            throw new ServiceException("Failed to send data: " + e.getMessage(), e);
        }
    }

    abstract void appendObject(String line) throws IOException;
}
