# Solace Monitor Statistics

The Following statistics are gathered per each Solace VMR or Messaging Appliance. The `<instance-name>` used in the Metrics Path is configured into the AD Solace Monitoring Agent configuration. It is not configured on the Solace node.

![](AD_ext_hierarchy.png)

## Derived Indicators: 

Metrics Prefix: `Custom Metrics|Solace|<instance-name>|Derived|`

Top-level boolean indicators that are derived from of the below metrics. These indicators consist of complex boolean expressions that could not be easily expressed in AppD dashboards.

| Metric        | Value | Description |
|---------------|-------|-------------|
| DataSvcOk     | Boolean | 1 indicates this VMR or Messaging Appliance is UP and distributing messages as expected. |
| MsgSpoolOk    | Boolean | 1 indicates this VMR or Messaging Appliance is UP and persisting messages to the message-spool as expected. |


## Global Statistics: 

Metrics Prefix: `Custom Metrics|Solace|<instance-name>|Statistics|`

Aggregate data throughput statistics gathered for the entire VMR or Messaging Appliance.

| Metric                           | Value | Description |
|----------------------------------|-------|-------------|
| _Aggregate Throughput Stats_ |
| CurrentIngressRatePerSecond      | Integer| Current inbound message rate per second |
| CurrentEgressRatePerSecond       | Integer| Current outbound message rate per second |
| CurrentIngressByteRatePerSecond  | Integer| Current inbound byte rate per second |
| CurrentEgressByteRatePerSecond   | Integer| Current outbound byte rate per second |
| _Compressed Stats_ |
| CurrentIngressCompressedRatePerSecond | Integer| Current compressed inbound byte rate per second |
| CurrentEgressCompressedRatePerSecond  | Integer| Current compressed outbound byte rate per second |
| IngressCompressionRatio          | Integer| Current compression ratio of inbound data |
| EgressCompressionRatio           | Integer | Current compression ratio of outbound data |
| _TLS Stats_ |
| CurrentIngressSslRatePerSecond   | Integer | Current SSL inbound byte rate per second |
| CurrentEgressSslRatePerSecond    | Integer | Current SSL outbound byte rate per second |


## Global Discard Statistics: 

Metrics Prefix: `Custom Metrics|Solace|<instance-name>|Statistics|Discards|`

Aggregate discard statistics for all inbound and outbound traffic. These are counters incremented since the VMR or Messaging Appliance is booted or since the counter was administratively cleared.

| Metric                           | Value | Description |
|----------------------------------|-------|-------------|
| TotalIngressDiscards             | Integer | Aggregate number of messages discarded upon arrival since boot time. |
| TotalEgressDiscards              | Integer | Aggregate number of messages discarded after arrival since boot time. |

### Global Ingress-Discard Statistics:  

Metrics Prefix: `Custom Metrics|Solace|<instance-name>|Statistics|Discards|Ingress|`

Aggregate inbound discard statistics. These are counters incremented since the VMR or Messaging Appliance is booted or since the counter was administratively cleared.

| Metric                           | Value | Description |
|----------------------------------|-------|-------------|
| NoSubscriptionMatch              | Integer | Aggregate number of messages discarded because they did not match any subscription since boot time. |
| TopicParseError                  | Integer | Aggregate number of messages discarded because the topic of the message could not be parsed since boot time. |
| ParseError                       | Integer | Aggregate number of messages discarded because the message could not be parsed since boot time. |
| MsgTooBig                        | Integer | Aggregate number of messages discarded because the message was larger than allowed since boot time. |
| TtlExceeded                      | Integer | Aggregate number of messages discarded because the message Time-to-Live expired since boot time. |
| WebParseError                    | Integer | Aggregate number of web-messages discarded because the message could not be parsed since boot time. |
| PublishTopicAcl                  | Integer | Aggregate number of messages discarded because the publisher was restricted by an ACL rule since boot time. |
| MsgSpoolDiscards                 | Integer | Aggregate number of messages discarded because of a msg-spool limit since boot time. |
| IngressMessagePromotionCongestion| Integer | Aggregate number of messages discarded due to congestion from persisting DIRECT messages since boot time. |
| IngressMessageSpoolCongestion    | Integer | Aggregate number of guaranteed messages discarded because of low-priority discard rules since boot time. |

