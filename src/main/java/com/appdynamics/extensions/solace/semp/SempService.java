package com.appdynamics.extensions.solace.semp;


import java.util.List;
import java.util.Map;

/**
 * Functions on this interface encapsulate all metrics queries issued to a Solace
 * eventbroker via the SEMP protocol. One SempService queries one Solace eventbroker.
 *
 * A specific SempService instance collaborates with its matching specific
 * SempRequestFactory, SempReplyFactory and SempMarshaller instances.
 */
public interface SempService {

    /**
     * Equivalent CLI: show client stats detail
     *
     * @return key-&gt;value map of global statistical metrics
     */
    Map<String,Object> checkGlobalStats();

    /**
     * Equivalent CLI: show message-spool stats detail
     *
     * @return key-&gt;value map of global msg-spool metrics
     */
    Map<String,Object> checkGlobalMsgSpoolStats();

    /**
     * Equivalent CLI: show redundancy detail
     *
     * @return key-&gt;value map of global redundancy metrics
     */
    Map<String,Object> checkGlobalRedundancy();

    /**
     * Equivalent CLI: show service detail
     *
     * @return key-&gt;value map of global service metrics
     */
    Map<String,Object> checkGlobalServiceStatus();

    /**
     * Equivalent CLI: show message-vpn * detail
     *
     * @return List of key-&gt;value maps of vpn metrics
     */
    List<Map<String,Object>> checkMsgVpnList();

    /**
     * Equivalent CLI: show message-spool message-vpn *
     *
     * @return List of key-&gt;value maps of msg-spool metrics per vpn
     */
    List<Map<String,Object>> checkMsgVpnSpoolList();

    /**
     * Equivalent CLI: show queue * detail
     *
     * @return List of key-&gt;value maps of queue metrics per queue
     */
    List<Map<String,Object>> checkQueueList();

    /**
     * Equivalent CLI: show queue * rates
     *
     * @return List of key-&gt;value maps of queue rate statistics per queue
     */
    List<Map<String,Object>> checkQueueRatesList();

    /**
     * Equivalent CLI: show queue * stats
     *
     * @return List of key-&gt;value maps of queue statistics per queue
     */
    List<Map<String,Object>> checkQueueStatsList();

    /**
     * Equivalent CLI: show topic-endpoint * detail
     *
     * @return List of key-&gt;value maps of DTE metrics per DTE
     */
    List<Map<String,Object>> checkTopicEndpointList();

    /**
     * Equivalent CLI: show topic-endpoint * rates
     *
     * @return List of key-&gt;value maps of rate statistics per DTE
     */
    List<Map<String,Object>> checkTopicEndpointRatesList();

    /**
     * Equivalent CLI: show topic-endpoint * stats
     *
     * @return List of key-&gt;value maps of statistics per DTE
     */
    List<Map<String,Object>> checkTopicEndpointStatsList();

    /**
     * Equivalent CLI: show bridge * detail
     *
     * @return List of key-&gt;value maps of bridge metrics per bridge
     */
    List<Map<String,Object>> checkGlobalBridgeList();

    /**
     * The configured displayname for the Solace eventbroker this SempService is querying
     *
     * @return String of the configured displayname from the config.yml
     */
    String getDisplayName();
}
