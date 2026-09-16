package MiniWindows.Red;

import MiniWindows.Modelo.Mensaje;

import java.io.Serializable;

public record EventoInsta(String tipo, String actor, String objetivo, String referencia, Mensaje mensaje)
        implements Serializable {

    public static final String MENSAJE = "MENSAJE";
    public static final String ME_GUSTA = "ME_GUSTA";
    public static final String SEGUIR = "SEGUIR";
    public static final String DEJAR_DE_SEGUIR = "DEJAR_DE_SEGUIR";
    public static final String PUBLICACION = "PUBLICACION";

    public static EventoInsta deMensaje(Mensaje mensaje) {
        return new EventoInsta(MENSAJE, mensaje.getEmisor(), mensaje.getReceptor(), "", mensaje);
    }

    public static EventoInsta meGusta(String actor, String autor, String referencia) {
        return new EventoInsta(ME_GUSTA, actor, autor, referencia, null);
    }

    public static EventoInsta seguimiento(String tipo, String actor, String objetivo) {
        return new EventoInsta(tipo, actor, objetivo, "", null);
    }

    public static EventoInsta publicacion(String actor, String referencia) {
        return new EventoInsta(PUBLICACION, actor, referencia, referencia, null);
    }

    public boolean es(String otroTipo) {
        return tipo.equals(otroTipo);
    }

    public boolean tocaA(String username) {
        return username != null && (username.equalsIgnoreCase(actor) || username.equalsIgnoreCase(objetivo));
    }
}
