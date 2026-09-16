package MiniWindows.Red.Servidor;

import MiniWindows.Modelo.Mensaje;
import MiniWindows.Red.RespuestaInsta;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class AvisosInsta {

    private final Map<String, List<ObjectOutputStream>> oyentes = new ConcurrentHashMap<>();

    public void registrar(String username, ObjectOutputStream salida) {
        oyentes.computeIfAbsent(clave(username), ignorado -> new CopyOnWriteArrayList<>()).add(salida);
    }

    public void quitar(String username, ObjectOutputStream salida) {
        List<ObjectOutputStream> lista = oyentes.get(clave(username));
        if (lista != null) {
            lista.remove(salida);
            if (lista.isEmpty()) {
                oyentes.remove(clave(username));
            }
        }
    }

    public void avisar(String username, Mensaje mensaje) {
        List<ObjectOutputStream> lista = oyentes.get(clave(username));
        if (lista == null) {
            return;
        }
        for (ObjectOutputStream salida : lista) {
            try {
                synchronized (salida) {
                    salida.writeObject(RespuestaInsta.ok(mensaje));
                    salida.flush();
                    salida.reset();
                }
            } catch (IOException cerrado) {
                lista.remove(salida);
            }
        }
    }

    public int cuantosEscuchan() {
        return oyentes.values().stream().mapToInt(List::size).sum();
    }

    private String clave(String username) {
        return username == null ? "" : username.toLowerCase(Locale.ROOT);
    }
}
