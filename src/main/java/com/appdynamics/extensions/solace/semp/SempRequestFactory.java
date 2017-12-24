package com.appdynamics.extensions.solace.semp;


public interface SempRequestFactory<Request> {
    Request createVersionRequest(String sempVersion);

    Request createGlobalStatsRequest(String sempVersion);

    Request createGlobalMsgSpoolRequest(String sempVersion);

    Request createGlobalRedundancyRequest(String sempVersion);

    Request createGlobalServiceRequest(String sempVersion);

    Request createQueueListRequest(String sempVersion);

    Request createGlobalBridgeListRequest(String sempVersion);
}
