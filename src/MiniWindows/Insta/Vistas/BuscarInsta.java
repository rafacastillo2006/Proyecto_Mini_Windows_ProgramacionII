package MiniWindows.Insta.Vistas;

import MiniWindows.Estructuras.ListaEnlazada;
import MiniWindows.Insta.Servicio.ServicioInsta;
import MiniWindows.Insta.SesionInsta;
import MiniWindows.Modelo.Publicacion;
import MiniWindows.Modelo.UsuarioInsta;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class BuscarInsta extends VBox {

    private ServicioInsta servicio;
    private Button btnPersonas;
    private Button btnHashtags;
    private VBox panelPersonas;
    private VBox panelHashtags;
    private StackPane contenedorPaneles;

    public BuscarInsta(ServicioInsta servicio) {
        this.servicio = servicio;

        setSpacing(20);
        setPadding(new Insets(20));
        setAlignment(Pos.TOP_CENTER);
        setStyle("-fx-background-color: #ffffff;");

        HBox selectorBox = new HBox(15);
        selectorBox.setAlignment(Pos.CENTER);
        selectorBox.setPadding(new Insets(10));

        btnPersonas = new Button("👤  Buscar Personas");
        btnHashtags = new Button("🏷️  Buscar Hashtags");

        btnPersonas.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        btnHashtags.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));

        selectorBox.getChildren().addAll(btnPersonas, btnHashtags);

        panelPersonas = crearPanelBuscarPersonas();
        panelHashtags = crearPanelBuscarHashtags();

        contenedorPaneles = new StackPane();
        contenedorPaneles.getChildren().addAll(panelPersonas, panelHashtags);

        btnPersonas.setOnAction(e -> mostrarPanel(true));
        btnHashtags.setOnAction(e -> mostrarPanel(false));

        mostrarPanel(true);

        VBox contenedor = new VBox(15, selectorBox, contenedorPaneles);
        contenedor.setMaxWidth(450);
        contenedor.setAlignment(Pos.TOP_CENTER);

        getChildren().add(contenedor);
    }

    private void mostrarPanel(boolean verPersonas) {
        panelPersonas.setVisible(verPersonas);
        panelPersonas.setManaged(verPersonas);

        panelHashtags.setVisible(!verPersonas);
        panelHashtags.setManaged(!verPersonas);

        if (verPersonas) {
            btnPersonas.setStyle("-fx-background-color: #0095f6; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 8 16; -fx-cursor: hand;");
            btnHashtags.setStyle("-fx-background-color: #efefef; -fx-text-fill: #262626; -fx-background-radius: 6; -fx-padding: 8 16; -fx-cursor: hand;");
        } else {
            btnHashtags.setStyle("-fx-background-color: #0095f6; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 8 16; -fx-cursor: hand;");
            btnPersonas.setStyle("-fx-background-color: #efefef; -fx-text-fill: #262626; -fx-background-radius: 6; -fx-padding: 8 16; -fx-cursor: hand;");
        }
    }

    private VBox crearPanelBuscarPersonas() {
        VBox box = new VBox(12);
        box.setAlignment(Pos.TOP_CENTER);

        HBox searchBox = new HBox(8);
        searchBox.setAlignment(Pos.CENTER);

        TextField txtCriterio = new TextField();
        txtCriterio.setPromptText("Escribe un usuario...");
        txtCriterio.setPrefWidth(280);
        txtCriterio.setStyle("-fx-background-color: #fafafa; -fx-border-color: #dbdbdb; -fx-border-radius: 4; -fx-padding: 8;");

        Button btnBuscar = new Button("Buscar");
        btnBuscar.setStyle("-fx-background-color: #0095f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 14; -fx-cursor: hand;");

        searchBox.getChildren().addAll(txtCriterio, btnBuscar);

        VBox resultadosBox = new VBox(10);
        resultadosBox.setAlignment(Pos.TOP_CENTER);

        ScrollPane scroll = new ScrollPane(resultadosBox);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(350);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        btnBuscar.setOnAction(e -> {
            resultadosBox.getChildren().clear();
            String query = txtCriterio.getText().trim();

            if (query.isEmpty()) return;

            ListaEnlazada<UsuarioInsta> lista = servicio.buscarPersonas(query);

            if (lista.esVacia()) {
                Label lblNoResults = new Label("No se encontraron usuarios.");
                lblNoResults.setStyle("-fx-text-fill: #737373;");
                resultadosBox.getChildren().add(lblNoResults);
            } else {
                UsuarioInsta actual = SesionInsta.getInstancia().getUsuarioActual();
                for (int i = 0; i < lista.getTamano(); i++) {
                    UsuarioInsta u = lista.obtener(i);
                    resultadosBox.getChildren().add(crearTarjetaUsuario(u, actual));
                }
            }
        });

        box.getChildren().addAll(searchBox, scroll);
        return box;
    }

    private HBox crearTarjetaUsuario(UsuarioInsta u, UsuarioInsta usuarioLogueado) {
        HBox tarjeta = new HBox(15);
        tarjeta.setAlignment(Pos.CENTER_LEFT);
        tarjeta.setPadding(new Insets(10));
        tarjeta.setStyle("-fx-background-color: #ffffff; -fx-border-color: #dbdbdb; -fx-border-radius: 5;");

        VBox info = new VBox(2);
        Label lblUser = new Label("@" + u.getUsername());
        lblUser.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));

        Label lblNombre = new Label(u.getNombreCompleto());
        lblNombre.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 11));
        lblNombre.setStyle("-fx-text-fill: #737373;");

        info.getChildren().addAll(lblUser, lblNombre);
        HBox.setHgrow(info, Priority.ALWAYS);
        tarjeta.getChildren().add(info);

        if (usuarioLogueado != null && !usuarioLogueado.getUsername().equalsIgnoreCase(u.getUsername())) {
            Button btnSeguir = new Button("Seguir");
            btnSeguir.setStyle("-fx-background-color: #0095f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");

            Button btnDejarSeguir = new Button("Siguiendo");
            btnDejarSeguir.setStyle("-fx-background-color: #efefef; -fx-text-fill: black; -fx-cursor: hand;");

            btnSeguir.setOnAction(e -> {
                servicio.seguirUsuario(usuarioLogueado.getUsername(), u.getUsername());
                tarjeta.getChildren().remove(btnSeguir);
                tarjeta.getChildren().add(btnDejarSeguir);
            });

            btnDejarSeguir.setOnAction(e -> {
                servicio.dejarDeSeguir(usuarioLogueado.getUsername(), u.getUsername());
                tarjeta.getChildren().remove(btnDejarSeguir);
                tarjeta.getChildren().add(btnSeguir);
            });

            tarjeta.getChildren().add(btnSeguir);
        }

        return tarjeta;
    }

    private VBox crearPanelBuscarHashtags() {
        VBox box = new VBox(12);
        box.setAlignment(Pos.TOP_CENTER);

        HBox searchBox = new HBox(8);
        searchBox.setAlignment(Pos.CENTER);

        TextField txtHashtag = new TextField();
        txtHashtag.setPromptText("#ejemplo");
        txtHashtag.setPrefWidth(280);
        txtHashtag.setStyle("-fx-background-color: #fafafa; -fx-border-color: #dbdbdb; -fx-border-radius: 4; -fx-padding: 8;");

        Button btnBuscar = new Button("Buscar");
        btnBuscar.setStyle("-fx-background-color: #0095f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 14; -fx-cursor: hand;");

        searchBox.getChildren().addAll(txtHashtag, btnBuscar);

        VBox resultadosBox = new VBox(10);
        resultadosBox.setAlignment(Pos.TOP_CENTER);

        ScrollPane scroll = new ScrollPane(resultadosBox);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(350);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        btnBuscar.setOnAction(e -> {
            resultadosBox.getChildren().clear();
            String h = txtHashtag.getText().trim();

            if (h.isEmpty()) return;

            ListaEnlazada<Publicacion> posts = servicio.buscarHashtag(h);

            if (posts.esVacia()) {
                Label lblNoResults = new Label("No hay publicaciones con ese hashtag.");
                lblNoResults.setStyle("-fx-text-fill: #737373;");
                resultadosBox.getChildren().add(lblNoResults);
            } else {
                for (int i = 0; i < posts.getTamano(); i++) {
                    Publicacion p = posts.obtener(i);
                    Label lblPost = new Label("@" + p.getAutor() + ": " + p.getDescripcion());
                    lblPost.setWrapText(true);
                    lblPost.setStyle("-fx-padding: 10; -fx-background-color: #ffffff; -fx-border-color: #dbdbdb; -fx-border-radius: 4;");
                    resultadosBox.getChildren().add(lblPost);
                }
            }
        });

        box.getChildren().addAll(searchBox, scroll);
        return box;
    }
}