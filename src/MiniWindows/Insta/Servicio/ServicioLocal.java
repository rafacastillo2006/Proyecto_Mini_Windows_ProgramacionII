package MiniWindows.Insta.Servicio;

import MiniWindows.Estructuras.ListaEnlazada;
import MiniWindows.Modelo.*;
import MiniWindows.Excepciones.*;
import MiniWindows.Insta.Logica.*;

public class ServicioLocal implements ServicioInsta {
    private GestorUsuariosIG gestorUsuarios = new GestorUsuariosIG();
    private GestorRelacionesUsuarios gestorRelaciones = new GestorRelacionesUsuarios();
    private GestorLineaTiempo gestorLineaTiempo = new GestorLineaTiempo();
    private GestorBandejaEntrada gestorBandejaEntrada = new GestorBandejaEntrada();

    @Override
    public UsuarioInsta autenticar(String username, String password) throws CuentaDesactivadaException {
        return gestorUsuarios.autenticar(username, password);
    }

    @Override
    public boolean registrarUsuario(UsuarioInsta usuario) throws UsernameDuplicadoException {
        return gestorUsuarios.registrar(usuario);
    }

    @Override
    public void hacerPost(String username, String descripcion, String rutaImagen, String tipoMobile) {
        gestorLineaTiempo.publicar(username, descripcion, rutaImagen, tipoMobile);
    }

    @Override
    public ListaEnlazada<Publicacion> obtenerTimeline(String username) {
        return gestorLineaTiempo.obtenerTimelineUsuario(username);
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
    public void enviarMensaje(String emisor, String receptor, String contenido, String tipo) {
        gestorBandejaEntrada.enviarMensaje(emisor, receptor, contenido, tipo);
    }

    @Override
    public ListaEnlazada<Mensaje> obtenerConversacion(String usuario1, String usuario2) {
        return gestorBandejaEntrada.obtenerConversacion(usuario1, usuario2);
    }

    @Override
    public void cambiarEstadoCuenta(String username, boolean activa) {}
}