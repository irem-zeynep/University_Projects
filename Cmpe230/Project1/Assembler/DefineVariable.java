package Cmpe230;
//Defines variables while reading the file first time. At that instant, bp register is used to hold available memory location.
public class DefineVariable extends Helper{
    //Defines word sized variable.
    public static void defineWord(String stringVariable){
        int variable = convertInteger(stringVariable);
        //If variable is number, put its value available memory location(s) according to its size.
        if(isNumber) {
            if(variable > 65535) {
                String binaryFormat = Integer.toBinaryString(variable);
                memory[twoByteRegisters[bp]+1] = (short)Integer.parseInt(binaryFormat.substring(binaryFormat.length()-16, binaryFormat.length()-8), 2);
                memory[twoByteRegisters[bp]] = (short)Integer.parseInt(binaryFormat.substring(binaryFormat.length()-8), 2);
            } else if(variable <= 255) {
                memory[twoByteRegisters[bp]] = (short) variable;
            } else {
                String binaryFormat = Integer.toBinaryString(variable);
                memory[twoByteRegisters[bp]+1] = (short)Integer.parseInt(binaryFormat.substring(0, binaryFormat.length()-8), 2);
                memory[twoByteRegisters[bp]] = (short)Integer.parseInt(binaryFormat.substring(binaryFormat.length()-8), 2);
            }
        }
        //Else if variable iz character.
        else if((stringVariable.charAt(0) == '\'' && stringVariable.endsWith("'"))|| (stringVariable.charAt(0) == '"' && stringVariable.endsWith("\""))) {
            byte first;
            //HYP86 does not work with strings.
            if (stringVariable.length() > 3) {
                System.out.println(" Word-Sized Constant Required at line " + pc); System.exit(0);
            } else if (stringVariable.length() == 3) {
                first = (byte) stringVariable.charAt(1);
                memory[twoByteRegisters[bp]] = first;
            }
        } else {
            System.out.println("Syntax error at line " + pc); System.exit(0);
        }
    }
    //Defines byte sized variable.
    public static void defineByte(String stringVariable){
        int variable = convertInteger(stringVariable);
        if(isNumber){
            if(variable > 255) {
                System.out.println(" Byte-Sized Constant Required at line " + pc); System.exit(0);
            } else {
                memory[twoByteRegisters[bp]] = (short) variable;
            }
        } else if((stringVariable.charAt(0) == '\'' && stringVariable.endsWith("'")) || (stringVariable.charAt(0) == '"' && stringVariable.endsWith("\""))) {
            byte first;
            //HYP86 does not work with strings.
            if (stringVariable.length() > 3) {
                System.out.println(" Byte-Sized Constant Required at line " + pc); System.exit(0);
            } else if (stringVariable.length() == 3) {
                first = (byte) stringVariable.charAt(1);
                memory[twoByteRegisters[bp]] = first;
            }
        }  else {
            System.out.println("Syntax error at line " + pc); System.exit(0);
        }
    }
}
