package aed.Laboratorios.urgencias;

// La documentación de esta parte del código fue cortesía de los miembros del DLSIS de la ETSIInf, UPM...
// Yo hice leves modificaciones a las 6am debido a la falta de documentación inicial... 
// Todo lo que hay detrás de "--" es de mi cortesía ;)

/**
 * Un paciente. --ESTA SE LLEVA LA PALMA DE LA DOCUMENTACIÓN, QUIÉN LO HIZO DE
 * VERDAD, DARLE UN PUTO PIN... "UN PACIENTE" Y SE QUEDA TAN AGUSTO. Y DE @'S O
 * DEMÁS NI HABLEMOS, VAYA A SER QUE NOS DUELAN LOS DEDOS DE TANTA
 * DOCUMENTACIÓN...
 */
public class Paciente implements Comparable<Paciente> {

  // DNI --NOJODAS
  private String DNI;
  // Prioridad --¿DE VERDAD? ¿QUÉ HAGO CON ESTO? NO ME LO EXPLIQUES VAYA A SER QUE
  // NO DESARROLLE MI HABILIDAD DE ADIVINO... AUNQ EN LA GuÍA LO EXPLICA
  // MEDIANANMENTE BIEN, ¡PERO DOCUMENTAAAAAR!
  private int prioridad;
  // Tiempo de admision en las urgencias --MUCHAS GRACIAS, NO ME HABÍA QUEDADO
  // CLARO QUE "TIEMPOADMISION" EN UNA CLASE "PACIENTE" QUE SIRVE PARA USARSE EN
  // OTRA CLASE "URGENCIAS" ERA EL TIEMPO DE ADMISIÓN... AHORA, PARA QUÉ USARLO,
  // ESO OTRO DÍA QUE HOY HABÍAMOS RELLENADO EL CUPO MÁXIMO DE ESCRITURA POR ESTE
  // AÑO.
  private int tiempoAdmision;
  // Tiempo cuando entro en la prioridad --¿¿¿¿¿¿¿¿¿que prioridad jdr?????????
  // EJEMPLO DE MALA DOCUMENTACIÓN: {@code int velocidadDino, velocidadDinoPito}:
  // la primera es la velocidad de un dinosaurio, la segunda es la velocidad de un
  // dinosaurio, pito. ASI NO JDR...
  private int tiempoAdmisionEnPrioridad;

  /**
   * Constructor. --GRACIAS; JDR NO ME HABÍA DADO CUENTA... "@PARAMS" Y ESAS
   * MIERDAS EXISTEN.
   */
  public Paciente(String DNI, int prioridad, int tiempoAdmision, int tiempoAdmisionEnPrioridad) {
    this.DNI = DNI;
    this.prioridad = prioridad;
    this.tiempoAdmision = tiempoAdmision;
    this.tiempoAdmisionEnPrioridad = tiempoAdmisionEnPrioridad;
  }

  /**
   * Devuelve el dni.
   * 
   * @return el dni. --SIN COMENTARIOS...
   */
  public String getDNI() {
    return DNI;
  }

  /**
   * Devuelve la prioridad. --¿ES EN SERIO?
   * 
   * @return la prioridad. --...
   */
  public int getPrioridad() {
    return prioridad;
  }

  /**
   * Devuelve el tiempo de admision.
   * 
   * @return el tiempo de admision. --QUE COÑO ES Y CÓMO PRETENÉIS QUE USE ESTE
   *         TIEMPO Y EL DE PRIORIDAD JDR!
   */
  public int getTiempoAdmision() {
    return tiempoAdmision;
  }

  /**
   * Devuelve el tiempo de admision en la prioridad actual.
   * 
   * @return el tiempo de admision en la prioridad actual. --NO ME JODAS, PENSÉ
   *         QUE ME DEVOLVÍA LOS AÑOS DE MI TÍA... ¡¡CÓMO MIERDAS USO YO ESTE
   *         PARÁMETRO!!
   */
  public int getTiempoAdmisionEnPrioridad() {
    return tiempoAdmisionEnPrioridad;
  }

  /**
   * Asigna una prioridad nueva.
   * 
   * @return la prioridad antigua.
   */
  public int setPrioridad(int prioridadNuevo) {
    int oldPrioridad = prioridad;
    prioridad = prioridadNuevo;
    return oldPrioridad;
  }

  /**
   * Asigna un nuevo tiempo de admision en prioridad.
   * 
   * @return el tiempo de admision en prioridad antigua.
   *         --TIEMPOADMISIONENPRIORIDAD O ABUELAENBICICLETA, LO SIGUIENTE ES
   *         OFUSCAR EL CÓDIGO.
   */
  public int setTiempoAdmisionEnPrioridad(int tiempoNuevo) {
    int oldTiempo = tiempoAdmisionEnPrioridad;
    tiempoAdmisionEnPrioridad = tiempoNuevo;
    return oldTiempo;
  }

  @Override
  public String toString() {
    return "<\"" + DNI.toString() + "\"," + prioridad + "," + tiempoAdmision + "," + tiempoAdmisionEnPrioridad + ">";
  }

  // ----------------------------------------------------------------------
  // Hay que definir compareTo:
  // (ve la descripcion en la guia)
  // --QUE COJONES ES TIEMPOAMDMISIONPRIORIDAD JOOOODER
  // --DOCUMENTAD CÓDIGO COÑO QUE PARECE QUE EL TECLADO OS QUEMA LOS DEDOS...

  /*
   * Según la guía hay unas 5 formas de interpretar esta mierda para pasar unos testers u otros (es un "u" excluyente).
   * 
   */
  @Override
  public int compareTo(Paciente paciente) {
    if (this.prioridad != paciente.prioridad) {
      return this.prioridad - paciente.prioridad;
    } else if (this.tiempoAdmisionEnPrioridad != paciente.tiempoAdmisionEnPrioridad) {
      return this.tiempoAdmisionEnPrioridad - paciente.tiempoAdmisionEnPrioridad;
    } else {
      return this.tiempoAdmision - paciente.tiempoAdmision;
    }
  }

  // Hay que definir equals
  // Usad solo el DNI al comparar pacientes
  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Paciente) {
      Paciente p = Paciente.class.cast(obj);
      return this.DNI.equals(p.DNI);
    } else
      return false;
  }

  // Hay que definir hashCode
  // Usad solo el DNI al calcular el hashCode
  @Override
  public int hashCode() {
    // try {
    // return Integer.parseInt(DNI); //Suponiendo que el String DNI es únicamente
    // númerico.
    // } catch (NumberFormatException e) { //Si no fuera así solucionamos el
    // problema "parseando" la parte númerica.
    // char[] dniC = DNI.toCharArray();
    // String dni = "";
    // for (char c : dniC) {
    // if(c >= 48 && c <= 59 )
    // dni += c;
    // }
    // return Integer.parseInt(dni);
    // }
    return DNI.hashCode();
  }

}
