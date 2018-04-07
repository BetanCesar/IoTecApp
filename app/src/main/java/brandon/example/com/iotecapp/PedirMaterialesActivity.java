package brandon.example.com.iotecapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.firebase.client.Firebase;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import brandon.example.com.iotecapp.Adapters.MaterialesListAdapter;
import brandon.example.com.iotecapp.pojo.Material;

public class PedirMaterialesActivity extends AppCompatActivity {

    private static final String TAG = "Firelog";
    private RecyclerView mMaterialList;
    private FirebaseFirestore mFirestore;
    private MaterialesListAdapter materialesListAdapter;
    private List<Material> materialsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedir_materiales);

        materialsList = new ArrayList<>();
        materialesListAdapter = new MaterialesListAdapter(getApplicationContext(), materialsList);

        mMaterialList = (RecyclerView) findViewById(R.id.material_list);
        mMaterialList.setHasFixedSize(true);
        mMaterialList.setLayoutManager(new LinearLayoutManager(this));
        mMaterialList.setAdapter(materialesListAdapter);

        mFirestore = FirebaseFirestore.getInstance();

        mFirestore.collection("materiales").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if(e != null){
                    Log.d(TAG, "Error: " + e.getMessage());
                }

                for(DocumentChange doc: documentSnapshots.getDocumentChanges()){
                    if(doc.getType() == DocumentChange.Type.ADDED){

                        String material_id = doc.getDocument().getId();

                        Material material = doc.getDocument().toObject(Material.class).withId(material_id);
                        materialsList.add(material);

                        materialesListAdapter.notifyDataSetChanged();
                    }
                }
            }
        });


    }


}
