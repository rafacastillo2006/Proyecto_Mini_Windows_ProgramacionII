package MiniWindows.Excepciones;

public class CuentaDesactivadaException extends MiniWindowsException {

    private static final long serialVersionUID = 1L;

    private final String username;

    public CuentaDesactivadaException(String username) {
        super("La cuenta \"" + username + "\" esta desactivada");
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
