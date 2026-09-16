package MiniWindows.Insta.Vistas;

import MiniWindows.Insta.EstilosInsta;
import MiniWindows.Insta.VentanaInsta;
import MiniWindows.Modelo.UsuarioInsta;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.function.BiConsumer;

public final class FilaUsuario {

    private FilaUsuario() {
    }

    public static HBox crear(VentanaInsta ventana, UsuarioInsta persona,
                             BiConsumer<UsuarioInsta, Button> alPulsar) {
        return armar(ventana, persona, alPulsar, false, false);
    }

    public static HBox crear(VentanaInsta ventana, UsuarioInsta persona,
                             BiConsumer<UsuarioInsta, Button> alPulsar, boolean compacta) {
        return armar(ventana, persona, alPulsar, compacta, false);
    }

    public static HBox conEstado(VentanaInsta ventana, UsuarioInsta persona,
                                 BiConsumer<UsuarioInsta, Button> alPulsar) {
        return armar(ventana, persona, alPulsar, false, true);
    }

    private static HBox armar(VentanaInsta ventana, UsuarioInsta persona,
                              BiConsumer<UsuarioInsta, Button> alPulsar, boolean compacta,
                              boolean conEstado) {
        String yo = ventana.getContexto().getUsuarioActual();
        boolean siguiendo = ventana.getContexto().getServicio().sigue(yo, persona.getUsername());

        Button nombre = EstilosInsta.usuario(persona.getUsername(), persona.esVerificada());
        nombre.setOnAction(evento -> ventana.mostrarPerfilDe(persona.getUsername()));

        VBox datos = new VBox(0, nombre, EstilosInsta.leyenda(persona.getNombreCompleto()));
        datos.setAlignment(Pos.CENTER_LEFT);
        if (conEstado) {
            Label estado = EstilosInsta.leyenda(siguiendo ? "Lo sigo" : "No lo sigues");
            estado.setPadding(new Insets(2, 0, 0, 0));
            datos.getChildren().add(estado);
        }

        Button accion;
        if (compacta) {
            accion = EstilosInsta.enlace(siguiendo ? "Siguiendo" : "Seguir");
        } else {
            accion = siguiendo ? EstilosInsta.botonSuave("Siguiendo")
                    : EstilosInsta.botonPrincipal("Seguir");
            accion.setMaxWidth(104);
            accion.setMinWidth(104);
        }
        accion.setOnAction(evento -> alPulsar.accept(persona, accion));

        HBox fila = new HBox(12, EstilosInsta.avatar(persona, compacta ? 36 : 44), datos,
                EstilosInsta.espaciador(), accion);
        fila.setAlignment(Pos.CENTER_LEFT);
        fila.setPadding(new Insets(compacta ? 5 : 8, 10, compacta ? 5 : 8, 10));
        return fila;
    }
}
