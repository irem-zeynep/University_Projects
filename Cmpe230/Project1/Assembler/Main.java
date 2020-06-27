package Cmpe230;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    //Reads from console
    static Scanner console = new Scanner(System.in);
    //Size of the memory
    public static int size = 2 << 15;
    public static short[] memory = new short[size];
    //Holds two byte registers' values. Since java has no unsigned data type it is integer array.
    public static int[] twoByteRegisters = new int[7];
    //Holds one byte registers' values. Since java has no unsigned data type it is short array.
    public static short[] oneByteRegisters = new short[8];
    static int pc = 0; //Program counter
    static int  sp =  (2 << 15 ) - 2; //Stack pointer
    static int ip = 0; //Instruction pointer: Holds the memory address of last available position after instructions

    // TWO BYTE REGISTERS' INDEXES
    final static int ax = 0;
    final static int bx = 1;
    final static int cx = 2;
    final static int dx = 3;
    final static int di = 4;
    final static int si = 5;
    final static int bp = 6; //Base pointer

    //ONE BYTE REGISTERS' INDEXES
    final static short ah = 0;
    final static short al = 1;
    final static short bh = 2;
    final static short bl = 3;
    final static short ch = 4;
    final static short cl = 5;
    final static short dh = 6;
    final static short dl = 7;

    //FLAGS
    static boolean zf = false;  // zero flag
    static boolean sf = false;  // sign flag
    static boolean cf = false;  // carry flag
    static boolean af = false;  // auxiliary flag
    static boolean of = false;  // overflow flag
    //Assigns as true when "code segment" is read from file.
    static boolean start = false;
    //Assigns as true when "code ends" is read from file.
    static boolean end = false;

    //Holds line numbers of labels.
    static Map<String, Integer> labelNo = new HashMap<>();
    //Holds address of variables.
    static Map<String, Integer> wordVariables = new HashMap<>();
    static Map<String, Integer> byteVariables = new HashMap<>();
    //Holds the command lines to do instructions after first reading.
    static ArrayList<String> commands = new ArrayList<>();
    static String operation, firstOperand, secondOperand, prefix;
    static String[] operands;
    //Line number of "code segment" on the file
    static int lineNumberOfCodeSegment;

    public static void main(String[] args) throws FileNotFoundException {
        try {
            //Reads file as argument
            Scanner input = new Scanner(new File(args[0]));
            while (input.hasNextLine()) {
                pc++;
                String command = input.nextLine();
                //If still in code segment part
                if (start && !end) {
                    commands.add(command);
                }
                if(command.trim().isEmpty())
                    continue;
                /* Split according to spaces. First put spaces front and end of the ",", ";". Aim is separating them from operands int he operands array.
                Second put space after the "]", and put space before the "[" to create [memory address] format and separate it from the other tokens.
                 */
                operands = command.trim().replace(",", " , ")
                        .replace(";", " ; ").replace(":", " :")
                        .replace("]", " ] ").replaceAll(" ++]+ +", "] ")
                        .replace("[", " [ ").replaceAll(" ++\\[+ +", " [")
                        .replaceAll(" +", " ").split(" ");
                operation = operands[0];
                //Check every line until "code segment" is read.
                if (!start) {
                    start = (operands[0].equalsIgnoreCase("code") && operands[1].equalsIgnoreCase("segment"));
                    lineNumberOfCodeSegment = pc;
                }
                //Check every line until "code ends" is read.
                if (!end) {
                    end = (operands[0].equalsIgnoreCase("code") && operands[1].equalsIgnoreCase("ends"));
                }
                if (end)
                    break;
                //In A86 giving labels operands names are not allowed. If this is the situation gives error. Else put label and line number into map.
                if (start && operands.length > 1 && operands[1].equals(":")) {
                    if (operands[0].equalsIgnoreCase("label") || operands[0].equalsIgnoreCase("MOV") || operands[0].equalsIgnoreCase("ADD")
                            || operands[0].equalsIgnoreCase("SUB") || operands[0].equalsIgnoreCase("MUL")
                            || operands[0].equalsIgnoreCase("DIV") || operands[0].equalsIgnoreCase("XOR")
                            || operands[0].equalsIgnoreCase("AND") || operands[0].equalsIgnoreCase("OR")
                            || operands[0].equalsIgnoreCase("NOT") || operands[0].equalsIgnoreCase("PUSH")
                            || operands[0].equalsIgnoreCase("POP") || operands[0].equalsIgnoreCase("NOP")
                            || operands[0].equalsIgnoreCase("RCR") || operands[0].equalsIgnoreCase("RCL")
                            || operands[0].equalsIgnoreCase("SHR") || operands[0].equalsIgnoreCase("SHL")
                            || operands[0].equalsIgnoreCase("CMP") || operands[0].equalsIgnoreCase("INT 20H")
                            || operands[0].equalsIgnoreCase("INC") || operands[0].equalsIgnoreCase("DEC")
                            || operands[0].equalsIgnoreCase("INT 21H") || operands[0].equalsIgnoreCase("JA")
                            || operands[0].equalsIgnoreCase("JZ") || operands[0].equalsIgnoreCase("JNZ")
                            || operands[0].equalsIgnoreCase("JE") || operands[0].equalsIgnoreCase("JNE")
                            || operands[0].equalsIgnoreCase("JAE") || operands[0].equalsIgnoreCase("JNAE")
                            || operands[0].equalsIgnoreCase("JB") || operands[0].equalsIgnoreCase("JNB")
                            || operands[0].equalsIgnoreCase("JBE") || operands[0].equalsIgnoreCase("JNBE")
                            || operands[0].equalsIgnoreCase("JC") || operands[0].equalsIgnoreCase("JNC")) {
                        System.out.println("Misplaced Built-In Symbol at line " + pc);
                        System.exit(0);
                    }
                    labelNo.put(operation, pc);
                    //If first token is instruction then raise instruction pointer by 6.
                } else if (start && (operation.equalsIgnoreCase("MOV") || operation.equalsIgnoreCase("ADD")
                        || operation.equalsIgnoreCase("SUB") || operation.equalsIgnoreCase("MUL")
                        || operation.equalsIgnoreCase("DIV") || operation.equalsIgnoreCase("XOR")
                        || operation.equalsIgnoreCase("AND") || operation.equalsIgnoreCase("OR")
                        || operation.equalsIgnoreCase("NOT") || operation.equalsIgnoreCase("PUSH")
                        || operation.equalsIgnoreCase("POP") || operation.equalsIgnoreCase("NOP")
                        || operation.equalsIgnoreCase("RCR") || operation.equalsIgnoreCase("RCL")
                        || operation.equalsIgnoreCase("SHR") || operation.equalsIgnoreCase("SHL")
                        || operation.equalsIgnoreCase("CMP") || operation.equalsIgnoreCase("INC")
                        || operation.equalsIgnoreCase("DEC") || operation.equalsIgnoreCase("INT")
                        || operation.equalsIgnoreCase("JA") || operation.equalsIgnoreCase("JZ")
                        || operation.equalsIgnoreCase("JNZ") || operation.equalsIgnoreCase("JE")
                        || operation.equalsIgnoreCase("JNE") || operation.equalsIgnoreCase("JAE")
                        || operation.equalsIgnoreCase("JNAE") || operation.equalsIgnoreCase("JB")
                        || operation.equalsIgnoreCase("JNB") || operation.equalsIgnoreCase("JBE")
                        || operation.equalsIgnoreCase("JNBE") || operation.equalsIgnoreCase("JC")
                        || operation.equalsIgnoreCase("JNC"))) {
                    twoByteRegisters[bp] += 6;
                    ip += 6;
                /* If second operand is db or dw checks the first operand. In A86 giving variables operands names are not allowed.
                   If this is the situation gives error. Else put variable name and memory address of variable into the one of the maps.
                 */
                } else if ((start && (operands[1].equalsIgnoreCase("db") || operands[1].equalsIgnoreCase("dw"))) &&
                        (operands[0].equalsIgnoreCase("MOV") || operands[0].equalsIgnoreCase("ADD")
                                || operands[0].equalsIgnoreCase("SUB") || operands[0].equalsIgnoreCase("MUL")
                                || operands[0].equalsIgnoreCase("DIV") || operands[0].equalsIgnoreCase("XOR")
                                || operands[0].equalsIgnoreCase("AND") || operands[0].equalsIgnoreCase("OR")
                                || operands[0].equalsIgnoreCase("NOT") || operands[0].equalsIgnoreCase("PUSH")
                                || operands[0].equalsIgnoreCase("POP") || operands[0].equalsIgnoreCase("NOP")
                                || operands[0].equalsIgnoreCase("RCR") || operands[0].equalsIgnoreCase("RCL")
                                || operands[0].equalsIgnoreCase("SHR") || operands[0].equalsIgnoreCase("SHL")
                                || operands[0].equalsIgnoreCase("CMP") || operands[0].equalsIgnoreCase("INT20H")
                                || operands[0].equalsIgnoreCase("INC") || operands[0].equalsIgnoreCase("DEC")
                                || operands[0].equalsIgnoreCase("INT21H") || operands[0].equalsIgnoreCase("JA")
                                || operands[0].equalsIgnoreCase("JZ") || operands[0].equalsIgnoreCase("JNZ")
                                || operands[0].equalsIgnoreCase("JE") || operands[0].equalsIgnoreCase("JNE")
                                || operands[0].equalsIgnoreCase("JAE") || operands[0].equalsIgnoreCase("JNAE")
                                || operands[0].equalsIgnoreCase("JB") || operands[0].equalsIgnoreCase("JNB")
                                || operands[0].equalsIgnoreCase("JBE") || operands[0].equalsIgnoreCase("JNBE")
                                || operands[0].equalsIgnoreCase("JC") || operands[0].equalsIgnoreCase("JNC"))) {
                    System.out.println("Misplaced Built-In Symbol at line " + pc);
                    System.exit(0);
                } else if (start && operands[1].equalsIgnoreCase("db")) {
                    byteVariables.put(operation, twoByteRegisters[bp]);
                    DefineVariable.defineByte(operands[2]);

                    twoByteRegisters[bp] += 1;
                } else if (start && operands[1].equalsIgnoreCase("dw")) {
                    wordVariables.put(operation, twoByteRegisters[bp]);
                    DefineVariable.defineWord(operands[2]);
                    twoByteRegisters[bp] += 2;
                } else {
                    if (start && !(operands[0].equalsIgnoreCase("code") && operands[1].equalsIgnoreCase("segment"))) {
                        System.out.println("Syntax error at line " + pc);
                        System.exit(0);
                    }
                }
            }
            //Reset for the second reading
            pc = lineNumberOfCodeSegment;
            //In first reading used as pointer which shows the next available memory cell. In the second reading it is a register as it should be.
            twoByteRegisters[bp] = 0;


            for (int lineNumber = 0; lineNumber < commands.size(); lineNumber++) {
                String command = commands.get(lineNumber);
                pc++;
                if(command.trim().isEmpty())
                    continue;
                /* The prevent problems may occur while splitting the command line, replaces ".*" with "g" which does not cause any problem.
                After the splitting replace back. The situation is same for the '.*'.
                 */
                String firstCharacter = "";
                String secondCharacter = "";
                int firstStartIndex = command.indexOf("\"");
                int secondStartIndex = command.indexOf("'");
                //Replaces.
                if (firstStartIndex != -1) {
                    if (!command.substring(firstStartIndex, firstStartIndex + 2).matches("\"\"") && command.substring(firstStartIndex, firstStartIndex + 3).matches("\".*\"")) {
                        firstCharacter = command.substring(firstStartIndex, firstStartIndex + 3);
                        command = command.replace(command.substring(secondStartIndex, secondStartIndex + 3), "g");
                    } else {
                        System.out.println("Missing quotes at line " + pc);
                    }
                }

                if (secondStartIndex != -1) {
                    if (!command.substring(secondStartIndex, secondStartIndex + 2).matches("''") && command.substring(secondStartIndex, secondStartIndex + 3).matches("'.*'")) {
                        secondCharacter = command.substring(secondStartIndex, secondStartIndex + 3);
                        command = command.replace(command.substring(secondStartIndex, secondStartIndex + 3), "'g'");
                    } else {
                        System.out.println("Missing quotes at line " + pc);
                    }
                }
                //Splits command line into operand array.
                operands = command.trim().replace(",", " , ")
                        .replace(";", " ; ").replace(":", " :")
                        .replace("]", " ] ").replaceAll(" ++]+ +", "] ")
                        .replace("[", " [ ").replaceAll(" ++\\[+ +", " [")
                        .replaceAll(" +", " ").split(" ");
                //Replaces back.
                for (int index = 0; index < operands.length; index++) {
                    if (operands[index].equals("\"g\"")) {
                        operands[index] = firstCharacter;
                    }
                    if (operands[index].equals("'g'")) {
                        operands[index] = secondCharacter;
                    }
                }
                /*Takes first operand to determine instruction's type. A86 is not case sensitive. So uses equalsIgnoreCase().
                checkTwoOperands and checkOneOperand methods are use the determine operands of instructions.
                 */
                operation = operands[0];

                if (operation.equalsIgnoreCase("MOV")) {
                    checkVariableTwoOperand();

                    if (firstOperand.equalsIgnoreCase("ax")) {
                        Move.moveWordToReg(secondOperand, ax, true);
                    } else if (firstOperand.equalsIgnoreCase("bx")) {
                        Move.moveWordToReg(secondOperand, bx, true);
                    } else if (firstOperand.equalsIgnoreCase("cx")) {
                        Move.moveWordToReg(secondOperand, cx, true);
                    } else if (firstOperand.equalsIgnoreCase("dx")) {
                        Move.moveWordToReg(secondOperand, dx, true);
                    } else if (firstOperand.equalsIgnoreCase("SI") || firstOperand.equalsIgnoreCase("si")) {
                        Move.moveWordToReg(secondOperand, si, false);
                    } else if (firstOperand.equalsIgnoreCase("DI") || firstOperand.equalsIgnoreCase("di")) {
                        Move.moveWordToReg(secondOperand, di, false);
                    } else if (firstOperand.equalsIgnoreCase("bp")) {
                        Move.moveWordToReg(secondOperand, bp, false);
                    } else if (firstOperand.equalsIgnoreCase("sp")) {
                        Move.moveWordToSp(secondOperand);
                    } else if (firstOperand.equalsIgnoreCase("ah")) {
                        Move.moveByteToReg(secondOperand, ah);
                    } else if (firstOperand.equalsIgnoreCase("al")) {
                        Move.moveByteToReg(secondOperand, al);
                    } else if (firstOperand.equalsIgnoreCase("bh")) {
                        Move.moveByteToReg(secondOperand, bh);
                    } else if (firstOperand.equalsIgnoreCase("bl")) {
                        Move.moveByteToReg(secondOperand, bl);
                    } else if (firstOperand.equalsIgnoreCase("ch")) {
                        Move.moveByteToReg(secondOperand, ch);
                    } else if (firstOperand.equalsIgnoreCase("cl")) {
                        Move.moveByteToReg(secondOperand, cl);
                    } else if (firstOperand.equalsIgnoreCase("dh")) {
                        Move.moveByteToReg(secondOperand, dh);
                    } else if (firstOperand.equalsIgnoreCase("dl")) {
                        Move.moveByteToReg(secondOperand, dl);
                    } else if (firstOperand.charAt(0) == 'W' || firstOperand.charAt(0) == 'w') {
                        Move.moveWordToMemory(firstOperand.substring(2, firstOperand.length() - 1), secondOperand, true);
                    } else if (firstOperand.charAt(0) == 'B' || firstOperand.charAt(0) == 'b') {
                        Move.moveByteToMemory(firstOperand.substring(2, firstOperand.length() - 1), secondOperand);
                    } else if (firstOperand.charAt(0) == '[' && firstOperand.endsWith("]")) {
                        Move.moveWordToMemory(firstOperand.substring(1, firstOperand.length() - 1), secondOperand, false);
                    }

                } else if (operation.equalsIgnoreCase("ADD") || operation.equalsIgnoreCase("INC") || operation.equalsIgnoreCase("İNC")) {
                    boolean tmp = false;
                    if (operation.equalsIgnoreCase("ADD"))
                        checkVariableTwoOperand();
                    else {
                        //If operation type is increment and type of address is not specified, gives error.
                        if (firstOperand.charAt(0) == '[' && firstOperand.endsWith("]")) {
                            System.out.println("Byte or Word? at line " + pc);
                            System.exit(0);
                        }
                        //Else determine the operands and store value of carry flag as tmp to change it back.
                        checkVariableOneOperand();
                        tmp = cf;
                        secondOperand = "1";
                    }

                    if (firstOperand.equalsIgnoreCase("ax")) {
                        Add.addToWordReg(secondOperand, ax, true);
                    } else if (firstOperand.equalsIgnoreCase("bx")) {
                        Add.addToWordReg(secondOperand, bx, true);
                    } else if (firstOperand.equalsIgnoreCase("cx")) {
                        Add.addToWordReg(secondOperand, cx, true);
                    } else if (firstOperand.equalsIgnoreCase("dx")) {
                        Add.addToWordReg(secondOperand, dx, true);
                    } else if (firstOperand.equalsIgnoreCase("SI") || firstOperand.equalsIgnoreCase("si")) {
                        Add.addToWordReg(secondOperand, si, false);
                    } else if (firstOperand.equalsIgnoreCase("DI") || firstOperand.equalsIgnoreCase("di")) {
                        Add.addToWordReg(secondOperand, di, false);
                    } else if (firstOperand.equalsIgnoreCase("bp")) {
                        Add.addToWordReg(secondOperand, bp, false);
                    } else if (firstOperand.equalsIgnoreCase("sp")) {
                        Add.addWordToSp(secondOperand);
                    } else if (firstOperand.equalsIgnoreCase("ah")) {
                        Add.addToByteReg(secondOperand, ah);
                    } else if (firstOperand.equalsIgnoreCase("al")) {
                        Add.addToByteReg(secondOperand, al);
                    } else if (firstOperand.equalsIgnoreCase("bh")) {
                        Add.addToByteReg(secondOperand, bh);
                    } else if (firstOperand.equalsIgnoreCase("bl")) {
                        Add.addToByteReg(secondOperand, bl);
                    } else if (firstOperand.equalsIgnoreCase("ch")) {
                        Add.addToByteReg(secondOperand, ch);
                    } else if (firstOperand.equalsIgnoreCase("cl")) {
                        Add.addToByteReg(secondOperand, cl);
                    } else if (firstOperand.equalsIgnoreCase("dh")) {
                        Add.addToByteReg(secondOperand, dh);
                    } else if (firstOperand.equalsIgnoreCase("dl")) {
                        Add.addToByteReg(secondOperand, dl);
                    } else if (firstOperand.charAt(0) == 'W' || firstOperand.charAt(0) == 'w') {
                        Add.addToWordMemory(firstOperand.substring(2, firstOperand.length() - 1), secondOperand, true);
                    } else if (firstOperand.charAt(0) == 'B' || firstOperand.charAt(0) == 'b') {
                        Add.addToByteMemory(firstOperand.substring(2, firstOperand.length() - 1), secondOperand);
                    } else if (firstOperand.charAt(0) == '[' && firstOperand.endsWith("]")) {
                        Add.addToWordMemory(firstOperand.substring(1, firstOperand.length() - 1), secondOperand, false);
                    }
                    if (!operation.equalsIgnoreCase("ADD"))
                        cf = tmp;

                } else if (operation.equalsIgnoreCase("SUB") || operation.equalsIgnoreCase("DEC")) {
                    boolean tmp = false;
                    if (operation.equalsIgnoreCase("SUB"))
                        checkVariableTwoOperand();
                    else {
                        //If operation is decrement and type of address is not specified, gives error.
                        if (firstOperand.charAt(0) == '[' && firstOperand.endsWith("]")) {
                            System.out.println("Byte or Word? at line " + pc);
                            System.exit(0);
                        }
                        //Else determine the operands and store value of carry flag as tmp to change it back.
                        checkVariableOneOperand();
                        tmp = cf;
                        secondOperand = "1";
                    }

                    if (firstOperand.equalsIgnoreCase("ax")) {
                        Subtract.subtractWordToReg(secondOperand, ax, true);
                    } else if (firstOperand.equalsIgnoreCase("bx")) {
                        Subtract.subtractWordToReg(secondOperand, bx, true);
                    } else if (firstOperand.equalsIgnoreCase("cx")) {
                        Subtract.subtractWordToReg(secondOperand, cx, true);
                    } else if (firstOperand.equalsIgnoreCase("dx")) {
                        Subtract.subtractWordToReg(secondOperand, dx, true);
                    } else if (firstOperand.equalsIgnoreCase("SI") || firstOperand.equalsIgnoreCase("si")) {
                        Subtract.subtractWordToReg(secondOperand, si, false);
                    } else if (firstOperand.equalsIgnoreCase("DI") || firstOperand.equalsIgnoreCase("di")) {
                        Subtract.subtractWordToReg(secondOperand, di, false);
                    } else if (firstOperand.equalsIgnoreCase("bp")) {
                        Subtract.subtractWordToReg(secondOperand, bp, false);
                    } else if (firstOperand.equalsIgnoreCase("sp")) {
                        Subtract.subtractWordFromSp(secondOperand);
                    } else if (firstOperand.equalsIgnoreCase("ah")) {
                        Subtract.subtractByteToReg(secondOperand, ah);
                    } else if (firstOperand.equalsIgnoreCase("al")) {
                        Subtract.subtractByteToReg(secondOperand, al);
                    } else if (firstOperand.equalsIgnoreCase("bh")) {
                        Subtract.subtractByteToReg(secondOperand, bh);
                    } else if (firstOperand.equalsIgnoreCase("bl")) {
                        Subtract.subtractByteToReg(secondOperand, bl);
                    } else if (firstOperand.equalsIgnoreCase("ch")) {
                        Subtract.subtractByteToReg(secondOperand, ch);
                    } else if (firstOperand.equalsIgnoreCase("cl")) {
                        Subtract.subtractByteToReg(secondOperand, cl);
                    } else if (firstOperand.equalsIgnoreCase("dh")) {
                        Subtract.subtractByteToReg(secondOperand, dh);
                    } else if (firstOperand.equalsIgnoreCase("dl")) {
                        Subtract.subtractByteToReg(secondOperand, dl);
                    } else if (firstOperand.charAt(0) == 'W' || firstOperand.charAt(0) == 'w') {
                        Subtract.subtractWordToMemory(firstOperand.substring(2, firstOperand.length() - 1), secondOperand, true);
                    } else if (firstOperand.charAt(0) == 'B' || firstOperand.charAt(0) == 'b') {
                        Subtract.subtractByteToMemory(firstOperand.substring(2, firstOperand.length() - 1), secondOperand);
                    } else if (firstOperand.charAt(0) == '[' && firstOperand.endsWith("]")) {
                        Subtract.subtractWordToMemory(firstOperand.substring(1, firstOperand.length() - 1), secondOperand, false);
                    }
                    if (operation.equalsIgnoreCase("DEC"))
                        cf = tmp;

                } else if (operation.equalsIgnoreCase("MUL")) {
                    checkVariableOneOperand();

                    if (firstOperand.equalsIgnoreCase("ax")) {
                        Multiply.multiplyByWordReg(ax);
                    } else if (firstOperand.equalsIgnoreCase("bx")) {
                        Multiply.multiplyByWordReg(bx);
                    } else if (firstOperand.equalsIgnoreCase("cx")) {
                        Multiply.multiplyByWordReg(cx);
                    } else if (firstOperand.equalsIgnoreCase("dx")) {
                        Multiply.multiplyByWordReg(dx);
                    } else if (firstOperand.equalsIgnoreCase("SI") || firstOperand.equalsIgnoreCase("si")) {
                        Multiply.multiplyByWordReg(si);
                    } else if (firstOperand.equalsIgnoreCase("DI") || firstOperand.equalsIgnoreCase("di")) {
                        Multiply.multiplyByWordReg(di);
                    } else if (firstOperand.equalsIgnoreCase("bp")) {
                        Multiply.multiplyByWordReg(bp);
                    } else if (firstOperand.equalsIgnoreCase("ah")) {
                        Multiply.multiplyByByteReg(ah);
                    } else if (firstOperand.equalsIgnoreCase("al")) {
                        Multiply.multiplyByByteReg(al);
                    } else if (firstOperand.equalsIgnoreCase("bh")) {
                        Multiply.multiplyByByteReg(bh);
                    } else if (firstOperand.equalsIgnoreCase("bl")) {
                        Multiply.multiplyByByteReg(bl);
                    } else if (firstOperand.equalsIgnoreCase("ch")) {
                        Multiply.multiplyByByteReg(ch);
                    } else if (firstOperand.equalsIgnoreCase("cl")) {
                        Multiply.multiplyByByteReg(cl);
                    } else if (firstOperand.equalsIgnoreCase("dh")) {
                        Multiply.multiplyByByteReg(dh);
                    } else if (firstOperand.equalsIgnoreCase("dl")) {
                        Multiply.multiplyByByteReg(dl);
                        //If type of address is not specified, gives error.
                    } else if (firstOperand.charAt(0) == '[' && firstOperand.endsWith("]")) {
                        System.out.println("Byte or Word? at line " + pc);
                        System.exit(0);
                    } else if (firstOperand.charAt(0) == 'W' || firstOperand.charAt(0) == 'w') {
                        Multiply.multiplyByWordMemory(firstOperand.substring(2, firstOperand.length() - 1));
                    } else if (firstOperand.charAt(0) == 'B' || firstOperand.charAt(0) == 'b') {
                        Multiply.multiplyByByteMemory(firstOperand.substring(2, firstOperand.length() - 1));
                    } else if (firstOperand.equalsIgnoreCase("sp")) {
                        Multiply.multiplyByImmediate(firstOperand);
                    } else {
                        System.out.println("Bad single operand at line " + pc);
                        System.exit(0);
                    }

                } else if (operation.equalsIgnoreCase("DIV") || operation.equalsIgnoreCase("div")) {
                    checkVariableOneOperand();

                    if (firstOperand.equalsIgnoreCase("ax")) {
                        Divide.divideByWordReg(ax);
                    } else if (firstOperand.equalsIgnoreCase("bx")) {
                        Divide.divideByWordReg(bx);
                    } else if (firstOperand.equalsIgnoreCase("cx")) {
                        Divide.divideByWordReg(cx);
                    } else if (firstOperand.equalsIgnoreCase("dx")) {
                        Divide.divideByWordReg(dx);
                    } else if (firstOperand.equalsIgnoreCase("SI") || firstOperand.equalsIgnoreCase("si")) {
                        Divide.divideByWordReg(si);
                    } else if (firstOperand.equalsIgnoreCase("DI") || firstOperand.equalsIgnoreCase("di")) {
                        Divide.divideByWordReg(di);
                    } else if (firstOperand.equalsIgnoreCase("bp")) {
                        Divide.divideByWordReg(bp);
                    } else if (firstOperand.equalsIgnoreCase("ah")) {
                        Divide.divideByByteReg(ah);
                    } else if (firstOperand.equalsIgnoreCase("al")) {
                        Divide.divideByByteReg(al);
                    } else if (firstOperand.equalsIgnoreCase("bh")) {
                        Divide.divideByByteReg(bh);
                    } else if (firstOperand.equalsIgnoreCase("bl")) {
                        Divide.divideByByteReg(bl);
                    } else if (firstOperand.equalsIgnoreCase("ch")) {
                        Divide.divideByByteReg(ch);
                    } else if (firstOperand.equalsIgnoreCase("cl")) {
                        Divide.divideByByteReg(cl);
                    } else if (firstOperand.equalsIgnoreCase("dh")) {
                        Divide.divideByByteReg(dh);
                    } else if (firstOperand.equalsIgnoreCase("dl")) {
                        Divide.divideByByteReg(dl);
                        //If type of address is not specified, gives error.
                    } else if (firstOperand.charAt(0) == '[' && firstOperand.endsWith("]")) {
                        System.out.println("Byte or Word? at line " + pc);
                        System.exit(0);
                    } else if (firstOperand.charAt(0) == 'W' || firstOperand.charAt(0) == 'w') {
                        Divide.divideByWordMemory(firstOperand.substring(2, firstOperand.length() - 1));
                    } else if (firstOperand.charAt(0) == 'B' || firstOperand.charAt(0) == 'b') {
                        Divide.divideByByteMemory(firstOperand.substring(2, firstOperand.length() - 1));
                    } else if (firstOperand.equalsIgnoreCase("sp")) {
                        Divide.divideByImmediate(firstOperand);
                    } else {
                        System.out.println("Bad single operand at line " + pc);
                        System.exit(0);
                    }

                } else if (operation.equalsIgnoreCase("XOR") || operation.equalsIgnoreCase("OR")
                        || operation.equalsIgnoreCase("AND")) {

                    checkVariableTwoOperand();
                    //xor, and, or instructions are done by same methods. Gives numbers according to instruction type to distinguish them in methods.
                    int type = 0;
                    if (operation.equalsIgnoreCase("OR"))
                        type = 1;
                    else if (operation.equalsIgnoreCase("AND"))
                        type = 2;

                    if (firstOperand.equalsIgnoreCase("ax")) {
                        BooleanOperation.wordToReg(secondOperand, ax, true, type);
                    } else if (firstOperand.equalsIgnoreCase("bx")) {
                        BooleanOperation.wordToReg(secondOperand, bx, true, type);
                    } else if (firstOperand.equalsIgnoreCase("cx")) {
                        BooleanOperation.wordToReg(secondOperand, cx, true, type);
                    } else if (firstOperand.equalsIgnoreCase("dx")) {
                        BooleanOperation.wordToReg(secondOperand, dx, true, type);
                    } else if (firstOperand.equalsIgnoreCase("sý") || firstOperand.equalsIgnoreCase("si")) {
                        BooleanOperation.wordToReg(secondOperand, si, false, type);
                    } else if (firstOperand.equalsIgnoreCase("dý") || firstOperand.equalsIgnoreCase("di")) {
                        BooleanOperation.wordToReg(secondOperand, di, false, type);
                    } else if (firstOperand.equalsIgnoreCase("bp")) {
                        BooleanOperation.wordToReg(secondOperand, bp, false, type);
                    } else if (firstOperand.equalsIgnoreCase("sp")) {
                        //Sp value can only be changed by pop or push instructions. If first operand is sp then gives error.
                        System.out.println("Not permitted to access that address at line " + pc);
                        System.exit(0);
                    } else if (firstOperand.equalsIgnoreCase("ah")) {
                        BooleanOperation.byteToReg(secondOperand, ah, type);
                    } else if (firstOperand.equalsIgnoreCase("al")) {
                        BooleanOperation.byteToReg(secondOperand, al, type);
                    } else if (firstOperand.equalsIgnoreCase("bh")) {
                        BooleanOperation.byteToReg(secondOperand, bh, type);
                    } else if (firstOperand.equalsIgnoreCase("bl")) {
                        BooleanOperation.byteToReg(secondOperand, bl, type);
                    } else if (firstOperand.equalsIgnoreCase("ch")) {
                        BooleanOperation.byteToReg(secondOperand, ch, type);
                    } else if (firstOperand.equalsIgnoreCase("cl")) {
                        BooleanOperation.byteToReg(secondOperand, cl, type);
                    } else if (firstOperand.equalsIgnoreCase("dh")) {
                        BooleanOperation.byteToReg(secondOperand, dh, type);
                    } else if (firstOperand.equalsIgnoreCase("dl")) {
                        BooleanOperation.byteToReg(secondOperand, dl, type);
                    } else if (firstOperand.charAt(0) == 'W' || firstOperand.charAt(0) == 'w') {
                        BooleanOperation.wordToMemory(firstOperand.substring(2, firstOperand.length() - 1), secondOperand, type, true);
                    } else if (firstOperand.charAt(0) == 'B' || firstOperand.charAt(0) == 'b') {
                        BooleanOperation.byteToMemory(firstOperand.substring(2, firstOperand.length() - 1), secondOperand, type);
                    } else if (firstOperand.charAt(0) == '[' || firstOperand.endsWith("]")) {
                        BooleanOperation.wordToMemory(firstOperand.substring(2, firstOperand.length() - 1), secondOperand, type, false);
                    }

                } else if (operation.equalsIgnoreCase("SHL") || operation.equalsIgnoreCase("SHR")) {

                    checkVariableTwoOperand();

                    boolean isSHR = false;
                    if (operation.equalsIgnoreCase("SHR"))
                        isSHR = true;

                    if (firstOperand.equalsIgnoreCase("ax")) {
                        Shift.bigReg(ax, secondOperand, true, isSHR);
                    } else if (firstOperand.equalsIgnoreCase("bx")) {
                        Shift.bigReg(bx, secondOperand, true, isSHR);
                    } else if (firstOperand.equalsIgnoreCase("cx")) {
                        Shift.bigReg(cx, secondOperand, true, isSHR);
                    } else if (firstOperand.equalsIgnoreCase("dx")) {
                        Shift.bigReg(dx, secondOperand, true, isSHR);
                    } else if (firstOperand.equalsIgnoreCase("SI") || firstOperand.equalsIgnoreCase("si")) {
                        Shift.bigReg(si, secondOperand, false, isSHR);
                    } else if (firstOperand.equalsIgnoreCase("DI") || firstOperand.equalsIgnoreCase("di")) {
                        Shift.bigReg(di, secondOperand, false, isSHR);
                    } else if (firstOperand.equalsIgnoreCase("bp")) {
                        Shift.bigReg(bp, secondOperand, false, isSHR);
                    } else if (firstOperand.equalsIgnoreCase("sp")) {
                        //Sp value can only be changed by pop or push instructions. If first operand is sp then gives error.
                        System.out.println("Not permitted to access that address at line " + pc);
                        System.exit(0);
                    } else if (firstOperand.equalsIgnoreCase("ah")) {
                        Shift.smallReg(ah, secondOperand, isSHR);
                    } else if (firstOperand.equalsIgnoreCase("al")) {
                        Shift.smallReg(al, secondOperand, isSHR);
                    } else if (firstOperand.equalsIgnoreCase("bh")) {
                        Shift.smallReg(bh, secondOperand, isSHR);
                    } else if (firstOperand.equalsIgnoreCase("bl")) {
                        Shift.smallReg(bl, secondOperand, isSHR);
                    } else if (firstOperand.equalsIgnoreCase("ch")) {
                        Shift.smallReg(ch, secondOperand, isSHR);
                    } else if (firstOperand.equalsIgnoreCase("cl")) {
                        Shift.smallReg(cl, secondOperand, isSHR);
                    } else if (firstOperand.equalsIgnoreCase("dh")) {
                        Shift.smallReg(dh, secondOperand, isSHR);
                    } else if (firstOperand.equalsIgnoreCase("dl")) {
                        Shift.smallReg(dl, secondOperand, isSHR);
                    } else if (firstOperand.charAt(0) == 'W' || firstOperand.charAt(0) == 'w' ||
                            firstOperand.charAt(0) == 'B' || firstOperand.charAt(0) == 'b') {
                        Shift.memory(firstOperand, secondOperand, isSHR);
                    }
                    if (firstOperand.charAt(0) == '[' && firstOperand.endsWith("]")) {
                        System.out.println("Byte or Word? at line " + pc);
                        System.exit(0);
                    }

                } else if (operation.equalsIgnoreCase("RCL") || operation.equalsIgnoreCase("RCR")) {

                    checkVariableTwoOperand();

                    boolean isRCR = false;
                    if (operation.equalsIgnoreCase("RCR"))
                        isRCR = true;

                    if (firstOperand.equalsIgnoreCase("ax")) {
                        Rotate.bigReg(ax, secondOperand, true, isRCR);
                    } else if (firstOperand.equalsIgnoreCase("bx")) {
                        Rotate.bigReg(bx, secondOperand, true, isRCR);
                    } else if (firstOperand.equalsIgnoreCase("cx")) {
                        Rotate.bigReg(cx, secondOperand, true, isRCR);
                    } else if (firstOperand.equalsIgnoreCase("dx")) {
                        Rotate.bigReg(dx, secondOperand, true, isRCR);
                    } else if (firstOperand.equalsIgnoreCase("SI") || firstOperand.equalsIgnoreCase("si")) {
                        Rotate.bigReg(si, secondOperand, false, isRCR);
                    } else if (firstOperand.equalsIgnoreCase("DI") || firstOperand.equalsIgnoreCase("di")) {
                        Rotate.bigReg(di, secondOperand, false, isRCR);
                    } else if (firstOperand.equalsIgnoreCase("bp")) {
                        Rotate.bigReg(bp, secondOperand, false, isRCR);
                    } else if (firstOperand.equalsIgnoreCase("sp")) {
                        //Sp value can only be changed by pop or push instructions. If first operand is sp then gives error.
                        System.out.println("Not permitted to access that address at line " + pc);
                        System.exit(0);
                    } else if (firstOperand.equalsIgnoreCase("ah")) {
                        Rotate.smallReg(ah, secondOperand, isRCR);
                    } else if (firstOperand.equalsIgnoreCase("al")) {
                        Rotate.smallReg(al, secondOperand, isRCR);
                    } else if (firstOperand.equalsIgnoreCase("bh")) {
                        Rotate.smallReg(bh, secondOperand, isRCR);
                    } else if (firstOperand.equalsIgnoreCase("bl")) {
                        Rotate.smallReg(bl, secondOperand, isRCR);
                    } else if (firstOperand.equalsIgnoreCase("ch")) {
                        Rotate.smallReg(ch, secondOperand, isRCR);
                    } else if (firstOperand.equalsIgnoreCase("cl")) {
                        Rotate.smallReg(cl, secondOperand, isRCR);
                    } else if (firstOperand.equalsIgnoreCase("dh")) {
                        Rotate.smallReg(dh, secondOperand, isRCR);
                    } else if (firstOperand.equalsIgnoreCase("dl")) {
                        Rotate.smallReg(dl, secondOperand, isRCR);
                    } else if (firstOperand.charAt(0) == 'W' || firstOperand.charAt(0) == 'w' ||
                            firstOperand.charAt(0) == 'B' || firstOperand.charAt(0) == 'b') {
                        Rotate.memory(firstOperand, secondOperand, isRCR);
                    }
                    if (firstOperand.charAt(0) == '[' && firstOperand.endsWith("]")) {
                        System.out.println("Byte or Word? at line " + pc);
                        System.exit(0);
                    }

                } else if (operation.equalsIgnoreCase("JMP")) {
                    //If label is defined in the first reading then jumps, else gives error.
                    if (labelNo.containsKey(operands[1])) {
                        lineNumber = labelNo.get(operands[1]) - (lineNumberOfCodeSegment + 1);
                    } else {
                        System.out.println("Undefined label " + pc);
                        System.exit(0);
                    }
                } else if (operation.equalsIgnoreCase("JZ") || operation.equalsIgnoreCase("JE")) {
                    if (zf) {
                        //If label is defined in the first reading then jumps, else gives error.
                        if (labelNo.containsKey(operands[1])) {
                            lineNumber = labelNo.get(operands[1]) - (lineNumberOfCodeSegment + 1);
                        } else {
                            System.out.println("Undefined label " + pc);
                            System.exit(0);
                        }
                    }
                } else if (operation.equalsIgnoreCase("JNZ") || operation.equalsIgnoreCase("JNE")) {
                    if (!zf) {
                        //If label is defined in the first reading then jumps, else gives error.
                        if (labelNo.containsKey(operands[1])) {
                            lineNumber = labelNo.get(operands[1]) - (lineNumberOfCodeSegment + 1);
                        } else {
                            System.out.println("Undefined label " + pc);
                            System.exit(0);
                        }
                    }
                } else if (operation.equalsIgnoreCase("JA") || operation.equalsIgnoreCase("JNBE")) {
                    if (!cf && !zf) {
                        //If label is defined in the first reading then jumps, else gives error.
                        if (labelNo.containsKey(operands[1])) {
                            lineNumber = labelNo.get(operands[1]) - (lineNumberOfCodeSegment + 1);
                        } else {
                            System.out.println("Undefined label " + pc);
                            System.exit(0);
                        }
                    }
                } else if (operation.equalsIgnoreCase("JAE") || operation.equalsIgnoreCase("JNB") || operation.equalsIgnoreCase("JNC")) {
                    if (!cf) {
                        //If label is defined in the first reading then jumps, else gives error.
                        if (labelNo.containsKey(operands[1])) {
                            lineNumber = labelNo.get(operands[1]) - (lineNumberOfCodeSegment + 1);
                        } else {
                            System.out.println("Undefined label " + pc);
                            System.exit(0);
                        }
                    }
                } else if (operation.equalsIgnoreCase("JB") || operation.equalsIgnoreCase("JNAE") || operation.equalsIgnoreCase("JC")) {
                    if (cf) {
                        //If label is defined in the first reading then jumps, else gives error.
                        if (labelNo.containsKey(operands[1])) {
                            lineNumber = labelNo.get(operands[1]) - (lineNumberOfCodeSegment + 1);
                        } else {
                            System.out.println("Undefined label " + pc);
                            System.exit(0);
                        }
                    }
                } else if (operation.equalsIgnoreCase("JBE")) {
                    if (cf || zf) {
                        //If label is defined in the first reading then jumps, else gives error.
                        if (labelNo.containsKey(operands[1])) {
                            lineNumber = labelNo.get(operands[1]) - (lineNumberOfCodeSegment + 1);
                        } else {
                            System.out.println("Undefined label " + pc);
                            System.exit(0);
                        }
                    }

                } else if (operation.equalsIgnoreCase("INT") || operation.equalsIgnoreCase("ÝNT")) {
                    if (operands[1].equalsIgnoreCase("20h")) System.exit(0);

                    if (operands[1].equalsIgnoreCase("21h")) {
                        if (oneByteRegisters[ah] == 1) {
                            oneByteRegisters[al] = (byte) console.nextLine().charAt(0);
                        } else if (oneByteRegisters[ah] == 2) {
                            System.out.print((char) oneByteRegisters[dl]);
                            oneByteRegisters[al] = oneByteRegisters[dl];
                        }
                    }
                } else if (operation.equalsIgnoreCase("NOT")) {
                    checkVariableOneOperand();
                    BooleanOperation.notOperation(firstOperand);

                } else if (operation.equalsIgnoreCase("PUSH")) {
                    checkVariableOneOperand();
                    StackController.push(firstOperand);

                } else if (operation.equalsIgnoreCase("POP")) {
                    checkVariableOneOperand();
                    StackController.pop(firstOperand);

                } else if (operation.equalsIgnoreCase("CMP")) {
                    checkVariableTwoOperand();

                    if (firstOperand.equalsIgnoreCase("ax")) {
                        Compare.compareWordRegister(ax, secondOperand);
                    } else if (firstOperand.equalsIgnoreCase("bx")) {
                        Compare.compareWordRegister(bx, secondOperand);
                    } else if (firstOperand.equalsIgnoreCase("cx")) {
                        Compare.compareWordRegister(cx, secondOperand);
                    } else if (firstOperand.equalsIgnoreCase("dx")) {
                        Compare.compareWordRegister(dx, secondOperand);
                    } else if (firstOperand.equalsIgnoreCase("SI") || firstOperand.equalsIgnoreCase("si")) {
                        Compare.compareWordRegister(si, secondOperand);
                    } else if (firstOperand.equalsIgnoreCase("DI") || firstOperand.equalsIgnoreCase("di")) {
                        Compare.compareWordRegister(di, secondOperand);
                    } else if (firstOperand.equalsIgnoreCase("bp")) {
                        Compare.compareWordRegister(bp, secondOperand);
                    } else if (firstOperand.equalsIgnoreCase("sp")) {
                        Compare.compareWordMemory("sp", secondOperand, true);
                    } else if (firstOperand.equalsIgnoreCase("ah")) {
                        Compare.compareByteRegister(ah, secondOperand);
                    } else if (firstOperand.equalsIgnoreCase("al")) {
                        Compare.compareByteRegister(al, secondOperand);
                    } else if (firstOperand.equalsIgnoreCase("bh")) {
                        Compare.compareByteRegister(bh, secondOperand);
                    } else if (firstOperand.equalsIgnoreCase("bl")) {
                        Compare.compareByteRegister(bl, secondOperand);
                    } else if (firstOperand.equalsIgnoreCase("ch")) {
                        Compare.compareByteRegister(ch, secondOperand);
                    } else if (firstOperand.equalsIgnoreCase("cl")) {
                        Compare.compareByteRegister(cl, secondOperand);
                    } else if (firstOperand.equalsIgnoreCase("dh")) {
                        Compare.compareByteRegister(dh, secondOperand);
                    } else if (firstOperand.equalsIgnoreCase("dl")) {
                        Compare.compareByteRegister(dl, secondOperand);
                    } else if (firstOperand.charAt(0) == 'W' || firstOperand.charAt(0) == 'w') {
                        Compare.compareWordMemory(firstOperand.substring(2, firstOperand.length() - 1), secondOperand, true);
                    } else if (firstOperand.charAt(0) == 'B' || firstOperand.charAt(0) == 'b') {
                        Compare.compareByteMemory(firstOperand.substring(2, firstOperand.length() - 1), secondOperand);
                    } else if (firstOperand.charAt(0) == '[' && firstOperand.endsWith("]")) {
                        Compare.compareWordMemory(firstOperand.substring(1, firstOperand.length() - 1), secondOperand, false);
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Index out of bounds error at line " + pc); System.exit(0);
        } catch (Exception e) {
            System.out.println("Error at line " + pc); System.exit(0);
        }
    }

    /* Determines first and second operand of command. If operand is variable convert it into memory address according to its prefix or how it is defined.
    If prefix is offset, then second operand takes memory address of variable as integer value.
    If operand is memory address, second operand or first operand assigned as merged version of prefix and operand.
    Gives error when there is too many arguments, missing comma, variable is not defined or prefix is used in a wrong way.
     */


    public static void checkVariableTwoOperand() {
        String variable = "", dataType, tempFirstOperand, tempSecondOperand;
        prefix = ""; //Can be w or b or offset. Else there is a syntax error.
        int indexOfComma, lengthOfCommand;
        if (operands.length>1 && (operands[1].equalsIgnoreCase("w") || operands[1].equalsIgnoreCase("b") || operands[1].equalsIgnoreCase("offset"))) {
            tempFirstOperand = operands[2];
            dataType = operands[1];
            if (tempFirstOperand.charAt(0) == '[' && tempFirstOperand.endsWith("]")) {
                firstOperand = dataType + tempFirstOperand;
            } else {
                firstOperand = tempFirstOperand;
                prefix = dataType;
                variable = firstOperand;
            }
            indexOfComma = 3;
        } else if (operands.length > 2 && (operands[2].equalsIgnoreCase("w") || operands[2].equalsIgnoreCase("b") || operands[2].equalsIgnoreCase("offset"))) {
            tempFirstOperand = operands[1];
            dataType = operands[2];
            if (tempFirstOperand.charAt(0) == '[' && tempFirstOperand.endsWith("]")) {
                firstOperand = dataType + tempFirstOperand;
            } else {
                firstOperand = tempFirstOperand;
                prefix = dataType;
                variable = firstOperand;
            }
            indexOfComma = 3;
        } else {
            firstOperand = operands[1];
            indexOfComma = 2;
        }

        if (!operands[indexOfComma].equals(",")) {
            System.out.println("Missing comma at line " + pc); System.exit(0);
        }

        if (operands.length > (indexOfComma + 1) && (operands[indexOfComma + 1].equalsIgnoreCase("w") || operands[indexOfComma + 1].equalsIgnoreCase("b") || operands[indexOfComma + 1].equalsIgnoreCase("offset"))) {
            dataType = operands[indexOfComma + 1];
            tempSecondOperand = operands[indexOfComma + 2];
            if (tempSecondOperand.charAt(0) == '[' || tempSecondOperand.endsWith("]")) {
                secondOperand = dataType + tempSecondOperand;
            } else {
                secondOperand = tempSecondOperand;
                variable = tempSecondOperand;
                prefix = dataType;
            }
            lengthOfCommand = indexOfComma + 3;
        } else if (operands.length > (indexOfComma + 2) && (operands[indexOfComma + 2].equalsIgnoreCase("w") || operands[indexOfComma + 2].equalsIgnoreCase("b") || operands[indexOfComma + 2].equalsIgnoreCase("offset"))) {
            dataType = operands[indexOfComma + 2];
            tempSecondOperand = operands[indexOfComma + 1];
            if (secondOperand.charAt(0) == '[' || secondOperand.endsWith("]")) {
                secondOperand = dataType + tempSecondOperand;
            } else {
                secondOperand = tempSecondOperand;
                variable = secondOperand;
                prefix = dataType;
            }
            lengthOfCommand = indexOfComma + 3;
        } else {
            secondOperand = operands[indexOfComma + 1];
            lengthOfCommand = indexOfComma + 2;
        }

        if (operands.length != lengthOfCommand) {
            System.out.println("Too many arguments at line " + pc); System.exit(0);
        }

        if (!prefix.isEmpty()) {
            if (prefix.equalsIgnoreCase("offset")) {
                if (!(wordVariables.containsKey(variable) || byteVariables.containsKey(variable))) {
                    System.out.println("Unknown operand at line " + pc);
                    System.exit(0);
                } else if (firstOperand.equals(variable)) {
                    System.out.println("Constant operand required at line " + pc);
                    System.exit(0);
                } else {
                    if (wordVariables.containsKey(variable)) {
                        secondOperand = "" + wordVariables.get(variable);
                    } else {
                        secondOperand = "" +  byteVariables.get(variable);
                    }
                }
            } else {
                if (!(wordVariables.containsKey(variable) || byteVariables.containsKey(variable))) {
                    System.out.println("Unknown operand at line " + pc);
                    System.exit(0);
                } else {
                    if (byteVariables.containsKey(variable)) {
                        if (variable.equals(firstOperand))
                            firstOperand = prefix + byteVariables.get(variable) + "]";
                        else
                            secondOperand = prefix + byteVariables.get(variable) + "]";
                    } else if (wordVariables.containsKey(variable)) {
                        if (variable.equals(firstOperand))
                            firstOperand = prefix + wordVariables.get(variable) + "]";
                        else
                            secondOperand = prefix + wordVariables.get(variable) + "]";
                    }
                }
            }

        } else if (byteVariables.containsKey(firstOperand)) {
            firstOperand = "b[" + byteVariables.get(firstOperand) + "]";
        } else if (byteVariables.containsKey(secondOperand)) {
            secondOperand = "b[" + byteVariables.get(secondOperand) + "]";
        } else if (wordVariables.containsKey(firstOperand)) {
            firstOperand = "w[" + wordVariables.get(firstOperand) + "]";
        } else if (wordVariables.containsKey(secondOperand)) {
            secondOperand = "w[" + wordVariables.get(secondOperand) + "]";
        }
    }

    //Works just like as upper method. Only difference is operations are done only for one operand.
    public static void checkVariableOneOperand() {
        String tempFirstOperand, dataType, prefix = "";
        int lengthOfCommand;
        if (operands[1].equalsIgnoreCase("w") || operands[1].equalsIgnoreCase("b") || operands[1].equalsIgnoreCase("offset")) {
            tempFirstOperand = operands[2];
            dataType = operands[1];
            if(tempFirstOperand.charAt(0) == '[' && tempFirstOperand.endsWith("]")) {
                firstOperand = dataType + tempFirstOperand;
            } else {
                firstOperand = tempFirstOperand;
                prefix = dataType;
            }
            lengthOfCommand = 3;
        } else if ((operands.length > 2) && (operands[2].equalsIgnoreCase("w") || operands[2].equalsIgnoreCase("b") || operands[2].equalsIgnoreCase("offset"))) {
            tempFirstOperand = operands[1];
            dataType = operands[2];
            if(tempFirstOperand.charAt(0) == '[' && tempFirstOperand.endsWith("]")) {
                firstOperand = dataType + tempFirstOperand;
            } else {
                firstOperand = tempFirstOperand;
                prefix = dataType;
            }
            lengthOfCommand = 3;
        } else {
            firstOperand = operands[1];
            lengthOfCommand = 2;
        }

        if(operands.length != lengthOfCommand){
            System.out.println("Too many arguments at line " + pc); System.exit(0);
        }

        if (prefix.length() != 0){
            if(prefix.equalsIgnoreCase("offset")) {
                if (!(wordVariables.containsKey(firstOperand) || byteVariables.containsKey(firstOperand))) {
                    System.out.println("Unknown operand at line " + pc); System.exit(0);
                } else if (operation.equalsIgnoreCase("POP") || operation.equalsIgnoreCase("NOT")) {
                    System.out.println("Constant operand required at line " + pc); System.exit(0);
                } else {
                    if (wordVariables.containsKey(firstOperand)) {
                        firstOperand = "" + wordVariables.get(firstOperand);
                    } else {
                        firstOperand = "" + byteVariables.get(firstOperand);
                    }
                }
            } else {
                if (!(wordVariables.containsKey(firstOperand) || byteVariables.containsKey(firstOperand))) {
                    System.out.println("Unknown operand at line " + pc); System.exit(0);
                } else {
                    if (byteVariables.containsKey(firstOperand)) {
                        firstOperand = "prefix[" + byteVariables.get(firstOperand) + "]";
                    } else if (wordVariables.containsKey(firstOperand)) {
                        firstOperand = "prefix[" + wordVariables.get(firstOperand) + "]";
                    }
                }

            }
        } else if(byteVariables.containsKey(firstOperand)) {
                firstOperand = "b[" + byteVariables.get(firstOperand) + "]";
        } else if(wordVariables.containsKey(firstOperand)) {
            firstOperand = "w[" + wordVariables.get(firstOperand) + "]";
        }
    }
}

