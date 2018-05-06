package brandon.example.com.iotecapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import brandon.example.com.iotecapp.LoginActivity;
import brandon.example.com.iotecapp.MainActivity;
import brandon.example.com.iotecapp.PerfilActivity;
import brandon.example.com.iotecapp.R;
import brandon.example.com.iotecapp.pojo.Dispositivo;
import brandon.example.com.iotecapp.pojo.Material;

/**
 * Created by Cesar on 07/04/18.
 */

public class DispositivosListAdapter extends RecyclerView.Adapter<DispositivosListAdapter.ViewHolder> {

    private static final String TAG = "Firelog";

    public List<Dispositivo> dispositivosList;
    public Context context;

    // Auth
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseAuth auth;

    // Firestore
    private FirebaseFirestore mFirestore;
    private DocumentReference docRef;

    // Storage
    public FirebaseStorage storage;
    public StorageReference storageRef;

    // Database
    private DatabaseReference mRef;

    // User
    private String uid;
    private String userName, userMatricula;

    private String materialSelected;

    public DispositivosListAdapter(final Context context, List<Dispositivo> dispositivosList, String materialSelected){
        this.dispositivosList = dispositivosList;
        this.context = context;
        this.materialSelected = materialSelected;

        storage = FirebaseStorage.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://iotecapp.appspot.com/Materiales").child("Dualshock4.png");

        // User
        auth = FirebaseAuth.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user == null){
                    Intent intent = new Intent(context,LoginActivity.class);
                    context.startActivity(intent);
                }
            }
        };
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Get user data
        mRef = FirebaseDatabase.getInstance().getReference("users").child(uid);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userName = dataSnapshot.child("nombre").getValue(String.class);
                userMatricula = dataSnapshot.child("matricula").getValue(String.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("MYLOG", "onCancelled", databaseError.toException());
            }
        });
    }

    @Override
    public DispositivosListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_dispositivo, parent, false);
        return new DispositivosListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final DispositivosListAdapter.ViewHolder holder, int position) {
        final long ONE_MEGABYTE = 1024 * 1024;
        storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            }
        });
        final String deviceNumberOld = dispositivosList.get(position).getNumero();
        holder.numeroText.setText(deviceNumberOld);
        if(dispositivosList.get(position).isPrestado()){
            holder.prestadoText.setText("En préstamo");
            holder.pedirDispositivoBtn.setVisibility(View.INVISIBLE);
        }else{
            holder.prestadoText.setText("En laboratorio");
            holder.pedirDispositivoBtn.setVisibility(View.VISIBLE);
        }

        final String dispositivo_id = dispositivosList.get(position).materialId;

        // Click on Pedir button
        holder.pedirDispositivoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Update device
                docRef = mFirestore.collection("materiales").document(materialSelected);
                docRef.collection("dispositivos").document(dispositivo_id)
                    .update("numero", deviceNumberOld,
                            "prestado", true)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "DocumentSnapshot successfully updated!");

                            // Update Material disponibles
                            docRef.collection("dispositivos")
                                    .whereEqualTo("prestado", false)
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                int count = 0;
                                                for (DocumentSnapshot document : task.getResult()) {
                                                    count++;
                                                }
                                                DocumentReference todasRef = mFirestore.collection("materiales").document(materialSelected);
                                                todasRef.update("disponible", count+"")
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Log.d(TAG, "Material updated successfully");
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.w(TAG, "Error updating material", e);
                                                            }
                                                        });
                                                Log.d(TAG, "Total: "+ count);
                                            } else {
                                                Log.d(TAG, "Error getting documents: ", task.getException());
                                            }
                                        }
                                    });

                            // Update prestamo collection
                            Map<String, Object> data = new HashMap<>();
                            data.put("name", userName);
                            data.put("matricula", userMatricula);
                            data.put("uuid", uid);
                            data.put("duid", dispositivo_id);

                            mFirestore.collection("prestamos")
                                    .add(data)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.w(TAG, "Error adding document", e);
                                        }
                                    });

                            Toast.makeText(context, "Dispositivo pedido satisfactoriamente", Toast.LENGTH_SHORT).show();
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    Intent intent = new Intent(context, MainActivity.class);
                                    context.startActivity(intent);
                                }
                            }, 1000);
                        } // End success
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error updating document", e);
                        }
                    });

                // Toast.makeText(context, "Dispositivo ID: " + dispositivo_id, Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return dispositivosList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public TextView numeroText, prestadoText;
        public ImageView materialImage1;
        public Button pedirDispositivoBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            numeroText = (TextView) mView.findViewById(R.id.numero_text);
            prestadoText = (TextView) mView.findViewById(R.id.prestado_text);
            materialImage1 = (ImageView) mView.findViewById(R.id.materialImageDetail);
            pedirDispositivoBtn = (Button) mView.findViewById(R.id.btnPedirDispositivo);
        }
    }


}