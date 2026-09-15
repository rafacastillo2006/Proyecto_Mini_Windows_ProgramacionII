package MiniWindows.Modelo;

import java.io.Serializable;
import java.util.Date;

public class Mensaje implements Serializable {
    private static final long serialVersionUID = 1L;

    private String emisor;
    private String receptor;
    private Date fechaHora;
    private String mensaje;
    private String tipo; //
    private boolean leido;

    public Mensaje(String emisor, String receptor, String mensaje, String tipo) {
        this.emisor = emisor;
        this.receptor = receptor;
        this.fechaHora = new Date();
        this.mensaje = mensaje;
        this.tipo = tipo;
        this.leido = false;
    }

    public String getEmisor() { return emisor; }

    public String getReceptor() { return receptor; }

    public Date getFechaHora() { return fechaHora; }

    public String getMensaje() { return mensaje; }

    public String getTipo() { return tipo; }

    public boolean isLeido() { return leido; }

    public void setLeido(boolean leido) { this.leido = leido; }
}