package brandon.example.com.iotecapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import brandon.example.com.iotecapp.Adapters.DispositivosListAdapter;
import brandon.example.com.iotecapp.pojo.Dispositivo;
import brandon.example.com.iotecapp.pojo.Material;

public class DispositivosActivity extends Activity {

    private static final String TAG = "Firelog";
    private RecyclerView mDispositivoList;
    private FirebaseFirestore mFirestore;
    private DispositivosListAdapter dispositivosListAdapter;
    private List<Dispositivo> dispositivoList;
    private TextView nombreMaterialText;
    private ImageView materialImageDetail;

    public FirebaseStorage mStorage;
    public StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dispositivos);

        nombreMaterialText = (TextView) findViewById(R.id.nombre_material);
        materialImageDetail = (ImageView) findViewById(R.id.materialImageDetail);

        Intent intent = getIntent();
        final String material_selected = intent.getExtras().getString("id");

        dispositivoList = new ArrayList<>();
        dispositivosListAdapter = new DispositivosListAdapter(getApplicationContext(), dispositivoList, material_selected);

        mDispositivoList = (RecyclerView) findViewById(R.id.dispositivos_list);
        mDispositivoList.setHasFixedSize(true);
        mDispositivoList.setLayoutManager(new LinearLayoutManager(this));
        mDispositivoList.setAdapter(dispositivosListAdapter);

        mFirestore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance();

        DocumentReference docRef = mFirestore.collection("materiales").document(material_selected);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        Material material = task.getResult().toObject(Material.class).withId(material_selected);
                        nombreMaterialText.setText(material.getNombre());
                        mStorageRef = mStorage.getReferenceFromUrl("gs://iotecapp.appspot.com/Materiales").child(material.getImage());
                        final long ONE_MEGABYTE = 1024 * 1024;
                        mStorageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                            @Override
                            public void onSuccess(byte[] bytes) {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                materialImageDetail.setImageBitmap(bitmap);
                            }
                        });
                        Log.d(TAG, "DocumentSnapshot data: " + task.getResult().getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        docRef.collection("dispositivos").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if(e != null){
                    Log.d(TAG, "Error: " + e.getMessage());
                }

                for(DocumentChange doc: documentSnapshots.getDocumentChanges()){
                    if(doc.getType() == DocumentChange.Type.ADDED){

                        String dispositivo_id = doc.getDocument().getId();

                        Dispositivo dispositivo = doc.getDocument().toObject(Dispositivo.class).withId(dispositivo_id);
                        dispositivoList.add(dispositivo);

                        dispositivosListAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }
}
