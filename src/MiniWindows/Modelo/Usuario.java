package MiniWindows.Modelo;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    private String nombreCompleto;
    private char genero;
    private final String username;
    private String hashContrasena;
    private final LocalDateTime fechaRegistro;
    private int edad;
    private boolean activa;
    private Rol rol;
    private String rutaFotoPerfil;

    public Usuario(String nombreCompleto, char genero, String username, String hashContrasena,
                   int edad, Rol rol, LocalDateTime fechaRegistro) {
        this.nombreCompleto = nombreCompleto;
        this.genero = genero;
        this.username = username;
        this.hashContrasena = hashContrasena;
        this.edad = edad;
        this.rol = rol;
        this.fechaRegistro = fechaRegistro;
        this.activa = true;
        this.rutaFotoPerfil = "";
    }

    public boolean esAdministrador() {
        return rol == Rol.ADMINISTRADOR;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public char getGenero() {
        return genero;
    }

    public void setGenero(char genero) {
        this.genero = genero;
    }

    public String getUsername() {
        return username;
    }

    public String getHashContrasena() {
        return hashContrasena;
    }

    public void setHashContrasena(String hashContrasena) {
        this.hashContrasena = hashContrasena;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public boolean estaActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public String getRutaFotoPerfil() {
        return rutaFotoPerfil;
    }

    public void setRutaFotoPerfil(String rutaFotoPerfil) {
        this.rutaFotoPerfil = rutaFotoPerfil;
    }

    @Override
    public String toString() {
        return username;
    }
}
