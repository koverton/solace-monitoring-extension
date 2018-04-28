package com.appdynamics.extensions.solace.semp.r8_2_0;

import com.appdynamics.extensions.solace.semp.SempRequestFactory;
import com.solacesystems.semp_jaxb.r8_2_0.request.ObjectFactory;
import com.solacesystems.semp_jaxb.r8_2_0.request.Rpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SempRequestFactory_r8_2_0 implements SempRequestFactory<Rpc> {
    private static final Logger logger = LoggerFactory.getLogger(SempRequestFactory_r8_2_0.class);

    public SempRequestFactory_r8_2_0() {
        factory = new ObjectFactory();
    }

    public Rpc createVersionRequest(String sempVersion) {
        final Rpc request = newShowRequest(sempVersion);
        request.getShow().setVersion(factory.createKeywordType());
        return request;
    }

    public Rpc createGlobalStatsRequest(String sempVersion) {
        final Rpc request = newShowRequest(sempVersion);
        request.getShow().setStats(factory.createRpcShowStats());
        request.getShow().getStats().setClient(factory.createRpcShowStatsClient());
        request.getShow().getStats().getClient().setDetail(factory.createKeywordType());
        return request;
    }

    public Rpc createGlobalMsgSpoolRequest(String sempVersion) {
        final Rpc request = newShowRequest(sempVersion);
        request.getShow().setMessageSpool(factory.createRpcShowMessageSpool());
        request.getShow().getMessageSpool().setDetail(factory.createKeywordType());
        return request;
    }

    public Rpc createGlobalRedundancyRequest(String sempVersion) {
        final Rpc request = newShowRequest(sempVersion);
        request.getShow().setRedundancy(factory.createRpcShowRedundancy());
        request.getShow().getRedundancy().setDetail(factory.createKeywordType());
        return request;
    }

    public Rpc createGlobalServiceRequest(String sempVersion) {
        final Rpc request = newShowRequest(sempVersion);
        request.getShow().setService(factory.createRpcShowService());
        return request;
    }
    
    public Rpc createQueueListRequest(String sempVersion) {
        final Rpc request = newShowRequest(sempVersion);
        request.getShow().setQueue(factory.createRpcShowQueue());
        request.getShow().getQueue().setName("*");
        request.getShow().getQueue().setDetail(factory.createKeywordType());
        return request;
    }

    public Rpc createTopicEndpointListRequest(String sempVersion) {
        final Rpc request = newShowRequest(sempVersion);
        request.getShow().setTopicEndpoint(factory.createRpcShowTopicEndpoint());
        request.getShow().getTopicEndpoint().setName("*");
        request.getShow().getTopicEndpoint().setDetail(factory.createKeywordType());
        return request;
    }

    public Rpc createGlobalBridgeListRequest(String sempVersion) {
        final Rpc request = newShowRequest(sempVersion);
        request.getShow().setBridge(factory.createRpcShowBridge());
        request.getShow().getBridge().setBridgeNamePattern("*");
        //request.getShow().getBridge().setDetail(factory.createKeywordType());
        return request;
    }

    private Rpc newShowRequest(String sempVersion) {
        final Rpc request = new Rpc();
        request.setSempVersion(sempVersion);
        request.setShow(factory.createRpcShow());
        return request;
    }

    final private ObjectFactory factory;
}
