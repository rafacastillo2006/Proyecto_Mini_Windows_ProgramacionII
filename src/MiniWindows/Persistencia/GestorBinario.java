package MiniWindows.Persistencia;

import MiniWindows.Estructuras.ListaEnlazada;
import java.io.*;

public class GestorBinario<T extends Serializable> {
    private String rutaArchivo;

    public GestorBinario(String rutaArchivo) {
        this.rutaArchivo = rutaArchivo;
    }

    public void guardarLista(ListaEnlazada<T> lista) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(rutaArchivo))) {
            oos.writeObject(lista);
        } catch (IOException e) {
            System.err.println("Error al guardar en binario: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public ListaEnlazada<T> leerLista() {
        File archivo = new File(rutaArchivo);
        if (!archivo.exists() || archivo.length() == 0) {
            return new ListaEnlazada<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
            return (ListaEnlazada<T>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error al leer desde binario: " + e.getMessage());
            return new ListaEnlazada<>();
        }
    }
}