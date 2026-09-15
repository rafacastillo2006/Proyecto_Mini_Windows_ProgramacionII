package MiniWindows.Persistencia;

public record ContenidoIns(CabeceraIns cabecera, byte[] datos) {

    public String comoTexto() {
        return new String(datos, java.nio.charset.StandardCharsets.UTF_8);
    }
}
