package com.cleanseproject.cleanse.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cleanseproject.cleanse.R;
import com.cleanseproject.cleanse.dataClasses.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private final int RC_GOOGLE_SIGN_IN = 9001;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callBacks;

    private String phoneVerificationId;
    private PhoneAuthProvider.ForceResendingToken phoneToken;

    private Button btnGoogle;
    private Button btnPhone;
    private Button btnEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnGoogle = findViewById(R.id.google_sign_i);
        btnPhone = findViewById(R.id.btn_phone);
        btnEmail = findViewById(R.id.btn_email);
        btnEmail.setOnClickListener(v -> initializeEmailUI());
        btnGoogle.setOnClickListener(v -> googleSignIn());
        btnPhone.setOnClickListener(v -> phoneDialog());
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        callBacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                authWithPhone(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                e.printStackTrace();
                Toast.makeText(LoginActivity.this, "Verification failed", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                phoneVerificationId = verificationId;
                phoneToken = token;
                final EditText txtPhone = findViewById(R.id.txt_phone);
                btn_Login_phone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        authWithPhone(PhoneAuthProvider.getCredential(phoneVerificationId, txtPhone.getText().toString()));
                    }
                });
            }
        };
    }

    /**
     * Comienza el inicio de sesión con la cuenta de Google
     */
    private void googleSignIn() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.firebase_web_client_id))
                .requestEmail()
                .build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(LoginActivity.this, googleSignInOptions);
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_GOOGLE_SIGN_IN);
    }

    Button btn_Login_phone;
    EditText txt_phonee;
    TextView txtNumeroRegion;

    private void phoneDialog() {
        setContentView(R.layout.dialog_phone_prompt);
        btn_Login_phone = findViewById(R.id.btn_login_phone);
        txt_phonee = findViewById(R.id.txt_phone);
        txtNumeroRegion = findViewById(R.id.txt_numero_de_region);

        btn_Login_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneSignIn(txtNumeroRegion.getText().toString() + txt_phonee.getText().toString());
                txt_phonee.setText("");
                txt_phonee.setHint("Type your code");
                txtNumeroRegion.setText("");
                btn_Login_phone.setText("Verify");

            }
        });
    }

    /**
     * Iniciar sesión a través de teléfono
     *
     * @param phoneNumber Número de teléfono en formato E.164
     */
    private void phoneSignIn(String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60,
                TimeUnit.SECONDS,
                LoginActivity.this,
                callBacks);
    }

    /**
     * Inicia sesión con Firebase
     *
     * @param credential Credenciales obtenidas
     */
    private void authWithPhone(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("phone", "signInWithCredential:success");
                        iniciarSesion(task.getResult().getUser());
                    } else {
                        // Sign in failed, display a message and update the UI
                        Log.w("phone", "signInWithCredential:failure", task.getException());
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            // The verification code entered was invalid
                        }
                    }
                });
    }

    /**
     * Override de onActivityResult que será llamado al iniciar sesión con Google
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthGoogle(Objects.requireNonNull(account));
                Log.d("mail", account.getEmail());
            } catch (ApiException | NullPointerException e) {
                errorInicioSesion();
                e.printStackTrace();
            }

        }
    }

    /**
     * Iniciar sesión en Firebase con la cuenta de Google
     *
     * @param account Cuenta de Google
     */
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
        Log.d("mail", user.getDisplayName() + " " + user.getPhoneNumber());
        DatabaseReference userReference = firebaseDatabase.getReference("users");
        userReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(user.getUid())) {
                    cambiarActividad(user);
                } else {
                    userReference.child(user.getUid()).setValue(new User());
                    startActivity(new Intent(LoginActivity.this, UserDataActivity.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void cambiarActividad(FirebaseUser user) {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        if (user.getEmail() != null) {
            intent.putExtra("username", user.getEmail());
        } else if (user.getPhoneNumber() != null) {
            intent.putExtra("username", user.getPhoneNumber());
        }
        startActivity(intent);
    }

    private void errorInicioSesion() {
        // TODO: Error en el inicio de sesión
        // Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
        Toast.makeText(LoginActivity.this, getString(R.string.google_sign_in_error), Toast.LENGTH_SHORT).show();
    }

    private Button btnLogIn;
    private Button btnSignUp;
    private TextView lblForgotPassword;
    private EditText txtEMail;
    private EditText txtPassword;
    private boolean emailCorrecto;
    private boolean pswdCorrecta;

    private void initializeEmailUI() {
        setContentView(R.layout.activity_email_login);
        btnLogIn = findViewById(R.id.btn_login);
        btnSignUp = findViewById(R.id.btn_sign_up);
        txtEMail = findViewById(R.id.txt_email);
        txtPassword = findViewById(R.id.txt_pswd);
        lblForgotPassword = findViewById(R.id.lbl_forgot_password);
        btnLogIn.setOnClickListener(v -> logIn(txtEMail.getText().toString(), txtPassword.getText().toString()));
        btnSignUp.setOnClickListener(v -> signUp(txtEMail.getText().toString(), txtPassword.getText().toString()));
        lblForgotPassword.setOnClickListener(v -> newAccount());
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
        btnSignUp.setEnabled(emailCorrecto && pswdCorrecta);
    }

    private void newAccount() {

    }

    private void logIn(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = task.getResult().getUser();
                        if (user.isEmailVerified()) {
                            iniciarSesion(user);
                        } else {
                            user.sendEmailVerification()
                                    .addOnCompleteListener(t -> {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(LoginActivity.this, "Please verify your email",
                                                    Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    } else {
                        // TODO: credenciales incorrectas
                    }
                });
    }

    private void signUp(String email, String password) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = task.getResult().getUser();
                        user.sendEmailVerification()
                                .addOnCompleteListener(t -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this, "Verification email sent",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    public void onBackPressed() {
        recreate();
    }


}
