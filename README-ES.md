# Kanto compiler
## A kanto code compiler

Para compilar un código se debe primero añadir el TXT con el código deseado en la carpeta "/res/" (los ejemplos ya están cargados en esta carpeta). 
Finalmente, en el package "scanner" se debe modificar la clase "TokenScanner" y modificar el atributo "init_filename". Una vez hecho esto sólo se debe ejecutar la clase Main. 
El ".ASM" generado se encontrará en la carpeta root del proyecto, es decir "/main.asm".

Si por el contrario se quieren realizar los tests, se debe ejecutar la clase "Testing", dentro del package "testing". Esto ejecutará una serie de tests de forma consecutiva, 
dichos tests se encuentran en la carpeta "src/testing/tests/". Los resultados obtenidos de las compilaciones se guardan en "src/testing/results/". 
Para añadir tests basta con añadir el fichero a la carpeta "src/testing/tests/".

Se deben añadir dos librerías que se encuentran en la carpeta "/libs/". Estas librerías permiten crear jsons de las estructuras de datos usadas. 
Estas estructuras de datos se guardan en la carpeta "/stats/". Se pueden visualizar los árboles haciendo uso de la herramienta online "https://vanya.jp.net/vtree/". 
Adicionalmente, se generan estadísticas del tiempo de compilación, guardadas en la misma carpeta que los jsons y en un archivo llamado "stats.txt".

Si se usa la herramienta online mencionada, es importante destacar que los nodos unidos por una línea horizontal son en realidad hermanos.

## Versions
--= IntelliJ =--
SDK -> corretto- 15 Amazon Corretto version 15.0.2
Language level -> 17
Build #IU-213.6777.52, built on January 28, 2022
Runtime version: 11.0.13+7-b1751.25 amd64
VM: OpenJDK 64-Bit Server VM by JetBrains s.r.o.

--= Mars =--
Mars 4.5