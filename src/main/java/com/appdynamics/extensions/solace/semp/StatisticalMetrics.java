package com.appdynamics.extensions.solace.semp;

public class StatisticalMetrics {
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
