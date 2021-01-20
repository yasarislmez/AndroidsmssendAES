package com.example.smsencrydescr;

import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.security.Key;
import java.util.ArrayList;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity {

    /**
     * Etkinlik ilk oluşturulduğunda çağrılır.
     */


    EditText recNum;
    EditText secretKey;
    EditText msgContent;
    Button send;
    Button cancel;

    public static void sendSMS(String recNumString, String encryptedMsg) {

        try {


            SmsManager smsManager = SmsManager.getDefault();

            // ileti 160 karakteri aşabilir

            // iletiyi alt katlara bölmemiz lazım

            ArrayList<String> parts = smsManager.divideMessage(encryptedMsg);

            smsManager.sendMultipartTextMessage(recNumString, null, parts,

                    null, null);
        } catch (Exception e) {

            e.printStackTrace();

        }

    }

    public static String byte2hex(byte[] b) {

        String hs = "";

        String stmp = "";

        for (int n = 0; n < b.length; n++) {

            stmp = Integer.toHexString(b[n] & 0xFF);

            if (stmp.length() == 1)

                hs += ("0" + stmp);

            else

                hs += stmp;

        }

        return hs.toUpperCase();

    }

    // utility function

    public static byte[] encryptSMS(String secretKeyString,

                                    String msgContentString) {

        try {

            byte[] returnArray;

            // kullanıcıdan güvenlik anahtarını al

            Key key = generateKey(secretKeyString);

            // AES şifreleme algoritması kullanımı

            Cipher c = Cipher.getInstance("AES");

            // şifreleme modülünün belirtilmesi

            c.init(Cipher.ENCRYPT_MODE, key);

            // şifrele

            returnArray = c.doFinal(msgContentString.getBytes());

            return returnArray;

        } catch (Exception e) {

            e.printStackTrace();

            byte[] returnArray = null;

            return returnArray;

        }

    }

    // şifreleme fonksiyonu (encryption)

    private static Key generateKey(String secretKeyString) throws Exception {

        // gizli anahtarı oluştur string olarak

        Key key = new SecretKeySpec(secretKeyString.getBytes(), "AES");

        return key;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        recNum = (EditText) findViewById(R.id.recNum);
        secretKey = (EditText) findViewById(R.id.secretKey);
        msgContent = (EditText) findViewById(R.id.msgContent);
        send = (Button) findViewById(R.id.Send);
        cancel = (Button) findViewById(R.id.cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                finish();
            }
        });
        // iletiyi şifrele ve gönder

        send.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                String recNumString = recNum.getText().toString();

                String secretKeyString = secretKey.getText().toString();

                String msgContentString = msgContent.getText().toString();

                // kullanıcının girdilerini kontrol et

                // gizli anahtar uzunluğu  AES-128-bit ten dolayı 16 bit olmalı

                if (recNumString.length() > 0 && secretKeyString.length() > 0

                        && msgContentString.length() > 0

                        && secretKeyString.length() == 16) {

                    // iletiyi şifrele

                    byte[] encryptedMsg = encryptSMS(secretKeyString, msgContentString);
                    // byte dizisini hex e dönüştürme

                    String msgString = byte2hex(encryptedMsg);
                    // sms ile mesaj gönder
                    sendSMS(recNumString, msgString);
                    // bitir
                    finish();
                } else
                    Toast.makeText(
                            getBaseContext(),
                            "Lütfen tel no , güvenlik anahtarını ve mesajı kontrol ediniz. güvenlik anahtarı 16 karakterli olmalı",
                            Toast.LENGTH_SHORT).show();
            }
        });
    }

}