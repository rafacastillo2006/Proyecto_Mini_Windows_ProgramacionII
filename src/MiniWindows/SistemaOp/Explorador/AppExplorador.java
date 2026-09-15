package MiniWindows.SistemaOp.Explorador;

import MiniWindows.SistemaOp.Apps.Aplicacion;
import MiniWindows.SistemaOp.Apps.ContextoApp;
import MiniWindows.SistemaOp.Archivos.NodoArchivo;
import MiniWindows.SistemaOp.Escritorio.Iconos;
import javafx.scene.Node;

public class AppExplorador implements Aplicacion {

    @Override
    public String id() {
        return "explorador";
    }

    @Override
    public String titulo() {
        return "Explorador de archivos";
    }

    @Override
    public String icono() {
        return "explorador";
    }

    @Override
    public double anchoInicial() {
        return 940;
    }

    @Override
    public double altoInicial() {
        return 580;
    }

    @Override
    public boolean puedeAbrir(NodoArchivo archivo) {
        return archivo != null && archivo.esCarpeta();
    }

    @Override
    public Node crearContenido(ContextoApp contexto) {
        return new PanelExplorador(contexto);
    }

    @Override
    public Node crearContenido(ContextoApp contexto, NodoArchivo archivo) {
        return new PanelExplorador(contexto, archivo == null ? null : archivo.getRuta());
    }
}
