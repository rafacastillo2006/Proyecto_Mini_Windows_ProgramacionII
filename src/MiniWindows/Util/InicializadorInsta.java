package MiniWindows.Insta.Util;

import MiniWindows.Insta.Servicio.ServicioLocal;
import MiniWindows.Modelo.UsuarioInsta;
import MiniWindows.Util.Rutas;
import java.io.File;

public class InicializadorInsta {

    public static void cargarDatosIniciales(ServicioLocal servicio) {
        File archivoUsuarios = new File(Rutas.USERS_FILE);

        if (!archivoUsuarios.exists()) {
            try {
                UsuarioInsta noticias = new UsuarioInsta(
                        "Noticias Globales", 'M', "noticias_360", "Noti#2026", 30, ""
                );
                UsuarioInsta deportes = new UsuarioInsta(
                        "Deportes Central", 'M', "deportes_total", "Dep#2026", 25, ""
                );
                UsuarioInsta entretenimiento = new UsuarioInsta(
                        "Mundo Entretenimiento", 'F', "enter_world", "Ent#2026", 22, ""
                );

                servicio.registrarUsuario(noticias);
                servicio.registrarUsuario(deportes);
                servicio.registrarUsuario(entretenimiento);

                // Publicaciones de ejemplo iniciales
                servicio.hacerPost("noticias_360", "¡Bienvenidos a Noticias 360! Cobertura 24/7 #noticias", "", "Cuadrada");
                servicio.hacerPost("deportes_total", "Resumen de la jornada deportiva semanal #deportes", "", "Cuadrada");
                servicio.hacerPost("enter_world", "Nuevos lanzamientos de la semana en la música y cine #cine", "", "Cuadrada");

            } catch (Exception e) {
                System.err.println("Error al inicializar cuentas por defecto: " + e.getMessage());
            }
        }
    }
}