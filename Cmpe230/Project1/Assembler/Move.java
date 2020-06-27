package Cmpe230;

public class Move extends Helper {
    //Moves second operand into 2 byte registers.
    public static void moveWordToReg(String secondOperand, int destination, boolean hasSubReg) {
        //If registers are one byte registers give error. Else if registers are two byte registers than put their value into destination register.
        if (determineByteRegister(secondOperand) != -1){
            System.out.println("Byte-word combinations are not allowed at line " + pc); System.exit(0);
        }
        int source = determineWordRegister(secondOperand);
        if (source != -1) {
            twoByteRegisters[destination] = twoByteRegisters[source];
        }
        //If second operand has [ ] at sides, than it is memory to register situation
        else if (secondOperand.length() > 1 && (secondOperand.charAt(1) == '[' || secondOperand.charAt(0) == '[') && secondOperand.endsWith("]")) {
            //If it is byte sized memory, than gives an error.
            if(secondOperand.charAt(0) == 'b' || secondOperand.charAt(0) == 'B') {
                System.out.println("Byte-word combinations are not allowed at line " + pc); System.exit(0);
            }

            String stringAddress = secondOperand.substring(secondOperand.indexOf("[") + 1, secondOperand.length() - 1);
            //If address is in valid address format, gets its value by calling method from helper class.
            int address = determineAddress(stringAddress);
            if (isAddress) {
                //If address location belongs to instructions, gives error.
                if (address < ip) {
                    System.out.println("Not permitted to access that address"); System.exit(0);
                }
                //Concat two byte of memory address and assign the value stored in that location to operand.
                String lowPart = Integer.toBinaryString(memory[address]);
                String stringOperand = Integer.toBinaryString(memory[address+1]).concat(("00000000" + lowPart).substring(lowPart.length()));
                int operand = Integer.parseInt(stringOperand, 2);

                twoByteRegisters[destination] = operand; //Move operand into destination.
            } else {
                System.out.println("Syntax error at line " + pc); System.exit(0);
            }
        } else {
            //If second operand is constant and integer, parses it move into the destination.
            int operand = convertInteger(secondOperand);
            if(isNumber){
                //If size is greater than 16 bit than put the lowest 16 bit.
                if(operand > 65535){
                    String binaryFormat = Integer.toBinaryString(operand);
                    twoByteRegisters[destination] = Integer.parseInt(binaryFormat.substring(binaryFormat.length()-16), 2);
                } else {
                    twoByteRegisters[destination] = operand;
                }
                //If operand is character
            } else if(secondOperand.charAt(0) == '\"' && secondOperand.endsWith("\"") || secondOperand.charAt(0) == '\'' && secondOperand.endsWith("'")){
                //Hyp86 does not work with strings.
                if(secondOperand.length() > 3){
                    System.out.println("Incompatible data type at line " + pc); System.exit(0);
                } else if(secondOperand.length() == 3){
                    int first = (byte) secondOperand.charAt(1);
                    twoByteRegisters[destination] = first;
                }
            } else {
                System.out.println("Syntax error at line " + pc); System.exit(0);
            }
        }
        //If register has sub 8 bit registers than updates them too.
        if(hasSubReg){
            updateChild(destination);
        }
    }
    //Moves second operand into 1 byte registers.
    public static void moveByteToReg(String secondOperand, short destination) {
        //If second operand is two byte sized register than gives error. Else if it is one byte sized register put its value into destination.
        if (determineWordRegister(secondOperand) != -1){
            System.out.println("Byte-word combinations are not allowed at line " + pc); System.exit(0);
        }
        int source = determineByteRegister(secondOperand);
        if(source != -1){
            oneByteRegisters[destination] = oneByteRegisters[source];
        }
        //If second operand has [ ] at sides, than it is memory to register situation
        else if (secondOperand.length() > 1 && (secondOperand.charAt(1) == '['  || secondOperand.charAt(0) == '[') && secondOperand.endsWith("]")){
            //If second operand points word sized address, than throws error.
            if(secondOperand.charAt(0) == 'W' || secondOperand.charAt(0) == 'w' ){
                System.out.println("Byte-word combinations are not allowed at line " + pc); System.exit(0);
            } else {
                String stringAddress = secondOperand.substring(secondOperand.indexOf("[") + 1, secondOperand.length() - 1);
                int address = determineAddress(stringAddress);
                //If address is in valid address format, gets its value by calling method from helper class.
                if(isAddress){
                    //If address location belongs to instructions, gives error.
                    if(address < ip) {
                        System.out.println("Not permitted to access that address at line " + pc); System.exit(0);
                    }
                    oneByteRegisters[destination] = memory[address];
                } else {
                    System.out.println("Syntax error at line " + pc); System.exit(0);
                }
            }
        } else {
            //If second operand is in number format than convert it into integer.
            int operand = convertInteger(secondOperand);
            //If its value is greater than one byte size, than gives error. Else puts it into destination.
            if(isNumber){
                if (operand > 255) {
                    System.out.println("Byte-sized required at line " + pc); System.exit(0);
                }
                oneByteRegisters[destination] = (short) operand;
                //else if second operand is character.
            } else if ((secondOperand.charAt(0) == '\"' && secondOperand.endsWith("\"")) || (secondOperand.charAt(0) == '\'' && secondOperand.endsWith("'"))) {
                //Hyp86 does not work with strings.
                if (secondOperand.length() > 3) {
                    System.out.println("Byte-sized required at line " + pc); System.exit(0);
                } else if (secondOperand.length() == 3) {
                    oneByteRegisters[destination] = (byte) secondOperand.charAt(1);
                }
            } else {
                System.out.println("Syntax error at line " + pc); System.exit(0);
            }
        }
        //Updates parent register.
        updateParent(destination);
    }
    //Moves second operand two byte memory location.
    public static void moveWordToMemory(String firstOperand, String secondOperand, boolean hasPrefix){
        int address, secondOperandValue, source;
        //If address value is in number format convert it into integer, else if it is register use register's value to point memory. Else gives syntax error
        address = determineAddress(firstOperand);
        if(!isAddress){
            System.out.println("Syntax error at line " + pc); System.exit(0);
        }
        String binaryFormat;
        //If address location belongs to instructions, gives error.
        if (address < ip){
            System.out.println("Not permitted to access that address" + pc); System.exit(0);
        }
        //If second operand is one byte sized register, checks whether first operand has w in front of it.
        if(determineByteRegister(secondOperand) != -1){
            //If it has w, gives error.
            if(hasPrefix){
                System.out.println("Byte-word combinations are not allowed at line " + pc); System.exit(0);
                //Else, first operand's type is determined by second operand. Calls the move method for one byte sized memory locations.
            } else {
                moveByteToMemory(firstOperand, secondOperand); return;
            }
        }
        //If second operand is two byte register, put its value into memory location.
        source = determineWordRegister(secondOperand);
        if(source != -1){
            if(hasSubReg) {
                memory[address] = oneByteRegisters[2*source+1];
                memory[address + 1] = oneByteRegisters[2*source];
            } else {
                binaryFormat = Integer.toBinaryString(twoByteRegisters[source]);
                if (binaryFormat.length() > 8) {
                    memory[address+1] = (short)Integer.parseInt(binaryFormat.substring(0, binaryFormat.length()-8),2);
                    memory[address] = (short)Integer.parseInt(binaryFormat.substring(binaryFormat.length()-8),2);
                } else {
                    memory[address] = (short)Integer.parseInt(binaryFormat,2);
                }
            }
        } else {
            //If second operand is in number format, convert it into integer.
            secondOperandValue = convertInteger(secondOperand);
            if (isNumber){
                if (secondOperandValue > 65535) {
                    String binarySecOp = Integer.toBinaryString(secondOperandValue);
                    memory[address + 1] = (short) Integer.parseInt(binarySecOp.substring(binarySecOp.length() - 16, binarySecOp.length() - 8), 2);
                    memory[address] = (short) Integer.parseInt(binarySecOp.substring(binarySecOp.length() - 8), 2);
                } else if (secondOperandValue > 255) {
                    binaryFormat = Integer.toBinaryString(secondOperandValue);
                    memory[address + 1] = (short) Integer.parseInt(binaryFormat.substring(0, binaryFormat.length() - 8), 2);
                    memory[address] = (short) Integer.parseInt(binaryFormat.substring(binaryFormat.length() - 8), 2);
                } else {
                    //If second operand's size is one byte and memory location's size is not specified, gives error.
                    if(!hasPrefix){
                        System.out.println("Unknown data type at line " + pc); System.exit(0);
                    }
                    memory[address] = (short) secondOperandValue;
                }
                //If second operand is in character format
            } else if ((secondOperand.charAt(0) == '\"' && secondOperand.endsWith("\"")) || (secondOperand.charAt(0) == '\'' && secondOperand.endsWith("'"))) {
                //If first operand's size is not specified, gives error.
                if (!hasPrefix) {
                    System.out.println("Byte or Word? at line " + pc); System.exit(0);
                }
                //Hyp86 does not work with strings.
                if (secondOperand.length() > 3) {
                    System.out.println("Incompatible data type at line " + pc); System.exit(0);
                }
                else if (secondOperand.length() == 3) {
                    secondOperandValue = (byte) secondOperand.charAt(1);
                    memory[address] = (short) secondOperandValue;
                }
            } else {
                System.out.println("Syntax error at line " + pc);  System.exit(0);
            }
        }
    }

