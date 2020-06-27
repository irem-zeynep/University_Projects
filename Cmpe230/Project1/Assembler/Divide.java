package Cmpe230;

public class Divide extends Helper {
    //Divide value in dx and ax value in two byte register. Put it quotient in ax and remainder in dx.
    public static void divideByWordReg(int reg){
        int q, r;
        int divisor = twoByteRegisters[reg];
        //If divisor is zero gives error.
        if(divisor == 0){
            System.out.println("Divide by Zero at line " + pc); System.exit(0);
        }
        String lowPart = Integer.toBinaryString(twoByteRegisters[ax]);
        String dividend = Integer.toBinaryString(twoByteRegisters[dx]).concat(("0000000000000000" + lowPart).substring(lowPart.length()));
        int dividendValue = Integer.parseInt(dividend,2);
        r = dividendValue % twoByteRegisters[reg];
        q = dividendValue / twoByteRegisters[reg];
        //If quotient is greater than two byte, gives overflow error.
        if(q > 65535) {
            System.out.println("Overflow at line " + pc); System.exit(0);
        }
        twoByteRegisters[ax] = q;
        twoByteRegisters[dx] = r;
        fillChildReg();
    }
    //Divide value in dx and ax by value in two byte sized memory location. Put it quotient in ax and remainder in dx.
    public static void divideByWordMemory(String operand){
        int address, q, r;
        address = determineAddress(operand);
        //If address location belongs to instructions, gives error.
        if(address < ip){
            System.out.println("Not permitted to access that address at line " + pc); System.exit(0);
        }
        String lowPart = Integer.toBinaryString(memory[address]);
        String stringDivisor = Integer.toBinaryString(memory[address+1]).concat(("00000000" + lowPart).substring(lowPart.length()));
        int divisor = Integer.parseInt(stringDivisor,2);
        //If divisor is zero gives error.
        if(divisor == 0){
            System.out.println("Divide by Zero at line " + pc); System.exit(0);
        }
        lowPart = Integer.toBinaryString(twoByteRegisters[ax]);
        String dividend = Integer.toBinaryString(twoByteRegisters[dx]).concat(("0000000000000000" + lowPart).substring(lowPart.length()));
        int dividendValue = Integer.parseInt(dividend,2);
        r = dividendValue % divisor;
        q = dividendValue / divisor;
        //If quotient is greater than two byte, gives overflow error.
        if(q > 65535) {
            System.out.println("Overflow at line " + pc); System.exit(0);
        }
        twoByteRegisters[ax] = q;
        twoByteRegisters[dx] = r;
        fillChildReg();
    }
    //Divide ax by value in one byte register. Put it quotient in al and remainder in ah.
    public static void divideByByteReg(int reg){
        int r, q, divisor;
        divisor = oneByteRegisters[reg];
        //If divisor is zero gives error.
        if (divisor == 0) {
            System.out.println("Divide by Zero at line " + pc); System.exit(0);
        }
        q = twoByteRegisters[ax] / divisor;
        r = twoByteRegisters[ax] % divisor;
        //If quotient is greater than one byte, gives overflow error.
        if(of = q > 255) {
            System.out.println("Overflow at line " + pc); System.exit(0);
        }
        oneByteRegisters[al] = (short) q;
        oneByteRegisters[ah] = (short) r;
        fillParentReg();
    }
    //Divide ax by value in one byte sized memory address. Put it quotient in al and remainder in ah.
    public static void divideByByteMemory(String operand){
        int q, r, dividend;
        int address = determineAddress(operand);
        //If address location belongs to instructions, gives error.
        if (address < ip) {
            System.out.println("Not permitted to access that address at line " + pc); System.exit(0);
        }
        int divisor = memory[address];
        //If divisor is zero gives error.
        if (divisor == 0){
            System.out.println("Divide by Zero at line " + pc); System.exit(0);
        }
        dividend = twoByteRegisters[ax];
        r = dividend % divisor;
        q = dividend / divisor;
        //If quotient is greater than one byte, gives overflow error.
        if (of = q > 255) {
            System.out.println("Overflow at line " + pc); System.exit(0);
        }
        oneByteRegisters[al] = (short) q;
        oneByteRegisters[ah] = (short) r;
        fillParentReg();
    }

    public static void divideByImmediate(String operand){
        int divisor = sp;
        //If operand's size is greater than two byte, gives error.
        if (divisor > 65535) {
            System.out.println("Bad single operand at line " + pc); System.exit(0);
        }
        //If divisor is zero gives error.
        if (divisor == 0){
            System.out.println("Divide by Zero at line " + pc); System.exit(0);
        } else if (divisor >= -128 && divisor <= 127) {
            divideByByteNumber(divisor);
        } else {
            divideByWordNumber(divisor);
        }
    }

    public static void divideByByteNumber(int divisor){
        int dividend = twoByteRegisters[ax];
        int q = dividend / divisor;
        int r = dividend % divisor;
        //If quotient is greater than one byte, gives overflow error.
        if (of = q > 255) {
            System.out.println("Overflow at line " + pc); System.exit(0);
        }
        oneByteRegisters[al] = (short) q;
        oneByteRegisters[ah] = (short) r;
        fillParentReg();
    }
    public static void divideByWordNumber(int divisor){
        String lowPart = Integer.toBinaryString(twoByteRegisters[ax]);
        String stringDividend = Integer.toBinaryString(twoByteRegisters[dx]).concat(("0000000000000000" + lowPart).substring(lowPart.length()));
        int dividend = Integer.parseInt(stringDividend,2);
        int q = dividend / divisor;
        int r = dividend % divisor;
        //If quotient is greater than two byte, gives overflow error.
        if (of = q > 65535) {
            System.out.println("Overflow at line " + pc); System.exit(0);
        }
        twoByteRegisters[ax] = q;
        twoByteRegisters[dx] = r;
        fillChildReg();
    }

    //Fill child registers of ax and dx
    public static void fillChildReg(){
        updateChild(ax);
        updateChild(dx);
    }
    //Update ax according to ah and al.
    public static void fillParentReg(){
        twoByteRegisters[ax] =  (oneByteRegisters[ah]<<8) | oneByteRegisters[al];
    }
}
