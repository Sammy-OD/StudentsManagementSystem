package stdmansys.utils;

import org.apache.commons.io.FileUtils;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.nio.charset.StandardCharsets;

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
            return new SecretKeySpec(FileUtils.readFileToByteArray(new File("doc/sk.txt")), "AES");
        }catch (Exception e){
          return null;
        }
    }

}
