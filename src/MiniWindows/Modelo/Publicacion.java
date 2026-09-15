package MiniWindows.Modelo;

import java.io.Serializable;
import java.util.Date;

public class Publicacion implements Serializable {
    private static final long serialVersionUID = 1L;

    private String autor;
    private String contenido;
    private Date fecha;
    private String rutaImagen;
    private String modoMobile;

    public Publicacion(String autor, String contenido, String rutaImagen, String modoMobile) {
        this.autor = autor;
        this.contenido = contenido;
        this.fecha = new Date();
        this.rutaImagen = rutaImagen;
        this.modoMobile = modoMobile;
    }


    public String getAutor() { return autor; }

    public String getContenido() { return contenido; }

    public Date getFecha() { return fecha; }

    public String getRutaImagen() { return rutaImagen; }

    public String getModoMobile() { return modoMobile; }
}