package brandon.example.com.iotecapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import brandon.example.com.iotecapp.DispositivosActivity;
import brandon.example.com.iotecapp.R;
import brandon.example.com.iotecapp.pojo.Material;

/**
 * Created by Cesar on 07/04/18.
 */

public class MaterialesListAdapter extends RecyclerView.Adapter<MaterialesListAdapter.ViewHolder> {

    private static final String TAG = "Firelog";
    public List<Material> materialsList;
    public Context context;
    public FirebaseStorage storage;
    public StorageReference storageRef;

    //private FirebaseFirestore mFirestore;
    //public DocumentReference docRef;

    public MaterialesListAdapter(Context context, List<Material> materialsList){
        this.materialsList = materialsList;
        this.context = context;

        storage = FirebaseStorage.getInstance();

        //mFirestore = FirebaseFirestore.getInstance();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_material, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        storageRef = storage.getReferenceFromUrl("gs://iotecapp.appspot.com/Materiales").child(materialsList.get(position).getImage());
        final long ONE_MEGABYTE = 1024 * 1024;
        storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                holder.materialImage.setImageBitmap(bitmap);
            }
        });
        holder.nombreText.setText(materialsList.get(position).getNombre());
        holder.cantidadText.setText(materialsList.get(position).getCantidad());
        holder.disponibleText.setText(materialsList.get(position).getDisponible());

        final String material_id = materialsList.get(position).materialId;

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context, "Material ID: " + material_id, Toast.LENGTH_SHORT).show();
                /*
                long number = 0l;
                Random rand = new Random();
                number = (rand.nextInt(1000000)+1000000000l) * (rand.nextInt(900)+100);



                Map<String, Object> dispositivo = new HashMap<>();
                dispositivo.put("numero", number+"");
                dispositivo.put("prestado", false);
                docRef = mFirestore.collection("materiales").document(material_id);
                docRef.collection("dispositivos").document()
                        .set(dispositivo)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully written!");


                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error writing document", e);
                            }
                        });
                */
                Intent intent = new Intent(context,DispositivosActivity.class);
                intent.putExtra("id", material_id);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return materialsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public TextView nombreText, cantidadText, disponibleText;
        public ImageView materialImage;

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            nombreText = (TextView) mView.findViewById(R.id.nombre_text);
            cantidadText = (TextView) mView.findViewById(R.id.cantidad_text);
            disponibleText = (TextView) mView.findViewById(R.id.disponible_text);
            materialImage = (ImageView) mView.findViewById(R.id.materialImagePrimary);
        }
    }


}
