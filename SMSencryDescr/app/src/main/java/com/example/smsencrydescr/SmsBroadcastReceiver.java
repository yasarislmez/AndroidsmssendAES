package com.example.smsencrydescr;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class SmsBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();

        Object[] object = (Object[]) bundle.get("pdus");  //sms protokolü pdus dayalı nesne almak için

        SmsMessage sms[] = new SmsMessage[object.length];

        Intent in = new Intent(context, DisplaySMSActivity.class);

        in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        in.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        String msgContent = "";

        String originNum = "";

        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < object.length; i++) {

            sms[i] = SmsMessage.createFromPdu((byte[]) object[i]);

            // alınan sms içeriği
            msgContent = sms[i].getDisplayMessageBody();

            // gönderenin telefon numarasını al

            originNum = sms[i].getDisplayOriginatingAddress();

            // uzun mesajları birleştir
            sb.append(msgContent);

            abortBroadcast();


        }
        // telefon no gir
        in.putExtra("originNum", originNum);
        // tüm mesajı intenta gönder
        in.putExtra("msgContent", new String(sb));
        // başlat DisplaySMSActivity.java

        context.startActivity(in);

    }
}


