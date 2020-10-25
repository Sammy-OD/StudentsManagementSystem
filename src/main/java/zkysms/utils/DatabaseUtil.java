package zkysms.utils;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import zkysms.constants.Constants;
import zkysms.constants.Path;
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
import java.sql.Statement;

public class DatabaseUtil {

    private static Logger LOGGER = LogUtil.getLOGGER(DatabaseUtil.class.getName());

    public static void createNewDatabase(String dbName, String keyStorePassword, String entryPassword) {
        Connection connection = null;
        String url = "jdbc:sqlite:database/" + dbName + ".db";
        try{
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(url);
        }catch(ClassNotFoundException | SQLException e){
            LOGGER.error(e.getMessage(), e);
        }finally{
            try{
                if(connection != null){
                    connection.close();
                    encryptDB(dbName, keyStorePassword, entryPassword);
                }
            }catch(SQLException e){
                e.printStackTrace();
            }
        }
    }

    public static void createTable(String dbName, String query, String keyStorePassword, String entryPassword) {
        decryptDB(dbName, keyStorePassword, entryPassword);
        Connection connection = null;
        Statement statement = null;
        try{
            connection = getDBConnection(dbName);
            statement = connection.createStatement();
            statement.execute(query);
        }catch(SQLException e) {
            LOGGER.error(e.getMessage(), e);
        }finally {
            try{
                statement.close();
                connection.close();
            }catch(SQLException e) {
                e.printStackTrace();
            }
            encryptDB(dbName, keyStorePassword, entryPassword);
        }
    }

    public static Connection getDBConnection(String dbName) {
        Connection connection = null;
        String url = "jdbc:sqlite:database/" + dbName + ".db";
        try{
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection(url);
        }catch(ClassNotFoundException | SQLException e){
            LOGGER.error(e.getMessage(), e);
        }
        return connection;
    }

    public static void encryptDB(String dbName, String keyStorePassword, String entryPassword) {
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
            cipher.init(Cipher.ENCRYPT_MODE, generateSecretKey(salt, keyStorePassword, entryPassword), new IvParameterSpec(iv));
            processFile(cipher, in, out);
            in.close();
            out.close();
            File file = new File("database/" + dbName + ".db");
            file.delete();
        }catch(IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public static void decryptDB(String dbName, String keyStorePassword, String entryPassword) {
        try {
            byte[] salt = new byte[8];
            byte[] iv = new byte[128/8];
            FileInputStream in = new FileInputStream("database/" + dbName + ".enc");
            FileOutputStream out = new FileOutputStream("database/" + dbName + ".db");
            in.read(salt);
            in.read(iv);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, generateSecretKey(salt, keyStorePassword, entryPassword), new IvParameterSpec(iv));
            processFile(cipher, in, out);
            in.close();
            out.close();
            File file = new File("database/" + dbName + ".enc");
            file.delete();
        }catch(IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private static SecretKeySpec generateSecretKey(byte[] salt, String keyStorePassword, String entryPassword) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            Cipher cipher = Cipher.getInstance("AES");
            KeySpec spec = new PBEKeySpec(CryptoUtil.decrypt(FileUtils.readFileToByteArray(new File(Path.ADMIN.getPath())), cipher, Path.KEYSTORE.getPath(), keyStorePassword, entryPassword, Constants.SECRET_KEY_ALIAS.getValue()).toCharArray(), salt, 10000, 128);
            SecretKey sk = factory.generateSecret(spec);
            return new SecretKeySpec(sk.getEncoded(), "AES");
        }catch(NoSuchAlgorithmException | InvalidKeySpecException | IOException | NoSuchPaddingException e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    private static void processFile(Cipher cipher, InputStream inputStream, OutputStream outputStream) {
        byte[] ibuf = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(ibuf)) != -1) {
                byte[] obuf = cipher.update(ibuf, 0, len);
                if ( obuf != null ){
                    outputStream.write(obuf);
                }
            }
            byte[] obuf = cipher.doFinal();
            if ( obuf != null ){
                outputStream.write(obuf);
            }
        }catch(IOException | IllegalBlockSizeException | BadPaddingException e){
            LOGGER.error(e.getMessage(), e);
        }
    }

}