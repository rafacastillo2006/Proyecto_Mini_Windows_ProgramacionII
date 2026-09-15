package MiniWindows.SistemaOp.AdminTareas;

public record InfoHilo(long id, String nombre, String estado, boolean demonio, int prioridad, String origen) {

    public static final String ORIGEN_SISTEMA = "Sistema";
    public static final String ORIGEN_APLICACION = "MiniWindows";

    public boolean esDelSistema() {
        return ORIGEN_SISTEMA.equals(origen);
    }
}
