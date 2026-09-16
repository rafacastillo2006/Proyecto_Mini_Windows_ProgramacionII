package MiniWindows.Modelo;

import MiniWindows.Estructuras.ListaEnlazada;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Locale;

public class Publicacion implements Serializable {

    private static final long serialVersionUID = 4L;

    public static final int LIMITE_DESCRIPCION = 220;

    private final String autor;
    private final String descripcion;
    private final byte[] imagen;
    private final String formato;
    private final String carpetaPersonal;
    private final LocalDateTime fecha;
    private final ListaEnlazada<String> meGustaDe = new ListaEnlazada<>();

    public Publicacion(String autor, String descripcion, byte[] imagen, String formato) {
        this(autor, descripcion, imagen, formato, "", LocalDateTime.now());
    }

    public Publicacion(String autor, String descripcion, byte[] imagen, String formato,
                       String carpetaPersonal, LocalDateTime fecha) {
        this.autor = autor;
        this.descripcion = descripcion;
        this.imagen = imagen;
        this.formato = formato;
        this.carpetaPersonal = carpetaPersonal;
        this.fecha = fecha;
    }

    public String getAutor() {
        return autor;
    }

    public String getDescripcion() {
        return descripcion == null ? "" : descripcion;
    }

    public byte[] getImagen() {
        return imagen;
    }

    public boolean tieneImagen() {
        return imagen != null && imagen.length > 0;
    }

    public String getFormato() {
        return formato;
    }

    public String getCarpetaPersonal() {
        return carpetaPersonal == null ? "" : carpetaPersonal;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public int getMeGusta() {
        return meGustaDe.tamano();
    }

    public boolean leGustaA(String username) {
        return meGustaDe.buscar(nombre -> nombre.equalsIgnoreCase(username)) != null;
    }

    public void marcarMeGusta(String username, boolean marcado) {
        boolean yaEsta = leGustaA(username);
        if (marcado && !yaEsta) {
            meGustaDe.agregar(username);
        } else if (!marcado && yaEsta) {
            meGustaDe.eliminar(meGustaDe.buscar(nombre -> nombre.equalsIgnoreCase(username)));
        }
    }

    public String clave() {
        return getAutor() + "|" + getFecha();
    }

    public boolean esLaMisma(Publicacion otra) {
        return otra != null && otra.getAutor().equalsIgnoreCase(autor) && otra.getFecha().equals(fecha);
    }

    public ListaEnlazada<String> etiquetas(char marca) {
        ListaEnlazada<String> encontradas = new ListaEnlazada<>();
        for (String palabra : getDescripcion().split("\\s+")) {
            if (palabra.length() > 1 && palabra.charAt(0) == marca) {
                String limpia = palabra.substring(1).replaceAll("[^A-Za-z0-9._]", "").toLowerCase(Locale.ROOT);
                if (!limpia.isEmpty() && encontradas.buscar(limpia::equals) == null) {
                    encontradas.agregar(limpia);
                }
            }
        }
        return encontradas;
    }

    public boolean menciona(String username) {
        return etiquetas('@').buscar(etiqueta -> etiqueta.equalsIgnoreCase(username)) != null;
    }

    public boolean tieneHashtag(String hashtag) {
        return etiquetas('#').buscar(etiqueta -> etiqueta.equalsIgnoreCase(hashtag)) != null;
    }
}
