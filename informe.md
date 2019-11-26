\newpage
# Sistema de valoracion de cierre de lotes de soja

## Estructura

El proyecto se encuentra dividido en diferentes modulos que se debieran ejecutar secuencialmente para poder construir el modelo final
Los modulos son
- Base de datos
- Repositorio de entidades comunes
- ETL para carga inicial
- Entrenador de modelo de evaluacion
- API de consulta de valores de cierre

La gestion para el desarrollo de los modulos se maneja bajo un proyecto umbrella que maneja las dependencias entre ellos, en general agregando las dependencias comunes al proyecto especifico

## Ejecucion

Para correr el proyecto, tenemos diferentes alternativas
La unica dependencia del sistema es [docker][1] y [compose][2]

Para correr el sistema podemos usar una de las distintas opciones

```bash
# correr completo en modo desarrollo
./script/upDev.sh

# correr completo en modo produccion
./script/upProd.sh

# para correr partes especificas en modo desarrollo
docker-compose -f ./docker/docker-compose.base.yml -f ./docker/docker-compose.dev.yml up postgres
docker-compose -f ./docker/docker-compose.base.yml -f ./docker/docker-compose.dev.yml up dbFiller
docker-compose -f ./docker/docker-compose.base.yml -f ./docker/docker-compose.dev.yml up trainer
docker-compose -f ./docker/docker-compose.base.yml -f ./docker/docker-compose.dev.yml up api

# para correr partes especificas en modo produccion
docker-compose -f ./docker/docker-compose.base.yml -f ./docker/docker-compose.prod.yml up postgres
docker-compose -f ./docker/docker-compose.base.yml -f ./docker/docker-compose.prod.yml up dbFiller
docker-compose -f ./docker/docker-compose.base.yml -f ./docker/docker-compose.prod.yml up trainer
docker-compose -f ./docker/docker-compose.base.yml -f ./docker/docker-compose.dprodev.yml up api
```
\newpage

## Base de datos

La base de datos esta sobre el motor PostgreSQL, cuenta con una unica tabla que almacena los valores de los distintos cierres de los lotes de soja
Cuenta con los siguientes campos

| Campo  | Tipo  |
| ------------ | ------------ |
| id  | **integer, primary key**  |
| fecha  | text  |
| open  | double  |
| high  | double  |
| low  | double  |
| last  | double  |
| cierre  | double  |
| ajdif  | double  |
| mon  | text, default 'D'  |
| oivol  | integer  |
| oidif  | integer  |
| volope  | integer  |
| unidad  | text, default 'TONS'  |
| dolarbn  | double  |
| dolaritau  | double  |
| difsem  | double  |
| hash  | **integer, unique**  |

Se agrega el hash para poder, desde los datos, determinar si esa entrada ya se encuentra evaluada y persistida en la tabla

## Clases comunes
**Nombre proyecto: commons**

En este subproyecto se incluyen las clases comunes que se usan a traves de todo el resto de los proyectos
Incluye
- Cierre: encapsula el valor de cierre que tuvo una valuacion
- DB: conexion a la base de datos y querys que se ejecutan sobre ella
- Row: que representa una entrada en la base de datos
- SoyRequest: una consulta al sistema sobre la cual se puede generar un cierre y generar entradas en la base de datos

## Carga de datos
**Nombre proyecto: dbFiller**

Este proyecto toma la entrada desde un archivo de datos y los inserta en la base de datos para posteriormente entrenar el modelo de evaluacion o ser dispuestos por la API

El programa de este proyecto esta compuesto por 2 monadas IO.
La primera monada IO, lee las lineas del archivo de entrenamiento, las mapea a la clase Row y devuelve una lista de estas
La segunda IO, simplemente inserta la lista de Rows en la base de datos

Las monadas se componen con una for comprehension como un ETL y finalmente se lo ejecuta

## Prediccion de valores
**Nombre proyecto: trainer**

Generando un cluster Spark, se genera un modelo RandomForest basado en los datos cargados en la base. Este modelo luego sirve para generar datos de cierre para nuevas valuaciones recibidas en la API

Compuesto por dos etapas. En primer lugar este programa se trae todas las entradas de la base de datos en una monada IO. Este se mapea en un pipeline que entrena el modelo en el cluster Spark
Este pipeline primero crea un dataset de entrenamiento a partir de las entradas que se reciben de la base de datos, seleccionando los datos utiles (DolarBN, DolarItau, DifSem y Cierre) que luego separara en entrenamiento y evaluacion. Con estos se genera un modelo, entrenado y testeado, que finalmente se persiste en formato pmml que puede utilizar la API

## API de consultas
**Nombre proyecto: api**

Dispone un servicio API para consultar los valores de cierre de ciertos lotes, en caso de ser un dato conocido, se devuelve el valor de cierre que tuvo; si no lo es, entonces utiliza el modelo para generar un nuevo dato de cierre, persistiendolo en la base de datos y devolviendo esta nueva valuacion

Basado en http4s, el programa basico con cada request, mediante una for comprehension, es
- Tomar el body del request como SoyRequest
- Procesarlo en nuestra aplicacion
- Devolver el resultado de cierre como JSON

El procesamiento del request es implica primero conseguir el cierre del mismo a partir de sus datos, como depende del modelo, lo tenemos incluido en una monada Try, la cual despues, mediante pattern matching podemos operar en su valor o tirar la exception directamente (esto podria mejorarse). En caso de ser exitosa la evaluacion, entonces se ejecuta el programa que inserta y devuelve el cierre sobre la base de datos
Este ultimo programa, es una composicion de uno, que inserta los datos en la base de datos, pero solo en caso que no existan aprovechando que podemos identificarlo mediante el hash, y otro, que recupera el dato de cierre de la base (que teniamos desde los datos de entrenamiento o del modelo)


[2]: https://docs.docker.com/compose/install/ "docker-compose"
[1]: https://docs.docker.com/ "docker"