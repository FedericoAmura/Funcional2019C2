# Funcional2019C2
Trabajo Práctico de Scala del 2do cuatrimestre de 2019


## Devcontainer

Para inicializar un nuevo proyecto con docker, y desarrollarlo con Visual Studio Code (VSC):

1. Instalar plugins "Remote Development" y "Remote Containers" en VSC

2. Crear la siguiente estructura de carpetas:

```
.
├── .devcontainer
│   ├── Dockerfile
│   └── devcontainer.json
├── project
│   └── plugins.sbt
└── src
    └── main
        └── scala
            └── Main.scala
```

3. Agregar a plugins.sbt:

```
addSbtPlugin("org.scalameta" % "sbt-metals" % "0.7.6")
addSbtPlugin("ch.epfl.scala" % "sbt-bloop" % "1.3.4")
```

4. Desde VSC abrir esa carpeta con "Remote-Containers: Open Folder in Container"

5. En ese nuevo proyecto abrir una terminal y correr:

```
bloop server &
sbt
```

6. En la consola de sbt poner `compile`

## IntelliJ

1. Instalar jdk en la compu

2. En IntelliJ instalar tambien el plugin Scala

3. Para correr el sistema completo en modo debug (con live reloading) ejecutar `./scripts/upDev.sh`

4. Para correr como prod ejecutar `./script/upProd.sh`