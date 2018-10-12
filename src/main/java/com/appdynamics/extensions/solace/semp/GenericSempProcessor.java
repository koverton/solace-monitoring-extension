package com.appdynamics.extensions.solace.semp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Given a SempConnectionContext, the GenericSempProcessor provides an interface to query
 * a generic SEMP-based data object from factory methods to create the specific request
 * type and convert the specific reply type to a result.
 *
 * @param <Request> type of the request object to be sent to the SEMP service
 * @param <Reply> type of the reply object to be returned by the SEMP service
 */
class GenericSempProcessor<Request,Reply> {
    private static final Logger logger = LoggerFactory.getLogger(GenericSempProcessor.class);

    /**
     * Used by the GenericSempService to provide query-specific construction logic for requests.
     * @param <ResultType> Request instance type.
     */
    interface RequestFactory<ResultType> {
        ResultType makeRequest();
    }

    /**
     * Used by the GenericSempService to provide query-specific construction logic for replies per specific requests.
     * @param <ReplyType> Reply instance type.
     * @param <ResultType> Reply-specific result type.
     */
    interface ResultFactory<ReplyType, ResultType> {
        ResultType makeResult(ReplyType reply);
    }

    /**
     * Generates Request object via the requestFactory; marshals it to XML; POSTs the XML
     * to the SEMP Service; constructs the Reply object and generates the ResultType from
     * that Reply via the ResultFactory.
     *
     * @param requestFactory -- adapts a specific Request source query (msg-spool, vpn, redundancy, etc.) to create a generic Request instance.
     * @param resultFactory -- adapts a specific Reply source object to create a generic ResultType instance.
     * @return A mapping of metric names to metric values for the requested object.
     */
    Map<String,Object> singleLevelQuery(RequestFactory<Request> requestFactory,
                                        ResultFactory<Reply,Map<String,Object>> resultFactory) {
        Request request = requestFactory.makeRequest();
        String xml = ctx.getMarshaller().toRequestXml(request);

        String response = ctx.getConnector().doPost(xml);
        @SuppressWarnings("unchecked")
        Reply reply = ctx.getMarshaller().fromReplyXml(response);

        Map<String,Object> result;
        if (!ctx.getReplyFactory().isSuccess(reply)) {
            logger.error("Empty query because no data in the response.");
            result = new HashMap<>();
        }
        else {
            result = resultFactory.makeResult(reply);
        }
        return result;
    }


    /**
     * Generates Request object that may repeat via the requestFactory. It marshals the initial
     * query to XML and POSTs the XML to the SEMP Service. For each Reply it constructs the Reply
     * object and generates a list of ResultTypes from that Reply via the ResultFactory. Each
     * Reply also contains a potential continuation request in a 'more-cookie' element. Loops
     * over all continuation requests found, POSTing those requests and processing the results
     * the same way until no more continuations are found.
     *
     * @param requestFactory -- adapts a specific Request source query (msg-spool, vpn, redundancy, etc.) to create a generic Request instance.
     * @param resultFactory -- adapts a specific Reply source object to create a generic ResultType instance.
     * @return A mapping of metric names to metric values for the requested object.
     */
    List<Map<String,Object>> repeatingQuery(RequestFactory<Request> requestFactory,
                                            ResultFactory<Reply,List<Map<String,Object>>> resultFactory) {
        Request request = requestFactory.makeRequest();
        String requestXml = ctx.getMarshaller().toRequestXml(request);

        List<Map<String, Object>> result = new ArrayList<>();
        while(requestXml != null) {
            String replyXml = ctx.getConnector().doPost(requestXml);
            @SuppressWarnings("unchecked")
            Reply reply = ctx.getMarshaller().fromReplyXml(replyXml);

            if (!ctx.getReplyFactory().isSuccess(reply)) {
                logger.error("Empty query because no data in the response.");
            } else {
                result.addAll(resultFactory.makeResult(reply));
            }
            requestXml = extractMoreCookie(replyXml);
        }
        return result;
    }

    private static String extractMoreCookie(String input) {
        int frompos = input.indexOf("<more-cookie>") + "<more-cookie>".length();
        int topos   = input.indexOf("</more-cookie>");
        if (-1 == frompos || -1 == topos) return null;
        return input.substring(frompos, topos);
    }


    GenericSempProcessor(SempConnectionContext<Request,Reply> ctx) {
        this.ctx = ctx;
    }

    final private SempConnectionContext<Request,Reply> ctx;
}
