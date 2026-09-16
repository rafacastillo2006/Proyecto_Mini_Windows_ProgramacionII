package MiniWindows.Insta.Servicio;

import MiniWindows.Estructuras.ListaEnlazada;
import MiniWindows.Excepciones.CuentaDesactivadaException;
import MiniWindows.Excepciones.MiniWindowsException;
import MiniWindows.Excepciones.OperacionArchivoException;
import MiniWindows.Insta.Imagen.GestorStickers;
import MiniWindows.Insta.Imagen.Sticker;
import MiniWindows.Insta.Logica.GestorBandejaEntrada;
import MiniWindows.Insta.Logica.GestorLineaTiempo;
import MiniWindows.Insta.Logica.GestorRelacionesUsuarios;
import MiniWindows.Insta.Logica.GestorUsuariosIG;
import MiniWindows.Modelo.Mensaje;
import MiniWindows.Modelo.Publicacion;
import MiniWindows.Modelo.UsuarioInsta;
import MiniWindows.Util.Rutas;
import MiniWindows.Util.Validador;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class ServicioLocal implements ServicioInsta {

    private final GestorUsuariosIG usuarios = new GestorUsuariosIG();
    private final GestorRelacionesUsuarios relaciones = new GestorRelacionesUsuarios();
    private final GestorLineaTiempo lineaTiempo = new GestorLineaTiempo(usuarios, relaciones);
    private final GestorBandejaEntrada bandeja = new GestorBandejaEntrada();
    private final GestorStickers stickers = new GestorStickers();

    @Override
    public UsuarioInsta autenticar(String username, String clave) throws CuentaDesactivadaException {
        return usuarios.autenticar(username, clave);
    }

    @Override
    public void registrar(UsuarioInsta usuario) throws MiniWindowsException {
        usuarios.registrar(usuario);
    }

    @Override
    public UsuarioInsta perfilDe(String username) {
        return usuarios.buscarPorUsername(username);
    }

    @Override
    public void actualizarPerfil(UsuarioInsta usuario) throws MiniWindowsException {
        usuarios.actualizar(usuario);
    }

    @Override
    public void cambiarEstadoCuenta(String username, boolean activa) throws MiniWindowsException {
        usuarios.cambiarEstado(username, activa);
    }

    @Override
    public void publicar(Publicacion publicacion) throws MiniWindowsException {
        if (publicacion.getDescripcion().length() > Publicacion.LIMITE_DESCRIPCION) {
            throw new OperacionArchivoException("La descripcion no puede pasar de "
                    + Publicacion.LIMITE_DESCRIPCION + " caracteres");
        }
        lineaTiempo.publicar(publicacion);
        guardarImagen(publicacion);
    }

    @Override
    public ListaEnlazada<Publicacion> lineaDeTiempo(String username) {
        return lineaTiempo.lineaDeTiempo(username);
    }

    @Override
    public ListaEnlazada<Publicacion> publicacionesDe(String username) {
        return lineaTiempo.publicacionesDe(username);
    }

    @Override
    public ListaEnlazada<Publicacion> menciones(String username) {
        return lineaTiempo.menciones(username);
    }

    @Override
    public void darMeGusta(Publicacion publicacion, boolean marcado) throws MiniWindowsException {
        lineaTiempo.darMeGusta(publicacion, marcado);
    }

    @Override
    public void seguir(String origen, String destino) throws MiniWindowsException {
        relaciones.seguir(origen, destino);
    }

    @Override
    public void dejarDeSeguir(String origen, String destino) throws MiniWindowsException {
        relaciones.dejarDeSeguir(origen, destino);
    }

    @Override
    public boolean sigue(String origen, String destino) {
        return relaciones.sigue(origen, destino);
    }

    @Override
    public ListaEnlazada<String> seguidosDe(String username) {
        return relaciones.seguidosDe(username);
    }

    @Override
    public ListaEnlazada<String> seguidoresDe(String username) {
        return relaciones.seguidoresDe(username);
    }

    @Override
    public ListaEnlazada<UsuarioInsta> buscarPersonas(String criterio) {
        return usuarios.buscar(criterio);
    }

    @Override
    public ListaEnlazada<UsuarioInsta> sugerencias(String username, int maximo) {
        ListaEnlazada<UsuarioInsta> candidatos = usuarios.activos().filtrar(usuario ->
                !usuario.getUsername().equalsIgnoreCase(username)
                        && !relaciones.sigue(username, usuario.getUsername()));
        candidatos.ordenar((uno, otro) -> {
            if (uno.esVerificada() != otro.esVerificada()) {
                return uno.esVerificada() ? -1 : 1;
            }
            return Integer.compare(relaciones.seguidoresDe(otro.getUsername()).tamano(),
                    relaciones.seguidoresDe(uno.getUsername()).tamano());
        });
        ListaEnlazada<UsuarioInsta> recorte = new ListaEnlazada<>();
        for (int i = 0; i < Math.min(maximo, candidatos.tamano()); i++) {
            recorte.agregar(candidatos.obtener(i));
        }
        return recorte;
    }

    @Override
    public ListaEnlazada<Publicacion> buscarHashtag(String hashtag) {
        return lineaTiempo.porHashtag(hashtag);
    }

    @Override
    public void enviarMensaje(Mensaje mensaje) throws MiniWindowsException {
        if (mensaje.getContenido().length() > Mensaje.LIMITE_CARACTERES) {
            throw new OperacionArchivoException("El mensaje no puede pasar de "
                    + Mensaje.LIMITE_CARACTERES + " caracteres");
        }
        bandeja.enviar(mensaje);
    }

    @Override
    public ListaEnlazada<Mensaje> bandejaDe(String username) {
        return bandeja.bandejaDe(username);
    }

    @Override
    public ListaEnlazada<Mensaje> conversacion(String uno, String otro) {
        return bandeja.conversacion(uno, otro);
    }

    @Override
    public ListaEnlazada<String> contactosDe(String username) {
        return bandeja.contactosDe(username);
    }

    @Override
    public int mensajesSinLeer(String username) {
        return bandeja.sinLeerDe(username);
    }

    @Override
    public void marcarConversacionLeida(String username, String otro) throws MiniWindowsException {
        bandeja.marcarConversacionLeida(username, otro);
    }

    @Override
    public void eliminarConversacion(String username, String otro) throws MiniWindowsException {
        bandeja.eliminarConversacion(username, otro);
    }

    @Override
    public ListaEnlazada<Sticker> stickersDe(String username) {
        return stickers.obtener(username);
    }

    @Override
    public void agregarSticker(String username, String nombreArchivo, byte[] imagen)
            throws MiniWindowsException {
        stickers.agregarPersonal(username, nombreArchivo, imagen);
    }

    @Override
    public ListaEnlazada<String> carpetasPersonalesDe(String username) {
        ListaEnlazada<String> carpetas = new ListaEnlazada<>();
        try (Stream<Path> contenido = Files.list(Rutas.getFoldersPersonalesDe(username))) {
            contenido.filter(Files::isDirectory)
                    .map(ruta -> ruta.getFileName().toString())
                    .sorted()
                    .forEach(carpetas::agregar);
        } catch (IOException error) {
            System.err.println("No se pudieron leer las carpetas personales: " + error.getMessage());
        }
        return carpetas;
    }

    @Override
    public void crearCarpetaPersonal(String username, String nombre) throws MiniWindowsException {
        if (!Validador.nombreArchivoValido(nombre)) {
            throw new OperacionArchivoException("Ese nombre de carpeta no es valido");
        }
        try {
            Files.createDirectories(Rutas.getFoldersPersonalesDe(username).resolve(nombre));
        } catch (IOException error) {
            throw new OperacionArchivoException("No se pudo crear la carpeta " + nombre, error);
        }
    }

    private void guardarImagen(Publicacion publicacion) {
        if (!publicacion.tieneImagen()) {
            return;
        }
        try {
            Path destino = publicacion.getCarpetaPersonal().isEmpty()
                    ? Rutas.getImagenesDe(publicacion.getAutor())
                    : Rutas.getFoldersPersonalesDe(publicacion.getAutor())
                            .resolve(publicacion.getCarpetaPersonal());
            Files.createDirectories(destino);
            String nombre = publicacion.getFecha().toString().replaceAll("[^0-9]", "") + ".png";
            Files.write(destino.resolve(nombre), publicacion.getImagen());
        } catch (IOException error) {
            System.err.println("No se pudo copiar la imagen de la publicacion: " + error.getMessage());
        }
    }
}
