package MiniWindows.Insta.Vistas;

import MiniWindows.Excepciones.MiniWindowsException;
import MiniWindows.Insta.EstilosInsta;
import MiniWindows.Insta.Imagen.ProcesadorImagen;
import MiniWindows.Insta.VentanaInsta;
import MiniWindows.Modelo.Publicacion;
import MiniWindows.Modelo.UsuarioInsta;
import MiniWindows.Red.EventoInsta;
import MiniWindows.SistemaOp.Escritorio.Iconos;
import MiniWindows.Util.Fechas;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.util.Locale;

public class TarjetaPublicacion extends VBox {

    public static final double ANCHO_MAXIMO = 440;

    private final VentanaInsta ventana;
    private final Publicacion publicacion;
    private final Label contador = EstilosInsta.fuerte("");
    private final Label aviso = EstilosInsta.error("");
    private final Button meGusta;

    private boolean marcado;
    private int totalMeGusta;

    public TarjetaPublicacion(VentanaInsta ventana, Publicacion publicacion) {
        this.ventana = ventana;
        this.publicacion = publicacion;
        this.marcado = publicacion.leGustaA(ventana.getContexto().getUsuarioActual());
        this.totalMeGusta = publicacion.getMeGusta();
        this.meGusta = EstilosInsta.botonIcono(Iconos.CORAZON, "Me gusta", 21);

        setSpacing(0);
        setMinWidth(0);
        setPrefWidth(ANCHO_MAXIMO);
        setMaxWidth(ANCHO_MAXIMO);
        setStyle(EstilosInsta.TARJETA);

        getChildren().addAll(cabecera(), imagen(), acciones(), pie());
        pintarMeGusta();
        actualizarContador();
        ventana.alRecibirEvento(this::atender);
    }

    private HBox cabecera() {
        UsuarioInsta autor = ventana.getContexto().getServicio().perfilDe(publicacion.getAutor());

        Button nombre = EstilosInsta.usuario(publicacion.getAutor(),
                autor != null && autor.esVerificada());
        nombre.setOnAction(evento -> ventana.mostrarPerfilDe(publicacion.getAutor()));

        HBox cabecera = new HBox(10, EstilosInsta.avatar(autor, 32), nombre);
        cabecera.setAlignment(Pos.CENTER_LEFT);
        cabecera.setPadding(new Insets(10, 14, 10, 12));
        return cabecera;
    }

    private StackPane imagen() {
        double proporcion = ProcesadorImagen.proporcionDe(publicacion.getFormato());
        ImageView vista = new ImageView();
        vista.setPreserveRatio(false);
        vista.setSmooth(true);

        StackPane marco = new StackPane(vista);
        marco.setMinSize(0, 0);
        marco.setPrefWidth(ANCHO_MAXIMO);
        marco.setStyle("-fx-background-color: " + EstilosInsta.SUAVE + "; "
                + "-fx-border-color: " + EstilosInsta.BORDE + " transparent " + EstilosInsta.BORDE
                + " transparent; -fx-border-width: 1 0 1 0;");
        marco.setVisible(publicacion.tieneImagen());
        marco.setManaged(publicacion.tieneImagen());
        vista.fitWidthProperty().bind(widthProperty());
        vista.fitHeightProperty().bind(widthProperty().divide(proporcion));
        marco.prefHeightProperty().bind(widthProperty().divide(proporcion));
        if (publicacion.tieneImagen()) {
            ventana.getCargador().cargar(publicacion.getImagen(), ANCHO_MAXIMO, vista::setImage);
        }
        return marco;
    }

    private HBox acciones() {
        meGusta.setOnAction(evento -> alternarMeGusta());

        Button comentar = EstilosInsta.botonIcono(Iconos.COMENTARIO,
                "Escribir a " + publicacion.getAutor(), 21);
        comentar.setOnAction(evento -> ventana.abrirConversacionCon(publicacion.getAutor()));

        Button compartir = EstilosInsta.botonIcono(Iconos.ENVIAR, "Ver el perfil del autor", 21);
        compartir.setOnAction(evento -> ventana.mostrarPerfilDe(publicacion.getAutor()));

        HBox fila = new HBox(4, meGusta, comentar, compartir);
        fila.setAlignment(Pos.CENTER_LEFT);
        fila.setPadding(new Insets(6, 8, 0, 6));
        return fila;
    }

