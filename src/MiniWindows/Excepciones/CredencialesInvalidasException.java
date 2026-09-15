package MiniWindows.Excepciones;

public class CredencialesInvalidasException extends MiniWindowsException {

    private static final long serialVersionUID = 1L;

    public CredencialesInvalidasException() {
        super("Usuario o contrasena incorrectos");
    }
}
