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

    // Supported hardware versions
    final public static SempVersion vmrMin = SempVersion.v8_6VMR;
    // Supported VMR versions
    final public static SempVersion applMin = SempVersion.v8_2_0;

    /**
     * Constructs a SempService for a given Solace broker that uses the appropriate SEMP library for version supported by that broker.
     *
     * @param connector transport connector to the service we want to query.
     * @return SempService object defining the platform and version number of the service we are connected to.
     */
    static public SempService createSempService(Sempv1Connector connector) {
        SempVersion sempVersion = connector.checkBrokerVersion();

        if (sempVersion.getPlatform().equals(SempVersion.Platform.VMR)) {
            // Prefer newest version we can
            if (sempVersion.getVersionNumber() >= SempVersion.v8_6VMR.getVersionNumber()) {
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
            }
            // TBD: Support more here
        }
        else {
            // Prefer newest version we can
            if (sempVersion.getVersionNumber() >= SempVersion.v8_2_0.getVersionNumber()) {
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
            }
            // TBD: Support more here
        }

        return null;
    }

    private static Object newInstance(String sempVersion, String localJarName, String fqClassName) {
        try {
            URLClassLoader child = getClassLoader(sempVersion, localJarName);
            Class clazz = Class.forName(fqClassName, true, child);
            return clazz.newInstance ();
        }
        catch(MalformedURLException urlex) {
            logger.error("Could not load semp version:{} Jar:{}", sempVersion, localJarName);
            urlex.printStackTrace();
        }
        catch(ClassNotFoundException noclex) {
            logger.error("Could not find class:{} in Jar:{}", fqClassName, localJarName);
            noclex.printStackTrace();
        }
        catch(InstantiationException instex) {
            logger.error("Could not instantiate class:{} in Jar:{}", fqClassName, localJarName);
            instex.printStackTrace();
        }
        catch(IllegalAccessException illex) {
            logger.error("Could not access class:{} in Jar:{}", fqClassName, localJarName);
            illex.printStackTrace();
        }
        return null;
    }

    private static URLClassLoader getClassLoader(String sempVersion, String localJarName) throws MalformedURLException {
        if (clmap.containsKey(sempVersion)) {
            return clmap.get(sempVersion);
        }
        URLClassLoader child = new URLClassLoader(
                new URL[]{new URL("file://./" + localJarName)},
                SempServiceFactory.class.getClassLoader());
        clmap.put(sempVersion, child);
        return child;
    }

    private static Map<String,URLClassLoader> clmap = new HashMap<>();
}
