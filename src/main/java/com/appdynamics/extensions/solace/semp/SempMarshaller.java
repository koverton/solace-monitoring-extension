package com.appdynamics.extensions.solace.semp;

public interface SempMarshaller<Request,Reply> {

    String toRequestXml(Request req);

    Reply fromReplyXml(String reply);
}
