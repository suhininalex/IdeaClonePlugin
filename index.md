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

#### Comparing

#### Tested projects

| Project                 | Time (inspection) | Time (action) | Clones found  |
|:------------------------|:------------------|:--------------|:--------------|
| JetBrains MPS           | 220 s             | 160 s         | 8109          |
| Android Framework Base  | 145 s             | 38 s          | 5055          |
| JetBrains IntelliJ IDEA | 221 s             | 49 s          | 5178          |
| Apache Hadoop           | 54 s              | 13 s          | 1753          |
| Spring Framework        | 35 s              | 3 s           | 746           |
| Consulo                 | 86 s              | 18 s          | 2275          |
| Apache CloudStack       | 44 s              | 7 s           | 2875          |
| Apache Camel            | 31 s              | 8 s           | 1992          |