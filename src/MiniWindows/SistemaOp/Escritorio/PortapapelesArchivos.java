package MiniWindows.SistemaOp.Escritorio;

import MiniWindows.SistemaOp.Archivos.NodoArchivo;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class PortapapelesArchivos {

    private final ObjectProperty<NodoArchivo> contenido = new SimpleObjectProperty<>();

    private boolean corte;

    public void copiar(NodoArchivo nodo) {
        contenido.set(nodo);
        corte = false;
    }

    public void cortar(NodoArchivo nodo) {
        contenido.set(nodo);
        corte = true;
    }

    public void limpiar() {
        contenido.set(null);
        corte = false;
    }

    public NodoArchivo getContenido() {
        return contenido.get();
    }

    public ReadOnlyObjectProperty<NodoArchivo> contenidoProperty() {
        return contenido;
    }

    public boolean esCorte() {
        return corte;
    }

    public boolean estaVacio() {
        return contenido.get() == null;
    }
}
