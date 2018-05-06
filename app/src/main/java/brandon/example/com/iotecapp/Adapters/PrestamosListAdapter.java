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

import brandon.example.com.iotecapp.LoginActivity;
import brandon.example.com.iotecapp.MainActivity;
import brandon.example.com.iotecapp.R;
import brandon.example.com.iotecapp.pojo.Material;
import brandon.example.com.iotecapp.pojo.Prestamo;

/**
 * Created by Cesar on 06/05/18.
 */

public class PrestamosListAdapter extends RecyclerView.Adapter<PrestamosListAdapter.ViewHolder> {

    private static final String TAG = "Firelog";

    public List<Prestamo> prestamosList;
    public Context context;

    // Firestore
    private FirebaseFirestore mFirestore;
    private DocumentReference docRef;

    // Storage
    public FirebaseStorage storage;
    public StorageReference storageRef;

    // Material
    private String nombreMaterial;
    private String materialId;
    private String deviceId;
    private String prestamoId;

    public PrestamosListAdapter(final Context context, List<Prestamo> prestamosList){
        this.prestamosList = prestamosList;
        this.context = context;

        storage = FirebaseStorage.getInstance();
        mFirestore = FirebaseFirestore.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://iotecapp.appspot.com/Materiales").child("Dualshock4.png");
    }


    @Override
    public PrestamosListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_prestamo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        //storageRef = storage.getReferenceFromUrl("gs://iotecapp.appspot.com/Materiales").child(materialsList.get(position).getImage());

        // Get prestamos data
        /*mFirestore.collection("prestamos")
                .whereEqualTo("uuid", uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
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
                */
        final String muid = prestamosList.get(position).getMuid();
        final String duid = prestamosList.get(position).getDuid();
        prestamoId = prestamosList.get(position).materialId;
        materialId = muid;
        deviceId = duid;
        DocumentReference docRef = mFirestore.collection("materiales").document(muid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        Material material = task.getResult().toObject(Material.class).withId(muid);
                        Log.d(TAG, "Nombre material:" + material.getNombre());
                        nombreMaterial = material.getNombre();
                        success(holder, nombreMaterial, material.getImage());
                        Log.d(TAG, "DocumentSnapshot data: " + task.getResult().getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        //Log.d(TAG, "AHHHH: " + nombreMaterial);
        //holder.materialNameText.setText(nombreMaterial);

        holder.devolverDispositivoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                returnDevice(holder);
            }
        });
        //Log.d(TAG, "Adapter: " + prestamosList.get(position).getDuid());
    }

    public void success(final ViewHolder holder, String name, String image){
        holder.materialNameText.setText(name);
        storageRef = storage.getReferenceFromUrl("gs://iotecapp.appspot.com/Materiales").child(image);
        final long ONE_MEGABYTE = 1024 * 1024;
        storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                successImage(holder, bitmap);
            }
        });
    }
    public void successImage(ViewHolder holder, Bitmap bitmap){
        holder.materialPrestamoImage.setImageBitmap(bitmap);
    }

    public void returnDevice(ViewHolder holder){
        // Update device
        docRef = mFirestore.collection("materiales").document(materialId);
        docRef.collection("dispositivos").document(deviceId)
                .update("prestado", false)
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
                                            DocumentReference todasRef = mFirestore.collection("materiales").document(materialId);
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

                        // Delete prestamo collection
                        mFirestore.collection("prestamos").document(prestamoId)
                                .delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                Toast.makeText(context, "Artículo devuelto", Toast.LENGTH_LONG).show();
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    public void run() {
                                        Intent intent = new Intent(context, MainActivity.class);
                                        context.startActivity(intent);
                                    }
                                }, 1000);
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "Error deleting document", e);
                                    }
                                });


                    } // End success
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return prestamosList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public TextView materialNameText;
        public ImageView materialPrestamoImage;
        public Button devolverDispositivoBtn;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            materialNameText = (TextView) mView.findViewById(R.id.nombre_text);
            materialPrestamoImage = (ImageView) mView.findViewById(R.id.materialPrestamoImage);
            devolverDispositivoBtn = (Button) mView.findViewById(R.id.btnDevolverDispositivo);
        }
    }
}
