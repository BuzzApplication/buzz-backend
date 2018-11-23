package com.buzz.api.email;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

import static javax.mail.Transport.send;

/**
 * Created by toshikijahja on 10/31/18.
 */
public class EmailClient {

    private static final String FROM = "buzz.developers@gmail.com";
    private static final String HOST = "localhost";
    private static final String PROPERTY = "mail.smtp.host";
    private static final String HTML = "text/html";

    private final Session session;

    public EmailClient() {
        final Properties properties = System.getProperties();
        properties.setProperty(PROPERTY, HOST);
        this.session = Session.getDefaultInstance(properties);
    }

    public void sendEmail(final String to, final String code) {
        try {
            final MimeMessage message = new MimeMessage(this.session);
            message.setFrom(new InternetAddress(FROM));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject("Buzz Verification Code");
            message.setContent("<h1>Verification code: " + code + "</h1>", HTML);

            send(message);
        } catch (final MessagingException ex) {
            System.out.println("Error: " + ex);
            ex.printStackTrace();
//            throw new BuzzException(INTERNAL_ERROR);
        }
    }
}
