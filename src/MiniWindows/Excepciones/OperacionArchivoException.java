package MiniWindows.Excepciones;

public class OperacionArchivoException extends MiniWindowsException {

    private static final long serialVersionUID = 1L;

    public OperacionArchivoException(String mensaje) {
        super(mensaje);
    }

    public OperacionArchivoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
