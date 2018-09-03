# solace-monitoring-extension
This java library provides an AppDynamics MachineAgent extension to query 
Solace message brokers and update an ADController.

This extension works only with the java standalone machine agent. It has been tested 
against a Machine Agent v4.3.7.3 GA build.

Solace is a multi-protocol Message-Oriented middleware, providing a self-contained 
standalone message broker server in both hardware and containerized formats.
Statistics and metrics on Solace performance can be queried from the management 
plane via HTTP. The following metrics are currently gathered by this monitor:

* Global messaging statistics
* Global HA redundancy metrics
* Global service status
* Global queue summaries
* Global durable topic endpoint summaries
* Global bridge summaries

Detailed documentation of all available metrics can be found [here](docs/metrics.md).

## Installation

The protocol for querying metrics is currently an XML-based POST request protocol. 
Schemas for request and reply messages are provided by Solace, which can be used 
to generate Java request and reply objects. I have a project for generating these 
libraries and installing them to Maven repositories: https://github.com/koverton/semp_jaxb

I have provided initial libraries for the latest Solace hardware and software versions 
in the `mvn_libs/` directory which you can install to your maven repository to build and deploy 
via maven:

```bash
cd mvn_libs
./install.sh
```

You will also need to obtain the AppDynamics `machine-agent.jar` and `appd-exts-commons.jar` 
appropriate to the version of your AppDynamics MachineAgent. You should get this from AppDynamics.

### Build and Deploy Steps

1. Type `mvn clean install` at the commandline from the solace-monitoring-extension directory
2. Deploy the output file `target/SolaceMonitor.zip` into `<machineagent install dir>/monitors/`
3. Unzip the deployed file
4. Restart the MachineAgent
5. In the AppDynamics Metric Browser, look for: 
> Application Infrastructure Performance  | \<Tier\> | Custom Metrics | Solace.

## Storing Passwords in the Extension `config.yml`

For simple Test or Dev environments Solace admin credentials can be stored in the 
config.yml file in cleartext. For each server in your configuration, the `password` 
configuration value is assumed to be cleartext and is used by the extension in that manner.
For higher environments, the best practice is to only store encrypted versions of the 
passwords; the Solace monitoring extension provides the following support for that.

A script for encrypting your passwords is included in the distributable bundle that 
can be run at the commandline with your own hash-key. The output of that script can 
be stored in the `encryptedPassword` field of each server credentials instead of `password`.

```bash
Linux$ ./encrypt.sh na1jFUKT8euDXp1p7bgwIxhI6ZESLylz # <-- my hash-key
Enter password to encrypt:
***************Encrypted String***************
1U57HGivpI0szB6X7n6tKw==
**********************************************
```

It is best practice to randomly generate a strong hash-key rather than entering something yourself. 
The hash-key you use to encrypt the password must also be saved, we recommend adding it to the 
MachineAgent commandline as the env-property `appdynamics.extensions.key`. Typically you will 
have other definitions, for example:
```bash
 java -Dappdynamics.agent.applicationName=<appname> \
      -Dappdynamics.agent.tierName=<app-teir> \
      -Dappdynamics.extensions.key=<your-hash-key> \
      -jar $ADAGENT_HOME/machineagent.jar
```

If for any reason you cannot set this property via the MachineAgent commandline it can alternatively 
be added to the extension's `config.yml` as the server configuration `encryptionKey`. Note this 
must be present for each server configuration specifying `encryptedPassword`.
	
## Directory Structure

<table><tbody>
<tr>
<th align="left"> Directory/File </th>
<th align="left"> Description </th>
</tr>
<tr>
<td class='confluenceTd'> mvn_libs </td>
<td class='confluenceTd'> Contains pre-built SEMP serialization libs required for this build and an installer script </td>
</tr>
<tr>
<td class='confluenceTd'> src/main/resources/conf </td>
<td class='confluenceTd'> Contains the extension config.yml and monitor.xml </td>
</tr>
<tr>
<td class='confluenceTd'> src/main/java </td>
<td class='confluenceTd'> Contains source code of the Solace monitoring extension </td>
</tr>
<tr>
<td class='confluenceTd'> target/SolaceMonitor.zip </td>
<td class='confluenceTd'> Distributable .zip artifact. Only obtained when using maven, run 'mvn clean install' to generate. </td>
</tr>
<tr>
<td class='confluenceTd'> pom.xml </td>
<td class='confluenceTd'> Maven build script used to compile and package the distributable bundle </td>
</tr>
</tbody>
</table>

## Configuring

