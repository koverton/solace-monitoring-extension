package com.appdynamics.extensions.solace.semp;


public interface SempRequestFactory<Request> {
    Request createVersionRequest(String sempVersion);

    Request createGlobalMsgSpoolRequest(String sempVersion);

    Request createGlobalRedundancyRequest(String sempVersion);

    Request createGlobalServiceRequest(String sempVersion);

    Request createGlobalStatsRequest(String sempVersion);

    Request createMsgVpnListRequest(String sempVersion);

    Request createMsgVpnSpoolListRequest(String sempVersion);

    Request createQueueListRequest(String sempVersion);

    Request createQueueRatesListRequest(String sempVersion);

    Request createTopicEndpointListRequest(String sempVersion);

    Request createTopicEndpointRatesListRequest(String sempVersion);

    Request createGlobalBridgeListRequest(String sempVersion);
}
