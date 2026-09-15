package MiniWindows.SistemaOp.AdminCuentas;

import MiniWindows.SistemaOp.Apps.Aplicacion;
import MiniWindows.SistemaOp.Apps.ContextoApp;
import MiniWindows.SistemaOp.Escritorio.Iconos;
import MiniWindows.SistemaOp.Nucleo.Sesion;
import javafx.scene.Node;

public class AppCuentas implements Aplicacion {

    @Override
    public String id() {
        return "cuentas";
    }

    @Override
    public String titulo() {
        return "Cuentas de usuario";
    }

    @Override
    public String icono() {
        return "cuentas";
    }

    @Override
    public double anchoInicial() {
        return 880;
    }

    @Override
    public double altoInicial() {
        return 520;
    }

    @Override
    public boolean permiteVariasInstancias() {
        return false;
    }

    @Override
    public boolean disponiblePara(Sesion sesion) {
        return sesion.esAdministrador();
    }

    @Override
    public Node crearContenido(ContextoApp contexto) {
        return new PanelCuentas(contexto);
    }
}
