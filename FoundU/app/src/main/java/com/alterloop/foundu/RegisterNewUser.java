package com.alterloop.foundu;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.alterloop.util.Utility;


public class RegisterNewUser extends ActionBarActivity {

    /* Declare Buttons and EditText boxes used in Register screen */
    Button buttonActivate;
    Button buttonRegister;
    EditText editTextPhonenumber;
    EditText editTextActivationCode;
    Spinner dropDownCountryCodes;

    String enteredPhonenumber="";
    String selectedCountryCode="";
    String enteredActivationCode="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_new_user);

        // Initialize buttons and edit text boxes
        buttonActivate=(Button)findViewById(R.id.buttonActivate);
        buttonRegister=(Button)findViewById(R.id.buttonRegister);
        editTextPhonenumber=(EditText)findViewById(R.id.editTextPhonenumber);
        editTextActivationCode=(EditText)findViewById(R.id.editTextActivationCode);
        dropDownCountryCodes=(Spinner)findViewById(R.id.spinnerCountryCodes);

        // Country Item Selected Listener, set default country code
        int defaultCodePoistion=Utility.GetCountryZipCode(getApplicationContext());
        dropDownCountryCodes.setSelection(defaultCodePoistion);

        dropDownCountryCodes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapter, View v,
                                       int position, long id) {
                // On selecting a spinner item
                String item = adapter.getItemAtPosition(position).toString();
                selectedCountryCode=item.split(",")[0];
                // Showing selected spinner item
                //Toast.makeText(getApplicationContext(),"Selected Country Code : " + selectedCountryCode, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                int defaultCodePoistion= Utility.GetCountryZipCode(getApplicationContext());
                dropDownCountryCodes.setSelection(defaultCodePoistion);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register_new_user, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(getApplication(), Settings.class));
            return true;
        }
        else if (id == R.id.action_help) {
            startActivity(new Intent(getApplication(), Help.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /* Button on click methods */

    public void startRegisterProcess(View view) {
        // Do something in response to Register button
        // Step 1: check phonenumber is valid
        // Step 2: Save phonenumber in local DB
        // Step 3: Send activation code through SMS +call webservice+ save phonenum and activation code in global repo
        if(editTextPhonenumber.getText().toString().length()<8)
        {
            //invalid phonenumber
            Toast.makeText(getApplicationContext(), "Invalid Phonenumber ! " + editTextPhonenumber.getText().toString()
                    , Toast.LENGTH_SHORT).show();
        }
        else {
            try {
                //String randomActivationCode="1234";
                String randomActivationCode=Integer.toString((int)(Math.random()*11111));   // random 4 digit code

                // Set the format as '+' then 'country code' then ' number' eg. +919898898778
                String Phonenumber="+"+selectedCountryCode+editTextPhonenumber.getText().toString();
                StringBuilder Msg=new StringBuilder();
                // SMS content
                Msg.append("Hello, your Found.U one time activation code is ");
                Msg.append(randomActivationCode);
                Msg.append(". Good Day!");

                SQLiteDatabase db = openOrCreateDatabase("FoundApp", MODE_PRIVATE, null);
                Cursor c = db.rawQuery("Select * from RegisteredUserInfo", null);
                int rowcount = 0;
                while (c.moveToNext()) {
                    rowcount = rowcount + 1;
                }
                // Send SMS
                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(Phonenumber, null, Msg.toString(), null, null);
                    Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();
                    if (rowcount > 0) {
                        db.execSQL("UPDATE RegisteredUserInfo SET isActivated=0, Phonenumber=" + Phonenumber + ", ActivationCode="+randomActivationCode + " ;");
                    } else {
                        // generate random activation code here
                        // call SMS gateway api and send SMS to the entered number with activation code
                        db.execSQL("INSERT INTO RegisteredUserInfo(Phonenumber, ActivationCode, isActivated) VALUES('" + Phonenumber + "', '"+randomActivationCode+"', 0); ");
                    }
                    //RegisteredUserInfo(id INTEGER PRIMARY KEY AUTOINCREMENT, Phonenumber TEXT, " +
                    //        "RegistrationDate DATETIME DEFAULT CURRENT_TIMESTAMP, isActivated INTEGER, ActivationCode TEXT )


                    }
                catch (Exception e) {
                        Toast.makeText(getApplicationContext(),
                                "SMS failed, please try again.",
                                Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }

                db.close();
                // startActivity(new Intent(getApplication(), LocationSharePrompt.class));
                // Toast.makeText( getApplicationContext(),"Activation code sent via sms "+randomActivationCode, Toast.LENGTH_SHORT).show();

            }
            catch(Exception ex) {
                Toast.makeText( getApplicationContext(),"Oops ! Something went wrong.", Toast.LENGTH_SHORT).show();
            }
            finally {
                SQLiteDatabase.releaseMemory();
            }


        }
    }

    public void activatePhonenumber(View view) {
        // Do something in response to Activate button
        // Step 1: Once SMS is received type activation code in the text box and click Activate
        // Activation code and phonenumber will be verified. If successful user will be created in global repository
        if(editTextActivationCode.getText().toString().length()<=0) {
            Toast.makeText( getApplicationContext(),"Enter activation code received via SMS.", Toast.LENGTH_SHORT).show();
        }
        else {

            try {
                SQLiteDatabase db = openOrCreateDatabase("FoundApp", MODE_PRIVATE, null);
                Cursor c = db.rawQuery("Select * from RegisteredUserInfo", null);
                String activationCode="";
                while (c.moveToNext()) {
                    activationCode=c.getString(c.getColumnIndex("ActivationCode"));
                }
                String a=editTextActivationCode.getText().toString();
                if(a.equals(activationCode))
                {
                    db.execSQL("UPDATE RegisteredUserInfo SET isActivated=1");
                    startActivity(new Intent(getApplication(), LocationSharePrompt.class));
                    /* kill the activity off the stack, so that when after registration when LocationSharePrompt
                    activity is open and back button is pressed, flow should not go to registration screen */
                    finish();
                }
                else {
                    Toast.makeText( getApplicationContext(),"Enter activation code received via SMS.", Toast.LENGTH_SHORT).show();
                }
                db.close();
            }
            catch(Exception ex)
            {
                System.out.print("....."+ex.getMessage());
            }
            finally {
                SQLiteDatabase.releaseMemory();
            }


        }
    }
}
