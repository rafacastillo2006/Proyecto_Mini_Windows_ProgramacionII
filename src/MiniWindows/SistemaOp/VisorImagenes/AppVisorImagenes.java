package MiniWindows.SistemaOp.VisorImagenes;

import MiniWindows.Modelo.TipoArchivo;
import MiniWindows.SistemaOp.Apps.Aplicacion;
import MiniWindows.SistemaOp.Apps.ContextoApp;
import MiniWindows.SistemaOp.Archivos.NodoArchivo;
import MiniWindows.SistemaOp.Escritorio.Iconos;
import javafx.scene.Node;

public class AppVisorImagenes implements Aplicacion {

    public static final String ID = "visor";

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String titulo() {
        return "Visor de imágenes";
    }

    @Override
    public String icono() {
        return "visor";
    }

    @Override
    public double anchoInicial() {
        return 960;
    }

    @Override
    public double altoInicial() {
        return 640;
    }

    @Override
    public boolean puedeAbrir(NodoArchivo archivo) {
        return archivo != null && archivo.getTipo() == TipoArchivo.IMAGEN;
    }

    @Override
    public Node crearContenido(ContextoApp contexto) {
        return new PanelVisor(contexto, null);
    }

    @Override
    public Node crearContenido(ContextoApp contexto, NodoArchivo archivo) {
        return new PanelVisor(contexto, archivo);
    }
}
