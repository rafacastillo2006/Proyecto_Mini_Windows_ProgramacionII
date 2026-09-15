package MiniWindows.Excepciones;

public class UsernameDuplicadoException extends MiniWindowsException {

    private static final long serialVersionUID = 1L;

    private final String username;

    public UsernameDuplicadoException(String username) {
        super("El usuario \"" + username + "\" ya existe en el sistema");
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
