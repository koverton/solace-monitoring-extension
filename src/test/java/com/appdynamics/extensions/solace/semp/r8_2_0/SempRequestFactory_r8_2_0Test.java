package com.appdynamics.extensions.solace.semp.r8_2_0;

import com.solacesystems.semp_jaxb.r8_2_0.request.Rpc;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.xml.bind.JAXBException;

import static org.junit.Assert.assertEquals;

public class SempRequestFactory_r8_2_0Test {
    private static final String SEMP_VERSION = "soltr/8_2_0";
    private static SempRequestFactory_r8_2_0 factory;
    private static SempMarshaller_r8_2_0 marshaller;

    private static String xmltag = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>";

    @BeforeClass
    public static void setup() throws JAXBException {
        factory = new SempRequestFactory_r8_2_0();
        marshaller = new SempMarshaller_r8_2_0();
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
    public void getQueueListTest() {
        Rpc request = factory.createQueueListRequest(SEMP_VERSION);
        String xml = marshaller.toRequestXml(request);
        System.out.println(xml);
        assertEquals(xmltag+"<rpc semp-version=\""+SEMP_VERSION+"\"><show><queue><name>*</name><detail/></queue></show></rpc>", xml);
    }}
