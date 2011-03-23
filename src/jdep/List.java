package jdep;

import java.io.PrintStream;

/**
 * Output sink.
 */
public interface List {

    /**
     * Printer
     */
    public static class Flat
	extends Object
	implements List
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


	public boolean listBegin(ClassFile list){
	    if (!list.visited){
		list.visited = true;
		this.out.printf(this.fmt,list.name);
		return true;
	    }
	    else
		return false;
	}
	public void listItem(ClassFile item){
	    if (!item.visited){
		item.visited = true;
		this.out.printf(this.fmt,item.name);
	    }
	}
	public void listEnd(ClassFile clas){
	}
    }


    public boolean listBegin(ClassFile list);

    public void listItem(ClassFile item);

    public void listEnd(ClassFile list);

}
