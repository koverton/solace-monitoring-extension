package com.appdynamics.extensions.solace.semp;

import com.appdynamics.extensions.solace.ServerConfigs;
import com.appdynamics.extensions.solace.semp.r9_2_0VMR.SempMarshaller_r9_2_0VMR;
import com.appdynamics.extensions.solace.semp.r9_2_0VMR.SempReplyFactory_r9_2_0VMR;
import com.appdynamics.extensions.solace.semp.r9_2_0VMR.SempRequestFactory_r9_2_0VMR;
import com.solacesystems.semp_jaxb.r9_2_0VMR.reply.QueueType;
import com.solacesystems.semp_jaxb.r9_2_0VMR.reply.RpcReply;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.appdynamics.extensions.solace.MonitorConfigs.EXCLUDE_DISCARD_METRICS;
import static com.appdynamics.extensions.solace.MonitorConfigs.EXCLUDE_EXTENDED_STATS;
import static org.junit.Assert.assertEquals;

public class ConnectorTest {

    private static final String err86vmr = "<rpc-reply semp-version=\"soltr/8_6VMR\">"
            + "<parse-error>invalid message: schema validation error</parse-error></rpc-reply>";
    private static final String err820 = "<rpc-reply semp-version=\"soltr/8_2_0\">"
            + "<parse-error>invalid message: schema validation error</parse-error></rpc-reply>";
    @Test
    public void versionTest() throws Exception {
        Sempv1Connector connector = new Sempv1Connector(
                "http://1.1.1.1:8080/SEMP",
                "admin",
                "admin",
                "jimmy");

        // VMR version
        assertEquals("8_6VMR", connector.getSempVersion(err86vmr).getVersionString());
        // Hardware version
        assertEquals("8_2_0", connector.getSempVersion(err820).getVersionString());
    }

    @Test
    public void localHostVersionTest() throws Exception {
        Sempv1Connector connector = new Sempv1Connector(
                "http://localhost:8080/SEMP",
                "admin",
                "admin",
                "jimmy");

        SempVersion version = connector.checkBrokerVersion();

        // VMR version
        assertEquals("8_6VMR", connector.getSempVersion(err86vmr).getVersionString());
        // Hardware version
        assertEquals("8_2_0", connector.getSempVersion(err820).getVersionString());
    }

    @Test
    public void emptyVersionTest() throws Exception {
        Sempv1Connector connector = new Sempv1Connector(
                "http://1.1.1.1:8080/SEMP",
                "admin",
                "admin",
                "jimmy");
        // 2) empty semp-version throws
        SempVersion ver = connector.getSempVersion("<rpc-reply semp-version=\"\">");
        assertEquals(SempVersion.INVALID_VERSION_STR, ver.getVersionString());
        assertEquals(SempVersion.INVALID_VERSION, ver.getVersionNumber(), 0.0001);
    }

    // @Test
    public void localhostDefaultConfigsQueueListTest() throws Exception {
        String sempVersion = "9_2_0VMR";
        Sempv1Connector connector = new Sempv1Connector(
                "http://localhost:8080/SEMP", "admin", "admin", "localdocker"
        );
        SempRequestFactory_r9_2_0VMR factory = new SempRequestFactory_r9_2_0VMR(new ServerConfigs(new HashMap<>()));
        SempMarshaller_r9_2_0VMR marshaller = new SempMarshaller_r9_2_0VMR();
        SempReplyFactory_r9_2_0VMR replyFactory = new SempReplyFactory_r9_2_0VMR(new ServerConfigs(new HashMap<>()));

        String request =  marshaller.toRequestXml( factory.createQueueListRequest(sempVersion) );
        String response = connector.doPost(request);
        RpcReply reply = marshaller.fromReplyXml(response);
        List<Map<String, Object>> queues = replyFactory.getQueueList(reply);

        for(Map<String, Object> q : queues) {
            System.out.println(q.get(Metrics.Queue.QueueName)
                    + " : CUR:" + q.get(Metrics.Queue.MessagesSpooled)
                    + ", TOTAL:" + q.get(Metrics.Queue.TotalMessagesSpooled)
            );
        }
    }

    @Test
    public void localhostExtendedConfigsQueueStatsTest() throws Exception {
        String sempVersion = "9_2_0VMR";
        Sempv1Connector connector = new Sempv1Connector(
                "http://localhost:8080/SEMP", "admin", "admin", "localdocker"
        );
        Map<String,String> configMap = new HashMap<>();
        configMap.put(EXCLUDE_EXTENDED_STATS, "false");
        configMap.put(EXCLUDE_DISCARD_METRICS, "false");
        ServerConfigs serverConfigs = new ServerConfigs(configMap);
        SempRequestFactory_r9_2_0VMR factory = new SempRequestFactory_r9_2_0VMR(serverConfigs);
        SempMarshaller_r9_2_0VMR marshaller = new SempMarshaller_r9_2_0VMR();
        SempReplyFactory_r9_2_0VMR replyFactory = new SempReplyFactory_r9_2_0VMR(serverConfigs);

        String request =  marshaller.toRequestXml( factory.createQueueStatsListRequest(sempVersion) );
        String response = connector.doPost(request);
        System.out.println(response);
        RpcReply reply = marshaller.fromReplyXml(response);
        List<Map<String, Object>> queues = replyFactory.getQueueStatsList(reply);

        for(Map<String, Object> q : queues) {
            System.out.println(q.get(Metrics.Queue.QueueName)
                    + " : CUR:" + q.get(Metrics.Queue.MessagesSpooled)
                    + ", TOTAL:" + q.get(Metrics.Queue.TotalMessagesSpooled)
            );
        }
    }
}
