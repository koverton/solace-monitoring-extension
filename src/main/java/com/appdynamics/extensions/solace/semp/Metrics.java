package com.appdynamics.extensions.solace.semp;

/**
 * Static identifier strings for every metric we publish.
 *
 * The PREFIX in each grouping is the metric prefix string for the overall
 * path to each metric.
 */
public final class Metrics {

    public class Service {
        public final static String PREFIX = "Services";

        public final static String SmfPortUp = "SmfPortUp";
        public final static String SempPortUp = "SempPortUp";
        public final static String SmfCompressedPortUp = "SmfCompressedPortUp";
        public final static String SmfSslPortUp = "SmfSslPortUp";
        public final static String WebPortUp = "WebPortUp";
        public final static String WebSslPortUp = "WebSslPortUp";
    }

    public class Redundancy {
        public final static String PREFIX = "Redundancy";

        public final static String ConfiguredStatus = "ConfiguredStatus";
        public final static String OperationalStatus = "OperationalStatus";
        //public final static String IsPrimary = "IsPrimary";
        public final static String IsActive = "IsActive";
    }

    public class MsgSpool {
        public final static String PREFIX = "MsgSpool";

        public final static String IsEnabled = "IsEnabled";
        public final static String IsActive = "IsActive";
        public final static String IsStandby = "IsStandby";
        public final static String IsDatapathUp = "IsDatapathUp";
        public final static String IsSynchronized = "IsSynchronized";
        public final static String CurrentIngressFlowsCount = "CurrentIngressFlowsCount";
        public final static String CurrentEgressFlowsCount = "CurrentEgressFlowsCount";
        public final static String TotalEndpointsCount = "TotalEndpointsCount";
        public final static String TotalMessagesSpooledCount = "TotalMessagesSpooledCount";
        public final static String TotalMessagesSpooledInMB = "TotalMessagesSpooledInMB";
        public final static String ActiveDiskPartitionUsagePct = "ActiveDiskPartitionUsagePct";
        public final static String MessageCountUtilizationPct = "MessageCountUtilizationPct";
        public final static String TransactionResourceUtilizationPct = "TransactionResourceUtilizationPct";
        public final static String TransactedSessionCountUtilizationPct = "TransactedSessionCountUtilizationPct";
        public final static String DeliveredUnackedMsgsUtilizationPct = "DeliveredUnackedMsgsUtilizationPct";
        public final static String SpoolFilesUtilizationPercentage = "SpoolFilesUtilizationPercentage";
    }

    public class Statistics {
        public final static String PREFIX = "Statistics";

        public final static String CurrentIngressRatePerSecond = "CurrentIngressRatePerSecond";
        public final static String CurrentEgressRatePerSecond = "CurrentEgressRatePerSecond";
        public final static String CurrentIngressByteRatePerSecond = "CurrentIngressByteRatePerSecond";
        public final static String CurrentEgressByteRatePerSecond = "CurrentEgressByteRatePerSecond";
        public final static String CurrentIngressCompressedRatePerSecond = "CurrentIngressCompressedRatePerSecond";
        public final static String CurrentEgressCompressedRatePerSecond = "CurrentEgressCompressedRatePerSecond";
        public final static String IngressCompressionRatio = "IngressCompressionRatio";
        public final static String EgressCompressionRatio = "EgressCompressionRatio";
        public final static String CurrentIngressSslRatePerSecond = "CurrentIngressSslRatePerSecond";
        public final static String CurrentEgressSslRatePerSecond = "CurrentEgressSslRatePerSecond";
        public final static String TotalClientsConnected = "TotalClientsConnected";
        public final static String TotalSmfClientsConnected = "TotalSmfClientsConnected";
        public final static String TotalClientDataMessagesReceived = "TotalClientDataMessagesReceived";
        public final static String TotalClientDataMessagesSent = "TotalClientDataMessagesSent";
        // Ingress discards
        public final static String TotalIngressDiscards = "Discards|TotalIngressDiscards";
        public final static String NoSubscriptionMatch = "Discards|Ingress|NoSubscriptionMatch";
        public final static String TopicParseError = "Discards|Ingress|TopicParseError";
        public final static String ParseError = "Discards|Ingress|ParseError";
        public final static String MsgTooBig = "Discards|Ingress|MsgTooBig";
        public final static String TtlExceeded = "Discards|Ingress|TtlExceeded";
        public final static String WebParseError = "Discards|Ingress|WebParseError";
        public final static String PublishTopicAcl = "Discards|Ingress|PublishTopicAcl";
        public final static String MsgSpoolDiscards = "Discards|Ingress|MsgSpoolDiscards";
        public final static String IngressMessagePromotionCongestion = "Discards|Ingress|IngressMessagePromotionCongestion";
        public final static String IngressMessageSpoolCongestion = "Discards|Ingress|IngressMessageSpoolCongestion";
        // Egress discards
        public final static String TotalEgressDiscards = "Discards|TotalEgressDiscards";
        public final static String TransmitCongestion = "Discards|Egress|TransmitCongestion";
        public final static String CompressionCongestion = "Discards|Egress|CompressionCongestion";
        public final static String MessageElided = "Discards|Egress|MessageElided";
        public final static String PayloadCouldNotBeFormatted = "Discards|Egress|PayloadCouldNotBeFormatted";
        public final static String EgressMessagePromotionCongestion = "Discards|Egress|EgressMessagePromotionCongestion";
        public final static String EgressMessageSpoolCongestion = "Discards|Egress|EgressMessageSpoolCongestion";
        public final static String MsgSpoolEgressDiscards = "Discards|Egress|MsgSpoolEgressDiscards";
    }

