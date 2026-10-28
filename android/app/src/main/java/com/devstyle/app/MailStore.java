package com.devstyle.app;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/** Inbox on disk, so the app shows mail offline without any server. */
public class MailStore {
    private static File file(Context context) {
        return new File(context.getFilesDir(), "inbox.json");
    }

    public static List<Mail> load(Context context) {
        List<Mail> list = new ArrayList<>();
        File target = file(context);
        if (!target.exists()) return list;
        try (FileInputStream in = new FileInputStream(target)) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            StringBuilder text = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) text.append(line);
            JSONArray array = new JSONArray(text.toString());
            for (int i = 0; i < array.length(); i++) list.add(Mail.from(array.getJSONObject(i)));
        } catch (Exception ignored) {
        }
        return list;
    }

    public static void save(Context context, List<Mail> mails) {
        JSONArray array = new JSONArray();
        for (Mail mail : mails) array.put(mail.toJson());
        try (FileOutputStream out = new FileOutputStream(file(context))) {
            out.write(array.toString().getBytes(StandardCharsets.UTF_8));
        } catch (Exception ignored) {
        }
    }

    public static boolean has(List<Mail> mails, String id) {
        for (Mail mail : mails) {
            if (mail.id.equals(id)) return true;
        }
        return false;
    }
}
