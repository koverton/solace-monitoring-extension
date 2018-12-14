package com.appdynamics.extensions.solace.semp;

import java.nio.file.Files;
import java.nio.file.Paths;

public class MockSempConnector implements SempConnector {
    final private String sempVersion;

    public MockSempConnector(String sempVersion) {
        this.sempVersion = sempVersion;
    }

    @Override
    public String getDisplayName() {
        return "Mock Connector " + sempVersion;
    }

    @Override
    public String doPost(String request) {
        try {
            request = request.replace("\n", "")
                    .replace("\r", "")
                    .replaceAll(">[ \t]+<", "><");
            if (request.contains("><show><version"))
                return readFile("show-version.xml");
            else if (request.contains("<show><stats><client>"))
                return readFile("show-stats.client.detail.xml");
            else if (request.contains("<show><message-spool><detail/>"))
                return readFile("show-message-spool.detail-backup.active.xml");
            else if (request.contains("<show><redundancy>"))
                return readFile("show-redundancy.detail-standalone.inactive.xml");
            else if (request.contains("<show><service/>"))
                return readFile("show-service.xml");
            else if (request.contains("<show><message-vpn><vpn-name>*</vpn-name><stats/>"))
                return readFile("show-vpn.stats.xml");
            else if (request.contains("<show><message-spool><vpn-name>*</vpn-name>"))
                return readFile("show-vpn.spool.xml");
            else if (request.contains("<show><queue><name>*</name><durable/><detail/>"))
                return readFile("show-queues.detail.xml");
            else if (request.contains("<show><queue><name>*</name><rates/><durable/>"))
                return readFile("show-queues.rates.xml");
            else if (request.contains("<show><queue><name>*</name><stats/><durable/>"))
                return readFile("show-queues.stats.xml");
            else if (request.contains("<show><topic-endpoint><name>*</name><durable/>"))
                return readFile("show-topicendpoints.detail.xml");
            else if (request.contains("<show><topic-endpoint><name>*</name><rates/><durable/>"))
                return readFile("show-topicendpoints.rates.xml");
            else if (request.contains("<show><topic-endpoint><name>*</name><stats/><durable/>"))
                return readFile("show-topicendpoints.stats.xml");
            else if (request.contains("<show><bridge><bridge-name-pattern>*</bridge-name-pattern></bridge>"))
                return readFile("show-bridges.xml");
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public SempVersion checkBrokerVersion() {
        return new SempVersion("soltr/" + sempVersion);
    }


    private String readFile(String filename) throws Exception {
        String replyFile = "resources/r" + sempVersion + "/" + filename;
        return new String(Files.readAllBytes(Paths.get(replyFile)));
    }
}
