package com.appdynamics.extensions.solace.semp;

/**
 * Encapsulates all the required request and reply handling libraries around
 * a particular SEMP Connection and its required SEMP version.
 *
 * @param <Request> The SEMP version-specific Request type
 * @param <Reply> The SEMP version-specific Reply type
 */
class SempConnectionContext<Request,Reply> {
    public SempConnectionContext(SempConnector connector,
                                 SempRequestFactory<Request> reqFactory,
                                 SempReplyFactory<Reply> replyFactory,
                                 SempMarshaller<Request, Reply> marshaller,
                                 String sempVersion) {
        this.connector    = connector;
        this.reqFactory   = reqFactory;
        this.replyFactory = replyFactory;
        this.marshaller   = marshaller;
        this.schemaVersion= "soltr/"+sempVersion;
    }

    public SempConnector getConnector() {
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

    public String getSchemaVersion() {
        return schemaVersion;
    }

    final private SempConnector connector;
    final private SempRequestFactory<Request> reqFactory;
    final private SempReplyFactory<Reply> replyFactory;
    final private SempMarshaller<Request,Reply> marshaller;
    final private String schemaVersion;
}
