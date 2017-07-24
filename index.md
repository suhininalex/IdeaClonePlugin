This plugin provides advanced on-the-fly duplicated code inspections for IntelliJ IDEA (aka clone detection).

#### Main features

* Supports detection of
  * exact clones
  * clones with renamed variables/types/etc.
  * clones with gaps
* Can analyze industry-size projects (with millions lines of code)
* Works on-the-fly, right as you type
* Provides duplicated code comparison
* Supports both Java and Kotlin (with more languages on the way)

> NB! The plugin requires an additional JVM memory of up to 500 Mb per 1

#### General usage instructions
* * *

##### Inspections

The plugin provides duplicated code inspection after the initial project indexing is complete: use Alt + Enter to see all duplicated fragments.

![](https://github.com/suhininalex/IdeaClonePlugin/blob/gh-pages/images/inspection.png?raw=true)

##### Settings

Use **File \| Settings** to setup plugin.

> NB! Changes to the settings may cause a full reindexing of the current project

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

The plugin has been used to analyze several popular Java projects.

| Project                 | Time              | Memory  | Clones found  |
|:------------------------|:------------------|:--------|:--------------|
| JetBrains MPS           | 220 s             | 960 Mb  | 8109          |
| Android Framework Base  | 145 s             | 666 Mb  | 5055          |
| JetBrains IntelliJ IDEA | 221 s             | 1279 Mb | 5178          |
| Apache Hadoop           | 54 s              | 400 Mb  | 1753          |
| Spring Framework        | 35 s              | 284 Mb  | 746           |
| Consulo                 | 86 s              | 517 Mb  | 2275          |
| Apache CloudStack       | 44 s              | 530 Mb  | 2875          |
| Apache Camel            | 31 s              | 371 Mb  | 1992          |

> Time - time required to do the initial indexing

> Memory - total memory used by IntelliJ IDEA after full project indexing

###### Parameters used

![](https://github.com/suhininalex/IdeaClonePlugin/blob/gh-pages/images/settings-experiment.png?raw=true)

###### Configuration

> CPU i5-760 lynfield (4 core, 2.8 Ghz) 

> JVM options: `-ea -Xms256m --Xmx4000m`
