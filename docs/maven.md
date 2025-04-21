# Convenção de nomenclatura de coordenadas Maven

Para que o Maven possa identificar e utilizar qualquer artefato (por exemplo, arquivo um `.jar`), cada artefato deve ser identificável por meio de uma combinação única de três identificadores. Essa combinação é chamada de "coordenadas Maven ".

As coordenadas Maven consistem em um identificador de grupo de projeto chamado `groupId`, um identificador de artefato chamado `artifactId` e o identificador de versão chamado `version`.

## Identificador do grupo de projeto

Quando projetos da mesma organização são relacionados por tópicos, dizemos que pertencem ao mesmo "grupo de projetos".

O `groupId` identifica exclusivamente um grupo de projetos em relação a todos os outros grupos.

Cada um groupIddeve seguir as regras de nomenclatura de pacotes do Java . Isso significa que ele começa com um nome de domínio invertido que você controla.

## Identificador de artefato

Este `artifactId` é o nome do artefato. Os identificadores devem consistir apenas em letras minúsculas, dígitos e hifens.


## Referências

- https://maven.apache.org/guides/mini/guide-naming-conventions.html
