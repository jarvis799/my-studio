package com.devstyle.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/** Brings the inbox service back after a reboot. */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Prefs prefs = new Prefs(context);
        if (!prefs.email().isEmpty() && !prefs.password().isEmpty()) {
            MailService.start(context);
        }
    }
}
