package zkysms.utils;

import org.slf4j.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.Random;

public class CryptoUtil {

    private static Logger LOGGER = LogUtil.getLOGGER(CryptoUtil.class.getName());

    public static byte[] encrypt(String toEncrypt, Cipher cipher, String keyStorePath, String keyStorePassword, String entryPassword, String alias){
        try{
            byte[] toEncryptBytes = toEncrypt.getBytes(StandardCharsets.UTF_8);
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(keyStorePath, keyStorePassword, entryPassword, alias));
            byte[] encrypted = cipher.doFinal(toEncryptBytes);
            return encrypted;
        }catch(BadPaddingException | IllegalBlockSizeException | InvalidKeyException e){
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    public static String decrypt(byte[] toDecrypt, Cipher cipher, String keyStorePath, String keyStorePassword, String entryPassword, String alias){
        try{
            cipher.init(Cipher.DECRYPT_MODE, getSecretKey(keyStorePath, keyStorePassword, entryPassword, alias));
            byte[] decrypted = cipher.doFinal(toDecrypt);
            return new String(decrypted);
        }catch(BadPaddingException | IllegalBlockSizeException | InvalidKeyException e){
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    public static SecretKey getSecretKey(String keyStorePath, String keyStorePassword, String entryPassword, String alias){
        try{
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(new FileInputStream(keyStorePath), keyStorePassword.toCharArray());
            KeyStore.ProtectionParameter ePassword = new KeyStore.PasswordProtection(entryPassword.toCharArray());
            KeyStore.SecretKeyEntry keyEntry = (KeyStore.SecretKeyEntry) keyStore.getEntry(alias, ePassword);
            return keyEntry.getSecretKey();
        }catch(IOException | CertificateException | NoSuchAlgorithmException | KeyStoreException | UnrecoverableEntryException e){
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    public static void createKeyStore(String path, String password) {
        try{
            KeyStore ks = KeyStore.getInstance("PKCS12");
            char[] keyStorePassword = password.toCharArray();
            ks.load(null, keyStorePassword);
            FileOutputStream fileOutputStream = new FileOutputStream(path);
            ks.store(fileOutputStream, keyStorePassword);
        }catch(KeyStoreException | IOException | CertificateException | NoSuchAlgorithmException e){
            LOGGER.error(e.getMessage(), e);
        }
    }

    public static void storeSecretKey(String keyStorePath, String keyStorePassword, String alias, String password, SecretKey sKey) {
        try{
            KeyStore ks = KeyStore.getInstance("PKCS12");
            ks.load(new FileInputStream(keyStorePath), keyStorePassword.toCharArray());
            char[] passwordArray = password.toCharArray();
            KeyStore.SecretKeyEntry secretKey = new KeyStore.SecretKeyEntry(sKey);
            KeyStore.ProtectionParameter entryPassword = new KeyStore.PasswordProtection(passwordArray);
            ks.setEntry(alias, secretKey, entryPassword);
            FileOutputStream fileOutputStream = new FileOutputStream(keyStorePath);
            ks.store(fileOutputStream, keyStorePassword.toCharArray());
        }catch(CertificateException | NoSuchAlgorithmException | IOException | KeyStoreException e){
            LOGGER.error(e.getMessage(), e);
        }
    }

    public static boolean validateKeyStore(String keyStorePath, String keyStorePassword){
        try{
            KeyStore ks = KeyStore.getInstance("PKCS12");
            ks.load(new FileInputStream(keyStorePath), keyStorePassword.toCharArray());
            return true;
        }catch(CertificateException | NoSuchAlgorithmException | IOException | KeyStoreException e){
            LOGGER.error(e.getMessage(), e);
            return false;
        }
    }

    public static String generatePassword(int length) {
        String s = "ABCDEFGHIJKLMNOPQRSTUVWSYZ" + "abcdefghijklmnopqrstuvwsyz" + "0123456789" + "#@&.";
        Random random = new Random();
        char[] chars = new char[length];
        for(int i = 0; i < chars.length; i++){
            chars[i] = s.charAt(random.nextInt(s.length()));
        }
        return new String(chars);
    }

}