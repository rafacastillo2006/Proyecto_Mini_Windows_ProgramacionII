package MiniWindows.Modelo;

public enum Rol {

    ADMINISTRADOR("Administrador"),
    ESTANDAR("Estándar");

    private final String etiqueta;

    Rol(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    public String getEtiqueta() {
        return etiqueta;
    }

    @Override
    public String toString() {
        return etiqueta;
    }
}
