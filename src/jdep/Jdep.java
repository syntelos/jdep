package jdep;

import java.io.DataInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * 
 */
public class Jdep
    extends java.net.URLClassLoader
{

    private boolean listtop = false, recursive = false;

    private Map<String,ClassFile> classes = new HashMap<String,ClassFile>();


    public Jdep(URL[] path){
	super(path);
    }


    public Jdep listtop(){
	this.listtop = (!this.listtop);
	return this;
    }
    public Jdep recursive(){
	this.recursive = (!this.recursive);
	return this;
    }
    public Jdep list(String classname, PrintStream out){
	return this.list(classname,new List.Flat(out));
    }
    public Jdep list(String classname, List out){

	return this.list(this.find(classname),out);
    }
    public Jdep list(ClassFile cf, List out){
	if (null != cf){

	    if (out.listBegin(cf)){

		ClassFile sf = cf.getSuperClassFile();
		while (null != sf){
		    this.list(sf,out);
		    sf = sf.getSuperClassFile();
		}

		for (Pool p: cf.pool){
		    if (p instanceof Pool.Class){
			ClassFile cc = (ClassFile)p.getValue(cf);
			if (null != cc){

			    this.list(cc,out);
			}
		    }
		}

		out.listEnd(cf);
	    }
	}
	return this;
    }
    public ClassFile lookup(String name){
	return this.classes.get(name.replace('.','/')+".class");
    }
    public ClassFile find(String name){
	if (null == name)
	    return null;
	else if (name.startsWith("java") && (!this.listtop))
	    return null;
	else {
	    ClassFile cf = this.lookup(name);
	    if (null == cf){
		try {
		    cf = new ClassFile(this,name);
		    this.classes.put(cf.name,cf);
		}
		catch (java.io.IOException exc){
		    //exc.printStackTrace()
		}
	    }
	    return cf;
	}
    }
    public DataInputStream getClassResource(String name){
	InputStream in = super.getResourceAsStream(name);
	if (null == in){
	    in = super.getResourceAsStream("/"+name);
	    if (null == in)
		return null;
	}
	return new DataInputStream(in);
    }

    public enum Option {
	CLASS, HELP, TOP, PATH, R;

	public final static Option For(String arg){
	    if (null == arg)
		return Option.HELP;
	    else {
		while (0 < arg.length() && '-' == arg.charAt(0))
		    arg = arg.substring(1);
		try {
		    return Option.valueOf(arg.toUpperCase());
		}
		catch (RuntimeException exc){
		    return Option.HELP;
		}
	    }
	}
    }
    public static void usage(){
	System.out.println("Usage");
	System.out.println();
	System.out.println("  jdep [--r] [--top] --path file.jar --class pkg.class ");
	System.out.println();
	System.out.println("Description");
	System.out.println();
	System.out.println("  List (recursive) class dependencies from class in path.");
	System.out.println();
	System.out.println("  Path is the usual colon (:) delimited list of directories");
	System.out.println("  and jar files.");
	System.out.println();
	System.out.println("  Class is a fully qualified dot delimited classname.  Inner");
	System.out.println("  classes are delimited with '$'.");
	System.out.println();
	System.out.println("  With 'top', list the 'java.*' top level classes.");
	System.out.println();
	System.exit(1);
    }
    public static void main(String[] argv){
	final int argc = argv.length;
	try {
	    boolean listtop = false, recursive = false;
	    URL[] path = null;
	    String classname = null;

	    for (int cc = 0; cc < argc; cc++){
		String arg = argv[cc];
		switch(Option.For(arg)){
		case CLASS:
		    cc += 1;
		    if (cc < argc){
			arg = argv[cc];
			classname = arg;
		    }
		    else
			usage();
		    break;
		case HELP:
		    usage();
		    break;
		case TOP:
		    listtop = true;
		    break;
		case PATH:
		    cc += 1;
		    if (cc < argc){
			arg = argv[cc];
			try {
			    path = ClassPath(path,arg);
			}
			catch (Exception exc){
			    System.err.printf("Error parsing path '%s'.%n",arg);
			    System.exit(1);
			}
		    }
		    else
			usage();
		    break;
		case R:
		    recursive = true;
		    break;
		default:
		    throw new Error(arg);
		}
	    }

	    if (null != path && null != classname){

		Jdep main = new Jdep(path);
		if (listtop)
		    main.listtop();
		if (recursive)
		    main.recursive();

		main.list(classname,System.out);

		System.exit(0);
	    }
	    else
		usage();
	}
	catch (Exception exc){
	    exc.printStackTrace();
	    System.exit(1);
	}
    }

    public final static URL[] Cat(URL[] a, URL[] b){
	if (null == a)
	    return b;
	else if (null == b)
	    return a;
	else {
	    int alen = a.length;
	    int blen = b.length;
	    URL[] copier = new URL[alen+blen];
	    System.arraycopy(a,0,copier,0,alen);
	    System.arraycopy(b,0,copier,alen,blen);
	    return copier;
	}
    }
    public final static URL[] ClassPath(URL[] classpath, String path)
	throws java.net.MalformedURLException
    {
	if (null != path){
	    StringTokenizer strtok = new StringTokenizer(path,":");
	    final int count = strtok.countTokens();
	    if (0 < count){
		URL[] list = new URL[count];
		for (int cc = 0; cc < count; cc++){
		    String pel = strtok.nextToken();
		    try {
			list[cc] = new URL(pel);
		    }
		    catch (java.net.MalformedURLException exc){
			list[cc] = new URL("file:"+pel);
		    }
		}
		return list;
	    }
	}
	return classpath;
    }
}
