package MiniWindows.SistemaOp.EditorTexto;

import MiniWindows.Modelo.TipoArchivo;
import MiniWindows.SistemaOp.Apps.Aplicacion;
import MiniWindows.SistemaOp.Apps.ContextoApp;
import MiniWindows.SistemaOp.Archivos.NodoArchivo;
import MiniWindows.SistemaOp.Escritorio.Iconos;
import javafx.scene.Node;

public class AppEditorTexto implements Aplicacion {

    public static final String ID = "editor";

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String titulo() {
        return "Editor de texto";
    }

    @Override
    public String icono() {
        return "editor";
    }

    @Override
    public double anchoInicial() {
        return 900;
    }

    @Override
    public double altoInicial() {
        return 600;
    }

    @Override
    public boolean puedeAbrir(NodoArchivo archivo) {
        return archivo != null && archivo.getTipo() == TipoArchivo.TEXTO;
    }

    @Override
    public Node crearContenido(ContextoApp contexto) {
        return new PanelEditor(contexto, null);
    }

    @Override
    public Node crearContenido(ContextoApp contexto, NodoArchivo archivo) {
        return new PanelEditor(contexto, archivo);
    }
}
