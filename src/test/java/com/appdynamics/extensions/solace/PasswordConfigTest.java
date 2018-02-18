package com.appdynamics.extensions.solace;

import com.appdynamics.extensions.crypto.Encryptor;
import org.junit.Test;

import javax.crypto.KeyGenerator;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import static com.appdynamics.extensions.TaskInputArgs.PASSWORD;
import static com.appdynamics.extensions.solace.MonitorConfigs.ADMIN_USER;
import static com.appdynamics.extensions.solace.MonitorConfigs.ENCRYPTED_PASSWORD;
import static com.appdynamics.extensions.solace.MonitorConfigs.ENCRYPTION_KEY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class PasswordConfigTest {
    private static final String user = "admin";
    private static final String pass = "wunderbar";

    @Test
    public void testBasicPasswordPresent() {
        Map<String,String> server = new HashMap<>();
        server.put(ADMIN_USER, user);
        server.put(PASSWORD, pass);

        String result = Helper.getPassword(server);

        assertEquals("Basic password config not found when present", pass, result);
    }

    @Test
    public void testBasicPasswordNotPresent() {
        Map<String,String> server = new HashMap<>();
        server.put(ADMIN_USER, user);

        String result = Helper.getPassword(server);

        assertNotEquals("Basic password config was found when not present", pass, result);
    }

    @Test
    public void testEncryptedPasswordPresent() throws NoSuchAlgorithmException {
        Map<String,String> server = new HashMap<>();
        server.put(ADMIN_USER, user);

        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        String enckey = keyGen.generateKey().toString();

        Encryptor encryptor = new Encryptor(enckey);
        String encryptedPassword = encryptor.encrypt(pass);
        server.put(ENCRYPTION_KEY, enckey);
        server.put(ENCRYPTED_PASSWORD, encryptedPassword);

        String result = Helper.getPassword(server);

        assertEquals("Encrypt/Decrypt roundtrip of password failed", pass, result);
    }

    @Test
    public void testIgnorePlainPasswordWhenEncryptedPasswordPresent() throws NoSuchAlgorithmException {
        // Put plain password in un-encrypted ...
        Map<String,String> server = new HashMap<>();
        server.put(ADMIN_USER, user);
        server.put(PASSWORD, pass);

        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        keyGen.init(128);
        String enckey = keyGen.generateKey().toString();

        // ... But put a DIFFERENT password in, encrypted
        String otherPass = "chrysanthamum";
        Encryptor encryptor = new Encryptor(enckey);
        String encryptedPassword = encryptor.encrypt(otherPass);
        server.put(ENCRYPTION_KEY, enckey);
        server.put(ENCRYPTED_PASSWORD, encryptedPassword);

        String result = Helper.getPassword(server);

        assertEquals("Encrypt/Decrypt roundtrip of encryptedPassword failed", otherPass, result);
        assertNotEquals("encryptedPassword was not preferred when plain password present", pass, result);
    }
}
