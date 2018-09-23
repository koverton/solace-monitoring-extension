package com.appdynamics.extensions.solace.semp;

/**
 * Abstract SEMP Connector represents v1 and v2 connectors.
 *
 * Used for unit testing, logging and debugging.
 */
public interface SempConnector {

    /**
     * Configurable displayName for this connector, typically represents the server being queried.
     *
     * Used for logging and debugging.
     *
     * @return String displayName configured for this connector.
     */
    String getDisplayName();

    /**
     * POSTs SEMP query to the SEMP service and returns POST-response as a String.
     * @param request the request object String. For SEMPv1 should be XML, for SEMPv2 should be JSON.
     * @return the response object String. For SEMPv1 should be XML, for SEMPv2 should be JSON.
     */
    String doPost(final String request);

    /**
     * Proactively issues a SEMP request to the SEMP service to find the SEMP version.
     * @return SempVersion object from the parsed response.
     */
    SempVersion checkBrokerVersion();

}
