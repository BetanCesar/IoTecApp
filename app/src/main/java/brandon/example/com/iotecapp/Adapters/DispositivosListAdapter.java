package brandon.example.com.iotecapp.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import brandon.example.com.iotecapp.R;
import brandon.example.com.iotecapp.pojo.Dispositivo;
import brandon.example.com.iotecapp.pojo.Material;

/**
 * Created by Cesar on 07/04/18.
 */

public class DispositivosListAdapter extends RecyclerView.Adapter<DispositivosListAdapter.ViewHolder> {

    public List<Dispositivo> dispositivosList;
    public Context context;
    public FirebaseStorage storage;
    public StorageReference storageRef;

    public DispositivosListAdapter(Context context, List<Dispositivo> dispositivosList){
        this.dispositivosList = dispositivosList;
        this.context = context;
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl("gs://iotecapp.appspot.com/Materiales").child("Dualshock4.png");
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
        holder.numeroText.setText(dispositivosList.get(position).getNumero());
        if(dispositivosList.get(position).isPrestado()){
            holder.prestadoText.setText("En préstamo");
        }else{
            holder.prestadoText.setText("En laboratorio");
        }


        final String dispositivo_id = dispositivosList.get(position).materialId;

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Dispositivo ID: " + dispositivo_id, Toast.LENGTH_SHORT).show();
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

        public ViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            numeroText = (TextView) mView.findViewById(R.id.numero_text);
            prestadoText = (TextView) mView.findViewById(R.id.prestado_text);
            materialImage1 = (ImageView) mView.findViewById(R.id.materialImageDetail);
        }
    }


}