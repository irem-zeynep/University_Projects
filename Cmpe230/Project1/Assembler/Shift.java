package Cmpe230;
// If type is true ,then right shift (SHR) is done otherwise left shift (SHL) takes place.
public class Shift extends Helper{
    // Shifts the given two byte sized register for secondOperand times, updates the required flags.
    public static void bigReg(int reg, String secondOperand, boolean hasSubReg, boolean type) {
        int shiftValue = convertInteger(secondOperand);
        // If second operand is an immediate number shifts the given register.
        if(isNumber) {
            if(shiftValue == 0) return;
            if (shiftValue > 0 && shiftValue < 32) {
                if (type) {
                    // Since right-most bit is lost during each shift, firstly one minus of the secondOperand
                    // is done, carry flag is updated and actual shift is done afterwards.
                    String tmp = Integer.toBinaryString(twoByteRegisters[reg] >>> (shiftValue-1));
                    cf = tmp.charAt(tmp.length()-1) == '1';
                    twoByteRegisters[reg] = twoByteRegisters[reg] >>> shiftValue;
                    tmp = Integer.toBinaryString(twoByteRegisters[reg]);
                    sf = false;
                    if(tmp.length() == 16)
                        sf = tmp.charAt(0) == '1' ;
                    if(shiftValue == 1) {
                        if(tmp.length() == 16)
                            of = tmp.charAt(0) == '1' ;
                        else
                            of = false;
                    }
                // Left shift
                } else {
                    String tmp = Integer.toBinaryString(twoByteRegisters[reg] << shiftValue);
                    if (tmp.length() > 16) {
                        twoByteRegisters[reg] = Integer.parseInt(tmp.substring(tmp.length()-16),2);
                        cf = tmp.charAt(tmp.length()-17) == '1';
                        sf = tmp.charAt(tmp.length()-16) == '1';
                    } else {
                        cf = false;
                        twoByteRegisters[reg] = Integer.parseInt(tmp,2);
                        sf = false;
                        if(tmp.length() == 16)
                            sf = tmp.charAt(0) == '1' ;
                    }
                    if(shiftValue == 1)
                        of = cf != sf;
                }
                zf = twoByteRegisters[reg] == 0;
            } else { // Number should be in range [0,31].
                System.out.println("Invalid number " + pc);  System.exit(0);
            }
            // If secondOperand is the register "cl" performs the shift.
        } else if (secondOperand.equalsIgnoreCase("cl")) {
            if(oneByteRegisters[cl] == 0)  return;
            if (type) {
                // Same as above firstly one minus of the secondOperand is done cf is updated and actual shift is done.
                String tmp = Integer.toBinaryString(twoByteRegisters[reg] >>> (oneByteRegisters[cl]-1));
                cf = tmp.charAt(tmp.length()-1) == '1';
                twoByteRegisters[reg] = twoByteRegisters[reg] >>> oneByteRegisters[cl];
                tmp = Integer.toBinaryString(twoByteRegisters[reg]);
                sf = false;
                if(tmp.length() == 16)
                    sf = tmp.charAt(0) == '1' ;
                if(oneByteRegisters[cl] == 1) {
                    if(tmp.length() == 16)
                        of = tmp.charAt(0) == '1' ;
                    else
                        of = false;
                }
                // Left shift.
            } else {
                String tmp = Integer.toBinaryString(twoByteRegisters[reg] << oneByteRegisters[cl]);
                if (tmp.length() > 16) {
                    twoByteRegisters[reg] = Integer.parseInt(tmp.substring(tmp.length()-16),2);
                    cf = tmp.charAt(tmp.length()-17) == '1';
                    sf = tmp.charAt(tmp.length()-16) == '1';
                } else {
                    cf = false;
                    twoByteRegisters[reg] = Integer.parseInt(tmp,2);
                    sf = false;
                    if(tmp.length() == 16)
                        sf = tmp.charAt(0) == '1' ;
                }
                if(oneByteRegisters[cl] == 1)
                    of = cf != sf;
            }
            zf = twoByteRegisters[reg] == 0;
            // If secondOperand is not number neither "cl" register, then there is an invalid operand so we terminate.
        } else {
            System.out.println("Incompatible operand " + pc);System.exit(0);
        }
        // Since one and two byte sized registers are stored in different arrays if firstOperand has children they are updated.
        if(hasSubReg){
            updateChild(reg);
        }
    }
    // Shifts the given one byte sized register for secondOperand times, updates the required flags.
    public static void smallReg(int reg, String secondOperand, boolean type) {
        int shiftValue = convertInteger(secondOperand);
        // If second operand is an immediate number shifts the given register.
        if(isNumber) {
            if (shiftValue == 0) return;
            if (shiftValue > 0 && shiftValue < 32) {
                if (type) {
                    // Same as in bigReg method we first need to perform one minus of secondOperand to update cf.
                    String tmp = Integer.toBinaryString(oneByteRegisters[reg] >>> (shiftValue-1));
                    cf = tmp.charAt(tmp.length()-1) == '1';
                    oneByteRegisters[reg] = (short) (oneByteRegisters[reg] >>> shiftValue);
                    tmp = Integer.toBinaryString(oneByteRegisters[reg]);
                    sf = false;
                    if(tmp.length() == 8)
                        sf = tmp.charAt(0) == '1' ;
                    if(shiftValue == 1) {
                        if(tmp.length() == 8)
                            of = tmp.charAt(0) == '1' ;
                        else
                            of = false;
                    }
                    // Left shift.
                } else {
                    String tmp = Integer.toBinaryString(oneByteRegisters[reg] << shiftValue);
                    if (tmp.length() > 8) {
                        oneByteRegisters[reg] = (short)Integer.parseInt(tmp.substring(tmp.length()-8),2);
                        cf = tmp.charAt(tmp.length()-9) == '1';
                        sf = tmp.charAt(tmp.length()-8) == '1';
                    } else {
                        cf = false;
                        oneByteRegisters[reg] = (short) Integer.parseInt(tmp,2);
                        sf = false;
                        if(tmp.length() == 8)
                            sf = tmp.charAt(0) == '1' ;
                    }
                    if(shiftValue == 1)
                        of = cf != sf;
                }
                zf = oneByteRegisters[reg] == 0;
            } else { // Number should be in range [0,31].
                System.out.println("Invalid number " + pc); System.exit(0);
            }
            // If secondOperand is "cl" register performs the shift.
        } else if (secondOperand.equalsIgnoreCase("cl")) {
            if(oneByteRegisters[cl] == 0) return;
            if (type) {
                String tmp = Integer.toBinaryString(oneByteRegisters[reg] >>> (oneByteRegisters[cl]-1));
                cf = tmp.charAt(tmp.length()-1) == '1';
                oneByteRegisters[reg] = (short) (oneByteRegisters[reg] >>> oneByteRegisters[cl]);
                tmp = Integer.toBinaryString(oneByteRegisters[reg]);
                sf = false;
                if(tmp.length() == 8)
                    sf = tmp.charAt(0) == '1' ;
                if(oneByteRegisters[cl] == 1) {
                    if(tmp.length() == 8)
                        of = tmp.charAt(0) == '1' ;
                    else
                        of = false;
                }
                // Left shift.
            } else {
                String tmp = Integer.toBinaryString(oneByteRegisters[reg] << oneByteRegisters[cl]);
                if (tmp.length() > 8) {
                    oneByteRegisters[reg] = (short) (Integer.parseInt(tmp.substring(tmp.length()-8),2));
                    cf = tmp.charAt(tmp.length()-9) == '1';
                    sf = tmp.charAt(tmp.length()-8) == '1';
                } else {
                    cf = false;
                    oneByteRegisters[reg] = (short) (Integer.parseInt(tmp,2));
                    sf = false;
                    if(tmp.length() == 8)
                        sf = tmp.charAt(0) == '1' ;
                }
                if(oneByteRegisters[cl] == 1)
                    of = cf != sf;
            }
            zf = oneByteRegisters[reg] == 0;
            // If secondOperand is not number neither "cl" register, then there is an invalid operand so we terminate.
        } else {
            System.out.println("Incompatible operand " + pc); System.exit(0);
        }
        // Updates the required parent register.
        updateParent(reg);
    }
    // Shifts the given memory location secondOperand times.
    public static void memory (String firstOperand, String secondOperand, boolean type) {
        int shiftValue = convertInteger(secondOperand);
        if(isNumber) {
            if(!(shiftValue >= 0 && shiftValue < 32)) {
                System.out.println("Invalid number " + pc); System.exit(0);
            }
        } else if (secondOperand.equalsIgnoreCase("cl")) {
            shiftValue = oneByteRegisters[cl];
        } else {
            System.out.println("Incompatible operand " + pc); System.exit(0);
        }
        String subFirstOperand = firstOperand.substring(2, firstOperand.length() - 1);

        int address = determineAddress(subFirstOperand);
        if(!isAddress) {
            System.out.println("Incompatible operand " + pc ); System.exit(0);
        }
        if(address < ip){
            System.out.println("Not permitted to access that address at line " + pc); System.exit(0);
        }
        if(shiftValue == 0)  return;
        if (firstOperand.charAt(0) == 'w' || firstOperand.charAt(0) == 'W') {
            String append = Integer.toBinaryString(memory[address]);
            String stringOperand = Integer.toBinaryString(memory[address+1]).concat(("00000000" + append).substring(append.length()));
            int operand = Integer.parseInt(stringOperand,2);
            if(type) {
                String tmp = Integer.toBinaryString(operand >>> shiftValue-1);
                cf = tmp.charAt(tmp.length()-1) == '1';
                tmp = Integer.toBinaryString(operand >>> shiftValue);
                sf = false;
                if(tmp.length() == 16)
                    sf = tmp.charAt(0) == '1' ;
                if(shiftValue == 1) {
                    if(tmp.length() == 16)
                        of = tmp.charAt(0) == '1' ;
                    else
                        of = false;
                }
                if(tmp.length() > 8) {
                    memory[address+1] = (short) Integer.parseInt(tmp.substring(0,tmp.length()-8),2);
                    memory[address] = (short) Integer.parseInt(tmp.substring(tmp.length()-8),2);
                } else {
                    memory[address] = (short) Integer.parseInt(tmp,2);
                }
            } else {
                String tmp = Integer.toBinaryString(operand << shiftValue);
                int lowerPart = Integer.parseInt(tmp.substring(tmp.length()-8),2);
                if (tmp.length() > 16) {
                    memory[address+1] = (short) Integer.parseInt(tmp.substring(tmp.length()-16,tmp.length()-8),2);
                    memory[address] = (short)lowerPart;
                    cf = tmp.charAt(tmp.length()-17) == '1';
                    sf = tmp.charAt(tmp.length()-16) == '1';
                } else {
                    cf = false;
                    if(tmp.length() > 8) {
                        memory[address+1] = (short) Integer.parseInt(tmp.substring(0,tmp.length()-8),2);
                        memory[address] = (short) lowerPart;
                    } else {
                        memory[address] = (short) Integer.parseInt(tmp,2);
                    }
                    sf = false;
                    if(tmp.length() == 16)
                        sf = tmp.charAt(0) == '1' ;
                }
                if(shiftValue == 1)
                    of = cf != sf;
            }
            zf = memory[address] == 0;
        } else if (firstOperand.charAt(0) == 'b' || firstOperand.charAt(0) == 'B') {
            if(type) {
                String tmp = Integer.toBinaryString(memory[address] >>> shiftValue-1);
                cf = tmp.charAt(tmp.length()-1) == '1';
                memory[address] = (short) (memory[address] >>> shiftValue);
                tmp = Integer.toBinaryString(memory[address]);
                sf = false;
                if(tmp.length() == 8)
                    sf = tmp.charAt(0) == '1' ;
                if(shiftValue == 1) {
                    if(tmp.length() == 8)
                        of = tmp.charAt(0) == '1' ;
                    else
                        of = false;
                }
            } else {
                memory[address] = (short) (memory[address] << shiftValue);
                String tmp = Integer.toBinaryString(memory[address]);
                if (tmp.length() > 8) {
                    memory[address] = (short)Integer.parseInt(tmp.substring(tmp.length()-8),2);
                    cf = tmp.charAt(tmp.length()-9) == '1';
                    sf = tmp.charAt(tmp.length()-8) == '1';
                } else {
                    cf = false;
                    memory[address] = (short) Integer.parseInt(tmp,2);
                    sf = false;
                    if(tmp.length() == 8)
                        sf = tmp.charAt(0) == '1' ;
                }
                if(shiftValue == 1)
                    of = cf != sf;
            }
            zf = memory[address] == 0;
        }
    }
}