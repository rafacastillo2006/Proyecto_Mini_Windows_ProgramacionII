package MiniWindows.Red.Cliente;

import MiniWindows.Estructuras.ListaEnlazada;
import MiniWindows.Excepciones.MiniWindowsException;
import MiniWindows.Excepciones.OperacionArchivoException;
import MiniWindows.Red.RespuestaInsta;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClienteInsta {

    public static final int PUERTO_POR_DEFECTO = 5050;

    private final String host;
    private final int puerto;

    public ClienteInsta(String host, int puerto) {
        this.host = host;
        this.puerto = puerto;
    }

    public RespuestaInsta llamar(String comando, Object... parametros) {
        try (Socket conexion = new Socket(host, puerto);
             ObjectOutputStream salida = new ObjectOutputStream(conexion.getOutputStream());
             ObjectInputStream entrada = new ObjectInputStream(conexion.getInputStream())) {
            salida.writeUTF(comando);
            salida.writeObject(parametros);
            salida.flush();
            Object respuesta = entrada.readObject();
            return respuesta instanceof RespuestaInsta resultado
                    ? resultado
                    : RespuestaInsta.fallo("El servidor respondio algo inesperado");
        } catch (Exception error) {
            return RespuestaInsta.fallo("Sin conexion con el servidor: " + error.getMessage());
        }
    }

    public void enviar(String comando, Object... parametros) throws MiniWindowsException {
        RespuestaInsta respuesta = llamar(comando, parametros);
        if (respuesta.tieneError()) {
            throw new OperacionArchivoException(respuesta.error());
        }
    }

    public <T> T pedir(Class<T> tipo, String comando, Object... parametros) {
        Object valor = llamar(comando, parametros).valor();
        return tipo.isInstance(valor) ? tipo.cast(valor) : null;
    }

    @SuppressWarnings("unchecked")
    public <T> ListaEnlazada<T> pedirLista(String comando, Object... parametros) {
        Object valor = llamar(comando, parametros).valor();
        return valor instanceof ListaEnlazada ? (ListaEnlazada<T>) valor : new ListaEnlazada<>();
    }
}
