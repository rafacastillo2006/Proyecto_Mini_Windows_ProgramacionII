package MiniWindows.Insta;

import MiniWindows.SistemaOp.Apps.Aplicacion;
import MiniWindows.SistemaOp.Apps.ContextoApp;
import javafx.scene.Node;

public class AppInsta implements Aplicacion {

    public static final String ID = "insta";

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String titulo() {
        return "INSTA+";
    }

    @Override
    public String icono() {
        return "insta";
    }

    @Override
    public double anchoInicial() {
        return 940;
    }

    @Override
    public double altoInicial() {
        return 660;
    }

    @Override
    public boolean permiteVariasInstancias() {
        return false;
    }

    @Override
    public Node crearContenido(ContextoApp contexto) {
        return new VentanaInsta(contexto);
    }
}
