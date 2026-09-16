package MiniWindows.Util;

import MiniWindows.Estructuras.ListaEnlazada;
import MiniWindows.Excepciones.MiniWindowsException;
import MiniWindows.Insta.Imagen.ArteGenerado;
import MiniWindows.Insta.Imagen.ProcesadorImagen;
import MiniWindows.Insta.Servicio.ServicioInsta;
import MiniWindows.Modelo.Mensaje;
import MiniWindows.Modelo.Publicacion;
import MiniWindows.Modelo.UsuarioInsta;

import java.time.LocalDateTime;

public final class InicializadorInsta {

    private static final String CLAVE_DEMO = "Insta#2026";

    private static final String[][] FAMOSOS = {
            {"lucia.travel", "Lucia Fernandez", "F", "27", "Recorriendo el mundo con una mochila"},
            {"chef_marco", "Marco Ruiz", "M", "34", "Cocina casera sin complicaciones"},
            {"mirador_espacial", "Mirador Espacial", "M", "31", "Astronomía para todos"},
            {"ritmo_catracho", "Ritmo Catracho", "F", "26", "La música de Honduras en un solo lugar"},
            {"depor_total", "Deportes Total", "M", "33", "Resultados y análisis cada semana"},
            {"tec_al_dia", "Tec al Dia", "M", "24", "Tecnología explicada fácil"},
            {"ana.fitness", "Ana Mejia", "F", "29", "Entrenamientos cortos para gente ocupada"},
            {"noticias_360", "Noticias 360", "F", "30", "Información verificada las 24 horas"}
    };

    private static final String[][] PUBLICACIONES = {
            {"lucia.travel", "Amanecer en la isla, valió la pena madrugar #viajes #honduras",
                    ProcesadorImagen.CUADRADO, ArteGenerado.COSTA},
            {"lucia.travel", "Tres días de ruta y todavía me sobran ganas #viajes #mochilero",
                    ProcesadorImagen.RETRATO, ArteGenerado.MONTANA},
            {"chef_marco", "Baleadas de desayuno, receta en los comentarios #cocina #honduras",
                    ProcesadorImagen.CUADRADO, ArteGenerado.COMIDA},
            {"chef_marco", "El truco del sofrito está en la paciencia #cocina, gracias @lucia.travel por la receta",
                    ProcesadorImagen.PAISAJE, ArteGenerado.COMIDA},
            {"mirador_espacial", "La luna de esta noche vista desde el patio #astronomia #ciencia",
                    ProcesadorImagen.CUADRADO, ArteGenerado.NOCHE},
            {"ritmo_catracho", "Ensayo de la semana, se viene algo bueno #musica",
                    ProcesadorImagen.PAISAJE, ArteGenerado.FIGURAS},
            {"depor_total", "Resumen de la jornada: dos goles en el último minuto #deportes, comenta @tec_al_dia",
                    ProcesadorImagen.PAISAJE, ArteGenerado.FIGURAS},
            {"tec_al_dia", "Cómo organizar tu escritorio virtual en cinco pasos #tecnologia",
                    ProcesadorImagen.CUADRADO, ArteGenerado.CIUDAD},
            {"ana.fitness", "Rutina de 15 minutos sin equipo #fitness #salud",
                    ProcesadorImagen.RETRATO, ArteGenerado.FIGURAS},
            {"noticias_360", "Resumen informativo de la mañana #noticias con @lucia.travel en portada",
                    ProcesadorImagen.CUADRADO, ArteGenerado.CIUDAD}
    };

    private InicializadorInsta() {
    }

    public static void sembrarSiHaceFalta(ServicioInsta servicio) {
        if (servicio.perfilDe(FAMOSOS[0][0]) != null) {
            return;
        }
        try {
            for (int i = 0; i < FAMOSOS.length; i++) {
                String[] datos = FAMOSOS[i];
                UsuarioInsta famoso = new UsuarioInsta(datos[1], datos[2].charAt(0), datos[0],
                        CLAVE_DEMO, Integer.parseInt(datos[3]), ArteGenerado.avatar(datos[1], i + 1));
                famoso.setBiografia(datos[4]);
                famoso.setVerificada(true);
                servicio.registrar(famoso);
            }
            LocalDateTime momento = LocalDateTime.now().minusDays(PUBLICACIONES.length);
            for (int i = 0; i < PUBLICACIONES.length; i++) {
                String[] datos = PUBLICACIONES[i];
                String formato = datos[2];
                byte[] imagen = ArteGenerado.publicacion(i + 2, formato, datos[3]);
                servicio.publicar(new Publicacion(datos[0], datos[1], imagen, formato,
                        "", momento.plusDays(i)));
            }
            entrelazar(servicio);
            repartirMeGusta(servicio);
            conversarDeEjemplo(servicio);
        } catch (MiniWindowsException error) {
            System.err.println("No se pudo sembrar el contenido de INSTA+: " + error.getMessage());
        }
    }

    public static ListaEnlazada<String> cuentasSugeridas() {
        ListaEnlazada<String> nombres = new ListaEnlazada<>();
        for (String[] datos : FAMOSOS) {
            nombres.agregar(datos[0]);
        }
        return nombres;
    }

    private static void repartirMeGusta(ServicioInsta servicio) throws MiniWindowsException {
        for (int i = 0; i < FAMOSOS.length; i++) {
            for (Publicacion publicacion : servicio.publicacionesDe(FAMOSOS[i][0])) {
                for (int j = 0; j < FAMOSOS.length; j++) {
                    if (i != j && (i + j) % 2 == 0) {
                        servicio.darMeGusta(publicacion, FAMOSOS[j][0], true);
                    }
                }
            }
        }
    }

    private static void conversarDeEjemplo(ServicioInsta servicio) throws MiniWindowsException {
        servicio.enviarMensaje(new Mensaje("chef_marco", "lucia.travel", "Te mande la receta que pediste"));
        servicio.enviarMensaje(new Mensaje("lucia.travel", "chef_marco", "Gracias, la pruebo hoy mismo"));
    }

    private static void entrelazar(ServicioInsta servicio) throws MiniWindowsException {
        for (int i = 0; i < FAMOSOS.length; i++) {
            for (int j = 0; j < FAMOSOS.length; j++) {
                if (i != j && (i + j) % 3 == 0) {
                    servicio.seguir(FAMOSOS[i][0], FAMOSOS[j][0]);
                }
            }
        }
    }

    public static String formatoPorDefecto() {
        return ProcesadorImagen.CUADRADO;
    }
}
