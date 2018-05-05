package com.appdynamics.extensions.solace.semp.r8_6VMR;

import com.appdynamics.extensions.solace.semp.TopicEndpointMetrics;
import com.appdynamics.extensions.solace.semp.*;
import com.solacesystems.semp_jaxb.r8_6VMR.reply.QueueType;
import com.solacesystems.semp_jaxb.r8_6VMR.reply.RpcReply;
import com.solacesystems.semp_jaxb.r8_6VMR.reply.SolStatsType;
import com.solacesystems.semp_jaxb.r8_6VMR.reply.RpcReply.Rpc.Show.Bridge.Bridges.Bridge2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SempReplyFactory_r8_6VMR implements SempReplyFactory<RpcReply> {
    private static final Logger logger = LoggerFactory.getLogger(SempReplyFactory_r8_6VMR.class);

    public boolean isSuccess(RpcReply reply) {
        if (reply.getParseError() != null && reply.getParseError().length()!=0)
            return false;
        return reply.getExecuteResult().getCode().equals("ok");
    }

    public RpcReply.Rpc.Show.Version getVersion(RpcReply reply) {
        return reply.getRpc()
                .getShow()
                .getVersion();
    }

    public Map<String,Object> getGlobalStats(RpcReply reply) {
        SolStatsType stats = reply.getRpc()
                .getShow()
                .getStats()
                .getClient()
                .getGlobal()
                .getStats();

        Map<String,Object> result = new HashMap<>();
        result.put(StatisticalMetrics.CurrentIngressRatePerSecond, stats.getCurrentIngressRatePerSecond());
        result.put(StatisticalMetrics.CurrentEgressRatePerSecond, stats.getCurrentEgressRatePerSecond());
        result.put(StatisticalMetrics.CurrentIngressByteRatePerSecond, stats.getCurrentIngressByteRatePerSecond());
        result.put(StatisticalMetrics.CurrentEgressByteRatePerSecond, stats.getCurrentEgressByteRatePerSecond());

        result.put(StatisticalMetrics.CurrentIngressCompressedRatePerSecond, stats.getZipStats().getCurrentIngressCompressedRatePerSecond());
        result.put(StatisticalMetrics.CurrentEgressCompressedRatePerSecond, stats.getZipStats().getCurrentEgressCompressedRatePerSecond());
        result.put(StatisticalMetrics.IngressCompressionRatio, stats.getZipStats().getIngressCompressionRatio());
        result.put(StatisticalMetrics.EgressCompressionRatio, stats.getZipStats().getEgressCompressionRatio());

        result.put(StatisticalMetrics.CurrentIngressSslRatePerSecond, stats.getSslStats().getCurrentIngressSslRatePerSecond());
        result.put(StatisticalMetrics.CurrentEgressSslRatePerSecond, stats.getSslStats().getCurrentEgressSslRatePerSecond());

        result.put(StatisticalMetrics.TotalIngressDiscards, stats.getIngressDiscards().getTotalIngressDiscards());
        result.put(StatisticalMetrics.NoSubscriptionMatch, stats.getIngressDiscards().getNoSubscriptionMatch());
        result.put(StatisticalMetrics.TopicParseError, stats.getIngressDiscards().getTopicParseError());
        result.put(StatisticalMetrics.ParseError, stats.getIngressDiscards().getParseError());
        result.put(StatisticalMetrics.MsgTooBig, stats.getIngressDiscards().getMsgTooBig());
        result.put(StatisticalMetrics.TtlExceeded, stats.getIngressDiscards().getTtlExceeded());
        result.put(StatisticalMetrics.WebParseError, stats.getIngressDiscards().getWebParseError());
        result.put(StatisticalMetrics.PublishTopicAcl, stats.getIngressDiscards().getPublishTopicAcl());
        result.put(StatisticalMetrics.MsgSpoolDiscards, stats.getIngressDiscards().getMsgSpoolDiscards());
        result.put(StatisticalMetrics.IngressMessagePromotionCongestion, stats.getIngressDiscards().getMessagePromotionCongestion());
        result.put(StatisticalMetrics.IngressMessageSpoolCongestion, stats.getIngressDiscards().getMessageSpoolCongestion());

        result.put(StatisticalMetrics.TotalEgressDiscards, stats.getEgressDiscards().getTotalEgressDiscards());
        result.put(StatisticalMetrics.TransmitCongestion, stats.getEgressDiscards().getTransmitCongestion());
        result.put(StatisticalMetrics.CompressionCongestion, stats.getEgressDiscards().getCompressionCongestion());
        result.put(StatisticalMetrics.MessageElided, stats.getEgressDiscards().getMessageElided());
        result.put(StatisticalMetrics.PayloadCouldNotBeFormatted, stats.getEgressDiscards().getPayloadCouldNotBeFormatted());
        result.put(StatisticalMetrics.EgressMessagePromotionCongestion, stats.getEgressDiscards().getMessagePromotionCongestion());
        result.put(StatisticalMetrics.EgressMessageSpoolCongestion, stats.getEgressDiscards().getMessageSpoolCongestion());
        result.put(StatisticalMetrics.MsgSpoolEgressDiscards, stats.getEgressDiscards().getMsgSpoolEgressDiscards());

        return result;
    }

    public Map<String,Object> getGlobalMsgSpool(RpcReply reply) {
        RpcReply.Rpc.Show.MessageSpool.MessageSpoolInfo stats = reply.getRpc()
                .getShow()
                .getMessageSpool()
                .getMessageSpoolInfo();

        Map<String,Object> result = new HashMap<>();
        result.put(MsgSpoolMetrics.IsEnabled, stats.getConfigStatus().startsWith("Enabled") ? 1:0);
        result.put(MsgSpoolMetrics.IsActive, stats.getOperationalStatus().equals("AD-Active") ? 1:0);
        result.put(MsgSpoolMetrics.IsStandby, stats.getOperationalStatus().equals("AD-Standby") ? 1:0);
        result.put(MsgSpoolMetrics.IsDatapathUp, stats.isDatapathUp() ? 1:0);
        result.put(MsgSpoolMetrics.IsSynchronized, stats.getSynchronizationStatus().equals("Synced") ? 1:0);
        result.put(MsgSpoolMetrics.MessageCountUtilizationPct,
                safeParseDouble("MessageCountUtilizationPct", stats.getMessageCountUtilizationPercentage()).longValue());
        result.put(MsgSpoolMetrics.TransactionResourceUtilizationPct,
                safeParseDouble("TransactionResourceUtilizationPct", stats.getTransactionResourceUtilizationPercentage()).longValue());
        result.put(MsgSpoolMetrics.TransactedSessionCountUtilizationPct,
                safeParseDouble("TransactedSessionCountUtilizationPct", stats.getTransactedSessionCountUtilizationPercentage()).longValue());
        result.put(MsgSpoolMetrics.DeliveredUnackedMsgsUtilizationPct,
                safeParseDouble("DeliveredUnackedMsgsUtilizationPct", stats.getDeliveredUnackedMsgsUtilizationPercentage()).longValue());
        result.put(MsgSpoolMetrics.SpoolFilesUtilizationPercentage,
                safeParseDouble("SpoolFilesUtilizationPercentage", stats.getSpoolFilesUtilizationPercentage()).longValue());
        return result;
    }

    public Map<String,Object> getGlobalRedundancy(RpcReply reply) {
        RpcReply.Rpc.Show.Redundancy redundancy = reply.getRpc()
                .getShow()
                .getRedundancy();

        Map<String,Object> result = new HashMap<>();
        result.put(RedundancyMetrics.ConfiguredStatus, redundancy.getConfigStatus().equals("Enabled") ? 1:0);
        result.put(RedundancyMetrics.OperationalStatus, redundancy.getRedundancyStatus().equals("Up") ? 1:0);
        //result.put(RedundancyMetrics.IsPrimary, redundancy.getActiveStandbyRole().equals("Primary") ? 1:0);
        // if ((Integer) result.get(RedundancyMetrics.IsPrimary) == 1) {
        if (redundancy.getVirtualRouters()
                .getPrimary()
                .getStatus()
                .getActivity()
                .equals("Local Active") ||
                redundancy.getVirtualRouters()
                        .getBackup()
                        .getStatus()
                        .getActivity()
                        .equals("Local Active")) {
            result.put(RedundancyMetrics.IsActive, 1);
        }
        else {
            result.put(RedundancyMetrics.IsActive, 0);
        }
        return result;
    }

    public Map<String,Object> getGlobalService(RpcReply reply) {
        RpcReply.Rpc.Show.Service service = reply.getRpc()
                .getShow()
                .getService();

        Map<String,Object> result = new HashMap<>();
        for (RpcReply.Rpc.Show.Service.Services.Service2 svc : service.getServices().getService()) {
            if (svc.getName().equals("SMF")) {
                result.put(ServiceMetrics.SmfPort,svc.getListenPort().intValue());
                result.put(ServiceMetrics.SmfPortUp, svc.getListenPortOperationalStatus().equals("Up") ? 1:0 );

                result.put(ServiceMetrics.SmfCompressedPort,svc.getCompressionListenPort().intValue());
                result.put(ServiceMetrics.SmfCompressedPortUp, svc.getCompressionListenPortOperationalStatus().equals("Up") ? 1:0 );

                result.put(ServiceMetrics.SmfSslPort,(int)svc.getSsl().getListenPort());
                result.put(ServiceMetrics.SmfSslPortUp, svc.getSsl().getListenPortOperationalStatus().equals("Up") ? 1:0 );
            }
            else if (svc.getName().equals("WEB")) {
                result.put(ServiceMetrics.WebPort,svc.getListenPort().intValue());
                result.put(ServiceMetrics.WebPortUp, svc.getListenPortOperationalStatus().equals("Up") ? 1:0 );

                result.put(ServiceMetrics.WebSslPort,(int)svc.getSsl().getListenPort());
                result.put(ServiceMetrics.WebSslPortUp, svc.getSsl().getListenPortOperationalStatus().equals("Up") ? 1:0 );
            }
        }
        return result;
    }

    public List<Map<String, Object>> getQueueList(RpcReply reply) {
        List<Map<String,Object>> results = new ArrayList<>();
        List<QueueType> queues = reply.getRpc()
                .getShow()
                .getQueue()
                .getQueues()
                .getQueue();
        for(QueueType q : queues) {
            Map<String, Object> result = new HashMap<>();
            result.put(QueueMetrics.QueueName, q.getName());
            result.put(QueueMetrics.VpnName, q.getInfo().getMessageVpn());
            result.put(QueueMetrics.IsIngressEnabled, q.getInfo().getIngressConfigStatus().equals("Up") ? 1 : 0);
            result.put(QueueMetrics.IsEgressEnabled, q.getInfo().getEgressConfigStatus().equals("Up") ? 1 : 0);
            result.put(QueueMetrics.IsDurable, q.getInfo().isDurable() ? 1 : 0);
            result.put(QueueMetrics.QuotaInMB, q.getInfo().getQuota().longValue());
            result.put(QueueMetrics.MessagesEnqueued, q.getInfo().getNumMessagesSpooled().intValue());
            result.put(QueueMetrics.UsageInMB, q.getInfo().getCurrentSpoolUsageInMb());
            result.put(QueueMetrics.ConsumerCount, q.getInfo().getBindCount().intValue());
            results.add(result);
        }
        return results;
    }

    @Override
    public List<Map<String, Object>> getTopicEndpointList(RpcReply rpcReply) {
        List<Map<String,Object>> results = new ArrayList<>();
        List<RpcReply.Rpc.Show.TopicEndpoint.TopicEndpoints.TopicEndpoint2> endpoints = rpcReply.getRpc()
                .getShow()
                .getTopicEndpoint()
                .getTopicEndpoints()
                .getTopicEndpoint();
        for(RpcReply.Rpc.Show.TopicEndpoint.TopicEndpoints.TopicEndpoint2 t : endpoints) {
            Map<String, Object> result = new HashMap<>();
            result.put(TopicEndpointMetrics.TopicEndpointName, t.getName());
            result.put(TopicEndpointMetrics.VpnName, t.getInfo().getMessageVpn());
            result.put(TopicEndpointMetrics.IsIngressEnabled, t.getInfo().getIngressConfigStatus().equals("Up") ? 1 : 0);
            result.put(TopicEndpointMetrics.IsEgressEnabled, t.getInfo().getEgressConfigStatus().equals("Up") ? 1 : 0);
            result.put(TopicEndpointMetrics.IsDurable, t.getInfo().isDurable() ? 1 : 0);
            result.put(TopicEndpointMetrics.QuotaInMB, t.getInfo().getQuota().longValue());
            result.put(TopicEndpointMetrics.MessagesSpooled, t.getInfo().getNumMessagesSpooled().intValue());
            result.put(TopicEndpointMetrics.UsageInMB, t.getInfo().getCurrentSpoolUsageInMb());
            result.put(TopicEndpointMetrics.ConsumerCount, t.getInfo().getBindCount().intValue());
            results.add(result);
        }
        return results;
    }


    public List<Map<String,Object>> getGlobalBridgeList(RpcReply reply) {
        List<Map<String,Object>> results = new ArrayList<>();
        List<Bridge2> bridges = reply.getRpc()
                .getShow()
                .getBridge()
                .getBridges()
                .getBridge();
        for(Bridge2 b : bridges) {
            // Skip the generated local side of bridges that the user doesn't configure
            if (b.getAdminState().equals("N/A"))
                continue;
            Map<String, Object> result = new HashMap<>();
            result.put(BridgeMetrics.BridgeName, b.getBridgeName());
            result.put(BridgeMetrics.VpnName, b.getLocalVpnName());
            result.put(BridgeMetrics.IsEnabled, b.getAdminState().equals("Enabled") ? 1:0);
            result.put(BridgeMetrics.IsConnected, b.getConnectionEstablisher().equals("Local") ? 1:0);
            result.put(BridgeMetrics.IsInSync, b.getInboundOperationalState().equals("Ready-InSync") ? 1:0);
            result.put(BridgeMetrics.IsBoundToBridgeQueue, b.getQueueOperationalState().equals("Bound") ? 1:0);
            result.put(BridgeMetrics.UptimeInSecs, b.getConnectionUptimeInSeconds().longValue());
            results.add(result);
        }
        return results;
    }

    private Double safeParseDouble(String fieldName, String input) {
        try {
            return Double.parseDouble(input);
        }
        catch(NumberFormatException ex) {
            logger.error("NumberFormatException parsing field {} value {}", fieldName, input);
        }
        return 0.0;
    }
}
