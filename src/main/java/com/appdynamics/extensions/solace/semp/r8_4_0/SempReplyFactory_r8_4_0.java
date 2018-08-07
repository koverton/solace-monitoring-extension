package com.appdynamics.extensions.solace.semp.r8_4_0;

import com.appdynamics.extensions.solace.semp.*;
import com.solacesystems.semp_jaxb.r8_2_0.reply.QueueType;
import com.solacesystems.semp_jaxb.r8_2_0.reply.RpcReply;
import com.solacesystems.semp_jaxb.r8_2_0.reply.SolStatsType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SempReplyFactory_r8_4_0 implements SempReplyFactory<RpcReply> {
    private static final Logger logger = LoggerFactory.getLogger(SempReplyFactory_r8_4_0.class);

    public boolean isSuccess(RpcReply reply) {
        if (reply.getParseError() != null && reply.getParseError().length() != 0)
            return false;
        return reply.getExecuteResult().getCode().equals("ok");
    }

    public RpcReply.Rpc.Show.Version getVersion(RpcReply reply) {
        return reply.getRpc()
                .getShow()
                .getVersion();
    }

    public Map<String, Object> getGlobalStats(RpcReply reply) {
        SolStatsType stats = reply.getRpc()
                .getShow()
                .getStats()
                .getClient()
                .getGlobal()
                .getStats();

        Map<String, Object> result = new HashMap<>();
        result.put(Metrics.Statistics.CurrentIngressRatePerSecond, stats.getCurrentIngressRatePerSecond());
        result.put(Metrics.Statistics.CurrentEgressRatePerSecond, stats.getCurrentEgressRatePerSecond());
        result.put(Metrics.Statistics.CurrentIngressByteRatePerSecond, stats.getCurrentIngressByteRatePerSecond());
        result.put(Metrics.Statistics.CurrentEgressByteRatePerSecond, stats.getCurrentEgressByteRatePerSecond());

        result.put(Metrics.Statistics.CurrentIngressCompressedRatePerSecond, stats.getZipStats().getCurrentIngressCompressedRatePerSecond());
        result.put(Metrics.Statistics.CurrentEgressCompressedRatePerSecond, stats.getZipStats().getCurrentEgressCompressedRatePerSecond());
        result.put(Metrics.Statistics.IngressCompressionRatio, stats.getZipStats().getIngressCompressionRatio());
        result.put(Metrics.Statistics.EgressCompressionRatio, stats.getZipStats().getEgressCompressionRatio());

        result.put(Metrics.Statistics.CurrentIngressSslRatePerSecond, stats.getSslStats().getCurrentIngressSslRatePerSecond());
        result.put(Metrics.Statistics.CurrentEgressSslRatePerSecond, stats.getSslStats().getCurrentEgressSslRatePerSecond());

        result.put(Metrics.Statistics.TotalClientsConnected, stats.getTotalClientsConnected());

        result.put(Metrics.Statistics.TotalIngressDiscards, stats.getIngressDiscards().getTotalIngressDiscards());
        result.put(Metrics.Statistics.NoSubscriptionMatch, stats.getIngressDiscards().getNoSubscriptionMatch());
        result.put(Metrics.Statistics.TopicParseError, stats.getIngressDiscards().getTopicParseError());
        result.put(Metrics.Statistics.ParseError, stats.getIngressDiscards().getParseError());
        result.put(Metrics.Statistics.MsgTooBig, stats.getIngressDiscards().getMsgTooBig());
        result.put(Metrics.Statistics.TtlExceeded, stats.getIngressDiscards().getTtlExceeded());
        result.put(Metrics.Statistics.WebParseError, stats.getIngressDiscards().getWebParseError());
        result.put(Metrics.Statistics.PublishTopicAcl, stats.getIngressDiscards().getPublishTopicAcl());
        result.put(Metrics.Statistics.MsgSpoolDiscards, stats.getIngressDiscards().getMsgSpoolDiscards());
        result.put(Metrics.Statistics.IngressMessagePromotionCongestion, stats.getIngressDiscards().getMessagePromotionCongestion());
        result.put(Metrics.Statistics.IngressMessageSpoolCongestion, stats.getIngressDiscards().getMessageSpoolCongestion());

        result.put(Metrics.Statistics.TotalEgressDiscards, stats.getEgressDiscards().getTotalEgressDiscards());
        result.put(Metrics.Statistics.TransmitCongestion, stats.getEgressDiscards().getTransmitCongestion());
        result.put(Metrics.Statistics.CompressionCongestion, stats.getEgressDiscards().getCompressionCongestion());
        result.put(Metrics.Statistics.MessageElided, stats.getEgressDiscards().getMessageElided());
        result.put(Metrics.Statistics.PayloadCouldNotBeFormatted, stats.getEgressDiscards().getPayloadCouldNotBeFormatted());
        result.put(Metrics.Statistics.EgressMessagePromotionCongestion, stats.getEgressDiscards().getMessagePromotionCongestion());
        result.put(Metrics.Statistics.EgressMessageSpoolCongestion, stats.getEgressDiscards().getMessageSpoolCongestion());
        result.put(Metrics.Statistics.MsgSpoolEgressDiscards, stats.getEgressDiscards().getMsgSpoolEgressDiscards());

        return result;
    }

    public Map<String, Object> getGlobalMsgSpool(RpcReply reply) {
        RpcReply.Rpc.Show.MessageSpool.MessageSpoolInfo stats = reply.getRpc()
                .getShow()
                .getMessageSpool()
                .getMessageSpoolInfo();

        Map<String, Object> result = new HashMap<>();
        result.put(Metrics.MsgSpool.IsEnabled, stats.getConfigStatus().startsWith("Enabled") ? 1 : 0);
        result.put(Metrics.MsgSpool.IsActive, stats.getOperationalStatus().equals("AD-Active") ? 1 : 0);
        result.put(Metrics.MsgSpool.IsDatapathUp, stats.isDatapathUp() ? 1 : 0);
        result.put(Metrics.MsgSpool.IsSynchronized, stats.getSynchronizationStatus().equals("Synced") ? 1 : 0);
        result.put(Metrics.MsgSpool.MessageCountUtilizationPct,
                safeParseDouble("MessageCountUtilizationPct", stats.getMessageCountUtilizationPercentage()).longValue());
        result.put(Metrics.MsgSpool.TransactionResourceUtilizationPct,
                safeParseDouble("TransactionResourceUtilizationPct", stats.getTransactionResourceUtilizationPercentage()).longValue());
        result.put(Metrics.MsgSpool.TransactedSessionCountUtilizationPct,
                safeParseDouble("TransactedSessionCountUtilizationPct", stats.getTransactedSessionCountUtilizationPercentage()).longValue());
        result.put(Metrics.MsgSpool.DeliveredUnackedMsgsUtilizationPct,
                safeParseDouble("DeliveredUnackedMsgsUtilizationPct", stats.getDeliveredUnackedMsgsUtilizationPercentage()).longValue());
        result.put(Metrics.MsgSpool.SpoolFilesUtilizationPercentage,
                safeParseDouble("SpoolFilesUtilizationPercentage", stats.getSpoolFilesUtilizationPercentage()).longValue());
        return result;
    }

    public Map<String, Object> getGlobalRedundancy(RpcReply reply) {
        RpcReply.Rpc.Show.Redundancy redundancy = reply.getRpc()
                .getShow()
                .getRedundancy();

        Map<String, Object> result = new HashMap<>();
        result.put(Metrics.Redundancy.ConfiguredStatus, redundancy.getConfigStatus().equals("Enabled") ? 1 : 0);
        result.put(Metrics.Redundancy.OperationalStatus, redundancy.getRedundancyStatus().equals("Up") ? 1 : 0);
        // result.put(Metrics.Redundancy.IsPrimary, redundancy.getActiveStandbyRole().equals("Primary") ? 1 : 0);

        // TODO: Need a way to figure out if we are active or backup
        // if ((Integer) result.get(Metrics.Redundancy.IsPrimary) == 1) {
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
            result.put(Metrics.Redundancy.IsActive, 1);
        }
        else {
            result.put(Metrics.Redundancy.IsActive, 0);
        }
        return result;
    }

    public Map<String, Object> getGlobalService(RpcReply reply) {
        RpcReply.Rpc.Show.Service service = reply.getRpc()
                .getShow()
                .getService();

        Map<String, Object> result = new HashMap<>();
        for (RpcReply.Rpc.Show.Service.Services.Service2 svc : service.getServices().getService()) {
            if (svc.getName().equals("SMF")) {
                result.put(Metrics.Service.SmfPort, svc.getListenPort().intValue());
                result.put(Metrics.Service.SmfPortUp, svc.getListenPortOperationalStatus().equals("Up") ? 1 : 0);

                result.put(Metrics.Service.SmfCompressedPort, svc.getCompressionListenPort().intValue());
                result.put(Metrics.Service.SmfCompressedPortUp, svc.getCompressionListenPortOperationalStatus().equals("Up") ? 1 : 0);

                result.put(Metrics.Service.SmfSslPort, (int) svc.getSsl().getListenPort());
                result.put(Metrics.Service.SmfSslPortUp, svc.getSsl().getListenPortOperationalStatus().equals("Up") ? 1 : 0);
            } else if (svc.getName().equals("WEB")) {
                result.put(Metrics.Service.WebPort, svc.getListenPort().intValue());
                result.put(Metrics.Service.WebPortUp, svc.getListenPortOperationalStatus().equals("Up") ? 1 : 0);

                result.put(Metrics.Service.WebSslPort, (int) svc.getSsl().getListenPort());
                result.put(Metrics.Service.WebSslPortUp, svc.getSsl().getListenPortOperationalStatus().equals("Up") ? 1 : 0);
            }
        }

        return result;
    }

    private Long longOrDefault(BigInteger value, long defaultValue) {
        if (value == null) return defaultValue;
        return value.longValue();
    }

    @Override
    public List<Map<String, Object>> getQueueList(RpcReply rpcReply) {
        List<Map<String,Object>> results = new ArrayList<>();
        List<QueueType> queues = rpcReply.getRpc()
                .getShow()
                .getQueue()
                .getQueues()
                .getQueue();
        for(QueueType q : queues) {
            Map<String, Object> result = new HashMap<>();
            result.put(Metrics.Queue.QueueName, q.getName());
            result.put(Metrics.Queue.VpnName, q.getInfo().getMessageVpn());
            result.put(Metrics.Queue.IsIngressEnabled, q.getInfo().getIngressConfigStatus().equals("Up") ? 1 : 0);
            result.put(Metrics.Queue.IsEgressEnabled, q.getInfo().getEgressConfigStatus().equals("Up") ? 1 : 0);
            result.put(Metrics.Queue.IsDurable, q.getInfo().isDurable() ? 1 : 0);
            result.put(Metrics.Queue.QuotaInMB, q.getInfo().getQuota().longValue());
            result.put(Metrics.Queue.MessagesEnqueued, q.getInfo().getNumMessagesSpooled().intValue());
            result.put(Metrics.Queue.UsageInMB, q.getInfo().getCurrentSpoolUsageInMb());
            result.put(Metrics.Queue.ConsumerCount, q.getInfo().getBindCount().intValue());
            result.put(Metrics.Queue.OldestMsgId, longOrDefault(q.getInfo().getOldestMsgId(), 0));
            result.put(Metrics.Queue.NewestMsgId, longOrDefault(q.getInfo().getNewestMsgId(), 0));
            results.add(result);
        }
        return results;
    }

    public List<Map<String, Object>> getQueueRatesList(RpcReply reply) {
        List<Map<String,Object>> results = new ArrayList<>();
        RpcReply.Rpc.Show.Queue.Queues queues = reply.getRpc()
                .getShow()
                .getQueue()
                .getQueues();
        for(QueueType q : queues.getQueue()) {
            Map<String, Object> result = new HashMap<>();
            List<QueueType.Rates> rates = q.getRates();
            result.put(Metrics.Queue.QueueName, q.getName());
            result.put(Metrics.Queue.VpnName, q.getInfo().getMessageVpn());
            for(QueueType.Rates r : rates) {
                result.put(Metrics.Queue.CurrentIngressRatePerSecond, r.getQendptDataRates().getCurrentIngressRatePerSecond());
                result.put(Metrics.Queue.CurrentIngressByteRatePerSecond, r.getQendptDataRates().getCurrentIngressByteRatePerSecond());
                result.put(Metrics.Queue.CurrentEgressRatePerSecond, r.getQendptDataRates().getCurrentEgressRatePerSecond());
                result.put(Metrics.Queue.CurrentEgressByteRatePerSecond, r.getQendptDataRates().getCurrentEgressByteRatePerSecond());
            }
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
            result.put(Metrics.TopicEndpoint.TopicEndpointName, t.getName());
            result.put(Metrics.TopicEndpoint.VpnName, t.getInfo().getMessageVpn());
            result.put(Metrics.TopicEndpoint.IsIngressEnabled, t.getInfo().getIngressConfigStatus().equals("Up") ? 1 : 0);
            result.put(Metrics.TopicEndpoint.IsEgressEnabled, t.getInfo().getEgressConfigStatus().equals("Up") ? 1 : 0);
            result.put(Metrics.TopicEndpoint.IsDurable, t.getInfo().isDurable() ? 1 : 0);
            result.put(Metrics.TopicEndpoint.QuotaInMB, t.getInfo().getQuota().longValue());
            result.put(Metrics.TopicEndpoint.MessagesSpooled, t.getInfo().getNumMessagesSpooled().intValue());
            result.put(Metrics.TopicEndpoint.UsageInMB, t.getInfo().getCurrentSpoolUsageInMb());
            result.put(Metrics.TopicEndpoint.ConsumerCount, t.getInfo().getBindCount().intValue());
            result.put(Metrics.TopicEndpoint.OldestMsgId, longOrDefault(t.getInfo().getOldestMsgId(), 0));
            result.put(Metrics.TopicEndpoint.NewestMsgId, longOrDefault(t.getInfo().getNewestMsgId(), 0));
            results.add(result);
        }
        return results;
    }

    public List<Map<String, Object>> getTopicEndpointRatesList(RpcReply reply) {
        List<Map<String,Object>> results = new ArrayList<>();
        RpcReply.Rpc.Show.TopicEndpoint.TopicEndpoints eps = reply.getRpc()
                .getShow()
                .getTopicEndpoint()
                .getTopicEndpoints();
        for(RpcReply.Rpc.Show.TopicEndpoint.TopicEndpoints.TopicEndpoint2 t : eps.getTopicEndpoint()) {
            Map<String, Object> result = new HashMap<>();
            result.put(Metrics.TopicEndpoint.TopicEndpointName, t.getName());
            result.put(Metrics.TopicEndpoint.VpnName, t.getInfo().getMessageVpn());
            for(RpcReply.Rpc.Show.TopicEndpoint.TopicEndpoints.TopicEndpoint2.Rates r : t.getRates()) {
                result.put(Metrics.TopicEndpoint.CurrentIngressRatePerSecond, r.getQendptDataRates().getCurrentIngressRatePerSecond());
                result.put(Metrics.TopicEndpoint.CurrentIngressByteRatePerSecond, r.getQendptDataRates().getCurrentIngressByteRatePerSecond());
                result.put(Metrics.TopicEndpoint.CurrentEgressRatePerSecond, r.getQendptDataRates().getCurrentEgressRatePerSecond());
                result.put(Metrics.TopicEndpoint.CurrentEgressByteRatePerSecond, r.getQendptDataRates().getCurrentEgressByteRatePerSecond());
            }
            results.add(result);
        }
        return results;
    }


    public List<Map<String,Object>> getGlobalBridgeList(RpcReply reply) {
        List<Map<String,Object>> results = new ArrayList<>();
        List<RpcReply.Rpc.Show.Bridge.Bridges.Bridge2> bridges = reply.getRpc()
                .getShow()
                .getBridge()
                .getBridges()
                .getBridge();
        for(RpcReply.Rpc.Show.Bridge.Bridges.Bridge2 b : bridges) {
            // Skip the generated local side of bridges that the user doesn't configure
            if (b.getAdminState().equals("N/A"))
                continue;
            Map<String, Object> result = new HashMap<>();
            result.put(Metrics.Bridge.BridgeName, b.getBridgeName());
            result.put(Metrics.Bridge.VpnName, b.getLocalVpnName());
            result.put(Metrics.Bridge.IsEnabled, b.getAdminState().equals("Enabled") ? 1:0);
            result.put(Metrics.Bridge.IsConnected, b.getConnectionEstablisher().equals("Local") ? 1:0);
            result.put(Metrics.Bridge.IsInSync, b.getInboundOperationalState().equals("Ready-InSync") ? 1:0);
            result.put(Metrics.Bridge.IsBoundToBridgeQueue, b.getQueueOperationalState().equals("Bound") ? 1:0);
            result.put(Metrics.Bridge.UptimeInSecs, b.getConnectionUptimeInSeconds().longValue());
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
