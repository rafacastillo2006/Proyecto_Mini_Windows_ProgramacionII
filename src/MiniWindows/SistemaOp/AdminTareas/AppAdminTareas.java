package MiniWindows.SistemaOp.AdminTareas;

import MiniWindows.SistemaOp.Apps.Aplicacion;
import MiniWindows.SistemaOp.Apps.ContextoApp;
import MiniWindows.SistemaOp.Escritorio.Iconos;
import javafx.scene.Node;

public class AppAdminTareas implements Aplicacion {

    public static final String ID = "tareas";

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String titulo() {
        return "Administrador de tareas";
    }

    @Override
    public String icono() {
        return "tareas";
    }

    @Override
    public double anchoInicial() {
        return 860;
    }

    @Override
    public double altoInicial() {
        return 560;
    }

    @Override
    public boolean permiteVariasInstancias() {
        return false;
    }

    @Override
    public boolean enElEscritorio() {
        return false;
    }

    @Override
    public Node crearContenido(ContextoApp contexto) {
        return new PanelAdminTareas(contexto);
    }
}
