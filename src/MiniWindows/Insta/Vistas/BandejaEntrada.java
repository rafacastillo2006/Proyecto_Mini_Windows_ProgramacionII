package MiniWindows.Insta.Vistas;

import MiniWindows.Estructuras.ListaEnlazada;
import MiniWindows.Excepciones.MiniWindowsException;
import MiniWindows.Insta.EstilosInsta;
import MiniWindows.Insta.Hilos.Notificaciones;
import MiniWindows.Insta.Imagen.Sticker;
import MiniWindows.Insta.VentanaInsta;
import MiniWindows.Modelo.Mensaje;
import MiniWindows.Modelo.UsuarioInsta;
import MiniWindows.Util.Fechas;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.nio.file.Files;

public class BandejaEntrada extends BorderPane {

    private static final double LADO_STICKER = 96;

    private final VentanaInsta ventana;
    private final ListView<String> contactos = new ListView<>();
    private final VBox mensajes = new VBox(6);
    private final TextField entrada = new TextField();
    private final Label aviso = EstilosInsta.leyenda("");
    private final FlowPane galeriaStickers = new FlowPane(8, 8);
    private final ScrollPane marco = new ScrollPane(mensajes);

    private Notificaciones vigilante;
    private String conversando;

    public BandejaEntrada(VentanaInsta ventana, String contactoInicial) {
        this.ventana = ventana;

        setPadding(new Insets(16));
        setStyle(EstilosInsta.PAGINA);

        contactos.setPrefWidth(190);
        contactos.setPlaceholder(EstilosInsta.leyenda("Sin conversaciones"));
        contactos.setStyle("-fx-font-family: '" + EstilosInsta.FUENTE + "'; -fx-font-size: 13px; "
                + "-fx-background-color: " + EstilosInsta.SUPERFICIE + "; -fx-background-radius: 8; "
                + "-fx-border-color: " + EstilosInsta.BORDE + "; -fx-border-radius: 8;");
        contactos.getSelectionModel().selectedItemProperty().addListener(
                (observable, anterior, actual) -> abrirConversacion(actual));

        marco.setFitToWidth(true);
        marco.setStyle("-fx-background: " + EstilosInsta.SUPERFICIE + "; -fx-background-color: "
                + EstilosInsta.SUPERFICIE + "; -fx-border-color: transparent;");
        mensajes.setPadding(new Insets(12));

        entrada.setPromptText("Escribe un mensaje (máximo " + Mensaje.LIMITE_CARACTERES + ")");
        entrada.setStyle(EstilosInsta.CAMPO);
        entrada.setOnAction(evento -> enviarTexto());
        entrada.textProperty().addListener((observable, anterior, actual) -> {
            if (actual.length() > Mensaje.LIMITE_CARACTERES) {
                entrada.setText(anterior);
            }
        });

        Button enviar = EstilosInsta.botonPrincipal("Enviar");
        enviar.setMaxWidth(90);
        enviar.setOnAction(evento -> enviarTexto());

        HBox pie = new HBox(8, entrada, enviar);
        HBox.setHgrow(entrada, Priority.ALWAYS);
        pie.setPadding(new Insets(8, 0, 0, 0));

        galeriaStickers.setVisible(false);
        galeriaStickers.setManaged(false);
        galeriaStickers.setPadding(new Insets(8, 0, 0, 0));

        VBox derecha = new VBox(6, barraConversacion(), marco, galeriaStickers, aviso, pie);
        VBox.setVgrow(marco, Priority.ALWAYS);
        derecha.setPadding(new Insets(0, 0, 0, 14));

        setLeft(new VBox(8, EstilosInsta.titulo("Inbox", 20), nuevaConversacion(), contactos));
        setCenter(derecha);

        cargarContactos();
        if (contactoInicial != null && !contactoInicial.isBlank()) {
            abrirCon(contactoInicial);
        }
        vigilar();
    }

    private HBox barraConversacion() {
        Button stickers = EstilosInsta.botonSuave("Enviar sticker");
        stickers.setOnAction(evento -> alternarStickers());

        Button importar = EstilosInsta.botonSuave("Importar sticker");
        importar.setOnAction(evento -> importarSticker());

        Button eliminar = EstilosInsta.botonSuave("Eliminar conversación");
        eliminar.setOnAction(evento -> eliminarConversacion());

        HBox barra = new HBox(8, stickers, importar, EstilosInsta.espaciador(), eliminar);
        barra.setAlignment(Pos.CENTER_LEFT);
        return barra;
    }

