package MiniWindows.Persistencia;

import MiniWindows.Excepciones.ArchivoCorruptoException;
import MiniWindows.Excepciones.OperacionArchivoException;
import MiniWindows.Modelo.TipoArchivo;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.locks.ReentrantLock;

public final class ArchivoIns {

    public static final String EXTENSION = ".ins";

    private static final int FIRMA = 0x4D57494E;
    private static final int VERSION = 1;

    private ArchivoIns() {
    }

    public static boolean esArchivoIns(Path archivo) {
        return archivo.getFileName().toString().toLowerCase().endsWith(EXTENSION);
    }

    public static void escribir(Path destino, CabeceraIns cabecera, byte[] datos) throws OperacionArchivoException {
        ReentrantLock cerrojo = Cerrojos.de(destino);
        cerrojo.lock();
        try {
            Path temporal = destino.resolveSibling(destino.getFileName() + ".tmp");
            try (DataOutputStream salida = new DataOutputStream(
                    new BufferedOutputStream(Files.newOutputStream(temporal)))) {
                salida.writeInt(FIRMA);
                salida.writeInt(VERSION);
                salida.writeByte(cabecera.tipo().ordinal());
                salida.writeUTF(cabecera.nombreVisible());
                salida.writeLong(cabecera.creado());
                salida.writeLong(cabecera.modificado());
                salida.writeInt(datos.length);
                salida.write(datos);
            }
            Files.move(temporal, destino, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException error) {
            descartarTemporal(destino);
            throw new OperacionArchivoException("No se pudo escribir " + destino.getFileName(), error);
        } finally {
            cerrojo.unlock();
        }
    }

    public static CabeceraIns leerCabecera(Path archivo) throws ArchivoCorruptoException {
        ReentrantLock cerrojo = Cerrojos.de(archivo);
        cerrojo.lock();
        try (DataInputStream entrada = new DataInputStream(
                new BufferedInputStream(Files.newInputStream(archivo)))) {
            return leerCabecera(archivo, entrada);
        } catch (IOException error) {
            throw new ArchivoCorruptoException(archivo.getFileName().toString(), error);
        } finally {
            cerrojo.unlock();
        }
    }

    public static ContenidoIns leer(Path archivo) throws ArchivoCorruptoException {
        ReentrantLock cerrojo = Cerrojos.de(archivo);
        cerrojo.lock();
        try (DataInputStream entrada = new DataInputStream(
                new BufferedInputStream(Files.newInputStream(archivo)))) {
            CabeceraIns cabecera = leerCabecera(archivo, entrada);
            byte[] datos = entrada.readNBytes(cabecera.longitud());
            if (datos.length != cabecera.longitud()) {
                throw new ArchivoCorruptoException(archivo.getFileName().toString());
            }
            return new ContenidoIns(cabecera, datos);
        } catch (IOException error) {
            throw new ArchivoCorruptoException(archivo.getFileName().toString(), error);
        } finally {
            cerrojo.unlock();
        }
    }

    public static void actualizarContenido(Path archivo, byte[] datos)
            throws ArchivoCorruptoException, OperacionArchivoException {
        ReentrantLock cerrojo = Cerrojos.de(archivo);
        cerrojo.lock();
        try {
            CabeceraIns cabecera = leerCabecera(archivo);
            escribir(archivo, cabecera.conLongitud(datos.length), datos);
        } finally {
            cerrojo.unlock();
        }
    }

    public static void renombrar(Path archivo, String nuevoNombreVisible)
            throws ArchivoCorruptoException, OperacionArchivoException {
        ReentrantLock cerrojo = Cerrojos.de(archivo);
        cerrojo.lock();
        try {
            ContenidoIns contenido = leer(archivo);
            escribir(archivo, contenido.cabecera().conNombre(nuevoNombreVisible), contenido.datos());
        } finally {
            cerrojo.unlock();
        }
    }

    private static void descartarTemporal(Path destino) {
        try {
            Files.deleteIfExists(destino.resolveSibling(destino.getFileName() + ".tmp"));
        } catch (IOException ignorado) {
        }
    }

    private static CabeceraIns leerCabecera(Path archivo, DataInputStream entrada)
            throws IOException, ArchivoCorruptoException {
        if (entrada.readInt() != FIRMA || entrada.readInt() != VERSION) {
            throw new ArchivoCorruptoException(archivo.getFileName().toString());
        }
        TipoArchivo tipo = TipoArchivo.desdeOrdinal(entrada.readByte());
        String nombreVisible = entrada.readUTF();
        long creado = entrada.readLong();
        long modificado = entrada.readLong();
        int longitud = entrada.readInt();
        if (longitud < 0) {
            throw new ArchivoCorruptoException(archivo.getFileName().toString());
        }
        return new CabeceraIns(tipo, nombreVisible, creado, modificado, longitud);
    }
}
