/*
 * Aino Compiler
 * Copyright 2003 Hannu Jokinen, Finland
 * Copyright 2010 John Pritchard, US
 * 
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see
 * <http://www.gnu.org/licenses/>.
 */
package jdep;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * 
 */
public final class ClassFile
    extends Object
{
    private Jdep jdep;

    public final String name;

    public final int minorVersion, majorVersion;

    public final Pool[] pool;

    public final int[] types;

    public final int accessFlags;
    public final int thisClass;
    public final int superClass;

    public boolean visited;

    private ClassFile superClassFile;


    public ClassFile(Jdep jdep, String name)
        throws IOException
    {
        super();
	this.jdep = jdep;
	this.name = name.replace('.','/');

        DataInputStream ios = jdep.getClassResource(this.name+".class");
        if (null == ios)
            throw new java.io.FileNotFoundException(name);

        try {
            /*
             * File magic
             */
            final int magic = ios.readInt();
            if (magic != 0xCAFEBABE)
                throw new IOException("File is not a java class file.");

            /*
             * File version
             */
            this.minorVersion = ios.readShort();
	    this.majorVersion = ios.readShort();

            final int poolCount = ios.readShort();
            /*
             * Read constant pool
             */
            this.pool = new Pool[poolCount];

            for (int i = 1; i < poolCount; i++) {

                this.pool[i] =  Pool.Read(ios);
            }

            this.accessFlags = ios.readShort();
            this.thisClass = ios.readShort();
            this.superClass  = ios.readShort();

            /*
             * Types
             */
            final int interfacesCount = ios.readShort();
            this.types = new int[interfacesCount];

            for (int i = 0; i < interfacesCount; i++) {

                this.types[i] = (ios.readShort() & 0xFFFF);
            }
        }
        finally {
            if (null != ios){
                try {
                    ios.close();
                }
                catch (IOException exc){
                }
            }
        }
    }


    public ClassFile lookup(String name){
        return this.jdep.lookup(name);
    }
    public ClassFile find(String name){
	if (null == name)
	    return null;
	else if (name.equals(this.name))
	    return this;
	else
	    return this.jdep.find(name);
    }
    public String getSuperClassName() {
        return (String)this.getConst(this.superClass);
    }
    public ClassFile getSuperClassFile(){
        ClassFile superClass = this.superClassFile;
        if (null == superClass){
	    superClass = this.jdep.find(this.getSuperClassName());
	    this.superClassFile = superClass;
        }
        return superClass;
    }
    public Pool.Tag getTag(int idx){
        return pool[idx].tag;
    }
    public boolean isTag(int idx, Pool.Tag tag){
        return (tag == pool[idx].tag);
    }
    public Object getConst(int idx) {

        return this.pool[idx].getValue(this);
    }
    public Pool getConst0(int idx) {

        return this.pool[idx];
    }
    public String getSimpleClassName(int index) {

        return SimpleName(this.getClassName(index));
    }
    public String getMethodClassName(int index){

        return (String)this.getConst(index);
    }
    public String getClassName(int index) {

        return (String)this.getConst(index);
    }
    public String getName(int index) {

        return (String)this.getConst(index);
    }
    public String getDescriptor(int index) {

        return (String)this.getConst(index);
    }
    public String getMemberName(int index) {

        final String c = this.getSimpleClassName(index);

        final String f = this.getName(index);

        return String.format("%s_%s",c,f);
    }
    public int countPool(){
        if (null == pool)
            return 0;
        else
            return pool.length;
    }
    public String getName(){
        return this.name;
    }
    public String getSimpleName(){
        return SimpleName(this.name);
    }
    public int hashCode(){
        return this.name.hashCode();
    }
    public boolean equals(Object that){
        if (that instanceof ClassFile)
            return (0 == this.compareTo( (ClassFile)that));
        else
            return false;
    }
    public int compareTo(ClassFile that){
        if (null == that)
            return 1;
        else 
            return this.name.compareTo(that.name);
    }

    public final static ClassFile[] Add(ClassFile[] list, ClassFile item){
        if (null == item)
            return list;
        else if (null == list)
            return new ClassFile[]{item};
        else {
            final int count = list.length;
            ClassFile[] copier = new ClassFile[count+1];
            System.arraycopy(list,0,copier,0,count);
            copier[count] = item;
            return copier;
        }
    }
    public final static String SimpleName(String classname){
        int idx = classname.lastIndexOf('/');
        if (-1 < idx)
            return classname.substring(idx+1);
        else
            return classname;
    }
}
