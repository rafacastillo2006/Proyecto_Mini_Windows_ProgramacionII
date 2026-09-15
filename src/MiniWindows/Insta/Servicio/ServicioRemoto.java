package MiniWindows.Insta.Servicio;

import MiniWindows.Estructuras.ListaEnlazada;
import MiniWindows.Modelo.*;
import MiniWindows.Excepciones.*;
import MiniWindows.Red.Cliente.ClienteInsta;

public class ServicioRemoto implements ServicioInsta {
    private ClienteInsta clienteRed;

    public ServicioRemoto(String host, int puerto) {
        this.clienteRed = new ClienteInsta(host, puerto);
    }

    @Override
    public UsuarioInsta autenticar(String username, String password) throws CuentaDesactivadaException {
        return (UsuarioInsta) clienteRed.enviarComando("LOGIN", username, password);
    }

    @Override
    public boolean registrarUsuario(UsuarioInsta usuario) throws UsernameDuplicadoException {
        return (boolean) clienteRed.enviarComando("REGISTRAR", usuario);
    }

    @Override
    public void hacerPost(String username, String descripcion, String rutaImagen, String tipoMobile) {
        clienteRed.enviarComando("PUBLICAR", username, descripcion, rutaImagen, tipoMobile);
    }

    @Override
    public ListaEnlazada<Publicacion> obtenerTimeline(String username) {
        return (ListaEnlazada<Publicacion>) clienteRed.enviarComando("GET_TIMELINE", username);
    }

    @Override
    public ListaEnlazada<Publicacion> obtenerInteracciones(String username) {
        return (ListaEnlazada<Publicacion>) clienteRed.enviarComando("GET_INTERACCIONES", username);
    }

    @Override
    public void seguirUsuario(String usuarioOrigen, String usuarioDestino) {
        clienteRed.enviarComando("SEGUIR", usuarioOrigen, usuarioDestino);
    }

    @Override
    public void dejarDeSeguir(String usuarioOrigen, String usuarioDestino) {
        clienteRed.enviarComando("UNFOLLOW", usuarioOrigen, usuarioDestino);
    }

    @Override
    public ListaEnlazada<UsuarioInsta> buscarPersonas(String criterio) {
        return (ListaEnlazada<UsuarioInsta>) clienteRed.enviarComando("BUSCAR_PERSONAS", criterio);
    }

    @Override
    public ListaEnlazada<Publicacion> buscarHashtag(String hashtag) {
        return (ListaEnlazada<Publicacion>) clienteRed.enviarComando("BUSCAR_HASHTAG", hashtag);
    }

    @Override
    public void enviarMensaje(String emisor, String receptor, String contenido, String tipo) {
        clienteRed.enviarComando("ENVIAR_MENSAJE", emisor, receptor, contenido, tipo);
    }

    @Override
    public ListaEnlazada<Mensaje> obtenerConversacion(String usuario1, String usuario2) {
        return (ListaEnlazada<Mensaje>) clienteRed.enviarComando("GET_CHAT", usuario1, usuario2);
    }

    @Override
    public void cambiarEstadoCuenta(String username, boolean activa) {
        clienteRed.enviarComando("ESTADO_CUENTA", username, activa);
    }
}