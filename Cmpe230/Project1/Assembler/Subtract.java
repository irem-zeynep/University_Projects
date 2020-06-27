package Cmpe230;

public class Subtract extends Helper {
    //Subtracts second operand from two byte sized register, updates flags.
    public static void subtractWordToReg(String secondOperand, int destination, boolean hasSubReg){
        int address, signedValue, operand, source;
        //If second operand is byte sized register, gives error.
        if (determineByteRegister(secondOperand) != -1) {
            System.out.println("Byte-word combinations are not allowed at line " + pc); System.exit(0);
        }
        source = determineWordRegister(secondOperand);
        //If second operand is word sized register, subtracts its value from destination and puts signedValue into two byte destination register.
        if (source != -1) {
            signedValue = (twoByteRegisters[destination] - twoByteRegisters[source]);
            if(cf = (signedValue < 0))
                signedValue += 65536;
            String firstOperandHex = Integer.toHexString(twoByteRegisters[destination]);
            String secondOperandHex = Integer.toHexString(twoByteRegisters[source]);
            af = (Integer.parseInt(firstOperandHex.substring(firstOperandHex.length()-1),16) - Integer.parseInt(secondOperandHex.substring(secondOperandHex.length()-1),16)) < 0;
            zf = signedValue == 0;
            sf = 32767 < signedValue;
            of = (twoByteRegisters[destination] < 32768 && twoByteRegisters[source] >= 32768 && sf) || (twoByteRegisters[destination] >= 32768 && twoByteRegisters[source] < 32768 && !sf) ;
            twoByteRegisters[destination] = signedValue;
        } //Memory to Register
        else if (secondOperand.length() > 1 && (secondOperand.charAt(1) == '[' || secondOperand.charAt(0) == '[') && secondOperand.endsWith("]")) {
            //If second operand points byte sized address, gives throws error.
            if (secondOperand.charAt(0) == 'b' || secondOperand.charAt(0) == 'B'){
                System.out.println("Incompatible data type at line " + pc); System.exit(0);
            }
            //If address is in valid address format, gets its value by calling method from helper class.
            String stringAddress = secondOperand.substring(secondOperand.indexOf("[") + 1, secondOperand.length() - 1);
            address = determineAddress(stringAddress);
            if (isAddress){
                //If address location belongs to instructions, gives error.
                if(address < ip){
                    System.out.println("Not permitted to access that address" + pc); System.exit(0);
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
                twoByteRegisters[destination] = signedValue;
            //If second operand starts with "[" and ends with "]" but it is not in the form of integer of valid register form, gives syntax error.
            } else {
                System.out.println("Syntax error at line " + pc); System.exit(0);
            }
        } else {
            //If second operand is integer format, converts it.
            operand = convertInteger(secondOperand);
            if (isNumber) {
                if (operand > 65535){
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
                twoByteRegisters[destination] = signedValue;
                zf = signedValue == 0;
            //If second operand is character gets its ascii value and realizes subtraction.
            } else if ((secondOperand.charAt(0) == '\"' && secondOperand.endsWith("\"")) || (secondOperand.charAt(0) == '\'' && secondOperand.endsWith("'"))) {
                //HYP86 does not work with strings
                if (secondOperand.length() > 3) {
                    System.out.println("Byte-sized required at line " + pc); System.exit(0);
                } else if (secondOperand.length() == 3) {
                    byte first = (byte) secondOperand.charAt(1);
                    signedValue = (twoByteRegisters[destination] - first);
                    if(cf = signedValue <0)
                        signedValue += 65536;
                    String firstOperandHex = Integer.toHexString(twoByteRegisters[destination]);
                    String secondOperandHex = Integer.toHexString(first);
                    af = (Integer.parseInt(firstOperandHex.substring(firstOperandHex.length()-1),16) - Integer.parseInt(secondOperandHex.substring(secondOperandHex.length()-1),16)) < 0;
                    sf = signedValue > 32767;
                    of = (twoByteRegisters[destination] >= 32768 && !sf) ;
                    zf = signedValue == 0;
                    twoByteRegisters[destination] = signedValue;
                }
            } else {
                System.out.println("Syntax error at line " + pc); System.exit(0);
            }
        }
        //If destination register has child registers, updates them.
        if (hasSubReg){
            updateChild(destination);
        }
    }
    //Subtracts second operand from one byte sized register, updates flags.
    public static void subtractByteToReg(String secondOperand, short destination){
        int source, signedValue;
        //If second operand is word sized register, gives error.
        if(determineWordRegister(secondOperand) != -1){
            System.out.println("Byte-word combinations are not allowed at line " + pc); System.exit(0);
        }
        //If second operand is byte sized register, subtract its value from destination and puts signedValue into one byte destination register.
        source = determineByteRegister(secondOperand);
        if (source != -1) {
            signedValue = (oneByteRegisters[destination] - oneByteRegisters[source]);
            if(cf = (signedValue < 0))
                signedValue += 256;
            String firstOperandHex = Integer.toHexString(oneByteRegisters[destination]);
            String secondOperandHex = Integer.toHexString(oneByteRegisters[source]);
            af = (Integer.parseInt(firstOperandHex.substring(firstOperandHex.length()-1),16) - Integer.parseInt(secondOperandHex.substring(secondOperandHex.length()-1),16)) < 0;
            zf = signedValue == 0;
            sf = 127 < signedValue;
            of = (oneByteRegisters[destination] < 128 && oneByteRegisters[source] >= 128 && sf) || (oneByteRegisters[destination] >= 128 && oneByteRegisters[source] < 128 && !sf) ;
            oneByteRegisters[destination] = (short) signedValue;
        }//Memory to Register
        else if (secondOperand.length() > 1 && secondOperand.charAt(1) == '[' && secondOperand.endsWith("]")) {
            //If second operand points word sized address, than throws error.
            if (secondOperand.charAt(0) == 'W' || secondOperand.charAt(0) == 'w'){
                System.out.println("Byte-word combinations are not allowed at line " + pc); System.exit(0);
            }
            String stringAddress;
            if(secondOperand.charAt(1) == '[') stringAddress = secondOperand.substring(2, secondOperand.length() - 1);
            else stringAddress = secondOperand.substring(1, secondOperand.length() - 1);
            //If address is in valid address format, gets its value by calling method from helper class.
            int address = determineAddress(stringAddress);
            if (isAddress) {
                //If address location belongs to instructions, gives error.
                if(address < ip){
                    System.out.println("Not permitted to access that address at line " + pc); System.exit(0);
                }
                int operand = memory[address];
                signedValue = (oneByteRegisters[destination] - operand);
                if(cf = (signedValue < 0))
                    signedValue += 256;
                String firstOperandHex = Integer.toHexString(oneByteRegisters[destination]);
                String secondOperandHex = Integer.toHexString(operand);
                af = (Integer.parseInt(firstOperandHex.substring(firstOperandHex.length()-1),16) - Integer.parseInt(secondOperandHex.substring(secondOperandHex.length()-1),16)) < 0;
                zf = signedValue == 0;
                sf = 127 < signedValue;
                of = (oneByteRegisters[destination] < 128 && operand >= 128 && sf) || (oneByteRegisters[destination] >= 128 && operand < 128 && !sf) ;
                oneByteRegisters[destination] = (short) signedValue;
            //If second operand starts with "[" and ends with "]" but it is not in the form of integer of valid register form, gives syntax error.
            } else {
                System.out.println("Syntax error at line " + pc);
            }
        } else {
            //If second operand is integer format, converts it.
            int operand = convertInteger(secondOperand);
            if (isNumber) {
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
                oneByteRegisters[destination] = (short) signedValue;
            //If second operand is integer format, converts it.
            } else if ((secondOperand.charAt(0) == '\"' && secondOperand.endsWith("\"")) || (secondOperand.charAt(0) == '\'' && secondOperand.endsWith("'"))) {
                if (secondOperand.length() > 3) {
                    //HYP86 does not work with strings.
                    System.out.println("Byte-sized required at line " + pc); System.exit(0);
                } else if (secondOperand.length() == 3) {
                    byte first = (byte) secondOperand.charAt(1);
                    signedValue = (oneByteRegisters[destination] - first);
                    if(cf = (signedValue < 0))
                        signedValue += 256;
                    String firstOperandHex = Integer.toHexString(oneByteRegisters[destination]);
                    String secondOperandHex = Integer.toHexString(first);
                    af = (Integer.parseInt(firstOperandHex.substring(firstOperandHex.length()-1),16) - Integer.parseInt(secondOperandHex.substring(secondOperandHex.length()-1),16)) < 0;
                    zf = signedValue == 0;
                    sf = 127 < signedValue;
                    of =  (oneByteRegisters[destination] >= 128 && !sf) ;
                    oneByteRegisters[destination] = (short) signedValue;
                }
            } else {
                System.out.println("Syntax error at line  " + pc); System.exit(0);
            }
        }
        //Updates parent registers of one byte sized register.
        updateParent(destination);
    }
    //Subtracts second operand from two byte sized memory location, updates flags.
    public static void subtractWordToMemory(String stringFirstOperand, String secondOperand, boolean hasPrefix){
        int  address, source, signedValue;
        address = determineAddress(stringFirstOperand);
        //If first operand is not in valid address format, gives error.
        if(!isAddress){
            System.out.println("Syntax error at line " + pc); System.exit(0);
        }
        //If address location belongs to instructions, gives error.
        if(address < ip){
            System.out.println("Not permitted to access that address" + pc); System.exit(0);
        }
        //Calculates first operands value.
        String lowPart = Integer.toBinaryString(memory[address]);
        String stringOperand = Integer.toBinaryString(memory[address+1]).concat(("00000000" + lowPart).substring(lowPart.length()));
        int firstOperand = Integer.parseInt(stringOperand, 2);
        //If second operand is register, get register index.
        if(determineByteRegister(secondOperand) != -1){
            if(hasPrefix){
                System.out.println("Byte-word combinations are not allowed at line " + pc); System.exit(0);
            //Else, first operand's type is determined by second operand. Calls the subtract method for one byte sized memory locations.
            } else {
                subtractByteToMemory(stringFirstOperand, secondOperand); return;
            }
        }
        //If second operand is two byte register, realizes subtraction operation.
        source = determineWordRegister(secondOperand);
        if (source != -1) {
            signedValue = firstOperand - twoByteRegisters[source];
            if(cf = signedValue < 0)
                signedValue += 65536;
            String firstOperandHex = Integer.toHexString(firstOperand);
            String secondOperandHex = Integer.toHexString(twoByteRegisters[source]);
            af = (Integer.parseInt(firstOperandHex.substring(firstOperandHex.length()-1),16) - Integer.parseInt(secondOperandHex.substring(secondOperandHex.length()-1),16)) < 0;
            zf = signedValue == 0;
            sf = signedValue > 32767;
            of = (firstOperand < 32768 && twoByteRegisters[source] >= 32768 && sf) || (firstOperand >= 32768 && twoByteRegisters[source] < 32768 && !sf) ;
            String binaryFormat = Integer.toBinaryString(signedValue);
            if (binaryFormat.length() > 8){
                memory[address+1] = (short)Integer.parseInt(binaryFormat.substring(0, binaryFormat.length()-8), 2);
                memory[address] = (short)Integer.parseInt(binaryFormat.substring(binaryFormat.length()-8), 2);
            } else {
                memory[address] = (short) Integer.parseInt(binaryFormat, 2);
                memory[address+1] = 0;
            }
        } else {
            //If second operand is in number format, convert it into integer.
            int valueOfSecondOperand = convertInteger(secondOperand);
            if (isNumber) {
                //If second operand's size is greater than two byte, gives error. Else realizes subtraction operation.
                if (valueOfSecondOperand > 65535 || valueOfSecondOperand < 0){
                    System.out.println("Word-sized required at line " + pc); System.exit(0);
                } else {
                    signedValue = firstOperand - valueOfSecondOperand;
                    if( cf = signedValue <0)
                        signedValue += 65536;
                    String firstOperandHex = Integer.toHexString(firstOperand);
                    String secondOperandHex = Integer.toHexString(valueOfSecondOperand);
                    af = (Integer.parseInt(firstOperandHex.substring(firstOperandHex.length()-1),16) - Integer.parseInt(secondOperandHex.substring(secondOperandHex.length()-1),16)) < 0;
                    zf = signedValue == 0;
                    sf = signedValue > 32767;
                    of = (firstOperand < 32768 && valueOfSecondOperand >= 32768 && sf) || (firstOperand >= 32768 && valueOfSecondOperand < 32768 && !sf) ;
                    String binaryFormat = Integer.toBinaryString(signedValue);
                    if(binaryFormat.length() > 8){
                        memory[address+1] = (short)Integer.parseInt(binaryFormat.substring(0, binaryFormat.length()-8), 2);
                        memory[address] = (short)Integer.parseInt(binaryFormat.substring(binaryFormat.length()-8), 2);
                    } else {
                        memory[address+1] = 0;
                        memory[address] = (short)Integer.parseInt(binaryFormat, 2);
                    }
                }
                //If second operand is in character format
            } else if ((secondOperand.charAt(0) == '\"' && secondOperand.endsWith("\"")) || (secondOperand.charAt(0) == '\'' && secondOperand.endsWith("'"))) {
                //If first operand's size is not specified, gives error.
                if (!hasPrefix) {
                    System.out.println("Byte or Word? at line " + pc); System.exit(0);
                }
                //Hyp86 does not work with strings.
                if (secondOperand.length() > 3) {
                    System.out.println("Byte-sized required at line " + pc);
                    System.exit(0);
                } else if (secondOperand.length() == 3) {
                    if(address < ip){
                        System.out.println("Not permitted to access that address" + pc);
                        System.exit(0);
                    }
                    byte valueOfChar = (byte) secondOperand.charAt(1);
                    signedValue = firstOperand - valueOfChar;
                    if(cf = signedValue <0)
                        signedValue += 65536;
                    String firstOperandHex = Integer.toHexString(firstOperand);
                    String secondOperandHex = Integer.toHexString(valueOfChar);
                    af = (Integer.parseInt(firstOperandHex.substring(firstOperandHex.length()-1),16) - Integer.parseInt(secondOperandHex.substring(secondOperandHex.length()-1),16)) < 0;
                    zf = signedValue == 0;
                    sf = signedValue > 32767;
                    of = (firstOperand >= 32768 && !sf) ;
                    String binaryFormat = Integer.toBinaryString(signedValue);
                    if (binaryFormat.length() > 8){
                        memory[address+1] = (short)Integer.parseInt(binaryFormat.substring(0, binaryFormat.length()-8), 2);
                        memory[address] = (short)Integer.parseInt(binaryFormat.substring(binaryFormat.length()-8), 2);
                    } else {
                        memory[address] = (short)Integer.parseInt(binaryFormat, 2);
                        memory[address] = 0;
                    }
                }
            } else {
                System.out.println("Syntax error at line " + pc); System.exit(0);
            }
        }
    }

    // Subtracts second operand from two byte sized memory location, updates flags.
    public static void subtractByteToMemory(String firstOperand, String secondOperand){
        int address, source, signedValue;
        //means this is decimal
        address = determineAddress(firstOperand);
        //If first operand is not in valid address format, gives error.
        if(!isAddress){
            System.out.println("Syntax error at line " + pc); System.exit(0);
        }
        //If address location belongs to instructions, gives error.
        if(address < ip){
            System.out.println("Not permitted to access that address" + pc); System.exit(0);
        }
        //If second operand is two byte register, gives error. Else if it is two byte register, realizes subtraction operation.
        if (determineWordRegister(secondOperand) != -1) {
            System.out.println("Byte-word combinations are not allowed at line " + pc); System.exit(0);
        }
        source = determineByteRegister(secondOperand);
        if (source != -1) {
            signedValue = memory[address] - oneByteRegisters[source];
            if(cf = signedValue <0)
                signedValue += 256;
            String firstOperandHex = Integer.toHexString(memory[address]);
            String secondOperandHex = Integer.toHexString(oneByteRegisters[source]);
            af = (Integer.parseInt(firstOperandHex.substring(firstOperandHex.length()-1),16) - Integer.parseInt(secondOperandHex.substring(secondOperandHex.length()-1),16)) < 0;
            sf = signedValue > 127;
            zf = signedValue == 0;
            of = (memory[address] < 128 && oneByteRegisters[source] >= 128 && sf) || (memory[address] >= 128 && oneByteRegisters[source] < 128 && !sf) ;
            memory[address] = (short) signedValue;
        } else {
            //If second operand is a number, converts it into integer. If it is greater than 1 byte gives an error.
            int secOp = convertInteger(secondOperand);
            if (isNumber) {
                if (secOp < 0 || secOp > 255){
                    System.out.println("Byte-sized required at line " + pc); System.exit(0);
                } else {
                    signedValue =  memory[address] - secOp;
                    if(cf = signedValue < 0)
                        signedValue += 256;
                    String firstOperandHex = Integer.toHexString(memory[address]);
                    String secondOperandHex = Integer.toHexString(secOp);
                    af = (Integer.parseInt(firstOperandHex.substring(firstOperandHex.length()-1),16) - Integer.parseInt(secondOperandHex.substring(secondOperandHex.length()-1),16)) < 0;
                    sf = signedValue > 127;
                    zf = signedValue == 0;
                    of = (memory[address] < 128 && secOp >= 128 && sf) || (memory[address] >= 128 && secOp < 128 && !sf) ;
                    memory[address] = (short) signedValue;
                }
            //If second operand is character, gets its ascii value and realizes addition operation.
            } else if ((secondOperand.charAt(0) == '\"' && secondOperand.endsWith("\"")) || (secondOperand.charAt(0) == '\'' && secondOperand.endsWith("'"))) {
                //Hyp86 does not work with strings.
                if (secondOperand.length() > 3) {
                    System.out.println("Byte-sized required at line " + pc); System.exit(0);
                } else if (secondOperand.length() == 3) {
                    if(address < ip){
                        System.out.println("Not permitted to access that address" + pc); System.exit(0);
                    }
                    byte valueOfChar = (byte) secondOperand.charAt(1);
                    signedValue = memory[address] - valueOfChar;
                    if(cf = signedValue < 0)
                        signedValue += 256;
                    String firstOperandHex = Integer.toHexString(memory[address]);
                    String secondOperandHex = Integer.toHexString(valueOfChar);
                    af = (Integer.parseInt(firstOperandHex.substring(firstOperandHex.length()-1),16) - Integer.parseInt(secondOperandHex.substring(secondOperandHex.length()-1),16)) < 0;
                    sf = signedValue > 127;
                    of = (memory[address] >= 128 && !sf) ;
                    zf = signedValue == 0;
                    memory[address] = (short) signedValue;
                }
            } else {
                System.out.println("Syntax error at line " + pc); System.exit(0);
            }
        }
    }

    public static void subtractWordFromSp(String secondOperand){
        int address, signedValue, operand, source;
        //If second operand is byte sized register, gives error.
        if (determineByteRegister(secondOperand) != -1) {
            System.out.println("Byte-word combinations are not allowed at line " + pc); System.exit(0);
        }
        source = determineWordRegister(secondOperand);
        //If second operand is word sized register, subtracts its value from destination and puts signedValue into sp.
        if (source != -1) {
            signedValue = (sp - twoByteRegisters[source]);
            if(cf = (signedValue < 0))
                signedValue += 65536;
            String firstOperandHex = Integer.toHexString(sp);
            String secondOperandHex = Integer.toHexString(twoByteRegisters[source]);
            af = (Integer.parseInt(firstOperandHex.substring(firstOperandHex.length()-1),16) - Integer.parseInt(secondOperandHex.substring(secondOperandHex.length()-1),16)) < 0;
            zf = signedValue == 0;
            sf = 32767 < signedValue;
            of = (sp < 32768 && twoByteRegisters[source] >= 32768 && sf) || (sp >= 32768 && twoByteRegisters[source] < 32768 && !sf) ;
            sp = signedValue;
        } //Memory to Register
        else if (secondOperand.length() > 1 && (secondOperand.charAt(1) == '[' || secondOperand.charAt(0) == '[') && secondOperand.endsWith("]")) {
            //If second operand points byte sized address, gives error.
            if (secondOperand.charAt(0) == 'b' || secondOperand.charAt(0) == 'B'){
                System.out.println("Incompatible data type at line " + pc); System.exit(0);
            }
            //If address is in valid address format, gets its value by calling method from helper class.
            String stringAddress = secondOperand.substring(secondOperand.indexOf("[") + 1, secondOperand.length() - 1);
            address = determineAddress(stringAddress);
            if (isAddress){
                //If address location belongs to instructions, gives error.
                if(address < ip){
                    System.out.println("Not permitted to access that address" + pc); System.exit(0);
                }
                String lowPart = Integer.toBinaryString(memory[address]);
                String stringOperand = Integer.toBinaryString(memory[address+1]).concat(("00000000" + lowPart).substring(lowPart.length()));
                operand = Integer.parseInt(stringOperand,2);
                signedValue = sp - operand;
                if(cf = signedValue < 0)
                    signedValue += 65536;
                String firstOperandHex = Integer.toHexString(sp);
                String secondOperandHex = Integer.toHexString(operand);
                af = (Integer.parseInt(firstOperandHex.substring(firstOperandHex.length()-1),16) - Integer.parseInt(secondOperandHex.substring(secondOperandHex.length()-1),16)) < 0;
                zf = signedValue == 0;
                sf = 32767 < signedValue;
                of = (sp < 32768 && operand >= 32768 && sf) || (sp >= 32768 && operand < 32768 && !sf) ;
                sp = signedValue;
            //If second operand starts with "[" and ends with "]" but it is not in the form of integer of valid register form, gives syntax error.
            } else {
                System.out.println("Syntax error at line " + pc); System.exit(0);
            }
        } else {
            //If second operand is integer format, converts it.
            operand = convertInteger(secondOperand);
            if (isNumber) {
                if (operand > 65535){
                    System.out.println("Word-sized required at line " + pc); System.exit(0);
                }
                signedValue = (sp - operand);
                if(cf = signedValue < 0)
                    signedValue += 65536;
                String firstOperandHex = Integer.toHexString(sp);
                String secondOperandHex = Integer.toHexString(operand);
                af = (Integer.parseInt(firstOperandHex.substring(firstOperandHex.length()-1),16) - Integer.parseInt(secondOperandHex.substring(secondOperandHex.length()-1),16)) < 0;
                sf = signedValue > 32767;
                of = (sp < 32768 && operand >= 32768 && sf) || (sp >= 32768 && operand < 32768 && !sf) ;
                sp = signedValue;
                zf = signedValue == 0;
            //If second operand is character gets its ascii value and realizes addition.
            } else if ((secondOperand.charAt(0) == '\"' && secondOperand.endsWith("\"")) || (secondOperand.charAt(0) == '\'' && secondOperand.endsWith("'"))) {
                //HYP86 does not work with strings.
                if (secondOperand.length() > 3) {
                    System.out.println("Byte-sized required at line " + pc); System.exit(0);
                } else if (secondOperand.length() == 3) {
                    byte first = (byte) secondOperand.charAt(1);
                    signedValue = (sp - first);
                    if(cf = signedValue <0)
                        signedValue += 65536;
                    String firstOperandHex = Integer.toHexString(sp);
                    String secondOperandHex = Integer.toHexString(first);
                    af = (Integer.parseInt(firstOperandHex.substring(firstOperandHex.length()-1),16) - Integer.parseInt(secondOperandHex.substring(secondOperandHex.length()-1),16)) < 0;
                    sf = signedValue > 32767;
                    of = (sp >= 32768 && !sf) ;
                    zf = signedValue == 0;
                    sp = signedValue;
                }
            } else {
                System.out.println("Syntax error at line " + pc); System.exit(0);
            }
        }
    }
}

