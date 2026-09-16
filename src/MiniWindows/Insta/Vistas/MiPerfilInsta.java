package MiniWindows.Insta.Vistas;

import MiniWindows.Estructuras.ListaEnlazada;
import MiniWindows.Excepciones.MiniWindowsException;
import MiniWindows.Insta.EstilosInsta;
import MiniWindows.Insta.VentanaInsta;
import MiniWindows.Modelo.Publicacion;
import MiniWindows.Modelo.UsuarioInsta;
import MiniWindows.Util.Fechas;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MiPerfilInsta extends ScrollPane {

    private static final double LADO_MINIATURA = 130;
    private static final int COLUMNAS = 3;

    private final VentanaInsta ventana;
    private final UsuarioInsta perfil;
    private final Label aviso = EstilosInsta.leyenda("");

    public MiPerfilInsta(VentanaInsta ventana) {
        this(ventana, ventana.getContexto().getUsuarioActual());
    }

    public MiPerfilInsta(VentanaInsta ventana, String username) {
        this.ventana = ventana;
        this.perfil = ventana.getContexto().getServicio().perfilDe(username);

        VBox columna = new VBox(16);
        columna.setPadding(new Insets(24));
        columna.setAlignment(Pos.TOP_CENTER);
        columna.setStyle("-fx-background-color: " + EstilosInsta.FONDO + ";");

        if (perfil == null) {
            columna.getChildren().add(EstilosInsta.error("Ese perfil no existe o esta desactivado."));
        } else {
            columna.getChildren().addAll(cabecera(), aviso, galeria());
        }

        setContent(columna);
        setFitToWidth(true);
        setStyle("-fx-background: " + EstilosInsta.FONDO + "; -fx-background-color: "
                + EstilosInsta.FONDO + "; -fx-border-color: transparent;");
    }

    private boolean esPropio() {
        return perfil.getUsername().equalsIgnoreCase(ventana.getContexto().getUsuarioActual());
    }

    private VBox cabecera() {
        Label arroba = EstilosInsta.titulo("@" + perfil.getUsername()
                + (perfil.esVerificada() ? "  ✓" : ""), 22);

        GridPane datos = new GridPane();
        datos.setHgap(14);
        datos.setVgap(4);
        datos.addRow(0, EstilosInsta.leyenda("Nombre"), EstilosInsta.texto(perfil.getNombreCompleto()));
        datos.addRow(1, EstilosInsta.leyenda("Edad"), EstilosInsta.texto(perfil.getEdad() + " años"));
        datos.addRow(2, EstilosInsta.leyenda("Género"),
                EstilosInsta.texto(perfil.getGenero() == 'F' ? "Femenino" : "Masculino"));
        datos.addRow(3, EstilosInsta.leyenda("Registro"),
                EstilosInsta.texto(Fechas.FECHA.format(perfil.getFechaRegistro())));
        datos.addRow(4, EstilosInsta.leyenda("Estado"),
                EstilosInsta.texto(perfil.estaActiva() ? "Cuenta activa" : "Cuenta desactivada"));

        HBox numeros = new HBox(18,
                dato(ventana.getContexto().getServicio().publicacionesDe(perfil.getUsername()).tamano(),
                        "publicaciones"),
                dato(ventana.getContexto().getServicio().seguidoresDe(perfil.getUsername()).tamano(),
                        "followers"),
                dato(ventana.getContexto().getServicio().seguidosDe(perfil.getUsername()).tamano(),
                        "following"));
        numeros.setAlignment(Pos.CENTER_LEFT);

        Label biografia = EstilosInsta.texto(perfil.getBiografia());
        biografia.setWrapText(true);
        biografia.setMaxWidth(340);

        VBox bloque = new VBox(10, arroba, datos, numeros, biografia, acciones());
        bloque.setAlignment(Pos.CENTER_LEFT);

        HBox fila = new HBox(24, EstilosInsta.avatar(perfil, 120), bloque);
        fila.setAlignment(Pos.CENTER_LEFT);

        VBox tarjeta = new VBox(fila);
        tarjeta.setPadding(new Insets(20));
        tarjeta.setMaxWidth(640);
        tarjeta.setStyle(EstilosInsta.TARJETA);
        return tarjeta;
    }

    private HBox acciones() {
        if (esPropio()) {
            Button editar = EstilosInsta.botonSuave("Editar perfil");
            editar.setOnAction(evento -> ventana.mostrarEditarPerfil());
            return new HBox(8, editar);
        }
        String yo = ventana.getContexto().getUsuarioActual();
        boolean siguiendo = ventana.getContexto().getServicio().sigue(yo, perfil.getUsername());

        Button seguir = siguiendo ? EstilosInsta.botonSuave("Dejar de seguir")
                : EstilosInsta.botonPrincipal("Seguir");
        seguir.setMaxWidth(140);
        seguir.setOnAction(evento -> alternarSeguir(siguiendo));

        Button mensaje = EstilosInsta.botonSuave("Enviar mensaje");
        mensaje.setOnAction(evento -> ventana.abrirConversacionCon(perfil.getUsername()));

        return new HBox(8, seguir, mensaje);
    }

    private void alternarSeguir(boolean siguiendo) {
        String yo = ventana.getContexto().getUsuarioActual();
        try {
            if (siguiendo) {
                if (!ventana.confirmar("Dejar de seguir",
                        "¿Seguro que quieres dejar de seguir a @" + perfil.getUsername() + "?")) {
                    return;
                }
                ventana.getContexto().getServicio().dejarDeSeguir(yo, perfil.getUsername());
            } else {
                ventana.getContexto().getServicio().seguir(yo, perfil.getUsername());
            }
            ventana.mostrarPerfilDe(perfil.getUsername());
        } catch (MiniWindowsException error) {
            aviso.setText(error.getMessage());
        }
    }

    private VBox dato(int cantidad, String etiqueta) {
        Label numero = EstilosInsta.texto(String.valueOf(cantidad));
        numero.setStyle(numero.getStyle() + " -fx-font-weight: bold;");
        VBox caja = new VBox(0, numero, EstilosInsta.leyenda(etiqueta));
        caja.setAlignment(Pos.CENTER);
        return caja;
    }

    private VBox galeria() {
        ListaEnlazada<Publicacion> propias =
                ventana.getContexto().getServicio().publicacionesDe(perfil.getUsername());

        GridPane rejilla = new GridPane();
        rejilla.setHgap(6);
        rejilla.setVgap(6);
        rejilla.setAlignment(Pos.CENTER);
        for (int i = 0; i < propias.tamano(); i++) {
            rejilla.add(miniatura(propias.obtener(i)), i % COLUMNAS, i / COLUMNAS);
        }

        VBox tarjeta = new VBox(12, EstilosInsta.leyenda("Publicaciones"),
                propias.estaVacia() ? EstilosInsta.leyenda("Todavia no hay publicaciones.") : rejilla);
        tarjeta.setPadding(new Insets(20));
        tarjeta.setMaxWidth(640);
        tarjeta.setStyle(EstilosInsta.TARJETA);
        return tarjeta;
    }

    private StackPane miniatura(Publicacion publicacion) {
        ImageView vista = new ImageView();
        vista.setFitWidth(LADO_MINIATURA);
        vista.setFitHeight(LADO_MINIATURA);
        vista.setPreserveRatio(false);
        vista.setSmooth(true);

        StackPane marco = new StackPane(vista);
        marco.setPrefSize(LADO_MINIATURA, LADO_MINIATURA);
        marco.setStyle("-fx-background-color: #efefef; -fx-background-radius: 6;");
        if (publicacion.tieneImagen()) {
            ventana.getCargador().cargar(publicacion.getImagen(), LADO_MINIATURA, vista::setImage);
        } else {
            Label texto = EstilosInsta.leyenda(publicacion.getDescripcion());
            texto.setWrapText(true);
            texto.setMaxWidth(LADO_MINIATURA - 16);
            marco.getChildren().add(texto);
        }
        return marco;
    }
}
