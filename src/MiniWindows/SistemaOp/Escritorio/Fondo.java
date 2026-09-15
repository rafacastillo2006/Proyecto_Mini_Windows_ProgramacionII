package MiniWindows.SistemaOp.Escritorio;

import javafx.scene.layout.Region;

public class Fondo extends Region {

    private static final String ESCRITORIO = "-fx-background-color: "
            + "linear-gradient(from 0% 0% to 100% 100%, #0e2f57 0%, #174d86 52%, #1f6ea8 100%), "
            + "radial-gradient(center 26% 20%, radius 58%, rgba(140,205,255,0.26), rgba(140,205,255,0.0)), "
            + "radial-gradient(center 82% 88%, radius 62%, rgba(6,22,42,0.40), rgba(6,22,42,0.0));";

    private static final String PANTALLA_ACCESO = "-fx-background-color: "
            + "linear-gradient(from 0% 0% to 0% 100%, #f7f8fa 0%, #e9edf2 100%);";

    private Fondo(String estilo) {
        setStyle(estilo);
    }

    public static Fondo escritorio() {
        return new Fondo(ESCRITORIO);
    }

    public static Fondo pantallaAcceso() {
        return new Fondo(PANTALLA_ACCESO);
    }
}
