package zkysms.mail;

import zkysms.constants.Constants;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

public class Mail {

    private Properties properties;

    public Mail() {}

    public boolean send(String senderEmail, String receiverEmail, String password, String subject, String text) {
        String host = "smtp.gmail.com";
        properties = System.getProperties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.auth", "true");
        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, password);
            }
        });
        session.setDebug(true);
        try {
            MimeMessage message = new MimeMessage(session);
            if(senderEmail.equals(Constants.NO_REPLY_EMAIL.getValue())){
                message.setFrom(new InternetAddress("noreply@zkysms.tk", "Students Management System"));
            }
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(receiverEmail));
            message.setSubject(subject);
            message.setText(text);
            Transport.send(message);
            if(senderEmail.equals(Constants.NO_REPLY_EMAIL.getValue())){
                if(delete(Constants.NO_REPLY_EMAIL.getValue(), Constants.NO_REPLY_PASSWORD.getValue())){
                    return true;
                }else{
                    return false;
                }
            }else{
                return true;
            }
        } catch (MessagingException | UnsupportedEncodingException mex) {
            mex.printStackTrace();
            return false;
        }
    }

    private boolean delete(String email, String password){
        String host = "imap.gmail.com";
        String port = "993";
        Properties properties = new Properties();
        properties.put("mail.imap.host", host);
        properties.put("mail.imap.port", port);
        properties.setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        properties.setProperty("mail.imap.socketFactory.fallback", "false");
        properties.setProperty("mail.imap.socketFactory.port", String.valueOf(port));
        Session session = Session.getDefaultInstance(properties);
        try {
            Store store = session.getStore("imap");
            store.connect(email, password);
            Folder folderSent = store.getFolder("[Gmail]/Sent Mail");
            Folder folderTrash = store.getFolder("[Gmail]/Trash");
            folderSent.open(Folder.READ_WRITE);
            folderTrash.open(Folder.READ_WRITE);
            Message[] arrayMessagesSent = folderSent.getMessages();
            Message[] toTrash = new Message[1];
            toTrash[0] = arrayMessagesSent[arrayMessagesSent.length - 1];
            folderSent.copyMessages(toTrash, folderTrash);
            Message[] arrayMessageTrash = folderTrash.getMessages();
            for(int i = 0; i < arrayMessageTrash.length; i++){
                arrayMessageTrash[arrayMessageTrash.length - 1].setFlag(Flags.Flag.DELETED, true);
            }
            folderTrash.close(true);
            folderSent.close(true);
            store.close();
            return true;
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
            return false;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }

}