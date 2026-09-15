package MiniWindows.Insta.Servicio;

import MiniWindows.Estructuras.ListaEnlazada;
import MiniWindows.Modelo.*;
import MiniWindows.Excepciones.*;
import MiniWindows.Insta.Logica.*;

public class ServicioLocal implements ServicioInsta {
    private GestorUsuariosInsta gestorUsuarios = new GestorUsuariosInsta();
    private GestorRelaciones gestorRelaciones = new GestorRelaciones();
    private GestorTimeline gestorTimeline = new GestorTimeline();
    private GestorInbox gestorInbox = new GestorInbox();

    @Override
    public UsuarioInsta autenticar(String username, String password) throws CuentaDesactivadaException {
        return gestorUsuarios.autenticar(username, password);
    }

    @Override
    public boolean registrarUsuario(UsuarioInsta usuario) throws UsernameDuplicadoException {
        return gestorUsuarios.registrar(usuario);
    }

    @Override
    public void publicarInsta(String username, String descripcion, String rutaImagen, String tipoMobile) {
        gestorTimeline.publicar(username, descripcion, rutaImagen, tipoMobile);
    }

    @Override
    public ListaEnlazada<Publicacion> obtenerTimeline(String username) {
        return gestorTimeline.obtenerTimelineUsuario(username);
    }

    @Override
    public ListaEnlazada<Publicacion> obtenerInteracciones(String username) {
        return new ListaEnlazada<>();
    }

    @Override
    public void seguirUsuario(String usuarioOrigen, String usuarioDestino) {
        gestorRelaciones.seguir(usuarioOrigen, usuarioDestino);
    }

    @Override
    public void dejarDeSeguir(String usuarioOrigen, String usuarioDestino) {
        gestorRelaciones.dejarDeSeguir(usuarioOrigen, usuarioDestino);
    }

    @Override
    public ListaEnlazada<UsuarioInsta> buscarPersonas(String criterio) {
        return new ListaEnlazada<>();
    }

    @Override
    public ListaEnlazada<Publicacion> buscarHashtag(String hashtag) {
        return new ListaEnlazada<>();
    }

    @Override
    public void enviarMensajeInbox(String emisor, String receptor, String contenido, String tipo) {
        gestorInbox.enviarMensaje(emisor, receptor, contenido, tipo);
    }

    @Override
    public ListaEnlazada<MensajeInbox> obtenerConversacion(String usuario1, String usuario2) {
        return gestorInbox.obtenerConversacion(usuario1, usuario2);
    }

    @Override
    public void cambiarEstadoCuenta(String username, boolean activa) {}
}