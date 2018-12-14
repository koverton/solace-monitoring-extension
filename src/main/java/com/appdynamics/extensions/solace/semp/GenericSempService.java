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
        logger.trace("<GenericSempService.checkGlobalRedundancy>");

        Map<String,Object> result = processor.singleLevelQuery(
                () -> ctx.getReqFactory().createGlobalRedundancyRequest(ctx.getSchemaVersion()),
                (Reply reply) -> ctx.getReplyFactory().getGlobalRedundancy(reply)
        );

        logger.trace("</GenericSempService.checkGlobalRedundancy>");
        return result;
    }

    public Map<String,Object> checkGlobalServiceStatus() {
        logger.trace("<GenericSempService.checkGlobalServiceStatus>");

        Map<String,Object> result = processor.singleLevelQuery(
                () -> ctx.getReqFactory().createGlobalServiceRequest(ctx.getSchemaVersion()),
                (Reply reply) -> ctx.getReplyFactory().getGlobalService(reply)
        );

        logger.trace("</GenericSempService.checkGlobalServiceStatus>");
        return result;
    }

    public Map<String,Object> checkGlobalMsgSpoolStats() {
        logger.trace("<GenericSempService.checkGlobalMsgSpoolStats>");

        Map<String,Object> result = processor.singleLevelQuery(
                () -> ctx.getReqFactory().createGlobalMsgSpoolRequest(ctx.getSchemaVersion()),
                (Reply reply) -> ctx.getReplyFactory().getGlobalMsgSpool(reply)
        );

        logger.trace("</GenericSempService.checkGlobalMsgSpoolStats>");
        return result;
    }

    public Map<String,Object> checkGlobalStats() {
        logger.trace("<GenericSempService.checkGlobalStats>");

        Map<String,Object> result = processor.singleLevelQuery(
                () -> ctx.getReqFactory().createGlobalStatsRequest(ctx.getSchemaVersion()),
                (Reply reply) -> ctx.getReplyFactory().getGlobalStats(reply)
        );

        logger.trace("</GenericSempService.checkGlobalStats>");
        return result;
    }

    public List<Map<String,Object>> checkMsgVpnList() {
        logger.trace("<GenericSempService.checkMsgVpnList>");

        List<Map<String,Object>> result = processor.repeatingQuery(
                () -> ctx.getReqFactory().createMsgVpnListRequest(ctx.getSchemaVersion()),
                (Reply reply) -> ctx.getReplyFactory().getMsgVpnList(reply)
        );

        logger.trace("</GenericSempService.checkMsgVpnList>");
        return result;
    }

    public List<Map<String,Object>> checkMsgVpnSpoolList() {
        logger.trace("<GenericSempService.checkMsgVpnSpoolList>");

        List<Map<String,Object>> result = processor.repeatingQuery(
                () -> ctx.getReqFactory().createMsgVpnSpoolListRequest(ctx.getSchemaVersion()),
                (Reply reply) -> ctx.getReplyFactory().getMsgVpnSpoolList(reply)
        );

        logger.trace("</GenericSempService.checkMsgVpnSpoolList>");
        return result;
    }

    public List<Map<String,Object>> checkQueueList() {
        logger.trace("<GenericSempService.checkQueueList>");

        List<Map<String,Object>> result = processor.repeatingQuery(
                () -> ctx.getReqFactory().createQueueListRequest(ctx.getSchemaVersion()),
                (Reply reply) -> ctx.getReplyFactory().getQueueList(reply)
        );

        logger.trace("</GenericSempService.checkQueueList>");
        return result;
    }

    public List<Map<String,Object>> checkQueueRatesList() {
        logger.trace("<GenericSempService.checkQueueRatesList>");

        List<Map<String,Object>> result = processor.repeatingQuery(
                () -> ctx.getReqFactory().createQueueRatesListRequest(ctx.getSchemaVersion()),
                (Reply reply) -> ctx.getReplyFactory().getQueueRatesList(reply)
        );

        logger.trace("</GenericSempService.checkQueueRatesList>");
        return result;
    }

    public List<Map<String,Object>> checkQueueStatsList() {
        logger.trace("<GenericSempService.checkQueueStatsList>");

        List<Map<String,Object>> result = processor.repeatingQuery(
                () -> ctx.getReqFactory().createQueueStatsListRequest(ctx.getSchemaVersion()),
                (Reply reply) -> ctx.getReplyFactory().getQueueStatsList(reply)
        );

        logger.trace("</GenericSempService.checkQueueStatsList>");
        return result;
    }

    public List<Map<String,Object>> checkTopicEndpointList() {
        logger.trace("<GenericSempService.checkTopicEndpointList>");

        List<Map<String,Object>> result = processor.repeatingQuery(
                () -> ctx.getReqFactory().createTopicEndpointListRequest(ctx.getSchemaVersion()),
                (Reply reply) -> ctx.getReplyFactory().getTopicEndpointList(reply)
        );

        logger.trace("</GenericSempService.checkTopicEndpointList>");
        return result;
    }

    public List<Map<String,Object>> checkTopicEndpointRatesList() {
        logger.trace("<GenericSempService.checkTopicEndpointRatesList>");

        List<Map<String,Object>> result = processor.repeatingQuery(
                () -> ctx.getReqFactory().createTopicEndpointRatesListRequest(ctx.getSchemaVersion()),
                (Reply reply) -> ctx.getReplyFactory().getTopicEndpointRatesList(reply)
        );

        logger.trace("</GenericSempService.checkTopicEndpointRatesList>");
        return result;
    }

    public List<Map<String,Object>> checkTopicEndpointStatsList() {
        logger.trace("<GenericSempService.checkTopicEndpointStatsList>");

        List<Map<String,Object>> result = processor.repeatingQuery(
                () -> ctx.getReqFactory().createTopicEndpointStatsListRequest(ctx.getSchemaVersion()),
                (Reply reply) -> ctx.getReplyFactory().getTopicEndpointStatsList(reply)
        );

        logger.trace("</GenericSempService.checkTopicEndpointStatsList>");
        return result;
    }

    public List<Map<String,Object>> checkGlobalBridgeList() {
        logger.trace("<GenericSempService.checkGlobalBridgeList>");

        List<Map<String,Object>> result = processor.repeatingQuery(
                () -> ctx.getReqFactory().createGlobalBridgeListRequest(ctx.getSchemaVersion()),
                (Reply reply) -> ctx.getReplyFactory().getGlobalBridgeList(reply)
        );

        logger.trace("</GenericSempService.checkGlobalBridgeList>");
        return result;
    }

    public String getDisplayName() {
        return ctx.getConnector().getDisplayName();
    }

    final private SempConnectionContext<Request,Reply> ctx;
    final private GenericSempProcessor<Request,Reply> processor;
}
