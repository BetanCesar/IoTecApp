package brandon.example.com.iotecapp.AcercaDe;

/**
 * Created by brandon on 04/04/18.
 */

import android.media.Image;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import brandon.example.com.iotecapp.R;

public class ImagenUno extends Fragment{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.imagen1, container, false);
        return rootView;
    }
}
