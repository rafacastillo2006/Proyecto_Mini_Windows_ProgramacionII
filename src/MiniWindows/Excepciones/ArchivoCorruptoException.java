package MiniWindows.Excepciones;

public class ArchivoCorruptoException extends MiniWindowsException {

    private static final long serialVersionUID = 1L;

    public ArchivoCorruptoException(String ruta) {
        super("El archivo \"" + ruta + "\" no tiene un formato valido de MiniWindows");
    }

    public ArchivoCorruptoException(String ruta, Throwable causa) {
        super("El archivo \"" + ruta + "\" no pudo leerse correctamente", causa);
    }
}
