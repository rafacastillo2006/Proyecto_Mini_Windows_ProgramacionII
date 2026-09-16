package MiniWindows.Red;

import java.io.Serializable;

public record RespuestaInsta(Object valor, String error) implements Serializable {

    public static RespuestaInsta ok(Object valor) {
        return new RespuestaInsta(valor, null);
    }

    public static RespuestaInsta fallo(String mensaje) {
        return new RespuestaInsta(null, mensaje);
    }

    public boolean tieneError() {
        return error != null;
    }
}
