package com.praru.notifier;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Created by aa49436 on 4/21/2015
 * under package com.praru.notifier
 */
public class MailService extends Service {

    public static final String userPreferences = "UserPrefs";

    private static final String username = "miscallnotifier@gmail.com";
    private static final String password = "ctl1CRNotifier";
//    private final String TAG = "MailService";

    public static String number;
    public static boolean ring = false;
    public static boolean callReceived = false;

    private static String recipient;
    private static String subject;
    private static String body;

    SharedPreferences sharedPreferences;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(getApplicationContext(), "Notifier Service Started", Toast.LENGTH_SHORT).show();
        startMonitoring();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(getApplicationContext(), "Notifier Service Stopped", Toast.LENGTH_SHORT).show();
    }

    private void sendMail(String email, String subject, String messageBody) {
        Session session = createSessionObject();

        try {
            Message message = createMessage(email, subject, messageBody, session);
            new SendMailTask().execute(message);
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    //Method to start monitoring the phone status to detect any missed call
    private void startMonitoring() {
        String emailIDKey = NotifierActivity.emailIDKey;
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        sharedPreferences = getSharedPreferences(userPreferences, Context.MODE_PRIVATE);

        if (sharedPreferences.contains(emailIDKey)) {
            recipient = sharedPreferences.getString(emailIDKey, "");
        } else {
            Toast.makeText(getApplicationContext(), "Please update the recipient email ID", Toast.LENGTH_LONG).show();
        }
        PhoneStateListener phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {

                if (state == TelephonyManager.CALL_STATE_RINGING) {
                    ring = true;
                    number = incomingNumber;
                    String contactName = getContactName(getApplicationContext(), number);
                    if (contactName != null) {
                        Toast.makeText(getApplicationContext(), "Incoming Call from " + contactName + "<" + number + ">", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Incoming Call from Unknown Number <" + number + ">", Toast.LENGTH_LONG).show();
                    }
                }

                if (state == TelephonyManager.CALL_STATE_IDLE) {
                    if (ring && !callReceived) {
                        String contactName = getContactName(getApplicationContext(), number);
                        subject = "Miscall Alert";

                        if (contactName != null) {
                            Toast.makeText(getApplicationContext(), "Miscall from " + contactName + "<" + number + ">", Toast.LENGTH_LONG).show();
                            body = "You have a new missed call from " + contactName + "<" + number + ">";
                        } else {
                            Toast.makeText(getApplicationContext(), "Miscall from Unknown Number <" + number + ">", Toast.LENGTH_LONG).show();
                            body = "You have a new missed call from an unknown number " + number;
                        }

                        if (recipient != null && !recipient.equalsIgnoreCase(""))
                            sendMail(recipient, subject, body);
                        else
                            Toast.makeText(getApplicationContext(), "ERROR SENDING NOTIFICATION : Please update the recipient email ID", Toast.LENGTH_LONG).show();

                        ring = false;
                    }
                }

                if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
                    callReceived = true;
                }
            }
        };

        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    private Message createMessage(String email, String subject, String messageBody, Session session) throws MessagingException, UnsupportedEncodingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("miscallnotifier@gmail.com", "Miscall/SMS Alert"));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(email, email));
        message.setSubject(subject);
        message.setText(messageBody);
        return message;
    }

    private Session createSessionObject() {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        return Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                PasswordAuthentication passwordAuthentication = new PasswordAuthentication(username, password);
                System.out.println("Password Authentication " + passwordAuthentication.getPassword() + " username " + passwordAuthentication.getUserName());
                return passwordAuthentication;
            }
        });
    }

    //Retrieve Contact Name
    private String getContactName(Context context, String phoneNumber) {
        String contactName = null;
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }
        if (!cursor.isClosed()) {
            cursor.close();
        }
        return contactName;
    }


    //Async Send Email Task
    private class SendMailTask extends AsyncTask<Message, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Message... messages) {
            try {
                Transport.send(messages[0]);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}