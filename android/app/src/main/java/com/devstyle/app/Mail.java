package com.devstyle.app;

import org.json.JSONException;
import org.json.JSONObject;

/** One message in the in-app inbox. */
public class Mail {
    public String id = "";
    public String from = "";
    public String name = "";
    public String subject = "";
    public String preview = "";
    public String date = "";
    public boolean replied = false;

    public static Mail from(JSONObject json) {
        Mail mail = new Mail();
        mail.id = json.optString("id");
        mail.from = json.optString("from");
        mail.name = json.optString("name");
        mail.subject = json.optString("subject");
        mail.preview = json.optString("preview");
        mail.date = json.optString("date");
        mail.replied = json.optBoolean("replied");
        return mail;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("id", id);
            json.put("from", from);
            json.put("name", name);
            json.put("subject", subject);
            json.put("preview", preview);
            json.put("date", date);
            json.put("replied", replied);
        } catch (JSONException ignored) {
        }
        return json;
    }

    @Override
    public String toString() {
        return (name.isEmpty() ? from : name) + " — " + subject;
    }
}
