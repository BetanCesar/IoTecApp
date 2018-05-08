package brandon.example.com.iotecapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class CreateAccountActivity extends AppCompatActivity {


    //LOGIN
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private EditText nombre, email, contraseña, matricula;
    private ImageView signup, camera, credencial, buttonback;
    private TextView gosignin;


    private Uri filePath;

    private final int PICK_IMAGE_REQUEST = 71;
    FirebaseStorage storage;
    StorageReference storageReference;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        auth = FirebaseAuth.getInstance();
        nombre = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.emailnew);
        contraseña = (EditText) findViewById(R.id.passwordnew);
        matricula = (EditText) findViewById(R.id.matricula);
        signup = (ImageView) findViewById(R.id.savedata);
        camera = (ImageView) findViewById(R.id.camera);
        credencial = (ImageView) findViewById(R.id.image_profile);
        buttonback = (ImageView) findViewById(R.id.backButton);
        gosignin = (TextView) findViewById(R.id.gosignin);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();


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


                                    //CODIGO PARA SUBIR IMAGEN
                                    uploadImage(newmatricula);


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

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                credencial.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }


    public void imageButtonClick() {

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void uploadImage(String newmatricula) {
        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/"+ newmatricula);
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //progressDialog.dismiss();
                            Toast.makeText(CreateAccountActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(CreateAccountActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //progressBar.setVisibility(View.GONE);
    }
}
