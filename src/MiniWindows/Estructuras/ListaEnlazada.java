package MiniWindows.Estructuras;

import java.io.Serializable;

public class ListaEnlazada<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private Nodo<T> cabeza;
    private int tamano;

    public ListaEnlazada() {
        this.cabeza = null;
        this.tamano = 0;
    }

    public void agregar(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (cabeza == null) {
            cabeza = nuevo;
        } else {
            Nodo<T> aux = cabeza;
            while (aux.getSiguiente() != null) {
                aux = aux.getSiguiente();
            }
            aux.setSiguiente(nuevo);
        }
        tamano++;
    }

    public boolean esVacia() {
        return cabeza == null || tamano == 0;
    }

    public int getTamano() {
        return tamano;
    }

    public T obtener(int indice) {
        if (indice < 0 || indice >= tamano) {
            throw new IndexOutOfBoundsException("Índice fuera de rango: " + indice);
        }
        Nodo<T> aux = cabeza;
        for (int i = 0; i < indice; i++) {
            aux = aux.getSiguiente();
        }
        return aux.getDato();
    }

    public void limpiar() {
        cabeza = null;
        tamano = 0;
    }
}