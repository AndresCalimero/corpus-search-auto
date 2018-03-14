# CorpusSearchAuto
Automatization tools for [CorpusSearch](http://corpussearch.sourceforge.net/)

## Summary

  **CorpusSearchAuto** is a Java program that automates large researches in corpus linguistics, it uses [CorpusSearch](http://corpussearch.sourceforge.net/) for searching syntactically annotated (parsed) corpora.
  
  The software is a set of **tools** (currently only have 2 tools, described below).

## Features

  * Classify [YCOE](http://www-users.york.ac.uk/~lang22/YCOE/YcoeHome.htm) and [PENN](https://www.ling.upenn.edu/hist-corpora/) corpus by genre (tool: **genre-finder**)
  * Run bulk searches of lexical and syntactic configurations by genre in several corpora (tool: **statistics-by-genres**)
  * Export results to HTML and Excel formats
  * Resource optimization

## Usage

```bash
  CospusSearchAuto -tool [tool] [parameters]
```

## The "data" folder

  The "data" folder is a folder in the same directory as the CorpusSearchAuto executable that should store all the corpora files and the CS.jar executable, CorpusSearchAuto will search for both in this directory.
  
  An example of its structure can be:
  
```bash
  data
    corporas
      YCOE
        info
          YcoeTextInfo.htm
          ...
        pos
          coadrian.o34.pos
          ...
        psd
          coadrian.o34.psd
          ...
      PPCEME
        info
          description.html
          ...
        pos
          abott-e1-p1.pos
          ...
        psd
          abott-e1-p1.psd
          ...
    output
      13-04-2017 18.46.36.333 YCOE
        prepositions
          homilies_and_sermons
            result.out
          ...
        ...
      ...
    searches
      queries
        YCOE
          prepositions.q
          ...
        ...
        PENN
          prepositions.q
          ...
        ...
      search.xml
      ...
    CS.jar
```

  **NOTE:** The only thing that is mandatory to have into the data folder is the corpora and the CS.jar executable, the rest is just a suggestion to organize more efficiently the [.q search files](http://corpussearch.sourceforge.net/CS-manual/QueryLanguage.html) and the output files.

## The XML search file

  CorpusSearchAuto requires an XML file that contains all the information necessary to perform a massive search in corpora, in that file you must specify the variables to be searched (associated with their corresponding .q) and all corpus belonging to each corpora, classified by genre.
  
  An example of a XML search file would be:
  
```xml
<?xml version="1.0" encoding="UTF-8"?>
<search xmlns="http://corpus.search.auto" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <corpora name="YCOE">
        <variables>
            <variable q-file="data\searches\queries\YCOE\adverbial_subordinators.q">Adverbial subordinators</variable>
            <variable q-file="data\searches\queries\YCOE\agentless_passives.q">Agentless passives</variable>
            <variable q-file="data\searches\queries\YCOE\attributive_adjetives.q">Attributive adjetives</variable>
            <variable q-file="data\searches\queries\YCOE\by_passives.q">By passives</variable>
            ...
        </variables>
        <genres>
            <genre name="Philosophy">
                <corpus>coboeth.o2</corpus>
                <corpus>codicts.o34</corpus>
                ...
            </genre>
            <genre name="History">
                <corpus>cobede.o2</corpus>
                <corpus>cochronA.o23</corpus>
                <corpus>cochronC</corpus>
                ...
            </genre>
            ...
        </genres>
    </corpora>
    <corpora name="PPCEME">
        <variables>
            <variable q-file="data\searches\queries\PENN\adverbial_subordinators.q">Adverbial subordinators</variable>
            <variable q-file="data\searches\queries\PENN\agentless_passives.q">Agentless passives</variable>
            <variable q-file="data\searches\queries\PENN\attributive_adjetives.q">Attributive adjetives</variable>
            <variable q-file="data\searches\queries\PENN\by_passives.q">By passives</variable>
            ...
        </variables>
        <genres>
            <genre name="Philosophy">
                <corpus>boethco-e1-h</corpus>
                <corpus>boethco-e1-p1</corpus>
                ...
            </genre>
            <genre name="History">
                <corpus>burnetcha-e3-h</corpus>
                <corpus>burnetcha-e3-p1</corpus>
                <corpus>burnetcha-e3-p2</corpus>
                ...
            </genre>
            ...
        </genres>
    </corpora>
    ...
</search>
```

## Tools

### genre-finder

  Generates an XML file that lists all the corpus of the specified corpora and classifies them by genres, currently only supports the [YCOE](http://www-users.york.ac.uk/~lang22/YCOE/YcoeHome.htm) and [PENN](https://www.ling.upenn.edu/hist-corpora/) corpora.
  
  The resulting XML file is useful to create the search file that the tool statistics-by-genres requires.
  
  An example of the resulting XML would be:
  
```xml
<?xml version="1.0" encoding="UTF-8"?>
<genres xmlns="corpus.search.auto" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="corpus.search.auto genres-schema.xsd" corpora="YCOE">
  <genre name="religious treatise">
    <corpus>coadrian.o34</corpus>
    <corpus>coalcuin</corpus>
    <corpus>cocura.o2</corpus>
    ...
  </genre>
  <genre name="homilies">
    <corpus>coaelhom.o3</corpus>
    <corpus>coaugust</corpus>
    ...
  </genre>
  ...
</genres>
```

##### Usage

```bash
  CospusSearchAuto -tool genre-finder -corpora [CORPORA] -in [PATH_OF_INFO_FILE] (-out [PATH_OF_OUT_FILE])
```

##### Parameters

| Name | Mandatory | Default value | Description |
| ------------- | ------------- | ------------- | ------------- |
| corpora | Yes | - | The corpora to be used, for example: YCOE |
| in | Yes | - | The corpora html file specifying the genres to which each corpus belongs, for example: "data\corporas\YCOE\info\YcoeTextInfo.htm" |
| out | No | in path | Path of the resulting XML file  |

##### Examples

```bash
  CorpusSearchAuto -tool genre-finder -corpora YCOE -in "data\corporas\YCOE\info\YcoeTextInfo.htm"
```

```bash
  CorpusSearchAuto -tool genre-finder -corpora PPCEME -in "data\corporas\PPCEME\info\description.html"
```

```bash
  CorpusSearchAuto -tool genre-finder -corpora PPCME2 -in "data\corporas\PPCME2\info\description.html" -out "C:\genres.xml"
```

---

### statistics-by-genres

Processes the XML search file (described above) and generates one or more output files with the results in the format(s) specified.

##### Usage

```bash
  CospusSearchAuto -tool statistics-by-genres -in [PATH_OF_SEARCH_FILE] (-show-only [all|hits|tokens|total] -out-format [all|html|excel] -out [PATH_OF_OUT_FOLDER])
```

##### Parameters

| Name | Mandatory | Default value | Description |
| ------------- | ------------- | ------------- | ------------- |
| in | Yes | - | Path of the XML search file, for example: "data/searches/search.xml" |
| show-only | No | all | What should be displayed in the output file(s) (hits, tokens, total or all) |
| out-format | No | all | What format the output file(s) should have (html, excel or all) |
| out | No | in path | Path to the folder where the output files will be saved |

##### Examples

```bash
  CorpusSearchAuto -tool statistics-by-genres -in "data\searches\search.xml"
```

```bash
  CorpusSearchAuto -tool statistics-by-genres -in "data\searches\search.xml" -show-only hits
```

```bash
  CorpusSearchAuto -tool statistics-by-genres -in "data\searches\search.xml" -show-only hits -out-format excel -out "C:\search_output"
```

## License

  [MIT](LICENSE)
