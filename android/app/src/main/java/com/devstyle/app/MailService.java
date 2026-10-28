package com.devstyle.app;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Foreground service: checks the inbox over IMAP every few minutes and sends an
 * HTML auto reply over SMTP. No AI service, no server — just the mail account.
 */
public class MailService extends Service {
    private static final long INTERVAL = 5 * 60 * 1000L;
    private volatile boolean running = true;
    private Thread worker;

    public static void start(Context context) {
        Intent intent = new Intent(context, MailService.class);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Notify.channel(this);
        startForeground(Notify.ID, Notify.build(this, "Inbox is on", "Waiting for mail"));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (worker == null) {
            worker = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (running) {
                        try {
                            poll();
                        } catch (Exception ignored) {
                        }
                        try {
                            Thread.sleep(INTERVAL);
                        } catch (InterruptedException stop) {
                            break;
                        }
                    }
                }
            });
            worker.start();
        }
        return START_STICKY;
    }

    private void poll() throws Exception {
        Prefs prefs = new Prefs(this);
        String user = prefs.email();
        String pass = prefs.password();
        if (user.isEmpty() || pass.isEmpty()) return;

        Properties props = new Properties();
        props.put("mail.store.protocol", "imaps");
        props.put("mail.imaps.host", prefs.imapHost());
        props.put("mail.imaps.port", String.valueOf(prefs.imapPort()));
        props.put("mail.imaps.ssl.enable", "true");
        props.put("mail.imaps.timeout", "15000");
        props.put("mail.imaps.connectiontimeout", "15000");

        Session session = Session.getInstance(props);
        Store store = session.getStore("imaps");
        store.connect(prefs.imapHost(), prefs.imapPort(), user, pass);

        Folder inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_ONLY);
        Message[] messages = inbox.getMessages();
        List<Mail> found = MailStore.load(this);
        int added = 0;
        int start = Math.max(0, messages.length - 20);

        for (int i = messages.length - 1; i >= start; i--) {
            Message message = messages[i];
            Mail mail = new Mail();
            Address[] addresses = message.getFrom();
            if (addresses != null && addresses.length > 0) {
                mail.from = addresses[0].toString();
                int at = mail.from.indexOf('<');
                mail.name = at > 0 ? mail.from.substring(0, at).replace("\"", "").trim() : mail.from;
                mail.from = at >= 0 ? addresses[0].toString().replaceAll(".*<", "").replace(">", "") : mail.from;
            }
            mail.subject = message.getSubject() == null ? "(no subject)" : message.getSubject();
            mail.preview = readText(message).trim();
            if (mail.preview.length() > 220) mail.preview = mail.preview.substring(0, 220);
            mail.date = message.getReceivedDate() == null ? "" : message.getReceivedDate().toString();
            mail.id = (mail.from + mail.subject + mail.date).hashCode() + "";

            if (MailStore.has(found, mail.id)) continue;
            found.add(0, mail);
            added++;

            if (prefs.autoReply() && !mail.from.equalsIgnoreCase(user)) {
                try {
                    reply(prefs, mail);
                    mail.replied = true;
                } catch (Exception ignored) {
                }
            }
        }

        MailStore.save(this, found);
        inbox.close(false);
        store.close();

        if (added > 0) {
            startForeground(Notify.ID, Notify.build(this, added + " new message" + (added > 1 ? "s" : ""),
                found.isEmpty() ? "Inbox" : found.get(0).subject));
        }
    }

    private void reply(Prefs prefs, Mail mail) throws Exception {
        String user = prefs.email();
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", prefs.smtpHost());
        props.put("mail.smtp.port", String.valueOf(prefs.smtpPort()));
        props.put("mail.smtp.ssl.enable", String.valueOf(prefs.smtpPort() == 465));
        props.put("mail.smtp.starttls.enable", String.valueOf(prefs.smtpPort() != 465));
        props.put("mail.smtp.timeout", "20000");

        Session session = Session.getInstance(props);
        MimeMessage reply = new MimeMessage(session);
        reply.setFrom(new InternetAddress(user, prefs.displayName()));
        reply.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mail.from));
        reply.setSubject("Re: " + mail.subject);
        reply.setContent(
            prefs.replyHtml().replace("{{name}}", mail.name).replace("{{subject}}", mail.subject),
            "text/html; charset=utf-8"
        );
        Transport.send(reply, user, prefs.password());
    }

    private String readText(Message message) {
        try {
            Object content = message.getContent();
            if (content instanceof String) return (String) content;
            if (content instanceof Multipart) {
                Multipart multipart = (Multipart) content;
                for (int i = 0; i < multipart.getCount(); i++) {
                    BodyPart part = multipart.getBodyPart(i);
                    if (part.getContent() instanceof String) return (String) part.getContent();
                }
            }
        } catch (Exception ignored) {
        }
        return "";
    }

    @Override
    public void onDestroy() {
        running = false;
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
