package com.praru.notifier;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ServiceRestarter extends BroadcastReceiver {
    public ServiceRestarter() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent(context, MailService.class);
        context.startService(service);
    }
}
