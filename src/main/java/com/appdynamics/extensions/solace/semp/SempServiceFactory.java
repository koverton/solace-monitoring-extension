package com.appdynamics.extensions.solace.semp;

import com.appdynamics.extensions.solace.semp.r8_2_0.*;
import com.appdynamics.extensions.solace.semp.r8_6VMR.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

public class SempServiceFactory {
    private static final Logger logger = LoggerFactory.getLogger(SempServiceFactory.class);

    /**
     * Constructs a SempService for a given Solace broker that uses the appropriate SEMP library for version supported by that broker.
     *
     * @param connector transport connector to the service we want to query.
     * @return SempService object defining the platform and version number of the service we are connected to.
     */
    static public SempService createSempService(Sempv1Connector connector) {
        logger.debug("<SempServiceFactory.createSempService>");
        SempVersion sempVersion = connector.checkBrokerVersion();

        if (sempVersion.getPlatform().equals(SempVersion.Platform.VMR)) {
            logger.info("SempServiceFactory instantiating VMR SEMP-service");

            // Prefer newest version we can
            //if (sempVersion.getVersionNumber() >= SempVersion.v8_6VMR.getVersionNumber()) {
                try {
                    return new GenericSempService<>(
                            new SempConnectionContext<>(connector,
                                    new SempRequestFactory_r8_6VMR(),
                                    new SempReplyFactory_r8_6VMR(),
                                    new SempMarshaller_r8_6VMR(),
                                    SempVersion.v8_6VMR.getVersionString()));
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
            //if (sempVersion.getVersionNumber() >= SempVersion.v8_2_0.getVersionNumber()) {
                try {
                    return new GenericSempService<>(
                            new SempConnectionContext<>(connector,
                                    new SempRequestFactory_r8_2_0(),
                                    new SempReplyFactory_r8_2_0(),
                                    new SempMarshaller_r8_2_0(),
                                    SempVersion.v8_2_0.getVersionString()));
                }
                catch(JAXBException ex) {
                    logger.error("Exception thrown attempting to create SempService version: "
                            + SempVersion.v8_2_0.getVersionString(), ex);
                    ex.printStackTrace();
                }
            //}
            // TBD: Support more here
        }
        logger.debug("</SempServiceFactory.createSempService>");

        return null;
    }
}
