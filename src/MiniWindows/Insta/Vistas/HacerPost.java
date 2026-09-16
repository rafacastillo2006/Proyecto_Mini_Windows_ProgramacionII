package MiniWindows.Insta.Vistas;

import MiniWindows.Excepciones.MiniWindowsException;
import MiniWindows.Insta.EstilosInsta;
import MiniWindows.Insta.Imagen.ArteGenerado;
import MiniWindows.Insta.Imagen.ProcesadorImagen;
import MiniWindows.Insta.VentanaInsta;
import MiniWindows.Modelo.Publicacion;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Files;
import java.util.Optional;

public class HacerPost extends VBox {

    private static final int LADO_MAXIMO = 1080;
    private static final double LADO_PREVIA = 230;
    private static final String SIN_CARPETA = "Carpeta /imagenes";

    private final VentanaInsta ventana;
    private final TextArea descripcion = new TextArea();
    private final ComboBox<String> formato = new ComboBox<>();
    private final ComboBox<String> carpeta = new ComboBox<>();
    private final Label contador = EstilosInsta.leyenda("0 / " + Publicacion.LIMITE_DESCRIPCION);
    private final Label aviso = EstilosInsta.error("");
    private final ImageView previa = new ImageView();

    private byte[] imagen;

    public HacerPost(VentanaInsta ventana) {
        this.ventana = ventana;

        setAlignment(Pos.TOP_CENTER);
        setPadding(new Insets(20));
        setSpacing(12);
        setStyle("-fx-background-color: " + EstilosInsta.FONDO + ";");

        descripcion.setPromptText("Descripción, #hashtags y @menciones");
        descripcion.setWrapText(true);
        descripcion.setPrefRowCount(4);
        descripcion.textProperty().addListener((observable, anterior, actual) -> {
            if (actual.length() > Publicacion.LIMITE_DESCRIPCION) {
                descripcion.setText(anterior);
                return;
            }
            contador.setText(actual.length() + " / " + Publicacion.LIMITE_DESCRIPCION);
        });

        formato.getItems().addAll(ProcesadorImagen.CUADRADO, ProcesadorImagen.RETRATO,
                ProcesadorImagen.PAISAJE);
        formato.setValue(ProcesadorImagen.CUADRADO);
        formato.setMaxWidth(Double.MAX_VALUE);

        carpeta.setMaxWidth(Double.MAX_VALUE);
        recargarCarpetas();

        previa.setFitWidth(LADO_PREVIA);
        previa.setFitHeight(LADO_PREVIA);
        previa.setPreserveRatio(true);
        StackPane marcoPrevia = new StackPane(previa);
        marcoPrevia.setPrefSize(LADO_PREVIA, LADO_PREVIA);
        marcoPrevia.setMaxSize(LADO_PREVIA, LADO_PREVIA);
        marcoPrevia.setStyle("-fx-background-color: #efefef; -fx-background-radius: 8;");

        aviso.setMinHeight(28);

        VBox tarjeta = new VBox(10, EstilosInsta.titulo("Cargar imagen", 22), marcoPrevia,
                botonesDeImagen(), descripcion, contador,
                EstilosInsta.leyenda("Formato de la imagen"), formato,
                EstilosInsta.leyenda("Guardar en"), filaCarpeta(),
                botonPublicar(), aviso);
        tarjeta.setAlignment(Pos.CENTER);
        tarjeta.setPadding(new Insets(20));
        tarjeta.setMaxWidth(420);
        tarjeta.setStyle(EstilosInsta.TARJETA);

        getChildren().add(tarjeta);
    }

    private HBox filaCarpeta() {
        Button nueva = EstilosInsta.botonSuave("Nueva");
        nueva.setOnAction(evento -> crearCarpeta());
        HBox fila = new HBox(8, carpeta, nueva);
        HBox.setHgrow(carpeta, javafx.scene.layout.Priority.ALWAYS);
        return fila;
    }

    private void recargarCarpetas() {
        carpeta.getItems().setAll(SIN_CARPETA);
        carpeta.getItems().addAll(ventana.getContexto().getServicio()
                .carpetasPersonalesDe(ventana.getContexto().getUsuarioActual()).aLista());
        carpeta.setValue(SIN_CARPETA);
    }

    private void crearCarpeta() {
        TextInputDialog dialogo = new TextInputDialog("Viajes");
        dialogo.setTitle("Nueva carpeta personal");
        dialogo.setHeaderText(null);
        dialogo.setContentText("Nombre de la carpeta");
        Optional<String> respuesta = dialogo.showAndWait();
        if (respuesta.isEmpty() || respuesta.get().isBlank()) {
            return;
        }
        try {
            ventana.getContexto().getServicio().crearCarpetaPersonal(
                    ventana.getContexto().getUsuarioActual(), respuesta.get().trim());
            recargarCarpetas();
            carpeta.setValue(respuesta.get().trim());
            aviso.setText("");
        } catch (MiniWindowsException error) {
            aviso.setText(error.getMessage());
        }
    }

    private HBox botonesDeImagen() {
        Button desdeDisco = EstilosInsta.botonSuave("Elegir imagen");
        desdeDisco.setOnAction(evento -> elegirImagen());

        Button generar = EstilosInsta.botonSuave("Generar una");
        generar.setOnAction(evento -> {
            imagen = ArteGenerado.publicacion((int) (System.nanoTime() % 97), formato.getValue());
            mostrarPrevia();
        });

        HBox fila = new HBox(8, desdeDisco, generar);
        fila.setAlignment(Pos.CENTER);
        return fila;
    }

    private void elegirImagen() {
        FileChooser selector = new FileChooser();
        selector.setTitle("Imagen de la publicacion");
        selector.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imagenes", "*.png", "*.jpg", "*.jpeg", "*.bmp", "*.gif"));
        File elegido = selector.showOpenDialog(getScene() == null ? null : getScene().getWindow());
        if (elegido == null) {
            return;
        }
        try {
            byte[] ajustada = ProcesadorImagen.ajustar(Files.readAllBytes(elegido.toPath()), LADO_MAXIMO);
            if (ajustada == null) {
                aviso.setText("Ese archivo no es una imagen valida.");
                return;
            }
            imagen = ajustada;
            aviso.setText("");
            mostrarPrevia();
        } catch (Exception error) {
            aviso.setText("No se pudo leer la imagen.");
        }
    }

    private void mostrarPrevia() {
        ventana.getCargador().cargar(imagen, LADO_PREVIA, previa::setImage);
    }

    private Button botonPublicar() {
        Button boton = EstilosInsta.botonPrincipal("Publicar");
        boton.setOnAction(evento -> publicar());
        return boton;
    }

    private void publicar() {
        String texto = descripcion.getText().trim();
        if (texto.isEmpty() && imagen == null) {
            aviso.setText("Escribe algo o elige una imagen.");
            return;
        }
        String destino = SIN_CARPETA.equals(carpeta.getValue()) ? "" : carpeta.getValue();
        try {
            ventana.getContexto().getServicio().publicar(new Publicacion(
                    ventana.getContexto().getUsuarioActual(), texto, imagen, formato.getValue(),
                    destino, java.time.LocalDateTime.now(), 0));
            ventana.mostrarInicio();
        } catch (MiniWindowsException error) {
            aviso.setText(error.getMessage());
        }
    }
}
