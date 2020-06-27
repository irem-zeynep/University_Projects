package Cmpe230;
// If type is true ,then right rotate (RCR) is done otherwise left rotate (RCL) takes place.
public class Rotate extends Helper{
    // Rotates the given two byte sized register for secondOperand times, updates the required flags.
    public static void bigReg(int reg, String secondOperand, boolean hasSubReg, boolean type) {
        int shiftValue = convertInteger(secondOperand);
        // If second operand is an immediate number rotates the given register.
        if(isNumber) {
            if (shiftValue == 0) return;
            if (shiftValue > 0 && shiftValue < 32) {
                String last;
                if (type) {
                    last = rotateRight(to17Bit(twoByteRegisters[reg]), shiftValue);
                    cf = last.charAt(0) == '1';
                    if(shiftValue == 1)
                        of = (Integer.parseInt(last.substring(1,2)) ^ Integer.parseInt(last.substring(2,3))) == 1;
                } else {
                    last = rotateLeft(to17Bit(twoByteRegisters[reg]), shiftValue);
                    cf = last.charAt(0) == '1';
                    if(shiftValue == 1) {
                        boolean a =  last.charAt(1) == 1 ;
                        of = a ^ cf;
                    }
                }
                twoByteRegisters[reg] = Integer.parseInt(last.substring(1), 2);
                // Number should be in range [0,31].
            } else {
                System.out.println("Invalid number " + pc); System.exit(0);
            }
            // If secondOperand is the register "cl" performs the rotation.
        } else if (secondOperand.equalsIgnoreCase("cl")) {
            String last;
            if (type) {
                last = rotateRight(to17Bit(twoByteRegisters[reg]), oneByteRegisters[cl]);
                cf = last.charAt(0) == '1';
                if(oneByteRegisters[cl] == 1)
                    of = (Integer.parseInt(last.substring(1,2)) ^ Integer.parseInt(last.substring(2,3))) == 1;
            } else {
                last = rotateLeft(to17Bit(twoByteRegisters[reg]), oneByteRegisters[cl]);
                cf = last.charAt(0) == '1';
                if(oneByteRegisters[cl] == 1) {
                    boolean a =  last.charAt(1) == 1;
                    of = a ^ cf;
                }
            }
            twoByteRegisters[reg] = Integer.parseInt(last.substring(1), 2);
            // If secondOperand is not number neither "cl" register, then there is an invalid operand so we terminate.
        } else {
            System.out.println("Incompatible operand " + pc); System.exit(0);
        }
        // Since one and two byte sized registers are stored in different arrays if firstOperand has children they are updated.
        if(hasSubReg){
            updateChild(reg);
        }
    }
    // Rotates the given one byte sized register for secondOperand times, updates the required flags.
    public static void smallReg(int reg, String secondOperand, boolean type) {
        int shiftValue = convertInteger(secondOperand);
        // If second operand is an immediate number rotates the given register.
        if(isNumber) {
            if(shiftValue == 0) return;
            if (shiftValue > 0 && shiftValue < 32) {
                String last;
                if (type) {
                    last = rotateRight(to9Bit(oneByteRegisters[reg]), shiftValue);
                    cf = last.charAt(0) == '1';
                    if(shiftValue == 1)
                        of = (Integer.parseInt(last.substring(1,2)) ^ Integer.parseInt(last.substring(2,3))) == 1;
                } else {
                    last = rotateLeft(to9Bit(oneByteRegisters[reg]), shiftValue);
                    cf = last.charAt(0) == '1';
                    if(shiftValue == 1) {
                        boolean a =  last.charAt(1) == 1 ;
                        of = a ^ cf;
                    }
                }
                oneByteRegisters[reg] = (short) Integer.parseInt(last.substring(1), 2);
                // Number should be in range [0,31].
            } else {
                System.out.println("Invalid number " + pc); System.exit(0);
            }
            // If secondOperand is the register "cl" performs the rotation.
        } else if (secondOperand.equalsIgnoreCase("cl")) {
            String last;
            if (type) {
                last = rotateRight(to9Bit(oneByteRegisters[reg]), oneByteRegisters[cl]);
                cf = last.charAt(0) == '1';
                if(oneByteRegisters[cl] == 1)
                    of = (Integer.parseInt(last.substring(1,2)) ^ Integer.parseInt(last.substring(2,3))) == 1;
            } else {
                last = rotateLeft(to9Bit(oneByteRegisters[reg]), oneByteRegisters[cl]);
                cf = last.charAt(0) == '1';
                if(oneByteRegisters[cl] == 1) {
                    boolean a =  last.charAt(1) == 1;
                    of = a ^ cf;
                }
            }
            oneByteRegisters[reg] = (short) Integer.parseInt(last.substring(1), 2);
            // If secondOperand is not number neither "cl" register, then there is an invalid operand so we terminate.
        } else {
            System.out.println("Incompatible operand " + pc); System.exit(0);
        }
        // Updates the required parent register.
        updateParent(reg);
    }
    // Rotates the given memory location secondOperand times.
    public static void memory(String firstOperand, String secondOperand, boolean type) {
        int shiftValue = convertInteger(secondOperand);
        // Checks if secondOperand is number.
        if(isNumber) {
            if(!(shiftValue >=0 && shiftValue < 32)) {
                System.out.println("Invalid number " + pc); System.exit(0);
            }
        } else if (secondOperand.equalsIgnoreCase("cl")) {
            shiftValue = oneByteRegisters[cl];
        } else {
            System.out.println("Incompatible operand " + pc); System.exit(0);
        }
        // If it is zero nothing should be done.
        if(shiftValue == 0) return;
        String subFirstOperand = firstOperand.substring(2, firstOperand.length() - 1); // value between square brackets.
        int address = determineAddress(subFirstOperand);
        if(!isAddress) {
            System.out.println("Incompatible operand " + pc ); System.exit(0);
        }
        if(address < ip){
            System.out.println("Not permitted to access that address at line " + pc); System.exit(0);
        }
        // Executed if given address is word sized.
        if (firstOperand.charAt(0) == 'w' || firstOperand.charAt(0) == 'W') {
            String lowPart = Integer.toBinaryString(memory[address]);
            String stringOperand = Integer.toBinaryString(memory[address+1]).concat(("00000000" + lowPart).substring(lowPart.length()));
            int operand = Integer.parseInt(stringOperand,2);
            stringOperand = to17Bit(operand);
            if (type) {
                stringOperand = rotateRight(stringOperand, shiftValue);
                cf = stringOperand.charAt(0) == '1';
                if(shiftValue == 1)
                    of = (Integer.parseInt(stringOperand.substring(1,2)) ^ Integer.parseInt(stringOperand.substring(2,3))) == 1;
            } else {
                stringOperand = rotateLeft(stringOperand, shiftValue);
                cf = stringOperand.charAt(0) == '1';
                if(shiftValue == 1) {
                    boolean a =  stringOperand.charAt(1) == 1;
                    of = a ^ cf;
                }
            }
            memory[address+1] = (short) Integer.parseInt(stringOperand.substring(1,9),2);
            memory[address] = (short) Integer.parseInt(stringOperand.substring(9),2);
           // Executed if given address is byte sized.
        } else if (firstOperand.charAt(0) == 'b' || firstOperand.charAt(0) == 'B') {
            String last;
            if(type) {
                last = rotateRight(to9Bit(memory[address]), shiftValue);
                memory[address] = (short) Integer.parseInt(last.substring(1),2);
                cf = last.charAt(0) == '1';
                if(shiftValue == 1)
                    of = (Integer.parseInt(last.substring(1,2)) ^ Integer.parseInt(last.substring(2,3))) == 1;
            } else {
                last = rotateLeft(to9Bit(memory[address]), shiftValue);
                memory[address] = (short) Integer.parseInt(last.substring(1),2);
                cf = last.charAt(0) == '1';
                if(shiftValue == 1) {
                    boolean a =  last.charAt(1) == 1;
                    of = a ^ cf;
                }
            }
        }
    }
    // Concatenates the carry flag value to the beginning of the value and returns the 17-Bit string representation.
    public static String to17Bit(int reg) {
        String x = Integer.toBinaryString(reg);
        while(x.length() != 16)
            x = "0" + x;
        if(cf)
            x = "1" + x;
        else
            x = "0" + x;
        return x;
    }
    // Concatenates the carry flag value to the beginning of the value and returns the 9-Bit string representation.
    public static String to9Bit(int reg) {
        String x = Integer.toBinaryString(reg);
        while(x.length() != 8)
            x = "0" + x;
        if(cf)
            x = "1" + x;
        else
            x = "0" + x;
        return x;
    }
    // Performs the left rotation in the given string.
    static String rotateLeft(String str, int d){
        d %= str.length();
        String ans = str.substring(d) + str.substring(0, d);
        return ans;
    }
    // Performs the right rotation in the given string.
    static String rotateRight(String str, int d){
        d %= str.length();
        return rotateLeft(str, str.length() - d);
    }

}