### Global Egress-Discard Statistics: 

Metrics Prefix: `Custom Metrics|Solace|<instance-name>|Statistics|Discards|Egress|`

Aggregate outbound discard statistics. These are counters incremented since the VMR or Messaging Appliance is booted or since the counter was administratively cleared.

| Metric                           | Value | Description |
|----------------------------------|-------|-------------|
| TransmitCongestion               | Integer | Aggregate number of Direct messages discarded due to overflow of the outbound msg-buffer since boot time. |
| CompressionCongestion            | Integer | Aggregate number of Direct messages discarded due to overflow of the compression buffer since boot time. |
| MessageElided                    | Integer | Aggregate number of Direct messages discarded due to client eliding rules since boot time. |
| PayloadCouldNotBeFormatted       | Integer | Aggregate number of messages discarded due to errors processing outbound msg-payload since boot time. |
| EgressMessagePromotionCongestion | Integer | Aggregate number of direct messages discarded due to inability to promote the message to persistent since boot time. |
| EgressMessageSpoolCongestion     | Integer | Aggregate number of guaranteed messages rejected to sender due to msg-spool issue on a queue with Reject-Msg-to-Sender-on-Discard set since boot time. |
| MsgSpoolEgressDiscards           | Integer | Aggregate number of guaranteed messages discarded due to persistend endpoint overflow since boot time. |


## Global Msg-Spool: 

Metrics Prefix: `Custom Metrics|Solace|<instance-name>|MsgSpool|`

Global message-spool indicators and statistics.

| Metric                           | Value | Description |
|----------------------------------|-------|-------------|
| IsEnabled                        | Boolean | 1 indicates the msg-spool is enabled in the configuration. |
| IsActive                         | Boolean | 1 indicates the msg-spool is currently active in the HA-Cluster. |
| IsDatapathUp                     | Boolean | 1 indicates the msg-spool is currently actively using the storage volume. |
| IsSynchronized                   | Boolean | 1 indicates the msg-spool on this node is in sync with the peer node. |
| MessageCountUtilizationPct          | Percent | Percent utilization of total available message reference count. |
| TransactionResourceUtilizationPct   | Percent | Percent utilization of total available transactions. |
| TransactedSessionCountUtilizationPct| Percent | Percent utilization of total available transacted sessions. |
| DeliveredUnackedMsgsUtilizationPct  | Percent | Percent utilization of total available delivered but unacked messages. |
| SpoolFilesUtilizationPercentage     | Percent | Percent utilization of total available spool files on this node. |


## Global Redundancy: 

Metrics Prefix: `Custom Metrics|Solace|<instance-name>|Redundancy|`

Global Redundancy indicators.

| Metric           | Value | Description |
|------------------|-------|-------------|
| ConfiguredStatus | Boolean | 1 if redundancy is Enabled in the configuration in this node. |
| OperationalStatus| Boolean | 1 if redundancy is currently operational in this node. |
| IsActive         | Boolean | 1 if this node is currectly the Active message-routing node in its redundant cluster. |


## Global Service Status: 

Metrics Prefix: `Custom Metrics|Solace|<instance-name>|Services|`

Global Service status and indicators.

| Metric                         | Value | Description |
|--------------------------------|-------|-------------|
| _SMF Service_                  |
| SmfPort                        | Integer | Port number for the Solace Message Format sessions. |
| SmfPortUp                      | Boolean | 1 if the SMF port is accepting connections. |
| _Compressed SMF Service_       |
| SmfCompressedPort              | Integer | Port number for compressed SMF sessions. |
| SmfCompressedPortUp            | Boolean | 1 if the Compressed-SMF port is accepting connections. |
| _Encrypted SMF Service_        |
| SmfSslPort                     | Integer | Port number for SMF over TLS sessions. |
| SmfSslPortUp                   | Boolean | 1 if the TLS-SMF port is accepting connections. |
| _Web-Socket Service_           |
| WebPort                        | Integer | Port number for web-socket sessions. |
| WebPortUp                      | Boolean | 1 if the Web-Socket port is accepting connections. |
| _Encrypted Web-Socket Service_ |
| WebSslPort                     | Integer | Port number for web-sockets over TLS sessions. |
| WebSslPortUp                   | Boolean | 1 if the TLS Web-Socket port is accepting connections. |

