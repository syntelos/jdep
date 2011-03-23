
Work in progress

    Usage
    
      jdep [--top] --path file.jar --class pkg.class 
    
    Description
    
      Recursively list class dependencies from class in path.
    
      Path is the usual (classpath) colon (:) delimited list of
      file system directories and jar files.
    
      Class is a fully qualified dot delimited classname.  Inner
      classes are delimited with '$'.
    
      With 'top', list the 'java.*' top level classes.
    
