
This monitoring plugin is designed to run under the **AppDynamics Standalone MachineAgent**.
For more information about the MachineAgent, see the [AppDynamics MachineAgent Website](https://docs.appdynamics.com/display/PRO45/Standalone+Machine+Agents).

## Assumptions

AppDynamics best-practice is to create an **Application** in your controller,
and create one or more **Tiers** under that application representing groups
of related components.


## AppDynamics MachineAgent

To get started quickly, and assuming you already have a controller you can use,
here are quick steps to install the MachineAgent, Solace plugin, and configure it
for Solace monitoring. The MachineAgent can be installed on any server that
has connectivity to the management ports on Solace PubSub+ brokers you want
to monitor. It will iterate through a list of Solace brokers


## Installing the MachineAgent

1. Download the MachineAgent appropriate to your system; the system bundle
comes with bundled Java 1.8 JRE, or you can just download the
non-bundled MachineAgent and point it at your own Java installation.
[Download Link](https://download.appdynamics.com/download/#version=&apm=machine&os=&platform_admin_os=&events=&eum=&page=1
)

    NOTE1: The user running the machine-agent must have read/write privileges to the MachineAgent installation directory.

    NOTE2: The machine-agent version should not be higher than the controller version; higher-version MachineAgents often have issues connecting.

2. Create a `MachineAgent` directory and unzip the bundle into that directory.

   https://docs.appdynamics.com/display/PRO43/Linux+Install+Using+ZIP+with+Bundled+JRE

3. Configure `MachineAgent/conf/controller-info.xml` to point to your Controller, enabling SSL.
For example:
    ```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <controller-info>
         <controller-host>solace.saas.appdynamics.com</controller-host>
         <controller-port>443</controller-port>
         <controller-ssl-enabled>true</controller-ssl-enabled>
         <account-access-key>bigfoot</account-access-key>
         <account-name>solace</account-name>
         <sim-enabled>false</sim-enabled>
    </controller-info>
    ```

4. Configure your Solace plugin see [top-level README.md](../README.md).

5. Start/Stop the MachineAgent via convenience scripts in `bin/machineagent` or create
your own if preferred. The MachineAgent requires that you set the Application, Tier and Node
per MachineAgent. These can be set with flags on the Java commandline, e.g.:

    ```bash
    nohup $MACHINE_AGENT_DIR/jre/bin/java \
        -Dappdynamics.agent.uniqueHostId=emeaperf1 \
        -Dappdynamics.agent.applicationName=solace-test \
        -Dappdynamics.agent.tierName=london \
        -Dappdynamics.agent.nodeName=ha_pair \
            -jar $MACHINE_AGENT_DIR/machineagent.jar &
    ```

## Starting the MachineAgent as a Service

The MachineAgent bundle comes with an `etc/` directory that contains service-scripts
for various well-known Unix/Linux service management tools like systemd,
init.d, sysconfig, etc. Use these to automatically start the MachineAgent
at system boot time. For example:

    ```bash
    koverton% cd MachineAgent/etc init.d
    koverton% ln -s `pwd`/appdynamics-machine-agent /etc/init.id/
    ```

Any additional arguments you'd like to customize your comandline with
can be added to the `JAVA_OPTS` environment variable, e.g.:

    ```bash
    JAVA_OPTS="-Dappdynamics.agent.applicationName=<appname>"
    JAVA_OPTS="$JAVA_OPTS -Dappdynamics.agent.tierName=<app-teir>"
    JAVA_OPTS="$JAVA_OPTS -Dappdynamics.agent.nodeName=<node>"
    JAVA_OPTS="$JAVA_OPTS -Dappdynamics.extensions.key=**your-hash-key**"
    ```

## Links

https://docs.appdynamics.com/display/PRO43/Install+the+Standalone+Machine+Agent

https://download.appdynamics.com/download/#version=&apm=machine&os=&platform_admin_os=&events=&eum=&page=1

https://docs.appdynamics.com/display/PRO43/Linux+Install+Using+ZIP+with+Bundled+JRE

https://docs.appdynamics.com/display/4.5.0/Server+Monitoring


