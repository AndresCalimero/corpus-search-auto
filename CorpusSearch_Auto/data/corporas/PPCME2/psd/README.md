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

  **NOTE:** The only thing that is mandatory to have inside the data folder are the corpora and the CS.jar executable, the rest is just a suggestion to organize more efficiently the [.q search files](http://corpussearch.sourceforge.net/CS-manual/QueryLanguage.html) and the output files.
