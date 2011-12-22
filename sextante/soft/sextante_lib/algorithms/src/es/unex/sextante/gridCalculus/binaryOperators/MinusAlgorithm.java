package es.unex.sextante.gridCalculus.binaryOperators;

public class MinusAlgorithm
         extends
            BinaryOperatorAlgorithm {

   @Override
   public void defineCharacteristics() {

      super.defineCharacteristics();

      setName("-");

   }


   @Override
   protected double getProcessedValue() {

      return m_dValue - m_dValue2;

   }

}
