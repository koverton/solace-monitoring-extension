package com.appdynamics.extensions.solace.semp;

import com.appdynamics.extensions.solace.ServerExclusionPolicies;
import com.appdynamics.extensions.solace.semp.r7_2_2.*;
import com.appdynamics.extensions.solace.semp.r8_2_0.*;
import com.appdynamics.extensions.solace.semp.r8_6VMR.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;

public class SempServiceFactory {
    private static final Logger logger = LoggerFactory.getLogger(SempServiceFactory.class);

    /**
     * Constructs a SempService for a given Solace broker that uses the appropriate SEMP library for version supported by that broker.
     *
     * @param connector transport connector to the service we want to query.
     * @return SempService object defining the platform and version number of the service we are connected to.
     */
    static public SempService createSempService(Sempv1Connector connector, ServerExclusionPolicies exclusionPolicies) {
        logger.debug("<SempServiceFactory.createSempService>");
        SempVersion sempVersion = connector.checkBrokerVersion();

        if (!sempVersion.isValid()) {
            logger.error("Not creating SempService because valid SEMP-version could not be detected");
        }
        else if (sempVersion.getPlatform().equals(SempVersion.Platform.VMR)) {
            logger.info("SempServiceFactory instantiating VMR SEMP-service");

            // Prefer newest version we can
            //if (sempVersion.getVersionNumber() >= SempVersion.v8_6VMR.getVersionNumber()) {
                try {
                    return new GenericSempService<>(
                            new SempConnectionContext<>(connector,
                                    new SempRequestFactory_r8_6VMR(exclusionPolicies),
                                    new SempReplyFactory_r8_6VMR(exclusionPolicies),
                                    new SempMarshaller_r8_6VMR(),
                                    sempVersion.getVersionString()));
                } catch (JAXBException ex) {
                    logger.error("Exception thrown attempting to create SempService version: "
                            + SempVersion.v8_6VMR.getVersionString(), ex);
                    ex.printStackTrace();
                }
            //}
            // TBD: Support more here
        }
        else {
            logger.info("SempServiceFactory instantiating Hardware SEMP-service");
            // Prefer newest version we can
            if (sempVersion.getVersionNumber() >= SempVersion.v8_2_0.getVersionNumber()) {
                try {
                    return new GenericSempService<>(
                            new SempConnectionContext<>(connector,
                                    new SempRequestFactory_r8_2_0(exclusionPolicies),
                                    new SempReplyFactory_r8_2_0(exclusionPolicies),
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
                                    new SempRequestFactory_r7_2_2(exclusionPolicies),
                                    new SempReplyFactory_r7_2_2(exclusionPolicies),
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
        logger.debug("</SempServiceFactory.createSempService>");

        return null;
    }
}
