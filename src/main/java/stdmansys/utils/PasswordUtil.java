package stdmansys.utils;

import org.apache.commons.io.FileUtils;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;

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
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec("zkyellow".toCharArray(), FileUtils.readFileToByteArray(new File("doc/salt.txt")), 10000, 128);
            SecretKey sk = factory.generateSecret(spec);
            return new SecretKeySpec(sk.getEncoded(), "AES");
        }catch(NoSuchAlgorithmException e){
            e.printStackTrace();
            return null;
        }catch(InvalidKeySpecException e){
            e.printStackTrace();
            return null;
        }catch(IOException e){
            e.printStackTrace();
            return null;
        }
    }

}
