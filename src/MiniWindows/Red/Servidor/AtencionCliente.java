package MiniWindows.Red.Servidor;

import MiniWindows.Insta.Servicio.ServicioInsta;
import MiniWindows.Modelo.Mensaje;
import MiniWindows.Modelo.Publicacion;
import MiniWindows.Modelo.UsuarioInsta;
import MiniWindows.Red.RespuestaInsta;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class AtencionCliente implements Runnable {

    private final Socket cliente;
    private final ServicioInsta servicio;

    public AtencionCliente(Socket cliente, ServicioInsta servicio) {
        this.cliente = cliente;
        this.servicio = servicio;
    }

    @Override
    public void run() {
        try (Socket conexion = cliente;
             ObjectInputStream entrada = new ObjectInputStream(conexion.getInputStream());
             ObjectOutputStream salida = new ObjectOutputStream(conexion.getOutputStream())) {
            String comando = entrada.readUTF();
            Object[] parametros = (Object[]) entrada.readObject();
            salida.writeObject(atender(comando, parametros));
            salida.flush();
        } catch (Exception error) {
            System.err.println("Error atendiendo a un cliente: " + error.getMessage());
        }
    }

    private RespuestaInsta atender(String comando, Object[] parametros) {
        try {
            return RespuestaInsta.ok(ejecutar(comando, parametros));
        } catch (Exception error) {
            return RespuestaInsta.fallo(error.getMessage() == null ? "Error en el servidor" : error.getMessage());
        }
    }

    private Object ejecutar(String comando, Object[] parametros) throws Exception {
        return switch (comando) {
            case "LOGIN" -> servicio.autenticar(texto(parametros, 0), texto(parametros, 1));
            case "REGISTRAR" -> {
                servicio.registrar((UsuarioInsta) parametros[0]);
                yield Boolean.TRUE;
            }
            case "PERFIL" -> servicio.perfilDe(texto(parametros, 0));
            case "ACTUALIZAR_PERFIL" -> {
                servicio.actualizarPerfil((UsuarioInsta) parametros[0]);
                yield Boolean.TRUE;
            }
            case "ESTADO_CUENTA" -> {
                servicio.cambiarEstadoCuenta(texto(parametros, 0), (Boolean) parametros[1]);
                yield Boolean.TRUE;
            }
            case "PUBLICAR" -> {
                servicio.publicar((Publicacion) parametros[0]);
                yield Boolean.TRUE;
            }
            case "LINEA_TIEMPO" -> servicio.lineaDeTiempo(texto(parametros, 0));
            case "PUBLICACIONES" -> servicio.publicacionesDe(texto(parametros, 0));
            case "MENCIONES" -> servicio.menciones(texto(parametros, 0));
            case "ME_GUSTA" -> {
                servicio.darMeGusta((Publicacion) parametros[0], texto(parametros, 1),
                        (Boolean) parametros[2]);
                yield Boolean.TRUE;
            }
            case "SEGUIR" -> {
                servicio.seguir(texto(parametros, 0), texto(parametros, 1));
                yield Boolean.TRUE;
            }
            case "DEJAR_DE_SEGUIR" -> {
                servicio.dejarDeSeguir(texto(parametros, 0), texto(parametros, 1));
                yield Boolean.TRUE;
            }
            case "SIGUE" -> servicio.sigue(texto(parametros, 0), texto(parametros, 1));
            case "SEGUIDOS" -> servicio.seguidosDe(texto(parametros, 0));
            case "SEGUIDORES" -> servicio.seguidoresDe(texto(parametros, 0));
            case "BUSCAR_PERSONAS" -> servicio.buscarPersonas(texto(parametros, 0));
            case "SUGERENCIAS" -> servicio.sugerencias(texto(parametros, 0), (Integer) parametros[1]);
            case "BUSCAR_HASHTAG" -> servicio.buscarHashtag(texto(parametros, 0));
            case "ENVIAR_MENSAJE" -> {
                servicio.enviarMensaje((Mensaje) parametros[0]);
                yield Boolean.TRUE;
            }
            case "BANDEJA" -> servicio.bandejaDe(texto(parametros, 0));
            case "CONVERSACION" -> servicio.conversacion(texto(parametros, 0), texto(parametros, 1));
            case "CONTACTOS" -> servicio.contactosDe(texto(parametros, 0));
            case "SIN_LEER" -> servicio.mensajesSinLeer(texto(parametros, 0));
            case "MARCAR_LEIDA" -> {
                servicio.marcarConversacionLeida(texto(parametros, 0), texto(parametros, 1));
                yield Boolean.TRUE;
            }
            case "ELIMINAR_CONVERSACION" -> {
                servicio.eliminarConversacion(texto(parametros, 0), texto(parametros, 1));
                yield Boolean.TRUE;
            }
            case "STICKERS" -> servicio.stickersDe(texto(parametros, 0));
            case "AGREGAR_STICKER" -> {
                servicio.agregarSticker(texto(parametros, 0), texto(parametros, 1), (byte[]) parametros[2]);
                yield Boolean.TRUE;
            }
            case "CARPETAS" -> servicio.carpetasPersonalesDe(texto(parametros, 0));
            case "CREAR_CARPETA" -> {
                servicio.crearCarpetaPersonal(texto(parametros, 0), texto(parametros, 1));
                yield Boolean.TRUE;
            }
            default -> throw new IllegalArgumentException("Comando desconocido: " + comando);
        };
    }

    private String texto(Object[] parametros, int posicion) {
        return parametros[posicion] == null ? "" : parametros[posicion].toString();
    }
}