    private HBox nuevaConversacion() {
        TextField destino = new TextField();
        destino.setPromptText("@usuario");
        destino.setStyle(EstilosInsta.CAMPO);
        Button abrir = EstilosInsta.botonSuave("Abrir");
        abrir.setOnAction(evento -> abrirCon(destino.getText()));
        destino.setOnAction(evento -> abrirCon(destino.getText()));

        HBox fila = new HBox(6, destino, abrir);
        HBox.setHgrow(destino, Priority.ALWAYS);
        return fila;
    }

    private void abrirCon(String texto) {
        String nombre = texto == null ? "" : texto.trim().replace("@", "");
        UsuarioInsta existe = ventana.getContexto().getServicio().perfilDe(nombre);
        if (existe == null) {
            aviso.setText("No existe @" + nombre);
            return;
        }
        if (existe.getUsername().equalsIgnoreCase(ventana.getContexto().getUsuarioActual())) {
            aviso.setText("No puedes escribirte a ti mismo.");
            return;
        }
        if (!existe.estaActiva()) {
            aviso.setText("La cuenta @" + existe.getUsername() + " está desactivada.");
            return;
        }
        aviso.setText("");
        if (!contactos.getItems().contains(existe.getUsername())) {
            contactos.getItems().add(existe.getUsername());
        }
        contactos.getSelectionModel().select(existe.getUsername());
    }

    private void cargarContactos() {
        String seleccion = contactos.getSelectionModel().getSelectedItem();
        contactos.getItems().setAll(
                ventana.getContexto().getServicio()
                        .contactosDe(ventana.getContexto().getUsuarioActual()).aLista());
        if (seleccion != null && contactos.getItems().contains(seleccion)) {
            contactos.getSelectionModel().select(seleccion);
        } else if (!contactos.getItems().isEmpty()) {
            contactos.getSelectionModel().selectFirst();
        }
    }

    private void abrirConversacion(String otro) {
        conversando = otro;
        mensajes.getChildren().clear();
        if (otro == null) {
            return;
        }
        String yo = ventana.getContexto().getUsuarioActual();
        for (Mensaje mensaje : ventana.getContexto().getServicio().conversacion(yo, otro)) {
            mensajes.getChildren().add(burbuja(mensaje, mensaje.getEmisor().equalsIgnoreCase(yo)));
        }
        try {
            ventana.getContexto().getServicio().marcarConversacionLeida(yo, otro);
        } catch (MiniWindowsException error) {
            aviso.setText(error.getMessage());
        }
        javafx.application.Platform.runLater(() -> marco.setVvalue(1));
    }

    private VBox burbuja(Mensaje mensaje, boolean propio) {
        VBox caja = new VBox(2);
        caja.setAlignment(propio ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);
        caja.setMaxWidth(Double.MAX_VALUE);

        if (mensaje.esSticker() && mensaje.getSticker() != null) {
            ImageView vista = new ImageView();
            vista.setFitWidth(LADO_STICKER);
            vista.setFitHeight(LADO_STICKER);
            vista.setPreserveRatio(true);
            ventana.getCargador().cargar(mensaje.getSticker(), LADO_STICKER, vista::setImage);
            caja.getChildren().add(vista);
        } else {
            Label texto = new Label(mensaje.getContenido());
            texto.setMnemonicParsing(false);
            texto.setWrapText(true);
            texto.setMaxWidth(320);
            texto.setPadding(new Insets(8, 13, 8, 13));
            texto.setStyle("-fx-font-family: '" + EstilosInsta.FUENTE + "'; -fx-font-size: 13.5px; "
                    + "-fx-background-radius: 16; -fx-text-fill: "
                    + (propio ? "white" : EstilosInsta.TEXTO) + "; -fx-background-color: "
                    + (propio ? EstilosInsta.AZUL : EstilosInsta.SUAVE) + ";");
            caja.getChildren().add(texto);
        }

        String marca = Fechas.formatearHora(mensaje.getFecha())
                + (propio ? "" : mensaje.estaLeido() ? "  ·  leído" : "  ·  nuevo");
        caja.getChildren().add(EstilosInsta.leyenda(marca));
        return caja;
    }

