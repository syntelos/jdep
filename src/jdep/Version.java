package jdep;

public final class Version
    extends Object
{
    public final static String Name = "jdep";
    public final static int Major   =  0;
    public final static int Minor   =  0;
    public final static int Build   =  1;


    public final static String Number = String.valueOf(Major)+'.'+String.valueOf(Minor);

    public final static String Full = Name+'-'+Number;

    private Version(){
        super();
    }
}
