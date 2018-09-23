package com.appdynamics.extensions.solace.semp;

/**
 * Converts SEMP version-specific Request and Reply objects to and from their String representations.
 *
 * @param <Request> SEMP version-specific Request object.
 * @param <Reply> SEMP version-specific Reply object.
 */
public interface SempMarshaller<Request,Reply> {

    String toRequestXml(Request req);

    Reply fromReplyXml(String reply);
}