    public class Vpn {
        public final static String PREFIX = "MsgVpns";

        public final static String VpnName = "VpnName";
        public final static String IsEnabled = "IsEnabled";
        public final static String OperationalStatus = "OperationalStatus";
        public final static String QuotaInMB = "QuotaInMB";
        public final static String UsageInMB = "UsageInMB";
        public final static String SMFConnectionsPct = "SMFConnectionsPct";
        public final static String TotalEndpointsCount = "TotalEndpointsCount";
        public final static String TotalMessagesSpooledCount = "TotalMessagesSpooledCount";
        public final static String TotalClientsConnected = "TotalClientsConnected";
        public final static String RestPortUp = "RestPortUp";
        public final static String CurrentIngressFlowsCount = "CurrentIngressFlowsCount";
        public final static String CurrentEgressFlowsCount = "CurrentEgressFlowsCount";
        public final static String CurrentIngressRatePerSecond = "CurrentIngressRatePerSecond";
        public final static String CurrentEgressRatePerSecond = "CurrentEgressRatePerSecond";
        public final static String CurrentIngressByteRatePerSecond = "CurrentIngressByteRatePerSecond";
        public final static String CurrentEgressByteRatePerSecond = "CurrentEgressByteRatePerSecond";
        public final static String TotalClientDataMessagesReceived = "TotalClientDataMessagesReceived";
        public final static String TotalClientDataMessagesSent = "TotalClientDataMessagesSent";
        // Ingress discards
        public final static String TotalIngressDiscards = "Discards|TotalIngressDiscards";
        public final static String NoSubscriptionMatch = "Discards|Ingress|NoSubscriptionMatch";
        public final static String TopicParseError = "Discards|Ingress|TopicParseError";
        public final static String ParseError = "Discards|Ingress|ParseError";
        public final static String MsgTooBig = "Discards|Ingress|MsgTooBig";
        public final static String TtlExceeded = "Discards|Ingress|TtlExceeded";
        public final static String WebParseError = "Discards|Ingress|WebParseError";
        public final static String PublishTopicAcl = "Discards|Ingress|PublishTopicAcl";
        public final static String MsgSpoolDiscards = "Discards|Ingress|MsgSpoolDiscards";
        public final static String IngressMessagePromotionCongestion = "Discards|Ingress|IngressMessagePromotionCongestion";
        public final static String IngressMessageSpoolCongestion = "Discards|Ingress|IngressMessageSpoolCongestion";
        // Egress discards
        public final static String TotalEgressDiscards = "Discards|TotalEgressDiscards";
        public final static String TransmitCongestion = "Discards|Egress|TransmitCongestion";
        public final static String CompressionCongestion = "Discards|Egress|CompressionCongestion";
        public final static String MessageElided = "Discards|Egress|MessageElided";
        public final static String PayloadCouldNotBeFormatted = "Discards|Egress|PayloadCouldNotBeFormatted";
        public final static String EgressMessagePromotionCongestion = "Discards|Egress|EgressMessagePromotionCongestion";
        public final static String EgressMessageSpoolCongestion = "Discards|Egress|EgressMessageSpoolCongestion";
        public final static String MsgSpoolEgressDiscards = "Discards|Egress|MsgSpoolEgressDiscards";
    }

    public class Queue {
        public final static String PREFIX = "Queues";

        public final static String QueueName = "QueueName";
        public final static String VpnName = "VpnName";
        public final static String IsEnabled = "IsEnabled";
        public final static String IsDurable = "IsDurable";
        public final static String QuotaInMB = "QuotaInMB";
        public final static String MessagesSpooled = "MessagesSpooled";
        public final static String UsageInMB = "UsageInMB";
        public final static String ConsumerCount = "ConsumerCount";
        public final static String RedeliveredCount = "RedeliveredCount";
        public final static String TotalMessagesSpooled = "TotalMessagesSpooled";
        public final static String CurrentIngressRatePerSecond = "CurrentIngressRatePerSecond";
        public final static String CurrentEgressRatePerSecond = "CurrentEgressRatePerSecond";
        public final static String CurrentIngressByteRatePerSecond = "CurrentIngressByteRatePerSecond";
        public final static String CurrentEgressByteRatePerSecond = "CurrentEgressByteRatePerSecond";

