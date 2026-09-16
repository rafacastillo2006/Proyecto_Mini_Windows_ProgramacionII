package MiniWindows.Modelo;

import java.io.Serializable;
import java.time.LocalDate;

public class UsuarioInsta implements Serializable {

    private static final long serialVersionUID = 2L;

    private final String username;
    private String nombreCompleto;
    private char genero;
    private String clave;
    private int edad;
    private String biografia;
    private byte[] foto;
    private boolean verificada;
    private boolean activa;
    private final LocalDate fechaRegistro;

    public UsuarioInsta(String nombreCompleto, char genero, String username, String clave,
                        int edad, byte[] foto) {
        this.nombreCompleto = nombreCompleto;
        this.genero = genero;
        this.username = username;
        this.clave = clave;
        this.edad = edad;
        this.foto = foto;
        this.biografia = "";
        this.verificada = false;
        this.activa = true;
        this.fechaRegistro = LocalDate.now();
    }

    public String getUsername() {
        return username;
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

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getBiografia() {
        return biografia == null ? "" : biografia;
    }

    public void setBiografia(String biografia) {
        this.biografia = biografia;
    }

    public byte[] getFoto() {
        return foto;
    }

    public void setFoto(byte[] foto) {
        this.foto = foto;
    }

    public boolean esVerificada() {
        return verificada;
    }

    public void setVerificada(boolean verificada) {
        this.verificada = verificada;
    }

    public boolean estaActiva() {
        return activa;
    }

    public void setActiva(boolean activa) {
        this.activa = activa;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    @Override
    public String toString() {
        return "@" + username;
    }
}
