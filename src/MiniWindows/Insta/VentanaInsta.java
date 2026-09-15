package MiniWindows.Insta;

import MiniWindows.Insta.Servicio.ServicioInsta;
import MiniWindows.Insta.Servicio.ServicioLocal;
import MiniWindows.Insta.Vistas.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class VentanaInsta extends BorderPane {

    private ServicioInsta servicio;
    private StackPane contenedorVistas;
    private VBox sideBar;

    public VentanaInsta() {
        this.servicio = new ServicioLocal();
        this.contenedorVistas = new StackPane();

        inicializarInterfaz();
    }

    private void inicializarInterfaz() {
        sideBar = new VBox(15);
        sideBar.setPrefWidth(200);
        sideBar.setPadding(new Insets(25, 15, 20, 20));
        sideBar.setStyle("-fx-background-color: #ffffff; -fx-border-color: #dbdbdb; -fx-border-width: 0 1 0 0;");

        Label lblLogo = new Label("Instagram");
        lblLogo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        lblLogo.setPadding(new Insets(0, 0, 20, 5));

        Button btnTimeline = crearBotonMenu("🏠  Inicio");
        Button btnBuscar = crearBotonMenu("🔍  Buscar");
        Button btnInbox = crearBotonMenu("💬  Mensajes");
        Button btnPublicar = crearBotonMenu("➕  Crear");
        Button btnPerfil = crearBotonMenu("👤  Perfil");
        Button btnCerrarSesion = crearBotonMenu("🚪  Cerrar Sesión");

        sideBar.getChildren().addAll(lblLogo, btnTimeline, btnBuscar, btnInbox, btnPublicar, btnPerfil, btnCerrarSesion);

        // Sin padding ni bordes blancos para que la vista ocupe todo el espacio
        contenedorVistas.setPadding(Insets.EMPTY);
        contenedorVistas.setStyle("-fx-background-color: transparent;");
        setCenter(contenedorVistas);

        btnTimeline.setOnAction(e -> cambiarVista(new LineaTiempoInsta(servicio)));
        btnBuscar.setOnAction(e -> cambiarVista(new BuscarInsta(servicio)));
        btnInbox.setOnAction(e -> cambiarVista(new BandejaEntrada(servicio)));
        btnPublicar.setOnAction(e -> cambiarVista(new HacerPost(servicio)));
        btnPerfil.setOnAction(e -> cambiarVista(new MiPerfilInsta(servicio)));
        btnCerrarSesion.setOnAction(e -> {
            SesionInsta.getInstancia().cerrarSesion();
            mostrarPantallaAutenticacion();
        });

        if (SesionInsta.getInstancia().haySesionActiva()) {
            mostrarAppPrincipal();
        } else {
            mostrarPantallaAutenticacion();
        }
    }

    public void mostrarPantallaAutenticacion() {
        setLeft(null);
        cambiarVista(new LoginInsta(
                servicio,
                this::mostrarAppPrincipal,
                () -> cambiarVista(new RegistrarseInsta(servicio, this::mostrarPantallaAutenticacion))
        ));
    }

    public void mostrarAppPrincipal() {
        setLeft(sideBar);
        cambiarVista(new LineaTiempoInsta(servicio));
    }

    private Button crearBotonMenu(String texto) {
        Button btn = new Button(texto);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
        btn.setStyle("-fx-background-color: transparent; -fx-padding: 10 12; -fx-cursor: hand;");

        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #f2f2f2; -fx-background-radius: 8; -fx-padding: 10 12; -fx-cursor: hand;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: transparent; -fx-padding: 10 12; -fx-cursor: hand;"));

        return btn;
    }

    public void cambiarVista(Node nuevaVista) {
        contenedorVistas.getChildren().clear();
        contenedorVistas.getChildren().add(nuevaVista);
    }

    public ServicioInsta getServicio() {
        return servicio;
    }
}