package MiniWindows.Red.Cliente;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClienteInsta {
    private String host;
    private int puerto;

    public ClienteInsta(String host, int puerto) {
        this.host = host;
        this.puerto = puerto;
    }

    public Object enviarComando(String comando, Object... parametros) {
        try (Socket socket = new Socket(host, puerto);
             ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream ois = new ObjectInputStream(socket.getInputStream())) {

            oos.writeUTF(comando);
            oos.writeObject(parametros);
            oos.flush();

            return ois.readObject();
        } catch (Exception e) {
            System.err.println("Error en la conexión con el servidor: " + e.getMessage());
            return null;
        }
    }
}