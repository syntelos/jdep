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

import java.io.DataInput;
import java.io.IOException;

/**
 * Element of the JVM {@link ClassFile Class File} constant pool.
 */
public abstract class Pool {
    /**
     * 
     */
    public enum Tag {
        Reserved0, 
        Utf8,                    // 0x01
        Reserved2,
        Integer,                 // 0x03
        Float,                   // 0x04
        Long,                    // 0x05
        Double,                  // 0x06
        Class,                   // 0x07
        String,                  // 0x08
        FieldRef,                // 0x09
        MethodRef,               // 0x0A
        InterfaceMethodRef,      // 0x0B
        NameAndType;             // 0x0C

        private final static Tag[] Values = Tag.values();

        public final static Tag _(int idx){

            return Values[idx];
        }
        public final static Tag _(DataInput din) throws IOException {

            return Tag._(din.readByte() & 0xFF);
        }
    }
    public final static Pool Read(DataInput din) throws IOException {
        switch(Pool.Tag._(din)){
        case Utf8:
            return new Pool.Utf8(din);
        case Integer:
            return new Pool.Integer(din);
        case Float:
            return new Pool.Float(din);
        case Long:
            return new Pool.Long(din);
        case Double:
            return new Pool.Double(din);
        case Class:
            return new Pool.Class(din);
        case String:
            return new Pool.String(din);
        case FieldRef:
            return new Pool.FieldRef(din);
        case MethodRef:
            return new Pool.MethodRef(din);
        case InterfaceMethodRef:
            return new Pool.InterfaceMethodRef(din);
        case NameAndType:
            return new Pool.NameAndType(din);
        default:
            throw new Error();
        }
    }
    /**
     * 
     */
    public final static class Utf8
        extends Pool
    {
        public final java.lang.String value;


        public Utf8(DataInput din) throws IOException {
            super(Pool.Tag.Utf8);

            StringBuilder strbuf = new StringBuilder();

            final int len = (din.readShort() & 0xFFFF);
            int charCnt = 0;
            int byte8, char16;

            while (charCnt < len) {

                byte8 = (din.readByte() & 0xFF);

                charCnt++;

                if (1 == (byte8 & 0x80)) {
                    /*
                     * A multi-byte character (!)
                     * 
                     * (6) Bits (5:0)..
                     */
                    char16 = (byte8 & 0x3F);

                    byte8 = (din.readByte() & 0xFF);

                    charCnt++;
                    /*
                     * (6) Bits (11:6)..
                     */
                    char16 |= ((byte8 & 0x3F) << 6);

                    if (1 == (byte8 & 0x80)) {
                        /*
                         * (4) Bits (15:12)..
                         */
                        byte8 = (din.readByte() & 0xFF);

                        charCnt++;

                        byte8 = (byte8 & 0xF);

                        char16 |= (byte8 << 12);
                    }

                    strbuf.append( (char)(char16 & 0xFFFF));
                }
                else {

                    strbuf.append( (char)(byte8 & 0xFF));
                }
            }
            this.value = strbuf.toString();
        }


        public Object getValue(ClassFile c){
            return this.value;
        }
    }
    /**
     * 
     */
    public final static class Integer
        extends Pool
    {
        public final java.lang.Integer value;


        public Integer(DataInput din) throws IOException {
            super(Pool.Tag.Integer);

            this.value = new java.lang.Integer(din.readInt());
        }


        public Object getValue(ClassFile c){
            return this.value;
        }
    }
    /**
     * 
     */
    public final static class Float
        extends Pool
    {
        public final java.lang.Float value;


        public Float(DataInput din) throws IOException {
            super(Pool.Tag.Float);

            this.value = new java.lang.Float(din.readFloat());
        }


        public Object getValue(ClassFile c){
            return this.value;
        }
    }
    /**
     * 
     */
    public final static class Long
        extends Pool
    {
        public final java.lang.Long value;


        public Long(DataInput din) throws IOException {
            super(Pool.Tag.Long);

            this.value = new java.lang.Long(din.readLong());
        }


        public Object getValue(ClassFile c){
            return this.value;
        }
    }
    /**
     * 
     */
    public final static class Double
        extends Pool
    {
        public final java.lang.Double value;


        public Double(DataInput din) throws IOException {
            super(Pool.Tag.Double);

            this.value = new java.lang.Double(din.readDouble());
        }


        public Object getValue(ClassFile c){
            return this.value;
        }
    }
    /**
     * 
     */
    public final static class Class
        extends Pool
    {
        public final int index;
        private ClassFile value;


        public Class(DataInput din) throws IOException {
            super(Pool.Tag.Class);

            this.index = (din.readShort() & 0xFFFF);
        }


        public Object getValue(ClassFile c){
            ClassFile value = this.value;
            if (null == value){
		java.lang.String classname = (java.lang.String)c.getConst(this.index);
		value = c.find(classname);
		this.value = value;
	    }
	    return value;
        }
    }
    /**
     * 
     */
    public final static class String
        extends Pool
    {
        public final int index;
        private java.lang.String value;


        public String(DataInput din) throws IOException {
            super(Pool.Tag.String);

            this.index = (din.readShort() & 0xFFFF);
        }


        public Object getValue(ClassFile c){
            java.lang.String value = this.value;
            if (null == value){
                value = (java.lang.String)c.getConst(this.index);
                this.value = value;
            }
            return value;
        }
    }
    /**
     * 
     */
    public final static class FieldRef
        extends Pool
    {
        public final int classIndex, nameAndTypeIndex;

	private Pool.NameAndType value;


        public FieldRef(DataInput din) throws IOException {
            super(Pool.Tag.FieldRef);

            this.classIndex = (din.readShort() & 0xFFFF);
            this.nameAndTypeIndex = (din.readShort() & 0xFFFF);
        }


        public Object getValue(ClassFile c){
	    Pool.NameAndType value = this.value;
            if (null == value){
                ClassFile clas = (ClassFile)c.getConst(this.classIndex);
                if (null != clas){
                    value = (Pool.NameAndType)c.getConst0(this.nameAndTypeIndex);
                    this.value = value;
                }
            }
            return value;
        }
    }
    /**
     * 
     */
    public final static class MethodRef
        extends Pool
    {
        public final int classIndex, nameAndTypeIndex;

	private Pool.NameAndType value;


        public MethodRef(DataInput din) throws IOException {
            super(Pool.Tag.MethodRef);

            this.classIndex = (din.readShort() & 0xFFFF);
            this.nameAndTypeIndex = (din.readShort() & 0xFFFF);
        }


        public Object getValue(ClassFile c){
	    Pool.NameAndType value = this.value;
            if (null == value){
                ClassFile clas = (ClassFile)c.getConst(this.classIndex);
                if (null != clas){
                    value = (Pool.NameAndType)c.getConst0(this.nameAndTypeIndex);
                    this.value = value;
                }
            }
            return value;
        }
    }
    /**
     * 
     */
    public final static class InterfaceMethodRef
        extends Pool
    {
        public final int classIndex, nameAndTypeIndex;

	private Pool.NameAndType value;


        public InterfaceMethodRef(DataInput din) throws IOException {
            super(Pool.Tag.InterfaceMethodRef);

            this.classIndex = (din.readShort() & 0xFFFF);
            this.nameAndTypeIndex = (din.readShort() & 0xFFFF);
        }


        public Object getValue(ClassFile c){
	    Pool.NameAndType value = this.value;
            if (null == value){
                ClassFile clas = (ClassFile)c.getConst(this.classIndex);
                if (null != clas){
                    value = (Pool.NameAndType)c.getConst0(this.nameAndTypeIndex);
                    this.value = value;
                }
            }
            return value;
        }
    }
    /**
     * 
     */
    public final static class NameAndType
        extends Pool
    {
        public final int nameIndex, typeIndex;
        private java.lang.String nameValue, typeValue;


        public NameAndType(DataInput din) throws IOException {
            super(Pool.Tag.NameAndType);

            this.nameIndex = (din.readShort() & 0xFFFF);
            this.typeIndex = (din.readShort() & 0xFFFF);
        }



        public java.lang.String getName(ClassFile c){
            java.lang.String nameValue = this.nameValue;
            if (null == nameValue){
                nameValue = (java.lang.String)c.getConst(this.nameIndex);
                this.nameValue = nameValue;
            }
            return nameValue;
        }
        public java.lang.String getType(ClassFile c){
            java.lang.String typeValue = this.typeValue;
            if (null == typeValue){
                typeValue = (java.lang.String)c.getConst(this.typeIndex);
                this.typeValue = typeValue;
            }
            return typeValue;
        }
        public Object getValue(ClassFile c){
            return this.getName(c);
        }
    }


    public final Pool.Tag tag;


    public Pool(Pool.Tag t) {
        super();
        this.tag = t;
    }

    public abstract Object getValue(ClassFile c);
}
