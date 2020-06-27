package Cmpe230;

public class Add extends Helper {
    //Adds second operand to two byte sized register, updates flags.
    public static void addToWordReg(String secondOperand, int destination, boolean hasSubReg) {
        int address, operand, sum, source;
        //If second operand is byte sized register, gives error.
        if (determineByteRegister(secondOperand) != -1) {
            System.out.println("Byte-Word combinations are not allowed at line " + pc); System.exit(0);
        }
        source = determineWordRegister(secondOperand);
        //If second operand is word sized register, add its value to destination and puts sum's value into two byte destination register.
        if (source != -1) {
            sum = (twoByteRegisters[destination] + twoByteRegisters[source]);

            String firstOperandHex = Integer.toHexString(twoByteRegisters[destination]);
            String secondOperandHex = Integer.toHexString(twoByteRegisters[source]);
            af = (Integer.parseInt(firstOperandHex.substring(firstOperandHex.length()-1),16) + Integer.parseInt(secondOperandHex.substring(secondOperandHex.length()-1),16)) >= 16;
            sf = sum > 32767;
            of = ((twoByteRegisters[destination] < 32768 && twoByteRegisters[source] < 32768 && sf) || (twoByteRegisters[destination] >= 32768 && twoByteRegisters[source] >= 32768 && !sf));
            zf = sum == 0;
            cf = sum > 65535;
            if (!cf) twoByteRegisters[destination] = sum;
            else {
                String binaryFormat = Integer.toBinaryString(sum);
                twoByteRegisters[destination] = Integer.parseInt(binaryFormat.substring(1));
            }
        }//Memory to Register
        else if (secondOperand.length() > 1 && (secondOperand.charAt(1) == '[' || secondOperand.charAt(0) == '[') && secondOperand.endsWith("]")) {
            //If second operand points byte sized address, gives error.
            if (secondOperand.charAt(0) == 'b' || secondOperand.charAt(0) == 'B') {
                System.out.println("Incompatible data type at line " + pc); System.exit(0);
            }
            //If address is in valid address format, gets its value by calling method from helper class.
            String stringAddress = secondOperand.substring(secondOperand.indexOf("[") + 1, secondOperand.length() - 1);
            address = determineAddress(stringAddress);
            //If address location belongs to instructions, gives error.
            if (isAddress) {
                if(address < ip) {
                    System.out.println("Not permitted to access that address" + pc); System.exit(0);
                }

                String lowPart = Integer.toBinaryString(memory[address]);
                String stringOperand = Integer.toBinaryString(memory[address+1]).concat(("00000000" + lowPart).substring(lowPart.length()));
                operand = Integer.parseInt(stringOperand,2);

                sum = twoByteRegisters[destination] + operand;

                String firstOperandHex = Integer.toHexString(twoByteRegisters[destination]);
                String secondOperandHex = Integer.toHexString(operand);
                af = (Integer.parseInt(firstOperandHex.substring(firstOperandHex.length()-1),16) + Integer.parseInt(secondOperandHex.substring(secondOperandHex.length()-1),16)) >= 16;
                zf = sum == 0;
                sf = sum > 32767;
                of = ((twoByteRegisters[destination] < 32768 && operand < 32768 && sf) || (twoByteRegisters[destination] >= 32768 && operand >= 32768 && !sf));
                cf = sum > 65535;
                if (!cf) twoByteRegisters[destination] = sum;
                //If second operand starts with "[" and ends with "]" but it is not in the form of integer of valid register form, gives syntax error.
            } else {
                System.out.println("Syntax error at line " + pc); System.exit(0);
            }
        } else {
            //If second operand is integer format, converts it.
            operand = convertInteger(secondOperand);
            if (isNumber) {
                //HYP86 works with unsigned values.
                if (operand > 65535 || operand < 0) {
                    System.out.println("Unsigned word value required at line " + pc); System.exit(0);
                }
                sum = (twoByteRegisters[destination] + operand);

                String firstOperandHex = Integer.toHexString(twoByteRegisters[destination]);
                String secondOperandHex = Integer.toHexString(operand);
                af = (Integer.parseInt(firstOperandHex.substring(firstOperandHex.length()-1),16) + Integer.parseInt(secondOperandHex.substring(secondOperandHex.length()-1),16)) >= 16;
                zf = sum == 0;
                sf = sum > 32767;
                of = ((twoByteRegisters[destination] < 32768 && operand < 32768 && sf) || (twoByteRegisters[destination] >= 32768 && operand >= 32768 && !sf));
                cf = sum > 65535;
                if (!cf) twoByteRegisters[destination] = sum;
                //If sum is 17 bit, puts lower 16 bit into register.
                else {
                    String binaryFormat = Integer.toBinaryString(sum);
                    twoByteRegisters[destination] = Integer.parseInt(binaryFormat.substring(1));
                }
                //If second operand is character gets its ascii value and realizes addition.
            } else if (secondOperand.charAt(0) == '\"' && secondOperand.endsWith("\"") || (secondOperand.charAt(0) == '\'' && secondOperand.endsWith("'"))) { //If immediate is char
                //HYP86 does not work with strings
                if (secondOperand.length() > 3) {
                    System.out.println("Incompatible data type" + pc); System.exit(0);
                } else if (secondOperand.length() == 3) {
                    byte first = (byte) secondOperand.charAt(1);
                    sum = (twoByteRegisters[destination] + first);

                    String firstOperandHex = Integer.toHexString(twoByteRegisters[destination]);
                    String secondOperandHex = Integer.toHexString(first);
                    af = (Integer.parseInt(firstOperandHex.substring(firstOperandHex.length()-1),16) + Integer.parseInt(secondOperandHex.substring(secondOperandHex.length()-1),16)) >= 16;
                    zf = sum == 0;
                    sf = sum > 32767;
                    of = ((twoByteRegisters[destination] < 32768 && sf));
                    cf = sum > 65535;
                    if (!cf) twoByteRegisters[destination] = sum;
                        //If sum is 17 bit, puts lower 16 bit into register.
                    else {
                        String binaryFormat = Integer.toBinaryString(sum);
                        twoByteRegisters[destination] = Integer.parseInt(binaryFormat.substring(1));
                    }
                }
            } else {
                System.out.println("Syntax error at line " + pc); System.exit(0);
            }
        }
        //If destination register has child registers, updates them.
        if (hasSubReg) {
            updateChild(destination);
        }
    }
    //Adds second operand to one byte sized register, updates flags.
    public static void addToByteReg(String secondOperand, short destination) {
        int source, sum;
        //If second operand is word sized register, gives error.
        if (determineWordRegister(secondOperand) != -1) {
            System.out.println("Byte-Word combinations are not allowed at line " + pc); System.exit(0);
        }
        //If second operand is byte sized register, add its value to destination and puts sum's value into one byte destination register.
        source = determineByteRegister(secondOperand);
        if (source != -1) {
            sum = oneByteRegisters[destination] + oneByteRegisters[source];

            String firstOperandHex = Integer.toHexString(oneByteRegisters[destination]);
            String secondOperandHex = Integer.toHexString(oneByteRegisters[source]);
            af = (Integer.parseInt(firstOperandHex.substring(firstOperandHex.length()-1),16) + Integer.parseInt(secondOperandHex.substring(secondOperandHex.length()-1),16)) >= 16;
            zf = sum == 0;
            sf = sum > 127;
            of = ((oneByteRegisters[destination] < 128 && oneByteRegisters[source] < 128 && sf) || (oneByteRegisters[destination] >= 128 && oneByteRegisters[source] >= 128 && !sf));
            cf = sum > 255;
            if (!cf) oneByteRegisters[destination] = (short) sum;
            //If sum is 17 bit, puts lower 16 bit into register.
            else {
                String binaryFormat = Integer.toBinaryString(sum);
                oneByteRegisters[destination] = (short) Integer.parseInt(binaryFormat.substring(1));
            } //Memory to Register
        } else if (secondOperand.length() > 1 && (secondOperand.charAt(1) == '[' || secondOperand.charAt(0) == '[') && secondOperand.endsWith("]")) {
            //If second operand points word sized address, than throws error.
            if (secondOperand.charAt(0) == 'w' || secondOperand.charAt(0) == 'W') {//Incompatible data type. Both operands must be byte.
                System.out.println("Incompatible data type at line " + pc); System.exit(0);
            }
            String stringAddress;
            stringAddress = secondOperand.substring(secondOperand.indexOf("[") + 1, secondOperand.length() - 1);
            //If address is in valid address format, gets its value by calling method from helper class.
            int address = determineAddress(stringAddress);
            if (isAddress) {
                //If address location belongs to instructions, gives error.
                if(address < ip) {
                    System.out.println("Not permitted to access that address at line " + pc); System.exit(0);
                }
                int operand = memory[address];
                sum = (oneByteRegisters[destination] + operand);

                String firstOperandHex = Integer.toHexString(oneByteRegisters[destination]);
                String secondOperandHex = Integer.toHexString(operand);
                af = (Integer.parseInt(firstOperandHex.substring(firstOperandHex.length()-1),16) + Integer.parseInt(secondOperandHex.substring(secondOperandHex.length()-1),16)) >= 16;
                zf = sum == 0;
                sf = sum > 127;
                of = ((twoByteRegisters[destination] < 128 && operand < 128 && sf) || (twoByteRegisters[destination] >= 128 && operand >= 128 && !sf));
                cf = sum > 255;
                if (!cf) oneByteRegisters[destination] = (short) sum;
                //If sum is 17 bit, puts lower 16 bit into register.
                else {
                    String binaryFormat = Integer.toBinaryString(sum);
                    oneByteRegisters[destination] = (short) Integer.parseInt(binaryFormat.substring(1));
                }
                //If second operand starts with "[" and ends with "]" but it is not in the form of integer of valid register form, gives syntax error.
            } else {
                System.out.println("Syntax error at line " + pc); System.exit(0);
            }
        } else {
            //If second operand is integer format, converts it.
            int operand = convertInteger(secondOperand);
            if (isNumber) { //Immediate is a number
                if (operand > 255 || operand < 0) {
                    System.out.println("Byte-sized required " + pc); System.exit(0);
                }
                sum = oneByteRegisters[destination] + operand;

                String firstOperandHex = Integer.toHexString(oneByteRegisters[destination]);
                String secondOperandHex = Integer.toHexString(operand);
                af = (Integer.parseInt(firstOperandHex.substring(firstOperandHex.length()-1),16) + Integer.parseInt(secondOperandHex.substring(secondOperandHex.length()-1),16)) >= 16;
                zf = sum == 0;
                sf = sum > 127;
                of = ((oneByteRegisters[destination] < 128 && operand < 128 && sf) || (oneByteRegisters[destination] >= 128 && operand >= 128 && !sf));
                cf = sum > 255;
                if (!cf) oneByteRegisters[destination] += operand;
                    //If sum is 17 bit, puts lower 16 bit into register.
                else {
                    String binaryFormat = Integer.toBinaryString(oneByteRegisters[destination] + operand);
                    oneByteRegisters[destination] = (short) Integer.parseInt(binaryFormat.substring(1));
                }
                //If second operand is character gets its ascii value and realize addition.
            } else if (secondOperand.charAt(0) == '\"' && secondOperand.endsWith("\"") || (secondOperand.charAt(0) == '\'' && secondOperand.endsWith("'"))) { // Immediate is a char
                byte first;
                //HYP86 does not work with strings.
                if (secondOperand.length() > 3) {
                    System.out.println("Byte size is required at line " + pc); System.exit(0);
                } else if (secondOperand.length() == 3) {
                    first = (byte) secondOperand.charAt(1);
                    sum = oneByteRegisters[destination] + first;

                    String firstOperandHex = Integer.toHexString(oneByteRegisters[destination]);
                    String secondOperandHex = Integer.toHexString(first);
                    af = (Integer.parseInt(firstOperandHex.substring(firstOperandHex.length()-1),16) + Integer.parseInt(secondOperandHex.substring(secondOperandHex.length()-1),16)) >= 16;
                    zf = sum == 0;
                    sf = sum > 127;
                    of = (oneByteRegisters[destination] < 128 && sf);
                    cf = sum > 255;
                    if (!cf) oneByteRegisters[destination] = (short) sum;
                        //If sum is 17 bit, puts lower 16 bit into register.
                    else {
                        String binaryFormat = Integer.toBinaryString(sum);
                        oneByteRegisters[destination] = (short) Integer.parseInt(binaryFormat.substring(1));
                    }
                }
            } else {
                System.out.println("Syntax error at line " + pc); System.exit(0);
            }
        }
        //Updates parent registers of one byte sized register.
        updateParent(destination);
    }
    //Adds second operand to two byte sized memory location, updates flags.
    public static void addToWordMemory(String stringFirstOperand, String secondOperand, boolean hasPrefix) {
        int address, source;
        address = determineAddress(stringFirstOperand);
        //If first operand is not in valid address format, gives error.
        if(!isAddress) {
            System.out.println("Syntax error at line " + pc); System.exit(0);
        }
        //If address location belongs to instructions, gives error.
        if(address < ip) {
            System.out.println("Not permitted to access that address" + pc); System.exit(0);
        }
        //Calculates first operands value.
        String lowPart = Integer.toBinaryString(memory[address]);
        String stringOperand = Integer.toBinaryString(memory[address+1]).concat(("00000000" + lowPart).substring(lowPart.length()));
        int firstOperand = Integer.parseInt(stringOperand, 2);
        //If second operand is register, get register index.
        source = determineByteRegister(secondOperand);
        //If second operand is one byte sized register, checks whether first operand has w in front of it.
        if(source != -1) {
            //If it has w, gives error.
            if(hasPrefix) {
                System.out.println("Byte-word combinations are not allowed at line " + pc); System.exit(0);
                //Else, first operand's type is determined by second operand. Calls the add method for one byte sized memory locations.
            } else {
                addToByteMemory(stringFirstOperand, secondOperand); return;
            }
        }
        //If second operand is two byte register, realizes addition operation.
        source = determineWordRegister(secondOperand);
        if (source != -1) {
            int sum = firstOperand + twoByteRegisters[source];

            String firstOperandHex = Integer.toHexString(firstOperand);
            String secondOperandHex = Integer.toHexString(twoByteRegisters[source]);
            af = (Integer.parseInt(firstOperandHex.substring(firstOperandHex.length()-1),16) + Integer.parseInt(secondOperandHex.substring(secondOperandHex.length()-1),16)) >= 16;
            zf = sum == 0;
            sf = sum > 32767;
            of = ((twoByteRegisters[source] < 32768 && firstOperand < 32768 && cf ) || (twoByteRegisters[source] >= 32768 && firstOperand >= 32768 && !cf));
            cf = sum > 65535;
            //If sum is 17 bit, puts lower 16 bit into register.
            if (!cf) {
                if (sum > 255) {
                    String binaryFormat = Integer.toBinaryString(sum);
                    memory[address+1] = (short) Integer.parseInt(binaryFormat.substring(0, binaryFormat.length()-8), 2);
                    memory[address] = (short) Integer.parseInt(binaryFormat.substring(binaryFormat.length()-8), 2);
                } else {
                    memory[address] = (short) sum;
                }
            } else {
                String binaryFormat = Integer.toBinaryString(sum);
                memory[address+1] = (short) Integer.parseInt(binaryFormat.substring(1, 9), 2);
                memory[address] = (short) Integer.parseInt(binaryFormat.substring(9), 2);
            }
        } else {
            //If second operand is in number format, convert it into integer.
            int secondOperandValue = convertInteger(secondOperand);
            if (isNumber) {
                //If second operand's size is greater than two byte, gives error. Else realizes addition operation.
                if (secondOperandValue > 65535) {
                    System.out.println("Word-sized required at line " + pc); System.exit(0);
                } else {
                    int sum = firstOperand + secondOperandValue;

                    String firstOperandHex = Integer.toHexString(firstOperand);
                    String secondOperandHex = Integer.toHexString(secondOperandValue);
                    af = (Integer.parseInt(firstOperandHex.substring(firstOperandHex.length()-1),16) + Integer.parseInt(secondOperandHex.substring(secondOperandHex.length()-1),16)) >= 16;
                    zf = sum == 0;
                    sf = sum > 32767;
                    of = ((secondOperandValue < 32768 && firstOperand < 32768 && sf) || (secondOperandValue >= 32768 && firstOperand >= 32768 && !sf));
                    cf = sum > 65535;
                    if (!cf) {
                        if (sum > 255) {
                            String binaryFormat = Integer.toBinaryString(sum);
                            memory[address+1] = (short) Integer.parseInt(binaryFormat.substring(0, binaryFormat.length()-8), 2);
                            memory[address] = (short) Integer.parseInt(binaryFormat.substring(binaryFormat.length()-8), 2);
                        } else {
                            //If second operand's size is one byte and memory location's size is not specified, gives error.
                            if(!hasPrefix) {
                                System.out.println("Unknown data type at line " + pc); System.exit(0);
                            }
                            memory[address] = (short) sum;
                        }
                        //If sum is 17 bit, puts lower 16 bit into register.
                    }  else {
                        String binaryFormat = Integer.toBinaryString(sum);
                        memory[address+1] = (short) Integer.parseInt(binaryFormat.substring(1, 9), 2);
                        memory[address] = (short) Integer.parseInt(binaryFormat.substring(9), 2);
                    }
                }
            //If second operand is in character format
            } else if ((secondOperand.charAt(0) == '\"' && secondOperand.endsWith("\"")) || (secondOperand.charAt(0) == '\'' && secondOperand.endsWith("'"))) {
                //Hyp86 does not work with strings.
                if (secondOperand.length() > 3) {
                    System.out.println("Byte-sized required at line " + pc); System.exit(0);
                }
                //If first operand's size is not specified, gives error.
                if(!hasPrefix) {
                    System.out.println("Byte or Word? at line " + pc); System.exit(0);
                }

                if (secondOperand.length() == 3) {
                    secondOperandValue = (byte) secondOperand.charAt(1);
                    int sum = firstOperand + secondOperandValue;

                    String firstOperandHex = Integer.toHexString(firstOperand);
                    String secondOperandHex = Integer.toHexString(secondOperandValue);
                    af = (Integer.parseInt(firstOperandHex.substring(firstOperandHex.length()-1),16) + Integer.parseInt(secondOperandHex.substring(secondOperandHex.length()-1),16)) >= 16;
                    zf = sum == 0;
                    sf = sum > 32767;
                    of = (firstOperand < 32768 && sf);
                    cf = sum > 65535;
                    if (!cf) {
                        if (sum > 255) {
                            String binaryFormat = Integer.toBinaryString(sum);
                            memory[address+1] = (short) Integer.parseInt(binaryFormat.substring(0, binaryFormat.length()-8), 2);
                            memory[address] = (short) Integer.parseInt(binaryFormat.substring(binaryFormat.length()-8), 2);
                        } else {
                            memory[address] = (short) sum;
                        }
                        //If sum is 17 bit, puts lower 16 bit into register.
                    }  else {
                        String binaryFormat = Integer.toBinaryString(sum);
                        memory[address+1] = (short) Integer.parseInt(binaryFormat.substring(1, 9), 2);
                        memory[address] = (short) Integer.parseInt(binaryFormat.substring(9), 2);
                    }
                }
            } else {
                System.out.println("Syntax error at line " + pc); System.exit(0);
            }
        }
    }
    //Adds second operand to two byte sized memory location, updates flags.
    public static void addToByteMemory(String firstOperand, String secondOperand) {
        int address, source;
        address = determineAddress(firstOperand);
        //If first operand is not in valid address syntax, gives error.
        if(!isAddress) {
            System.out.println("Syntax error at line " + pc); System.exit(0);
        }
        //If address location belongs to instructions, gives error.
        if(address < ip) {
            System.out.println("Not permitted to access that address" + pc); System.exit(0);
        }
        //If second operand is two byte register, gives error. Else if it is two byte register, realizes addition operation.
        if (determineWordRegister(secondOperand) != -1) {
            System.out.println("Byte-word combinations are not allowed"); System.exit(0);
        }
        source = determineByteRegister(secondOperand);
        if (source != -1) {
            int sum = memory[address] + oneByteRegisters[source];

            String firstOperandHex = Integer.toHexString(memory[address]);
            String secondOperandHex = Integer.toHexString(oneByteRegisters[source]);
            af = (Integer.parseInt(firstOperandHex.substring(firstOperandHex.length()-1),16) + Integer.parseInt(secondOperandHex.substring(secondOperandHex.length()-1),16)) >= 16;
            zf = sum == 0;
            sf = sum > 127;
            of = ((memory[address] < 128 && oneByteRegisters[source] < 128 && sf) || (memory[address] >= 128 && oneByteRegisters[source] >= 128 && !sf));
            cf = sum > 255;
            if (!cf) memory[address] += oneByteRegisters[source];
            else {
                String binaryFormat = Integer.toBinaryString(memory[address] + oneByteRegisters[source]);
                memory[address] = (short) Integer.parseInt(binaryFormat.substring(1), 2);
            }
        } else {
            //If second operand is a number, converts it into integer. If it is greater than 1 byte gives an error.
            int secondOperandValue = convertInteger(secondOperand);
            if (isNumber) {
                if (secondOperandValue > 255) {
                    System.out.println("Byte-word combinations are not allowed at line " + pc); System.exit(0);
                } else {
                    int sum = memory[address] + secondOperandValue;

                    String firstOperandHex = Integer.toHexString(memory[address]);
                    String secondOperandHex = Integer.toHexString(secondOperandValue);
                    af = (Integer.parseInt(firstOperandHex.substring(firstOperandHex.length()-1),16) + Integer.parseInt(secondOperandHex.substring(secondOperandHex.length()-1),16)) >= 16;
                    zf = sum == 0;
                    sf = sum > 127;
                    of = ((memory[address] < 128 && secondOperandValue < 128 && sf) || (memory[address] >= 128 && secondOperandValue >= 128 && !sf));
                    cf = (sum > 255);
                    if (!cf) memory[address] = (short) sum;
                        //If sum is 17 bit, puts lower 16 bit into register.
                    else {
                        String binaryFormat = Integer.toBinaryString(sum);
                        memory[address] = (short) Integer.parseInt(binaryFormat.substring(1), 2);
                    }
                }
                //If second operand is character, gets its ascii value and realizes addition operation.
            } else if ((secondOperand.charAt(0) == '\"' && secondOperand.endsWith("\"")) || (secondOperand.charAt(0) == '\'' && secondOperand.endsWith("'"))) {
                //Hyp86 does not work with strings.
                if (secondOperand.length() > 3) {
                    System.out.println("Byte-sized required at line " + pc); System.exit(0);
                }
                else if (secondOperand.length() == 3) {
                    secondOperandValue = (byte) secondOperand.charAt(1);
                    int sum = memory[address] + secondOperandValue;

                    String firstOperandHex = Integer.toHexString(memory[address]);
                    String secondOperandHex = Integer.toHexString(secondOperandValue);
                    af = (Integer.parseInt(firstOperandHex.substring(firstOperandHex.length()-1),16) + Integer.parseInt(secondOperandHex.substring(secondOperandHex.length()-1),16)) >= 16;
                    zf = sum == 0;
                    sf = sum > 127;
                    of = (memory[address] < 128 && sf);
                    cf = sum > 255;
                    if (!cf) memory[address] = (short) sum;
                        //If sum is 17 bit, puts lower 16 bit into register.
                    else {
                        String binaryFormat = Integer.toBinaryString(sum);
                        memory[address] = (short) Integer.parseInt(binaryFormat.substring(1), 2);
                    }
                }
            } else {
                System.out.println("Syntax error at line " + pc); System.exit(0);
            }
        }
    }

