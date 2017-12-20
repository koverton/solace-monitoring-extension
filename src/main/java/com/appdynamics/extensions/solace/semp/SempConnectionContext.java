package com.appdynamics.extensions.solace.semp;

public class SempConnectionContext<Request,Reply> {
    public SempConnectionContext(Sempv1Connector connector,
                                 SempRequestFactory<Request> reqFactory,
                                 SempReplyFactory<Reply> replyFactory,
                                 SempMarshaller<Request, Reply> marshaller,
                                 String sempVersion) {
        this.connector = connector;
        this.reqFactory = reqFactory;
        this.replyFactory = replyFactory;
        this.marshaller = marshaller;
        this.sempVersion = sempVersion;
    }

    public Sempv1Connector getConnector() {
        return connector;
    }

    public SempRequestFactory<Request> getReqFactory() {
        return reqFactory;
    }

    public SempReplyFactory<Reply> getReplyFactory() {
        return replyFactory;
    }

    public SempMarshaller<Request, Reply> getMarshaller() {
        return marshaller;
    }

    public String getSempVersion() {
        return sempVersion;
    }

    final private Sempv1Connector connector;
    final private SempRequestFactory<Request> reqFactory;
    final private SempReplyFactory<Reply> replyFactory;
    final private SempMarshaller<Request,Reply> marshaller;
    final private String sempVersion;
}
