package brandon.example.com.iotecapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    ImageView signin;
    TextView create, forgot;
    EditText email,password;


    //LOGIN
    private FirebaseAuth auth;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        setContentView(R.layout.activity_login);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        signin = (ImageView) findViewById(R.id.signin);
        create = (TextView) findViewById(R.id.createaccount);
        forgot = (TextView) findViewById(R.id.forgotpass);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);

        signin.setOnClickListener(this);
        create.setOnClickListener(this);
        forgot.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.signin:

                        String newemail = email.getText().toString();
                        final String newpassword = password.getText().toString();

                        if (TextUtils.isEmpty(newemail)) {
                            email.setError("Empty");
                            return;
                        }

                        if (TextUtils.isEmpty(newpassword)) {
                            password.setError("Empty");
                            return;
                        }

                        //progressBar.setVisibility(View.VISIBLE);

                        //authenticate user
                        auth.signInWithEmailAndPassword(newemail, newpassword)
                                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        // If sign in fails, display a message to the user. If sign in succeeds
                                        // the auth state listener will be notified and logic to handle the
                                        // signed in user can be handled in the listener.
                                        //progressBar.setVisibility(View.GONE);
                                        if (!task.isSuccessful()) {
                                            // there was an error
                                            if (password.length() < 6) {
                                                password.setError("Contraseña muy pequeña");
                                            } else {
                                                Toast.makeText(LoginActivity.this, "autenticación fallida", Toast.LENGTH_LONG).show();
                                            }
                                        } else {
                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                });

        break;
            case R.id.createaccount:
                Intent intent1 = new Intent(LoginActivity.this, CreateAccountActivity.class);
                startActivity(intent1);
                break;

            case R.id.forgotpass:
                Intent intent2 = new Intent(LoginActivity.this,ResetPasswordActivity.class);
                startActivity(intent2);

        }
    }
}
