package MiniWindows.SistemaOp.Apps;

import MiniWindows.SistemaOp.Archivos.NodoArchivo;

@FunctionalInterface
public interface RecibeArchivo {

    void mostrarArchivo(NodoArchivo archivo);
}
