package MiniWindows.Estructuras;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Predicate;

public class ListaEnlazada<T> implements Iterable<T>, Serializable {

    private static final long serialVersionUID = 1L;

    private Nodo<T> primero;
    private Nodo<T> ultimo;
    private int tamano;

    public void agregar(T valor) {
        Nodo<T> nuevo = new Nodo<>(valor);
        if (primero == null) {
            primero = nuevo;
        } else {
            ultimo.setSiguiente(nuevo);
        }
        ultimo = nuevo;
        tamano++;
    }

    public void agregarAlInicio(T valor) {
        Nodo<T> nuevo = new Nodo<>(valor);
        nuevo.setSiguiente(primero);
        primero = nuevo;
        if (ultimo == null) {
            ultimo = nuevo;
        }
        tamano++;
    }

    public void agregarTodos(Iterable<? extends T> valores) {
        for (T valor : valores) {
            agregar(valor);
        }
    }

    public T obtener(int indice) {
        return nodoEn(indice).getValor();
    }

    public T primero() {
        if (primero == null) {
            throw new NoSuchElementException("La lista esta vacia");
        }
        return primero.getValor();
    }

    public T ultimo() {
        if (ultimo == null) {
            throw new NoSuchElementException("La lista esta vacia");
        }
        return ultimo.getValor();
    }

    public T buscar(Predicate<T> criterio) {
        for (Nodo<T> actual = primero; actual != null; actual = actual.getSiguiente()) {
            if (criterio.test(actual.getValor())) {
                return actual.getValor();
            }
        }
        return null;
    }

    public ListaEnlazada<T> filtrar(Predicate<T> criterio) {
        ListaEnlazada<T> resultado = new ListaEnlazada<>();
        for (T valor : this) {
            if (criterio.test(valor)) {
                resultado.agregar(valor);
            }
        }
        return resultado;
    }

    public int indiceDe(T valor) {
        int indice = 0;
        for (Nodo<T> actual = primero; actual != null; actual = actual.getSiguiente()) {
            if (Objects.equals(actual.getValor(), valor)) {
                return indice;
            }
            indice++;
        }
        return -1;
    }

    public boolean contiene(T valor) {
        return indiceDe(valor) >= 0;
    }

    public boolean eliminar(T valor) {
        Nodo<T> anterior = null;
        for (Nodo<T> actual = primero; actual != null; actual = actual.getSiguiente()) {
            if (Objects.equals(actual.getValor(), valor)) {
                desenlazar(anterior, actual);
                return true;
            }
            anterior = actual;
        }
        return false;
    }

    public T eliminarEn(int indice) {
        Nodo<T> objetivo = nodoEn(indice);
        Nodo<T> anterior = indice == 0 ? null : nodoEn(indice - 1);
        desenlazar(anterior, objetivo);
        return objetivo.getValor();
    }

    public void reemplazar(int indice, T valor) {
        nodoEn(indice).setValor(valor);
    }

    public void ordenar(Comparator<T> comparador) {
        primero = ordenar(primero, comparador);
        ultimo = primero;
        while (ultimo != null && ultimo.getSiguiente() != null) {
            ultimo = ultimo.getSiguiente();
        }
    }

    public void vaciar() {
        primero = null;
        ultimo = null;
        tamano = 0;
    }

    public int tamano() {
        return tamano;
    }

    public boolean estaVacia() {
        return tamano == 0;
    }

    public List<T> aLista() {
        List<T> copia = new ArrayList<>(tamano);
        for (T valor : this) {
            copia.add(valor);
        }
        return copia;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {

            private Nodo<T> actual = primero;

            @Override
            public boolean hasNext() {
                return actual != null;
            }

            @Override
            public T next() {
                if (actual == null) {
                    throw new NoSuchElementException();
                }
                T valor = actual.getValor();
                actual = actual.getSiguiente();
                return valor;
            }
        };
    }

    @Override
    public String toString() {
        StringBuilder texto = new StringBuilder("[");
        for (Nodo<T> actual = primero; actual != null; actual = actual.getSiguiente()) {
            texto.append(actual.getValor());
            if (actual.getSiguiente() != null) {
                texto.append(", ");
            }
        }
        return texto.append(']').toString();
    }

    private Nodo<T> nodoEn(int indice) {
        if (indice < 0 || indice >= tamano) {
            throw new IndexOutOfBoundsException("Indice fuera de rango: " + indice);
        }
        Nodo<T> actual = primero;
        for (int posicion = 0; posicion < indice; posicion++) {
            actual = actual.getSiguiente();
        }
        return actual;
    }

    private void desenlazar(Nodo<T> anterior, Nodo<T> objetivo) {
        if (anterior == null) {
            primero = objetivo.getSiguiente();
        } else {
            anterior.setSiguiente(objetivo.getSiguiente());
        }
        if (objetivo == ultimo) {
            ultimo = anterior;
        }
        objetivo.setSiguiente(null);
        tamano--;
    }

    private Nodo<T> ordenar(Nodo<T> inicio, Comparator<T> comparador) {
        if (inicio == null || inicio.getSiguiente() == null) {
            return inicio;
        }
        Nodo<T> mitad = mitad(inicio);
        Nodo<T> derecha = mitad.getSiguiente();
        mitad.setSiguiente(null);
        return mezclar(ordenar(inicio, comparador), ordenar(derecha, comparador), comparador);
    }

    private Nodo<T> mitad(Nodo<T> inicio) {
        Nodo<T> lento = inicio;
        Nodo<T> rapido = inicio.getSiguiente();
        while (rapido != null && rapido.getSiguiente() != null) {
            lento = lento.getSiguiente();
            rapido = rapido.getSiguiente().getSiguiente();
        }
        return lento;
    }

    private Nodo<T> mezclar(Nodo<T> izquierda, Nodo<T> derecha, Comparator<T> comparador) {
        Nodo<T> inicio = null;
        Nodo<T> cola = null;
        while (izquierda != null && derecha != null) {
            Nodo<T> elegido;
            if (comparador.compare(izquierda.getValor(), derecha.getValor()) <= 0) {
                elegido = izquierda;
                izquierda = izquierda.getSiguiente();
            } else {
                elegido = derecha;
                derecha = derecha.getSiguiente();
            }
            elegido.setSiguiente(null);
            if (inicio == null) {
                inicio = elegido;
            } else {
                cola.setSiguiente(elegido);
            }
            cola = elegido;
        }
        Nodo<T> restante = izquierda != null ? izquierda : derecha;
        if (cola == null) {
            return restante;
        }
        cola.setSiguiente(restante);
        return inicio;
    }
}
