package com.appdynamics.extensions.solace.semp.r8_13_0;

import com.appdynamics.extensions.solace.Helper;
import com.appdynamics.extensions.solace.ServerConfigs;
import com.appdynamics.extensions.solace.semp.Metrics;
import com.appdynamics.extensions.solace.semp.*;
import com.solacesystems.semp_jaxb.r8_13_0.reply.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.appdynamics.extensions.solace.Helper.*;
import static com.appdynamics.extensions.solace.semp.r8_13_0.StatsHelper.*;

public class SempReplyFactory_r8_13_0 implements SempReplyFactory<RpcReply> {
    private static final Logger logger = LoggerFactory.getLogger(SempReplyFactory_r8_13_0.class);

    final private ServerConfigs serverConfigs;

    public SempReplyFactory_r8_13_0(ServerConfigs serverConfigs) {
        this.serverConfigs = serverConfigs;
    }

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
        result.put(Metrics.Statistics.TotalClientDataMessagesReceived, stats.getClientDataMessagesReceived());
        result.put(Metrics.Statistics.TotalClientDataMessagesSent, stats.getClientDataMessagesSent());

        if (!serverConfigs.getExcludeCompressionMetrics()) {
            result.put(Metrics.Statistics.CurrentIngressCompressedRatePerSecond, stats.getZipStats().getCurrentIngressCompressedRatePerSecond());
            result.put(Metrics.Statistics.CurrentEgressCompressedRatePerSecond, stats.getZipStats().getCurrentEgressCompressedRatePerSecond());
            result.put(Metrics.Statistics.IngressCompressionRatio, stats.getZipStats().getIngressCompressionRatio()*100);
            result.put(Metrics.Statistics.EgressCompressionRatio, stats.getZipStats().getEgressCompressionRatio()*100);
        }

        if (!serverConfigs.getExcludeTlsMetrics()) {
            result.put(Metrics.Statistics.CurrentIngressSslRatePerSecond, stats.getSslStats().getCurrentIngressSslRatePerSecond());
            result.put(Metrics.Statistics.CurrentEgressSslRatePerSecond, stats.getSslStats().getCurrentEgressSslRatePerSecond());
        }

        result.put(Metrics.Statistics.TotalClientsConnected, stats.getTotalClientsConnected());
        result.put(Metrics.Statistics.TotalSmfClientsConnected, stats.getTotalClientsConnectedServiceSmf());

        if (!serverConfigs.getExcludeDiscardMetrics()) {
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
        }

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
        result.put(Metrics.MsgSpool.IsStandby, stats.getOperationalStatus().equals("AD-Standby") ? 1 : 0);
        result.put(Metrics.MsgSpool.IsDatapathUp, stats.isDatapathUp() ? 1 : 0);
        result.put(Metrics.MsgSpool.IsSynchronized, stats.getSynchronizationStatus().equals("Synced") ? 1 : 0);
        result.put(Metrics.MsgSpool.CurrentIngressFlowsCount, stats.getIngressFlowCount());
        result.put(Metrics.MsgSpool.CurrentEgressFlowsCount,
                longOrDefault(stats.getActiveFlowCount(),0)
                        +longOrDefault(stats.getInactiveFlowCount(),0)
                        +longOrDefault(stats.getBrowserFlowCount(),0));
        result.put(Metrics.MsgSpool.TotalEndpointsCount,
                longOrDefault(stats.getMessageSpoolEntitiesUsedByQueue(),0)
                        +longOrDefault(stats.getMessageSpoolEntitiesUsedByDte(),0));
        result.put(Metrics.MsgSpool.TotalMessagesSpooledCount, longOrDefault(stats.getTotalMessagesCurrentlySpooled(), 0));
        result.put(Metrics.MsgSpool.TotalMessagesSpooledInMB, Math.round(stats.getCurrentPersistUsage()));

