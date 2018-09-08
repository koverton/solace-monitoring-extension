package com.appdynamics.extensions.solace.semp;

import java.util.List;
import java.util.Map;

public interface SempReplyFactory<Reply> {

    boolean isSuccess(Reply reply);

    Map<String,Object> getGlobalStats(Reply reply);

    Map<String,Object> getGlobalMsgSpool(Reply reply);

    Map<String,Object> getGlobalRedundancy(Reply reply);

    Map<String,Object> getGlobalService(Reply reply);

    List<Map<String,Object>> getMsgVpnList(Reply reply);

    List<Map<String, Object>> getMsgVpnSpoolList(Reply reply);

    List<Map<String,Object>> getQueueList(Reply reply);

    List<Map<String,Object>> getQueueRatesList(Reply reply);

    List<Map<String,Object>> getTopicEndpointList(Reply reply);

    List<Map<String,Object>> getTopicEndpointRatesList(Reply reply);

    List<Map<String,Object>> getGlobalBridgeList(Reply reply);
}
