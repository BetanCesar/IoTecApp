package brandon.example.com.iotecapp.pojo;

import android.support.annotation.NonNull;

/**
 * Created by Cesar on 07/04/18.
 */

public class MaterialId {

    public String materialId;

    public <T extends MaterialId> T withId(@NonNull final String id){
        this.materialId = id;
        return (T) this;
    }

}
