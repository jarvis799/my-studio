package com.devstyle.app;

import android.content.Context;
import android.content.SharedPreferences;

/** Small settings store: mail account, auto reply and the reply template. */
public class Prefs {
    private final SharedPreferences sp;

    public Prefs(Context context) {
        sp = context.getSharedPreferences("devstyle", Context.MODE_PRIVATE);
    }

    public String email() { return sp.getString("email", ""); }
    public String password() { return sp.getString("password", ""); }
    public String displayName() { return sp.getString("displayName", "Team"); }
    public String imapHost() { return sp.getString("imapHost", "imap.gmail.com"); }
    public int imapPort() { return sp.getInt("imapPort", 993); }
    public String smtpHost() { return sp.getString("smtpHost", "smtp.gmail.com"); }
    public int smtpPort() { return sp.getInt("smtpPort", 465); }
    public boolean autoReply() { return sp.getBoolean("autoReply", true); }
    public String replyHtml() { return sp.getString("replyHtml", DEFAULT_REPLY); }

    public void save(String email, String password, String displayName, String imapHost, int imapPort,
                     String smtpHost, int smtpPort, boolean autoReply, String replyHtml) {
        sp.edit()
            .putString("email", email)
            .putString("password", password)
            .putString("displayName", displayName)
            .putString("imapHost", imapHost)
            .putInt("imapPort", imapPort)
            .putString("smtpHost", smtpHost)
            .putInt("smtpPort", smtpPort)
            .putBoolean("autoReply", autoReply)
            .putString("replyHtml", replyHtml)
            .apply();
    }

    public static final String DEFAULT_REPLY =
        "<div style=\"font-family:Arial,Helvetica,sans-serif;background:#f7f9f5;padding:24px\">" +
        "<div style=\"max-width:520px;margin:0 auto;background:#ffffff;border-radius:20px;padding:26px;" +
        "border:1px solid #e6efe7\">" +
        "<p style=\"margin:0 0 4px;letter-spacing:.18em;text-transform:uppercase;font-size:11px;" +
        "color:#4f9d1a\">Auto reply</p>" +
        "<h2 style=\"margin:0 0 12px;font-size:20px;color:#0f1b14\">Thanks for writing, {{name}}</h2>" +
        "<p style=\"margin:0 0 12px;color:#5c6b60;line-height:1.6\">I have your message about " +
        "&ldquo;{{subject}}&rdquo; and I will reply personally as soon as I can.</p>" +
        "<p style=\"margin:0;color:#5c6b60;line-height:1.6\">This reply was sent automatically from my app.</p>" +
        "</div></div>";
}