    private void alternarStickers() {
        boolean visible = galeriaStickers.isVisible();
        if (!visible) {
            galeriaStickers.getChildren().clear();
            ListaEnlazada<Sticker> disponibles = ventana.getContexto().getServicio()
                    .stickersDe(ventana.getContexto().getUsuarioActual());
            for (Sticker sticker : disponibles) {
                galeriaStickers.getChildren().add(botonSticker(sticker));
            }
        }
        galeriaStickers.setVisible(!visible);
        galeriaStickers.setManaged(!visible);
    }

    private Button botonSticker(Sticker sticker) {
        ImageView vista = new ImageView();
        vista.setFitWidth(56);
        vista.setFitHeight(56);
        vista.setPreserveRatio(true);
        ventana.getCargador().cargar(sticker.datos(), 56, vista::setImage);

        Button boton = new Button();
        boton.setGraphic(vista);
        boton.setTooltip(new javafx.scene.control.Tooltip(sticker.nombre()));
        boton.setStyle("-fx-background-color: white; -fx-border-color: " + EstilosInsta.BORDE
                + "; -fx-background-radius: 8; -fx-border-radius: 8; -fx-cursor: hand;");
        boton.setOnAction(evento -> enviarSticker(sticker));
        return boton;
    }

    private void importarSticker() {
        FileChooser selector = new FileChooser();
        selector.setTitle("Importar sticker");
        selector.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg"));
        File elegido = selector.showOpenDialog(getScene() == null ? null : getScene().getWindow());
        if (elegido == null) {
            return;
        }
        try {
            ventana.getContexto().getServicio().agregarSticker(
                    ventana.getContexto().getUsuarioActual(), elegido.getName(),
                    Files.readAllBytes(elegido.toPath()));
            aviso.setText("Sticker agregado.");
            if (galeriaStickers.isVisible()) {
                alternarStickers();
                alternarStickers();
            }
        } catch (MiniWindowsException error) {
            aviso.setText(error.getMessage());
        } catch (Exception error) {
            aviso.setText("No se pudo leer la imagen.");
        }
    }

    private void enviarSticker(Sticker sticker) {
        if (conversando == null) {
            aviso.setText("Elige primero una conversación.");
            return;
        }
        enviar(new Mensaje(ventana.getContexto().getUsuarioActual(), conversando,
                sticker.nombre(), Mensaje.STICKER, sticker.datos()));
    }

    private void enviarTexto() {
        String texto = entrada.getText().trim();
        if (texto.isEmpty() || conversando == null) {
            return;
        }
        enviar(new Mensaje(ventana.getContexto().getUsuarioActual(), conversando, texto));
        entrada.clear();
    }

    private void enviar(Mensaje mensaje) {
        try {
            ventana.getContexto().getServicio().enviarMensaje(mensaje);
            abrirConversacion(conversando);
            aviso.setText("");
        } catch (MiniWindowsException error) {
            aviso.setText(error.getMessage());
        }
    }

    private void eliminarConversacion() {
        if (conversando == null) {
            return;
        }
        if (!ventana.confirmar("Eliminar conversación",
                "Se borrará toda la conversación con @" + conversando + ". ¿Continuar?")) {
            return;
        }
        try {
            ventana.getContexto().getServicio().eliminarConversacion(
                    ventana.getContexto().getUsuarioActual(), conversando);
            conversando = null;
            mensajes.getChildren().clear();
            cargarContactos();
        } catch (MiniWindowsException error) {
            aviso.setText(error.getMessage());
        }
    }

    private void vigilar() {
        sceneProperty().addListener((observable, anterior, actual) -> {
            if (actual == null) {
                if (vigilante != null) {
                    vigilante.detener();
                    vigilante = null;
                }
            } else if (vigilante == null) {
                vigilante = new Notificaciones(ventana.getContexto().getServicio(),
                        ventana.getContexto().getUsuarioActual(), this::avisar);
                vigilante.start();
            }
        });
    }

    private void avisar(Mensaje mensaje) {
        aviso.setText("Nuevo mensaje de @" + mensaje.getEmisor());
        cargarContactos();
        if (mensaje.getEmisor().equalsIgnoreCase(conversando)) {
            abrirConversacion(conversando);
        }
    }
}
