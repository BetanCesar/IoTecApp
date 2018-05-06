package brandon.example.com.iotecapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import brandon.example.com.iotecapp.Adapters.PrestamosListAdapter;
import brandon.example.com.iotecapp.pojo.Material;
import brandon.example.com.iotecapp.pojo.Prestamo;

public class MisPrestamosActivity extends Activity {

    private static final String TAG = "Firelog";
    private RecyclerView mPrestamoList;
    private FirebaseFirestore mFirestore;
    private PrestamosListAdapter prestamosListAdapter;
    private List<Prestamo> prestamosList;

    // Auth
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth auth;

    // User
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_prestamos);

        prestamosList = new ArrayList<>();
        prestamosListAdapter = new PrestamosListAdapter(getApplicationContext(), prestamosList);

        mPrestamoList = (RecyclerView) findViewById(R.id.prestamos_list);
        mPrestamoList.setHasFixedSize(true);
        mPrestamoList.setLayoutManager(new LinearLayoutManager(this));
        mPrestamoList.setAdapter(prestamosListAdapter);

        mFirestore = FirebaseFirestore.getInstance();

        // User
        auth = FirebaseAuth.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user == null){
                    Intent intent = new Intent(MisPrestamosActivity.this,LoginActivity.class);
                    startActivity(intent);
                }
            }
        };
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

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
                                //Log.d(TAG,"Id: " + doc.getId());
                                String prestamo_id = doc.getId();
                                Prestamo prestamo = doc.toObject(Prestamo.class).withId(prestamo_id);
                                //Log.d(TAG, "Prestamo: " + prestamo.toString());
                                prestamosList.add(prestamo);
                                prestamosListAdapter.notifyDataSetChanged();
                            }
                            Log.d(TAG, "Total: " +count);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }
}
