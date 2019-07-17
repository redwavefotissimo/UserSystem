package com.common.EmailAPI;

import java.io.File;
import java.util.List;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class EmailAPI {

    private Properties props;
    private Authenticator auth;

    public static enum ContentType{
        Plain,
        HTML
    }

    public void setSSLProps(String userID, String password, String port, String smtpHostServer) throws Exception{

        setProps(userID, password, port, smtpHostServer);

        props.put("mail.smtp.socketFactory.port", port);
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
    }

    public void setTLSProps(String userID, String password, String port, String smtpHostServer) throws Exception{

        setProps(userID, password, port, smtpHostServer);

        props.put("mail.smtp.starttls.enable", "true");
    }

    public void setProps(String smtpHostServer)  throws Exception{

        auth = null;

        this.setBaseProps();
        props.put("mail.smtp.host", smtpHostServer);
    }

    public void sendEmail(String fromRecipient, String toRecipient, String ccRecipient, String subject,
                          String content, List<String> attachments, ContentType contentType) throws Exception {
        Session session = Session.getInstance(props, auth);

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(fromRecipient));

        if(toRecipient.contains(",")){
            message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(toRecipient) );
        }else{
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(toRecipient));
        }

        if(!ccRecipient.isEmpty()) {
            if (ccRecipient.contains(",")) {
                message.addRecipients(Message.RecipientType.CC, InternetAddress.parse(ccRecipient));
            } else {
                message.addRecipient(Message.RecipientType.CC, new InternetAddress(ccRecipient));
            }
        }

        if(contentType == ContentType.Plain){
            message.addHeader("Content-type","text/plain; charset=UTF-8");
        }else if (contentType == ContentType.HTML){
            message.addHeader("Content-type", "text/HTML; charset=UTF-8");
        }

        message.setSubject(subject);

        if(attachments != null && attachments.size() > 0){
            MimeBodyPart messageBodyPart = new MimeBodyPart();

            messageBodyPart.setText(content);

            MimeMultipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            for(String attachment : attachments){
                messageBodyPart = new MimeBodyPart();
                messageBodyPart.attachFile(new File(attachment));
                multipart.addBodyPart(messageBodyPart);
            }

            message.setContent(multipart);
        }else{
            message.setText(content);
        }

        Transport.send(message);
    }

    private void setBaseProps()  throws Exception{
        props = new Properties();
    }

    private void setProps(final String userID, String password, final String port, final String smtpHostServer) throws Exception{

        final String finalPassword = password;

        auth = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userID, finalPassword);
            }
        };

        this.setBaseProps();

        props.put("mail.smtp.host", smtpHostServer);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.user", userID);
        props.put("mail.smtp.password", password);
    }

}
