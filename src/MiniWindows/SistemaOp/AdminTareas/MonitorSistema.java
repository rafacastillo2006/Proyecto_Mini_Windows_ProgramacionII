package MiniWindows.SistemaOp.AdminTareas;

import MiniWindows.Estructuras.ListaEnlazada;
import javafx.application.Platform;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

public class MonitorSistema {

    public static final String PREFIJO_HILOS = "MiniWindows-";

    private static final String NOMBRE_HILO = PREFIJO_HILOS + "Monitor";
    private static final long INTERVALO_MS = 1000;

    private final Consumer<MuestraSistema> alMuestrear;

    private volatile boolean activo;
    private Thread hilo;

    public MonitorSistema(Consumer<MuestraSistema> alMuestrear) {
        this.alMuestrear = alMuestrear;
    }

    public synchronized void iniciar() {
        if (activo) {
            return;
        }
        activo = true;
        hilo = new Thread(this::muestrear, NOMBRE_HILO);
        hilo.setDaemon(true);
        hilo.start();
    }

    public synchronized void detener() {
        activo = false;
        if (hilo != null) {
            hilo.interrupt();
            hilo = null;
        }
    }

    public static MuestraSistema tomarMuestra() {
        Runtime runtime = Runtime.getRuntime();
        long reservada = runtime.totalMemory();
        long usada = reservada - runtime.freeMemory();
        long maxima = runtime.maxMemory();
        return new MuestraSistema(usada, reservada, maxima, cargaProceso(), cargaSistema(),
                ManagementFactory.getRuntimeMXBean().getUptime(), listarHilos());
    }

    private void muestrear() {
        while (activo) {
            MuestraSistema muestra = tomarMuestra();
            Platform.runLater(() -> {
                if (activo) {
                    alMuestrear.accept(muestra);
                }
            });
            try {
                Thread.sleep(INTERVALO_MS);
            } catch (InterruptedException interrupcion) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    private static ListaEnlazada<InfoHilo> listarHilos() {
        List<Thread> vivos = new ArrayList<>(Thread.getAllStackTraces().keySet());
        vivos.sort(Comparator.comparing(Thread::getName, String.CASE_INSENSITIVE_ORDER));
        ListaEnlazada<InfoHilo> hilos = new ListaEnlazada<>();
        for (Thread hilo : vivos) {
            String origen = hilo.getName().startsWith(PREFIJO_HILOS)
                    ? InfoHilo.ORIGEN_APLICACION
                    : InfoHilo.ORIGEN_SISTEMA;
            hilos.agregar(new InfoHilo(hilo.threadId(), hilo.getName(), etiquetaEstado(hilo.getState()),
                    hilo.isDaemon(), hilo.getPriority(), origen));
        }
        return hilos;
    }

    private static String etiquetaEstado(Thread.State estado) {
        return switch (estado) {
            case NEW -> "Nuevo";
            case RUNNABLE -> "Ejecutando";
            case BLOCKED -> "Bloqueado";
            case WAITING -> "Esperando";
            case TIMED_WAITING -> "Esperando (con tiempo)";
            case TERMINATED -> "Terminado";
        };
    }

    private static double cargaProceso() {
        OperatingSystemMXBean sistema = ManagementFactory.getOperatingSystemMXBean();
        if (sistema instanceof com.sun.management.OperatingSystemMXBean extendido) {
            return extendido.getProcessCpuLoad();
        }
        return -1;
    }

    private static double cargaSistema() {
        OperatingSystemMXBean sistema = ManagementFactory.getOperatingSystemMXBean();
        if (sistema instanceof com.sun.management.OperatingSystemMXBean extendido) {
            return extendido.getCpuLoad();
        }
        return -1;
    }
}
