package com.devstyle.app;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.widget.ArrayAdapter;

/** Native shell: the site on one tab, the inbox and mail settings on the others. */
public class MainActivity extends AppCompatActivity {
    private WebView web;
    private LinearLayout inboxPanel;
    private LinearLayout settingsPanel;
    private TextView inboxEmpty;
    private ListView inboxList;
    private final List<Mail> mails = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private TextView tabSite;
    private TextView tabInbox;
    private TextView tabSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        web = findViewById(R.id.web);
        inboxPanel = findViewById(R.id.inboxPanel);
        settingsPanel = findViewById(R.id.settingsPanel);
        inboxEmpty = findViewById(R.id.inboxEmpty);
        inboxList = findViewById(R.id.inboxList);
        tabSite = findViewById(R.id.tabSite);
        tabInbox = findViewById(R.id.tabInbox);
        tabSettings = findViewById(R.id.tabSettings);

        WebSettings settings = web.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        web.setWebViewClient(new android.webkit.WebViewClient());

        String site = readAsset("content.json");
        String url = "file:///android_asset/www/index.html";
        if (!assetExists("www/index.html")) {
            try {
                url = new JSONObject(site).optString("site", "https://github.com");
            } catch (Exception ignored) {
                url = "https://github.com";
            }
        }
        web.loadUrl(url);

        adapter = new ArrayAdapter<>(this, R.layout.item_mail, new ArrayList<String>());
        inboxList.setAdapter(adapter);

        tabSite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { show(0); }
        });
        tabInbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { show(1); }
        });
        tabSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { show(2); }
        });

        bindSettings();
        show(0);
    }

    private void show(int tab) {
        web.setVisibility(tab == 0 ? View.VISIBLE : View.GONE);
        inboxPanel.setVisibility(tab == 1 ? View.VISIBLE : View.GONE);
        settingsPanel.setVisibility(tab == 2 ? View.VISIBLE : View.GONE);
        tabSite.setAlpha(tab == 0 ? 1f : 0.45f);
        tabInbox.setAlpha(tab == 1 ? 1f : 0.45f);
        tabSettings.setAlpha(tab == 2 ? 1f : 0.45f);
        if (tab == 1) loadInbox();
    }

    private void loadInbox() {
        mails.clear();
        mails.addAll(MailStore.load(this));
        adapter.clear();
        for (Mail mail : mails) adapter.add(mail.toString());
        adapter.notifyDataSetChanged();
        inboxEmpty.setVisibility(mails.isEmpty() ? View.VISIBLE : View.GONE);
        inboxList.setVisibility(mails.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void bindSettings() {
        final Prefs prefs = new Prefs(this);
        final EditText email = findViewById(R.id.fieldEmail);
        final EditText password = findViewById(R.id.fieldPassword);
        final EditText displayName = findViewById(R.id.fieldDisplayName);
        final EditText imapHost = findViewById(R.id.fieldImapHost);
        final EditText imapPort = findViewById(R.id.fieldImapPort);
        final EditText smtpHost = findViewById(R.id.fieldSmtpHost);
        final EditText smtpPort = findViewById(R.id.fieldSmtpPort);
        final EditText reply = findViewById(R.id.fieldReply);
        final android.widget.Switch auto = findViewById(R.id.switchAuto);

        email.setText(prefs.email());
        password.setText(prefs.password());
        displayName.setText(prefs.displayName());
        imapHost.setText(prefs.imapHost());
        imapPort.setText(String.valueOf(prefs.imapPort()));
        smtpHost.setText(prefs.smtpHost());
        smtpPort.setText(String.valueOf(prefs.smtpPort()));
        reply.setText(prefs.replyHtml());
        auto.setChecked(prefs.autoReply());

        Button save = findViewById(R.id.buttonSave);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prefs.save(
                    email.getText().toString().trim(),
                    password.getText().toString(),
                    displayName.getText().toString().trim(),
                    imapHost.getText().toString().trim(),
                    number(imapPort.getText().toString(), 993),
                    smtpHost.getText().toString().trim(),
                    number(smtpPort.getText().toString(), 465),
                    auto.isChecked(),
                    reply.getText().toString()
                );
                MailService.start(MainActivity.this);
                Toast.makeText(MainActivity.this, "Saved — checking mail now", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int number(String value, int fallback) {
        try {
            return Integer.parseInt(value.trim());
        } catch (Exception error) {
            return fallback;
        }
    }

    private boolean assetExists(String path) {
        try (InputStream in = getAssets().open(path)) {
            return in != null;
        } catch (Exception error) {
            return false;
        }
    }

    private String readAsset(String path) {
        try (InputStream in = getAssets().open(path)) {
            java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int read;
            while ((read = in.read(buffer)) != -1) out.write(buffer, 0, read);
            return out.toString("UTF-8");
        } catch (Exception error) {
            return "{}";
        }
    }

    @Override
    public void onBackPressed() {
        if (web != null && web.canGoBack()) {
            web.goBack();
            return;
        }
        super.onBackPressed();
    }
}
