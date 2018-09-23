package com.appdynamics.extensions.solace.semp;


/**
 * Creates a service-specific Request instance for each of the SempService queries.
 *
 * @param <Request> SEMP version-specific Request object.
 */
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

    Request createQueueStatsListRequest(String sempVersion);

    Request createTopicEndpointListRequest(String sempVersion);

    Request createTopicEndpointRatesListRequest(String sempVersion);

    Request createTopicEndpointStatsListRequest(String sempVersion);

    Request createGlobalBridgeListRequest(String sempVersion);
}
