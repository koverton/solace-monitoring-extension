package com.appdynamics.extensions.solace.semp.r9_2_0;

import com.appdynamics.extensions.solace.MonitorConfigs;
import com.appdynamics.extensions.solace.ServerConfigs;
import com.appdynamics.extensions.solace.semp.Metrics;
import com.solacesystems.semp_jaxb.r9_2_0.reply.RpcReply;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.xml.bind.JAXBException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static com.appdynamics.extensions.solace.DerivedMetricsLogic.deriveMetrics;
import static com.appdynamics.extensions.solace.MonitorConfigs.EXCLUDE_EXTENDED_STATS;
import static org.junit.Assert.assertEquals;

public class DerivedMetricsTest {

    private String readFile(String filename) throws Exception {
        String SEMP_VERSION = "r9_2_0";
        String replyFile = "resources/" + SEMP_VERSION + "/" + filename;
        return new String(Files.readAllBytes(Paths.get(replyFile)));
    }
    private static SempMarshaller_r9_2_0 marshaller;
    private static SempReplyFactory_r9_2_0 factory;

    @BeforeClass
    public static void setup() throws JAXBException {
        marshaller = new SempMarshaller_r9_2_0();
        Map<String,String> exclusionsMap = new HashMap<>();
        exclusionsMap.put(EXCLUDE_EXTENDED_STATS, "false");
        factory = new SempReplyFactory_r9_2_0(new ServerConfigs(exclusionsMap));
    }

    @Test
    public void derivedPrimaryActiveTest() throws Exception {
        RpcReply reply = marshaller.fromReplyXml(readFile("show-redundancy.detail.MW01P.xml"));
        Map<String, Object> redundancy = factory.getGlobalRedundancy(reply);
        reply = marshaller.fromReplyXml(readFile("show-msgspool.detail.MW01P.xml"));
        Map<String, Object> spool = factory.getGlobalMsgSpool(reply);
        reply = marshaller.fromReplyXml(readFile("show-service.MW01P.xml"));
        Map<String, Object> service = factory.getGlobalService(reply);
        Map<String, Object> derived = deriveMetrics(MonitorConfigs.RedundancyModel.REDUNDANT,
                service, redundancy, spool);
        assertEquals(1, derived.get(Metrics.Derived.DataSvcOk));
        assertEquals(1, derived.get(Metrics.Derived.MsgSpoolOk));
    }

    @Test
    public void derivedBackupStandbyTest() throws Exception {
        RpcReply reply = marshaller.fromReplyXml(readFile("show-redundancy.detail.MW02P.xml"));
        Map<String, Object> redundancy = factory.getGlobalRedundancy(reply);
        reply = marshaller.fromReplyXml(readFile("show-msgspool.detail.MW02P.xml"));
        Map<String, Object> spool = factory.getGlobalMsgSpool(reply);
        reply = marshaller.fromReplyXml(readFile("show-service.MW02P.xml"));
        Map<String, Object> service = factory.getGlobalService(reply);
        Map<String, Object> derived = deriveMetrics(MonitorConfigs.RedundancyModel.REDUNDANT,
                service, redundancy, spool);
        assertEquals(1, derived.get(Metrics.Derived.DataSvcOk));
        assertEquals(1, derived.get(Metrics.Derived.MsgSpoolOk));
    }
}