    private VBox pie() {
        TextFlow descripcion = new TextFlow();
        descripcion.maxWidthProperty().bind(widthProperty().subtract(28));
        descripcion.setLineSpacing(1);
        descripcion.getChildren().add(trozo(publicacion.getAutor() + " escribió: ", true, null));
        descripcion.getChildren().add(trozo("\"", false, null));

        String[] palabras = publicacion.getDescripcion().split(" ");
        for (int i = 0; i < palabras.length; i++) {
            agregarPalabra(descripcion, palabras[i]);
            if (i < palabras.length - 1) {
                descripcion.getChildren().add(trozo(" ", false, null));
            }
        }
        descripcion.getChildren().add(trozo("\"", false, null));

        VBox pie = new VBox(5, contador, descripcion,
                EstilosInsta.leyenda(Fechas.formatearFecha(publicacion.getFecha())), aviso);
        pie.setPadding(new Insets(6, 14, 14, 14));
        return pie;
    }

    private void agregarPalabra(TextFlow destino, String palabra) {
        char marca = palabra.isEmpty() ? ' ' : palabra.charAt(0);
        if (marca != '@' && marca != '#') {
            destino.getChildren().add(trozo(palabra, false, null));
            return;
        }
        String cuerpo = palabra.substring(1);
        int corte = 0;
        while (corte < cuerpo.length() && esDeEtiqueta(cuerpo.charAt(corte))) {
            corte++;
        }
        String etiqueta = cuerpo.substring(0, corte).toLowerCase(Locale.ROOT);
        if (etiqueta.isEmpty()) {
            destino.getChildren().add(trozo(palabra, false, null));
            return;
        }
        destino.getChildren().add(trozo(marca + cuerpo.substring(0, corte), false,
                marca == '@' ? () -> ventana.mostrarPerfilDe(etiqueta)
                        : () -> ventana.buscarHashtag(etiqueta)));
        if (corte < cuerpo.length()) {
            destino.getChildren().add(trozo(cuerpo.substring(corte), false, null));
        }
    }

    private boolean esDeEtiqueta(char letra) {
        return Character.isLetterOrDigit(letra) || letra == '.' || letra == '_';
    }

    private Text trozo(String contenido, boolean fuerte, Runnable accion) {
        Text texto = new Text(contenido);
        texto.setStyle("-fx-font-family: '" + EstilosInsta.FUENTE + "'; -fx-font-size: 13.5px; "
                + (fuerte ? "-fx-font-weight: bold; " : "")
                + "-fx-fill: " + (accion == null ? EstilosInsta.TEXTO : EstilosInsta.AZUL) + ";");
        if (accion != null) {
            texto.setCursor(Cursor.HAND);
            texto.setOnMouseClicked(evento -> accion.run());
        }
        return texto;
    }

    private void alternarMeGusta() {
        String yo = ventana.getContexto().getUsuarioActual();
        try {
            ventana.getContexto().getServicio().darMeGusta(publicacion, yo, !marcado);
            aviso.setText("");
            releer();
            ventana.publicarEvento(EventoInsta.meGusta(yo, publicacion.getAutor(), publicacion.clave()));
        } catch (MiniWindowsException error) {
            aviso.setText(error.getMessage());
        }
    }

    private void atender(EventoInsta evento) {
        if (evento.es(EventoInsta.ME_GUSTA) && publicacion.clave().equals(evento.referencia())) {
            releer();
        }
    }

    public void releer() {
        Publicacion actual = ventana.getContexto().getServicio()
                .publicacionesDe(publicacion.getAutor()).buscar(publicacion::esLaMisma);
        if (actual == null) {
            return;
        }
        totalMeGusta = actual.getMeGusta();
        marcado = actual.leGustaA(ventana.getContexto().getUsuarioActual());
        pintarMeGusta();
        actualizarContador();
    }

    private void pintarMeGusta() {
        meGusta.setGraphic(Iconos.crear(marcado ? Iconos.CORAZON_LLENO : Iconos.CORAZON, 21,
                Color.web(marcado ? EstilosInsta.PELIGRO : EstilosInsta.TEXTO)));
    }

    private void actualizarContador() {
        contador.setText(totalMeGusta == 1 ? "1 me gusta" : totalMeGusta + " me gusta");
    }
}
