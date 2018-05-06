package brandon.example.com.iotecapp.pojo;

/**
 * Created by Cesar on 06/05/18.
 */

public class Prestamo extends MaterialId{
    String nombre, matricula, uuid, duid, muid;

    public Prestamo() {
    }

    public Prestamo(String nombre, String matricula, String uuid, String duid, String muid) {
        this.nombre = nombre;
        this.matricula = matricula;
        this.uuid = uuid;
        this.duid = duid;
        this.muid = muid;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDuid() {
        return duid;
    }

    public void setDuid(String duid) {
        this.duid = duid;
    }

    public String getMuid() {
        return muid;
    }

    public void setMuid(String muid) {
        this.muid = muid;
    }

    @Override
    public String toString() {
        return "Prestamo{" +
                "nombre='" + nombre + '\'' +
                ", matricula='" + matricula + '\'' +
                ", uuid='" + uuid + '\'' +
                ", duid='" + duid + '\'' +
                ", muid='" + muid + '\'' +
                '}';
    }
}
