
Status

  The result set has not been proven to be complete.  Unexpected
  experiences parsing the class file constant pool motivate a proof of
  the result set.

Usage

  jdep [--top] --path file.jar --class pkg.class 

Description

  Recursively list class dependencies from class in path.

  Path is the usual (classpath) colon (:) delimited list of
  file system directories and jar files.

  Class is a fully qualified dot delimited classname.  Inner
  classes are delimited with '$'.

  With 'top', list the 'java.*' top level classes.

Notes

  Inner classes are not reported.