    public static void addWordToSp(String secondOperand) {
        int address, operand, sum, source;
        //If second operand is byte sized register, gives error.
        if (determineByteRegister(secondOperand) != -1) {
            System.out.println("Byte-Word combinations are not allowed at line " + pc); System.exit(0);
        }
        source = determineWordRegister(secondOperand);
        //If second operand is word sized register, add its value to destination and puts sum's value into sp.
        if (source != -1) {
            sum = (sp + twoByteRegisters[source]);

            String firstOperandHex = Integer.toHexString(sp);
            String secondOperandHex = Integer.toHexString(twoByteRegisters[source]);
            af = (Integer.parseInt(firstOperandHex.substring(firstOperandHex.length()-1),16) + Integer.parseInt(secondOperandHex.substring(secondOperandHex.length()-1),16)) >= 16;
            sf = sum > 32767;
            of = ((sp < 32768 && twoByteRegisters[source] < 32768 && sf) || (sp >= 32768 && twoByteRegisters[source] >= 32768 && !sf));
            zf = sum == 0;
            cf = sum > 65535;
            if (!cf) sp = sum;
            else {
                String binaryFormat = Integer.toBinaryString(sum);
                sp = Integer.parseInt(binaryFormat.substring(1));
            }
        }//Memory to Register
        else if (secondOperand.length() > 1 && (secondOperand.charAt(1) == '[' || secondOperand.charAt(0) == '[') && secondOperand.endsWith("]")) {
            //If second operand points byte sized address, gives error.
            if (secondOperand.charAt(0) == 'b' || secondOperand.charAt(0) == 'B') {
                System.out.println("Incompatible data type at line " + pc); System.exit(0);
            }
            //If address is in valid address format, gets its value by calling method from helper class.
            String stringAddress = secondOperand.substring(secondOperand.indexOf("[") + 1, secondOperand.length() - 1);
            address = determineAddress(stringAddress);
            //If address location belongs to instructions, gives error.
            if (isAddress) {
                if(address < ip) {
                    System.out.println("Not permitted to access that address" + pc); System.exit(0);
                }

                String lowPart = Integer.toBinaryString(memory[address]);
                String stringOperand = Integer.toBinaryString(memory[address+1]).concat(("00000000" + lowPart).substring(lowPart.length()));
                operand = Integer.parseInt(stringOperand,2);

                sum = sp + operand;

                String firstOperandHex = Integer.toHexString(sp);
                String secondOperandHex = Integer.toHexString(operand);
                af = (Integer.parseInt(firstOperandHex.substring(firstOperandHex.length()-1),16) + Integer.parseInt(secondOperandHex.substring(secondOperandHex.length()-1),16)) >= 16;
                zf = sum == 0;
                sf = sum > 32767;
                of = ((sp < 32768 && operand < 32768 && sf) || (sp >= 32768 && operand >= 32768 && !sf));
                cf = sum > 65535;
                if (!cf) sp = sum;
                //If second operand starts with "[" and ends with "]" but it is not in the form of integer of valid register form, gives syntax error.
            } else {
                System.out.println("Syntax error at line " + pc); System.exit(0);
            }
        } else {
            //If second operand is integer format, converts it.
            operand = convertInteger(secondOperand);
            if (isNumber) {
                //HYP86 works with unsigned values.
                if (operand > 65535 || operand < 0) {
                    System.out.println("Unsigned word value required at line " + pc); System.exit(0);
                }
                sum = (sp + operand);

                String firstOperandHex = Integer.toHexString(sp);
                String secondOperandHex = Integer.toHexString(operand);
                af = (Integer.parseInt(firstOperandHex.substring(firstOperandHex.length()-1),16) + Integer.parseInt(secondOperandHex.substring(secondOperandHex.length()-1),16)) >= 16;
                zf = sum == 0;
                sf = sum > 32767;
                of = ((sp < 32768 && operand < 32768 && sf) || (sp >= 32768 && operand >= 32768 && !sf));
                cf = sum > 65535;
                if (!cf) sp = sum;
                    //If sum is 17 bit, puts lower 16 bit into register.
                else {
                    String binaryFormat = Integer.toBinaryString(sum);
                    sp = Integer.parseInt(binaryFormat.substring(1));
                }
                //If second operand is character gets its ascii value and realizes addition.
            } else if (secondOperand.charAt(0) == '\"' && secondOperand.endsWith("\"") || (secondOperand.charAt(0) == '\'' && secondOperand.endsWith("'"))) { //If immediate is char
                //HYP86 does not work with strings
                if (secondOperand.length() > 3) {
                    System.out.println("Incompatible data type" + pc); System.exit(0);
                } else if (secondOperand.length() == 3) {
                    byte first = (byte) secondOperand.charAt(1);
                    sum = (sp + first);

                    String firstOperandHex = Integer.toHexString(sp);
                    String secondOperandHex = Integer.toHexString(first);
                    af = (Integer.parseInt(firstOperandHex.substring(firstOperandHex.length()-1),16) + Integer.parseInt(secondOperandHex.substring(secondOperandHex.length()-1),16)) >= 16;
                    zf = sum == 0;
                    sf = sum > 32767;
                    of = ((sp < 32768 && sf));
                    cf = sum > 65535;
                    if (!cf) sp = sum;
                        //If sum is 17 bit, puts lower 16 bit into register.
                    else {
                        String binaryFormat = Integer.toBinaryString(sum);
                        sp = Integer.parseInt(binaryFormat.substring(1));
                    }
                }
            } else {
                System.out.println("Syntax error at line " + pc); System.exit(0);
            }
        }
    }
}
