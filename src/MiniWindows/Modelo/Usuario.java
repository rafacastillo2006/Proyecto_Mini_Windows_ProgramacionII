package MiniWindows.Modelo;

import java.io.Serializable;

public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;
    private String username;
    private String password;
    private boolean esAdmin;

    public Usuario(String username, String password, boolean esAdmin) {
        this.username = username;
        this.password = password;
        this.esAdmin = esAdmin;
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public boolean esAdmin() { return esAdmin; }
}
