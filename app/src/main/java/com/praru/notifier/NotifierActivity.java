package com.praru.notifier;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class NotifierActivity extends ActionBarActivity {
    public static String number;
    public static boolean ring = false;
    public static boolean callReceived = false;

    private static final String username = "miscallnotifier@gmail.com";
    private static final String password = "ctl1CRNotifier";
    private static final String recipient = "neog.pradyumna@gmail.com";

    private static String subject;
    private static String body;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifier);

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        PhoneStateListener phoneStateListener = new PhoneStateListener(){
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {

                if(state==TelephonyManager.CALL_STATE_RINGING){
                    ring = true;
                    number = incomingNumber;
                    String contactName = getContactName(getApplicationContext(), number);
                    if(contactName != null) {
                        Toast.makeText(getApplicationContext(), "Incoming Call from " + contactName + "<" + number + ">", Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(getApplicationContext(), "Incoming Call from " + number, Toast.LENGTH_LONG).show();
                    }
                }

                if (state == TelephonyManager.CALL_STATE_IDLE){
                    if (ring == true && callReceived == false){
                        String contactName = getContactName(getApplicationContext(), number);
                        subject = "Miscall Alert";
                        if(contactName != null) {
                            Toast.makeText(getApplicationContext(), "Miscall from " + contactName + "<" + number + ">", Toast.LENGTH_LONG).show();
                            body = "You have a new missed call from " + contactName + "<" + number + ">";
                            sendMail(recipient, subject, body);
                        }else{
                            Toast.makeText(getApplicationContext(), "Miscall from Unknown Number <"  + number + ">", Toast.LENGTH_LONG).show();
                            body = "You have a new missed call from an unknown number " + number;
                            sendMail(recipient, subject, body);
                        }
                        ring = false;
                    }else {
                        Toast.makeText(getApplicationContext(), "Phone is idle", Toast.LENGTH_LONG).show();
                    }
                }

                if (state == TelephonyManager.CALL_STATE_OFFHOOK){
                    callReceived = true;
                    Toast.makeText(getApplicationContext(), "Phone is currently in a call",Toast.LENGTH_LONG).show();
                }


            }
        };

        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notifier, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
/*
    //Creating Session
    private Session createSessionObject(){
        Properties props = new Properties();
        props.put("mail.smtp.auth","true");
        props.put("mail.smtp.starttls.enable","true");
        props.put("mail.smtp.host","smtp.gmail.com");
        props.put("mail.smtp.port","587");

        return Session.getInstance(props,new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    //Creating the Message
    private Message createMessage(String toList, String subject, String body, Session session) throws MessagingException, UnsupportedEncodingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("notifier@prady.com", "Notifier"));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(toList,toList));
        message.setSubject(subject);
        message.setText(body);
        return message;
    }

    //Sending the Email
    private void sendEmail(String email, String subject, String body){
        *//*Session session = createSessionObject();
        try{
            Message message = createMessage(email, subject, body, session);
            new SendMailTask().execute(message);
        }catch (AddressException e){
            e.printStackTrace();
        }catch(MessagingException e){
            e.printStackTrace();
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }*//*
        try {
            new SendMailTask().execute();
//            GMailSender sender = new GMailSender(username, password);
//            sender.sendMail(subject,body,username,email);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    //Creating Async Class to send email
    private class SendMailTask extends AsyncTask<Message, Void, Void> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(WelcomeActivity.this, "Please wait", "Sending mail", true, false);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
        }

        @Override
        protected Void doInBackground(Message... messages) {
            try {
//                Transport.send(messages[0]);
                Toast.makeText(getApplicationContext(), "Sending Email Notification", Toast.LENGTH_LONG).show();
                GMailSender sender = new GMailSender(username, password);
                sender.sendMail(subject,body,username,recipient);
            } catch (MessagingException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }*/

    private void sendMail(String email, String subject, String messageBody) {
        Session session = createSessionObject();

        try {
            Message message = createMessage(email, subject, messageBody, session);
            new SendMailTask().execute(message);
        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
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

    private class SendMailTask extends AsyncTask<Message, Void, Void> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(NotifierActivity.this, "Please wait", "Sending mail", true, false);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
        }

        @Override
        protected Void doInBackground(Message... messages) {
            try {
                Transport.send(messages[0]);
                System.out.println("Voila");
            } catch (MessagingException e) {
                System.out.println("Tai Tai Fish");
                e.printStackTrace();
            }
            return null;
        }
    }

    //Retrieve Contact Name
    private String getContactName(Context context, String phoneNumber){
        String contactName = null;
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME},null,null,null);
        if(cursor == null){
            return null;
        }
        if(cursor.moveToFirst()){
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }
        if(cursor != null && !cursor.isClosed()){
            cursor.close();
        }
        return contactName;
    }
    /*private static boolean ring = false;
    private static boolean callReceived = false;
    private static String number;

    private static final String username = "miscallnotifier@gmail.com";
    private static final String password = "ctliCR@notifier";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifier);

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        PhoneStateListener phoneStateListener = new PhoneStateListener(){
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {

                if(state==TelephonyManager.CALL_STATE_RINGING){
                    ring = true;
                    number = incomingNumber;
                    Toast.makeText(getApplicationContext(), "Incoming Call from " + number, Toast.LENGTH_LONG).show();
                }

                if (state == TelephonyManager.CALL_STATE_IDLE){
                    if (ring == true && callReceived == false){
                        Toast.makeText(getApplicationContext(),"Miscall from " + number, Toast.LENGTH_LONG).show();
                        sendEmail("neog.pradyumna@gmail.com", "Miscall Alert", "You have a new missed call from " + number);
                    }else {
                        Toast.makeText(getApplicationContext(), "Phone is idle", Toast.LENGTH_LONG).show();
                    }
                }

                if (state == TelephonyManager.CALL_STATE_OFFHOOK){
                    callReceived = true;
                    Toast.makeText(getApplicationContext(), "Phone is currently in a call",Toast.LENGTH_LONG).show();
                }
            }
        };
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notifier, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Creating Session
    private Session createSessionObject(){
        Properties props = new Properties();
        props.put("mail.smtp.auth","true");
        props.put("mail.smtp.starttls.enable","true");
        props.put("mail.smtp.host","smtp.gmail.com");
        props.put("mail.smtp.port","587");

        return Session.getInstance(props,new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    //Creating the Message
    private Message createMessage(String toList, String subject, String body, Session session) throws MessagingException, UnsupportedEncodingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("neog.pradyumna@gmail.com", "Notifier"));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(toList,toList));
        message.setSubject(subject);
        message.setText(body);
        return message;
    }

    //Sending the Email
    private void sendEmail(String email, String subject, String body){
        Session session = createSessionObject();
        try{
            Message message = createMessage(email, subject, body, session);
            new SendMailTask().execute(message);
        }catch (AddressException e){
            e.printStackTrace();
        }catch(MessagingException e){
            e.printStackTrace();
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
    }

    //Creating Async Class to send email
    private class SendMailTask extends AsyncTask<Message, Void, Void> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(NotifierActivity.this, "Please wait", "Sending mail", true, false);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressDialog.dismiss();
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
    }*/
}
