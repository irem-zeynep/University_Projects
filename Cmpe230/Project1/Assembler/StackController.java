package Cmpe230;

public class StackController extends Helper {
    //Pushes operand in stack. Then decrements value of stack pointer by two. Operand's type must be word.
    public static void push(String stringOperand) {
        int address, operand;
        String binaryFormat;
        //If operand is in number format, converts it into integer and pushes to stack.
        operand = convertInteger(stringOperand);
        if(isNumber){
            if(operand >= 0 && operand <= 255) {
                memory[sp] = (short) operand;
                sp -= 2;
            } else if(operand >= 0 && operand <= 65535) {
                binaryFormat = Integer.toBinaryString(operand);
                memory[sp+1] = (short)Integer.parseInt(binaryFormat.substring(0, binaryFormat.length()-8), 2);
                memory[sp] = (short)Integer.parseInt(binaryFormat.substring(binaryFormat.length()-8), 2);
                sp -= 2;
            } else {
                System.out.println("Word-sized required at line at " + pc); System.exit(0);
            }

            //If operand is register, pushes its value to stack.
        } else if (stringOperand.equalsIgnoreCase("ax")) {
            memory[sp] =  oneByteRegisters[al];
            memory[sp+1] =  oneByteRegisters[ah];
            sp -= 2;
        } else if (stringOperand.equalsIgnoreCase("bx")) {
            memory[sp] = oneByteRegisters[bl];
            memory[sp+1] =  oneByteRegisters[bh];
            sp -= 2;
        } else if (stringOperand.equalsIgnoreCase("cx")) {
            memory[sp] =  oneByteRegisters[cl];
            memory[sp+1] = oneByteRegisters[ch];
            sp -= 2;
        } else if (stringOperand.equalsIgnoreCase("dx")) {
            memory[sp] = oneByteRegisters[dl];
            memory[sp+1] = oneByteRegisters[dh];
            sp -= 2;
        } else if (stringOperand.equalsIgnoreCase("SI") || stringOperand.equalsIgnoreCase("si")) {
            binaryFormat = Integer.toBinaryString(twoByteRegisters[si]);
            if(binaryFormat.length() > 8){
                memory[sp+1] = (short) Integer.parseInt(binaryFormat.substring(0,binaryFormat.length()-8),2);
                memory[sp] = (short)Integer.parseInt(binaryFormat.substring(binaryFormat.length()-8),2);
            }else{
                memory[sp] = (short)Integer.parseInt(binaryFormat,2);
            }
            sp -= 2;
        } else if (stringOperand.equalsIgnoreCase("DI") || stringOperand.equalsIgnoreCase("di")) {
            binaryFormat = Integer.toBinaryString(twoByteRegisters[di]);
            if(binaryFormat.length() > 8) {
                memory[sp+1] = (short) Integer.parseInt(binaryFormat.substring(0, binaryFormat.length()-8), 2);
                memory[sp] = (short)Integer.parseInt(binaryFormat.substring(binaryFormat.length()-8), 2);
            } else {
                memory[sp] = (short)Integer.parseInt(binaryFormat,2);
            }
            sp -= 2;
        } else if (stringOperand.equalsIgnoreCase("sp")) {
            binaryFormat = Integer.toBinaryString(sp);
            if(binaryFormat.length() > 8) {
                memory[sp+1] = (short) Integer.parseInt(binaryFormat.substring(0, binaryFormat.length()-8), 2);
                memory[sp] = (short)Integer.parseInt(binaryFormat.substring(binaryFormat.length()-8), 2);
            } else {
                memory[sp] = (short)Integer.parseInt(binaryFormat,2);
            }
            sp -= 2;
        } else if (stringOperand.equalsIgnoreCase("bp")) {
            binaryFormat = Integer.toBinaryString(twoByteRegisters[bp]);
            if(binaryFormat.length() > 8) {
                memory[sp+1] = (short) Integer.parseInt(binaryFormat.substring(0, binaryFormat.length()-8), 2);
                memory[sp] = (short)Integer.parseInt(binaryFormat.substring(binaryFormat.length()-8), 2);
            } else {
                memory[sp] = (short)Integer.parseInt(binaryFormat,2);
            }
            sp -= 2;

            //If operand is memory address, pushes its value to stack.
        } else if (stringOperand.length() > 1 && (stringOperand.charAt(1) == '[' || stringOperand.charAt(0) == '[') && stringOperand.endsWith("]")) {
            if (stringOperand.charAt(0) == 'b' || stringOperand.charAt(0) == 'B') {
                System.out.println("Must be word at line " + pc); System.exit(0);
            }
            String stringAddress;
            if(stringOperand.charAt(1) == '[') stringAddress = stringOperand.substring(2, stringOperand.length() - 1);
            else stringAddress = stringOperand.substring(1, stringOperand.length() - 1);
            address = convertInteger(stringAddress);
            if (isNumber) {
                if(address < ip){
                    System.out.println("Not permitted to access that address at line " + pc); System.exit(0);
                }
                memory[sp] = memory[address];
                memory[sp+1] = memory[address + 1];
                sp -= 2;
            } else {
                address = determineRegisterAddress(stringAddress);
                if (registerIndirectAddressing) {
                    if(address < ip){
                        System.out.println("Not permitted to access that address at line " + pc); System.exit(0);
                    }
                    memory[sp] = memory[address];
                    memory[sp+1] = memory[address + 1];
                    sp -= 2;
                } else {
                    System.out.println("Syntax error at line " + pc); System.exit(0);
                }
            }
            //If operand is character, pushes its value to stack.
        } else if((stringOperand.charAt(0) == '\'' && stringOperand.endsWith("'")) || (stringOperand.charAt(0) == '"' && stringOperand.endsWith("\""))) {
            int first;
            if (stringOperand.length() > 3) {
                System.out.println("Incompatibl data type at line " + pc); System.exit(0);
            } else if (stringOperand.length() == 3) {
                first = (byte) stringOperand.charAt(1);
                memory[sp] = (short) first;
                sp -= 2;
            }
        } else {
            System.out.println("Error at line " + pc); System.exit(0);
        }
    }
    //Increment value of stack pointer by two. Pops value from stack and put that value into operand.
    public static void pop(String stringOperand){
        int address, operand;
        String binaryFormat;
        sp+=2;
        if (sp > ((2 << 15 ) - 2)) {
            System.out.println("Pop without push at line " + pc); System.exit(0);
        }
        //If operand is register put popped value into it.
        if (stringOperand.equalsIgnoreCase("ax")) {
            String lowPart = Integer.toBinaryString(memory[sp]);
            binaryFormat = Integer.toBinaryString(memory[sp+1]).concat(("00000000" + lowPart).substring(lowPart.length()));
            operand = Integer.parseInt(binaryFormat,2);
            twoByteRegisters[ax] = operand;
            oneByteRegisters[al] = memory[sp];
            oneByteRegisters[ah] = memory[sp+1];
        } else if (stringOperand.equalsIgnoreCase("bx")) {
            String lowPart = Integer.toBinaryString(memory[sp]);
            binaryFormat = Integer.toBinaryString(memory[sp+1]).concat(("00000000" + lowPart).substring(lowPart.length()));
            operand = Integer.parseInt(binaryFormat,2);
            twoByteRegisters[bx] = operand;
            oneByteRegisters[bl] = memory[sp];
            oneByteRegisters[bh] = memory[sp+1];
        } else if (stringOperand.equalsIgnoreCase("cx")) {
            String lowPart = Integer.toBinaryString(memory[sp]);
            binaryFormat = Integer.toBinaryString(memory[sp+1]).concat(("00000000" + lowPart).substring(lowPart.length()));
            operand = Integer.parseInt(binaryFormat,2);
            twoByteRegisters[cx] = operand;
            oneByteRegisters[cl] = memory[sp];
            oneByteRegisters[ch] = memory[sp+1];
        } else if (stringOperand.equalsIgnoreCase("dx")) {
            String lowPart = Integer.toBinaryString(memory[sp]);
            binaryFormat = Integer.toBinaryString(memory[sp+1]).concat(("00000000" + lowPart).substring(lowPart.length()));
            operand = Integer.parseInt(binaryFormat,2);
            twoByteRegisters[dx] = operand;
            oneByteRegisters[dl] = memory[sp];
            oneByteRegisters[dh] = memory[sp+1];
        } else if (stringOperand.equalsIgnoreCase("SI") || stringOperand.equalsIgnoreCase("si")) {
            String lowPart = Integer.toBinaryString(memory[sp]);
            binaryFormat = Integer.toBinaryString(memory[sp+1]).concat(("00000000" + lowPart).substring(lowPart.length()));
            operand = Integer.parseInt(binaryFormat,2);
            twoByteRegisters[si] = operand;
        } else if (stringOperand.equalsIgnoreCase("DI") || stringOperand.equalsIgnoreCase("di")) {
            String lowPart = Integer.toBinaryString(memory[sp]);
            binaryFormat = Integer.toBinaryString(memory[sp+1]).concat(("00000000" + lowPart).substring(lowPart.length()));
            operand = Integer.parseInt(binaryFormat,2);
            twoByteRegisters[di] = operand;
        } else if (stringOperand.equalsIgnoreCase("bp")) {
            String lowPart = Integer.toBinaryString(memory[sp]);
            binaryFormat = Integer.toBinaryString(memory[sp+1]).concat(("00000000" + lowPart).substring(lowPart.length()));
            operand = Integer.parseInt(binaryFormat,2);
            twoByteRegisters[bp] = operand;
            //If operand is memory put popped value into it.
        } else if (stringOperand.equalsIgnoreCase("sp")) {
            String lowPart = Integer.toBinaryString(memory[sp]);
            binaryFormat = Integer.toBinaryString(memory[sp+1]).concat(("00000000" + lowPart).substring(lowPart.length()));
            operand = Integer.parseInt(binaryFormat,2);
            sp = operand;
            //If operand is memory put popped value into it.
        } else if (stringOperand.length() > 1 && (stringOperand.charAt(1) == '[' || stringOperand.charAt(0) == '[')  && stringOperand.endsWith("]")) {
            if (stringOperand.charAt(0) == 'b' || stringOperand.charAt(0) == 'B') {
                System.out.println("Must be word at line " + pc); System.exit(0);
            }
            String stringAddress;
            if(stringOperand.charAt(1) == '[') stringAddress = stringOperand.substring(2, stringOperand.length() - 1);
            else stringAddress = stringOperand.substring(1, stringOperand.length() - 1);
            address = convertInteger(stringAddress);
            if (isNumber) {
                if(address < ip){
                    System.out.println("Not permitted to access that address at line " + pc); System.exit(0);
                }
                memory[address] = memory[sp];
                memory[address + 1] = memory[sp+1];
            } else {
                address = determineRegisterAddress(stringAddress);
                if (registerIndirectAddressing) {
                    if(address < ip){
                        System.out.println("Not permitted to access that address at line " + pc); System.exit(0);
                    }
                    memory[address] = memory[sp];
                    memory[address + 1] = memory[sp+1];
                } else {
                    System.out.println("Syntax error at line " + pc); System.exit(0);
                }
            }
        } else {
            System.out.println("Error at line " + pc); System.exit(0);
        }
        memory[sp] = 0;
        memory[sp+1] = 0;
    }
}


