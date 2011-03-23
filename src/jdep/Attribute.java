/*
 * Aino Java PIC Compiler
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

import java.io.DataInputStream;
import java.io.IOException;

public class Attribute {
    int nameIndex;
    int length;
    byte[] info;
    ClassFile cls;


    public Attribute(int ni, int len, ClassFile c, DataInputStream ios) throws IOException {
        super();
        this.nameIndex = ni;
        this.length = len;
        this.cls = c;
        this.info = new byte[len];
        int read, ofs = 0;
        while (0 < len){
            read = ios.read(this.info,ofs,len);
            len -= read;
            ofs += read;
        }
    }


    public int getByte(int i) {

        return (info[i] & 0xff);
    }
    public int getSignByte(int i) {

        return info[i];
    }
    public int getShort(int i) {

        return (getByte(i)<<8)|getByte(i+1);
    }
    public int getSignShort(int i) {

        return (getSignByte(i)<<8)+getByte(i+1);
    }
    public int getInt(int i) {

        return ( (getByte(i)<<24)|
                 (getByte(i+1)<<16)|
                 (getByte(i+2)<<8)|
                 (getByte(i+3) & 0xFF));
    }
    public int getCodeLength() {

        return getInt(4);
    }
    public int getCodeByte(int i) {

        return getByte(i+8);
    }
    public int getCodeShort(int i) {

        return getShort(i+8);
    }
    public int getSignCodeShort(int i) {

        return getSignShort(i+8);
    }
    public int getCodeInt(int i) {

        return getInt(i+8);
    }
    public int getConstantValue() {

        return getShort(0);
    }
    public int getMaxStack() {

        return getShort(0);
    }
    public int getMaxLocals() {

        return getShort(2);
    }
    public int getExceptionTableLength() {

        return getShort(8 + getCodeLength());
    }
    public int getAttributesCount() {

        return getShort(8 + getCodeLength() + getExceptionTableLength()*8);
    }
    public boolean isRuntimeAnnotations(){
        return (this.cls.getConst(this.nameIndex).equals("RuntimeVisibleAnnotations"));
    }
    public boolean isLineNumberTable(){
        return (this.cls.getConst(this.nameIndex).equals("LineNumberTable"));
    }
    public int getLineNumber(int pc) {
        int exps_p = 8 + getCodeLength();
        int exptbllen = getShort(exps_p);
        int attrscnt_p = 8 * exptbllen + 2 + exps_p;
        int attrs = getShort(attrscnt_p);
        int i = attrscnt_p + 2;
        while (i < length) {
            int nameix = getShort(i);
            int len = getInt(i+2);
            i += 6;
            if (cls.getConst(nameix).equals("LineNumberTable")) {
                int linenumtbllen = getShort(i);
                for (int j = 0; j < linenumtbllen; j++) {
                    int c = getShort(i+2+4*j);
                    if (c == pc) return getShort(i+4+4*j);
                    if (c > pc) return 0;
                }
            }
            i += len;
        }
        return 0;
    }

    public final static Attribute[] Add(Attribute[] list, Attribute item){
        if (null == item)
            return list;
        else if (null == list)
            return new Attribute[]{item};
        else {
            int count = list.length;
            Attribute[] copier = new Attribute[count];
            System.arraycopy(list,0,copier,0,count);
            copier[count] = item;
            return copier;
        }
    }
}

