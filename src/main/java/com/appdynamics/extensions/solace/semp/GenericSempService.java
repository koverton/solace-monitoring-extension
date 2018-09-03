package com.appdynamics.extensions.solace.semp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    GenericSempService(SempConnectionContext<Request,Reply> ctx) {
        this.ctx = ctx;
        this.processor = new GenericSempProcessor<>(ctx);
    }

    public Map<String,Object> checkGlobalRedundancy() {
        logger.debug("<GenericSempService.checkGlobalRedundancy>");

        Map<String,Object> result = processor.singleLevelQuery(
                () -> ctx.getReqFactory().createGlobalRedundancyRequest(ctx.getSchemaVersion()),
                (Reply reply) -> ctx.getReplyFactory().getGlobalRedundancy(reply)
        );

        logger.debug("</GenericSempService.checkGlobalRedundancy>");
        return result;
    }

    public Map<String,Object> checkGlobalServiceStatus() {
        logger.debug("<GenericSempService.checkGlobalServiceStatus>");

        Map<String,Object> result = processor.singleLevelQuery(
                () -> ctx.getReqFactory().createGlobalServiceRequest(ctx.getSchemaVersion()),
                (Reply reply) -> ctx.getReplyFactory().getGlobalService(reply)
        );

        logger.debug("</GenericSempService.checkGlobalServiceStatus>");
        return result;
    }

    public Map<String,Object> checkGlobalMsgSpoolStats() {
        logger.debug("<GenericSempService.checkGlobalMsgSpoolStats>");

        Map<String,Object> result = processor.singleLevelQuery(
                () -> ctx.getReqFactory().createGlobalMsgSpoolRequest(ctx.getSchemaVersion()),
                (Reply reply) -> ctx.getReplyFactory().getGlobalMsgSpool(reply)
        );

        logger.debug("</GenericSempService.checkGlobalMsgSpoolStats>");
        return result;
    }

    public Map<String,Object> checkGlobalStats() {
        logger.debug("<GenericSempService.checkGlobalStats>");

        Map<String,Object> result = processor.singleLevelQuery(
                () -> ctx.getReqFactory().createGlobalStatsRequest(ctx.getSchemaVersion()),
                (Reply reply) -> ctx.getReplyFactory().getGlobalStats(reply)
        );

        logger.debug("</GenericSempService.checkGlobalStats>");
        return result;
    }

    public List<Map<String,Object>> checkMsgVpnList() {
        logger.debug("<GenericSempService.checkMsgVpnList>");

        List<Map<String,Object>> result = processor.repeatingQuery(
                () -> ctx.getReqFactory().createMsgVpnListRequest(ctx.getSchemaVersion()),
                (Reply reply) -> ctx.getReplyFactory().getMsgVpnList(reply)
        );

        logger.debug("</GenericSempService.checkMsgVpnList>");
        return result;
    }

    public List<Map<String,Object>> checkQueueList() {
        logger.debug("<GenericSempService.checkQueueList>");

        List<Map<String,Object>> result = processor.repeatingQuery(
                () -> ctx.getReqFactory().createQueueListRequest(ctx.getSchemaVersion()),
                (Reply reply) -> ctx.getReplyFactory().getQueueList(reply)
        );

        logger.debug("</GenericSempService.checkQueueList>");
        return result;
    }

    public List<Map<String,Object>> checkQueueRatesList() {
        logger.debug("<GenericSempService.checkQueueRatesList>");

        List<Map<String,Object>> result = processor.repeatingQuery(
                () -> ctx.getReqFactory().createQueueRatesListRequest(ctx.getSchemaVersion()),
                (Reply reply) -> ctx.getReplyFactory().getQueueRatesList(reply)
        );

        logger.debug("</GenericSempService.checkQueueRatesList>");
        return result;
    }

    public List<Map<String,Object>> checkTopicEndpointList() {
        logger.debug("<GenericSempService.checkTopicEndpointList>");

        List<Map<String,Object>> result = processor.repeatingQuery(
                () -> ctx.getReqFactory().createTopicEndpointListRequest(ctx.getSchemaVersion()),
                (Reply reply) -> ctx.getReplyFactory().getTopicEndpointList(reply)
        );

        logger.debug("</GenericSempService.checkTopicEndpointList>");
        return result;
    }

    public List<Map<String,Object>> checkTopicEndpointRatesList() {
        logger.debug("<GenericSempService.checkTopicEndpointRatesList>");

        List<Map<String,Object>> result = processor.repeatingQuery(
                () -> ctx.getReqFactory().createTopicEndpointRatesListRequest(ctx.getSchemaVersion()),
                (Reply reply) -> ctx.getReplyFactory().getTopicEndpointRatesList(reply)
        );

        logger.debug("</GenericSempService.checkTopicEndpointRatesList>");
        return result;
    }

    public List<Map<String,Object>> checkGlobalBridgeList() {
        logger.debug("<GenericSempService.checkGlobalBridgeList>");

        List<Map<String,Object>> result = processor.repeatingQuery(
                () -> ctx.getReqFactory().createGlobalBridgeListRequest(ctx.getSchemaVersion()),
                (Reply reply) -> ctx.getReplyFactory().getGlobalBridgeList(reply)
        );

        logger.debug("</GenericSempService.checkGlobalBridgeList>");
        return result;
    }

    public String getDisplayName() {
        return ctx.getConnector().getDisplayName();
    }

    final private SempConnectionContext<Request,Reply> ctx;
    final private GenericSempProcessor<Request,Reply> processor;
}
