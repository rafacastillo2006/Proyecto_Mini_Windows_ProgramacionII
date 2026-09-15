package MiniWindows.Insta.Logica;

import MiniWindows.Estructuras.ListaEnlazada;
import MiniWindows.Modelo.Publicacion;
import MiniWindows.Persistencia.GestorBinario;
import MiniWindows.Util.Rutas;

public class GestorLineaTiempo {

    public void publicar(String username, String descripcion, String rutaImagen, String modoMobile) {
        GestorBinario<Publicacion> gb = new GestorBinario<>(Rutas.getRutaPosts(username));
        ListaEnlazada<Publicacion> posts = gb.leerLista();

        Publicacion nueva = new Publicacion(username, descripcion, rutaImagen, modoMobile);
        posts.agregar(nueva);
        gb.guardarLista(posts);
    }

    public ListaEnlazada<Publicacion> obtenerTimelineUsuario(String username) {
        GestorBinario<Publicacion> gb = new GestorBinario<>(Rutas.getRutaPosts(username));
        return gb.leerLista();
    }
}