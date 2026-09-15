package MiniWindows.Excepciones;

public class PermisoDenegadoException extends MiniWindowsException {

    private static final long serialVersionUID = 1L;

    public PermisoDenegadoException(String mensaje) {
        super(mensaje);
    }
}
