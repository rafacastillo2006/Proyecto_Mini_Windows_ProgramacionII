package MiniWindows.Insta.Logica;

import MiniWindows.Estructuras.ListaEnlazada;
import MiniWindows.Excepciones.MiniWindowsException;
import MiniWindows.Modelo.Publicacion;
import MiniWindows.Modelo.UsuarioInsta;
import MiniWindows.Util.Rutas;

import java.util.Comparator;
import java.util.Locale;

public class GestorLineaTiempo {

    private static final Comparator<Publicacion> MAS_RECIENTE =
            Comparator.comparing(Publicacion::getFecha).reversed();

    private final GestorUsuariosIG usuarios;
    private final GestorRelacionesUsuarios relaciones;

    public GestorLineaTiempo(GestorUsuariosIG usuarios, GestorRelacionesUsuarios relaciones) {
        this.usuarios = usuarios;
        this.relaciones = relaciones;
    }

    public void publicar(Publicacion publicacion) throws MiniWindowsException {
        ListaEnlazada<Publicacion> propias = publicacionesDe(publicacion.getAutor());
        propias.agregar(publicacion);
        AlmacenInsta.escribir(Rutas.getInsta(publicacion.getAutor()), propias);
    }

    public ListaEnlazada<Publicacion> publicacionesDe(String username) {
        return AlmacenInsta.leer(Rutas.getInsta(username), Publicacion.class);
    }

    public ListaEnlazada<Publicacion> visiblesDe(String username) {
        UsuarioInsta autor = usuarios.buscarPorUsername(username);
        return autor != null && autor.estaActiva() ? publicacionesDe(username) : new ListaEnlazada<>();
    }

    public ListaEnlazada<Publicacion> lineaDeTiempo(String username) {
        ListaEnlazada<Publicacion> feed = publicacionesDe(username);
        for (String seguido : relaciones.seguidosDe(username)) {
            feed.agregarTodos(visiblesDe(seguido));
        }
        feed.ordenar(MAS_RECIENTE);
        return feed;
    }

    public ListaEnlazada<Publicacion> menciones(String username) {
        ListaEnlazada<Publicacion> encontradas = new ListaEnlazada<>();
        for (UsuarioInsta usuario : usuarios.activos()) {
            if (usuario.getUsername().equalsIgnoreCase(username)) {
                continue;
            }
            for (Publicacion publicacion : publicacionesDe(usuario.getUsername())) {
                if (publicacion.menciona(username) && !yaEsta(encontradas, publicacion)) {
                    encontradas.agregar(publicacion);
                }
            }
        }
        encontradas.ordenar(MAS_RECIENTE);
        return encontradas;
    }

    public ListaEnlazada<Publicacion> porHashtag(String hashtag) {
        String etiqueta = normalizar(hashtag);
        ListaEnlazada<Publicacion> encontradas = new ListaEnlazada<>();
        if (etiqueta.isEmpty()) {
            return encontradas;
        }
        for (UsuarioInsta usuario : usuarios.activos()) {
            for (Publicacion publicacion : publicacionesDe(usuario.getUsername())) {
                if (publicacion.tieneHashtag(etiqueta) && !yaEsta(encontradas, publicacion)) {
                    encontradas.agregar(publicacion);
                }
            }
        }
        encontradas.ordenar(MAS_RECIENTE);
        return encontradas;
    }

    public void darMeGusta(Publicacion publicacion, boolean marcado) throws MiniWindowsException {
        ListaEnlazada<Publicacion> propias = publicacionesDe(publicacion.getAutor());
        for (Publicacion guardada : propias) {
            if (guardada.getFecha().equals(publicacion.getFecha())) {
                guardada.setMeGusta(guardada.getMeGusta() + (marcado ? 1 : -1));
                publicacion.setMeGusta(guardada.getMeGusta());
                AlmacenInsta.escribir(Rutas.getInsta(publicacion.getAutor()), propias);
                return;
            }
        }
    }

    private boolean yaEsta(ListaEnlazada<Publicacion> lista, Publicacion candidata) {
        return lista.buscar(publicacion -> publicacion.getAutor().equalsIgnoreCase(candidata.getAutor())
                && publicacion.getFecha().equals(candidata.getFecha())) != null;
    }

    private String normalizar(String hashtag) {
        String texto = hashtag == null ? "" : hashtag.trim().toLowerCase(Locale.ROOT);
        return texto.startsWith("#") ? texto.substring(1) : texto;
    }
}
