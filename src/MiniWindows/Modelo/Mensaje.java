package MiniWindows.Modelo;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Mensaje implements Serializable {

    private static final long serialVersionUID = 3L;

    public static final int LIMITE_CARACTERES = 300;
    public static final String TEXTO = "Texto";
    public static final String STICKER = "Sticker";

    private final String emisor;
    private final String receptor;
    private final String contenido;
    private final String tipo;
    private final byte[] sticker;
    private final LocalDateTime fecha;
    private boolean leido;

    public Mensaje(String emisor, String receptor, String contenido) {
        this(emisor, receptor, contenido, TEXTO, null);
    }

    public Mensaje(String emisor, String receptor, String contenido, String tipo, byte[] sticker) {
        this.emisor = emisor;
        this.receptor = receptor;
        this.contenido = contenido;
        this.tipo = tipo;
        this.sticker = sticker;
        this.fecha = LocalDateTime.now();
        this.leido = false;
    }

    public String getEmisor() {
        return emisor;
    }

    public String getReceptor() {
        return receptor;
    }

    public String getContenido() {
        return contenido == null ? "" : contenido;
    }

    public String getTipo() {
        return tipo;
    }

    public boolean esSticker() {
        return STICKER.equals(tipo);
    }

    public byte[] getSticker() {
        return sticker;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public boolean estaLeido() {
        return leido;
    }

    public void marcarLeido() {
        this.leido = true;
    }
}
