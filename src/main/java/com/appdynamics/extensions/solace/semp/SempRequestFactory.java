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

    Request createQueueListRequest(String sempVersion, String namePattern, String vpnName);

    Request createQueueRatesListRequest(String sempVersion, String namePattern, String vpnName);

    Request createQueueStatsListRequest(String sempVersion, String namePattern, String vpnName);

    Request createTopicEndpointListRequest(String sempVersion, String namePattern, String vpnName);

    Request createTopicEndpointRatesListRequest(String sempVersion, String namePattern, String vpnName);

    Request createTopicEndpointStatsListRequest(String sempVersion, String namePattern, String vpnName);

    Request createGlobalBridgeListRequest(String sempVersion);
}
