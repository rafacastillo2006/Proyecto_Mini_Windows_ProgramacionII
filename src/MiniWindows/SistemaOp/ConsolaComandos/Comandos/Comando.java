package MiniWindows.SistemaOp.ConsolaComandos.Comandos;

import MiniWindows.Excepciones.MiniWindowsException;

@FunctionalInterface
public interface Comando {

    String ejecutar(String argumento) throws MiniWindowsException;
}
