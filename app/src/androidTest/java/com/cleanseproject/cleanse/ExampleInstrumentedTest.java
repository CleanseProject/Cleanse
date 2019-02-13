package com.cleanseproject.cleanse;

import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.widget.Button;
import android.widget.EditText;

import com.cleanseproject.cleanse.activities.LoginActivity;



/**
 * @author ernesto
 */

public class ExampleInstrumentedTest extends ActivityInstrumentationTestCase2<LoginActivity> {

    private Button mloginbutton;
    private EditText etext1;
    private EditText etext2;

    public ExampleInstrumentedTest() {
        super(LoginActivity.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        LoginActivity actividad = getActivity();
        etext1 = actividad.findViewById(R.id.txt_email);
        etext2 = actividad.findViewById(R.id.txt_pswd);
        mloginbutton = actividad.findViewById(R.id.btn_login);

    }
    private static final String USERNAME = "ernesto";
    private static final String PASSWORD = "123456";

    public void testLoginSignup() {
        TouchUtils.tapView(this, etext1);
        getInstrumentation().sendStringSync(USERNAME);
//        // now on value2 entry
        TouchUtils.tapView(this, etext2);
        getInstrumentation().sendStringSync(PASSWORD);
        // now on Add button
        TouchUtils.tapView(this, mloginbutton);

    }


}
