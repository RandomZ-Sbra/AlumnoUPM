# Repositorio con prácticas de años pasados de la ETSIInf (UPM)

## ⚠️Atención

En ningún momento se deberá usar este repositorio con intenciones fraudulentas. Las prácticas ya fueron realizadas en su momento y quedan registradas en la BBDD de cada departamento, por lo que no me hago responsable EN ABSOLUTO de las consecuencias si salta la alerta de copia.

El repositorio tiene exclusivamente intención didáctica para usarse como guía, no para copiarlo. Si usas este repositorio reconoces que eres responsable de tus actos y que sólamente lo usarás para ver ejemplos sin aplicarlos directamente en tus prácticas.

En serio gente, no me hagáis poner el repositorio en privado ;)

## Estructura del proyecto

```ini
AlumnoUPM
├─ Java
│  ├─ AED
│  │  └─ Entregas
│  │     ├─ lib
│  │     │  ├─ aedlib.jar
│  │     │  └─ junit-platform-console-standalone-1.9.2.jar
│  │     └─ src
│  │        └─ aed
│  │           ├─ Individuales
│  │           │  ├─ filter
│  │           │  │  ├─ GreaterThan.java
│  │           │  │  └─ Utils.java
│  │           │  ├─ individual2
│  │           │  │  └─ IndexedListCheckSumUtils.java
│  │           │  ├─ individual5
│  │           │  │  ├─ TempData.java
│  │           │  │  └─ Utils.java
│  │           │  ├─ individual6
│  │           │  │  └─ Suma.java
│  │           │  └─ positionlistiterator
│  │           │     └─ NIterator.java
│  │           └─ Laboratorios
│  │              ├─ delivery
│  │              │  └─ Delivery.java
│  │              ├─ hashtable
│  │              │  └─ HashTable.java
│  │              ├─ hotel
│  │              │  ├─ Habitacion.java
│  │              │  ├─ Hotel.java
│  │              │  ├─ MiHotel.java
│  │              │  └─ Reserva.java
│  │              ├─ polinomios
│  │              │  ├─ Monomio.java
│  │              │  └─ Polinomio.java
│  │              ├─ recursion
│  │              │  ├─ Explorador.java
│  │              │  ├─ Lugar.java
│  │              │  ├─ MyInteger.java
│  │              │  ├─ Punto.java
│  │              │  ├─ PuntoCardinal.java
│  │              │  ├─ Utils.java
│  │              │  └─ ZMyTester.java
│  │              ├─ tries
│  │              │  ├─ DictImpl.java
│  │              │  └─ Dictionary.java
│  │              └─ urgencias
│  │                 ├─ Paciente.java
│  │                 ├─ PacienteExisteException.java
│  │                 ├─ PacienteNoExisteException.java
│  │                 ├─ Tests.java
│  │                 ├─ Urgencias.java
│  │                 └─ UrgenciasAED.java
│  ├─ Concurrencia
│  │  ├─ controlAlmacen
│  │  │  ├─ lib
│  │  │  │  ├─ cclib.jar
│  │  │  │  ├─ jcsp.jar
│  │  │  │  └─ junit-platform-console-standalone-1.10.0-M1.jar
│  │  │  └─ src
│  │  │     └─ cc
│  │  │        └─ controlAlmacen
│  │  │           ├─ check.java
│  │  │           ├─ Cliente.java
│  │  │           ├─ ControlAlmacen.java
│  │  │           ├─ ControlAlmacenCSP.java
│  │  │           ├─ ControlAlmacenCSPL.java
│  │  │           ├─ ControlAlmacenMonitor.java
│  │  │           ├─ ControlAlmacenMonitor2.java
│  │  │           ├─ ControlAlmacenMonitorC.java
│  │  │           ├─ ControlAlmacenMonitorXD.java
│  │  │           ├─ controlAlmacenTest.java
│  │  │           └─ Fabrica.java
│  │  └─ Entragas
│  │     ├─ lib
│  │     │  └─ cclib.jar
│  │     └─ src
│  │        ├─ Almacen1.java
│  │        ├─ AlmacenN.java
│  │        ├─ CC_01_Threads.java
│  │        ├─ CC_02_Carrera.java
│  │        ├─ CC_02_Carrera2.java
│  │        ├─ CC_03_MutexEA.java
│  │        ├─ CC_04_MutexSem.java
│  │        ├─ CC_04_MutexSemLu.java
│  │        ├─ CC_05_P1CSem.java
│  │        ├─ CC_06_PNCSem.java
│  │        ├─ CC_09_PmultiCMon.java
│  │        ├─ MultiAlmacenMon.java
│  │        └─ test.java
│  └─ Progra2
│     └─ Life
│        ├─ lib
│        │  ├─ stdlib.jar
│        │  └─ TAD.jar
│        └─ src
│           ├─ bg.txt
│           └─ com
│              └─ java
│                 └─ game
│                    ├─ Gen.java
│                    ├─ IGen.java
│                    ├─ ILifeHistory.java
│                    ├─ Life.java
│                    ├─ LifeController.java
│                    ├─ LifeExecuter.java
│                    ├─ LifeHistory.java
│                    ├─ LifeView.java
│                    └─ LifeViewListener.java
└─ README.md



```

---

## 📃Nota:

Las librerías aedlib y cclib de los proyectos incluidas en las carpetas "lib" son obra exclusiva del departamento del DLSIIS de la UPM, son librerías PRIVADAS en su mayoría, no se deben usar sin el permiso explícito de dicho departamento. Están en los repositorios para los alumnos de la ETSIInf (UPM), si no perteneces a dicha organización y/o no cursas las respectivas asignaturas contacta con el departamento para solicitar permiso en caso de hacer uso de las mismas. Si bien cclib tiene un [repositorio abierto en GitHub](https://github.com/aherranz/cclib) con licencia pública, se recomienda avisar del uso de la librería o al menos dar crédito a los desarrolladores de a misma.

<p>
Si eres dueño de las librerías empleadas o perteneces al departamento correspondiente de la UPM y deseas reclamar la publcación de estas, póngase en contacto conmigo antes de emplear ninguna acción legal pues me ampara la libre publicación del conocimiento que las instituciones educativas deben cumplir 
(<a href="https://www.boe.es/buscar/doc.php?id=BOE-A-2023-7500">BOE-A-2023-7500</a>). De igual forma, en el momento que verifique la solicitud de reclamo de una librería la eliminaré inmediatamente.
<p>
Las reclamaciones no surtirán efecto sobre aquellas librerías de uso público o con licencias "<a href="https://www.gnu.org/licenses/licenses.es.html">GNU/GPL</a>" (<a href="https://introcs.cs.princeton.edu/java/stdlib/">stdlib.jar</a>, <a href="https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.9.2/">junit-platform-console-standalone-1.9.2.jar (y demás versiones)</a>, <a href="https://jar-download.com/artifact-search/tad">tad.jar</a>, etc.); librerías que no pertenezcan a su departamento o sobre las cuales el reclamador no tenga ningún derecho de autor; ni el código de la solución propuesta por el alumno.