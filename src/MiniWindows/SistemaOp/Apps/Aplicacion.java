package MiniWindows.SistemaOp.Apps;

import MiniWindows.SistemaOp.Archivos.NodoArchivo;
import MiniWindows.SistemaOp.Nucleo.Sesion;
import javafx.scene.Node;

public interface Aplicacion {

    String id();

    String titulo();

    String icono();

    Node crearContenido(ContextoApp contexto);

    default double anchoInicial() {
        return 900;
    }

    default double altoInicial() {
        return 560;
    }

    default boolean permiteVariasInstancias() {
        return true;
    }

    default boolean disponiblePara(Sesion sesion) {
        return true;
    }

    default boolean enElEscritorio() {
        return true;
    }

    default boolean puedeAbrir(NodoArchivo archivo) {
        return false;
    }

    default Node crearContenido(ContextoApp contexto, NodoArchivo archivo) {
        return crearContenido(contexto);
    }
}
