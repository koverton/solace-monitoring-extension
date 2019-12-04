package com.appdynamics.extensions.solace.semp.r8_2_0;

import com.appdynamics.extensions.solace.ServerConfigs;
import com.appdynamics.extensions.solace.semp.Metrics;
import com.appdynamics.extensions.solace.semp.SempStateTest;
import com.appdynamics.extensions.solace.semp.SempTestHelper;
import com.solacesystems.semp_jaxb.r8_2_0.reply.*;
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

public class SempMarshaller_r8_2_0Test
{

    private String readFile(String filename) throws Exception {
        String SEMP_VERSION = "r8_2_0";
        String replyFile = "resources/" + SEMP_VERSION + "/" + filename;
        return new String(Files.readAllBytes(Paths.get(replyFile)));
    }
    private static SempMarshaller_r8_2_0 marshaller;
    private static SempReplyFactory_r8_2_0 factory;

    @BeforeClass
    public static void setup() throws JAXBException {
        marshaller = new SempMarshaller_r8_2_0();
        Map<String,String> exclusionsMap = new HashMap<>();
        exclusionsMap.put(EXCLUDE_EXTENDED_STATS, "false");
        exclusionsMap.put(EXCLUDE_DISCARD_METRICS, "false");
        exclusionsMap.put(EXCLUDE_TLS_METRICS, "false");
        exclusionsMap.put(EXCLUDE_COMPRESSION_METRICS, "false");
        exclusionsMap.put(EXCLUDE_TEMPORARIES, "false");
        factory = new SempReplyFactory_r8_2_0(new ServerConfigs(exclusionsMap));
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
        assertEquals(487L, stats.get(Metrics.Statistics.TotalClientsConnected));
    }

    @Test
    public void showMessageSpoolTest() throws Exception {
        RpcReply reply = marshaller.fromReplyXml(readFile("show-message-spool.detail.xml"));
        Map<String, Object> info =
                factory.getGlobalMsgSpool(reply);
        assertNotNull(info);
        SempStateTest.msgSpoolTest(info);
        SempTestHelper.noNullValuesCheck(info);
    }

    @Test
    public void showActiveActivePrimaryStandbyRedundancyTest() throws Exception {
        RpcReply reply = marshaller.fromReplyXml(readFile("show-redundancy.detail-primary.inactive.xml"));
        Map<String, Object> redundancy = factory.getGlobalRedundancy(reply);
        SempStateTest.redundancyTest(redundancy);
        SempTestHelper.noNullValuesCheck(redundancy);
        assertEquals(0,  redundancy.get(Metrics.Redundancy.IsActive));
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
        assertEquals("AD-Standby", msgSpoolStatus);
    }

    @Test
    public void showActiveActiveBackupActiveRedundancyTest() throws Exception {
        RpcReply reply = marshaller.fromReplyXml(readFile("show-redundancy.detail-backup.active.xml"));

        Map<String, Object> redundancy = factory.getGlobalRedundancy(reply);
        SempStateTest.redundancyTest(redundancy);
        SempTestHelper.noNullValuesCheck(redundancy);
        assertEquals(1,  redundancy.get(Metrics.Redundancy.IsActive));
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
        assertEquals("AD-Active", msgSpoolStatus);
    }

    @Test
    public void showActiveStandbyPrimaryActiveRedundancyTest() throws Exception {
        RpcReply reply = marshaller.fromReplyXml(readFile("show-redundancy.detail-actstby.primary-active.xml"));
        Map<String, Object> redundancy = factory.getGlobalRedundancy(reply);
        SempStateTest.redundancyTest(redundancy);
        SempTestHelper.noNullValuesCheck(redundancy);
        assertEquals(1,  redundancy.get(Metrics.Redundancy.IsActive));

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
    public void showActiveStandbyBackupActiveRedundancyTest() throws Exception {
        RpcReply reply = marshaller.fromReplyXml(readFile("show-redundancy.detail-actstby.backup-active.xml"));
        Map<String, Object> redundancy = factory.getGlobalRedundancy(reply);
        SempStateTest.redundancyTest(redundancy);
        SempTestHelper.noNullValuesCheck(redundancy);
        assertEquals(1,  redundancy.get(Metrics.Redundancy.IsActive));

        String redStatus = reply.getRpc()
                .getShow()
                .getRedundancy()
                .getRedundancyStatus();
        assertEquals("Up", redStatus);

        String backupActivity = reply.getRpc()
                .getShow()
                .getRedundancy()
                .getVirtualRouters()
                .getBackup()
                .getStatus()
                .getActivity();
        assertEquals("Local Active", backupActivity);

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
        assertEquals("AD-Active", msgSpoolStatus);
    }


    @Test
    public void showActiveStandbyPrimaryStandbyRedundancyTest() throws Exception {
        RpcReply reply = marshaller.fromReplyXml(readFile("show-redundancy.detail-actstby.primary-inactive.xml"));
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
                .getPrimary()
                .getStatus()
                .getActivity();
        assertEquals("Mate Active", primaryActivity);

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
        assertEquals("AD-Standby", msgSpoolStatus);
    }
    @Test
    public void showActiveStandbyBackupStandbyRedundancyTest() throws Exception {
        RpcReply reply = marshaller.fromReplyXml(readFile("show-redundancy.detail-actstby.backup-inactive.xml"));
        Map<String, Object> redundancy = factory.getGlobalRedundancy(reply);
        SempStateTest.redundancyTest(redundancy);
        SempTestHelper.noNullValuesCheck(redundancy);
        assertEquals(0,  redundancy.get(Metrics.Redundancy.IsActive));

        String redStatus = reply.getRpc()
                .getShow()
                .getRedundancy()
                .getRedundancyStatus();
        assertEquals("Up", redStatus);

        String backupActivity = reply.getRpc()
                .getShow()
                .getRedundancy()
                .getVirtualRouters()
                .getBackup()
                .getStatus()
                .getActivity();
        assertEquals("Mate Active", backupActivity);

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
    public void showStandaloneRedundancyTest() throws Exception {
        RpcReply reply = marshaller.fromReplyXml(readFile("show-redundancy.detail.standalone.xml"));
        Map<String, Object> redundancy = factory.getGlobalRedundancy(reply);
        SempStateTest.standaloneTest(redundancy);
        SempTestHelper.noNullValuesCheck(redundancy);
        assertEquals(0, redundancy.get(Metrics.Redundancy.OperationalStatus));
        assertEquals(0, redundancy.get(Metrics.Redundancy.IsActive));
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
