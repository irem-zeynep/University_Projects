package Cmpe230;

public class Compare extends Helper{
    //Compare operation when first operand is two byte-sized register.
    public static void compareWordRegister(int destination, String secondOperand){
        int address, operand, source, signedValue;
        //If second operand is byte sized register, gives error.
        if (determineByteRegister(secondOperand) != -1) {
            System.out.println("Byte-word combinations are not allowed"); System.exit(0);
        }
        //Register to Register
        source = determineWordRegister(secondOperand);
        if(source != -1) {
            signedValue = (twoByteRegisters[destination] - twoByteRegisters[source]);
            if(cf = (signedValue < 0))
                signedValue += 65536;
            String firstOperandHex = Integer.toHexString(twoByteRegisters[destination]);
            String secondOperandHex = Integer.toHexString(twoByteRegisters[source]);
            af = (Integer.parseInt(firstOperandHex.substring(firstOperandHex.length()-1),16) - Integer.parseInt(secondOperandHex.substring(secondOperandHex.length()-1),16)) < 0;
            zf = signedValue == 0;
            sf = 32767 < signedValue;
            of = (twoByteRegisters[destination] < 32768 && twoByteRegisters[source] >= 32768 && sf) || (twoByteRegisters[destination] >= 32768 && twoByteRegisters[source] < 32768 && !sf) ;
        }
        //Memory to Register
        else if (secondOperand.length() > 1 && (secondOperand.charAt(1) == '[' && secondOperand.charAt(0) == '[') && secondOperand.endsWith("]")) {
            //If second operand points byte sized address, gives throws error.
            if(secondOperand.charAt(0) == 'b' || secondOperand.charAt(0) == 'B') {
                System.out.println("Incompatible data type at line " + pc); System.exit(0);
            }
            String stringAddress;
            stringAddress = secondOperand.substring(secondOperand.indexOf("[") + 1, secondOperand.length() - 1);
            //If address is in valid address format, gets its value by calling method from helper class.
            address = determineAddress(stringAddress);
            if(isAddress){
                //If address location belongs to instructions, gives error.
                if(address < ip){
                    System.out.println("Not permitted to access that address " + pc); System.exit(0);
                }
                String lowPart = Integer.toBinaryString(memory[address]);
                String stringOperand = Integer.toBinaryString(memory[address+1]).concat(("00000000" + lowPart).substring(lowPart.length()));
                operand = Integer.parseInt(stringOperand,2);
                signedValue = twoByteRegisters[destination] - operand;
                if(cf = signedValue < 0)
                    signedValue += 65536;
                String firstOperandHex = Integer.toHexString(twoByteRegisters[destination]);
                String secondOperandHex = Integer.toHexString(operand);
                af = (Integer.parseInt(firstOperandHex.substring(firstOperandHex.length()-1),16) - Integer.parseInt(secondOperandHex.substring(secondOperandHex.length()-1),16)) < 0;
                zf = signedValue == 0;
                sf = 32767 < signedValue;
                of = (twoByteRegisters[destination] < 32768 && operand >= 32768 && sf) || (twoByteRegisters[destination] >= 32768 && operand < 32768 && !sf) ;
            } else {
                    System.out.println("Syntax error at line " + pc); System.exit(0);
            }
        } else {
            //If second operand is integer format, converts it.
            operand = convertInteger(secondOperand);
            if(isNumber){
                if (operand < 0 || operand > 65535){
                    System.out.println("Word-sized required at line " + pc); System.exit(0);
                }
                signedValue = (twoByteRegisters[destination] - operand);
                if(cf = signedValue < 0)
                    signedValue += 65536;
                String firstOperandHex = Integer.toHexString(twoByteRegisters[destination]);
                String secondOperandHex = Integer.toHexString(operand);
                af = (Integer.parseInt(firstOperandHex.substring(firstOperandHex.length()-1),16) - Integer.parseInt(secondOperandHex.substring(secondOperandHex.length()-1),16)) < 0;
                sf = signedValue > 32767;
                of = (twoByteRegisters[destination] < 32768 && operand >= 32768 && sf) || (twoByteRegisters[destination] >= 32768 && operand < 32768 && !sf) ;
                zf = signedValue == 0;
                //If second operand is character gets its ascii value.
            } else if(secondOperand.charAt(0) == '"' && secondOperand.endsWith("\"")){
                if (secondOperand.length() > 3) {
                    System.out.println("Byte-sized required at line " + pc); System.exit(0);
                } else if (secondOperand.length() == 3) {
                    byte charValue = (byte) secondOperand.charAt(1);
                    signedValue = (twoByteRegisters[destination] - charValue);
                    if(cf = signedValue <0)
                        signedValue += 65536;
                    String firstOperandHex = Integer.toHexString(twoByteRegisters[destination]);
                    String secondOperandHex = Integer.toHexString(charValue);
                    af = (Integer.parseInt(firstOperandHex.substring(firstOperandHex.length()-1),16) - Integer.parseInt(secondOperandHex.substring(secondOperandHex.length()-1),16)) < 0;
                    sf = signedValue > 32767;
                    of = (twoByteRegisters[destination] >= 32768 && !sf) ;
                    zf = signedValue == 0;
                }
            } else {
                System.out.println("Syntax error at line " + pc); System.exit(0);
            }
        }
    }
    //Compare operation when first operand is one byte-sized register.
    public static void compareByteRegister(int destination, String  secondOperand){
        int address, operand, source,signedValue;
        //If second operand is word sized register, gives error.
        if (determineWordRegister(secondOperand) != -1) {
            System.out.println("Byte-word combinations are not allowed"); System.exit(0);
        }
        //Register to Register
        source = determineByteRegister(secondOperand);
        if(source != -1) {
            signedValue = (oneByteRegisters[destination] - oneByteRegisters[source]);
            if(cf = (signedValue < 0))
                signedValue += 256;
            String firstOperandHex = Integer.toHexString(oneByteRegisters[destination]);
            String secondOperandHex = Integer.toHexString(oneByteRegisters[source]);
            af = (Integer.parseInt(firstOperandHex.substring(firstOperandHex.length()-1),16) - Integer.parseInt(secondOperandHex.substring(secondOperandHex.length()-1),16)) < 0;
            zf = signedValue == 0;
            sf = 127 < signedValue;
            of = (oneByteRegisters[destination] < 128 && oneByteRegisters[source] >= 128 && sf) || (oneByteRegisters[destination] >= 128 && oneByteRegisters[source] < 128 && !sf) ;
        }
        //Memory to Register
        else if (secondOperand.length() > 1 && (secondOperand.charAt(1) == '[' && secondOperand.charAt(0) == '[') && secondOperand.endsWith("]")) {
            if(secondOperand.charAt(0) == 'w' || secondOperand.charAt(0) == 'W') {
                System.out.println("Incompatible data type at line " + pc); System.exit(0);
            }
            String stringAddress;
            stringAddress = secondOperand.substring(secondOperand.indexOf("[") + 1, secondOperand.length() - 1);
            //If address is in valid address format, gets its value by calling method from helper class.
            address = determineAddress(stringAddress);
            if(isAddress){
                //If address location belongs to instructions, gives error.
                if(address < ip){
                    System.out.println("Not permitted to access that address " + pc); System.exit(0);
                }
                operand = memory[address];
                signedValue = (oneByteRegisters[destination] - operand);
                if(cf = (signedValue < 0))
                    signedValue += 256;
                String firstOperandHex = Integer.toHexString(oneByteRegisters[destination]);
                String secondOperandHex = Integer.toHexString(operand);
                af = (Integer.parseInt(firstOperandHex.substring(firstOperandHex.length()-1),16) - Integer.parseInt(secondOperandHex.substring(secondOperandHex.length()-1),16)) < 0;
                zf = signedValue == 0;
                sf = 127 < signedValue;
                of = (oneByteRegisters[destination] < 128 && operand >= 128 && sf) || (oneByteRegisters[destination] >= 128 && operand < 128 && !sf) ;
                //If second operand starts with "[" and ends with "]" but it is not in the form of integer of valid register form, gives syntax error.
            } else {
                System.out.println("Syntax error at line " + pc); System.exit(0);
            }
        } else {
            //If second operand is integer format, converts it.
            operand = convertInteger(secondOperand);
            if (isNumber) {//Immediate is a number
                if (operand < 0 || operand > 255){
                    System.out.println("Byte-sized required " + pc); System.exit(0);
                }
                signedValue = (oneByteRegisters[destination] - operand);
                if(cf = (signedValue < 0))
                    signedValue += 256;
                String firstOperandHex = Integer.toHexString(oneByteRegisters[destination]);
                String secondOperandHex = Integer.toHexString(operand);
                af = (Integer.parseInt(firstOperandHex.substring(firstOperandHex.length()-1),16) - Integer.parseInt(secondOperandHex.substring(secondOperandHex.length()-1),16)) < 0;
                zf = signedValue == 0;
                sf = 127 < signedValue;
                of = (oneByteRegisters[destination] < 128 && operand >= 128 && sf) || (oneByteRegisters[destination] >= 128 && operand < 128 && !sf) ;
                //If second operand is character gets its ascii value.
            } else if ((secondOperand.charAt(0) == '\"' && secondOperand.endsWith("\"")) || (secondOperand.charAt(0) == '\'' && secondOperand.endsWith("'"))) {
                if (secondOperand.length() > 3) {
                    System.out.println("Byte-sized required at line " + pc); System.exit(0);
                } else if (secondOperand.length() == 3) {
                    byte charValue = (byte) secondOperand.charAt(1);
                    signedValue = (oneByteRegisters[destination] - charValue);
                    if(cf = (signedValue < 0))
                        signedValue += 256;
                    String firstOperandHex = Integer.toHexString(oneByteRegisters[destination]);
                    String secondOperandHex = Integer.toHexString(charValue);
                    af = (Integer.parseInt(firstOperandHex.substring(firstOperandHex.length()-1),16) - Integer.parseInt(secondOperandHex.substring(secondOperandHex.length()-1),16)) < 0;
                    zf = signedValue == 0;
                    sf = 127 < signedValue;
                    of = (oneByteRegisters[destination] >= 128 && !sf) ;
                }
            } else {
                System.out.println("Syntax error at line  " + pc); System.exit(0);
            }
        }

    }
    //Compare operation when first operand is two byte-sized memory location.
    public static void compareWordMemory(String stringFirstOperand, String stringSecondOperand, boolean hasPrefix){
        int address, source, secondOperand, signedValue, firstOperand;
        if (stringFirstOperand.equalsIgnoreCase("sp")) {
            //Gets first operands value.
            firstOperand = sp;
        } else {
            address = determineAddress(stringFirstOperand);
            //If first operand is not in valid address format, gives error.
            if (!isAddress) {
                System.out.println("Syntax error at line " + pc); System.exit(0);
            }
            //If address location belongs to instructions, gives error.
            if (address < ip) {
                System.out.println("Not permitted to access that address " + pc);  System.exit(0);
            }
            //Calculates first operands value.
            String lowPart = Integer.toBinaryString(memory[address]);
            String stringOperand = Integer.toBinaryString(memory[address + 1]).concat(("00000000" + lowPart).substring(lowPart.length()));
            firstOperand = Integer.parseInt(stringOperand, 2);
        }
        source = determineByteRegister(stringSecondOperand);
        //If second operand is one byte sized register, checks whether first operand has w in front of it.
        if(source != -1){
            //If it has w, gives error.
            if(hasPrefix){
                System.out.println("Byte-word combinations are not allowed at line " + pc); System.exit(0);
            } else {
                //Else, first operand's type is determined by second operand. Calls the compare method for one byte sized memory locations.
                compareByteMemory(stringFirstOperand, stringSecondOperand);
                return;
            }
        }
        //Register-register
        source = determineWordRegister(stringSecondOperand);
        if(source != -1) {
            signedValue = firstOperand - twoByteRegisters[source];
            if(cf = signedValue <0)
                signedValue += 65536;
            String firstOperandHex = Integer.toHexString(firstOperand);
            String secondOperandHex = Integer.toHexString(twoByteRegisters[source]);
            af = (Integer.parseInt(firstOperandHex.substring(firstOperandHex.length()-1),16) - Integer.parseInt(secondOperandHex.substring(secondOperandHex.length()-1),16)) < 0;
            zf = signedValue == 0;
            sf = signedValue > 32767;
            of = (firstOperand < 32768 && twoByteRegisters[source] >= 32768 && sf) || (firstOperand >= 32768 && twoByteRegisters[source] < 32768 && !sf) ;
        } else {
            //If second operand is in number format, convert it into integer.
            secondOperand = convertInteger(stringSecondOperand);
            if(isNumber) {
                //If second operand's size is one byte and memory location's size is not specified, gives error.
                if(secondOperand <= 255 && !hasPrefix){
                    System.out.println("Unknown data type at line " + pc); System.exit(0);
                }
                if (secondOperand > 65535 || secondOperand < 0){
                    System.out.println("Word-sized required at line " + pc); System.exit(0);
                } else {
                    signedValue = firstOperand - secondOperand;
                    if( cf = signedValue <0)
                        signedValue += 65536;
                    String firstOperandHex = Integer.toHexString(firstOperand);
                    String secondOperandHex = Integer.toHexString(secondOperand);
                    af = (Integer.parseInt(firstOperandHex.substring(firstOperandHex.length()-1),16) - Integer.parseInt(secondOperandHex.substring(secondOperandHex.length()-1),16)) < 0;
                    zf = signedValue == 0;
                    sf = signedValue > 32767;
                    of = (firstOperand < 32768 && secondOperand >= 32768 && sf) || (firstOperand >= 32768 && secondOperand < 32768 && !sf) ;
                }
                //If second operand is in character format
            } else if ((stringSecondOperand.charAt(0) == '"' && stringSecondOperand.endsWith("\""))|| (stringSecondOperand.charAt(0) == '\'' && stringSecondOperand.endsWith("'"))) {
                if (!hasPrefix) {
                    System.out.println("Byte or Word? at line " + pc); System.exit(0);
                }
                if (stringSecondOperand.length() > 3) {
                    System.out.println("Incompatible data type at line " + pc); System.exit(0);
                } else if(stringSecondOperand.length() == 3) {
                    byte charValue = (byte) stringSecondOperand.charAt(1);
                    signedValue = firstOperand - charValue;
                    if(cf = signedValue <0)
                        signedValue += 65536;
                    String firstOperandHex = Integer.toHexString(firstOperand);
                    String secondOperandHex = Integer.toHexString(charValue);
                    af = (Integer.parseInt(firstOperandHex.substring(firstOperandHex.length()-1),16) - Integer.parseInt(secondOperandHex.substring(secondOperandHex.length()-1),16)) < 0;
                    zf = signedValue == 0;
                    sf = signedValue > 32767;
                    of = (firstOperand >= 32768 && !sf) ;
                }
            } else {
                System.out.println("Syntax error at line  " + pc); System.exit(0);
            }
        }
    }
    //Compare operation when first operand is one byte-sized memory location.
    public static void compareByteMemory(String stringFirstOperand, String stringSecondOperand){
        int secondOperand, address, reg, signedValue;
        address = determineAddress(stringFirstOperand);
        //If first operand is not in valid address syntax, gives error.
        if(!isAddress) {
            System.out.println("Syntax error at line " + pc); System.exit(0);
        }
        //If address location belongs to instructions, gives error.
        if(address < ip){
            System.out.println("Not permitted to access that address " + pc); System.exit(0);
        }
        //If second operand is two byte register, gives error. Else if it is two byte register, realizes compare operation.
        if (determineWordRegister(stringSecondOperand) != -1) {
            System.out.println("Byte-word combinations are not allowed at line " + pc);System.exit(0);
        }
        reg = determineByteRegister(stringSecondOperand);
        if(reg != -1) {
            signedValue = memory[address] - oneByteRegisters[reg];
            if(cf = signedValue <0)
                signedValue += 256;
            String firstOperandHex = Integer.toHexString(memory[address]);
            String secondOperandHex = Integer.toHexString(oneByteRegisters[reg]);
            af = (Integer.parseInt(firstOperandHex.substring(firstOperandHex.length()-1),16) - Integer.parseInt(secondOperandHex.substring(secondOperandHex.length()-1),16)) < 0;
            sf = signedValue > 127;
            zf = signedValue == 0;
            of = (memory[address] < 128 && oneByteRegisters[reg] >= 128 && sf) || (memory[address] >= 128 && oneByteRegisters[reg] < 128 && !sf) ;

        } else {
            //If second operand is a number, converts it into integer. If it is greater than 1 byte gives an error.
            secondOperand = convertInteger(stringSecondOperand);
            if(isNumber){
                if (secondOperand > 255){
                    System.out.println("Byte-sized required at line " + pc); System.exit(0);
                } else {
                    signedValue =  memory[address] - secondOperand;
                    if(cf = signedValue < 0)
                        signedValue += 256;
                    String firstOperandHex = Integer.toHexString(memory[address]);
                    String secondOperandHex = Integer.toHexString(secondOperand);
                    af = (Integer.parseInt(firstOperandHex.substring(firstOperandHex.length()-1),16) - Integer.parseInt(secondOperandHex.substring(secondOperandHex.length()-1),16)) < 0;
                    sf = signedValue > 127;
                    zf = signedValue == 0;
                    of = (memory[address] < 128 && secondOperand >= 128 && sf) || (memory[address] >= 128 && secondOperand < 128 && !sf) ;
                }
                //If second operand is character, gets its ascii value and realizes compare operation.
            } else if((stringSecondOperand.charAt(0) == '"' && stringSecondOperand.endsWith("\"")) || (stringSecondOperand.charAt(0) == '\'' && stringSecondOperand.endsWith("'"))){
                if(stringSecondOperand.length() > 3){
                    System.out.println("Incompatible data type at line " + pc);  System.exit(0);
                } else if(stringSecondOperand.length() == 3){
                    byte charValue = (byte) stringSecondOperand.charAt(1);
                    signedValue = memory[address] - charValue;
                    if(cf = signedValue < 0)
                        signedValue += 256;
                    String firstOperandHex = Integer.toHexString(memory[address]);
                    String secondOperandHex = Integer.toHexString(charValue);
                    af = (Integer.parseInt(firstOperandHex.substring(firstOperandHex.length()-1),16) - Integer.parseInt(secondOperandHex.substring(secondOperandHex.length()-1),16)) < 0;
                    sf = signedValue > 127;
                    of = (memory[address] >= 128 && !sf) ;
                    zf = signedValue == 0;
                }
            } else {
                System.out.println("Syntax error at line  " + pc); System.exit(0);
            }
        }
    }
}
