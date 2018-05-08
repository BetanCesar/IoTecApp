package brandon.example.com.iotecapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import brandon.example.com.iotecapp.pojo.Prestamo;

public class PerfilActivity extends AppCompatActivity {

    private static final String TAG = "Firelog";

    private TextView nombre, prestados, correo, matricula;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth auth;
    private DatabaseReference mRef;
    private Button logout, editar_perfil;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private ImageView credencial;

    // Firestore
    private FirebaseFirestore mFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        credencial = (ImageView) findViewById(R.id.credencial);
        nombre = (TextView) findViewById(R.id.nombre);
        prestados = (TextView) findViewById(R.id.prestados);
        correo = (TextView) findViewById(R.id.correo);
        matricula = (TextView) findViewById(R.id.matricula);
        logout = (Button) findViewById(R.id.logoutnew);
        firebaseStorage = FirebaseStorage.getInstance();
        editar_perfil = (Button) findViewById(R.id.editar_perfil);

        editar_perfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PerfilActivity.this,EditarPerfilActivity.class);
                startActivity(intent);
            }
        });

        // Firestore
        mFirestore = FirebaseFirestore.getInstance();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });

        auth = FirebaseAuth.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        setDataToView(user);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user == null){
                    Intent intent = new Intent(PerfilActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };

        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Log.e("MYLOG",uid);

        mRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                nombre.setText(dataSnapshot.child("nombre").getValue(String.class));
                String matriculanueva = dataSnapshot.child("matricula").getValue(String.class);
                matricula.setText(matriculanueva);

                storageReference = firebaseStorage.getReferenceFromUrl("gs://iotecapp.appspot.com/images").child(matriculanueva);
                final long ONE_MEGABYTE = 1024 * 1024*3;
                storageReference.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        credencial.setImageBitmap(bitmap);
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("MYLOG", "onCancelled", databaseError.toException());
            }
        });


        // Update articulos prestados
        mFirestore.collection("prestamos")
                .whereEqualTo("uuid", uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            int count = 0;
                            for (DocumentSnapshot doc : task.getResult()) {
                                count++;
                            }
                            prestados.setText(count+"");
                            Log.d(TAG, "Total: " +count);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });


    }

    private void setDataToView(FirebaseUser user) {

        correo.setText(user.getEmail());

    }

    public void signOut() {
        auth.signOut();
// this listener will be called when there is change in firebase user session
        FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    startActivity(new Intent(PerfilActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };
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
