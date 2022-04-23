package com.uno.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import com.uno.R;
import com.uno.models.User;
import com.uno.providers.AuthProvider;
import com.uno.providers.UserProvider;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    ImageView mViewBack;
    TextInputEditText mTextViewUserName;
    TextInputEditText mTextViewUserEmail;
    TextInputEditText mTextViewUserPassword;
    TextInputEditText mTextViewUserConfirmPassword;
    Button mButtonRegister;
    UserProvider mUserProvider;
    AuthProvider mAuthProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mViewBack = findViewById(R.id.IconBack);
        mTextViewUserName = findViewById(R.id.userName);
        mTextViewUserEmail = findViewById(R.id.userEmail);
        mTextViewUserPassword = findViewById(R.id.userPassword);
        mTextViewUserConfirmPassword = findViewById(R.id.userConfirmPassword);
        mButtonRegister = findViewById(R.id.btn_register);

        mUserProvider = new UserProvider();
        mAuthProvider = new AuthProvider();

        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });

        mViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void register() {

        String userName = mTextViewUserName.getText().toString();
        String email = mTextViewUserEmail.getText().toString();
        String password = mTextViewUserPassword.getText().toString();
        String confirmPassword = mTextViewUserConfirmPassword.getText().toString();

        if(!userName.isEmpty() && !email.isEmpty() && !password.isEmpty() && !confirmPassword.isEmpty()) {
            if(isEmailValid(email)){
                if(password.equals(confirmPassword)){
                    if(password.length() >= 6){
                        createUser(email,password,userName);
                    }else{
                        Toast.makeText(this,"La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_LONG).show();
                    }
                }
            }else {
                Toast.makeText(this,"Has insertado todos los campos pero el email no es valido", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(this,"No has insertado todos los campos", Toast.LENGTH_LONG).show();
        }
    }

    private void createUser(final String email, final String password, final String userName) {
        mAuthProvider.register(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    String id = mAuthProvider.getUid();
                    User user = new User();
                    user.setEmail(email);
                    user.setUsername(userName);
                    user.setId(id);
                    mUserProvider.createUser(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(RegisterActivity.this,"Usuario registrado con exito", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            }else{
                                Toast.makeText(RegisterActivity.this,"Usuario no se ha registrado con exito", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(RegisterActivity.this,"No se pudo registrar el usuario", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    /**
     * VERIFICA QUE SEA UN EMAIL VALIDO
     */
    public boolean isEmailValid(String email) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}