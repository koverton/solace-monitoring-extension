package com.appdynamics.extensions.solace.semp.r8_6VMR;

import com.appdynamics.extensions.solace.ServerConfigs;
import com.appdynamics.extensions.solace.semp.SempRequestFactory;
import com.solacesystems.semp_jaxb.r8_6VMR.request.ObjectFactory;
import com.solacesystems.semp_jaxb.r8_6VMR.request.Rpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SempRequestFactory_r8_6VMR implements SempRequestFactory<Rpc> {
    private static final Logger logger = LoggerFactory.getLogger(SempRequestFactory_r8_6VMR.class);

    public SempRequestFactory_r8_6VMR(ServerConfigs serverConfigs) {
        this.serverConfigs = serverConfigs;
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
        request.getShow().getRedundancy().setDetail1(factory.createKeywordType());
        return request;
    }

    public Rpc createGlobalServiceRequest(String sempVersion) {
        final Rpc request = newShowRequest(sempVersion);
        request.getShow().setService(factory.createRpcShowService());
        return request;
    }

    public Rpc createMsgVpnListRequest(String sempVersion) {
        final Rpc request = newShowRequest(sempVersion);
        request.getShow().setMessageVpn(factory.createRpcShowMessageVpn());
        request.getShow().getMessageVpn().setVpnName("*");
        request.getShow().getMessageVpn().setStats1(factory.createKeywordType());
        request.getShow().getMessageVpn().setCount1(factory.createKeywordType());
        request.getShow().getMessageVpn().setNumElements1(100L);
        return request;
    }

    public Rpc createMsgVpnSpoolListRequest(String sempVersion) {
        final Rpc request = newShowRequest(sempVersion);
        request.getShow().setMessageSpool(factory.createRpcShowMessageSpool());
        request.getShow().getMessageSpool().setVpnName("*");
        request.getShow().getMessageSpool().setDetail(factory.createKeywordType());
        return request;
    }

    public Rpc createQueueListRequest(String sempVersion) {
        final Rpc request = newShowRequest(sempVersion);
        request.getShow().setQueue(factory.createRpcShowQueue());
        request.getShow().getQueue().setName("*");
        if (serverConfigs.getExcludeTemporaries())
            request.getShow().getQueue().setDurable(factory.createKeywordType());
        request.getShow().getQueue().setDetail(factory.createKeywordType());
        request.getShow().getQueue().setCount(factory.createKeywordType());
        request.getShow().getQueue().setNumElements(100L);
        return request;
    }

    public Rpc createQueueRatesListRequest(String sempVersion) {
        final Rpc request = newShowRequest(sempVersion);
        request.getShow().setQueue(factory.createRpcShowQueue());
        request.getShow().getQueue().setName("*");
        if (serverConfigs.getExcludeTemporaries())
            request.getShow().getQueue().setDurable(factory.createKeywordType());
        request.getShow().getQueue().setRates(factory.createKeywordType());
        request.getShow().getQueue().setCount(factory.createKeywordType());
        request.getShow().getQueue().setNumElements(100L);
        return request;
    }

    public Rpc createQueueStatsListRequest(String sempVersion) {
        final Rpc request = newShowRequest(sempVersion);
        request.getShow().setQueue(factory.createRpcShowQueue());
        request.getShow().getQueue().setName("*");
        if (serverConfigs.getExcludeTemporaries())
            request.getShow().getQueue().setDurable(factory.createKeywordType());
        request.getShow().getQueue().setStats(factory.createKeywordType());
        request.getShow().getQueue().setCount(factory.createKeywordType());
        request.getShow().getQueue().setNumElements(100L);
        return request;
    }

    public Rpc createTopicEndpointListRequest(String sempVersion) {
        final Rpc request = newShowRequest(sempVersion);
        request.getShow().setTopicEndpoint(factory.createRpcShowTopicEndpoint());
        request.getShow().getTopicEndpoint().setName("*");
        if (serverConfigs.getExcludeTemporaries())
            request.getShow().getTopicEndpoint().setDurable(factory.createKeywordType());
        request.getShow().getTopicEndpoint().setDetail(factory.createKeywordType());
        request.getShow().getTopicEndpoint().setCount(factory.createKeywordType());
        request.getShow().getTopicEndpoint().setNumElements(100L);
        return request;
    }

    public Rpc createTopicEndpointRatesListRequest(String sempVersion) {
        final Rpc request = newShowRequest(sempVersion);
        request.getShow().setTopicEndpoint(factory.createRpcShowTopicEndpoint());
        request.getShow().getTopicEndpoint().setName("*");
        if (serverConfigs.getExcludeTemporaries())
            request.getShow().getTopicEndpoint().setDurable(factory.createKeywordType());
        request.getShow().getTopicEndpoint().setRates(factory.createKeywordType());
        request.getShow().getTopicEndpoint().setCount(factory.createKeywordType());
        request.getShow().getTopicEndpoint().setNumElements(100L);
        return request;
    }

    public Rpc createTopicEndpointStatsListRequest(String sempVersion) {
        final Rpc request = newShowRequest(sempVersion);
        request.getShow().setTopicEndpoint(factory.createRpcShowTopicEndpoint());
        request.getShow().getTopicEndpoint().setName("*");
        if (serverConfigs.getExcludeTemporaries())
            request.getShow().getTopicEndpoint().setDurable(factory.createKeywordType());
        request.getShow().getTopicEndpoint().setStats(factory.createKeywordType());
        request.getShow().getTopicEndpoint().setCount(factory.createKeywordType());
        request.getShow().getTopicEndpoint().setNumElements(100L);
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

    final private ServerConfigs serverConfigs;
    final private ObjectFactory factory;
}
