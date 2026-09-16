package MiniWindows.Insta.Imagen;

import java.io.Serializable;

public record Sticker(String nombre, byte[] datos) implements Serializable {
}
