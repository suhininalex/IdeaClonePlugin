### Idea clone plugin ###

This is a plugin for IDE IntelliJ IDEA witch can be used to find duplicated code in the project.

**Main features**

* Ability to look for exact clones and clones with renamed tokens 
* Ability to analyze huge projects (millions lines of code)
* Ability to provide clone inspections on-the-fly
* Ability to analyze Java/Kotlin files

Requires additional memory for analyzing: ~0.5 Gb per 1 million lines of code for inspections and the same for looking for all duplicates.

### Dependencies ###

1. Kovenant 3.0 
    * https://github.com/mplatvoet/kovenant
2. Compressed suffix tree
    * https://github.com/suhininalex/SuffixTree
3. Union Find Set
    * https://github.com/mromanak/union-find-set