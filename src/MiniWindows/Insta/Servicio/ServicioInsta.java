package MiniWindows.Insta.Servicio;

import MiniWindows.Estructuras.ListaEnlazada;
import MiniWindows.Modelo.*;
import MiniWindows.Excepciones.*;

public interface ServicioInsta {

    UsuarioInsta autenticar(String username, String password) throws CuentaDesactivadaException;

    boolean registrarUsuario(UsuarioInsta usuario) throws UsernameDuplicadoException;

    void hacerPost(String username, String descripcion, String rutaImagen, String tipoMobile);

    ListaEnlazada<Publicacion> obtenerTimeline(String username);

    ListaEnlazada<Publicacion> obtenerInteracciones(String username);

    void seguirUsuario(String usuarioOrigen, String usuarioDestino);

    void dejarDeSeguir(String usuarioOrigen, String usuarioDestino);

    ListaEnlazada<UsuarioInsta> buscarPersonas(String criterio);

    ListaEnlazada<Publicacion> buscarHashtag(String hashtag);

    void enviarMensaje(String emisor, String receptor, String contenido, String tipo);

    ListaEnlazada<Mensaje> obtenerConversacion(String usuario1, String usuario2);

    void cambiarEstadoCuenta(String username, boolean activa);
}