package com.cleanseproject.cleanse;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class EMailLoginActivity extends AppCompatActivity {

    private Button btnLogIn;
    private EditText txtEMail;
    private EditText txtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_login);
        btnLogIn = findViewById(R.id.btn_login);
        txtEMail = findViewById(R.id.txt_email);
        txtPassword = findViewById(R.id.txt_pswd);
        btnLogIn.setOnClickListener(v -> logIn(txtEMail.getText().toString(), txtPassword.getText().toString()));
    }

    private void logIn(String email, String password) {
        if (checkEMail(email) && checkPassword(password)) {
            // TODO: Iniciar sesión con Firebase
        }

    }

    private boolean checkEMail(String email) {
        // TODO: Comprobar si el mail está en blanco, bien formado y mandar advertendia a través de label
        return false;
    }

    private boolean checkPassword(String password) {
        // TODO: Comprobar si se ha insertado una contraseña
        return false;
    }


}
