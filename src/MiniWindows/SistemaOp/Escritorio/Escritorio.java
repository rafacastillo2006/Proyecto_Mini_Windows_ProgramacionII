package MiniWindows.SistemaOp.Escritorio;

import MiniWindows.Estructuras.ListaEnlazada;
import MiniWindows.SistemaOp.Apps.Aplicacion;
import MiniWindows.SistemaOp.Apps.ContextoApp;
import MiniWindows.SistemaOp.Apps.RegistroAplicaciones;
import MiniWindows.SistemaOp.Nucleo.Sesion;
import MiniWindows.SistemaOp.Nucleo.SistemaOperativo;
import javafx.event.EventTarget;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class Escritorio extends BorderPane {

    private final GestorVentanas gestor;
    private final MenuInicio menuInicio;
    private final CentroAvisos avisos = new CentroAvisos();

    public Escritorio(SistemaOperativo sistema, Sesion sesion, Runnable alCerrarSesion, Runnable alApagar) {
        Pane capaVentanas = new Pane();
        capaVentanas.setPickOnBounds(false);
        gestor = new GestorVentanas(capaVentanas);

        RegistroAplicaciones registro = sistema.getAplicaciones();
        AccionesArchivos acciones = new AccionesArchivos(sistema.getSistemaArchivos(), new PortapapelesArchivos());
        ContextoApp contexto = new ContextoApp(sesion, sistema.getSistemaArchivos(),
                sistema.getServicioCuentas(), gestor, registro, acciones);
        gestor.usarContexto(contexto);
        contexto.usarCentroAvisos(avisos);
        StackPane.setAlignment(avisos, Pos.TOP_RIGHT);

        ListaEnlazada<Aplicacion> disponibles = registro.disponiblesPara(sesion);
        menuInicio = new MenuInicio(disponibles, sesion, gestor, alCerrarSesion, alApagar);
        StackPane.setAlignment(menuInicio, Pos.BOTTOM_CENTER);
        StackPane.setMargin(menuInicio, new Insets(0, 0, 8, 0));

        IconosEscritorio iconos = new IconosEscritorio(contexto, disponibles);
        ContextMenu menuEscritorio = iconos.menuDelEscritorio();
        Fondo fondo = Fondo.escritorio();
        fondo.setOnContextMenuRequested(evento ->
                menuEscritorio.show(fondo, evento.getScreenX(), evento.getScreenY()));

        StackPane area = new StackPane(fondo, iconos, capaVentanas, menuInicio, avisos);
        BarraTareas barraTareas = new BarraTareas(sesion, gestor, menuInicio::alternar, alCerrarSesion);

        addEventFilter(MouseEvent.MOUSE_PRESSED, evento -> {
            if (menuInicio.isVisible()
                    && !perteneceA(menuInicio, evento.getTarget())
                    && !perteneceA(barraTareas.getBotonInicio(), evento.getTarget())) {
                menuInicio.ocultar();
            }
        });

        setCenter(area);
        setBottom(barraTareas);
    }

    public GestorVentanas getGestorVentanas() {
        return gestor;
    }

    public CentroAvisos getCentroAvisos() {
        return avisos;
    }

    private boolean perteneceA(Node contenedor, EventTarget objetivo) {
        Node actual = objetivo instanceof Node nodo ? nodo : null;
        while (actual != null) {
            if (actual == contenedor) {
                return true;
            }
            actual = actual.getParent();
        }
        return false;
    }
}
