package MiniWindows.SistemaOp.AdminTareas;

import MiniWindows.Estructuras.ListaEnlazada;

public record MuestraSistema(long memoriaUsada, long memoriaReservada, long memoriaMaxima,
                             double cpuProceso, double cpuSistema, long tiempoEncendido,
                             ListaEnlazada<InfoHilo> hilos) {

    public double proporcionMemoria() {
        return memoriaMaxima <= 0 ? 0 : (double) memoriaUsada / memoriaMaxima;
    }

    public int totalHilos() {
        return hilos.tamano();
    }

    public int hilosDeAplicacion() {
        int propios = 0;
        for (InfoHilo hilo : hilos) {
            if (!hilo.esDelSistema()) {
                propios++;
            }
        }
        return propios;
    }
}
