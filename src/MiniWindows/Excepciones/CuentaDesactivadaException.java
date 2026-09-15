package MiniWindows.Excepciones;

public class CuentaDesactivadaException extends Exception {
    public CuentaDesactivadaException(String mensaje) {
        super(mensaje);
    }
}
