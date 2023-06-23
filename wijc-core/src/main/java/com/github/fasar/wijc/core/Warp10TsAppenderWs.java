package com.github.fasar.wijc.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.secheron.ioms.microservice.iomsconvertor.model.Tuple2;
import com.secheron.ioms.microservice.iomsconvertor.model.errors.ServiceException;
import com.secheron.microservice.iomsconvertor.tools.ObjectMappersUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.Closeable;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

@Component
@Slf4j
public class Warp10TsAppenderWs extends TsAppenderAbstract implements Closeable {

    private final Warp10WebSocketClient wsClient;
    @Value("ioms.ts.warp10.ws-url")
    URI serverUri;
    @Value("ioms.ts.warp10.writeToken")
    String writeToken;

    @Autowired
    public Warp10TsAppenderWs(String writeToken, URI serverUri, Warp10TSConfiguration configuration, ObjectMapper mapper) throws InterruptedException {
        super(configuration, mapper);
        wsClient = new Warp10WebSocketClient(serverUri, writeToken);
        wsClient.connect();
    }

    public static void main(String[] args) throws MalformedURLException, URISyntaxException, ServiceException, InterruptedException {
        URI uri = new URI("ws://10.0.0.10:8080/api/v0/streamupdate");
        Warp10TsAppenderWs ws = new Warp10TsAppenderWs("writeTokenCI", uri, Warp10TSConfiguration.defaultConfiguration, ObjectMappersUtils.buildJson());
        TsIdentifier id = TsIdentifier.of("test.mytest",
                new Tuple2<>("relayId", "relayId"),
                new Tuple2<>("counterCatName", "counterCatName"),
                new Tuple2<>("counterName", "counterName"),
                new Tuple2<>("indice", "" + 1)
        );
        long i = 0;
        ws.append(id, i++, 1.20);
        ws.append(id, i++, 1.20);
        ws.append(id, i++, 1.20);
        ws.append(id, i++, 1.20);
        ws.append(id, i++, 1.20);
        ws.append(id, i++, 1.20);
        ws.append(id, i++, 1.20);
        ws.append(id, i++, 1.20);

        Thread.sleep(1000);

    }

    @Override
    void appendObject(String line) throws IOException {
        wsClient.send(line);
    }

    @Override
    public void close() {
        try {
            wsClient.closeBlocking();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
