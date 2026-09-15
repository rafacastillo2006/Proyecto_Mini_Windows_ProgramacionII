package MiniWindows.Modelo;

import java.io.Serializable;

public class Mensaje implements Serializable {
    private static final long serialVersionUID = 1L;

    private String emisor;
    private String receptor;
    private String contenido;
    private String tipo;

    public Mensaje(String emisor, String receptor, String contenido, String tipo) {
        this.emisor = emisor;
        this.receptor = receptor;
        this.contenido = contenido;
        this.tipo = tipo;
    }

    public String getEmisor() { return emisor; }

    public String getReceptor() { return receptor; }

    public String getContenido() { return contenido; }

    public String getTipo() { return tipo; }
}