    public static void moveByteToMemory(String firstOperand, String secondOperand){
        int address, secondOperandValue, source;
        address = determineAddress(firstOperand);
        //If first operand is not in valid address syntax, gives error.
        if(!isAddress) {
            System.out.println("Syntax error at line " + pc); System.exit(0);
        }
        //If address location belongs to instructions, gives error.
        if (address < ip) {
            System.out.println("Not permitted to access that address" + pc);System.exit(0);
        }
        //If second operand is two byte register, gives error. Else if it is two byte register, realizes move operation.
        if (determineWordRegister(secondOperand) != -1) {
            System.out.println("Byte-word combinations are not allowed"); System.exit(0);
        }
        source = determineByteRegister(secondOperand);
        if (source != -1) {
            memory[address] = oneByteRegisters[source];
        } else {
            //If second operand is a number, converts it into integer. If it is greater than 1 byte gives an error.
            secondOperandValue = convertInteger(secondOperand);
            if (isNumber) {
                if (secondOperandValue > 255) {
                    System.out.println("Byte-sized required at line " + pc); System.exit(0);
                } else {
                    memory[address] = (short) secondOperandValue;
                }
            }
            //If second operand is character, gets its ascii value and move it into destination.
            else if (secondOperand.charAt(0) == '"' && secondOperand.endsWith("\"") || secondOperand.charAt(0) == '\'' && secondOperand.endsWith("'")) {
                //Hyp86 does not work with strings.
                if (secondOperand.length() > 3) {
                    System.out.println("Incompatible data type at line " + pc); System.exit(0);
                } else if (secondOperand.length() == 3) {
                    secondOperandValue = (byte) secondOperand.charAt(1);
                    memory[address] = (short) secondOperandValue;
                }
            } else {
                System.out.println("Syntax error at line " + pc); System.exit(0);
            }
        }
    }
    public static void moveWordToSp(String secondOperand) {
        //If registers are one byte registers give error. Else if registers are two byte registers than put their value into destination register.
        if (determineByteRegister(secondOperand) != -1){
            System.out.println("Byte-word combinations are not allowed at line " + pc); System.exit(0);
        }
        int source = determineWordRegister(secondOperand);
        if (source != -1) {
            sp = twoByteRegisters[source];
        }
        //If second operand has [ ] at sides, than it is memory to register situation
        else if (secondOperand.length() > 1 && (secondOperand.charAt(1) == '[' || secondOperand.charAt(0) == '[') && secondOperand.endsWith("]")) {
            //If it is byte sized memory, than gives an error.
            if(secondOperand.charAt(0) == 'b' || secondOperand.charAt(0) == 'B') {
                System.out.println("Byte-word combinations are not allowed at line " + pc); System.exit(0);
            }

            String stringAddress = secondOperand.substring(secondOperand.indexOf("[") + 1, secondOperand.length() - 1);
            //If address is in valid address format, gets its value by calling method from helper class.
            int address = determineAddress(stringAddress);
            if (isAddress) {
                //If address location belongs to instructions, gives error.
                if (address < ip) {
                    System.out.println("Not permitted to access that address"); System.exit(0);
                }
                //Concat two byte of memory address and assign the value stored in that location to operand.
                String lowPart = Integer.toBinaryString(memory[address]);
                String stringOperand = Integer.toBinaryString(memory[address+1]).concat(("00000000" + lowPart).substring(lowPart.length()));
                int operand = Integer.parseInt(stringOperand, 2);

                sp = operand; //Move operand into destination.
            } else {
                System.out.println("Syntax error at line " + pc); System.exit(0);
            }
        } else {
            //If second operand is constant and integer, parses it move into the destination.
            int operand = convertInteger(secondOperand);
            if(isNumber){
                //If size is greater than 16 bit than put the lowest 16 bit.
                if(operand > 65535){
                    String binaryFormat = Integer.toBinaryString(operand);
                    sp = Integer.parseInt(binaryFormat.substring(binaryFormat.length()-16), 2);
                } else {
                    sp = operand;
                }
                //If operand is character
            } else if(secondOperand.charAt(0) == '\"' && secondOperand.endsWith("\"") || secondOperand.charAt(0) == '\'' && secondOperand.endsWith("'")){
                //Hyp86 does not work with strings.
                if(secondOperand.length() > 3){
                    System.out.println("Incompatible data type at line " + pc); System.exit(0);
                } else if(secondOperand.length() == 3){
                    int first = (byte) secondOperand.charAt(1);
                    sp = first;
                }
            } else {
                System.out.println("Syntax error at line " + pc); System.exit(0);
            }
        }
    }
}
