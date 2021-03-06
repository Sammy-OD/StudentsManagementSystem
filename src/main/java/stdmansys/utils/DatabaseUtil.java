package stdmansys.utils;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {

    public static void createNewDatabase(String dbName) {
        Connection connection = null;
        String url = "jdbc:sqlite:database/" + dbName + ".db";
        try{
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(url);
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            try{
                if(connection != null){
                    connection.close();
                    encryptDB(dbName);
                }
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    public static Connection getDBConnection(String dbName) {
        Connection connection = null;
        String url = "jdbc:sqlite:database/" + dbName + ".db";
        try{
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(url);
        }catch (ClassNotFoundException e){
            e.printStackTrace();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return connection;
    }

    public static void encryptDB(String dbName) {
        try {
            byte[] salt = new byte[8];
            byte[] iv = new byte[128/8];
            SecureRandom secureRandom = new SecureRandom();
            secureRandom.nextBytes(salt);
            secureRandom.nextBytes(iv);
            FileInputStream in = new FileInputStream("database/" + dbName + ".db");
            FileOutputStream out = new FileOutputStream("database/" + dbName + ".enc");
            out.write(salt);
            out.write(iv);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, generateSecretKey(salt), new IvParameterSpec(iv));
            processFile(cipher, in, out);
            in.close();
            out.close();
            File file = new File("database/" + dbName + ".db");
            file.delete();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
    }

    public static void decryptDB(String dbName) {
        try {
            byte[] salt = new byte[8];
            byte[] iv = new byte[128/8];
            FileInputStream in = new FileInputStream("database/" + dbName + ".enc");
            FileOutputStream out = new FileOutputStream("database/" + dbName + ".db");
            in.read(salt);
            in.read(iv);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, generateSecretKey(salt), new IvParameterSpec(iv));
            processFile(cipher, in, out);
            in.close();
            out.close();
            File file = new File("database/" + dbName + ".enc");
            file.delete();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        }
    }

    private static SecretKeySpec generateSecretKey(byte[] salt) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            KeySpec spec = new PBEKeySpec("zkyellow".toCharArray(), salt, 10000, 128);
            SecretKey sk = factory.generateSecret(spec);
            return new SecretKeySpec(sk.getEncoded(), "AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void processFile(Cipher cipher, InputStream inputStream, OutputStream outputStream) {
        byte[] ibuf = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(ibuf)) != -1) {
                byte[] obuf = cipher.update(ibuf, 0, len);
                if ( obuf != null ) outputStream.write(obuf);
            }
            byte[] obuf = cipher.doFinal();
            if ( obuf != null ) outputStream.write(obuf);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
    }

}