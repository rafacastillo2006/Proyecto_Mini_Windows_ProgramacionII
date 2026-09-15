package MiniWindows.Modelo;

import java.io.Serializable;
import java.time.LocalDate;

public class UsuarioInsta implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nombreCompleto;
    private char genero;
    private String username;
    private String password;
    private LocalDate fechaRegistro;
    private int edad;
    private boolean activa;
    private String fotoPerfil;

    public UsuarioInsta(String nombreCompleto, char genero, String username, String password, int edad, String fotoPerfil) {
        this.nombreCompleto = nombreCompleto;
        this.genero = genero;
        this.username = username;
        this.password = password;
        this.edad = edad;
        this.fotoPerfil = fotoPerfil;
        this.fechaRegistro = LocalDate.now(); // Se toma automáticamente del sistema
        this.activa = true; // Activa por defecto
    }

    // Getters y Setters
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public char getGenero() { return genero; }
    public void setGenero(char genero) { this.genero = genero; }

    public String getUsername() { return username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public LocalDate getFechaRegistro() { return fechaRegistro; }

    public int getEdad() { return edad; }
    public void setEdad(int edad) { this.edad = edad; }

    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }

    public String getFotoPerfil() { return fotoPerfil; }
    public void setFotoPerfil(String fotoPerfil) { this.fotoPerfil = fotoPerfil; }
}