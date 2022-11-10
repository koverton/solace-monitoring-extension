package com.appdynamics.extensions.solace.semp.r9_9VMR;

import com.appdynamics.extensions.solace.ServerConfigs;
import com.appdynamics.extensions.solace.semp.Sempv1Connector;
import com.solacesystems.semp_jaxb.r9_9VMR.reply.RpcReply;
import com.solacesystems.semp_jaxb.r9_9VMR.request.Rpc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SequencedGetTest {
    private static final String SEMP_VERSION = "soltr/9_9VMR";

    private static String extractString(String input, String after, String until) {
        int frompos = input.indexOf(after) + after.length();
        int topos = input.indexOf(until);
        if (-1 == frompos || -1 == topos) return null;
        String extract = input.substring(frompos, topos);
        return extract;
    }

    //@Test
    public void theTest() throws Exception {
        SempRequestFactory_r9_9VMR reqfactory = new SempRequestFactory_r9_9VMR(new ServerConfigs(new HashMap<>()));
        SempReplyFactory_r9_9VMR respfactory = new SempReplyFactory_r9_9VMR(new ServerConfigs(new HashMap<>()));
        SempMarshaller_r9_9VMR marshaller = new SempMarshaller_r9_9VMR();

        Sempv1Connector connector = new Sempv1Connector(
                "http://192.168.56.103:8080/SEMP",
                "admin",
                "admin",
                "jimmy");

        Rpc request = reqfactory.createQueueListRequest(SEMP_VERSION, "*", null);
        String xmlrequest = marshaller.toRequestXml(request);

        List<Map<String,Object>> result = new ArrayList<>();

        while(xmlrequest != null) {
            String xmlresponse = connector.doPost(xmlrequest);
            String nextRequest = extractString(xmlresponse, "<more-cookie>", "</more-cookie>");
            RpcReply reply = marshaller.fromReplyXml(xmlresponse);
            List<Map<String,Object>> batch = respfactory.getQueueList(reply);
            result.addAll(batch);
            xmlrequest = nextRequest;
        }
    }
}