        result.put(Metrics.MsgSpool.ActiveDiskPartitionUsagePct,
                safeParseDouble(Metrics.MsgSpool.ActiveDiskPartitionUsagePct, stats.getMessageCountUtilizationPercentage()).longValue());
        result.put(Metrics.MsgSpool.MessageCountUtilizationPct,
                safeParseDouble(Metrics.MsgSpool.MessageCountUtilizationPct, stats.getMessageCountUtilizationPercentage()).longValue());
        result.put(Metrics.MsgSpool.TransactionResourceUtilizationPct,
                safeParseDouble(Metrics.MsgSpool.TransactionResourceUtilizationPct, stats.getTransactionResourceUtilizationPercentage()).longValue());
        result.put(Metrics.MsgSpool.TransactedSessionCountUtilizationPct,
                safeParseDouble(Metrics.MsgSpool.TransactedSessionCountUtilizationPct, stats.getTransactedSessionCountUtilizationPercentage()).longValue());
        result.put(Metrics.MsgSpool.DeliveredUnackedMsgsUtilizationPct,
                safeParseDouble(Metrics.MsgSpool.DeliveredUnackedMsgsUtilizationPct, stats.getDeliveredUnackedMsgsUtilizationPercentage()).longValue());
        result.put(Metrics.MsgSpool.SpoolFilesUtilizationPercentage,
                safeParseDouble(Metrics.MsgSpool.SpoolFilesUtilizationPercentage, stats.getSpoolFilesUtilizationPercentage()).longValue());

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
        if ( ((Integer)result.get(Metrics.Redundancy.ConfiguredStatus)) == 0L) {
            result.put(Metrics.Redundancy.IsActive, 0);
            return result;
        }
        // TODO: Need a way to figure out if we are active or backup
        // if ((Integer) result.get(Metrics.Redundancy.IsPrimary) == 1) {
        try {
            if ( getRedundantNodeSpoolStatus(
                    redundancy.getVirtualRouters()
                            .getPrimary()
                            .getStatus()
                            .getDetail())
                    .equals("AD-Active")) {
                // We are Primary and AD-Active
                result.put(Metrics.Redundancy.IsActive, 1);
            }
            else if ( getRedundantNodeSpoolStatus(
                    redundancy.getVirtualRouters()
                            .getBackup()
                            .getStatus()
                            .getDetail())
                    .equals("AD-Active")) {
                // We are Backup and AD-Active
                result.put(Metrics.Redundancy.IsActive, 1);
            } else {
                // We are not AD-Active
                result.put(Metrics.Redundancy.IsActive, 0);
            }
            directMessagingOnly(redundancy, result);
        }
        catch(Throwable t) {
            result.put(Metrics.Redundancy.IsActive, 0);
            logger.error("Exception thrown processing Redundancy info.", t);
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
                result.put(Metrics.Service.SmfPortUp, svc.getListenPortOperationalStatus().equals("Up") ? 1 : 0);
                result.put(Metrics.Service.SmfCompressedPortUp, svc.getCompressionListenPortOperationalStatus().equals("Up") ? 1 : 0);
                result.put(Metrics.Service.SmfSslPortUp, svc.getSsl().getListenPortOperationalStatus().equals("Up") ? 1 : 0);
            } else if (svc.getName().equals("SEMP")) {
                result.put(Metrics.Service.SempPortUp, svc.getListenPortOperationalStatus().equals("Up") ? 1 : 0);
            } else if (svc.getName().equals("WEB")) {
                result.put(Metrics.Service.WebPortUp, svc.getListenPortOperationalStatus().equals("Up") ? 1 : 0);
                RpcReply.Rpc.Show.Service.Services.Service2.Ssl ssl = svc.getSsl();
                if (ssl != null) {
                    result.put(Metrics.Service.WebSslPortUp, ssl.getListenPortOperationalStatus().equals("Up") ? 1 : 0);
                }
            } else if (svc.getName().equals("REST")) {
                String restOpStatus = svc.getListenPortOperationalStatus();
                int isUp = 0;
                if (restOpStatus != null && restOpStatus.equals("Up") ) {
                    isUp = 1;
                }
                String vpnName = svc.getVpnName();
                // HACK: this should really make use of the MetricPrinter which knows the platform-specific
                // way to structure a metric address
                result.put(Metrics.Vpn.PREFIX +'|'+vpnName+'|'+Metrics.Vpn.RestPortUp, isUp);
            }
        }

        return result;
    }

    public List<Map<String, Object>> getMsgVpnList(RpcReply reply) {
        List<Map<String,Object>> results = new ArrayList<>();
        List<RpcReply.Rpc.Show.MessageVpn.Vpn> vpns = reply.getRpc()
                .getShow()
                .getMessageVpn()
                .getVpn();

        for(RpcReply.Rpc.Show.MessageVpn.Vpn vpn : vpns) {
            SolStatsType stats = vpn.getStats();

            Map<String, Object> result = new HashMap<>();
            result.put(Metrics.Vpn.VpnName, vpn.getName());
            result.put(Metrics.Vpn.IsEnabled, (vpn.isEnabled() ? 1 : 0) );
            result.put(Metrics.Vpn.OperationalStatus, (vpn.isOperational() ? 1 : 0) );
            result.put(Metrics.Vpn.QuotaInMB, vpn.getMaximumSpoolUsageMb() );
            result.put(Metrics.Vpn.TotalClientsConnected, vpn.getConnections());
            result.put(Metrics.Vpn.SMFConnectionsPct,
                    calcPercentage(vpn.getConnectionsServiceSmf(),vpn.getMaxConnectionsServiceSmf()));
            result.put(Metrics.Vpn.TotalClientDataMessagesReceived, stats.getClientDataMessagesReceived());
            result.put(Metrics.Vpn.TotalClientDataMessagesSent, stats.getClientDataMessagesSent());

            if (!serverConfigs.getExcludeDiscardMetrics()) {
                result.put(Metrics.Vpn.TotalIngressDiscards, stats.getIngressDiscards().getTotalIngressDiscards());
                result.put(Metrics.Vpn.NoSubscriptionMatch, stats.getIngressDiscards().getNoSubscriptionMatch());
                result.put(Metrics.Vpn.TopicParseError, stats.getIngressDiscards().getTopicParseError());
                result.put(Metrics.Vpn.ParseError, stats.getIngressDiscards().getParseError());
                result.put(Metrics.Vpn.MsgTooBig, stats.getIngressDiscards().getMsgTooBig());
                result.put(Metrics.Vpn.TtlExceeded, stats.getIngressDiscards().getTtlExceeded());
                result.put(Metrics.Vpn.PublishTopicAcl, stats.getIngressDiscards().getPublishTopicAcl());
                result.put(Metrics.Vpn.MsgSpoolDiscards, stats.getIngressDiscards().getMsgSpoolDiscards());
                result.put(Metrics.Vpn.IngressMessagePromotionCongestion, stats.getIngressDiscards().getMessagePromotionCongestion());
                result.put(Metrics.Vpn.IngressMessageSpoolCongestion, stats.getIngressDiscards().getMessageSpoolCongestion());

                result.put(Metrics.Vpn.TotalEgressDiscards, stats.getEgressDiscards().getTotalEgressDiscards());
                result.put(Metrics.Vpn.TransmitCongestion, stats.getEgressDiscards().getTransmitCongestion());
                result.put(Metrics.Vpn.CompressionCongestion, stats.getEgressDiscards().getCompressionCongestion());
                result.put(Metrics.Vpn.MessageElided, stats.getEgressDiscards().getMessageElided());
                result.put(Metrics.Vpn.EgressMessagePromotionCongestion, stats.getEgressDiscards().getMessagePromotionCongestion());
                result.put(Metrics.Vpn.EgressMessageSpoolCongestion, stats.getEgressDiscards().getMessageSpoolCongestion());
                result.put(Metrics.Vpn.MsgSpoolEgressDiscards, stats.getEgressDiscards().getMsgSpoolEgressDiscards());
            }

            if (!serverConfigs.getExcludeExtendedStats()) {
                result.put(Metrics.Vpn.CurrentIngressRatePerSecond, stats.getCurrentIngressRatePerSecond());
                result.put(Metrics.Vpn.CurrentEgressRatePerSecond, stats.getCurrentEgressRatePerSecond());
                result.put(Metrics.Vpn.CurrentIngressByteRatePerSecond, stats.getCurrentIngressByteRatePerSecond());
                result.put(Metrics.Vpn.CurrentEgressByteRatePerSecond, stats.getCurrentEgressByteRatePerSecond());
            }

            results.add(result);
        }
        return results;
    }

    public List<Map<String, Object>> getMsgVpnSpoolList(RpcReply reply) {
        List<Map<String,Object>> results = new ArrayList<>();
        List<Object> vpns = reply.getRpc()
                .getShow()
                .getMessageSpool()
                .getMessageVpn()
                .getVpnNameOrVpnOrMessageSpoolRates();
        for(Object o : vpns) {
            MessageSpoolMessageVpnEntry vpn = (MessageSpoolMessageVpnEntry)o;
            Map<String, Object> result = new HashMap<>();
            result.put(Metrics.Vpn.VpnName , vpn.getName());
            result.put(Metrics.Vpn.UsageInMB , vpn.getCurrentSpoolUsageMb());

            result.put(Metrics.Vpn.TotalEndpointsCount,
                    longOrDefault(vpn.getCurrentQueuesAndTopicEndpoints(),0));
            result.put(Metrics.Vpn.TotalMessagesSpooledCount, vpn.getCurrentMessagesSpooled());

            if (!serverConfigs.getExcludeExtendedStats()) {
                result.put(Metrics.Vpn.CurrentIngressFlowsCount, vpn.getCurrentIngressFlows());
                result.put(Metrics.Vpn.CurrentEgressFlowsCount, vpn.getCurrentEgressFlows());
            }
            results.add(result);
        }
        return results;
    }

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
            result.put(Metrics.Queue.IsEnabled, combineConfigStatus(q.getInfo()));
            result.put(Metrics.Queue.IsDurable, q.getInfo().isDurable() ? 1 : 0);
            result.put(Metrics.Queue.QuotaInMB, q.getInfo().getQuota().longValue());
            result.put(Metrics.Queue.MessagesSpooled, longOrDefault(q.getInfo().getNumMessagesSpooled(),0L).intValue());
            result.put(Metrics.Queue.UsageInMB, longOrDefault(q.getInfo().getCurrentSpoolUsageInMb(), 0));
            result.put(Metrics.Queue.ConsumerCount, longOrDefault(q.getInfo().getBindCount(), 0).intValue());
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
        for (QueueType q : queues.getQueue()) {
            Map<String, Object> result = new HashMap<>();
            List<QueueType.Rates> rates = q.getRates();
            result.put(Metrics.Queue.QueueName, q.getName());
            result.put(Metrics.Queue.VpnName, q.getInfo().getMessageVpn());
            for (QueueType.Rates r : rates) {
                result.put(Metrics.Queue.CurrentIngressRatePerSecond, r.getQendptDataRates().getCurrentIngressRatePerSecond());
                result.put(Metrics.Queue.CurrentIngressByteRatePerSecond, r.getQendptDataRates().getCurrentIngressByteRatePerSecond());
                result.put(Metrics.Queue.CurrentEgressRatePerSecond, r.getQendptDataRates().getCurrentEgressRatePerSecond());
                result.put(Metrics.Queue.CurrentEgressByteRatePerSecond, r.getQendptDataRates().getCurrentEgressByteRatePerSecond());
            }
            results.add(result);
        }
        return results;
    }

    public List<Map<String, Object>> getQueueStatsList(RpcReply reply) {
        List<Map<String,Object>> results = new ArrayList<>();
        RpcReply.Rpc.Show.Queue.Queues queues = reply.getRpc()
                .getShow()
                .getQueue()
                .getQueues();
        for (QueueType q : queues.getQueue()) {
            QueueType.Stats stats = q.getStats();
            MessageSpoolStatsType spoolStats = stats.getMessageSpoolStats();
            Map<String, Object> result = new HashMap<>();
            result.put(Metrics.Queue.QueueName, q.getName());
            result.put(Metrics.Queue.VpnName, q.getInfo().getMessageVpn());
            result.put(Metrics.Queue.TotalMessagesSpooled, spoolStats.getTotalMessagesSpooled().longValue());
            result.put(Metrics.Queue.RedeliveredCount, spoolStats.getMessagesRedelivered().longValue());
            if (!serverConfigs.getExcludeDiscardMetrics()) {
                // Ingress
                result.put(Metrics.Queue.TotalIngressDiscards, countIngressDiscards(spoolStats));
                result.put(Metrics.Queue.MsgSpoolDiscards, Helper.longOrDefault(spoolStats.getSpoolUsageExceeded(), 0) );
                result.put(Metrics.Queue.MsgTooBig, Helper.longOrDefault(spoolStats.getMaxMessageSizeExceeded(), 0) );
                result.put(Metrics.Queue.SpoolShutdown, Helper.longOrDefault(spoolStats.getSpoolShutdownDiscard(), 0) );
                result.put(Metrics.Queue.UserProfileDenial, Helper.longOrDefault(spoolStats.getUserProfileDenyGuaranteed(), 0) );
                result.put(Metrics.Queue.NoLocalDelivery, Helper.longOrDefault(spoolStats.getNoLocalDeliveryDiscard(), 0) );
                result.put(Metrics.Queue.DestinationGroupError, Helper.longOrDefault(spoolStats.getDestinationGroupError(), 0) );
                result.put(Metrics.Queue.LowPriorityMsgCongestion, Helper.longOrDefault(spoolStats.getLowPriorityMsgCongestionDiscard(), 0) );
                // Egress
                result.put(Metrics.Queue.TotalEgressDiscards, countEgressDiscards(spoolStats));
                result.put(Metrics.Queue.TTLExceeded,             Helper.longOrDefault(spoolStats.getTotalTtlExpiredDiscardMessages(), 0) );
                result.put(Metrics.Queue.TTLExpiredToDMQ,         Helper.longOrDefault(spoolStats.getTotalTtlExpiredToDmqMessages(), 0) );
                result.put(Metrics.Queue.TTLExpireToDMQFailed,    Helper.longOrDefault(spoolStats.getTotalTtlExpiredToDmqFailures(), 0) );
                result.put(Metrics.Queue.MaxRedelivery,           Helper.longOrDefault(spoolStats.getMaxRedeliveryExceededDiscardMessages(), 0) );
                result.put(Metrics.Queue.MaxRedeliveryToDMQ,      Helper.longOrDefault(spoolStats.getMaxRedeliveryExceededToDmqMessages(), 0) );
                result.put(Metrics.Queue.MaxRedeliveryToDMQFailed,Helper.longOrDefault(spoolStats.getMaxRedeliveryExceededToDmqFailures(), 0) );
            }
            results.add(result);
        }
        return results;
    }

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
            result.put(Metrics.TopicEndpoint.IsEnabled, combineConfigStatus(t.getInfo()));
            result.put(Metrics.TopicEndpoint.IsDurable, t.getInfo().isDurable() ? 1 : 0);
            result.put(Metrics.TopicEndpoint.QuotaInMB, t.getInfo().getQuota().longValue());
            result.put(Metrics.TopicEndpoint.MessagesSpooled, longOrDefault(t.getInfo().getNumMessagesSpooled(),0).intValue());
            result.put(Metrics.TopicEndpoint.UsageInMB, longOrDefault(t.getInfo().getCurrentSpoolUsageInMb(),0));
            result.put(Metrics.TopicEndpoint.ConsumerCount, longOrDefault(t.getInfo().getBindCount(),0).intValue());
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
        for (RpcReply.Rpc.Show.TopicEndpoint.TopicEndpoints.TopicEndpoint2 t : eps.getTopicEndpoint()) {
            Map<String, Object> result = new HashMap<>();
            result.put(Metrics.TopicEndpoint.TopicEndpointName, t.getName());
            result.put(Metrics.TopicEndpoint.VpnName, t.getInfo().getMessageVpn());
            for (RpcReply.Rpc.Show.TopicEndpoint.TopicEndpoints.TopicEndpoint2.Rates r : t.getRates()) {
                result.put(Metrics.TopicEndpoint.CurrentIngressRatePerSecond, r.getQendptDataRates().getCurrentIngressRatePerSecond());
                result.put(Metrics.TopicEndpoint.CurrentIngressByteRatePerSecond, r.getQendptDataRates().getCurrentIngressByteRatePerSecond());
                result.put(Metrics.TopicEndpoint.CurrentEgressRatePerSecond, r.getQendptDataRates().getCurrentEgressRatePerSecond());
                result.put(Metrics.TopicEndpoint.CurrentEgressByteRatePerSecond, r.getQendptDataRates().getCurrentEgressByteRatePerSecond());
            }
            results.add(result);
        }
        return results;
    }

    public List<Map<String, Object>> getTopicEndpointStatsList(RpcReply reply) {
        List<Map<String,Object>> results = new ArrayList<>();
        RpcReply.Rpc.Show.TopicEndpoint.TopicEndpoints endpoints = reply.getRpc()
                .getShow()
                .getTopicEndpoint()
                .getTopicEndpoints();
        for (RpcReply.Rpc.Show.TopicEndpoint.TopicEndpoints.TopicEndpoint2 e : endpoints.getTopicEndpoint()) {
            RpcReply.Rpc.Show.TopicEndpoint.TopicEndpoints.TopicEndpoint2.Stats stats = e.getStats();
            MessageSpoolStatsType spoolStats = stats.getMessageSpoolStats();
            Map<String, Object> result = new HashMap<>();
            result.put(Metrics.TopicEndpoint.TopicEndpointName, e.getName());
            result.put(Metrics.TopicEndpoint.VpnName, e.getInfo().getMessageVpn());
            result.put(Metrics.TopicEndpoint.TotalMessagesSpooled, spoolStats.getTotalMessagesSpooled().longValue());
            result.put(Metrics.TopicEndpoint.RedeliveredCount, spoolStats.getMessagesRedelivered().longValue());
            if (!serverConfigs.getExcludeDiscardMetrics()) {
                // Ingress
                result.put(Metrics.TopicEndpoint.TotalIngressDiscards, countIngressDiscards(spoolStats));
                result.put(Metrics.TopicEndpoint.MsgSpoolDiscards,        Helper.longOrDefault(spoolStats.getSpoolUsageExceeded(), 0) );
                result.put(Metrics.TopicEndpoint.MsgTooBig,               Helper.longOrDefault(spoolStats.getMaxMessageSizeExceeded(), 0) );
                result.put(Metrics.TopicEndpoint.SpoolShutdown,           Helper.longOrDefault(spoolStats.getSpoolShutdownDiscard(), 0) );
                result.put(Metrics.TopicEndpoint.UserProfileDenial,       Helper.longOrDefault(spoolStats.getUserProfileDenyGuaranteed(), 0) );
                result.put(Metrics.TopicEndpoint.NoLocalDelivery,         Helper.longOrDefault(spoolStats.getNoLocalDeliveryDiscard(), 0) );
                result.put(Metrics.TopicEndpoint.DestinationGroupError,   Helper.longOrDefault(spoolStats.getDestinationGroupError(), 0) );
                result.put(Metrics.TopicEndpoint.LowPriorityMsgCongestion,Helper.longOrDefault(spoolStats.getLowPriorityMsgCongestionDiscard(), 0) );
                // Egress
                result.put(Metrics.TopicEndpoint.TotalEgressDiscards, countEgressDiscards(spoolStats));
                result.put(Metrics.TopicEndpoint.TTLExceeded,             Helper.longOrDefault(spoolStats.getTotalTtlExpiredDiscardMessages(), 0) );
                result.put(Metrics.TopicEndpoint.TTLExpiredToDMQ,         Helper.longOrDefault(spoolStats.getTotalTtlExpiredToDmqMessages(), 0) );
                result.put(Metrics.TopicEndpoint.TTLExpireToDMQFailed,    Helper.longOrDefault(spoolStats.getTotalTtlExpiredToDmqFailures(), 0) );
                result.put(Metrics.TopicEndpoint.MaxRedelivery,           Helper.longOrDefault(spoolStats.getMaxRedeliveryExceededDiscardMessages(), 0) );
                result.put(Metrics.TopicEndpoint.MaxRedeliveryToDMQ,      Helper.longOrDefault(spoolStats.getMaxRedeliveryExceededToDmqMessages(), 0) );
                result.put(Metrics.TopicEndpoint.MaxRedeliveryToDMQFailed,Helper.longOrDefault(spoolStats.getMaxRedeliveryExceededToDmqFailures(), 0) );
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
            results.add(result);
        }
        return results;
    }

}
