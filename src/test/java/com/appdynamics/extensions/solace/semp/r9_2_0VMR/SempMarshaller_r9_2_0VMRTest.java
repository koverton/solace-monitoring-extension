package com.appdynamics.extensions.solace.semp.r9_2_0VMR;

import com.appdynamics.extensions.solace.ServerConfigs;
import com.appdynamics.extensions.solace.semp.Metrics;
import com.appdynamics.extensions.solace.semp.SempStateTest;
import com.appdynamics.extensions.solace.semp.SempTestHelper;
import com.solacesystems.semp_jaxb.r9_2_0VMR.reply.*;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.appdynamics.extensions.solace.MonitorConfigs.*;
import static org.junit.Assert.*;

public class SempMarshaller_r9_2_0VMRTest
{

    private String readFile(String filename) throws Exception {
        String SEMP_VERSION = "9_2_0VMR";
        String replyFile = "resources/r" + SEMP_VERSION + "/" + filename;
        return new String(Files.readAllBytes(Paths.get(replyFile)));
    }
    private static SempMarshaller_r9_2_0VMR marshaller;
    private static SempReplyFactory_r9_2_0VMR factory;

    @BeforeClass
    public static void setup() throws JAXBException {
        marshaller = new SempMarshaller_r9_2_0VMR();
        Map<String,String> exclusionsMap = new HashMap<>();
        exclusionsMap.put(EXCLUDE_EXTENDED_STATS, "false");
        exclusionsMap.put(EXCLUDE_DISCARD_METRICS, "false");
        exclusionsMap.put(EXCLUDE_TLS_METRICS, "false");
        exclusionsMap.put(EXCLUDE_COMPRESSION_METRICS, "false");
        exclusionsMap.put(EXCLUDE_TEMPORARIES, "false");
        factory = new SempReplyFactory_r9_2_0VMR(new ServerConfigs(exclusionsMap));
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
        SempTestHelper.noNullValuesCheck(service);
    }

    @Test
    public void showGlobalStatsTest() throws Exception {
        RpcReply reply = marshaller.fromReplyXml(readFile("show-stats.client.detail.xml"));
        Map<String, Object> stats =
                factory.getGlobalStats(reply);
        assertNotNull(stats);
        SempTestHelper.noNullValuesCheck(stats);
        assertEquals(4L, stats.get(Metrics.Statistics.TotalClientsConnected));
    }

    @Test
    public void showPrimaryMessageSpoolTest() throws Exception {
        RpcReply reply = marshaller.fromReplyXml(readFile("show-message-spool.detail-primary.xml"));
        Map<String, Object> info =
                factory.getGlobalMsgSpool(reply);
        assertNotNull(info);
        SempTestHelper.noNullValuesCheck(info);
        SempStateTest.msgSpoolTest(info);

        Integer isEnabled = (Integer)info.get(Metrics.MsgSpool.IsEnabled);
        Integer isActive = (Integer)info.get(Metrics.MsgSpool.IsActive);
        Integer isStandby = (Integer)info.get(Metrics.MsgSpool.IsStandby);
        Integer isDatapathUp = (Integer)info.get(Metrics.MsgSpool.IsDatapathUp);
        Integer isSynchronized = (Integer)info.get(Metrics.MsgSpool.IsSynchronized);
        assertEquals(1, isEnabled.intValue());
        assertEquals(1, isActive.intValue());
        assertEquals(0, isStandby.intValue());
        assertEquals(1, isDatapathUp.intValue());
        assertEquals(1, isSynchronized.intValue());
    }

    @Test
    public void showBackupMessageSpoolTest() throws Exception {
        RpcReply reply = marshaller.fromReplyXml(readFile("show-message-spool.detail-backup.xml"));
        Map<String, Object> info =
                factory.getGlobalMsgSpool(reply);
        assertNotNull(info);
        SempTestHelper.noNullValuesCheck(info);

        Integer isEnabled = (Integer)info.get(Metrics.MsgSpool.IsEnabled);
        Integer isActive = (Integer)info.get(Metrics.MsgSpool.IsActive);
        Integer isStandby = (Integer)info.get(Metrics.MsgSpool.IsStandby);
        Integer isDatapathUp = (Integer)info.get(Metrics.MsgSpool.IsDatapathUp);
        Integer isSynchronized = (Integer)info.get(Metrics.MsgSpool.IsSynchronized);
        assertEquals(1, isEnabled.intValue());
        assertEquals(0, isActive.intValue());
        assertEquals(1, isStandby.intValue());
        assertEquals(0, isDatapathUp.intValue());
        assertEquals(0, isSynchronized.intValue());
    }

    @Test
    public void showPrimaryRedundancyTest() throws Exception {
        RpcReply reply = marshaller.fromReplyXml(readFile("show-redundancy.detail-primary.xml"));
        Map<String, Object> redundancy = factory.getGlobalRedundancy(reply);
        SempStateTest.redundancyTest(redundancy);
        SempTestHelper.noNullValuesCheck(redundancy);
        assertEquals(1,  redundancy.get(Metrics.Redundancy.IsActive));
        assertEquals(1,  redundancy.get(Metrics.Redundancy.OperationalStatus));
        assertEquals(1,  redundancy.get(Metrics.Redundancy.ConfiguredStatus));

        String redStatus = reply.getRpc()
                .getShow()
                .getRedundancy()
                .getRedundancyStatus();
        assertEquals("Up", redStatus);

        String primaryActivity = reply.getRpc()
                .getShow()
                .getRedundancy()
                .getVirtualRouters()
                .getPrimary()
                .getStatus()
                .getActivity();
        assertEquals("Local Active", primaryActivity);

        String msgSpoolStatus = reply.getRpc()
                .getShow()
                .getRedundancy()
                .getVirtualRouters()
                .getPrimary()
                .getStatus()
                .getDetail()
                .getMessageSpoolStatus()
                .getInternal()
                .getRedundancy();
        assertEquals("AD-Active", msgSpoolStatus);
    }

