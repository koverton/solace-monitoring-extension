package com.appdynamics.extensions.solace.semp;

import java.util.List;
import java.util.Map;

// Given a SempConnectionContext, the GenericSempService provides an interface to
// query each major SEMP-based data object, tying together the requestFactory, replyFactory
// and SempMarshaller, deferring to each of them for the type-specific implementations.
class GenericSempService<Request,Reply> implements SempService {
    public GenericSempService(SempConnectionContext<Request,Reply> ctx) {
        this.ctx = ctx;
    }
    public Map<String,Object> checkGlobalStats() {
        // Show the appliance version
        Request request = ctx.getReqFactory().createGlobalStatsRequest(ctx.getSempVersion());
        String xml = ctx.getMarshaller().toRequestXml(request);

        String response = ctx.getConnector().doPost(xml);
        @SuppressWarnings("unchecked")
        Reply reply = ctx.getMarshaller().fromReplyXml(response);
        return ctx.getReplyFactory().getGlobalStats(reply);
    }

    public Map<String,Object> checkGlobalRedundancy() {
        Request request = ctx.getReqFactory().createGlobalRedundancyRequest(ctx.getSempVersion());
        String xml = ctx.getMarshaller().toRequestXml(request);

        String response = ctx.getConnector().doPost(xml);
        @SuppressWarnings("unchecked")
        Reply reply = ctx.getMarshaller().fromReplyXml(response);
        return ctx.getReplyFactory().getGlobalRedundancy(reply);
    }

    public Map<String,Object> checkGlobalServiceStatus() {
        Request request = ctx.getReqFactory().createGlobalServiceRequest(ctx.getSempVersion());
        String xml = ctx.getMarshaller().toRequestXml(request);

        String response = ctx.getConnector().doPost(xml);
        @SuppressWarnings("unchecked")
        Reply reply = ctx.getMarshaller().fromReplyXml(response);
        return ctx.getReplyFactory().getGlobalService(reply);
    }

    public Map<String,Object> checkGlobalMsgSpoolStats() {
        Request request = ctx.getReqFactory().createGlobalMsgSpoolRequest(ctx.getSempVersion());
        String xml = ctx.getMarshaller().toRequestXml(request);

        String response = ctx.getConnector().doPost(xml);
        @SuppressWarnings("unchecked")
        Reply reply = ctx.getMarshaller().fromReplyXml(response);
        return ctx.getReplyFactory().getGlobalMsgSpool(reply);
    }

    public List<Map<String,Object>> checkQueueList() {
        Request request = ctx.getReqFactory().createQueueListRequest(ctx.getSempVersion());
        String xml = ctx.getMarshaller().toRequestXml(request);

        String response = ctx.getConnector().doPost(xml);
        @SuppressWarnings("unchecked")
        Reply reply = ctx.getMarshaller().fromReplyXml(response);
        return ctx.getReplyFactory().getQueueList(reply);
    }

    public String getDisplayName() {
        return ctx.getConnector().getDisplayName();
    }


    final private SempConnectionContext<Request,Reply> ctx;
}
