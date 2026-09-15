package MiniWindows.Excepciones;

public class MiniWindowsException extends Exception {

    private static final long serialVersionUID = 1L;

    public MiniWindowsException(String mensaje) {
        super(mensaje);
    }

    public MiniWindowsException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
