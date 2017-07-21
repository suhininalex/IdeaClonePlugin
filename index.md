Plugin provides on-the-fly inspection about duplicated code.

#### Main features

- Searching exact source clones and clones with renamed tokens
- Analyzing huge projects (millions lines of code)
- Providing clone inspections on-the-fly
- Comparing duplicated code fragments
- Analyzing Java/Kotlin files

> Plugin requires additional memory up to 500 Mb per 1 million LOC

#### General usage instructions
* * *

##### Inspections

Since the plugin is installed inspection is available. 
Use **Alt + Enter** to see all duplicated fragments.

![](https://github.com/suhininalex/IdeaClonePlugin/blob/gh-pages/images/inspection.png?raw=true)

##### Settings

Use **File \| Settings** to setup plugin.

> Changes may cause a full reindexing of the current project

![](https://github.com/suhininalex/IdeaClonePlugin/blob/gh-pages/images/settings.png?raw=true)

#### Comparision with other tools
* * *

| Tool                                | Commercial | On-the-fly | Recall     | Scalability |
|:------------------------------------|:-----------|:-----------|:-----------|:------------|
| IntelliJ IDEA Ultimate (action)     | yes        | no         | excellent  | acceptable  |
| IntelliJ IDEA Ultimate (inspection) | yes        | yes        | acceptable | excellent   |
| IDEA clone plugin                   | no         | yes        | good       | good        |

<!---  
| PMD                                 | no         | no         | ?          | ?           |
| Checkstyle                          | no         | no         | ?          | ?           |
| Duplicate finder maven plugin       | no         | no         | ?          | ?           |

# ###### PMD

###### Checkstyle

###### Duplicate finder maven plugin

###### IntelliJ IDEA Ultimate

###### IDEA clone plugin

--->

#### Tested projects
* * *

This plugin has been used to analyze several popular Java projects. 

| Project                 | Time (indexing)   | Memory  | Clones found  |
|:------------------------|:------------------|:--------|:--------------|
| JetBrains MPS           | 220 s             | 960 Mb  | 8109          |
| Android Framework Base  | 145 s             | 666 Mb  | 5055          |
| JetBrains IntelliJ IDEA | 221 s             | 1279 Mb | 5178          |
| Apache Hadoop           | 54 s              | 400 Mb  | 1753          |
| Spring Framework        | 35 s              | 284 Mb  | 746           |
| Consulo                 | 86 s              | 517 Mb  | 2275          |
| Apache CloudStack       | 44 s              | 530 Mb  | 2875          |
| Apache Camel            | 31 s              | 371 Mb  | 1992          |

> Time (indexing) - time required to build a project index

> Memory - total memory used by IntelliJ IDEA after full project indexing

###### Used parameters

![](https://github.com/suhininalex/IdeaClonePlugin/blob/gh-pages/images/settings-experiment.png?raw=true)

###### Configuration

> CPU i5-760 lynfield (4 core, 2.8 Ghz) 

> Max heap: 4000 Mb