## MSG-VPN SCOPED RESOURCES: `Custom Metrics|Solace|<instance-name>|MsgVpns|<vpn-name>|`

The following statistics are gathered per each message-VPN configured on the VMR or Messaging Appliance.

### Queue List: 

Metrics Prefix: `Custom Metrics|Solace|<instance-name>|MsgVpns|<vpn-name>|Queues|`

Statistics and indicators per each queue in a msg-VPN.

| Metric                           | Value | Description |
|----------------------------------|-------|-------------|
| IsIngressEnabled                 | Boolean | 1 if the queue configuration has enabled the queue for publishers. |
| IsEgressEnabled                  | Boolean | 1 if the queue configuration has enabled the queue for consumers. |
| IsDurable                        | Boolean | 1 if the queue is durable (i.e. not temporary) . |
| QuotaInMB                        | Integer | Max spool-usage for this queue in MB. |
| MessagesEnqueued                 | Integer | Current number of messages enqueued on this endpoint. |
| UsageInMB                        | Integer | Current spool-usage for this queue in MB. |
| ConsumerCount                    | Integer | Current number of consumers bound to this queue. |
| CurrentIngressRatePerSecond      | Integer| Queue current inbound message rate per second |
| CurrentEgressRatePerSecond       | Integer| Queue current outbound message rate per second |
| CurrentIngressByteRatePerSecond  | Integer| Queue current inbound byte rate per second |
| CurrentEgressByteRatePerSecond   | Integer| Queue current outbound byte rate per second |

### Durable TopicEndpoint List:

Metrics Prefix: `Custom Metrics|Solace|<instance-name>|MsgVpns|<vpn-name>|TopicEndpoints|`

Statistics and indicators per each topic endpoint in a msg-VPN.

| Metric                           | Value | Description |
|----------------------------------|-------|-------------|
| IsIngressEnabled                 | Boolean | 1 if the endpoint configuration has enabled the endpoint for publishers. |
| IsEgressEnabled                  | Boolean | 1 if the endpoint configuration has enabled the endpoint for consumers. |
| IsDurable                        | Boolean | 1 if the endpoint is durable (i.e. not temporary) . |
| QuotaInMB                        | Integer | Max spool-usage for this endpoint in MB. |
| MessagesSpooled                  | Integer | Current number of messages spooled on this endpoint. |
| UsageInMB                        | Integer | Current spool-usage for this endpoint in MB. |
| ConsumerCount                    | Integer | Current number of consumers bound to this endpoint. |
| CurrentIngressRatePerSecond      | Integer| Topic-Endpoint current inbound message rate per second |
| CurrentEgressRatePerSecond       | Integer| Topic-Endpoint current outbound message rate per second |
| CurrentIngressByteRatePerSecond  | Integer| Topic-Endpoint current inbound byte rate per second |
| CurrentEgressByteRatePerSecond   | Integer| Topic-Endpoint current outbound byte rate per second |

### Bridge List:

Metrics Prefix: `Custom Metrics|Solace|<instance-name>|MsgVpns|<vpn-name>|Bridges|`

Statistics and indicators per each bridge in a msg-VPN.

| Metric               | Value | Description |
|----------------------|-------|-------------|
| IsEnabled            | Boolean | 1 if the bridge configuration has enabled the bridge. |
| IsConnected          | Boolean | 1 if the bridge is currently connected to the remote msg-vpn. |
| IsInSync             | Boolean | 1 if the bridge data transmisssion is currently In-Sync. |
| IsBoundToBridgeQueue | Boolean | 1 if the bridge consumer is bound to the remote bridge-queue. |
| UptimeInSecs         | Integer | Number of seconds the bridge has been UP. |

