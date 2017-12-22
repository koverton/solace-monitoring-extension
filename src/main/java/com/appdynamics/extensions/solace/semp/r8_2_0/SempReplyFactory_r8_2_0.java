package com.appdynamics.extensions.solace.semp.r8_2_0;

import com.appdynamics.extensions.solace.semp.SempReplyFactory;
import com.solacesystems.semp_jaxb.r8_2_0.reply.QueueType;
import com.solacesystems.semp_jaxb.r8_2_0.reply.RpcReply;
import com.solacesystems.semp_jaxb.r8_2_0.reply.SolStatsType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SempReplyFactory_r8_2_0 implements SempReplyFactory<RpcReply> {
    private static final Logger logger = LoggerFactory.getLogger(SempReplyFactory_r8_2_0.class);

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
        result.put("CurrentIngressRatePerSecond", stats.getCurrentIngressRatePerSecond());
        result.put("CurrentEgressRatePerSecond", stats.getCurrentEgressRatePerSecond());
        result.put("CurrentIngressByteRatePerSecond", stats.getCurrentIngressByteRatePerSecond());
        result.put("CurrentEgressByteRatePerSecond", stats.getCurrentEgressByteRatePerSecond());

        result.put("CurrentIngressCompressedRatePerSecond", stats.getZipStats().getCurrentIngressCompressedRatePerSecond());
        result.put("CurrentEgressCompressedRatePerSecond", stats.getZipStats().getCurrentEgressCompressedRatePerSecond());
        result.put("IngressCompressionRatio", stats.getZipStats().getIngressCompressionRatio());
        result.put("EgressCompressionRatio", stats.getZipStats().getEgressCompressionRatio());

        result.put("CurrentIngressSslRatePerSecond", stats.getSslStats().getCurrentIngressSslRatePerSecond());
        result.put("CurrentEgressSslRatePerSecond", stats.getSslStats().getCurrentEgressSslRatePerSecond());

        result.put("TotalIngressDiscards", stats.getIngressDiscards().getTotalIngressDiscards());
        result.put("NoSubscriptionMatch", stats.getIngressDiscards().getNoSubscriptionMatch());
        result.put("TopicParseError", stats.getIngressDiscards().getTopicParseError());
        result.put("ParseError", stats.getIngressDiscards().getParseError());
        result.put("MsgTooBig", stats.getIngressDiscards().getMsgTooBig());
        result.put("TtlExceeded", stats.getIngressDiscards().getTtlExceeded());
        result.put("WebParseError", stats.getIngressDiscards().getWebParseError());
        result.put("PublishTopicAcl", stats.getIngressDiscards().getPublishTopicAcl());
        result.put("MsgSpoolDiscards", stats.getIngressDiscards().getMsgSpoolDiscards());
        result.put("IngressMessagePromotionCongestion", stats.getIngressDiscards().getMessagePromotionCongestion());
        result.put("IngressMessageSpoolCongestion", stats.getIngressDiscards().getMessageSpoolCongestion());

        result.put("TotalEgressDiscards", stats.getEgressDiscards().getTotalEgressDiscards());
        result.put("TransmitCongestion", stats.getEgressDiscards().getTransmitCongestion());
        result.put("CompressionCongestion", stats.getEgressDiscards().getCompressionCongestion());
        result.put("MessageElided", stats.getEgressDiscards().getMessageElided());
        result.put("PayloadCouldNotBeFormatted", stats.getEgressDiscards().getPayloadCouldNotBeFormatted());
        result.put("EgressMessagePromotionCongestion", stats.getEgressDiscards().getMessagePromotionCongestion());
        result.put("EgressMessageSpoolCongestion", stats.getEgressDiscards().getMessageSpoolCongestion());
        result.put("MsgSpoolEgressDiscards", stats.getEgressDiscards().getMsgSpoolEgressDiscards());

        return result;
    }

    public Map<String, Object> getGlobalMsgSpool(RpcReply reply) {
        RpcReply.Rpc.Show.MessageSpool.MessageSpoolInfo stats = reply.getRpc()
                .getShow()
                .getMessageSpool()
                .getMessageSpoolInfo();

        Map<String, Object> result = new HashMap<>();
        result.put("IsEnabled", stats.getConfigStatus().startsWith("Enabled") ? 1 : 0);
        result.put("IsActive", stats.getOperationalStatus().equals("AD-Active") ? 1 : 0);
        result.put("IsDatapathUp", stats.isDatapathUp() ? 1 : 0);
        result.put("IsSynchronized", stats.getSynchronizationStatus().equals("Synced") ? 1 : 0);
        result.put("MessageCountUtilizationPct", safeParseDouble("MessageCountUtilizationPct", stats.getMessageCountUtilizationPercentage()));
        result.put("TransactionResourceUtilizationPct", safeParseDouble("TransactionResourceUtilizationPct", stats.getTransactionResourceUtilizationPercentage()));
        result.put("TransactedSessionCountUtilizationPct", safeParseDouble("TransactedSessionCountUtilizationPct", stats.getTransactedSessionCountUtilizationPercentage()));
        result.put("DeliveredUnackedMsgsUtilizationPct", safeParseDouble("DeliveredUnackedMsgsUtilizationPct", stats.getDeliveredUnackedMsgsUtilizationPercentage()));
        result.put("SpoolFilesUtilizationPercentage", safeParseDouble("SpoolFilesUtilizationPercentage", stats.getSpoolFilesUtilizationPercentage()));
        return result;
    }

    public Map<String, Object> getGlobalRedundancy(RpcReply reply) {
        RpcReply.Rpc.Show.Redundancy redundancy = reply.getRpc()
                .getShow()
                .getRedundancy();

        Map<String, Object> result = new HashMap<>();
        result.put("ConfiguredStatus", redundancy.getConfigStatus().equals("Enabled") ? 1 : 0);
        result.put("OperationalStatus", redundancy.getRedundancyStatus().equals("Up") ? 1 : 0);
        // result.put("IsPrimary", redundancy.getActiveStandbyRole().equals("Primary") ? 1 : 0);
        // TODO: Need a way to figure out if we are active or backup
        if ((Integer) result.get("IsPrimary") == 1) {
            result.put("IsActive",
                    redundancy.getVirtualRouters()
                            .getPrimary()
                            .getStatus()
                            .getActivity()
                            .equals("Local Active") ? 1 : 0);
        } else {
            result.put("IsActive",
                    redundancy.getVirtualRouters()
                            .getBackup()
                            .getStatus()
                            .getActivity()
                            .equals("Local Active") ? 1 : 0);
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
                result.put("SmfPort", svc.getListenPort().intValue());
                result.put("SmfPortUp", svc.getListenPortOperationalStatus().equals("Up") ? 1 : 0);

                result.put("SmfCompressedPort", svc.getCompressionListenPort().intValue());
                result.put("SmfCompressedPortUp", svc.getCompressionListenPortOperationalStatus().equals("Up") ? 1 : 0);

                result.put("SmfSslPort", (int) svc.getSsl().getListenPort());
                result.put("SmfSslPortUp", svc.getSsl().getListenPortOperationalStatus().equals("Up") ? 1 : 0);
            } else if (svc.getName().equals("WEB")) {
                result.put("WebPort", svc.getListenPort().intValue());
                result.put("WebPortUp", svc.getListenPortOperationalStatus().equals("Up") ? 1 : 0);

                result.put("WebSslPort", (int) svc.getSsl().getListenPort());
                result.put("WebSslPortUp", svc.getSsl().getListenPortOperationalStatus().equals("Up") ? 1 : 0);
            }
        }

        return result;
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
            result.put("QueueName", q.getName());
            result.put("VpnName", q.getInfo().getMessageVpn());
            result.put("IsIngressEnabled", q.getInfo().getIngressConfigStatus().equals("Up") ? 1 : 0);
            result.put("IsEgressEnabled", q.getInfo().getEgressConfigStatus().equals("Up") ? 1 : 0);
            result.put("QuotaInMB", q.getInfo().getQuota().longValue());
            result.put("MessagesEnqueued", q.getInfo().getNumMessagesSpooled().intValue());
            result.put("UsageInMB", q.getInfo().getCurrentSpoolUsageInMb());
            result.put("ConsumerCount", q.getInfo().getBindCount().intValue());
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
