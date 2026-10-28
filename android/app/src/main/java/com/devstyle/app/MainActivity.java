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
        Button save = findViewById(R.id.btnSave);
        email.setText(prefs.getEmail());
        password.setText(prefs.getPassword());
        save.setOnClickListener(view -> {
            prefs.save(email.getText().toString(), password.getText().toString());
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
        });
    }

    private String readAsset(String name) {
        try (InputStream is = getAssets().open(name)) {
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            return new String(buffer);
        } catch (Exception e) {
            return "{}";
        }
    }

    private boolean assetExists(String name) {
        try {
            getAssets().open(name).close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}