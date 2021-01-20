package com.example.smsencrydescr;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class DisplaySMSActivity extends Activity {
    EditText secretKey;

    TextView senderNum;

    TextView encryptedMsg;

    TextView decryptedMsg;

    Button submit;

    Button cancel;

    String originNum = "";

    String msgContent = "";

    public static byte[] hex2byte(byte[] b) {

        if ((b.length % 2) != 0)

            throw new IllegalArgumentException("merhaba");

        byte[] b2 = new byte[b.length / 2];

        for (int n = 0; n < b.length; n += 2) {

            String item = new String(b, n, 2);

            b2[n / 2] = (byte) Integer.parseInt(item, 16);

        }

        return b2;

    }

    // hex dizisini bayt dizisine dönüştürür

    public static byte[] decryptSMS(String secretKeyString, byte[] encryptedMsg)

            throws Exception {

        // kullanıcının girdisinden gizli anahtar oluştur

        Key key = generateKey(secretKeyString);

        Cipher c = Cipher.getInstance("AES");

        c.init(Cipher.DECRYPT_MODE, key);


        byte[] decValue = c.doFinal(encryptedMsg);

        return decValue;

    }

    // şifre çözme fonksiyonu

    private static Key generateKey(String secretKeyString) throws Exception {

        Key key = new SecretKeySpec(secretKeyString.getBytes(), "AES");

        return key;

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.onreceiver);

        senderNum = (TextView) findViewById(R.id.senderNum);

        encryptedMsg = (TextView) findViewById(R.id.encryptedMsg);

        decryptedMsg = (TextView) findViewById(R.id.decryptedMsg);

        secretKey = (EditText) findViewById(R.id.secretKey);

        submit = (Button) findViewById(R.id.submit);

        cancel = (Button) findViewById(R.id.cancel);


        Bundle extras = getIntent().getExtras();

        if (extras != null) {

            // gönderen telefon numarasın "extras" dan alma

            originNum = extras.getString("originNum");

            // "extras" dan şifreli iletiyi alma

            msgContent = extras.getString("msgContent");

            // metin alanlarını belirleme

            senderNum.setText(originNum);

            encryptedMsg.setText(msgContent);

        } else {

            // intent null ise yanlış

            Toast.makeText(getBaseContext(), "hata oluştu!",

                    Toast.LENGTH_SHORT).show();

            finish();

        }


        // iptal butonuna bastığında

        cancel.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                finish();

            }

        });

        // mesajı çöz butonuna bastığında şifreyi çöz

        submit.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                // kullanıcı girişi AES gizli anahtarı

                String secretKeyString = secretKey.getText().toString();

                // uzunluk 16 karakterli olma AES 128 bit

                if (secretKeyString.length() > 0

                        && secretKeyString.length() == 16) {

                    try {

                        // şifrelenmiş ileti dizisini bytr dönüştür


                        byte[] msg = hex2byte(msgContent.getBytes());

                        // byte dizisinin şifresini çöz

                        byte[] result = decryptSMS(secretKey.getText().toString(), msg);

                        decryptedMsg.setText(new String(result));

                    } catch (Exception e) {

                        // ileti bozuk veya anahtar yanlış ise ileti çözülmez


                        decryptedMsg.setText("iletinin şifresi çözülmedi!");

                    }

                } else

                    Toast.makeText(getBaseContext(),

                            "16 karakterli gizli bir anahtar sağlanmalı!",

                            Toast.LENGTH_SHORT).show();

            }

        });

    }
}
