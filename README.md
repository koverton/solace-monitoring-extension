# solace-monitoring-extension
This java library provides an AppDynamics MachineAgent extension to query 
Solace message brokers and update an ADController.

This extension works only with the java standalone machine agent.

Solace is a multi-protocol Message-Oriented middleware, providing a self-contained  
standalone message broker server in both hardware and containerized formats.
Statistics and metrics on Solace performance can be queried from the management 
plane via HTTP. The following metrics are currently gathered by this monitor:

* Global messaging statistics
* Global HA redundancy metrics
* Global service status
* Global queue summaries

## Installation

The protocol for querying metrics is currently an XML-based POST request protocol. 
Schemas for request and reply messages are provided by Solace, which can be used 
to generate Java request and reply objects. I have a project for generating these 
libraries and installing them to Maven repositories: https://github.com/koverton/semp_jaxb

I have provided initial libraries for the last Solace hardware and software versions 
in the `mvn_libs/` directory which you can install locally to build and deploy with:

```bash
cd mvn_libs
./install.sh
```

1. Type `mvn clean install` in the command line from the solace-monitoring-extension directory
2. Deploy the output file `target/SolaceMonitor.zip` into `<machineagent install dir>/monitors/`
3. Unzip the deployed file
4. Restart the MachineAgent
5. In the AppDynamics Metric Browser, look for: 
> Application Infrastructure Performance  | \<Tier\> | Custom Metrics | Solace.


## Directory Structure

<table><tbody>
<tr>
<th align="left"> Directory/File </th>
<th align="left"> Description </th>
</tr>
<tr>
<td class='confluenceTd'> mvnlibs/ </td>
<td class='confluenceTd'> Contains pre-built SEMP serialization objects required for this build and an installer script </td>
</tr>
<tr>
<td class='confluenceTd'> src/main/resources/conf </td>
<td class='confluenceTd'> Contains the monitor.xml </td>
</tr>
<tr>
<td class='confluenceTd'> src/main/java </td>
<td class='confluenceTd'> Contains source code of the Nginx monitoring extension </td>
</tr>
<tr>
<td class='confluenceTd'> target </td>
<td class='confluenceTd'> Only obtained when using maven. Run 'mvn clean install' to get the distributable .zip file </td>
</tr>
<tr>
<td class='confluenceTd'> pom.xml </td>
<td class='confluenceTd'> Maven build script to package the project (required only if changing Java code) </td>
</tr>
</tbody>
</table>

## Configuring

The `monitor.xml` file should not require any modification, it just gives the 
AppDynamics MachineAgent all the necessary information to load and run the 
extension.

The `config.yml` file is how you configure the Solace monitoring extension to 
monitor Solace message brokers, and therefore should be modified to reflect your 
Solace deployment that you want to monitor.

<table><tbody>
<tr>
<th align="left"> Configuration </th>
<th align="left"> Description </th>
</tr>
<tr>
<td class='confluenceTd'> metricPrefix </td>
<td class='confluenceTd'> This will create this metric in all the tiers, 
under this path; it should always start with Custom Metrics|Solace, but can be varied 
from there onwards.</td>
</tr>
<tr>
<td class='confluenceTd'> Servers </td>
<td class='confluenceTd'> List of server details to query and upload to the Controller; 
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
<td class='confluenceTd'> displayName </td>
<td class='confluenceTd'> Server name added to the metric path for this node </td>
</tr>
<tr>
<td class='confluenceTd'> mgmtUrl </td>
<td class='confluenceTd'> SEMP URL for server queries; format should be `http://&lt;server-address&gt;:8080/SEMP` for 
Solace VMR instances and `http://&lt;mgmt-address&gt;/SEMP` for Solace hardware instances.
</td>
</tr>
<tr>
<td class='confluenceTd'> adminUser </td>
<td class='confluenceTd'> admin username to use for querying; can be a readonly user </td>
</tr>
<tr>
<td class='confluenceTd'> adminPassword </td>
<td class='confluenceTd'> HTTP authentication credentials used for the admin user </td>
</tr>
</tbody>
</table>

## Example Configuration

```yaml

#This will create this metric in all the tiers, under this path
metricPrefix: "Custom Metrics|Solace|"

# number of concurrent tasks
numberOfThreads: 2

# List of Solace Servers
servers:
  - mgmtUrl: "http://192.168.56.201:8080/SEMP"
    adminUser: "admin"
    adminPassword: "admin"
    #displayName is required. Displays your server name in metric path.
    displayName: "PrimaryVMR"
  - mgmtUrl: "http://192.168.56.202:8080/SEMP"
    adminUser: "admin"
    adminPassword: "admin"
    #displayName is required. Displays your server name in metric path.
    displayName: "BackupVMR"
  - mgmtUrl: "http://192.168.56.203:8080/SEMP"
    adminUser: "admin"
    adminPassword: "admin"
    #displayName is required. Displays your server name in metric path.
    displayName: "MonitorVMR"
```

## Next Steps / TODO Items

1. Fix redundancy status for hardware queries (as of 8.2.0)
2. More backwards compatibility schemas
3. Better calculation to match parsing schema version to the version presented by the router
4. When too many schemas need to be supported, load only the required libs dynamically via child classloaders
5. Add bridge statistics / status