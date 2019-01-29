package com.cleanseproject.cleanse;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private final int RC_GOOGLE_SIGN_IN = 9001;

    private SignInButton btnGoogle;
    private Button btnFacebook;
    private Button btnEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnGoogle = findViewById(R.id.google_sign_in);
        btnGoogle.setSize(SignInButton.SIZE_STANDARD);
        btnFacebook = findViewById(R.id.btn_facebook);
        btnEmail = findViewById(R.id.btn_email);
        btnEmail.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, EMailLoginActivity.class)));
        btnGoogle.setOnClickListener(v -> googleSignIn());
        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void googleSignIn() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.firebase_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(LoginActivity.this, googleSignInOptions);
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthGoogle(account);
                Log.d("mail", account.getEmail());
            } catch (ApiException e) {
                errorInicioSesion();
                e.printStackTrace();
            }

        }
    }

    private void firebaseAuthGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        iniciarSesion(firebaseAuth.getCurrentUser());
                    } else {
                        errorInicioSesion();
                    }
                });
    }

    private void iniciarSesion(FirebaseUser user) {
        // TODO: Iniciar sesión con Google
        Log.d("mail", user.getDisplayName() + " " + user.getPhoneNumber());
    }

    private void errorInicioSesion() {
        // TODO: Error en el inicio de sesión
        // Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
        Toast.makeText(LoginActivity.this, getString(R.string.google_sign_in_error), Toast.LENGTH_SHORT).show();
    }

}
