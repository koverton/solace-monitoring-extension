package com.appdynamics.extensions.solace.semp.r8_6VMR;

import com.appdynamics.extensions.solace.ServerConfigs;
import com.solacesystems.semp_jaxb.r8_6VMR.request.Rpc;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.xml.bind.*;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class SempRequestFactory_r8_6VMRTest {
    private static final String SEMP_VERSION = "soltr/8_6VMR";
    private static SempRequestFactory_r8_6VMR factory;
    private static SempMarshaller_r8_6VMR marshaller;

    private static String xmltag = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";

    @BeforeClass
    public static void setup() throws JAXBException {
        factory = new SempRequestFactory_r8_6VMR(new ServerConfigs(new HashMap<>()));
        marshaller = new SempMarshaller_r8_6VMR();
    }

    @Test
    public void getVersionTest() {
        Rpc request = factory.createVersionRequest(SEMP_VERSION);
        String xml = marshaller.toRequestXml(request);
        assertEquals(xmltag+"<rpc semp-version=\"" + SEMP_VERSION + "\"><show><version xsi:type=\"keywordType\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"/></show></rpc>", xml);
    }

    @Test
    public void getGlobalStatsTest() {
        Rpc request = factory.createGlobalStatsRequest(SEMP_VERSION);
        String xml = marshaller.toRequestXml(request);
        assertEquals(xmltag+"<rpc semp-version=\"" + SEMP_VERSION + "\"><show><stats><client><detail/></client></stats></show></rpc>", xml);
    }

    @Test
    public void getGlobalMsgSpoolTest() {
        Rpc request = factory.createGlobalMsgSpoolRequest(SEMP_VERSION);
        String xml = marshaller.toRequestXml(request);
        assertEquals(xmltag+"<rpc semp-version=\"" + SEMP_VERSION + "\"><show><message-spool><detail/></message-spool></show></rpc>", xml);
    }

    @Test
    public void getGlobalRedundancyTest() {
        Rpc request = factory.createGlobalRedundancyRequest(SEMP_VERSION);
        String xml = marshaller.toRequestXml(request);
        assertEquals(xmltag+"<rpc semp-version=\"" + SEMP_VERSION + "\"><show><redundancy><detail/></redundancy></show></rpc>", xml);
    }

    @Test
    public void getGlobalServiceTest() {
        Rpc request = factory.createGlobalServiceRequest(SEMP_VERSION);
        String xml = marshaller.toRequestXml(request);
        assertEquals(xmltag+"<rpc semp-version=\"" + SEMP_VERSION + "\"><show><service/></show></rpc>", xml);
    }

    @Test
    public void getVpnStatsListTest() {
        Rpc request = factory.createMsgVpnListRequest(SEMP_VERSION);
        String xml = marshaller.toRequestXml(request);
        assertEquals(xmltag+"<rpc semp-version=\""+SEMP_VERSION+"\"><show><message-vpn><vpn-name>*</vpn-name><stats/><count/><num-elements>100</num-elements></message-vpn></show></rpc>", xml);
    }

    @Test
    public void getVpnSpoolListTest() {
        Rpc request = factory.createMsgVpnSpoolListRequest(SEMP_VERSION);
        String xml = marshaller.toRequestXml(request);
        assertEquals(xmltag+"<rpc semp-version=\""+SEMP_VERSION+"\"><show><message-spool><vpn-name>*</vpn-name><detail/></message-spool></show></rpc>", xml);
    }

    @Test
    public void getQueueListTest() {
        Rpc request = factory.createQueueListRequest(SEMP_VERSION);
        String xml = marshaller.toRequestXml(request);
        assertEquals(xmltag+"<rpc semp-version=\""+SEMP_VERSION+"\"><show><queue><name>*</name><durable/><detail/><count/><num-elements>100</num-elements></queue></show></rpc>", xml);
    }

    @Test
    public void getQueueRatesListTest() {
        Rpc request = factory.createQueueRatesListRequest(SEMP_VERSION);
        String xml = marshaller.toRequestXml(request);
        assertEquals(xmltag+"<rpc semp-version=\""+SEMP_VERSION+"\"><show><queue><name>*</name><rates/><durable/><count/><num-elements>100</num-elements></queue></show></rpc>", xml);
    }

    @Test
    public void getQueueStatListTest() {
        Rpc request = factory.createQueueStatsListRequest(SEMP_VERSION);
        String xml = marshaller.toRequestXml(request);
        assertEquals(xmltag+"<rpc semp-version=\""+SEMP_VERSION+"\"><show><queue><name>*</name><stats/><durable/><count/><num-elements>100</num-elements></queue></show></rpc>", xml);
    }

    @Test
    public void getTopicEndpointListTest() {
        Rpc request = factory.createTopicEndpointListRequest(SEMP_VERSION);
        String xml = marshaller.toRequestXml(request);
        assertEquals(xmltag+"<rpc semp-version=\""+SEMP_VERSION+"\"><show><topic-endpoint><name>*</name><durable/><detail/><count/><num-elements>100</num-elements></topic-endpoint></show></rpc>", xml);
    }

    @Test
    public void getTopicEndpointRatesListTest() {
        Rpc request = factory.createTopicEndpointRatesListRequest(SEMP_VERSION);
        String xml = marshaller.toRequestXml(request);
        assertEquals(xmltag+"<rpc semp-version=\""+SEMP_VERSION+"\"><show><topic-endpoint><name>*</name><rates/><durable/><count/><num-elements>100</num-elements></topic-endpoint></show></rpc>", xml);
    }

    @Test
    public void getTopicEndpointStatsListTest() {
        Rpc request = factory.createTopicEndpointStatsListRequest(SEMP_VERSION);
        String xml = marshaller.toRequestXml(request);
        assertEquals(xmltag+"<rpc semp-version=\""+SEMP_VERSION+"\"><show><topic-endpoint><name>*</name><stats/><durable/><count/><num-elements>100</num-elements></topic-endpoint></show></rpc>", xml);
    }

    @Test
    public void getGlobalBridgeListTest() {
        Rpc request = factory.createGlobalBridgeListRequest(SEMP_VERSION);
        String xml = marshaller.toRequestXml(request);
        assertEquals(xmltag+"<rpc semp-version=\""+SEMP_VERSION+"\"><show><bridge><bridge-name-pattern>*</bridge-name-pattern></bridge></show></rpc>", xml);
    }
}
