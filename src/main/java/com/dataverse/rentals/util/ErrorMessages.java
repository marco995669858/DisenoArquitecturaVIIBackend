package com.dataverse.rentals.util;

import java.sql.SQLException;

/**
 * Traduce excepciones técnicas (SQL/MyBatis) a mensajes en español que un usuario final puede
 * entender. Los {@code Impl} nunca dejan que el mensaje crudo de la base de datos llegue al
 * cliente: siguen logueando la excepción completa (con stack trace) para diagnóstico, pero la
 * respuesta HTTP usa {@link #resolver(Exception)}.
 */
public final class ErrorMessages {

    private ErrorMessages() {
    }

    public static String resolver(Exception e) {
        SQLException sql = extraerSqlException(e);
        if (sql == null) {
            return "Ocurrió un problema al procesar su solicitud. Intente nuevamente.";
        }

        return switch (sql.getErrorCode()) {
            case 1062 -> "Ya existe un registro con esos mismos datos. Verifique los campos únicos e intente nuevamente.";
            case 1452 -> "El registro relacionado seleccionado no existe o fue eliminado. Actualice la página e intente nuevamente.";
            case 1451 -> "No se puede completar la operación: hay otros registros que dependen de este.";
            case 3819 -> "Los datos ingresados no cumplen las validaciones del sistema (revise montos, fechas o rangos).";
            case 1048 -> "Falta completar un campo obligatorio.";
            case 1406 -> "Uno de los valores ingresados es demasiado largo para el campo correspondiente.";
            case 1264 -> "Uno de los valores ingresados está fuera del rango permitido.";
            default -> "Ocurrió un problema al procesar su solicitud. Intente nuevamente.";
        };
    }

    /** Recorre la cadena de causas hasta encontrar la SQLException real, sin importar cuántas capas de MyBatis/Spring la envuelvan. */
    private static SQLException extraerSqlException(Throwable t) {
        while (t != null) {
            if (t instanceof SQLException sqlException) {
                return sqlException;
            }
            t = t.getCause();
        }
        return null;
    }
}
