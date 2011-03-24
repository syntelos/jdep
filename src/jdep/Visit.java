package jdep;

import java.io.PrintStream;

/**
 * Output sink.
 */
public interface Visit {

    /**
     * Printer
     */
    public static class Flat
	extends Object
	implements Visit
    {

	protected PrintStream out;

	protected String fmt = "%s%n";


	public Flat(PrintStream out){
	    super();
	    if (null != out)
		this.out = out;
	    else
		throw new IllegalArgumentException();
	}


	public boolean visit(ClassFile cf){
	    if (!cf.visited){
		cf.visited = true;
		final String name = cf.name;
		if (0 > name.indexOf('$'))
		    this.out.printf(this.fmt,name);
		return true;
	    }
	    else
		return false;
	}
    }


    public boolean visit(ClassFile cf);

}