The `monitor.xml` file should not require any modification, it gives the 
AppDynamics MachineAgent all the necessary information to load and run the 
Solace monitoring extension.

The `config.yml` file is how you configure the Solace monitoring extension to 
monitor Solace message brokers, and therefore should be modified to reflect the 
Solace deployment you want to monitor.

<table><tbody>
<tr>
<th align="left"> Config.yml Configuration </th>
<th align="left"> Description </th>
</tr>
<tr>
<td class='confluenceTd'> <tt>metricPrefix</tt> </td>
<td class='confluenceTd'>
This will create this metric in a specific component so that the same
custom metric name will be distinct under different applications
see [this AppDynamics KB post](https://community.appdynamics.com/t5/Knowledge-Base/How-to-troubleshoot-missing-custom-metrics-or-extensions-metrics/ta-p/28695)
</td>
</tr>
<tr>
<td class='confluenceTd'> <tt>Servers</tt> </td>
<td class='confluenceTd'> List of all server details to query and upload to the Controller; 
see table below for each server configuration </td>
</tr>
</tbody>
</table>

<table><tbody>
<tr>
<th align="left"> Server Configuration </th>
<th align="left"> Description </th>
</tr>
<tr>
<td class='confluenceTd'> <tt>displayName</tt> </td>
<td class='confluenceTd'> Server name added to the metric path for this node </td>
</tr>
<tr>
<td class='confluenceTd'> <tt>mgmtUrl</tt> </td>
<td class='confluenceTd'> SEMP URL for server queries; format should be http://&lt;server-address&gt;:8080/SEMP for 
Solace VMR instances and http://&lt;mgmt-address&gt;/SEMP for Solace hardware instances.
</td>
</tr>
<tr>
<td class='confluenceTd'> <tt>adminUser</tt> </td>
<td class='confluenceTd'> admin username to use for querying; can be a readonly user </td>
</tr>
<tr>
<td class='confluenceTd'> <tt>password</tt> or <tt>encryptedPassword</tt> </td>
<td class='confluenceTd'> <p>HTTP authentication credentials used for the admin user. <tt>password</tt> 
assumes the credentials are stored directly as cleartext, <tt>encryptedPassword</tt> assumes the 
credentials are stored in encryped form as generated by your hash-key and the bundle's encrypt.sh script.
</p>
<blockquote><em>NOTE: if both properties are present in the configuration, <tt>password</tt> will be preferred over 
<tt>encryptedPassword</tt></em>.</blockquote>
</td>
</tr>

<tr>
<td class='confluenceTd'> (OPTIONAL:) <tt>encryptionKey</tt> </td>
<td class='confluenceTd'> <p>Key used to decrypt the value from the <tt>encryptedPassword</tt> field; assumes the 
encrypted credentials were generated by the <tt>encryptionKey</tt> value and the bundle's <tt>encrypt.sh</tt> script.
</p>
</blockquote>
</td>
</tr>

<tr>
<td class='confluenceTd'> <tt>vpnExclusionPolicy</tt> </td>
<td class='confluenceTd'> <tt>[ WHITELIST | BLACKLIST ]</tt>: Determines how the <tt>excludeMsgVpns</tt> configuration
will be applied. a WHITELIST treats it as a list of specific VPNs to be monitored, a BLACKLIST treats it
as a list of VPNs NOT to monitor.</td>
</tr>
<tr>
<td class='confluenceTd'> <tt>excludeMsgVpns</tt> </td>
<td class='confluenceTd'> List of regex patterns of MsgVPNs either whitelisted or blacklisted on this server.
See Java Regex Pattern documentation: https://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html
</td>
</tr>
<tr>
<td class='confluenceTd'> <tt>queueExclusionPolicy</tt> </td>
<td class='confluenceTd'> <tt>[ WHITELIST | BLACKLIST ]</tt>: Determines how the <tt>excludeQueues</tt> configuration
will be applied. a WHITELIST treats it as a list of specific Queues to be monitored, a BLACKLIST treats
it as a list of Queues NOT to monitor.</td>
</tr>
<tr>
<td class='confluenceTd'> <tt>excludeQueues</tt> </td>
<td class='confluenceTd'> List of regex patterns of Queues either whitelisted or blacklisted on this server.
See Java Regex Pattern documentation: https://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html
</td>
</tr>
<tr>
<td class='confluenceTd'> <tt>topicEndpointExclusionPolicy</tt> </td>
<td class='confluenceTd'> <tt>[ WHITELIST | BLACKLIST ]</tt>: Determines how the <tt>excludeTopicEndpoints</tt> configuration
will be applied. a WHITELIST treats it as a list of specific Topic-Endpoints to be monitored, a BLACKLIST treats
it as a list of Topic-Endpoints NOT to monitor.</td>
</tr>
<tr>
<td class='confluenceTd'> <tt>excludeTopicEndpoints</tt> </td>
<td class='confluenceTd'> List of regex patterns of Topic-Endpoints either whitelisted or blacklisted on this server.
See Java Regex Pattern documentation: https://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html
</td>
</tr>


<tr>
<td class='confluenceTd'> <tt>excludeTemporaries</tt> </td>
<td class='confluenceTd'> [ Boolean : default TRUE ] Indicates whether or not to ignore temporary Queues and
temporary Topic-Endpoints.
</td>
<tr>
<td class='confluenceTd'> <tt>excludeTlsMetrics</tt> </td>
<td class='confluenceTd'> [ Boolean : default TRUE ] Indicates whether or not to ignore statistical metrics for TLS performance.
If you aren't using TLS this can reduce the number of metrics uploaded to your controller.
</td>
<tr>
<td class='confluenceTd'> <tt>excludeCompressionMetrics</tt> </td>
<td class='confluenceTd'> [ Boolean : default TRUE ] Indicates whether or not to ignore statistical metrics for Compressed connections performance.
If you aren't using compression this can reduce the number of metrics uploaded to your controller.
</td>
<tr>
<td class='confluenceTd'> <tt>excludeDiscardMetrics</tt> </td>
<td class='confluenceTd'> [ Boolean : default TRUE ] Indicates whether or not to ignore message-discard metrics.
If discard metrics are not useful to you for high-level monitoring, this can reduce the number of metrics
uploaded to your controller.
</td>
</tr>

</tbody>
</table>

## Example Configuration

```yaml

# This will create this metric in a specific component so that the same
# custom metric name will be distinct under different applications
# see: https://community.appdynamics.com/t5/Knowledge-Base/How-to-troubleshoot-missing-custom-metrics-or-extensions-metrics/ta-p/28695
#This will create this metric in all the tiers, under this path
metricPrefix: "Server|Component:2345263|Custom Metrics|Solace|"

# number of concurrent tasks
numberOfThreads: 2

# List of Solace Servers
servers:
  - mgmtUrl: "http://192.168.56.201:8080/SEMP"
    adminUser: "admin"
    password: "admin"
    #displayName is required. Displays your server name in metric path.
    displayName: "PrimaryVMR"
    vpnExclusionPolicy: "blacklist"
    excludeMsgVpns: ["#config-sync", "default"]
    queueExclusionPolicy: "whitelist"
    excludeQueues: ["onlyThis", "andThis"]
    topicEndpointExclusionPolicy: "blacklist"
    excludeTopicEndpoints: ["log/.*"]
    excludeTemporaries: true
  - mgmtUrl: "http://192.168.56.202:8080/SEMP"
    adminUser: "admin"
    encryptedPassword: "fHu/E8G0rGuCBXAWHvuoIA=="
    #displayName is required. Displays your server name in metric path.
    displayName: "BackupVMR"
    vpnExclusionPolicy: "blacklist"
    excludeMsgVpns: ["#config-sync", "default"]
    queueExclusionPolicy: "blacklist"
    excludeQueues: ["#P2P/.*"]
    topicEndpointExclusionPolicy: "blacklist"
    excludeTopicEndpoints: ["log/.*"]
    excludeTemporaries: false
    excludeTlsMetrics: false
    excludeDiscardMetrics: false
  - mgmtUrl: "http://192.168.56.203:8080/SEMP"
    adminUser: "admin"
    password: "admin"
    #displayName is required. Displays your server name in metric path.
    displayName: "MonitorVMR"
    vpnExclusionPolicy: "blacklist"
    excludeMsgVpns: ["#config-sync", "default"]
    queueExclusionPolicy: "whitelist"
    excludeQueues: ["onlyThis", "andThis"]
    excludeTopicEndpoints: ["selector1"]
    excludeCompressionMetrics: false
    excludeDiscardMetrics: false
```

## Latest Updates
1. Fix redundancy status for hardware queries (as of 8.2.0)
2. Per-metric documentation
3. Exclusion lists now accept regular-expression patterns
4. Optional setting to exclude monitoring all temporary endpoints


## Next Steps / TODO Items

In priority order:
1. Add more statistics to all entities; current version emphasizes status checks
2. More backwards compatibility schemas
3. Better calculation to match parsing schema version to the version presented by the router
4. When many schemas need to be supported, should load only the servers' required libs dynamically via child classloaders

Additional metrics that make sense to add:
1. Client connections
2. DR links