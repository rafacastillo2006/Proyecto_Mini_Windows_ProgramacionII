package MiniWindows.Insta.Imagen;

import MiniWindows.Estructuras.ListaEnlazada;
import MiniWindows.Excepciones.MiniWindowsException;
import MiniWindows.Excepciones.OperacionArchivoException;
import MiniWindows.Persistencia.GestorBinario;
import MiniWindows.Util.Rutas;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class GestorStickers {

    public static final String[] NOMBRES_POR_DEFECTO = {"Feliz", "Triste", "Corazon", "Risa", "Aplauso"};
    private static final int LADO = 200;

    public ListaEnlazada<Sticker> obtener(String username) {
        ListaEnlazada<Sticker> guardados = leer(username);
        if (guardados.estaVacia()) {
            guardados = porDefecto();
            try {
                GestorBinario.guardar(Rutas.getStickers(username), guardados);
            } catch (MiniWindowsException error) {
                System.err.println("No se pudieron registrar los stickers: " + error.getMessage());
            }
        }
        return guardados;
    }

    public void agregarPersonal(String username, String nombreArchivo, byte[] imagen)
            throws MiniWindowsException {
        if (!esFormatoPermitido(nombreArchivo)) {
            throw new OperacionArchivoException("El sticker debe ser un archivo .png o .jpg");
        }
        byte[] ajustado = ProcesadorImagen.ajustar(imagen, LADO);
        if (ajustado == null) {
            throw new OperacionArchivoException("Esa imagen no se pudo leer");
        }
        String nombre = nombreArchivo.replaceAll("\\.[^.]+$", "");
        ListaEnlazada<Sticker> stickers = obtener(username);
        stickers.agregar(new Sticker(nombre, ajustado));
        GestorBinario.guardar(Rutas.getStickers(username), stickers);
        guardarCopia(Rutas.getStickersPersonalesDe(username).resolve(nombre + ".png"), ajustado);
    }

    public static boolean esFormatoPermitido(String nombreArchivo) {
        if (nombreArchivo == null) {
            return false;
        }
        String nombre = nombreArchivo.toLowerCase();
        return nombre.endsWith(".png") || nombre.endsWith(".jpg") || nombre.endsWith(".jpeg");
    }

    private ListaEnlazada<Sticker> porDefecto() {
        ListaEnlazada<Sticker> stickers = new ListaEnlazada<>();
        for (String nombre : NOMBRES_POR_DEFECTO) {
            byte[] dibujo = ArteGenerado.sticker(nombre);
            stickers.agregar(new Sticker(nombre, dibujo));
            guardarCopia(Rutas.getStickersGlobales().resolve(nombre.toLowerCase() + ".png"), dibujo);
        }
        return stickers;
    }

    private ListaEnlazada<Sticker> leer(String username) {
        try {
            return GestorBinario.cargar(Rutas.getStickers(username), Sticker.class);
        } catch (MiniWindowsException error) {
            return new ListaEnlazada<>();
        }
    }

    private void guardarCopia(Path destino, byte[] datos) {
        try {
            if (!Files.exists(destino)) {
                Files.createDirectories(destino.getParent());
                Files.write(destino, datos);
            }
        } catch (IOException error) {
            System.err.println("No se pudo copiar el sticker: " + error.getMessage());
        }
    }
}
