package MiniWindows.Insta.Servicio;

import MiniWindows.Estructuras.ListaEnlazada;
import MiniWindows.Excepciones.CuentaDesactivadaException;
import MiniWindows.Excepciones.MiniWindowsException;
import MiniWindows.Insta.Imagen.Sticker;
import MiniWindows.Modelo.Mensaje;
import MiniWindows.Modelo.Publicacion;
import MiniWindows.Modelo.UsuarioInsta;

public interface ServicioInsta {

    UsuarioInsta autenticar(String username, String clave) throws CuentaDesactivadaException;

    void registrar(UsuarioInsta usuario) throws MiniWindowsException;

    UsuarioInsta perfilDe(String username);

    void actualizarPerfil(UsuarioInsta usuario) throws MiniWindowsException;

    void cambiarEstadoCuenta(String username, boolean activa) throws MiniWindowsException;

    void publicar(Publicacion publicacion) throws MiniWindowsException;

    ListaEnlazada<Publicacion> lineaDeTiempo(String username);

    ListaEnlazada<Publicacion> publicacionesDe(String username);

    ListaEnlazada<Publicacion> menciones(String username);

    void darMeGusta(Publicacion publicacion, String username, boolean marcado) throws MiniWindowsException;

    void seguir(String origen, String destino) throws MiniWindowsException;

    void dejarDeSeguir(String origen, String destino) throws MiniWindowsException;

    boolean sigue(String origen, String destino);

    ListaEnlazada<String> seguidosDe(String username);

    ListaEnlazada<String> seguidoresDe(String username);

    ListaEnlazada<UsuarioInsta> buscarPersonas(String criterio);

    ListaEnlazada<UsuarioInsta> sugerencias(String username, int maximo);

    ListaEnlazada<Publicacion> buscarHashtag(String hashtag);

    void enviarMensaje(Mensaje mensaje) throws MiniWindowsException;

    ListaEnlazada<Mensaje> bandejaDe(String username);

    ListaEnlazada<Mensaje> conversacion(String uno, String otro);

    ListaEnlazada<String> contactosDe(String username);

    int mensajesSinLeer(String username);

    void marcarConversacionLeida(String username, String otro) throws MiniWindowsException;

    void eliminarConversacion(String username, String otro) throws MiniWindowsException;

    ListaEnlazada<Sticker> stickersDe(String username);

    void agregarSticker(String username, String nombreArchivo, byte[] imagen) throws MiniWindowsException;

    ListaEnlazada<String> carpetasPersonalesDe(String username);

    void crearCarpetaPersonal(String username, String nombre) throws MiniWindowsException;
}
