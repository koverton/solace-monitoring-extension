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
	public final static String TotalIngressDiscards = "TotalIngressDiscards";
	public final static String NoSubscriptionMatch = "NoSubscriptionMatch";
	public final static String TopicParseError = "TopicParseError";
	public final static String ParseError = "ParseError";
	public final static String MsgTooBig = "MsgTooBig";
	public final static String TtlExceeded = "TtlExceeded";
	public final static String WebParseError = "WebParseError";
	public final static String PublishTopicAcl = "PublishTopicAcl";
	public final static String MsgSpoolDiscards = "MsgSpoolDiscards";
	public final static String IngressMessagePromotionCongestion = "IngressMessagePromotionCongestion";
	public final static String IngressMessageSpoolCongestion = "IngressMessageSpoolCongestion";
	public final static String TotalEgressDiscards = "TotalEgressDiscards";
	public final static String TransmitCongestion = "TransmitCongestion";
	public final static String CompressionCongestion = "CompressionCongestion";
	public final static String MessageElided = "MessageElided";
	public final static String PayloadCouldNotBeFormatted = "PayloadCouldNotBeFormatted";
	public final static String EgressMessagePromotionCongestion = "EgressMessagePromotionCongestion";
	public final static String EgressMessageSpoolCongestion = "EgressMessageSpoolCongestion";
	public final static String MsgSpoolEgressDiscards = "MsgSpoolEgressDiscards";
}
