package com.praru.notifier;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;


public class NotifierActivity extends ActionBarActivity {
    public static final String userPreferences = "UserPrefs";
    public static final String emailIDKey = "emailIDKey";
    public static final String monitoringKey = "monitoringSwitchKey";
    public static boolean isMonitoring = false;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String recipient;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifier);

        EditText emailInput = (EditText) findViewById(R.id.emailID);
        Switch monitoringSwitch = (Switch) findViewById(R.id.onOffSwitch);

        sharedPreferences = getSharedPreferences(userPreferences, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        if (sharedPreferences.contains(emailIDKey)) {
            recipient = sharedPreferences.getString(emailIDKey, "");
            emailInput.setText(recipient);
        } else {
            Toast.makeText(getApplicationContext(), "Please update the recipient email ID", Toast.LENGTH_LONG).show();
        }

        if (sharedPreferences.contains(monitoringKey)) {
            isMonitoring = sharedPreferences.getBoolean(monitoringKey, true);
        } else {

            editor.putBoolean(monitoringKey, isMonitoring);
            editor.apply();
        }

        monitoringSwitch.setChecked(isMonitoring);

        monitoringSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    startService();
                } else {
                    stopService();
                }
                editor.remove(monitoringKey);
                editor.putBoolean(monitoringKey, isChecked);
                editor.apply();
            }
        });
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

    //Method to start the service
    public void startService() {
        startService(new Intent(getBaseContext(), MailService.class));
    }

    //Method to stop the service
    public void stopService() {
        stopService(new Intent(getBaseContext(), MailService.class));
    }

    //Method to save and update user data
    public void saveData(View view) {
        EditText editEmailID = (EditText) findViewById(R.id.emailID);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(emailIDKey);
        editor.putString(emailIDKey, editEmailID.getText().toString());
        editor.apply();
        Toast.makeText(getApplicationContext(), "Data updated", Toast.LENGTH_SHORT).show();
    }



    /*public void onMonitoringSwitchedClick(View view){
        boolean on = ((Switch)view).isChecked();
        Switch monitoringSwitch = (Switch) findViewById(R.id.onOffSwitch);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (on) {
            startService();
            isMonitoring = true;
        }
        else {
            stopService();
            isMonitoring = false;
        }
        editor.remove(monitoringKey);
        editor.putBoolean(monitoringKey,true);
        editor.apply();
        monitoringSwitch.setChecked(isMonitoring);
    }*/

    /*private BroadcastReceiver smsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
                for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                    String messageBody = smsMessage.getMessageBody();
                }
            }
        }
    };*/
    /*public class SMSBroadcastReceiver extends BroadcastReceiver {

        private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
        private static final String TAG = "SMSBroadcastReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
                for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                    String messageBody = smsMessage.getMessageBody();
                }
            }
        *//*
            Log.i(TAG, "Intent Received " + intent.getAction());
            if (intent.getAction().equals(SMS_RECEIVED)){
                Bundle bundle = intent.getExtras();
                if(bundle != null){
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    final SmsMessage[] messages = new SmsMessage[pdus.length];
                    for (int i = 0; i < pdus.length; i++){
                        messages[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                    }
                    if (messages.length > -1) {
                        subject = "SMS Alert";
                        body = messages[0].getMessageBody();
                        sendMail(emailID, subject, body);
                    }
                }
            }*//*
        }
    }*/

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