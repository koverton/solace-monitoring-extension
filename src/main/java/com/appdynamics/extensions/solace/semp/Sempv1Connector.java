package com.appdynamics.extensions.solace.semp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.*;

public class Sempv1Connector {
    private static final Logger logger = LoggerFactory.getLogger(Sempv1Connector.class);

    public Sempv1Connector(final String url, final String username, final String password, String displayName) throws MalformedURLException {
        this.url = new URL(url);
        this.displayName = displayName;
        Authenticator.setDefault(new MyAuthenticator(username, password));
    }

    public String getDisplayName() {
        return displayName;
    }

    public String doPost(final String request) {
        try {
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);

            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            logger.info("SEMP POST URL: {}", url);
            logger.debug("SEMP POST DATA: {}", request);
            out.write(request);
            out.close();

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(
                                connection.getInputStream()));
                logger.info("Successful SEMP Response");
                String total = "";
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    logger.debug(inputLine);
                    total += inputLine;
                }
                in.close();
                return total;
            } else {
                logger.error("Error: [{}] {}", responseCode , connection.getResponseMessage());
            }
        } catch (Exception e) {
            logger.error("Exception thrown posting request", e);
            e.printStackTrace();
        }
        return "";
    }

    public SempVersion checkBrokerVersion() {
        // Doesn't really matter what version we're asking, we're going to pull the very first semp-version tag
        String result = doPost("<rpc semp-version=\"soltr/8_2_0\"><show><version/></show></rpc>");
        return getSempVersion(result);
    }

    public SempVersion getSempVersion(String result) throws IllegalArgumentException {
        int start = result.indexOf("semp-version");
        if (-1 != start) {
            // <rpc-reply semp-version="soltr/8_6VMR">
            start += 14;
            int end = result.indexOf('"', start);
            if (end > start) {
                return new SempVersion(result.substring(start, end));
            }
        }
        throw new IllegalArgumentException("Unrecognized semp schema version");
    }

    private class MyAuthenticator extends Authenticator {
        String user;
        String pwd;
        public MyAuthenticator(String username, String password) {
            user = username;
            pwd = password;
        }
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(user, pwd.toCharArray());
        }
    }

    final private URL url;
    final private String displayName;
}
