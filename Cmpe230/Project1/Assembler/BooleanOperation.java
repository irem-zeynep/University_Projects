package Cmpe230;

// Type 0 stands for XOR.
// Type 1 stands for OR.
// Type 2 stands for AND.
public class BooleanOperation extends Helper {
    //Performs the operation for two byte sized registers depending on the type, updates flags.
    public static void wordToReg(String secondOperand, int destination, boolean hasSubReg, int type) {
        int address, operand, source;
        // Checks whether secondOperand is a two byte sized register.
        source = determineWordRegister(secondOperand);
        if (source != -1) {
            if (type == 0)
                twoByteRegisters[destination] = twoByteRegisters[destination] ^ twoByteRegisters[source];
            else if (type == 1)
                twoByteRegisters[destination] = twoByteRegisters[destination] | twoByteRegisters[source];
            else
                twoByteRegisters[destination] = twoByteRegisters[destination] & twoByteRegisters[source];
            of = false;
            cf = false;
            zf = twoByteRegisters[destination] == 0;
            sf = twoByteRegisters[destination] > 32767;
            // Checks if secondOperand is a memory address.
        } else if (secondOperand.length() > 1 && (secondOperand.charAt(1) == '[' || secondOperand.charAt(0) == '[' ) && secondOperand.endsWith("]")) {
            // Word byte combination not allowed.
            if (secondOperand.charAt(0) == 'b' || secondOperand.charAt(0) == 'B') {
                System.out.println("Incompatible data type at line " + pc); System.exit(0);
            }
            // Address value inside the square brackets.
            String subSecondOperand = secondOperand.substring((secondOperand.indexOf('[')) +1, secondOperand.length() - 1);
            address = determineAddress(subSecondOperand);
            if (isAddress){
                if(address < ip){
                    System.out.println("Not permitted to access that address at line " + pc); System.exit(0);
                }
                String append = Integer.toBinaryString(memory[address]);
                String stringOperand = Integer.toBinaryString(memory[address+1]).concat(("00000000" + append).substring(append.length()));
                operand = Integer.parseInt(stringOperand);
                if (type == 0)
                    twoByteRegisters[destination] = twoByteRegisters[destination] ^ operand;
                else if (type == 1)
                    twoByteRegisters[destination] = twoByteRegisters[destination] | operand;
                else
                    twoByteRegisters[destination] = twoByteRegisters[destination] & operand;
                of = false;
                cf = false;
                zf = twoByteRegisters[destination] == 0;
                sf = twoByteRegisters[destination] > 32767;
            } else {
                System.out.println("Syntax error at line " + pc);
            }
        } else {
            // Checks if secondOperand is integer.
            int op = convertInteger(secondOperand);
            if (isNumber) {
                if (op < 0 || op > 65535) {
                    System.out.println("Overflow error at line " + pc); System.exit(0);
                } else {
                    if (type == 0)
                        twoByteRegisters[destination] = twoByteRegisters[destination] ^ op;
                    else if (type == 1)
                        twoByteRegisters[destination] = twoByteRegisters[destination] | op;
                    else
                        twoByteRegisters[destination] = twoByteRegisters[destination] & op;
                    of = false;
                    cf = false;
                    zf = twoByteRegisters[destination] == 0;
                    sf = twoByteRegisters[destination] > 32767;
                }
                // Checks whether secondOperand is a character between "" or ''.
            } else if ((secondOperand.charAt(0) == '\'' && secondOperand.endsWith("'")) || (secondOperand.charAt(0) == '"' && secondOperand.endsWith("\""))) {
                if (secondOperand.length() > 3){
                    System.out.println("Byte-sized required at line " + pc); System.exit(0);
                } else if (secondOperand.length() == 3){
                    if (type == 0)
                        twoByteRegisters[destination] = twoByteRegisters[destination] ^ (byte)secondOperand.charAt(1);
                    else if (type == 1)
                        twoByteRegisters[destination] = twoByteRegisters[destination] | (byte)secondOperand.charAt(1);
                    else
                        twoByteRegisters[destination] = twoByteRegisters[destination] & (byte)secondOperand.charAt(1);
                    of = false;
                    cf = false;
                    zf = twoByteRegisters[destination] == 0;
                    sf = twoByteRegisters[destination] > 32767;
                }
            } else if (byteVariables.containsKey(secondOperand)) {
                System.out.println("Incompatible data types " + pc); System.exit(0);
            } else if (wordVariables.containsKey(secondOperand)) {
                int index = wordVariables.get(secondOperand);
                if(index < ip){
                    System.out.println("Not permitted to access that address at line " + pc); System.exit(0);
                }
                String append = Integer.toBinaryString(memory[index]);
                String stringOperand = Integer.toBinaryString(memory[index + 1]).concat(("00000000" + append).substring(append.length()));

                if (type == 0)
                    twoByteRegisters[destination] = twoByteRegisters[destination] ^ Integer.parseInt(stringOperand,2);
                else if (type == 1)
                    twoByteRegisters[destination] = twoByteRegisters[destination] | Integer.parseInt(stringOperand,2);
                else
                    twoByteRegisters[destination] = twoByteRegisters[destination] & Integer.parseInt(stringOperand,2);
                of = false;
                cf = false;
                zf = twoByteRegisters[destination] == 0;
                sf = twoByteRegisters[destination] > 32767;
            } else {
                System.out.println("No such variable at line " + pc); System.exit(0);
            }
        }
        // Updates children of given register.
        if (hasSubReg){
            updateChild(destination);
        }
    }
    //Performs the operation for one byte sized registers depending on the type, updates flags.
    public static void byteToReg(String secondOperand, short destination, int type) {
        int address, operand, source;
        // If secondOperand is a word sized register we terminate.
        if (secondOperand.equalsIgnoreCase("ax") || secondOperand.equalsIgnoreCase("bx") ||
                secondOperand.equalsIgnoreCase("cx") || secondOperand.equalsIgnoreCase("dx") ||
                secondOperand.equalsIgnoreCase("si") || secondOperand.equalsIgnoreCase("sÃƒÆ’Ã‚Â½") ||
                secondOperand.equalsIgnoreCase("di") || secondOperand.equalsIgnoreCase("dÃƒÆ’Ã‚Â½") ||
                secondOperand.equalsIgnoreCase("sp")) {
            System.out.println("Incompatible data types " + pc);
            System.exit(0);
        }
        // Checks if secondOperand is byte sized register.
        source = determineByteRegister(secondOperand);
        if (source != -1) {
            if (type == 0)
                oneByteRegisters[destination] = (short) (oneByteRegisters[destination] ^ oneByteRegisters[source]);
            else if (type == 1)
                oneByteRegisters[destination] = (short) (oneByteRegisters[destination] | oneByteRegisters[source]);
            else
                oneByteRegisters[destination] = (short) (oneByteRegisters[destination] & oneByteRegisters[source]);
            of = false;
            cf = false;
            zf = oneByteRegisters[destination] == 0;
            sf = oneByteRegisters[destination] > 127;
            // Checks if secondOperand is a memory address.
        } else if (secondOperand.length() > 1 && (secondOperand.charAt(1) == '[' || secondOperand.charAt(0) == '[') && secondOperand.endsWith("]")) {
            // If address is word sized we terminate.
            if (secondOperand.charAt(0) == 'w' || secondOperand.charAt(0) == 'W') {
                System.out.println("Incompatible data type at line " + pc); System.exit(0);
            }
            // Address value between square brackets.
            String subSecondOperand = secondOperand.substring((secondOperand.indexOf('['))+1, secondOperand.length() - 1);
            address = determineAddress(subSecondOperand);
            if (isAddress){
                if(address < ip){
                    System.out.println("Not permitted to access that address at line " + pc); System.exit(0);
                }
                String stringOperand = Integer.toBinaryString(memory[address]);
                operand = Integer.parseInt(stringOperand , 2);
                if (type == 0)
                    oneByteRegisters[destination] = (short) (oneByteRegisters[destination] ^ operand);
                else if (type == 1)
                    oneByteRegisters[destination] = (short) (oneByteRegisters[destination] | operand);
                else
                    oneByteRegisters[destination] = (short) (oneByteRegisters[destination] & operand);
                of = false;
                cf = false;
                zf = oneByteRegisters[destination] == 0;
                sf = oneByteRegisters[destination] > 127;
            } else {
                System.out.println("Syntax error at line " + pc); System.exit(0);
            }
        } else {
            // Checks whether secondOperand is integer.
            int op = convertInteger(secondOperand);
            if (isNumber) {
                if (of = (op > 65535)) {
                    System.out.println("Overflow error at line " + pc); System.exit(0);
                } else if (op <= 255) {
                    if (type == 0)
                        oneByteRegisters[destination] = (short) (oneByteRegisters[destination] ^ op);
                    else if (type == 1)
                        oneByteRegisters[destination] = (short) (oneByteRegisters[destination] | op);
                    else
                        oneByteRegisters[destination] = (short) (oneByteRegisters[destination] & op);
                    cf = false;
                    zf = oneByteRegisters[destination] == 0;
                    sf = oneByteRegisters[destination] > 127;
                } else {
                    System.out.println("Byte sized constant required " + pc); System.exit(0);
                }
                // Checks whether secondOperand is a character between "" or ''.
            } else if ((secondOperand.charAt(0) == '\'' && secondOperand.endsWith("'")) || (secondOperand.charAt(0) == '"' && secondOperand.endsWith("\""))) {

                if (of = secondOperand.length() > 3){
                    System.out.println("overflow at line " + pc); System.exit(0);
                } else if (secondOperand.length() == 3) {
                    if (type == 0)
                        oneByteRegisters[destination] = (short) (oneByteRegisters[destination] ^ (byte) secondOperand.charAt(1));
                    else if (type == 1)
                        oneByteRegisters[destination] = (short) (oneByteRegisters[destination] | (byte) secondOperand.charAt(1));
                    else
                        oneByteRegisters[destination] = (short) (oneByteRegisters[destination] & (byte) secondOperand.charAt(1));
                    cf = false;
                    zf = oneByteRegisters[destination] == 0;
                    sf = oneByteRegisters[destination] > 127;
                }
            } else {
                System.out.println("Syntax error at line " + pc); System.exit(0);
            }
        }
        updateParent(destination);
    }
    // Performs the necessary operation with one byte sized memory location, updates flags.
    public static void byteToMemory(String firstOperand, String secondOperand, int type) {
        int address, source;
        address = determineAddress(firstOperand);
        //If first operand is not in valid address syntax, gives error.
        if(!isAddress) {
            System.out.println("Syntax error at line " + pc); System.exit(0);
        }
        if(address < ip){
            System.out.println("Not permitted to access that address at line " + pc); System.exit(0);
        }
        // Checks if secondOperand is two byte sized register.
        if(determineWordRegister(secondOperand) != -1){
            System.out.println("Byte-Word combination is not allowed at line " + pc); System.exit(0);
        }
        // Checks if secondOperand is one byte sized register.
        source = determineByteRegister(secondOperand);
        if (source != -1) {
            if (type == 0)
                memory[address] =  (short) (memory[address] ^ oneByteRegisters[source]);
            else if (type == 1)
                memory[address] =  (short) (memory[address] | oneByteRegisters[source]);
            else
                memory[address] =  (short) (memory[address] & oneByteRegisters[source]);
            of = false;
            cf = false;
            zf = memory[address] == 0;
            sf = memory[address] > 127;
        } else {
            int op = convertInteger(secondOperand);
            if (isNumber) {
                if (of = (op < 0 || op > 65535)) {
                    System.out.println("Overflow error at line " + pc); System.exit(0);
                } else if (op <= 255) {
                    if (type == 0)
                        memory[address] =  (short) (memory[address] ^ op);
                    else if (type == 1)
                        memory[address] =  (short) (memory[address] | op);
                    else
                        memory[address] =  (short) (memory[address] & op);
                    cf = false;
                    zf = memory[address] == 0;
                    sf = memory[address] > 127;
                } else {
                    System.out.println("Byte sized constant required at line " + pc); System.exit(0);
                }
                // Checks whether secondOperand is a character between "" or ''.
            } else if ((secondOperand.charAt(0) == '\'' && secondOperand.endsWith("'")) || (secondOperand.charAt(0) == '"' && secondOperand.endsWith("\""))) {
                if (secondOperand.length() > 3){
                    System.out.println("Byte-sized required at line " + pc); System.exit(0);
                } else if (secondOperand.length() == 3) {
                    if (type == 0)
                        memory[address] =  (short) (memory[address] ^ (byte) secondOperand.charAt(1));
                    else if (type == 1)
                        memory[address] =  (short) (memory[address] | (byte) secondOperand.charAt(1));
                    else
                        memory[address] =  (short) (memory[address] & (byte) secondOperand.charAt(1));
                    of = false;
                    cf = false;
                    zf = memory[address] == 0;
                    sf = memory[address] > 127;
                }
            } else if (byteVariables.containsKey(secondOperand)) {
                if (type == 0)
                    memory[address] =  (short) (memory[address] ^ memory[wordVariables.get(secondOperand)]);
                else if (type == 1)
                    memory[address] =  (short) (memory[address] | memory[wordVariables.get(secondOperand)]);
                else
                    memory[address] =  (short) (memory[address] & memory[wordVariables.get(secondOperand)]);
                of = false;
                cf = false;
                zf = memory[address] == 0;
                sf = memory[address] > 127;
            } else if (wordVariables.containsKey(secondOperand)) {
                System.out.println("Incompatible data types " + pc);
                System.exit(0);
            } else {
                System.out.println("No such variable at line " + pc);
                System.exit(0);
            }
        }
    }
    // Performs the necessary operation with two byte sized memory location, updates flags.
    public static void wordToMemory(String firstOperand, String secondOperand, int type, boolean hasPrefix) {
        int address, secOp;
        address = determineAddress(firstOperand);
        //If first operand is not in valid address syntax, gives error.
        if(!isAddress) {
            System.out.println("Syntax error at line " + pc); System.exit(0);
        }
        if(address < ip){
            System.out.println("Not permitted to access that address at line " + pc); System.exit(0);
        }
        String binaryFormat;
        if (determineByteRegister(secondOperand) != -1){
            if (hasPrefix){
                System.out.println("Byte-word combination is not allowed at line " + pc); System.exit(0);
            // If firstOperand does not contain prefix and secondOperand is a byte register then byteToMemory method is called.
            } else {
                byteToMemory(firstOperand, secondOperand, type);
                return;
            }
        }
        // Checks if secondOperand is word register.
        int source = determineWordRegister(secondOperand);
        if (source != -1 && hasSubReg){
            // 2*source +1 is the index of lower part of the register, 2*source is high part's.
            if (type == 0) {
                memory[address] = (short) (memory[address] ^ oneByteRegisters[2*source+1]);
                memory[address+1] = (short) (memory[address+1] ^ oneByteRegisters[2*source]);
            } else if (type == 1) {
                memory[address] = (short) (memory[address] | oneByteRegisters[2*source+1]);
                memory[address+1] = (short) (memory[address+1] | oneByteRegisters[2*source]);
            } else {
                memory[address] = (short) (memory[address] & oneByteRegisters[2*source+1]);
                memory[address+1] = (short) (memory[address+1] & oneByteRegisters[2*source]);
            }
            of = false;
            cf = false;
            zf = memory[address] == 0 && memory[address+1] == 0;
            sf = memory[address+1] > 127;
        } else if (source != -1){
            binaryFormat = Integer.toBinaryString(twoByteRegisters[source]);
            if (binaryFormat.length() > 8){
                if (type == 0) {
                    memory[address+1] = (short) (memory[address+1] ^ Integer.parseInt(binaryFormat.substring(0,binaryFormat.length()-8),2));
                    memory[address] = (short)( memory[address] ^ Integer.parseInt(binaryFormat.substring(binaryFormat.length()-8),2));
                } else if (type == 1) {
                    memory[address+1] = (short) (memory[address+1] | Integer.parseInt(binaryFormat.substring(0,binaryFormat.length()-8),2));
                    memory[address] = (short)( memory[address] | Integer.parseInt(binaryFormat.substring(binaryFormat.length()-8),2));
                } else {
                    memory[address+1] = (short) (memory[address+1] & Integer.parseInt(binaryFormat.substring(0,binaryFormat.length()-8),2));
                    memory[address] = (short)( memory[address] & Integer.parseInt(binaryFormat.substring(binaryFormat.length()-8),2));
                }
            } else {
                if (type == 0)
                    memory[address] = (short) (memory[address] ^ Integer.parseInt(binaryFormat,2));
                else if (type == 1)
                    memory[address] = (short) (memory[address] | Integer.parseInt(binaryFormat,2));
                else
                    memory[address] = (short) (memory[address] & Integer.parseInt(binaryFormat,2));
            }
            of = false;
            cf = false;
            zf = memory[address] == 0 && memory[address+1] == 0;
            sf = memory[address+1] > 127;
        } else {
            // Checks if secondOperand is integer.
            secOp = convertInteger(secondOperand);
            if (isNumber){
                if (of = (secOp < 0 || secOp > 65535)) {
                    System.out.println("overflow at line " + pc);  System.exit(0);
                } else {
                    binaryFormat = Integer.toBinaryString(secOp);
                    if(binaryFormat.length()>8) {
                        if (type == 0) {
                            memory[address + 1] = (short) (memory[address + 1] ^ Integer.parseInt(binaryFormat.substring(0, binaryFormat.length() - 8), 2));
                            memory[address] = (short) (memory[address] ^ Integer.parseInt(binaryFormat.substring(binaryFormat.length() - 8), 2));
                        } else if (type == 1) {
                            memory[address + 1] = (short) (memory[address + 1] | Integer.parseInt(binaryFormat.substring(0, binaryFormat.length() - 8), 2));
                            memory[address] = (short) (memory[address] | Integer.parseInt(binaryFormat.substring(binaryFormat.length() - 8), 2));
                        } else {
                            memory[address + 1] = (short) (memory[address + 1] & Integer.parseInt(binaryFormat.substring(0, binaryFormat.length() - 8), 2));
                            memory[address] = (short) (memory[address] & Integer.parseInt(binaryFormat.substring(binaryFormat.length() - 8), 2));
                        }
                    } else {
                        // If w/b is not stated for the memory address we terminate.
                        if (!hasPrefix) {
                            System.out.println("Byte or Word? at line " + pc); System.exit(0);
                        }
                        if (type == 0) {
                            memory[address] = (short) (memory[address] ^ Integer.parseInt(binaryFormat, 2));
                        } else if (type == 1) {
                            memory[address] = (short) (memory[address] | Integer.parseInt(binaryFormat, 2));
                        } else {
                            memory[address + 1] = 0;
                            memory[address] = (short) (memory[address] & Integer.parseInt(binaryFormat, 2));
                        }
                    }
                    of = false;
                    cf = false;
                    zf = memory[address] == 0 && memory[address+1] == 0;
                    sf = memory[address+1] > 127;
                }
                // Checks whether secondOperand is a character between "" or ''.
            } else if ((secondOperand.charAt(0) == '\'' && secondOperand.endsWith("'")) || (secondOperand.charAt(0) == '"' && secondOperand.endsWith("\""))){
                if (secondOperand.length() > 3){
                    System.out.println("Byte-sized required at line " + pc); System.exit(0);
                } else if (secondOperand.length() == 3) {
                    // If type of the address is not given we terminate.
                    if(!hasPrefix){
                        System.out.println("Byte or Word ? at line " + pc); System.exit(0);
                    }
                    if (type == 0) {
                        memory[address] = (short)( memory[address] ^ (byte) secondOperand.charAt(1));
                    } else if (type == 1) {
                        memory[address] = (short)( memory[address] | (byte) secondOperand.charAt(1));
                    } else {
                        memory[address+1] = (short) (0);
                        memory[address] = (short)( memory[address] & (byte) secondOperand.charAt(1));
                    }
                    cf = false;
                    zf = memory[address] == 0 && memory[address+1] == 0;
                    sf = memory[address+1] > 127;
                }
            } else if (byteVariables.containsKey(secondOperand)){
                System.out.println("Incompatible data types " + pc); System.exit(0);
            } else if (wordVariables.containsKey(secondOperand)){
                int index = wordVariables.get(secondOperand);
                if (type == 0) {
                    memory[address+1] = (short)(memory[address+1] ^ memory[index+1]);
                    memory[address] = (short)(memory[address] ^ memory[index]);
                } else if (type == 1) {
                    memory[address+1] = (short)(memory[address+1] | memory[index+1]);
                    memory[address] = (short)(memory[address] | memory[index]);
                } else {
                    memory[address+1] = (short)(memory[address+1] & memory[index+1]);
                    memory[address] = (short)(memory[address] & memory[index]);
                }
                of = false;
                cf = false;
                zf = memory[address] == 0 && memory[address+1] == 0;
                sf = memory[address+1] > 127;
            } else{
                System.out.println("No such variable at line " + pc); System.exit(0);
            }
        }
    }
    // Performs the not operation.
    public static void notOperation(String operand) {
        //If given operand is register
        int destination;
        destination = determineWordRegister(operand);
        if (destination != -1) {
            twoByteRegisters[destination] = 65535 & ~twoByteRegisters[destination];
            if(hasSubReg){
                updateChild(destination);
            }
            return;
        }
        destination = determineByteRegister(operand);
        if (destination != -1) {
            oneByteRegisters[destination] = (short) (255 &  ~oneByteRegisters[destination]);
            updateParent(destination);
        }
        //If given operand is not register, than it should be address to point memory
        else{
            if (operand.length() > 1 && operand.charAt(1) == '[' && operand.endsWith("]")){
                int address;
                String stringAddress = operand.substring(2, operand.length()-1);
                //Gets the address value to point memory
                address = determineAddress(stringAddress);
                if (isAddress) {
                    if (operand.charAt(0) == 'w' || operand.charAt(0) == 'W') { // If operand points a word address
                        memory[address] = (short) ~memory[address];
                        memory[address + 1] = (short) ~memory[address + 1];
                    } else if (operand.charAt(0) == 'b' || operand.charAt(0) == 'B') { // If operand points a byte address
                        memory[address] = (short) ~memory[address];
                    }
                } else {
                    System.out.println("Constant operand required at line " +  pc); System.exit(0);
                }
            }
        }


    }
}