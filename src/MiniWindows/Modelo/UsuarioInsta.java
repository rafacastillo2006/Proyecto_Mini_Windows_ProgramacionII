package MiniWindows.Modelo;

import java.io.Serializable;
import java.util.Date;

public class UsuarioInsta implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nombreCompleto;
    private char genero;
    private String username;
    private String password;
    private Date fechaRegistro;
    private int edad;
    private boolean activa;
    private String rutaFotoPerfil;

    public UsuarioInsta(String nombreCompleto, char genero, String username, String password, int edad, String rutaFotoPerfil) {
        this.nombreCompleto = nombreCompleto;
        this.genero = genero;
        this.username = username;
        this.password = password;
        this.fechaRegistro = new Date();
        this.edad = edad;
        this.activa = true;
        this.rutaFotoPerfil = rutaFotoPerfil;
    }

    public String getNombreCompleto() { return nombreCompleto; }

    public char getGenero() { return genero; }

    public String getUsername() { return username; }

    public String getPassword() { return password; }

    public Date getFechaRegistro() { return fechaRegistro; }

    public int getEdad() { return edad; }

    public boolean isActiva() { return activa; }

    public void setActiva(boolean activa) { this.activa = activa; }

    public String getRutaFotoPerfil() { return rutaFotoPerfil; }
}