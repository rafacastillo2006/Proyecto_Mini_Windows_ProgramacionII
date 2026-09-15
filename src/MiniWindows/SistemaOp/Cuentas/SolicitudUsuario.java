package MiniWindows.SistemaOp.Cuentas;

import MiniWindows.Modelo.Rol;

public record SolicitudUsuario(String nombreCompleto, char genero, String username,
                               String contrasena, int edad, Rol rol) {
}
