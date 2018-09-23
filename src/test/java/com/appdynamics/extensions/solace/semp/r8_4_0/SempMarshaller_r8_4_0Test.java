package com.appdynamics.extensions.solace.semp.r8_4_0;

import com.appdynamics.extensions.solace.ServerExclusionPolicies;
import com.appdynamics.extensions.solace.semp.Metrics;
import com.solacesystems.semp_jaxb.r8_2_0.reply.*;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.appdynamics.extensions.solace.MonitorConfigs.EXCLUDE_EXTENDED_STATS;
import static org.junit.Assert.*;

public class SempMarshaller_r8_4_0Test
{

    private String readFile(String filename) throws Exception {
        String SEMP_VERSION = "r8_4_0";
        String replyFile = "resources/" + SEMP_VERSION + "/" + filename;
        return new String(Files.readAllBytes(Paths.get(replyFile)));
    }
    private static SempMarshaller_r8_4_0 marshaller;
    private static SempReplyFactory_r8_4_0 factory;

    @BeforeClass
    public static void setup() throws JAXBException {
        marshaller = new SempMarshaller_r8_4_0();
        Map<String,String> exclusionsMap = new HashMap<>();
        exclusionsMap.put(EXCLUDE_EXTENDED_STATS, "false");
        factory = new SempReplyFactory_r8_4_0(new ServerExclusionPolicies(exclusionsMap));
    }

    @Test
    public void getStatusSuccessTest() throws Exception {
        RpcReply reply = marshaller.fromReplyXml(readFile("set-success.xml"));
        assertTrue(factory.isSuccess(reply));
    }

    @Test
    public void getStatusFailNotAllowedTest() throws Exception {
        RpcReply reply = marshaller.fromReplyXml(readFile("set-failure.not.allowed.xml"));
        assertFalse(factory.isSuccess(reply));
        assertEquals("fail", reply.getExecuteResult().getCode());
        assertEquals("not allowed", reply.getExecuteResult().getReason());
        assertEquals(89, reply.getExecuteResult().getReasonCode().intValue());
    }

    @Test
    public void getStatusFailBadXmlTest() throws Exception {
        RpcReply reply = marshaller.fromReplyXml(readFile("set-failure.parse.error.xml"));
        assertFalse(factory.isSuccess(reply));
        assertEquals("invalid message: schema validation error", reply.getParseError());
    }

    @Test
    public void showServiceTest() throws Exception {
        RpcReply reply = marshaller.fromReplyXml(readFile("show-service.xml"));
        assertTrue(factory.isSuccess(reply));
        Map<String, Object> service = factory.getGlobalService(reply);
        assertNotNull(service);
    }

    @Test
    public void showGlobalStatsTest() throws Exception {
        RpcReply reply = marshaller.fromReplyXml(readFile("show-stats.client.detail.xml"));
        Map<String, Object> stats =
                factory.getGlobalStats(reply);
        assertNotNull(stats);
        assertEquals(367L, stats.get(Metrics.Statistics.TotalClientsConnected));
    }

    @Test
    public void showMessageSpoolTest() throws Exception {
        RpcReply reply = marshaller.fromReplyXml(readFile("show-message-spool.detail-primary.active.xml"));
        Map<String, Object> info =
                factory.getGlobalMsgSpool(reply);
        assertNotNull(info);
    }

    // TODO: had no hardware config available to validate output against
    @Test
    public void showPrimaryRedundancyTest() throws Exception {
        RpcReply reply = marshaller.fromReplyXml(readFile("show-redundancy.detail-primary.active.xml"));
        Map<String, Object> redundancy = factory.getGlobalRedundancy(reply);
        assertNotNull(redundancy);
    }

    // TODO
    @Test
    public void showBackupRedundancyTest() throws Exception {
        RpcReply reply = marshaller.fromReplyXml(readFile("show-redundancy.detail-backup.inactive.xml"));
        Map<String, Object> redundancy = factory.getGlobalRedundancy(reply);
        assertNotNull(redundancy);
    }

    @Test
    public void showMsgVpnListTest() throws Exception {
        RpcReply reply = marshaller.fromReplyXml(readFile("show-vpn.stats.xml"));
        List<Map<String, Object>> vpns = factory.getMsgVpnList(reply);
        assertNotNull(vpns);
    }

    @Test
    public void showQueueListTest() throws Exception {
        RpcReply reply = marshaller.fromReplyXml(readFile("show-queues.detail.xml"));
        List<Map<String, Object>> queues = factory.getQueueList(reply);
        assertNotNull(queues);
    }

    @Test
    public void showQueueRatesListTest() throws Exception {
        RpcReply reply = marshaller.fromReplyXml(readFile("show-queues.rates.xml"));
        List<Map<String, Object>> queues = factory.getQueueRatesList(reply);
        assertNotNull(queues);
    }

    @Test
    public void showQueueStatsListTest() throws Exception {
        RpcReply reply = marshaller.fromReplyXml(readFile("show-queues.stats.xml"));
        List<Map<String, Object>> queues = factory.getQueueStatsList(reply);
        assertNotNull(queues);
    }

    @Test
    public void showTopicEndpointListTest() throws Exception {
        RpcReply reply = marshaller.fromReplyXml(readFile("show-topicendpoints.detail.xml"));
        List<Map<String, Object>> endpoints = factory.getTopicEndpointList(reply);
        assertNotNull(endpoints);
    }

    @Test
    public void showTopicEndpointRatesListTest() throws Exception {
        RpcReply reply = marshaller.fromReplyXml(readFile("show-topicendpoints.rates.xml"));
        List<Map<String, Object>> endpoints = factory.getTopicEndpointRatesList(reply);
        assertNotNull(endpoints);
    }

    @Test
    public void showTopicEndpointStatsListTest() throws Exception {
        RpcReply reply = marshaller.fromReplyXml(readFile("show-topicendpoints.stats.xml"));
        List<Map<String, Object>> endpoints = factory.getTopicEndpointStatsList(reply);
        assertNotNull(endpoints);
        assertEquals(2, endpoints.size());
        for(Map<String,Object> e : endpoints) {
            if (e.get(Metrics.TopicEndpoint.TopicEndpointName).equals("t2")) {
                assertEquals(190L, e.get(Metrics.TopicEndpoint.RedeliveredCount));
                assertEquals(190L, e.get(Metrics.TopicEndpoint.TotalEgressDiscards));
            }
        }
    }

    @Test
    public void showBridgeListTest() throws Exception {
        RpcReply reply = marshaller.fromReplyXml(readFile("show-bridges.xml"));
        List<Map<String, Object>> bridges = factory.getGlobalBridgeList(reply);
        assertNotNull(bridges);
    }
}
