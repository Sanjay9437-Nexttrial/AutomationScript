package Utility;

import java.io.File;
import java.util.Properties;

import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.Authenticator;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

public class MailSender {

    public static void sendEmail() {

        // Load properties (recipients, attachments, etc.)
        Properties config = ConfigReader.loadProperties();

        String toList = config.getProperty("toRecipients"); // comma-separated
        String ccList = config.getProperty("ccRecipients"); // optional
        String bccList = config.getProperty("bccRecipients"); // optional
        String attachments = config.getProperty("attachments"); // comma-separated file paths

        // Sender credentials
        final String from = config.getProperty("emailFrom"); // "sanjay.km@nexttrial.ai"
        final String password = config.getProperty("emailPassword"); // app password

        // SMTP Properties for Gmail
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        // Session
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try {
            // Create message
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from));

            // To recipients
            if (toList != null && !toList.isEmpty()) {
                msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toList));
            } else {
                System.out.println("No TO recipients specified.");
                return;
            }

            // CC recipients
            if (ccList != null && !ccList.isEmpty()) {
                msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(ccList));
            }

            // BCC recipients
            if (bccList != null && !bccList.isEmpty()) {
                msg.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(bccList));
            }

            msg.setSubject("TestNG Automation Execution Report");

            // Body + attachments
            Multipart multipart = new MimeMultipart();

            // 1️⃣ Body Text
            MimeBodyPart bodyPart = new MimeBodyPart();
            bodyPart.setText(
                "Hello Team,\n\n" +
                "Please find attached the latest TestNG Automation Execution Report.\n\n" +
                "Regards,\nAutomation Team"
            );
            multipart.addBodyPart(bodyPart);

            // 2️⃣ Attachments
            if (attachments != null && !attachments.isEmpty()) {
                for (String filePath : attachments.split(",")) {
                    File file = new File(filePath.trim());
                    if (file.exists()) {
                        MimeBodyPart attachPart = new MimeBodyPart();
                        attachPart.attachFile(file);
                        multipart.addBodyPart(attachPart);
                    } else {
                        System.out.println("Attachment not found: " + filePath);
                    }
                }
            }

            msg.setContent(multipart);

            // Send email
            Transport.send(msg);
            System.out.println("Email Sent Successfully!");

        } catch (MessagingException me) {
            me.printStackTrace();
            System.out.println("Failed to send email due to messaging error.");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to send email due to general error.");
        }
    }
}
