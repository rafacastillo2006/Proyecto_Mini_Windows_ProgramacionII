package MiniWindows.Insta.Vistas;

import MiniWindows.Estructuras.ListaEnlazada;
import MiniWindows.Insta.Servicio.ServicioInsta;
import MiniWindows.Insta.SesionInsta;
import MiniWindows.Insta.Hilos.Notificaciones;
import MiniWindows.Modelo.Mensaje;
import MiniWindows.Modelo.UsuarioInsta;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import java.io.File;

public class BandejaEntrada extends VBox {

    private ServicioInsta servicio;
    private UsuarioInsta usuarioActual;
    private String destinatarioActual = "";
    private VBox chatArea;
    private Notificaciones hiloNotificaciones;

    public BandejaEntrada(ServicioInsta servicio) {
        this.servicio = servicio;
        this.usuarioActual = SesionInsta.getInstancia().getUsuarioActual();

        setSpacing(10);
        setPadding(new Insets(15));
        setAlignment(Pos.TOP_CENTER);
        setStyle("-fx-background-color: #fafafa;");

        if (usuarioActual == null) {
            Label lblError = new Label("Debe iniciar sesión para acceder al Inbox.");
            lblError.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
            getChildren().add(lblError);
            return;
        }

        inicializarInterfaz();
        iniciarNotificaciones();
    }

    private void inicializarInterfaz() {
        HBox contenedorPrincipal = new HBox(15);
        contenedorPrincipal.setPrefHeight(450);

        VBox panelDestinatario = new VBox(8);
        panelDestinatario.setPrefWidth(150);
        panelDestinatario.setPadding(new Insets(10));
        panelDestinatario.setStyle("-fx-background-color: #ffffff; -fx-border-color: #dbdbdb;");

        Label lblDestino = new Label("Bandeja de Entrada");
        lblDestino.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));

        TextField txtDestinatario = new TextField();
        txtDestinatario.setPromptText("@usuario");

        Button btnAbrirChat = new Button("Abrir Chat");
        btnAbrirChat.setMaxWidth(Double.MAX_VALUE);
        btnAbrirChat.setStyle("-fx-background-color: #efefef;");

        panelDestinatario.getChildren().addAll(lblDestino, txtDestinatario, btnAbrirChat);

        VBox panelChat = new VBox(10);
        panelChat.setPrefWidth(300);
        HBox.setHgrow(panelChat, Priority.ALWAYS);

        chatArea = new VBox(8);
        chatArea.setPadding(new Insets(10));

        ScrollPane scrollChat = new ScrollPane(chatArea);
        scrollChat.setFitToWidth(true);
        scrollChat.setPrefHeight(350);
        scrollChat.setStyle("-fx-background-color: #ffffff; -fx-border-color: #dbdbdb;");

        HBox cajaEnvio = new HBox(8);
        cajaEnvio.setAlignment(Pos.CENTER);

        TextField txtMensaje = new TextField();
        txtMensaje.setPromptText("Escribe un mensaje...");
        HBox.setHgrow(txtMensaje, Priority.ALWAYS);

        Button btnSticker = new Button("📁");
        btnSticker.setTooltip(new Tooltip("Enviar Sticker"));

        Button btnEnviar = new Button("Enviar");
        btnEnviar.setStyle("-fx-background-color: #0095f6; -fx-text-fill: white; -fx-font-weight: bold;");

        cajaEnvio.getChildren().addAll(txtMensaje, btnSticker, btnEnviar);
        panelChat.getChildren().addAll(scrollChat, cajaEnvio);

        contenedorPrincipal.getChildren().addAll(panelDestinatario, panelChat);
        getChildren().add(contenedorPrincipal);

        btnAbrirChat.setOnAction(e -> {
            destinatarioActual = txtDestinatario.getText().trim();
            cargarChat();
        });

        btnEnviar.setOnAction(e -> {
            String texto = txtMensaje.getText().trim();
            if (!destinatarioActual.isEmpty() && !texto.isEmpty()) {
                servicio.enviarMensaje(usuarioActual.getUsername(), destinatarioActual, texto, "TEXTO");
                txtMensaje.clear();
                cargarChat();
            }
        });

        btnSticker.setOnAction(e -> {
            if (destinatarioActual.isEmpty()) return;
            FileChooser fc = new FileChooser();
            fc.setTitle("Seleccionar Sticker");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg"));
            File f = fc.showOpenDialog(getScene().getWindow());
            if (f != null) {
                servicio.enviarMensaje(usuarioActual.getUsername(), destinatarioActual, f.getAbsolutePath(), "STICKER");
                cargarChat();
            }
        });
    }

    private void cargarChat() {
        chatArea.getChildren().clear();
        if (destinatarioActual.isEmpty()) return;

        ListaEnlazada<Mensaje> conversacion = servicio.obtenerConversacion(usuarioActual.getUsername(), destinatarioActual);

        for (int i = 0; i < conversacion.getTamano(); i++) {
            Mensaje m = conversacion.obtener(i);
            boolean esMio = m.getEmisor().equalsIgnoreCase(usuarioActual.getUsername());

            Label lblMsg = new Label((esMio ? "Tú: " : "@" + m.getEmisor() + ": ") + m.getContenido());
            lblMsg.setWrapText(true);
            lblMsg.setMaxWidth(220);
            lblMsg.setPadding(new Insets(6, 10, 6, 10));

            HBox fila = new HBox(lblMsg);
            if (esMio) {
                fila.setAlignment(Pos.CENTER_RIGHT);
                lblMsg.setStyle("-fx-background-color: #efefef; -fx-background-radius: 10;");
            } else {
                fila.setAlignment(Pos.CENTER_LEFT);
                lblMsg.setStyle("-fx-background-color: #0095f6; -fx-text-fill: white; -fx-background-radius: 10;");
            }

            chatArea.getChildren().add(fila);
        }
    }

    private void iniciarNotificaciones() {
        hiloNotificaciones = new Notificaciones(usuarioActual.getUsername(), mensajeNuevo -> {
            if (mensajeNuevo.getEmisor().equalsIgnoreCase(destinatarioActual)) {
                cargarChat();
            }
        });
        hiloNotificaciones.start();
    }
}