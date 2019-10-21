# Funcional2019C2
Trabajo Práctico de Scala del 2do cuatrimestre de 2019


## Devcontainer

Para inicializar un nuevo proyecto con docker, y desarrollarlo con Visual Studio Code (VSC):

0. Instalar plugins "Remote Development" y "Remote Containers" en VSC

1. Crear la siguiente estructura de carpetas:

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

2. Agregar a plugins.sbt:

```
addSbtPlugin("org.scalameta" % "sbt-metals" % "0.7.6")
addSbtPlugin("ch.epfl.scala" % "sbt-bloop" % "1.3.4")
```

3. Desde VSC abrir esa carpeta con "Remote-Containers: Open Folder in Container"

4. En ese nuevo proyecto abrir una terminal y correr:

```
bloop server &
sbt
```

4. En la consola de sbt poner `compile`

