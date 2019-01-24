package com.cleanseproject.cleanse;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class EMailLoginActivity extends AppCompatActivity {

    private Button btnLogIn;
    private TextView lblNewAccount;
    private EditText txtEMail;
    private EditText txtPassword;
    private boolean emailCorrecto;
    private boolean pswdCorrecta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_login);
        initializeUI();
    }

    private void initializeUI() {
        btnLogIn = findViewById(R.id.btn_login);
        txtEMail = findViewById(R.id.txt_email);
        txtPassword = findViewById(R.id.txt_pswd);
        lblNewAccount = findViewById(R.id.lbl_new_account);
        btnLogIn.setOnClickListener(v -> logIn(txtEMail.getText().toString(), txtPassword.getText().toString()));
        lblNewAccount.setOnClickListener(v -> newAccount());
        txtEMail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                emailCorrecto = checkEMail(s);
                actualizarBoton();
            }

            private boolean checkEMail(CharSequence email) {
                if (email == null)
                    return false;
                return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
            }
        });
        txtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                pswdCorrecta = !s.toString().equals("");
                actualizarBoton();
            }
        });
    }

    private void actualizarBoton() {
        btnLogIn.setEnabled(emailCorrecto && pswdCorrecta);
    }

    private void newAccount() {

    }

    private void logIn(String email, String password) {


    }


}
