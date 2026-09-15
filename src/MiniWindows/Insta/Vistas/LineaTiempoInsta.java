package MiniWindows.Insta.Vistas;

import MiniWindows.Estructuras.ListaEnlazada;
import MiniWindows.Insta.Servicio.ServicioInsta;
import MiniWindows.Insta.SesionInsta;
import MiniWindows.Insta.Hilos.CargaIMGS;
import MiniWindows.Modelo.Publicacion;
import MiniWindows.Modelo.UsuarioInsta;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class LineaTiempoInsta extends VBox {

    private ServicioInsta servicio;

    public LineaTiempoInsta(ServicioInsta servicio) {
        this.servicio = servicio;

        setSpacing(20);
        setPadding(new Insets(20));
        setAlignment(Pos.TOP_CENTER);
        setStyle("-fx-background-color: #fafafa;");

        UsuarioInsta usuario = SesionInsta.getInstancia().getUsuarioActual();

        if (usuario != null) {
            getChildren().add(crearFeed(usuario.getUsername()));
        } else {
            Label lblError = new Label("Debe iniciar sesión para ver la Línea de Tiempo.");
            lblError.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
            getChildren().add(lblError);
        }
    }

    private ScrollPane crearFeed(String username) {
        VBox feedBox = new VBox(25);
        feedBox.setAlignment(Pos.TOP_CENTER);
        feedBox.setPadding(new Insets(10));

        ListaEnlazada<Publicacion> posts = servicio.obtenerTimeline(username);

        if (posts.esVacia()) {
            Label lblVacio = new Label("No hay publicaciones disponibles en tu línea de tiempo.");
            lblVacio.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));
            lblVacio.setStyle("-fx-text-fill: #8e8e8e;");
            feedBox.getChildren().add(lblVacio);
        } else {
            for (int i = 0; i < posts.getTamano(); i++) {
                Publicacion post = posts.obtener(i);
                feedBox.getChildren().add(crearTarjetaPost(post));
            }
        }

        ScrollPane scroll = new ScrollPane(feedBox);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        return scroll;
    }

    private VBox crearTarjetaPost(Publicacion post) {
        VBox tarjeta = new VBox(10);
        tarjeta.setMaxWidth(470);
        tarjeta.setPadding(new Insets(12, 0, 15, 0));
        tarjeta.setStyle("-fx-background-color: #ffffff; -fx-border-color: #dbdbdb; -fx-border-radius: 8; -fx-background-radius: 8; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.03), 8, 0, 0, 2);");

        HBox cabecera = new HBox(10);
        cabecera.setAlignment(Pos.CENTER_LEFT);
        cabecera.setPadding(new Insets(0, 12, 5, 12));

        ImageView imgAvatar = new ImageView();
        imgAvatar.setFitWidth(32);
        imgAvatar.setFitHeight(32);

        Circle clipCircle = new Circle(16, 16, 16);
        imgAvatar.setClip(clipCircle);

        ListaEnlazada<UsuarioInsta> resultadoBusqueda = servicio.buscarPersonas(post.getAutor());
        if (!resultadoBusqueda.esVacia()) {
            UsuarioInsta autorObj = resultadoBusqueda.obtener(0);
            if (autorObj.getFotoPerfil() != null && !autorObj.getFotoPerfil().isEmpty()) {
                CargaIMGS.cargarImagenAsync(autorObj.getFotoPerfil(), 32, 32, img -> {
                    if (img != null) {
                        javafx.application.Platform.runLater(() -> imgAvatar.setImage(img));
                    }
                });
            }
        }

        Label lblAutor = new Label(post.getAutor());
        lblAutor.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        lblAutor.setStyle("-fx-text-fill: #262626;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnOpciones = new Button("•••");
        btnOpciones.setStyle("-fx-background-color: transparent; -fx-text-fill: #737373; -fx-cursor: hand;");

        cabecera.getChildren().addAll(imgAvatar, lblAutor, spacer, btnOpciones);

        ImageView imgPost = new ImageView();
        imgPost.setFitWidth(470);
        imgPost.setPreserveRatio(true);

        if (post.getRutaImagen() != null && !post.getRutaImagen().isEmpty()) {
            CargaIMGS.cargarImagenAsync(post.getRutaImagen(), 470, 470, img -> {
                if (img != null) {
                    javafx.application.Platform.runLater(() -> imgPost.setImage(img));
                }
            });
        }

        HBox barraAcciones = new HBox(12);
        barraAcciones.setPadding(new Insets(5, 12, 0, 12));
        barraAcciones.setAlignment(Pos.CENTER_LEFT);

        Button btnLike = crearBotonAccion("🤍");
        Button btnComentario = crearBotonAccion("💬");
        Button btnCompartir = crearBotonAccion("✈️");

        Region spacerAcciones = new Region();
        HBox.setHgrow(spacerAcciones, Priority.ALWAYS);

        Button btnGuardar = crearBotonAccion("🔖");

        btnLike.setOnAction(e -> {
            if (btnLike.getText().equals("🤍")) {
                btnLike.setText("❤️");
                btnLike.setStyle("-fx-background-color: transparent; -fx-font-size: 16px; -fx-text-fill: red; -fx-cursor: hand;");
            } else {
                btnLike.setText("🤍");
                btnLike.setStyle("-fx-background-color: transparent; -fx-font-size: 16px; -fx-cursor: hand;");
            }
        });

        barraAcciones.getChildren().addAll(btnLike, btnComentario, btnCompartir, spacerAcciones, btnGuardar);

        TextFlow flowDescripcion = new TextFlow();
        flowDescripcion.setPadding(new Insets(0, 12, 0, 12));

        Text txtUser = new Text(post.getAutor() + " ");
        txtUser.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));

        String descTexto = post.getDescripcion() != null ? post.getDescripcion() : "";
        Text txtDesc = new Text(descTexto);
        txtDesc.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 13));

        flowDescripcion.getChildren().addAll(txtUser, txtDesc);

        tarjeta.getChildren().addAll(cabecera, imgPost, barraAcciones, flowDescripcion);
        return tarjeta;
    }

    private Button crearBotonAccion(String icono) {
        Button btn = new Button(icono);
        btn.setFont(Font.font(15));
        btn.setStyle("-fx-background-color: transparent; -fx-padding: 2; -fx-cursor: hand;");
        return btn;
    }
}