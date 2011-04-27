package com.hardcode.driverManager;

/**
 * Interfaz a implementar por los objetos de validación
 *
 * @author Fernando González Cortés
 */
public interface DriverValidation {
    /**
     * El método validate se invocará al crear los drivers, y será el validador
     * el que indicará qué driver es válido para la aplicación y cual no
     *
     * @param d Driver a validar
     *
     * @return true o false en caso de que el driver sea o no sea valido
     */
    public boolean validate(Driver d);
}