    @Test
    public void showBackupRedundancyTest() throws Exception {
        RpcReply reply = marshaller.fromReplyXml(readFile("show-redundancy.detail-backup.xml"));
        Map<String, Object> redundancy = factory.getGlobalRedundancy(reply);
        SempStateTest.redundancyTest(redundancy);
        SempTestHelper.noNullValuesCheck(redundancy);
        assertEquals(0,  redundancy.get(Metrics.Redundancy.IsActive));

        String redStatus = reply.getRpc()
                .getShow()
                .getRedundancy()
                .getRedundancyStatus();
        assertEquals("Up", redStatus);

        String primaryActivity = reply.getRpc()
                .getShow()
                .getRedundancy()
                .getVirtualRouters()
                .getBackup()
                .getStatus()
                .getActivity();
        assertEquals("Mate Active", primaryActivity);

        String msgSpoolStatus = reply.getRpc()
                .getShow()
                .getRedundancy()
                .getVirtualRouters()
                .getBackup()
                .getStatus()
                .getDetail()
                .getMessageSpoolStatus()
                .getInternal()
                .getRedundancy();
        assertEquals("AD-Standby", msgSpoolStatus);
    }

    @Test
    public void showMsgVpnListTest() throws Exception {
        RpcReply reply = marshaller.fromReplyXml(readFile("show-vpn.stats.xml"));
        List<Map<String, Object>> vpns = factory.getMsgVpnList(reply);
        assertNotNull(vpns);
        SempTestHelper.noNullValuesCheck(vpns);
    }

    @Test
    public void showQueueListTest() throws Exception {
        RpcReply reply = marshaller.fromReplyXml(readFile("show-queues.detail.xml"));
        List<Map<String, Object>> queues = factory.getQueueList(reply);
        assertNotNull(queues);
        SempTestHelper.noNullValuesCheck(queues);
        for (Map<String, Object> q : queues) {
            if (q.get(Metrics.Queue.QueueName).equals("q1")) {
                Integer currentSpooled = (Integer) q.get(Metrics.Queue.MessagesSpooled);
                assertEquals(5432, currentSpooled.intValue());
            }
        }
    }

    @Test
    public void showQueueRatesListTest() throws Exception {
        RpcReply reply = marshaller.fromReplyXml(readFile("show-queues.rates.xml"));
        List<Map<String, Object>> queues = factory.getQueueRatesList(reply);
        assertNotNull(queues);
        SempTestHelper.noNullValuesCheck(queues);
    }

    @Test
    public void showQueueStatsListTest() throws Exception {
        RpcReply reply = marshaller.fromReplyXml(readFile("show-queues.stats.xml"));
        List<Map<String, Object>> queues = factory.getQueueStatsList(reply);
        assertNotNull(queues);
        SempTestHelper.noNullValuesCheck(queues);
        for (Map<String, Object> q : queues) {
            if (q.get(Metrics.Queue.QueueName).equals("q1")) {
                Long totalSpooled = (Long) q.get(Metrics.Queue.TotalMessagesSpooled);
                assertEquals(8765, totalSpooled.intValue());
            }
            for(Map.Entry<String,Object> pair : q.entrySet()) {
                System.out.println("    " + pair.getKey() + " => " + pair.getValue() );
            }
        }
    }

    @Test
    public void showTopicEndpointListTest() throws Exception {
        RpcReply reply = marshaller.fromReplyXml(readFile("show-topicendpoints.detail.xml"));
        List<Map<String, Object>> endpoints = factory.getTopicEndpointList(reply);
        assertNotNull(endpoints);
        SempTestHelper.noNullValuesCheck(endpoints);
    }

    @Test
    public void showTopicEndpointRatesListTest() throws Exception {
        RpcReply reply = marshaller.fromReplyXml(readFile("show-topicendpoints.rates.xml"));
        List<Map<String, Object>> endpoints = factory.getTopicEndpointRatesList(reply);
        assertNotNull(endpoints);
        SempTestHelper.noNullValuesCheck(endpoints);
    }

    @Test
    public void showTopicEndpointStatsListTest() throws Exception {
        RpcReply reply = marshaller.fromReplyXml(readFile("show-topicendpoints.stats.xml"));
        List<Map<String, Object>> endpoints = factory.getTopicEndpointStatsList(reply);
        assertNotNull(endpoints);
        assertEquals(2, endpoints.size());
        for(Map<String,Object> e : endpoints) {
            SempTestHelper.noNullValuesCheck(e);
            for(Map.Entry<String,Object> pair : e.entrySet()) {
                System.out.println("    " + pair.getKey() + " => " + pair.getValue() );
            }
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
        SempTestHelper.noNullValuesCheck(bridges);
    }
}
