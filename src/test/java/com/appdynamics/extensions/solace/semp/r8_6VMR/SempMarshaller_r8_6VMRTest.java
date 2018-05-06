package com.appdynamics.extensions.solace.semp.r8_6VMR;

import com.appdynamics.extensions.solace.semp.Metrics;
import com.solacesystems.semp_jaxb.r8_6VMR.reply.*;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class SempMarshaller_r8_6VMRTest
{

    private String readFile(String filename) throws Exception {
        String SEMP_VERSION = "8_6VMR";
        String replyFile = "resources/r" + SEMP_VERSION + "/" + filename;
        return new String(Files.readAllBytes(Paths.get(replyFile)));
    }
    private static SempMarshaller_r8_6VMR marshaller;
    private static SempReplyFactory_r8_6VMR factory;

    @BeforeClass
    public static void setup() throws JAXBException {
        marshaller = new SempMarshaller_r8_6VMR();
        factory = new SempReplyFactory_r8_6VMR();
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
    }

    @Test
    public void showPrimaryMessageSpoolTest() throws Exception {
        RpcReply reply = marshaller.fromReplyXml(readFile("show-message-spool.detail-primary.xml"));
        Map<String, Object> info =
                factory.getGlobalMsgSpool(reply);
        assertNotNull(info);

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
        RpcReply reply = marshaller.fromReplyXml(readFile("show-redundancy.detail-backup.xml"));
        Map<String, Object> redundancy = factory.getGlobalRedundancy(reply);
        assertNotNull(redundancy);
    }

    @Test
    public void showBackupRedundancyTest() throws Exception {
        RpcReply reply = marshaller.fromReplyXml(readFile("show-redundancy.detail-backup.xml"));
        Map<String, Object> redundancy = factory.getGlobalRedundancy(reply);
        assertNotNull(redundancy);
    }

    @Test
    public void showQueueListTest() throws Exception {
        RpcReply reply = marshaller.fromReplyXml(readFile("show-queues.detail.xml"));
        List<Map<String, Object>> queues = factory.getQueueList(reply);
        assertNotNull(queues);
    }

    @Test
    public void showTopicEndpointListTest() throws Exception {
        RpcReply reply = marshaller.fromReplyXml(readFile("show-topicendpoints.detail.xml"));
        List<Map<String, Object>> endpoints = factory.getTopicEndpointList(reply);
        assertNotNull(endpoints);
    }

    @Test
    public void showBridgeListTest() throws Exception {
        RpcReply reply = marshaller.fromReplyXml(readFile("show-bridges.xml"));
        List<Map<String, Object>> bridges = factory.getGlobalBridgeList(reply);
        assertNotNull(bridges);
    }
}
