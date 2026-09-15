package MiniWindows.Persistencia;

import java.nio.file.Path;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public final class Cerrojos {

    private static final ConcurrentHashMap<String, ReentrantLock> CERROJOS = new ConcurrentHashMap<>();

    private Cerrojos() {
    }

    public static ReentrantLock de(Path archivo) {
        String clave = archivo.toAbsolutePath().normalize().toString().toLowerCase(Locale.ROOT);
        return CERROJOS.computeIfAbsent(clave, ruta -> new ReentrantLock());
    }
}
