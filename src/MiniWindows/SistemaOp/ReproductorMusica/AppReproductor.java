package MiniWindows.SistemaOp.ReproductorMusica;

import MiniWindows.Modelo.TipoArchivo;
import MiniWindows.SistemaOp.Apps.Aplicacion;
import MiniWindows.SistemaOp.Apps.ContextoApp;
import MiniWindows.SistemaOp.Archivos.NodoArchivo;
import MiniWindows.SistemaOp.Escritorio.Iconos;
import javafx.scene.Node;

public class AppReproductor implements Aplicacion {

    public static final String ID = "reproductor";

    @Override
    public String id() {
        return ID;
    }

    @Override
    public String titulo() {
        return "Reproductor de música";
    }

    @Override
    public String icono() {
        return "reproductor";
    }

    @Override
    public double anchoInicial() {
        return 720;
    }

    @Override
    public double altoInicial() {
        return 620;
    }

    @Override
    public boolean permiteVariasInstancias() {
        return false;
    }

    @Override
    public boolean puedeAbrir(NodoArchivo archivo) {
        return archivo != null && archivo.getTipo() == TipoArchivo.MUSICA;
    }

    @Override
    public Node crearContenido(ContextoApp contexto) {
        return new PanelReproductor(contexto, null);
    }

    @Override
    public Node crearContenido(ContextoApp contexto, NodoArchivo archivo) {
        return new PanelReproductor(contexto, archivo);
    }
}
