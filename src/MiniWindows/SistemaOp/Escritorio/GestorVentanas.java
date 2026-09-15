package MiniWindows.SistemaOp.Escritorio;

import MiniWindows.SistemaOp.Apps.Aplicacion;
import MiniWindows.SistemaOp.Apps.RecibeArchivo;
import MiniWindows.SistemaOp.Apps.ContextoApp;
import MiniWindows.SistemaOp.Archivos.NodoArchivo;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;

public class GestorVentanas {

    private static final double MARGEN_ACOPLE = 26;
    private static final String ESTILO_PREVIO = "-fx-background-color: rgba(0,103,192,0.18); "
            + "-fx-border-color: rgba(0,103,192,0.55); -fx-border-width: 2; "
            + "-fx-background-radius: 8; -fx-border-radius: 8;";

    private static final double MARGEN_INICIAL = 56;
    private static final double DESPLAZAMIENTO_CASCADA = 32;
    private static final int POSICIONES_CASCADA = 6;

    private final Pane capa;
    private final ObservableList<VentanaInterna> ventanas = FXCollections.observableArrayList();
    private final ObjectProperty<VentanaInterna> ventanaActiva = new SimpleObjectProperty<>();
    private final Region previoAcople = new Region();

    private ContextoApp contexto;
    private int aperturas;

    public GestorVentanas(Pane capa) {
        this.capa = capa;
        previoAcople.setStyle(ESTILO_PREVIO);
        previoAcople.setMouseTransparent(true);
        previoAcople.setVisible(false);
    }

    public ZonaAcople zonaPara(double x, double y) {
        if (y <= MARGEN_ACOPLE) {
            return ZonaAcople.SUPERIOR;
        }
        if (x <= MARGEN_ACOPLE) {
            return ZonaAcople.IZQUIERDA;
        }
        if (x >= capa.getWidth() - MARGEN_ACOPLE) {
            return ZonaAcople.DERECHA;
        }
        return ZonaAcople.NINGUNA;
    }

    public void mostrarPrevio(ZonaAcople zona) {
        if (zona == ZonaAcople.NINGUNA) {
            ocultarPrevio();
            return;
        }
        if (!capa.getChildren().contains(previoAcople)) {
            capa.getChildren().add(previoAcople);
        }
        double mitad = capa.getWidth() / 2;
        switch (zona) {
            case IZQUIERDA -> ubicarPrevio(0, 0, mitad, capa.getHeight());
            case DERECHA -> ubicarPrevio(mitad, 0, mitad, capa.getHeight());
            case SUPERIOR -> ubicarPrevio(0, 0, capa.getWidth(), capa.getHeight());
            default -> ocultarPrevio();
        }
        previoAcople.setVisible(true);
        previoAcople.toFront();
    }

    public void ocultarPrevio() {
        previoAcople.setVisible(false);
        capa.getChildren().remove(previoAcople);
    }

    private void ubicarPrevio(double x, double y, double ancho, double alto) {
        previoAcople.setLayoutX(x);
        previoAcople.setLayoutY(y);
        previoAcople.setPrefSize(ancho, alto);
        previoAcople.setMaxSize(ancho, alto);
    }

    public void usarContexto(ContextoApp contexto) {
        this.contexto = contexto;
    }

    public VentanaInterna abrir(Aplicacion aplicacion) {
        return abrir(aplicacion, null);
    }

    public VentanaInterna abrir(Aplicacion aplicacion, NodoArchivo archivo) {
        if (!aplicacion.permiteVariasInstancias()) {
            VentanaInterna existente = ventanas.stream()
                    .filter(ventana -> ventana.getAplicacion().id().equals(aplicacion.id()))
                    .findFirst()
                    .orElse(null);
            if (existente != null) {
                if (archivo != null && existente.getContenido() instanceof RecibeArchivo receptor) {
                    receptor.mostrarArchivo(archivo);
                }
                existente.setMinimizada(false);
                activar(existente);
                return existente;
            }
        }
        VentanaInterna ventana = new VentanaInterna(this, aplicacion,
                archivo == null ? aplicacion.crearContenido(contexto) : aplicacion.crearContenido(contexto, archivo));
        ubicar(ventana, aplicacion);
        capa.getChildren().add(ventana);
        ventanas.add(ventana);
        activar(ventana);
        return ventana;
    }

    public void cerrar(VentanaInterna ventana) {
        capa.getChildren().remove(ventana);
        ventanas.remove(ventana);
        if (ventanaActiva.get() == ventana) {
            ventanaActiva.set(null);
        }
        for (int posicion = ventanas.size() - 1; posicion >= 0; posicion--) {
            VentanaInterna candidata = ventanas.get(posicion);
            if (!candidata.estaMinimizada()) {
                activar(candidata);
                return;
            }
        }
    }

    public void cerrarTodas() {
        for (VentanaInterna ventana : ventanas.toArray(new VentanaInterna[0])) {
            cerrar(ventana);
        }
        aperturas = 0;
    }

    public void activar(VentanaInterna ventana) {
        if (ventanaActiva.get() == ventana && !ventana.estaMinimizada()) {
            return;
        }
        if (ventana.estaMinimizada()) {
            ventana.setMinimizada(false);
        }
        ventana.toFront();
        ventanaActiva.set(ventana);
        for (VentanaInterna otra : ventanas) {
            otra.marcarActiva(otra == ventana);
        }
    }

    public void alternar(VentanaInterna ventana) {
        if (ventana.estaMinimizada()) {
            activar(ventana);
        } else if (ventanaActiva.get() == ventana) {
            ventana.setMinimizada(true);
            ventanaActiva.set(null);
        } else {
            activar(ventana);
        }
    }

    public ObservableList<VentanaInterna> getVentanas() {
        return ventanas;
    }

    public ReadOnlyObjectProperty<VentanaInterna> ventanaActivaProperty() {
        return ventanaActiva;
    }

    private void ubicar(VentanaInterna ventana, Aplicacion aplicacion) {
        double anchoDisponible = capa.getWidth() > 0 ? capa.getWidth() : aplicacion.anchoInicial() + 2 * MARGEN_INICIAL;
        double altoDisponible = capa.getHeight() > 0 ? capa.getHeight() : aplicacion.altoInicial() + 2 * MARGEN_INICIAL;
        double ancho = Math.max(VentanaInterna.ANCHO_MINIMO, Math.min(aplicacion.anchoInicial(), anchoDisponible - 48));
        double alto = Math.max(VentanaInterna.ALTO_MINIMO, Math.min(aplicacion.altoInicial(), altoDisponible - 48));
        ventana.setPrefSize(ancho, alto);

        double desplazamiento = (aperturas % POSICIONES_CASCADA) * DESPLAZAMIENTO_CASCADA;
        aperturas++;
        double x = Math.min(MARGEN_INICIAL + desplazamiento, Math.max(0, anchoDisponible - ancho));
        double y = Math.min(MARGEN_INICIAL + desplazamiento, Math.max(0, altoDisponible - alto));
        ventana.setLayoutX(x);
        ventana.setLayoutY(y);
    }
}
