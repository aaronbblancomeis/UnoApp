package com.uno.activities;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.firestore.DocumentSnapshot;
import com.uno.R;
import com.uno.models.User;
import com.uno.providers.AuthProvider;
import com.uno.providers.UserProvider;



public class MainActivity extends AppCompatActivity {

    /**
     * TODO
     * 1. Splash Screen
     * 2. Pantalla de carga
     * 3. SpotsDialog (25. Alert Dialog) No funciona pq pone que estan deprecated las librerias que usa
     * 4. Pantalla inicial será home y para inciar sesión se hara desde account (estilo Zalando,Amazon...)
     * 5. Diseño de paleta de colores para la aplicación y mejorar diseño...
     * 6. En el Activity_product_post poner una X para cerrar y volver a la pantalla anterior
     * 7. Categoria será un menu en forma de arbol ( Ropa , Calzado ) -> ( Camisetas , Pantalones...) --> Mejorar
     * 8. Quitar el scroll al menu
     */

    TextView mTextViewRegister;
    TextInputEditText mTextInputEmail;
    TextInputEditText mTextInputPassword;
    Button mButtonLogin;
    SignInButton mButtonGoogle;
    UserProvider mUserProvider;
    AuthProvider mAuthProvider;

    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextViewRegister = findViewById(R.id.register);
        mTextInputEmail = findViewById(R.id.email);
        mTextInputPassword = findViewById(R.id.password);
        mButtonLogin = findViewById(R.id.btn_login);
        mButtonGoogle = findViewById(R.id.btnLoginGoogle);

        mUserProvider = new UserProvider();
        mAuthProvider = new AuthProvider();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);



        mButtonGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInGoogle();
            }
        });

        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        mTextViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Log In con correo y contraseña
     */
    private void login() {
        String email = mTextInputEmail.getText().toString();
        String password = mTextInputPassword.getText().toString();

        mAuthProvider.login(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(MainActivity.this, "El email o la contraseña que ingresaste no son correctas", Toast.LENGTH_LONG).show();
                }
            }
        });
        Log.d("CAMPO", "email: " + email);
        Log.d("CAMPO", "password: " + password);
    }

    /**
     *   @Override
     *     public void onStart() {
     *         super.onStart();
     *         // Check if user is signed in (non-null) and update UI accordingly.
     *         FirebaseUser currentUser = mAuth.getCurrentUser();
     *         Intent intent = new Intent(MainActivity.this,HomeActivity.class);
     *         startActivity(intent);
     *     }
     */

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    /**
     * Obtiene el idToken y autentifica que existe
     */
    private void firebaseAuthWithGoogle(String idToken) {
       mAuthProvider.googleLogin(idToken)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            checkUserExit(mAuthProvider.getUid());
                        } else {
                            Toast.makeText(MainActivity.this,"Error al iniciar sesion con google", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    /**
     * Check in the database if the user exist
     */
    private void checkUserExit(String id) {
        mUserProvider.getUser(id).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    Intent intent = new Intent(MainActivity.this,HomeActivity.class);
                    startActivity(intent);
                }else{
                    User user = new User();
                    user.setEmail(mAuthProvider.getEmail());
                    user.setUsername(mAuthProvider.getDisplayName());
                    user.setId(id);
                    mUserProvider.createUser(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Intent intent = new Intent(MainActivity.this,HomeActivity.class);
                                startActivity(intent);
                            }else {
                                Toast.makeText(MainActivity.this, "No se pudo alamcenar la informacion del usuario", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
}