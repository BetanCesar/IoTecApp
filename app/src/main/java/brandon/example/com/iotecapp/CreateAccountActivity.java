package brandon.example.com.iotecapp;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateAccountActivity extends AppCompatActivity {


    //LOGIN
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private EditText nombre, email, contraseña, matricula;
    private ImageView signup, camera, credencial, buttonback;
    private TextView gosignin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        auth = FirebaseAuth.getInstance();
        nombre = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.emailnew);
        contraseña = (EditText) findViewById(R.id.passwordnew);
        matricula = (EditText) findViewById(R.id.matricula);
        signup = (ImageView) findViewById(R.id.signup);
        camera = (ImageView) findViewById(R.id.camera);
        credencial = (ImageView) findViewById(R.id.image_profile);
        buttonback = (ImageView) findViewById(R.id.backButton);
        gosignin = (TextView) findViewById(R.id.gosignin);


        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String newemail = email.getText().toString().trim();
                String newpass = contraseña.getText().toString().trim();
                final String newname = nombre.getText().toString().trim();
                final String newmatricula = matricula.getText().toString().trim();

                if(TextUtils.isEmpty(newname)){
                    nombre.setError("Ingresa tu nombre");
                    return;
                }
                if(TextUtils.isEmpty(newmatricula)){
                    matricula.setError("Ingresa matricula");
                    return;
                }

                if (TextUtils.isEmpty(newemail)) {
                    email.setError("Ingresa tu correo");
                    return;
                }

                if (TextUtils.isEmpty(newpass)) {
                    contraseña.setError("Ingresa tu contraseña");
                    return;
                }

                if (newpass.length() < 6) {
                    contraseña.setError("Contraseña muy corta, minimo 6 caracteres");
                    return;
                }

                //progressBar.setVisibility(View.VISIBLE)
                auth.createUserWithEmailAndPassword(newemail,newpass)
                        .addOnCompleteListener(CreateAccountActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(CreateAccountActivity.this, "createUser:onComplete:"+task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                //progressBar.setVisibolity(View.GONE);
                                if(!task.isSuccessful()){
                                    Toast.makeText(CreateAccountActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                                }else{
                                    DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
                                    DatabaseReference currentUserDB = mDatabase.child(auth.getCurrentUser().getUid());
                                    currentUserDB.child("nombre").setValue(newname);
                                    currentUserDB.child("matricula").setValue(newmatricula);
                                    currentUserDB.child("correo").setValue(newemail);
                                    Intent intent = new Intent(CreateAccountActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageButtonClick();
            }
        });

        buttonback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        gosignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch (requestCode) {
            case 0:
                if (resultCode == this.RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    credencial.setImageURI(selectedImage);
                }

                break;
            case 1:
                if (resultCode == this.RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    credencial.setImageURI(selectedImage);
                }
                break;
        }
    }

    public void imageButtonClick() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, 1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //progressBar.setVisibility(View.GONE);
    }
}
