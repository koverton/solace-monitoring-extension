package com.appdynamics.extensions.solace.semp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Given a SempConnectionContext, the GenericSempService provides an interface to query
 * each major SEMP-based data object, tying together the requestFactory, replyFactory
 * and SempMarshaller, deferring to each of them for the type-specific implementations.
 *
 * @param <Request> type of the request object to be sent to the SEMP service
 * @param <Reply> type of the reply object to be returned by the SEMP service
 */
class GenericSempService<Request,Reply> implements SempService {
    private static final Logger logger = LoggerFactory.getLogger(GenericSempService.class);

    public GenericSempService(SempConnectionContext<Request,Reply> ctx) {
        this.ctx = ctx;
    }
    public Map<String,Object> checkGlobalStats() {
        logger.debug("<GenericSempService.checkGlobalStats>");
        Request request = ctx.getReqFactory().createGlobalStatsRequest(ctx.getSchemaVersion());
        String xml = ctx.getMarshaller().toRequestXml(request);

        String response = ctx.getConnector().doPost(xml);
        @SuppressWarnings("unchecked")
        Reply reply = ctx.getMarshaller().fromReplyXml(response);

        Map<String,Object> result;
        if (!ctx.getReplyFactory().isSuccess(reply)) {
            logger.error("Empty GlobalStats because no data in the response.");
            result = new HashMap<>();
        }
        else {
            result = ctx.getReplyFactory().getGlobalStats(reply);
        }
        logger.debug("</GenericSempService.checkGlobalStats>");
        return result;
    }

    public Map<String,Object> checkGlobalRedundancy() {
        logger.debug("<GenericSempService.checkGlobalRedundancy>");
        Request request = ctx.getReqFactory().createGlobalRedundancyRequest(ctx.getSchemaVersion());
        String xml = ctx.getMarshaller().toRequestXml(request);

        String response = ctx.getConnector().doPost(xml);
        @SuppressWarnings("unchecked")
        Reply reply = ctx.getMarshaller().fromReplyXml(response);

        Map<String,Object> result;
        if (!ctx.getReplyFactory().isSuccess(reply)) {
            logger.error("Empty GlobalRedundancy because no data in the response.");
            result = new HashMap<>();
        }
        else {
            result = ctx.getReplyFactory().getGlobalRedundancy(reply);
        }
        logger.debug("</GenericSempService.checkGlobalRedundancy>");
        return result;
    }

    public Map<String,Object> checkGlobalServiceStatus() {
        logger.debug("<GenericSempService.checkGlobalServiceStatus>");
        Request request = ctx.getReqFactory().createGlobalServiceRequest(ctx.getSchemaVersion());
        String xml = ctx.getMarshaller().toRequestXml(request);

        String response = ctx.getConnector().doPost(xml);
        @SuppressWarnings("unchecked")
        Reply reply = ctx.getMarshaller().fromReplyXml(response);

        Map<String,Object> result;
        if (!ctx.getReplyFactory().isSuccess(reply)) {
            logger.error("Empty GlobalService because no data in the response.");
            result = new HashMap<>();
        }
        else {
            result = ctx.getReplyFactory().getGlobalService(reply);
        }
        logger.debug("</GenericSempService.checkGlobalServiceStatus>");
        return result;
    }

    public Map<String,Object> checkGlobalMsgSpoolStats() {
        logger.debug("<GenericSempService.checkGlobalMsgSpoolStats>");
        Request request = ctx.getReqFactory().createGlobalMsgSpoolRequest(ctx.getSchemaVersion());
        String xml = ctx.getMarshaller().toRequestXml(request);

        String response = ctx.getConnector().doPost(xml);
        @SuppressWarnings("unchecked")
        Reply reply = ctx.getMarshaller().fromReplyXml(response);

        Map<String,Object> result;
        if (!ctx.getReplyFactory().isSuccess(reply)) {
            logger.error("Empty GlobalMsgSpool because no data in the response.");
            result = new HashMap<>();
        }
        else {
            result = ctx.getReplyFactory().getGlobalMsgSpool(reply);
        }
        logger.debug("</GenericSempService.checkGlobalMsgSpoolStats>");
        return result;
    }

    public List<Map<String,Object>> checkQueueList() {
        logger.debug("<GenericSempService.checkQueueList>");
        Request request = ctx.getReqFactory().createQueueListRequest(ctx.getSchemaVersion());
        String xml = ctx.getMarshaller().toRequestXml(request);

        String response = ctx.getConnector().doPost(xml);
        @SuppressWarnings("unchecked")
        Reply reply = ctx.getMarshaller().fromReplyXml(response);

        List<Map<String,Object>> result;
        if (!ctx.getReplyFactory().isSuccess(reply)) {
            logger.error("Empty QueueList because no data in the response.");
            result = new ArrayList<>();
        }
        else {
            result = ctx.getReplyFactory().getQueueList(reply);
        }
        logger.debug("</GenericSempService.checkQueueList>");
        return result;
    }

    public String getDisplayName() {
        return ctx.getConnector().getDisplayName();
    }


    final private SempConnectionContext<Request,Reply> ctx;
}
