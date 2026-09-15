package MiniWindows.Insta.Vistas;

import MiniWindows.Insta.SesionInsta;
import MiniWindows.Insta.Servicio.ServicioInsta;
import MiniWindows.Modelo.UsuarioInsta;
import MiniWindows.Modelo.Publicacion;
import MiniWindows.Estructuras.ListaEnlazada;
import MiniWindows.Insta.Hilos.CargaIMGS;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class MiPerfilInsta extends VBox {

    private ServicioInsta servicio;

    public MiPerfilInsta(ServicioInsta servicio) {
        this.servicio = servicio;

        setSpacing(15);
        setPadding(new Insets(20));
        setAlignment(Pos.TOP_CENTER);
        setStyle("-fx-background-color: #fafafa;");

        UsuarioInsta usuario = SesionInsta.getInstancia().getUsuarioActual();

        if (usuario != null) {
            getChildren().add(crearEncabezadoPerfil(usuario));
            getChildren().add(new Separator());
            getChildren().add(crearGridPublicaciones(usuario));
        } else {
            Label lblInvalido = new Label("Debe iniciar sesión para ver su perfil.");
            lblInvalido.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
            getChildren().add(lblInvalido);
        }
    }

    private HBox crearEncabezadoPerfil(UsuarioInsta usuario) {
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(10));

        ImageView imgPerfil = new ImageView();
        imgPerfil.setFitWidth(100);
        imgPerfil.setFitHeight(100);
        imgPerfil.setStyle("-fx-border-color: #dbdbdb; -fx-border-radius: 50;");

        if (usuario.getFotoPerfil() != null && !usuario.getFotoPerfil().isEmpty()) {
            CargaIMGS.cargarImagenAsync(usuario.getFotoPerfil(), 100, 100, img -> {
                if (img != null) imgPerfil.setImage(img);
            });
        }

        VBox infoBox = new VBox(8);

        Label lblUsername = new Label("@" + usuario.getUsername());
        lblUsername.setFont(Font.font("Segoe UI", FontWeight.BOLD, 20));

        Label lblNombre = new Label(usuario.getNombreCompleto());
        lblNombre.setFont(Font.font("Segoe UI", FontWeight.NORMAL, 14));

        HBox statsBox = new HBox(15);
        ListaEnlazada<Publicacion> posts = servicio.obtenerTimeline(usuario.getUsername());

        Label lblPosts = new Label(posts.getTamano() + " publicaciones");
        lblPosts.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));

        statsBox.getChildren().addAll(lblPosts);
        infoBox.getChildren().addAll(lblUsername, lblNombre, statsBox);

        header.getChildren().addAll(imgPerfil, infoBox);
        return header;
    }

    private ScrollPane crearGridPublicaciones(UsuarioInsta usuario) {
        TilePane grid = new TilePane();
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPrefColumns(3);

        ListaEnlazada<Publicacion> posts = servicio.obtenerTimeline(usuario.getUsername());

        for (int i = 0; i < posts.getTamano(); i++) {
            Publicacion p = posts.obtener(i);
            ImageView preview = new ImageView();

            // Tamaño duplicado a 240x240
            preview.setFitWidth(240);
            preview.setFitHeight(240);

            if (p.getRutaImagen() != null && !p.getRutaImagen().isEmpty()) {
                CargaIMGS.cargarImagenAsync(p.getRutaImagen(), 240, 240, img -> {
                    if (img != null) preview.setImage(img);
                });
            }

            grid.getChildren().add(preview);
        }

        ScrollPane scroll = new ScrollPane(grid);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        return scroll;
    }
}