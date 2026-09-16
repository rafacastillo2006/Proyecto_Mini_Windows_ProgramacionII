package MiniWindows;

import MiniWindows.Red.Cliente.ClienteInsta;
import MiniWindows.Red.Servidor.ServidorInsta;
import MiniWindows.SistemaOp.Nucleo.RutasSistema;
import MiniWindows.Util.Rutas;

import java.io.IOException;

public final class ServidorMain {

    private ServidorMain() {
    }

    public static void main(String[] argumentos) throws IOException, InterruptedException {
        int puerto = argumentos.length > 0
                ? Integer.parseInt(argumentos[0])
                : ClienteInsta.PUERTO_POR_DEFECTO;

        Rutas.usarRaiz(RutasSistema.raizFisica());
        ServidorInsta servidor = new ServidorInsta(puerto, new MiniWindows.Insta.Servicio.ServicioLocal());
        servidor.encender();
        Runtime.getRuntime().addShutdownHook(new Thread(servidor::apagar));

        while (servidor.estaEncendido()) {
            Thread.sleep(1000);
        }
    }
}
