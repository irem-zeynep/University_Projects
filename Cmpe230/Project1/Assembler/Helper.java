package Cmpe230;

public class Helper extends Main{
    static boolean isNumber = false, registerIndirectAddressing = false, hasSubReg = false, isAddress = false;
    //Methods that return index of two byte sized register if second operand is one of them. Else it returns -1.
        public static int determineWordRegister(String secondOperand) {
        int reg = -1;
        if (hasSubReg = secondOperand.equalsIgnoreCase("ax")) {
            reg = ax;
        } else if (hasSubReg = secondOperand.equalsIgnoreCase("bx")) {
            reg = bx;
        } else if (hasSubReg = secondOperand.equalsIgnoreCase("cx")) {
            reg = cx;
        } else if (hasSubReg = secondOperand.equalsIgnoreCase("dx")) {
            reg = dx;
        } else if (secondOperand.equalsIgnoreCase("si") || secondOperand.equalsIgnoreCase("SI")) {
            reg = si;
        } else if (secondOperand.equalsIgnoreCase("di") || secondOperand.equalsIgnoreCase("DI")) {
            reg = di;
        } else if (secondOperand.equalsIgnoreCase("bp")) {
            reg = bp;
        }
        return reg;
    }
    //Methods that return index of one byte sized register if second operand is one of them. Else it returns -1.
    public static int determineByteRegister(String secondOperand) {
        int reg = -1;
        if (secondOperand.equalsIgnoreCase("ah")) {
            reg = ah;
        } else if (secondOperand.equalsIgnoreCase("bh")) {
            reg = bh;
        } else if (secondOperand.equalsIgnoreCase("ch")) {
            reg = ch;
        } else if (secondOperand.equalsIgnoreCase("dh")) {
            reg = dh;
        } else if (secondOperand.equalsIgnoreCase("al")) {
            reg = al;
        } else if (secondOperand.equalsIgnoreCase("bl")) {
            reg = bl;
        } else if (secondOperand.equalsIgnoreCase("cl")) {
            reg = cl;
        } else if (secondOperand.equalsIgnoreCase("dl")) {
            reg = dl;
        }
        return reg;
    }
    //Methods that converts operand into integer and returns it if it is in integer format.
    public static int convertInteger(String operand){
        int base = -1;
        isNumber = false; //If operand is in integer format, assigned as true, else false.
        if (operand.matches("[0-9a-fA-F]+h") || operand.charAt(0) == '0') { //Means address is in hexadecimal base
            base = 16;
            operand = (operand.endsWith("h") || operand.endsWith("H")) ? operand.substring(0,operand.length()-1) : operand ;
        } else if (operand.matches("\\d+") || operand.matches("\\d+d")) { //Means address is in decimal base
            base = 10;
            operand = (operand.endsWith("d") || operand.endsWith("D")) ? operand.substring(0,operand.length()-1) : operand ;
        } else if (operand.matches("[01]+b")) { //Means address is in binary base
            base = 2;
            operand = (operand.endsWith("b") || operand.endsWith("B")) ? operand.substring(0,operand.length()-1) : operand ;
        } else if (operand.equalsIgnoreCase("sp")) {
            isNumber = true;
            return sp;
        }
        if (base != -1) {
            isNumber = true;
            return Integer.parseInt(operand, base);
        } else
            return -1;
    }
    //If operand is used in register indirect addressing, returns address value stored in that register.
    public static int determineRegisterAddress(String operand){
       int address;
       registerIndirectAddressing = false;
        if (operand.equalsIgnoreCase("bx")) {
            address = twoByteRegisters[bx];
        } else if (operand.equalsIgnoreCase("SI") || operand.equalsIgnoreCase("si")) {
            address = twoByteRegisters[si];
        } else if (operand.equalsIgnoreCase("DI") || operand.equalsIgnoreCase("di") ) {
            address = twoByteRegisters[di];
        } else if (operand.equalsIgnoreCase("bp")) {
            address = oneByteRegisters[bp];
        } else
            address = -1;
        if(address != -1) registerIndirectAddressing = true;
        return address;
    }
    //Methods that determines value of address and returns that address.
    public static int determineAddress(String operand){
        int address = -1;
        isAddress = false;
        if (isAddress = (operand.matches("-?[0-9a-fA-F]+h") || operand.charAt(0) == '0')) { //means address is hexadecimal
            address =  (operand.endsWith("h") || operand.endsWith("H"))? Integer.parseInt(operand.substring(0,operand.length()-1), 16) : Integer.parseInt(operand, 16);
        } else if (isAddress = (operand.matches("\\d+") || operand.matches("\\d+d"))) { //means address is decimal
            address =  (operand.endsWith("d") || operand.endsWith("D"))? Integer.parseInt(operand.substring(0,operand.length()-1)) : Integer.parseInt(operand);
        } else if (isAddress = operand.matches("[01]+b")) { //means address is binary
            address =  (operand.endsWith("b") || operand.endsWith("B"))? Integer.parseInt(operand.substring(0,operand.length()-1), 2) : Integer.parseInt(operand, 2);
        } else if (isAddress = operand.equalsIgnoreCase("bx")) {
            address = twoByteRegisters[bx];
        } else if (isAddress = (operand.equalsIgnoreCase("SI") || operand.equalsIgnoreCase("si"))) {
            address = twoByteRegisters[si];
        } else if (isAddress = (operand.equalsIgnoreCase("DI") || operand.equalsIgnoreCase("di"))) {
            address = twoByteRegisters[di];
        } else if (isAddress = operand.equalsIgnoreCase("BP")) {
            address = twoByteRegisters[bp];
        }
        return address;
    }
    //Updates child register of two byte register.
    public static void updateChild(int destination){
        String binaryFormat = Integer.toBinaryString(twoByteRegisters[destination]);
        if (binaryFormat.length() > 8) {
            oneByteRegisters[2*destination] = (short)Integer.parseInt(binaryFormat.substring(0, binaryFormat.length() - 8),2);
            oneByteRegisters[2*destination+1] = (short)Integer.parseInt(binaryFormat.substring(binaryFormat.length() - 8),2);
        } else {
            oneByteRegisters[2*destination+1] = (short)Integer.parseInt(binaryFormat,2);
            oneByteRegisters[2*destination] = 0;
        }
    }
    //Updates parent register of one byte register.
    public static void updateParent(int destination){
        if (destination%2 == 0) {
            twoByteRegisters[destination/2] =  (oneByteRegisters[destination]<<8) | oneByteRegisters[destination+1];
        } else {
            twoByteRegisters[destination/2] =  (oneByteRegisters[destination-1]<<8) | oneByteRegisters[destination];
        }
    }

}
