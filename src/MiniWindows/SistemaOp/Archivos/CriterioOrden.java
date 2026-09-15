package MiniWindows.SistemaOp.Archivos;

import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

public enum CriterioOrden {

    NOMBRE("Nombre", comparadorPorNombre()),
    FECHA("Fecha", Comparator.comparing(NodoArchivo::getModificado).reversed()),
    TIPO("Tipo", Comparator.comparing(nodo -> nodo.getTipo().getEtiqueta())),
    TAMANO("Tamaño", Comparator.comparingLong(NodoArchivo::getTamano).reversed());

    private final String etiqueta;
    private final Comparator<NodoArchivo> comparador;

    CriterioOrden(String etiqueta, Comparator<NodoArchivo> comparador) {
        this.etiqueta = etiqueta;
        this.comparador = comparador;
    }

    private static Comparator<NodoArchivo> comparadorPorNombre() {
        Collator collator = Collator.getInstance(Locale.forLanguageTag("es"));
        collator.setStrength(Collator.SECONDARY);
        return (uno, otro) -> collator.compare(uno.getNombre(), otro.getNombre());
    }

    public Comparator<NodoArchivo> comparador() {
        return Comparator.comparing(NodoArchivo::esCarpeta).reversed().thenComparing(comparador);
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
