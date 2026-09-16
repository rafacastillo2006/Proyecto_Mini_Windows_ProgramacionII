package MiniWindows.Insta.Servicio;

import MiniWindows.Estructuras.ListaEnlazada;
import MiniWindows.Excepciones.CuentaDesactivadaException;
import MiniWindows.Excepciones.MiniWindowsException;
import MiniWindows.Insta.Imagen.Sticker;
import MiniWindows.Modelo.Mensaje;
import MiniWindows.Modelo.Publicacion;
import MiniWindows.Modelo.UsuarioInsta;
import MiniWindows.Red.Cliente.ClienteInsta;
import MiniWindows.Red.RespuestaInsta;

public class ServicioRemoto implements ServicioInsta {

    public static final String HOST_LOCAL = "localhost";

    private final String host;
    private final int puerto;
    private final ClienteInsta cliente;

    public ServicioRemoto(String host, int puerto) {
        this.host = host;
        this.puerto = puerto;
        this.cliente = new ClienteInsta(host, puerto);
    }

    public static ServicioRemoto siHayServidor() {
        return siHayServidor(HOST_LOCAL, ClienteInsta.PUERTO_POR_DEFECTO);
    }

    public static ServicioRemoto siHayServidor(String host, int puerto) {
        ServicioRemoto remoto = new ServicioRemoto(host, puerto);
        return remoto.hayConexion() ? remoto : null;
    }

    public boolean hayConexion() {
        return Boolean.TRUE.equals(cliente.pedir(Boolean.class, "PING"));
    }

    public String getHost() {
        return host;
    }

    public int getPuerto() {
        return puerto;
    }

    @Override
    public UsuarioInsta autenticar(String username, String clave) throws CuentaDesactivadaException {
        RespuestaInsta respuesta = cliente.llamar("LOGIN", username, clave);
        if (respuesta.tieneError() && respuesta.error().contains("desactivada")) {
            throw new CuentaDesactivadaException(respuesta.error());
        }
        return respuesta.valor() instanceof UsuarioInsta usuario ? usuario : null;
    }

    @Override
    public void registrar(UsuarioInsta usuario) throws MiniWindowsException {
        cliente.enviar("REGISTRAR", usuario);
    }

    @Override
    public UsuarioInsta perfilDe(String username) {
        return cliente.pedir(UsuarioInsta.class, "PERFIL", username);
    }

    @Override
    public void actualizarPerfil(UsuarioInsta usuario) throws MiniWindowsException {
        cliente.enviar("ACTUALIZAR_PERFIL", usuario);
    }

    @Override
    public void cambiarEstadoCuenta(String username, boolean activa) throws MiniWindowsException {
        cliente.enviar("ESTADO_CUENTA", username, activa);
    }

    @Override
    public void publicar(Publicacion publicacion) throws MiniWindowsException {
        cliente.enviar("PUBLICAR", publicacion);
    }

    @Override
    public ListaEnlazada<Publicacion> lineaDeTiempo(String username) {
        return cliente.pedirLista("LINEA_TIEMPO", username);
    }

    @Override
    public ListaEnlazada<Publicacion> publicacionesDe(String username) {
        return cliente.pedirLista("PUBLICACIONES", username);
    }

    @Override
    public ListaEnlazada<Publicacion> menciones(String username) {
        return cliente.pedirLista("MENCIONES", username);
    }

    @Override
    public void darMeGusta(Publicacion publicacion, String username, boolean marcado)
            throws MiniWindowsException {
        cliente.enviar("ME_GUSTA", publicacion, username, marcado);
    }

    @Override
    public void seguir(String origen, String destino) throws MiniWindowsException {
        cliente.enviar("SEGUIR", origen, destino);
    }

    @Override
    public void dejarDeSeguir(String origen, String destino) throws MiniWindowsException {
        cliente.enviar("DEJAR_DE_SEGUIR", origen, destino);
    }

    @Override
    public boolean sigue(String origen, String destino) {
        Boolean respuesta = cliente.pedir(Boolean.class, "SIGUE", origen, destino);
        return respuesta != null && respuesta;
    }

    @Override
    public ListaEnlazada<String> seguidosDe(String username) {
        return cliente.pedirLista("SEGUIDOS", username);
    }

    @Override
    public ListaEnlazada<String> seguidoresDe(String username) {
        return cliente.pedirLista("SEGUIDORES", username);
    }

    @Override
    public ListaEnlazada<UsuarioInsta> buscarPersonas(String criterio) {
        return cliente.pedirLista("BUSCAR_PERSONAS", criterio);
    }

    @Override
    public ListaEnlazada<UsuarioInsta> sugerencias(String username, int maximo) {
        return cliente.pedirLista("SUGERENCIAS", username, maximo);
    }

    @Override
    public ListaEnlazada<Publicacion> buscarHashtag(String hashtag) {
        return cliente.pedirLista("BUSCAR_HASHTAG", hashtag);
    }

    @Override
    public void enviarMensaje(Mensaje mensaje) throws MiniWindowsException {
        cliente.enviar("ENVIAR_MENSAJE", mensaje);
    }

    @Override
    public ListaEnlazada<Mensaje> bandejaDe(String username) {
        return cliente.pedirLista("BANDEJA", username);
    }

    @Override
    public ListaEnlazada<Mensaje> conversacion(String uno, String otro) {
        return cliente.pedirLista("CONVERSACION", uno, otro);
    }

    @Override
    public ListaEnlazada<String> contactosDe(String username) {
        return cliente.pedirLista("CONTACTOS", username);
    }

    @Override
    public int mensajesSinLeer(String username) {
        Integer respuesta = cliente.pedir(Integer.class, "SIN_LEER", username);
        return respuesta == null ? 0 : respuesta;
    }

    @Override
    public void marcarConversacionLeida(String username, String otro) throws MiniWindowsException {
        cliente.enviar("MARCAR_LEIDA", username, otro);
    }

    @Override
    public void eliminarConversacion(String username, String otro) throws MiniWindowsException {
        cliente.enviar("ELIMINAR_CONVERSACION", username, otro);
    }

    @Override
    public ListaEnlazada<Sticker> stickersDe(String username) {
        return cliente.pedirLista("STICKERS", username);
    }

    @Override
    public void agregarSticker(String username, String nombreArchivo, byte[] imagen)
            throws MiniWindowsException {
        cliente.enviar("AGREGAR_STICKER", username, nombreArchivo, imagen);
    }

    @Override
    public ListaEnlazada<String> carpetasPersonalesDe(String username) {
        return cliente.pedirLista("CARPETAS", username);
    }

    @Override
    public void crearCarpetaPersonal(String username, String nombre) throws MiniWindowsException {
        cliente.enviar("CREAR_CARPETA", username, nombre);
    }
}
