package com.appdynamics.extensions.solace.semp;


import java.util.List;
import java.util.Map;

public interface SempService {

    Map<String,Object> checkGlobalStats();

    Map<String,Object> checkGlobalRedundancy();

    Map<String,Object> checkGlobalServiceStatus();

    Map<String,Object> checkGlobalMsgSpoolStats();

    List<Map<String,Object>> checkMsgVpnList();

    List<Map<String,Object>> checkQueueList();

    List<Map<String,Object>> checkQueueRatesList();

    List<Map<String,Object>> checkTopicEndpointList();

    List<Map<String,Object>> checkTopicEndpointRatesList();

    List<Map<String,Object>> checkGlobalBridgeList();

    String getDisplayName();
}
