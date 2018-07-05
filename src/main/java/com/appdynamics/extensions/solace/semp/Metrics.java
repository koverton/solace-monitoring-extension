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

        public final static String SmfPort = "SmfPort";
        public final static String SmfPortUp = "SmfPortUp";
        public final static String SmfCompressedPort = "SmfCompressedPort";
        public final static String SmfCompressedPortUp = "SmfCompressedPortUp";
        public final static String SmfSslPort = "SmfSslPort";
        public final static String SmfSslPortUp = "SmfSslPortUp";
        public final static String WebPort = "WebPort";
        public final static String WebPortUp = "WebPortUp";
        public final static String WebSslPort = "WebSslPort";
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
        public final static String IsIngressEnabled = "IsIngressEnabled";
        public final static String IsEgressEnabled = "IsEgressEnabled";
        public final static String IsDurable = "IsDurable";
        public final static String QuotaInMB = "QuotaInMB";
        public final static String MessagesEnqueued = "MessagesEnqueued";
        public final static String UsageInMB = "UsageInMB";
        public final static String ConsumerCount = "ConsumerCount";
        public final static String OldestMsgId = "OldestMsgId";
        public final static String NewestMsgId = "NewestMsgId";
        public final static String CurrentIngressRatePerSecond = "CurrentIngressRatePerSecond";
        public final static String CurrentEgressRatePerSecond = "CurrentEgressRatePerSecond";
        public final static String CurrentIngressByteRatePerSecond = "CurrentIngressByteRatePerSecond";
        public final static String CurrentEgressByteRatePerSecond = "CurrentEgressByteRatePerSecond";
    }

    public class TopicEndpoint {
        public final static String PREFIX = "TopicEndpoints";

        public final static String TopicEndpointName = "TopicEndpointName";
        public final static String VpnName = "VpnName";
        public final static String IsIngressEnabled = "IsIngressEnabled";
        public final static String IsEgressEnabled = "IsEgressEnabled";
        public final static String IsDurable = "IsDurable";
        public final static String QuotaInMB = "QuotaInMB";
        public final static String MessagesSpooled = "MessagesSpooled";
        public final static String UsageInMB = "UsageInMB";
        public final static String ConsumerCount = "ConsumerCount";
        public final static String OldestMsgId = "OldestMsgId";
        public final static String NewestMsgId = "NewestMsgId";
        public final static String CurrentIngressRatePerSecond = "CurrentIngressRatePerSecond";
        public final static String CurrentEgressRatePerSecond = "CurrentEgressRatePerSecond";
        public final static String CurrentIngressByteRatePerSecond = "CurrentIngressByteRatePerSecond";
        public final static String CurrentEgressByteRatePerSecond = "CurrentEgressByteRatePerSecond";
    }

    public final class Bridge {
        public final static String PREFIX = "Bridges";

        public final static String BridgeName = "BridgeName";
        public final static String VpnName = "VpnName";
        public final static String IsEnabled = "IsEnabled";
        public final static String IsConnected = "IsConnected";
        public final static String IsInSync = "IsInSync";
        public final static String IsBoundToBridgeQueue = "IsBoundToBridgeQueue";
        public final static String UptimeInSecs = "UptimeInSecs";
    }

    public final class Derived {
        public final static String PREFIX = "Derived";

        public final static String DataSvcOk = "DataSvcOk";
        public final static String MsgSpoolOk = "MsgSpoolOk";
    }
}
