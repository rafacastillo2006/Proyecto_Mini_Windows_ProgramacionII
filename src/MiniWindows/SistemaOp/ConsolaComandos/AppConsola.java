package MiniWindows.SistemaOp.ConsolaComandos;

import MiniWindows.SistemaOp.Apps.Aplicacion;
import MiniWindows.SistemaOp.Apps.ContextoApp;
import MiniWindows.SistemaOp.Escritorio.Iconos;
import javafx.scene.Node;

public class AppConsola implements Aplicacion {

    public static final String ID = "consola";

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String titulo() {
        return "Consola de comandos";
    }

    @Override
    public String icono() {
        return "consola";
    }

    @Override
    public double anchoInicial() {
        return 780;
    }

    @Override
    public double altoInicial() {
        return 480;
    }

    @Override
    public Node crearContenido(ContextoApp contexto) {
        return new PanelConsola(contexto);
    }
}