        // Ingress discards
        public final static String TotalIngressDiscards = "Discards|TotalIngressDiscards";
        public final static String MsgSpoolDiscards = "Discards|Ingress|MsgSpoolDiscards";
        public final static String MsgTooBig = "Discards|Ingress|MsgTooBig";
        public final static String SpoolShutdown = "Discards|Ingress|SpoolShutdown";
        public final static String UserProfileDenial = "Discards|Ingress|UserProfileDenial";
        public final static String NoLocalDelivery = "Discards|Ingress|NoLocalDelivery";
        public final static String DestinationGroupError = "Discards|Ingress|DestinationGroupError";
        public final static String LowPriorityMsgCongestion = "Discards|Ingress|LowPriorityMsgCongestion";
        // Egress discards
        public final static String TotalEgressDiscards = "Discards|TotalEgressDiscards";
        public final static String TTLExceeded = "Discards|Egress|TTLExceeded";
        public final static String TTLExpiredToDMQ = "Discards|Egress|TTLExpiredToDMQ";
        public final static String TTLExpireToDMQFailed = "Discards|Egress|TTLExpireToDMQFailed";
        public final static String MaxRedelivery = "Discards|Egress|MaxRedelivery";
        public final static String MaxRedeliveryToDMQ = "Discards|Egress|MaxRedeliveryToDMQ";
        public final static String MaxRedeliveryToDMQFailed = "Discards|Egress|MaxRedeliveryToDMQFailed";
    }

    public class TopicEndpoint {
        public final static String PREFIX = "TopicEndpoints";

        public final static String TopicEndpointName = "TopicEndpointName";
        public final static String VpnName = "VpnName";
        public final static String IsEnabled = "IsEnabled";
        public final static String IsDurable = "IsDurable";
        public final static String QuotaInMB = "QuotaInMB";
        public final static String MessagesSpooled = "MessagesSpooled";
        public final static String UsageInMB = "UsageInMB";
        public final static String ConsumerCount = "ConsumerCount";
        public final static String RedeliveredCount = "RedeliveredCount";
        public final static String TotalMessagesSpooled = "TotalMessagesSpooled";
        public final static String CurrentIngressRatePerSecond = "CurrentIngressRatePerSecond";
        public final static String CurrentEgressRatePerSecond = "CurrentEgressRatePerSecond";
        public final static String CurrentIngressByteRatePerSecond = "CurrentIngressByteRatePerSecond";
        public final static String CurrentEgressByteRatePerSecond = "CurrentEgressByteRatePerSecond";

        // Ingress discards
        public final static String TotalIngressDiscards = "Discards|TotalIngressDiscards";
        public final static String MsgSpoolDiscards = "Discards|Ingress|MsgSpoolDiscards";
        public final static String MsgTooBig = "Discards|Ingress|MsgTooBig";
        public final static String SpoolShutdown = "Discards|Ingress|SpoolShutdown";
        public final static String UserProfileDenial = "Discards|Ingress|UserProfileDenial";
        public final static String NoLocalDelivery = "Discards|Ingress|NoLocalDelivery";
        public final static String DestinationGroupError = "Discards|Ingress|DestinationGroupError";
        public final static String LowPriorityMsgCongestion = "Discards|Ingress|LowPriorityMsgCongestion";
        // Egress discards
        public final static String TotalEgressDiscards = "Discards|TotalEgressDiscards";
        public final static String TTLExceeded = "Discards|Egress|TTLExceeded";
        public final static String TTLExpiredToDMQ = "Discards|Egress|TTLExpiredToDMQ";
        public final static String TTLExpireToDMQFailed = "Discards|Egress|TTLExpireToDMQFailed";
        public final static String MaxRedelivery = "Discards|Egress|MaxRedelivery";
        public final static String MaxRedeliveryToDMQ = "Discards|Egress|MaxRedeliveryToDMQ";
        public final static String MaxRedeliveryToDMQFailed = "Discards|Egress|MaxRedeliveryToDMQFailed";
    }

    public final class Bridge {
        public final static String PREFIX = "Bridges";

        public final static String BridgeName = "BridgeName";
        public final static String VpnName = "VpnName";
        public final static String IsEnabled = "IsEnabled";
        public final static String IsConnected = "IsConnected";
        public final static String IsInSync = "IsInSync";
        public final static String IsBoundToBridgeQueue = "IsBoundToBridgeQueue";
    }

    public final class Derived {
        public final static String PREFIX = "Derived";

        public final static String DataSvcOk = "DataSvcOk";
        public final static String MsgSpoolOk = "MsgSpoolOk";
    }
}
