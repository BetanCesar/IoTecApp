package brandon.example.com.iotecapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class EditarPerfilActivity extends AppCompatActivity {
    private ImageView savedata;


    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth auth;
    private DatabaseReference mRef;


    private EditText nombre, email, matricula;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil);

        nombre = (EditText) findViewById(R.id.name);
        email = (EditText) findViewById(R.id.emailnew);
        matricula = (EditText) findViewById(R.id.matricula);
        savedata = (ImageView) findViewById(R.id.savedata);

        auth = FirebaseAuth.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user == null){
                    Intent intent = new Intent(EditarPerfilActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();


        mRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                nombre.setText(dataSnapshot.child("nombre").getValue(String.class));
                String matriculanueva = dataSnapshot.child("matricula").getValue(String.class);
                matricula.setText(matriculanueva);
                email.setText(dataSnapshot.child("correo").getValue(String.class));


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("MYLOG", "onCancelled", databaseError.toException());
            }
        });




        savedata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final String newemail = email.getText().toString().trim();
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

                if (TextUtils.isEmpty(newemail)) {
                    Toast.makeText(getApplication(), "Enter your registered email id", Toast.LENGTH_SHORT).show();
                    return;
                }

                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
                DatabaseReference currentUserDB = mDatabase.child(auth.getCurrentUser().getUid());
                currentUserDB.child("nombre").setValue(newname);
                currentUserDB.child("matricula").setValue(newmatricula);
                currentUserDB.child("correo").setValue(newemail);

                Intent intent = new Intent(EditarPerfilActivity.this, MainActivity.class);
                startActivity(intent);
                finish();


                }


        });




    }


    @Override
    protected void onResume() {
        super.onResume();
        //progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        auth.addAuthStateListener(authStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (authStateListener != null) {
            auth.removeAuthStateListener(authStateListener);
        }
    }
}
