package MiniWindows.Persistencia;

import MiniWindows.Estructuras.ListaEnlazada;
import MiniWindows.Excepciones.ArchivoCorruptoException;
import MiniWindows.Excepciones.OperacionArchivoException;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.locks.ReentrantLock;

public final class GestorBinario {

    private static final int FIRMA = 0x4D575245;
    private static final int VERSION = 1;

    private GestorBinario() {
    }

    public static <T extends Serializable> void guardar(Path archivo, ListaEnlazada<T> registros)
            throws OperacionArchivoException {
        ReentrantLock cerrojo = Cerrojos.de(archivo);
        cerrojo.lock();
        try {
            Files.createDirectories(archivo.getParent());
            Path temporal = archivo.resolveSibling(archivo.getFileName() + ".tmp");
            try (ObjectOutputStream salida = new ObjectOutputStream(
                    new BufferedOutputStream(Files.newOutputStream(temporal)))) {
                salida.writeInt(FIRMA);
                salida.writeInt(VERSION);
                salida.writeInt(registros.tamano());
                for (T registro : registros) {
                    salida.writeObject(registro);
                }
            }
            Files.move(temporal, archivo, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException error) {
            try {
                Files.deleteIfExists(archivo.resolveSibling(archivo.getFileName() + ".tmp"));
            } catch (IOException ignorado) {
            }
            throw new OperacionArchivoException("No se pudo guardar " + archivo.getFileName(), error);
        } finally {
            cerrojo.unlock();
        }
    }

    public static <T extends Serializable> ListaEnlazada<T> cargar(Path archivo, Class<T> tipo)
            throws ArchivoCorruptoException {
        ListaEnlazada<T> registros = new ListaEnlazada<>();
        if (!Files.exists(archivo)) {
            return registros;
        }
        ReentrantLock cerrojo = Cerrojos.de(archivo);
        cerrojo.lock();
        try (ObjectInputStream entrada = new ObjectInputStream(
                new BufferedInputStream(Files.newInputStream(archivo)))) {
            if (entrada.readInt() != FIRMA || entrada.readInt() != VERSION) {
                throw new ArchivoCorruptoException(archivo.getFileName().toString());
            }
            int cantidad = entrada.readInt();
            for (int posicion = 0; posicion < cantidad; posicion++) {
                registros.agregar(tipo.cast(entrada.readObject()));
            }
            return registros;
        } catch (IOException | ClassNotFoundException | ClassCastException error) {
            throw new ArchivoCorruptoException(archivo.getFileName().toString(), error);
        } finally {
            cerrojo.unlock();
        }
    }
}
