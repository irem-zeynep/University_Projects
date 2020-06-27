package Cmpe230;

public class Multiply extends Helper{
    //Multiply value in two byte register with value in ax. Put it in ax and dx if necessary.
    public static void multiplyByWordReg(int reg){
        int product = twoByteRegisters[reg]*twoByteRegisters[ax];
        String binaryFormat = Integer.toBinaryString(product);
        if(of = cf = (binaryFormat.length() > 16)) {
            twoByteRegisters[dx] = Integer.parseInt(binaryFormat.substring(0, binaryFormat.length() - 16),2);
            twoByteRegisters[ax] = Integer.parseInt(binaryFormat.substring(binaryFormat.length() - 16),2);
        } else {
            twoByteRegisters[ax] = Integer.parseInt(binaryFormat,2);
        }
        fillChildReg(true);
    }
    //Multiply value in two byte sized memory location with value in ax. Put it in ax and dx if necessary.
    public static void multiplyByWordMemory(String operand){
        int product, address;
        address = determineAddress(operand);
        //If address location belongs to instructions, gives error.
        if(address < ip){
            System.out.println("Not permitted to access that address at line " + pc); System.exit(0);
        }
        String append = Integer.toBinaryString(memory[address]);
        String stringOperand = Integer.toBinaryString(memory[address+1]).concat(("00000000" + append).substring(append.length()));
        int operandValue = Integer.parseInt(stringOperand,2);
        product = operandValue*twoByteRegisters[ax];
        String binaryFormat = Integer.toBinaryString(product);
        if(of = cf = (binaryFormat.length() > 16)){
            twoByteRegisters[dx] = Integer.parseInt(binaryFormat.substring(0, binaryFormat.length() - 16),2);
            twoByteRegisters[ax] = Integer.parseInt(binaryFormat.substring(binaryFormat.length() - 16),2);
        }else{
            twoByteRegisters[ax] = Integer.parseInt(binaryFormat,2);
        }
        fillChildReg(true);
    }
    //Multiply value in one byte register with value in ax. Put it in ax.
    public static void multiplyByByteReg(int reg){
        twoByteRegisters[ax] = oneByteRegisters[reg]*oneByteRegisters[al];
        fillChildReg(false);
    }
    //Multiply value in one byte sized memory location with value in ax. Put it in ax.
    public static void multiplyByByteMemory(String operand){
        int address = determineAddress(operand);
        //If address location belongs to instructions, gives error.
        if(address < ip){
            System.out.println("Not permitted to access that address at line " + pc); System.exit(0);
        }
        twoByteRegisters[ax] = memory[address]*oneByteRegisters[al];
        fillChildReg(false);
    }
    //Calls multiplyByByteNumber, multiplyByWordNumber, multiplyByChar methods according to type of operand. Realize multiplication.
    public static void multiplyByImmediate(String operand){
        int operandValue = sp;
        if (operandValue < -32768 || operandValue > 327768) {
            System.out.println("Word-sized required at line " + pc); System.exit(0);
        } else if (operandValue >= -128 && operandValue <= 127) {
            multiplyByByteNumber(operandValue);
        } else {
            multiplyByWordNumber(operandValue);
        }
    }

    public static void multiplyByByteNumber(int operand){
        twoByteRegisters[ax] = oneByteRegisters[al] * operand;
        fillChildReg(false);
    }
    public static void multiplyByWordNumber(int operand){
        int product = twoByteRegisters[ax] * operand;
        String binaryFormat = Integer.toBinaryString(product);
        if(of = cf = (binaryFormat.length() > 16)){
            twoByteRegisters[dx] = Integer.parseInt(binaryFormat.substring(0, binaryFormat.length() - 16),2);
            twoByteRegisters[ax] = Integer.parseInt(binaryFormat.substring(binaryFormat.length() - 16),2);
        }else{
            twoByteRegisters[ax] = Integer.parseInt(binaryFormat,2);
        }
        fillChildReg(true);
    }
    //Fill child registers of ax and dx if necessary.
    public static void fillChildReg(boolean isBoth){
        updateChild(ax);
        if(isBoth) updateChild(dx);
    }
}
