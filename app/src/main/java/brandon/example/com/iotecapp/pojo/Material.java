package brandon.example.com.iotecapp.pojo;

/**
 * Created by Cesar on 07/04/18.
 */

public class Material extends MaterialId{
    String nombre, cantidad, disponible, image;

    public Material() {
    }

    public Material(String nombre, String cantidad, String disponible, String image) {
        this.nombre = nombre;
        this.cantidad = cantidad;

        this.disponible = disponible;
        this.image = image;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
