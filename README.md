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
│  └─ Life
│     ├─ lib
│     │  ├─ stdlib.jar
│     │  └─ TAD.jar
│     └─ src
│        ├─ bg.txt
│        └─ com
│           └─ java
│              └─ game
│                 ├─ Gen.java
│                 ├─ IGen.java
│                 ├─ ILifeHistory.java
│                 ├─ Life.java
│                 ├─ LifeController.java
│                 ├─ LifeExecuter.java
│                 ├─ LifeHistory.java
│                 ├─ LifeView.java
│                 └─ LifeViewListener.java
└─ README.md


```

---

## 📃Nota:

Las librerías aedlib y cclib de los proyectos incluidas en las carpetas "lib" son obra exclusiva del departamento del DLSIIS de la UPM, son librerias PRIVADAS en su mayoría, no se deben usar sin el permiso explicito de dicho departamento. Están en los repositorios para los alumnos de la ETSIInf (UPM), si no perteneces a dicha organización y/o no cursas las respectivas asignaturas contacta con el departamento para solicitar permiso para su uso.

<p>
Si eres el responsable o perteneces al DLSIIS de la UPM y deseas reclamar la publcación de dichas librerias pongase en contacto conmigo antes de emplear ninguna acción legal pues me escudo en la libre publicación del conocimiento que las instituciones educativas deben cumplir 
(<a href="https://www.boe.es/buscar/doc.php?id=BOE-A-2023-7500">BOE-A-2023-7500</a>). De igual forma, en el momento que verifique la solicitud de reclamo de una librería la eliminaré de forma inmediata.
<p>
Sin embargo, las librerias públicas no tienen forma de ser reclamadas (<a href="https://introcs.cs.princeton.edu/java/stdlib/">stdlib.jar</a>, <a href="https://repo1.maven.org/maven2/org/junit/platform/junit-platform-console-standalone/1.9.2/">junit-platform-console-standalone-1.9.2.jar</a>, <a href="https://jar-download.com/artifact-search/tad">tad.jar</a>).

```
AlumnoUPM
├─ .git
│  ├─ COMMIT_EDITMSG
│  ├─ config
│  ├─ description
│  ├─ FETCH_HEAD
│  ├─ HEAD
│  ├─ hooks
│  │  ├─ applypatch-msg.sample
│  │  ├─ commit-msg.sample
│  │  ├─ fsmonitor-watchman.sample
│  │  ├─ post-update.sample
│  │  ├─ pre-applypatch.sample
│  │  ├─ pre-commit.sample
│  │  ├─ pre-merge-commit.sample
│  │  ├─ pre-push.sample
│  │  ├─ pre-rebase.sample
│  │  ├─ pre-receive.sample
│  │  ├─ prepare-commit-msg.sample
│  │  ├─ push-to-checkout.sample
│  │  └─ update.sample
│  ├─ index
│  ├─ info
│  │  └─ exclude
│  ├─ logs
│  │  ├─ HEAD
│  │  └─ refs
│  │     ├─ heads
│  │     │  └─ main
│  │     └─ remotes
│  │        └─ origin
│  │           └─ main
│  ├─ objects
│  │  ├─ 01
│  │  │  └─ 63073ee6b8cfd0c6ca1c082a076c6d745b7e7f
│  │  ├─ 02
│  │  │  └─ 203d540b44d574146cc1eb9865b41c092ee9ea
│  │  ├─ 03
│  │  │  └─ 67355a65b34a7580eec08be5f68c46e2b5b37e
│  │  ├─ 04
│  │  │  └─ 9113b752f6c5ae1c4792ab77e5e97ff38bcc6a
│  │  ├─ 08
│  │  │  └─ 5b0ec9ed48c78d5f54d80eac703a1595ac2017
│  │  ├─ 0a
│  │  │  └─ 4feb5e6f6ca3bd30b0d89bbc6c3852b33cc1be
│  │  ├─ 0c
│  │  │  └─ 3ebb4ed0990014a7a7c97fc2086dfc4c54aae3
│  │  ├─ 0d
│  │  │  └─ 00ab843f0902c04e93a1ab752ff3e7751f5af4
│  │  ├─ 0e
│  │  │  └─ c57b431b4f1a893e3576166354e2608dbe88a9
│  │  ├─ 11
│  │  │  └─ 3578a680b97e80ccf3ce085541d85cc98db65a
│  │  ├─ 13
│  │  │  └─ c14f9cf2a678d9ba18bb09dcf2fec829044604
│  │  ├─ 1b
│  │  │  ├─ 76965bf9ddd09dc57bb8c3de22e4021d5269df
│  │  │  └─ a619e54087102392aa231dac085b1328f7959e
│  │  ├─ 1c
│  │  │  └─ 19fbca3e0d20998413aa797182af8ea20b9c19
│  │  ├─ 22
│  │  │  └─ 49f06b4fc3746e68ee58bfd2cc36c2bec32638
│  │  ├─ 24
│  │  │  └─ 1ee87e448c316686b49ddb1c3def73a19b1154
│  │  ├─ 27
│  │  │  ├─ 2bd9c114cb68add9843cc9dbabf7bddf962ecd
│  │  │  └─ a5416bb1025f38b8b63b948e5a39975389a88b
│  │  ├─ 2b
│  │  │  └─ 7fd2552826ce533ac7cb152fe68467ce20f139
│  │  ├─ 2f
│  │  │  └─ 855bb5498863832a4ed4dcc8030371de1ce0cf
│  │  ├─ 32
│  │  │  └─ f0a9f79d9bc1b204b969ed9149003c80e43dd2
│  │  ├─ 41
│  │  │  ├─ 1ad9237921d72c68a07fb46e548515dfe0e821
│  │  │  ├─ ab9413e769daf6de32a17e96f11757e6e50e25
│  │  │  └─ d312a7e613cdbe2e23bcebe7e08db0252a3aec
│  │  ├─ 46
│  │  │  └─ 3bba0c0ed6637b1d15642060364b69c3a6850b
│  │  ├─ 48
│  │  │  └─ c476bf33bb3d344ad9dbbe509d60b7b8b88840
│  │  ├─ 4c
│  │  │  └─ df6f483a4250ecb969496e53577d5b65885b06
│  │  ├─ 4d
│  │  │  └─ e60019b9a4bc115b3c288dc270194d921f2469
│  │  ├─ 4e
│  │  │  └─ c17200dbdaf75d37c4b90ed2c180003971ba8c
│  │  ├─ 4f
│  │  │  └─ c28d956225ad4e91c5059ed3f8aabfcece11a9
│  │  ├─ 50
│  │  │  └─ 24e3e6fd1917b6f59fb04f1f39771956bb43e9
│  │  ├─ 5b
│  │  │  └─ 533759fff81cb7f92d9a74442482431e275c77
│  │  ├─ 5e
│  │  │  └─ 0f11b9cc48f8c0e339284d555551bfe1a182a5
│  │  ├─ 61
│  │  │  └─ b78d0efd7e54c42cb5710dcf2d835d54d78248
│  │  ├─ 62
│  │  │  └─ dfaa4affb8896216c9999dbdd45fb26880a44a
│  │  ├─ 69
│  │  │  ├─ 6a42a9f0d99a680e0046a5aeac1e76e194e084
│  │  │  └─ 7e7691646344b39646916058994e55790aafde
│  │  ├─ 6d
│  │  │  └─ 83d11efdebf3876c882c1eb6bc623eb3d29a4d
│  │  ├─ 6e
│  │  │  └─ aec64c430a6242880dbfff8df69e05070cb46b
│  │  ├─ 72
│  │  │  └─ e46fd1ee983f845dc947dc81ffdb20b867f48b
│  │  ├─ 78
│  │  │  └─ 36082b6da2142eecbb12f3b79f374d20613c77
│  │  ├─ 7c
│  │  │  └─ 3890f4c682adb805fcfad078ef5309f0bb6164
│  │  ├─ 84
│  │  │  └─ 15d1e36277e17a5dd2a5328c5386cac6d956df
│  │  ├─ 85
│  │  │  └─ 688dd69b7f3f8f16a508d6e328b475f66ac4c9
│  │  ├─ 8a
│  │  │  └─ 5c7bdc2efbb82b0457f8b079a3e8704dfdc54a
│  │  ├─ 8d
│  │  │  └─ 3a8b96c99f6f552ba22abda7d1cf4d9a95df8d
│  │  ├─ 9b
│  │  │  └─ dff6041c7c6064af42b7bcb8f6aa302f62545a
│  │  ├─ 9e
│  │  │  └─ c1c4f8391c4d1582021e2721cb96905967a783
│  │  ├─ a1
│  │  │  └─ 1d4c413d5c32274d6289cd9a81c157ee0b66c9
│  │  ├─ a8
│  │  │  └─ 2e75eb518aa8d29315f6bd3155fb37990dc4f8
│  │  ├─ aa
│  │  │  └─ cc90cc5bb3c650067b36aab228ae4a63fe5269
│  │  ├─ ab
│  │  │  └─ 0112ca95bab0dc1d7a5055db87baf71987c9b1
│  │  ├─ ac
│  │  │  └─ 2f39561b13147c0c254375a987e1f081c8da22
│  │  ├─ b4
│  │  │  ├─ 5ffb78121050939b1dbb97e542595337a0f799
│  │  │  └─ d58166e4f1df8ed6edbfd6c76f8857df666434
│  │  ├─ b6
│  │  │  └─ 7ad49b1e624739b39071da75b03c94261e6ce3
│  │  ├─ bb
│  │  │  └─ 0ae3cc14b20ee591a70b0a8e35eb82a6fd2ad1
│  │  ├─ bd
│  │  │  └─ 5758e33582286b171c3ccf583951e42b0ad564
│  │  ├─ c1
│  │  │  └─ 0131f33c7490cdaf4f16f5c947ccf90d682662
│  │  ├─ c6
│  │  │  └─ c1a8d68b886fd6814b1b3ace7ec9092c4aa651
│  │  ├─ ca
│  │  │  └─ 5cc14df67758d3847a3f854958326ccd0cfbd0
│  │  ├─ d0
│  │  │  └─ 7195213510dc2a20f19e9198c453bb0feebdb6
│  │  ├─ d3
│  │  │  └─ eb144d3c18f065df7c066a9658cf8018b06186
│  │  ├─ da
│  │  │  └─ 0a3a4958279246ce0b955337567453384bcbfe
│  │  ├─ dd
│  │  │  └─ 3e367ecfd9ed3521195fcc27b13700c76ea0ff
│  │  ├─ df
│  │  │  └─ bc544534d6aaee5a4f8830d460ca899cac7409
│  │  ├─ ec
│  │  │  └─ 856868f0cb035cea80a641d0b06f4a9427ebc8
│  │  ├─ ef
│  │  │  ├─ 2007eee30896ef74e3bd8f4b1574eb2713014a
│  │  │  └─ a81e61a2f0887c39ff34f5bcaac82238cb1667
│  │  ├─ f1
│  │  │  └─ 7eb094c55be05a071321c755144cf85c24eda1
│  │  ├─ fa
│  │  │  └─ bc95c269938c7691642f198e3432ba591bee6c
│  │  ├─ fd
│  │  │  └─ 29e56a609d9246eeee7ae199dfabf8c67174f5
│  │  ├─ fe
│  │  │  └─ b35f22e69a66198116331c90b2e609c0f3a766
│  │  ├─ info
│  │  └─ pack
│  └─ refs
│     ├─ heads
│     │  └─ main
│     ├─ remotes
│     │  └─ origin
│     │     └─ main
│     └─ tags
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