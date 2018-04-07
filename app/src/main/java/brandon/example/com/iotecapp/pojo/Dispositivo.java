package brandon.example.com.iotecapp.pojo;

/**
 * Created by Cesar on 07/04/18.
 */

public class Dispositivo extends MaterialId{
    String numero;
    boolean prestado;

    public Dispositivo() {
    }

    public Dispositivo(String numero, boolean prestado) {
        this.numero = numero;
        this.prestado = prestado;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public boolean isPrestado() {
        return prestado;
    }

    public void setPrestado(boolean prestado) {
        this.prestado = prestado;
    }
}
