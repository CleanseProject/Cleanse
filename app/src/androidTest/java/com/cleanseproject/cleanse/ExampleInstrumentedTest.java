package com.cleanseproject.cleanse;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.widget.Button;
import android.widget.EditText;

import com.cleanseproject.cleanse.activities.LoginActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */

public class ExampleInstrumentedTest extends ActivityInstrumentationTestCase2<LoginActivity> {
    EditText txtemail;
    EditText txtpass;
    Button btnlogin;
    Button btnemail;

    public ExampleInstrumentedTest() {
        super(LoginActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        LoginActivity actividad = getActivity();


        try {
            runTestOnUiThread(actividad::initializeEmailUI);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        txtemail = actividad.findViewById(R.id.txt_email);
        txtpass = actividad.findViewById(R.id.txt_pswd);
        btnlogin = actividad.findViewById(R.id.btn_login);
        btnemail = actividad.findViewById(R.id.btn_email);

    }



    public void testLoginSignup(){
        String username = "hernanagf97@gmail.com";
        String pass = "123456";

        TouchUtils.tapView(this, txtemail);
        getInstrumentation().sendStringSync(username);

        TouchUtils.tapView(this, txtpass);
        getInstrumentation().sendStringSync(pass);

        TouchUtils.tapView(this, btnlogin);

    }
}
