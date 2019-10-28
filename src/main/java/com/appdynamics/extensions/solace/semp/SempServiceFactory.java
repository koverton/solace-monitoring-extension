package com.appdynamics.extensions.solace.semp;

import com.appdynamics.extensions.solace.ServerConfigs;
import com.appdynamics.extensions.solace.semp.r7_2_2.*;
import com.appdynamics.extensions.solace.semp.r8_2_0.*;
import com.appdynamics.extensions.solace.semp.r8_13_0.*;
import com.appdynamics.extensions.solace.semp.r9_2_0.*;
import com.appdynamics.extensions.solace.semp.r8_6VMR.*;
import com.appdynamics.extensions.solace.semp.r9_2_0VMR.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;

/**
 * Constructs a SempService for a given Solace broker that uses the appropriate SEMP library for version supported by that broker.
 *
 */
public class SempServiceFactory {
    private static final Logger logger = LoggerFactory.getLogger(SempServiceFactory.class);

    /**
     * Constructs a SempService for a given Solace broker that uses the appropriate SEMP library for version supported by that broker.
     *
     * @param connector transport connector to the service we want to query.
     * @param serverConfigs encapsulates all configs and exclusion policies used by the SempService.
     * @return SempService object defining the platform and version number of the service we are connected to.
     */
    static public SempService createSempService(SempConnector connector, ServerConfigs serverConfigs) {
        logger.trace("<SempServiceFactory.createSempService>");
        SempVersion sempVersion = connector.checkBrokerVersion();

        if (!sempVersion.isValid()) {
            logger.error("Not creating SempService because valid SEMP-version could not be detected");
        }
        else if (sempVersion.getPlatform().equals(SempVersion.Platform.VMR)) {
            logger.info("SempServiceFactory instantiating VMR SEMP-service");

            // Prefer newest version we can
            if (sempVersion.getVersionNumber() >= SempVersion.v9_2_0VMR.getVersionNumber()) {
                try {
                    return new GenericSempService<>(
                            new SempConnectionContext<>(connector,
                                    new SempRequestFactory_r9_2_0VMR(serverConfigs),
                                    new SempReplyFactory_r9_2_0VMR(serverConfigs),
                                    new SempMarshaller_r9_2_0VMR(),
                                    sempVersion.getVersionString()));
                } catch (JAXBException ex) {
                    logger.error("Exception thrown attempting to create SempService version: "
                            + SempVersion.v8_6VMR.getVersionString(), ex);
                    ex.printStackTrace();
                }
            }
            else {
                // 8_6VMR is lowest supported
                try {
                    return new GenericSempService<>(
                            new SempConnectionContext<>(connector,
                                    new SempRequestFactory_r8_6VMR(serverConfigs),
                                    new SempReplyFactory_r8_6VMR(serverConfigs),
                                    new SempMarshaller_r8_6VMR(),
                                    sempVersion.getVersionString()));
                } catch (JAXBException ex) {
                    logger.error("Exception thrown attempting to create SempService version: "
                            + SempVersion.v8_6VMR.getVersionString(), ex);
                    ex.printStackTrace();
                }
            }
            // TBD: Support more here
        }
        else {
            logger.info("SempServiceFactory instantiating Hardware SEMP-service");
            // Prefer newest version we can
            if (sempVersion.getVersionNumber() >= SempVersion.v9_2_0.getVersionNumber()) {
                try {
                    return new GenericSempService<>(
                            new SempConnectionContext<>(connector,
                                    new SempRequestFactory_r9_2_0(serverConfigs),
                                    new SempReplyFactory_r9_2_0(serverConfigs),
                                    new SempMarshaller_r9_2_0(),
                                    sempVersion.getVersionString()));
                }
                catch(JAXBException ex) {
                    logger.error("Exception thrown attempting to create SempService version: "
                            + SempVersion.v9_2_0.getVersionString(), ex);
                    ex.printStackTrace();
                }
            }
            else if (sempVersion.getVersionNumber() >= SempVersion.v8_13_0.getVersionNumber()) {
                try {
                    return new GenericSempService<>(
                            new SempConnectionContext<>(connector,
                                    new SempRequestFactory_r8_13_0(serverConfigs),
                                    new SempReplyFactory_r8_13_0(serverConfigs),
                                    new SempMarshaller_r8_13_0(),
                                    sempVersion.getVersionString()));
                }
                catch(JAXBException ex) {
                    logger.error("Exception thrown attempting to create SempService version: "
                            + SempVersion.v8_13_0.getVersionString(), ex);
                    ex.printStackTrace();
                }
            }
            else if (sempVersion.getVersionNumber() >= SempVersion.v8_2_0.getVersionNumber()) {
                try {
                    return new GenericSempService<>(
                            new SempConnectionContext<>(connector,
                                    new SempRequestFactory_r8_2_0(serverConfigs),
                                    new SempReplyFactory_r8_2_0(serverConfigs),
                                    new SempMarshaller_r8_2_0(),
                                    sempVersion.getVersionString()));
                }
                catch(JAXBException ex) {
                    logger.error("Exception thrown attempting to create SempService version: "
                            + SempVersion.v8_2_0.getVersionString(), ex);
                    ex.printStackTrace();
                }
            }
            else {
                try {
                    return new GenericSempService<>(
                            new SempConnectionContext<>(connector,
                                    new SempRequestFactory_r7_2_2(serverConfigs),
                                    new SempReplyFactory_r7_2_2(serverConfigs),
                                    new SempMarshaller_r7_2_2(),
                                    sempVersion.getVersionString()));
                }
                catch(JAXBException ex) {
                    logger.error("Exception thrown attempting to create SempService version: "
                            + SempVersion.v7_2_2.getVersionString(), ex);
                    ex.printStackTrace();
                }
            }
            // TBD: Support more here
        }
        logger.trace("</SempServiceFactory.createSempService>");

        return null;
    }
}
