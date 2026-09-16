package MiniWindows.Insta.Logica;

import MiniWindows.Estructuras.ListaEnlazada;
import MiniWindows.Excepciones.MiniWindowsException;
import MiniWindows.Persistencia.GestorBinario;

import java.io.Serializable;
import java.nio.file.Path;

final class AlmacenInsta {

    private AlmacenInsta() {
    }

    static <T extends Serializable> ListaEnlazada<T> leer(Path archivo, Class<T> tipo) {
        try {
            return GestorBinario.cargar(archivo, tipo);
        } catch (MiniWindowsException error) {
            System.err.println("INSTA+ no pudo leer " + archivo.getFileName() + ": " + error.getMessage());
            return new ListaEnlazada<>();
        }
    }

    static <T extends Serializable> void escribir(Path archivo, ListaEnlazada<T> registros)
            throws MiniWindowsException {
        GestorBinario.guardar(archivo, registros);
    }
}
