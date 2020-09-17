package stdmansys.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

public class PasswordUtil {

    public static byte[] encryptPassword(String unencryptedPassword, SecretKey secretKey, Cipher cipher){
        try{
            byte[] unencryptedPasswordBytes = unencryptedPassword.getBytes(StandardCharsets.UTF_8);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedPassword = cipher.doFinal(unencryptedPasswordBytes);
            return encryptedPassword;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static String decryptPassword(byte[] encryptedPassword, SecretKey secretKey, Cipher cipher){
        try{
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedPassword = cipher.doFinal(encryptedPassword);
            return new String(decryptedPassword);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static SecretKey getSecretKey(){
        try{
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(new FileInputStream("doc/sk.pfx"), "me.zky".toCharArray());
            KeyStore.ProtectionParameter entryPassword = new KeyStore.PasswordProtection("me.zky".toCharArray());
            KeyStore.SecretKeyEntry keyEntry = (KeyStore.SecretKeyEntry) keyStore.getEntry("admin-secret-key", entryPassword);
            return keyEntry.getSecretKey();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (CertificateException e) {
            e.printStackTrace();
            return null;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (KeyStoreException e) {
            e.printStackTrace();
            return null;
        } catch (UnrecoverableEntryException e) {
            e.printStackTrace();
            return null;
        }
    }

}