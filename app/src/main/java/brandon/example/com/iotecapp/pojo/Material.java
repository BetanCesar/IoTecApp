package brandon.example.com.iotecapp.pojo;

/**
 * Created by Cesar on 07/04/18.
 */

public class Material extends MaterialId{
    String nombre, cantidad, disponible;

    public Material() {
    }

    public Material(String nombre, String cantidad, String disponible) {
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.disponible = disponible;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public String getDisponible() {
        return disponible;
    }

    public void setDisponible(String disponible) {
        this.disponible = disponible;
    }
}
