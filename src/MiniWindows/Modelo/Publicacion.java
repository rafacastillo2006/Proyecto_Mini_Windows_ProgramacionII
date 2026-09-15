package MiniWindows.Modelo;

import java.io.Serializable;

public class Publicacion implements Serializable {
    private static final long serialVersionUID = 1L;

    private String autor;
    private String descripcion;
    private String rutaImagen;
    private String tipoMobile;

    public Publicacion(String autor, String descripcion, String rutaImagen, String tipoMobile) {
        this.autor = autor;
        this.descripcion = descripcion;
        this.rutaImagen = rutaImagen;
        this.tipoMobile = tipoMobile;
    }

    public String getAutor() { return autor; }
    public String getDescripcion() { return descripcion; }
    public String getRutaImagen() { return rutaImagen; }
    public String getTipoMobile() { return tipoMobile; }
}