/**
 * <p>This java library provides an AppDynamics MachineAgent extension to query
 * Solace message brokers and update an ADController. This extension works only with the java standalone machine agent. It has been tested
 * against a Machine Agent v4.3.7.3 GA build. The main entrypoint class is {@link com.appdynamics.extensions.solace.SolaceMonitor}.
 *
 * <p>Solace is a multi-protocol Message-Oriented middleware, providing a self-contained
 * standalone message broker server in both hardware and containerized formats.
 * Statistics and metrics on Solace performance can be queried from the management
 * plane via HTTP. The following metrics are currently gathered by this monitor:
 * <ul>
 * <li> Global messaging statistics</li>
 * <li> Global HA redundancy metrics</li>
 * <li> Global service status</li>
 * <li> Global msg-spool status and statistics</li>
 * <li> Msg-VPN messaging status and statistics</li>
 * <li> Msg-VPN queue summaries</li>
 * <li> Msg-VPN durable topic endpoint summaries</li>
 * <li> Msg-VPN bridge summaries </li>
 * </ul>
 *
 */

package com.appdynamics.extensions.solace;
