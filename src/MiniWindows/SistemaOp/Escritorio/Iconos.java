package MiniWindows.SistemaOp.Escritorio;

import MiniWindows.Modelo.TipoArchivo;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public final class Iconos {

    private static final double TAMANO_BASE = 24;
    private static final String CARPETA_RECURSOS = "/MiniWindows/recursos/iconos/";
    private static final Map<String, Image> CACHE = new HashMap<>();

    public static final String INICIO = "M4 4h7v7H4V4zm9 0h7v7h-7V4zM4 13h7v7H4v-7zm9 0h7v7h-7v-7z";
    public static final String CARPETA = "M3 6.5A2.5 2.5 0 0 1 5.5 4h3.2l2 2h7.8A2.5 2.5 0 0 1 21 8.5v9a2.5 2.5 0 0 1-2.5 2.5h-13A2.5 2.5 0 0 1 3 17.5v-11z";
    public static final String DOCUMENTO = "M6 3h8l5 5v12a1 1 0 0 1-1 1H6a1 1 0 0 1-1-1V4a1 1 0 0 1 1-1zm8 1.6V8h3.4L14 4.6z";
    public static final String IMAGEN = "M4 5h16a1 1 0 0 1 1 1v12a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1V6a1 1 0 0 1 1-1zm1.6 12h12.8l-4.6-6-3.4 4.3-2.3-2.7-2.5 4.4zM8.5 11a1.6 1.6 0 1 0 0-3.2 1.6 1.6 0 0 0 0 3.2z";
    public static final String MUSICA = "M18 3.2v10.6a3 3 0 1 1-2-2.83V7.4l-6 1.2v7.2a3 3 0 1 1-2-2.83V6.2l10-3z";
    public static final String ARCHIVO = "M6 3h12a1 1 0 0 1 1 1v16a1 1 0 0 1-1 1H6a1 1 0 0 1-1-1V4a1 1 0 0 1 1-1zm2 4v2h8V7H8zm0 4v2h8v-2H8zm0 4v2h5v-2H8z";
    public static final String USUARIO = "M12 12.4a4.2 4.2 0 1 0 0-8.4 4.2 4.2 0 0 0 0 8.4zM12 14c-4.2 0-7.6 2.2-7.6 5v1h15.2v-1c0-2.8-3.4-5-7.6-5z";
    public static final String CUENTAS = "M12 2.6l7.4 3.2v5.6c0 4.5-3.1 8.7-7.4 9.9-4.3-1.2-7.4-5.4-7.4-9.9V5.8L12 2.6zm0 4.6a2.4 2.4 0 1 0 0 4.8 2.4 2.4 0 0 0 0-4.8zm-4 9.4c.6-1.9 2.2-3 4-3s3.4 1.1 4 3H8z";
    public static final String CERRAR = "M6.4 5L12 10.6 17.6 5 19 6.4 13.4 12 19 17.6 17.6 19 12 13.4 6.4 19 5 17.6 10.6 12 5 6.4 6.4 5z";
    public static final String MINIMIZAR = "M5 11.2h14v1.8H5z";
    public static final String MAXIMIZAR = "M5 5h14v14H5V5zm1.8 1.8v10.4h10.4V6.8H6.8z";
    public static final String RESTAURAR = "M8 3h13v13h-4v4H4V7h4V3zm1.8 1.8V7H17v7.2h2.2V4.8H9.8zM5.8 8.8v9.4h9.4V8.8H5.8z";
    public static final String ATRAS = "M14.8 4.6L7.4 12l7.4 7.4 1.6-1.6L10.6 12l5.8-5.8-1.6-1.6z";
    public static final String ADELANTE = "M9.2 4.6L16.6 12l-7.4 7.4-1.6-1.6L13.4 12 7.6 6.2l1.6-1.6z";
    public static final String ARRIBA = "M12 4l7.4 7.4-1.6 1.6L13 8.2V20h-2V8.2l-4.8 4.8-1.6-1.6L12 4z";
    public static final String ACTUALIZAR = "M12 5V2.4L15.6 6 12 9.6V7a5 5 0 1 0 5 5h2a7 7 0 1 1-7-7z";
    public static final String NUEVA_CARPETA = "M3 6.5A2.5 2.5 0 0 1 5.5 4h3.2l2 2h7.8A2.5 2.5 0 0 1 21 8.5v9a2.5 2.5 0 0 1-2.5 2.5h-13A2.5 2.5 0 0 1 3 17.5v-11zM11 10v2H9v2h2v2h2v-2h2v-2h-2v-2h-2z";
    public static final String NUEVO_DOCUMENTO = "M6 3h8l5 5v12a1 1 0 0 1-1 1H6a1 1 0 0 1-1-1V4a1 1 0 0 1 1-1zm8 1.6V8h3.4L14 4.6zM11 11v2H9v2h2v2h2v-2h2v-2h-2v-2h-2z";
    public static final String RENOMBRAR = "M4 16.6L15.4 5.2l3.4 3.4L7.4 20H4v-3.4zM16.8 3.8l1.6-1.6a1 1 0 0 1 1.4 0l2 2a1 1 0 0 1 0 1.4l-1.6 1.6-3.4-3.4z";
    public static final String COPIAR = "M9 2h9a2 2 0 0 1 2 2v11h-2V4H9V2zM6 6h9a2 2 0 0 1 2 2v12a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2z";
    public static final String CORTAR = "M7.5 2l4.5 7.5L16.5 2H19l-5.6 9.3 1.5 2.5a3.6 3.6 0 1 1-1.7 1L12 12.6l-1.2 2.2a3.6 3.6 0 1 1-1.7-1l1.5-2.5L5 2h2.5zM7.4 16.4a1.6 1.6 0 1 0 0 3.2 1.6 1.6 0 0 0 0-3.2zm9.2 0a1.6 1.6 0 1 0 0 3.2 1.6 1.6 0 0 0 0-3.2z";
    public static final String PEGAR = "M9 2h6a1 1 0 0 1 1 1v1h2a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2h2V3a1 1 0 0 1 1-1zm1 2v1h4V4h-4z";
    public static final String ELIMINAR = "M9 3h6l1 1h4v2H4V4h4l1-1zM6 8h12l-1 12a1 1 0 0 1-1 1H8a1 1 0 0 1-1-1L6 8zm3 2v9h2v-9H9zm4 0v9h2v-9h-2z";
    public static final String BUSCAR = "M10.5 3a7.5 7.5 0 1 1-4.6 13.4l-2.5 2.5-1.4-1.4 2.5-2.5A7.5 7.5 0 0 1 10.5 3zm0 2a5.5 5.5 0 1 0 0 11 5.5 5.5 0 0 0 0-11z";
    public static final String SALIR = "M10 3h7a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-7v-2h7V5h-7V3zm-.6 5.4L12.8 12l-3.4 3.6L8 14.2l1.2-1.2H3v-2h6.2L8 9.8l1.4-1.4z";
    public static final String CONSOLA = "M3 4h18v16H3V4zm2 2v12h14V6H5zm2.4 1.6L11.8 12l-4.4 4.4L6 15l3-3-3-3 1.4-1.4zM13 15h5v2h-5v-2z";
    public static final String PLAY = "M8 5.5v13l11-6.5L8 5.5z";
    public static final String PAUSA = "M6.5 5h4v14h-4V5zm7 0h4v14h-4V5z";
    public static final String DETENER = "M6 6h12v12H6V6z";
    public static final String VOLUMEN = "M4 9h4l5-4v14l-5-4H4V9zm12.6 3a4.5 4.5 0 0 0-2.6-4.1v8.2a4.5 4.5 0 0 0 2.6-4.1z";
    public static final String OJO = "M12 5c5 0 9.3 3.1 11 7-1.7 3.9-6 7-11 7S2.7 15.9 1 12c1.7-3.9 6-7 11-7zm0 2c-3.9 0-7.3 2.2-8.8 5 1.5 2.8 4.9 5 8.8 5s7.3-2.2 8.8-5c-1.5-2.8-4.9-5-8.8-5zm0 1.8a3.2 3.2 0 1 1 0 6.4 3.2 3.2 0 0 1 0-6.4z";
    public static final String OJO_TACHADO = "M3.5 2.1l18.4 18.4-1.4 1.4-3.3-3.3A12 12 0 0 1 12 19c-5 0-9.3-3.1-11-7a12.4 12.4 0 0 1 4.3-5.2L2.1 3.5l1.4-1.4zM12 5c5 0 9.3 3.1 11 7a12.6 12.6 0 0 1-3.2 4.2l-3-3A4 4 0 0 0 11.8 8l-2.4-2.4A12 12 0 0 1 12 5zM6.7 8.1l1.8 1.8a4 4 0 0 0 5.6 5.6l1.4 1.4A6 6 0 0 1 12 18c-3.9 0-7.3-2.2-8.8-5a10.4 10.4 0 0 1 3.5-4.9z";
    public static final String ORGANIZAR = "M3 4h7l2 2h9a1 1 0 0 1 1 1v2H3V4zm0 7h18v9a1 1 0 0 1-1 1H4a1 1 0 0 1-1-1v-9zm3 2v5h3v-5H6zm5 0v5h3v-5h-3zm5 0v5h3v-5h-3z";
    public static final String STICKER = "M13 3.1A9 9 0 1 1 3.1 13H10a3 3 0 0 0 3-3V3.1zM15 3.7A9 9 0 0 1 20.3 9H15V3.7zM8.4 8.4a1.3 1.3 0 1 1 0 2.6 1.3 1.3 0 0 1 0-2.6zm-2.2 5.4h5.6a2.8 2.8 0 0 1-5.6 0z";
    public static final String CORAZON = "M12 20.3l-1.45-1.32C5.4 14.24 2 11.16 2 7.5 2 4.7 4.2 2.5 7 2.5c1.6 0 3.1.74 4 1.9.9-1.16 2.4-1.9 4-1.9 2.8 0 5 2.2 5 5 0 3.66-3.4 6.74-8.55 11.48L12 20.3zm0-2.7c4.5-4.08 7-6.4 7-9.1 0-1.9-1.4-3.3-3-3.3-1.3 0-2.5.83-2.97 2h-2.06C10.5 6.03 9.3 5.2 8 5.2c-1.6 0-3 1.4-3 3.3 0 2.7 2.5 5.02 7 9.1z";
    public static final String CORAZON_LLENO = "M12 20.3l-1.45-1.32C5.4 14.24 2 11.16 2 7.5 2 4.7 4.2 2.5 7 2.5c1.6 0 3.1.74 4 1.9.9-1.16 2.4-1.9 4-1.9 2.8 0 5 2.2 5 5 0 3.66-3.4 6.74-8.55 11.48L12 20.3z";
    public static final String COMENTARIO = "M12 2.6c5.2 0 9.4 3.8 9.4 8.5s-4.2 8.5-9.4 8.5c-1 0-2-.15-2.9-.42L4 21.4l1.1-3.9C3.5 16 2.6 13.9 2.6 11.6c0-4.7 4.2-9 9.4-9zm0 2c-4.1 0-7.4 3.1-7.4 7 0 1.9.8 3.6 2.1 4.8l.5.5-.5 1.7 2-.9.6.2c.86.3 1.77.45 2.7.45 4.1 0 7.4-2.9 7.4-6.6S16.1 4.6 12 4.6z";
    public static final String ENVIAR = "M21.6 2.4L2.9 9.9c-.7.3-.7 1.3 0 1.6l4.9 1.9 1.9 5c.3.7 1.3.7 1.6 0l2.1-4.1 4.5 3.4c.6.4 1.4.1 1.5-.6l2.9-13.5c.2-.8-.6-1.5-1.4-1.2zM8.5 12.2L5.4 11l12.2-4.9-9.1 6.1zm1.6 3.9l-1.2-3.1 8.9-6-6.1 7.3-1.6 1.8z";
    public static final String GUARDAR = "M6 2.5h12a1 1 0 0 1 1 1v17.2a.8.8 0 0 1-1.25.66L12 17.7l-5.75 3.66A.8.8 0 0 1 5 20.7V3.5a1 1 0 0 1 1-1zm1 2v14.1l4.46-2.84a1 1 0 0 1 1.08 0L17 18.6V4.5H7z";
    public static final String VERIFICADO = "M12 2l2.3 2.2 3.1-.3.9 3 2.8 1.4-1.2 2.9 1.2 2.9-2.8 1.4-.9 3-3.1-.3L12 22l-2.3-2.2-3.1.3-.9-3-2.8-1.4 1.2-2.9-1.2-2.9 2.8-1.4.9-3 3.1.3L12 2zm-1.2 13.2l5-5-1.5-1.5-3.5 3.6-1.6-1.7-1.5 1.5 3.1 3.1z";
    public static final String CASA = "M12 2.6l9.4 8.2-1.32 1.5-1.08-.94V20a1 1 0 0 1-1 1h-4.5v-6h-3v6H6a1 1 0 0 1-1-1v-8.64l-1.08.94L2.6 10.8 12 2.6z";
    public static final String MAS_CUADRADO = "M5 3h14a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2zm0 2v14h14V5H5zm6 2h2v4h4v2h-4v4h-2v-4H7v-2h4V7z";
    public static final String ZOOM_MAS = "M10.5 3a7.5 7.5 0 1 1-4.6 13.4l-2.5 2.5-1.4-1.4 2.5-2.5A7.5 7.5 0 0 1 10.5 3zm0 2a5.5 5.5 0 1 0 0 11 5.5 5.5 0 0 0 0-11zm-1 2h2v2.5H14v2h-2.5V14h-2v-2.5H7v-2h2.5V7z";
    public static final String ZOOM_MENOS = "M10.5 3a7.5 7.5 0 1 1-4.6 13.4l-2.5 2.5-1.4-1.4 2.5-2.5A7.5 7.5 0 0 1 10.5 3zm0 2a5.5 5.5 0 1 0 0 11 5.5 5.5 0 0 0 0-11zM7 9.5h7v2H7v-2z";
    public static final String AJUSTAR = "M4 4h6v2H6v4H4V4zm10 0h6v6h-2V6h-4V4zM4 14h2v4h4v2H4v-6zm14 0h2v6h-6v-2h4v-4z";
    public static final String IMPORTAR = "M11 3h2v8.2l2.1-2.1 1.4 1.4L12 15l-4.5-4.5 1.4-1.4L11 11.2V3zM5 17h14v2H5v-2z";
    public static final String TABLA = "M3 4h18v16H3V4zm2 2v3h5V6H5zm7 0v3h7V6h-7zM5 11v3h5v-3H5zm7 0v3h7v-3h-7zM5 16v2h5v-2H5zm7 0v2h7v-2h-7z";
    public static final String RENDIMIENTO = "M3 19h18v2H3v-2zm2-8h3v7H5v-7zm5-5h3v12h-3V6zm5-4h3v16h-3V2z";
    public static final String FINALIZAR = "M7 7h10v10H7V7zm-4-4h18v18H3V3zm2 2v14h14V5H5z";
    public static final String APAGAR = "M11 3h2v9h-2V3zm-3.1 2.3l1.4 1.5a6 6 0 1 0 5.4 0l1.4-1.5a8 8 0 1 1-8.2 0z";

    private Iconos() {
    }

    public static Node crear(String contenido, double tamano, Color color) {
        SVGPath figura = new SVGPath();
        figura.setContent(contenido);
        figura.setFill(color);
        double escala = tamano / TAMANO_BASE;
        figura.setScaleX(escala);
        figura.setScaleY(escala);
        StackPane contenedor = new StackPane(new Group(figura));
        contenedor.setMinSize(tamano, tamano);
        contenedor.setPrefSize(tamano, tamano);
        contenedor.setMaxSize(tamano, tamano);
        contenedor.setMouseTransparent(true);
        return contenedor;
    }

    public static Node crear(String contenido, double tamano) {
        return crear(contenido, tamano, Color.web(Estilos.TEXTO));
    }

    public static Node deTipo(TipoArchivo tipo, double tamano) {
        return imagen(switch (tipo) {
            case CARPETA -> "carpeta";
            case TEXTO -> "documento";
            case IMAGEN -> "imagen";
            case MUSICA -> "musica";
            case GENERICO -> "archivo";
        }, tamano);
    }

    public static Node imagen(String nombre, double tamano) {
        Image dibujo = cargar(nombre);
        if (dibujo == null) {
            return crear(glifoEquivalente(nombre), tamano, Color.web(Estilos.ACENTO));
        }
        ImageView vista = new ImageView(dibujo);
        vista.setFitWidth(tamano);
        vista.setFitHeight(tamano);
        vista.setPreserveRatio(true);
        vista.setSmooth(true);
        vista.setMouseTransparent(true);
        return vista;
    }

    private static Image cargar(String nombre) {
        if (CACHE.containsKey(nombre)) {
            return CACHE.get(nombre);
        }
        Image dibujo = null;
        try (InputStream entrada = Iconos.class.getResourceAsStream(CARPETA_RECURSOS + nombre + ".png")) {
            if (entrada != null) {
                dibujo = new Image(entrada);
            }
        } catch (Exception error) {
            dibujo = null;
        }
        CACHE.put(nombre, dibujo);
        return dibujo;
    }

    private static String glifoEquivalente(String nombre) {
        return switch (nombre) {
            case "explorador", "carpeta" -> CARPETA;
            case "editor", "documento" -> DOCUMENTO;
            case "visor", "imagen" -> IMAGEN;
            case "insta" -> IMAGEN;
            case "reproductor", "musica" -> MUSICA;
            case "consola" -> CONSOLA;
            case "cuentas" -> CUENTAS;
            case "tareas" -> RENDIMIENTO;
            case "inicio" -> INICIO;
            default -> ARCHIVO;
        };
    }
}
