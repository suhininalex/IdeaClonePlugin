This is a plugin for IDE IntelliJ IDEA 2017+. 
It provides both on-the-fly inspection and action to find all duplicates in source code.


#### Main features

- Ability to look for exact clones and clones with renamed tokens
- Ability to analyze huge projects (millions lines of code)
- Ability to provide clone inspections on-the-fly
- Ability to analyze Java/Kotlin files

#### Warning

Plugin requires additional memory

> Up to 500 Mb per 1 million LOC to build index and produce inspections 
>
> Up to 500 Mb per 1 million LOC to show all clones in the project

#### General usage instructions

#### Settings

One can set plugin parameters via **File \| Settings**

> Note that almost any change will cause full reindexing of the current project

![](https://github.com/suhininalex/IdeaClonePlugin/blob/gh-pages/images/find-configuration.png?raw=true)

#### Samples


#### Comparision with other tools

| Tool                                | Commercial | On-the-fly | Recall     | Scalability |
|:------------------------------------|:-----------|:-----------|:-----------|:------------|
| PMD                                 | no         | no         | ?          | ?           |
| Checkstyle                          | no         | no         | ?          | ?           |
| Duplicate finder maven plugin       | no         | no         | ?          | ?           |
| IntelliJ IDEA Ultimate (action)     | yes        | no         | good       | bad         |
| IntelliJ IDEA Ultimate (inspection) | yes        | yes        | acceptable | excellent   |
| IDEA clone plugin                   | no         | 13 s       | good       | good        |

###### PMD

###### Checkstyle

###### IntelliJ IDEA Ultimate (action)

###### IntelliJ IDEA Ultimate (inspection)

###### IDEA clone plugin

#### Tested projects

The plugin have been used to analyze a few popular Java projects. 

| Project                 | Time (inspection) | Time (action) | Memory  | Clones found  |
|:------------------------|:------------------|:--------------|:--------|:--------------|
| JetBrains MPS           | 220 s             | 160 s         | 2950 Mb | 8109          |
| Android Framework Base  | 145 s             | 38 s          | 1495 Mb | 5055          |
| JetBrains IntelliJ IDEA | 221 s             | 49 s          | 2210 Mb | 5178          |
| Apache Hadoop           | 54 s              | 13 s          | 799 Mb  | 1753          |
| Spring Framework        | 35 s              | 3 s           | 403 Mb  | 746           |
| Consulo                 | 86 s              | 18 s          | 869 Mb  | 2275          |
| Apache CloudStack       | 44 s              | 7 s           | 841 Mb  | 2875          |
| Apache Camel            | 31 s              | 8 s           | 640 Mb  | 1992          |

> Time (inspection) - time required to build project index (that's enough to allow clone inspections)

> Time (action) - time required to extract and show all clones found in the project

> Memory - total memory used by IntelliJ IDEA after both indexing and extraction of clone classes 

###### Configuration

> CPU i5-760 lynfield (4 core, 2.8 Ghz) 

> Max heap: 4000 Mb

###### Used parameters

![](https://github.com/suhininalex/IdeaClonePlugin/blob/gh-pages/images/test-configuration.png?raw=true)
