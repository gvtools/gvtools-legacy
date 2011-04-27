package com.hardcode.gdbms.engine.instruction;

import com.hardcode.gdbms.engine.values.Value;


/**
 * Interfaz a implementar sobre los nodos
 *
 * @author Fernando González Cortés
 */
public interface Expression {
    /**
     * Evalúa la expresión para la fila row y realiza el cacheado del resultado
     * en caso de que la expression sea un literal
     *
     * @param row fila para la que se evalua la expresión
     *
     * @return Valor resultante de evaluar la expresión para la fila row
     *
     * @throws EvaluationException Si se produce algún error semántico
     */
    Value evaluateExpression(long row) throws EvaluationException;

    /**
     * Obtiene el nombre del campo en el que consiste la expresión. En el caso
     * de que la expresión conste de alguna operación o no contenga ninguna
     * referencia a un campo se devolverá null.
     *
     * @return Nombre del campo
     */
    String getFieldName();

    /**
     * Checks if this expression is an aggregate function. It is, implements
     * the Function interface and its isAggregate method returns true
     *
     * @return boolean
     */
    boolean isAggregated();

    /**
     * Simplifica las expresiones del árbol de adaptadores
     */
    void simplify();

    /**
     * Evalúa la expresión para la fila row
     *
     * @param row fila para la que se evalua la expresión
     *
     * @return Valor resultante de evaluar la expresión para la fila row
     *
     * @throws EvaluationException Si se produce algún error semántico
     */
    Value evaluate(long row) throws EvaluationException;

    /**
     * Indica si los operandos de esta expresión son siempre los mismos o
     * pueden cambiar. Puede cambiar cuando el operando es una funcion o una
     * referencia a un campo y no debe cambiar en el resto de casos
     *
     * @return true si esta expresión va a devolver siempre el mismo valor
     */
    boolean isLiteral();
}
