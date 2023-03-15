package aed.Laboratorios.urgencias;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Tests {

  @Test
  public void testAdmitir() throws PacienteExisteException {
    Urgencias u = new UrgenciasAED();
    u.admitirPaciente("111", 5, 1);
    Paciente p = u.atenderPaciente(10);

    // Check expected DNI ("111") == observed DNI (p.getDNI())
    try {
      assertEquals("111", p.getDNI());
    } catch (AssertionError err) {
      assertTrue(false, TestHandler.handleError(err));
    }

  }

  /**
   * 1- Comprueba que tras haber admitido a un paciente P1 y despúes a un paciente
   * P2, ambos con la misma prioridad, una llamada a atenderPaciente() devuelve el
   * paciente P1
   * <p>
   * 2- Comprueba que tras haber admitido a un paciente P1 y despúes a otro P2,
   * ambos con la misma prioridad, una llamada al método atenderPaciente()
   * devuelve el paciente P1 primero y una segunda llamada devuelve el paciente P2
   */
  @Test
  public void test1y2() {
    try {
      Urgencias u = new UrgenciasAED();
      Paciente p1 = u.admitirPaciente("1", 5, 0);
      Paciente p2 = u.admitirPaciente("2", 5, 1);
      assertEquals(u.atenderPaciente(2), p1);
      assertEquals(u.atenderPaciente(3), p2);
    } catch (Throwable e) {
      assertTrue(false, TestHandler.handleError(e));
    }
  }

  /**
   * 3- Comprueba que despúes de haber admitido a un paciente P1 con prioridad 5,
   * y
   * despúes a un paciente P2 con prioridad 1, una llamada a atenderPaciente()
   * devuelve el paciente P2
   */
  @Test
  public void test3() {
    try {
      Urgencias u = new UrgenciasAED();
      Paciente p1 = u.admitirPaciente("1", 5, 0);
      Paciente p2 = u.admitirPaciente("2", 1, 1);
      assertEquals(u.atenderPaciente(2), p2);
      p1.getDNI(); // Para no poner un @SuppressWarnings hacemos una llamada sin ninguna útilidad
                   // real.
    } catch (Throwable e) {
      assertTrue(false, TestHandler.handleError(e));
    }
  }

  /**
   * 4- Comprueba que despúes de admitir a un paciente P1 y otro P2, ambos con la
   * misma prioridad, tas una llamada a salirPaciente() con el DNI del paciente P1
   * como argumento, llamar al método atenderPaciente() devuelve el paciente P2
   */
  @Test
  public void test4() {
    try {
      Urgencias u = new UrgenciasAED();
      Paciente p1 = u.admitirPaciente("1", 5, 0);
      Paciente p2 = u.admitirPaciente("2", 1, 1);
      u.salirPaciente(p1.getDNI(), 2);
      assertEquals(u.atenderPaciente(3), p2);
    } catch (Throwable e) {
      assertTrue(false, TestHandler.handleError(e));
    }
  }

  /**
   * 5- Comprueba que tas admitir a un paciente P1 y despúes a un paciente P2,
   * ambos
   * con prioridad 5, y despúes haber llamado al método cambiarPrioridad() con el
   * DNI de P2 y la nueva prioridad a 1, una llamada al método atenderPaciente()
   * devuelve el paciente P2.
   */
  @Test
  public void test5() {
    try {
      Urgencias u = new UrgenciasAED();
      Paciente p1 = u.admitirPaciente("1", 5, 0);
      Paciente p2 = u.admitirPaciente("2", 1, 1);
      u.cambiarPrioridad(p2.getDNI(), 1, 2);
      p1.getPrioridad();
      assertEquals(u.atenderPaciente(3), p2);
    } catch (Throwable e) {
      assertTrue(false, TestHandler.handleError(e));
    }
  }

  /**
   * Subclase TestHandler que modifica el valor de salida de los mensajes de error
   * en caso de que algún {@code test} falle.
   * 
   * @author LMD
   * @author S0mbra
   * @implNote Para su implementación se debe usar en un bloque try-catch. Los
   *           tests se harán en el bloque try y cathc tendrá un throwable como
   *           argumento. Se deberá usar un assert que contenga un String como
   *           modificador del mensaje de error. Entonces llamar al método
   *           handleError en el bloque catch.
   */
  private static class TestHandler {
    public static String handleError(Throwable err) {
      StackTraceElement[] stackTrace = err.getStackTrace();
      String locateErr = null;
      for (StackTraceElement element : stackTrace) {
        if (element.getClassName().equals(Tests.class.getName())) {
          locateErr = "\nLocated Error: " + err.getMessage() + " at " + element.getClassName() + "."
              + element.getMethodName() + ": line " + element.getLineNumber() + "\n";
          // Aquí se puede editar el mensaje de error hasta dar un "full trace". Yo lo
          // dejo en lo básico dando el número de línea donde el test falla y los valores
          // esperados que da por defecto el mensaje de error de junit AssertionError.
          break;
        }
      }
      return locateErr;
    }
  }
}