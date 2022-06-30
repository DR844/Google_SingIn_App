package com.executor.googlesinginapp;

import android.content.Context;
import android.os.StrictMode;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;

import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendMessage {
    public static Message sendEmail(Context foContext, String fromEmailAddress,
                                    String toEmailAddress, String token)
            throws MessagingException, IOException {
        /* Load pre-authorized user credentials from the environment.
           TODO(developer) - See https://developers.google.com/identity for
            guides on implementing OAuth2 for your application.*/
    /*    GoogleCredentials credentials = GoogleCredentials.getApplicationDefault()
                .createScoped(GmailScopes.GMAIL_SEND);*/
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here

            GoogleCredential credential = new GoogleCredential().setAccessToken(token);
            JsonFactory jsonFactory = GsonFactory.getDefaultInstance();
            HttpTransport httpTransport = new NetHttpTransport();

            // Create the gmail API client
   /*     Gmail service = new Gmail.Builder(new NetHttpTransport(),
                GsonFactory.getDefaultInstance(),
                requestInitializer)
                .setApplicationName(foContext.getString(R.string.app_name))
                .build();*/

            Gmail service = new Gmail.Builder(httpTransport, jsonFactory, credential)
                    .setApplicationName("Easy SMS Forwarder").build();

            // Create the email content
            String messageSubject = "Test message";
            String bodyText = "lorem ipsum.";

            // Encode as MIME message
            Properties props = new Properties();
            Session session = Session.getDefaultInstance(props, null);
            MimeMessage email = new MimeMessage(session);
            email.setFrom(new InternetAddress(fromEmailAddress));
            email.addRecipient(javax.mail.Message.RecipientType.TO,
                    new InternetAddress(toEmailAddress));
            email.setSubject(messageSubject);
            email.setText(bodyText);

            // Encode and wrap the MIME message into a gmail message
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            email.writeTo(buffer);
            byte[] rawMessageBytes = buffer.toByteArray();
            String encodedEmail = Base64.encodeBase64URLSafeString(rawMessageBytes);
            Message message = new Message();
            message.setRaw(encodedEmail);

            try {
                // Create send message
                message = service.users().messages().send("me", message).execute();
                System.out.println("Message id: " + message.getId());
                System.out.println(message.toPrettyString());
                return message;
            } catch (GoogleJsonResponseException e) {
                // TODO(developer) - handle error appropriately
                GoogleJsonError error = e.getDetails();
                if (error.getCode() == 403) {
                    System.err.println("Unable to send message: " + e.getDetails());
                } else {
                    throw e;
                }
            }
        }
        return null;
    }
}
