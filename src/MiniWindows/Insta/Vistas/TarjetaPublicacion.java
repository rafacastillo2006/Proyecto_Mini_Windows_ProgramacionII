package MiniWindows.Insta.Vistas;

import MiniWindows.Excepciones.MiniWindowsException;
import MiniWindows.Insta.EstilosInsta;
import MiniWindows.Insta.Imagen.ProcesadorImagen;
import MiniWindows.Insta.VentanaInsta;
import MiniWindows.Modelo.Publicacion;
import MiniWindows.Modelo.UsuarioInsta;
import MiniWindows.Util.Fechas;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class TarjetaPublicacion extends VBox {

    private static final double ANCHO = 440;

    private final VentanaInsta ventana;
    private final Publicacion publicacion;
    private final Label contador = EstilosInsta.leyenda("");

    private boolean marcado;

    public TarjetaPublicacion(VentanaInsta ventana, Publicacion publicacion) {
        this.ventana = ventana;
        this.publicacion = publicacion;

        setSpacing(8);
        setMaxWidth(ANCHO);
        setPadding(new Insets(10, 0, 12, 0));
        setStyle(EstilosInsta.TARJETA);

        getChildren().addAll(cabecera(), imagen(), acciones(), contador, cuerpo());
        actualizarContador();
    }

    private HBox cabecera() {
        UsuarioInsta autor = ventana.getContexto().getServicio().perfilDe(publicacion.getAutor());

        Button nombre = EstilosInsta.enlace(publicacion.getAutor() + " escribió:");
        nombre.setOnAction(evento -> ventana.mostrarPerfilDe(publicacion.getAutor()));

        VBox datos = new VBox(0, nombre, EstilosInsta.leyenda(Fechas.formatear(publicacion.getFecha())));
        HBox cabecera = new HBox(10, EstilosInsta.avatar(autor, 34), datos);
        cabecera.setAlignment(Pos.CENTER_LEFT);
        cabecera.setPadding(new Insets(0, 12, 0, 12));
        return cabecera;
    }

    private StackPane imagen() {
        double alto = ANCHO / ProcesadorImagen.proporcionDe(publicacion.getFormato());
        ImageView vista = new ImageView();
        vista.setFitWidth(ANCHO);
        vista.setFitHeight(alto);
        vista.setPreserveRatio(false);
        vista.setSmooth(true);

        StackPane marco = new StackPane(vista);
        marco.setPrefSize(ANCHO, alto);
        marco.setMinHeight(alto);
        marco.setStyle("-fx-background-color: #efefef;");
        marco.setVisible(publicacion.tieneImagen());
        marco.setManaged(publicacion.tieneImagen());
        if (publicacion.tieneImagen()) {
            ventana.getCargador().cargar(publicacion.getImagen(), ANCHO, vista::setImage);
        }
        return marco;
    }

    private HBox acciones() {
        Button meGusta = EstilosInsta.botonSuave("Me gusta");
        meGusta.setOnAction(evento -> alternarMeGusta(meGusta));

        Button responder = EstilosInsta.botonSuave("Mensaje");
        responder.setOnAction(evento -> ventana.abrirConversacionCon(publicacion.getAutor()));

        HBox fila = new HBox(8, meGusta, responder);
        fila.setAlignment(Pos.CENTER_LEFT);
        fila.setPadding(new Insets(2, 12, 0, 12));
        return fila;
    }

    private VBox cuerpo() {
        Label texto = EstilosInsta.texto("\"" + publicacion.getDescripcion() + "\"  —  "
                + Fechas.formatearFecha(publicacion.getFecha()));
        texto.setWrapText(true);
        texto.setMaxWidth(ANCHO - 24);

        VBox cuerpo = new VBox(4, texto);
        if (!publicacion.etiquetas('@').estaVacia() || !publicacion.etiquetas('#').estaVacia()) {
            cuerpo.getChildren().add(etiquetas());
        }
        VBox.setMargin(cuerpo, new Insets(0, 12, 0, 12));
        return cuerpo;
    }

    private HBox etiquetas() {
        HBox fila = new HBox(6);
        for (String mencion : publicacion.etiquetas('@')) {
            Button enlace = EstilosInsta.enlace("@" + mencion);
            enlace.setOnAction(evento -> ventana.mostrarPerfilDe(mencion));
            fila.getChildren().add(enlace);
        }
        for (String hashtag : publicacion.etiquetas('#')) {
            Button enlace = EstilosInsta.enlace("#" + hashtag);
            enlace.setOnAction(evento -> ventana.buscarHashtag(hashtag));
            fila.getChildren().add(enlace);
        }
        return fila;
    }

    private void alternarMeGusta(Button boton) {
        try {
            marcado = !marcado;
            ventana.getContexto().getServicio().darMeGusta(publicacion, marcado);
            boton.setText(marcado ? "Ya no me gusta" : "Me gusta");
            actualizarContador();
        } catch (MiniWindowsException error) {
            contador.setText(error.getMessage());
        }
    }

    private void actualizarContador() {
        contador.setText(publicacion.getMeGusta() + " me gusta");
        VBox.setMargin(contador, new Insets(0, 12, 0, 12));
    }
}
