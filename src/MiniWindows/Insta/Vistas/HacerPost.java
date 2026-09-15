package MiniWindows.Insta.Vistas;

import MiniWindows.Insta.Servicio.ServicioInsta;
import MiniWindows.Insta.SesionInsta;
import MiniWindows.Modelo.UsuarioInsta;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import java.io.File;

public class HacerPost extends VBox {

    private ServicioInsta servicio;
    private String rutaImagenSeleccionada = "";

    public HacerPost(ServicioInsta servicio) {
        this.servicio = servicio;

        setSpacing(15);
        setPadding(new Insets(20));
        setAlignment(Pos.TOP_CENTER);
        setStyle("-fx-background-color: #fafafa;");

        UsuarioInsta usuario = SesionInsta.getInstancia().getUsuarioActual();

        if (usuario == null) {
            Label lblError = new Label("Inicia sesión para hacer una publicación.");
            lblError.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
            getChildren().add(lblError);
            return;
        }

        Label lblTitulo = new Label("Nueva Publicación");
        lblTitulo.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));

        TextArea txtDescripcion = new TextArea();
        txtDescripcion.setPromptText("Descripción (máx. 140 caracteres)...");
        txtDescripcion.setWrapText(true);
        txtDescripcion.setMaxWidth(400);
        txtDescripcion.setPrefRowCount(4);

        Label lblContador = new Label("0 / 140");
        lblContador.setStyle("-fx-text-fill: #737373; -fx-font-size: 11px;");

        txtDescripcion.textProperty().addListener((obs, oldText, newText) -> {
            if (newText.length() > 140) {
                txtDescripcion.setText(oldText);
            } else {
                lblContador.setText(txtDescripcion.getText().length() + " / 140");
            }
        });

        ComboBox<String> cbModoMobile = new ComboBox<>();
        cbModoMobile.getItems().addAll("CUADRADO", "RETRATO", "PAISAJE");
        cbModoMobile.setValue("CUADRADO");
        cbModoMobile.setMaxWidth(400);

        Button btnSeleccionarFoto = new Button("Seleccionar Imagen");
        btnSeleccionarFoto.setMaxWidth(400);

        Label lblRutaFoto = new Label("Sin imagen seleccionada");
        lblRutaFoto.setStyle("-fx-font-size: 11px; -fx-text-fill: #737373;");

        btnSeleccionarFoto.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Seleccionar imagen a publicar");
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Archivos de Imagen", "*.png", "*.jpg", "*.jpeg")
            );
            File archivo = fileChooser.showOpenDialog(getScene().getWindow());
            if (archivo != null) {
                rutaImagenSeleccionada = archivo.getAbsolutePath();
                lblRutaFoto.setText(archivo.getName());
            }
        });

        Button btnPublicar = new Button("Publicar");
        btnPublicar.setMaxWidth(400);
        btnPublicar.setStyle("-fx-background-color: #0095f6; -fx-text-fill: white; -fx-font-weight: bold;");

        Label lblMensaje = new Label();

        btnPublicar.setOnAction(e -> {
            String desc = txtDescripcion.getText().trim();
            String modo = cbModoMobile.getValue();

            if (desc.isEmpty() && rutaImagenSeleccionada.isEmpty()) {
                lblMensaje.setTextFill(Color.RED);
                lblMensaje.setText("Ingresa texto o selecciona una imagen.");
                return;
            }

            servicio.hacerPost(usuario.getUsername(), desc, rutaImagenSeleccionada, modo);

            lblMensaje.setTextFill(Color.GREEN);
            lblMensaje.setText("¡Publicación finalizada!");

            txtDescripcion.clear();
            rutaImagenSeleccionada = "";
            lblRutaFoto.setText("Sin imagen seleccionada");
        });

        VBox form = new VBox(10, lblTitulo, txtDescripcion, lblContador, cbModoMobile, btnSeleccionarFoto, lblRutaFoto, btnPublicar, lblMensaje);
        form.setAlignment(Pos.CENTER);
        form.setMaxWidth(400);

        getChildren().add(form);
    }
}