package com.mycompany.app;

import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.InputMismatchException;

/**
 * The FileReader Class implements the Function readFile(),
 * which understands a TXT-File with an Environment formulated in STRIPS.
 * Is capable to identify Start-State, Goal-State and Actions.
 */
public class FileReader {
    private final BufferedReader br;
    private PlannerList<OperationEffect> SOPlannerList; //List of Predicates in S0
    private PlannerList<OperationPrecondition> SzPlannerList; //List of Predicates in Sz
    private final PlannerList<Operation> ActionPlannerList; //List all of Actions

    /**
     * FileReader Constructor
     * instantiates the Lists
     * starts the readFile function
     *
     * @throws FileNotFoundException if file is not found
     */
    public FileReader(@NotNull String fileName) throws IOException {
        SOPlannerList = new PlannerList<>();
        SzPlannerList = new PlannerList<>();
        ActionPlannerList = new PlannerList<>();
        java.io.FileReader fr = new java.io.FileReader(fileName);
        br = new BufferedReader(fr);

        readFile();
    }

    /**
     * FileReader Constructor
     * instantiates the Lists
     * starts the readFile function
     */
    public FileReader(@NotNull StringReader data) throws IOException{
        SOPlannerList = new PlannerList<>();
        SzPlannerList = new PlannerList<>();
        ActionPlannerList = new PlannerList<>();
        br = new BufferedReader(data);

        readFile();
    }

    /**
     * Reads the InputFile line by line (uses readNewLine-Function)
     * identifies the Line-type(S0,Sz, ACT, PRE, EFF, Empty)
     * and calls the Functions handling each Line-Type
     * when there are 5 Empty lines the loop ends and the Reading is finished
     *
     * @throws IOException if (readNewLine() throws)
     */
    private void readFile() throws IOException {
        boolean bool = true;
        int emptyLineCounter = 0;
        while (bool) {
            String row = readNewLine();
            switch (getLineType(row)) {
                case s0, sz -> {
                    emptyLineCounter = 0;
                    handleStartAndEndState(row);
                }
                case act -> {
                    emptyLineCounter = 0;
                    handleOperators(row);
                }
                case emt -> {
                    emptyLineCounter++;
                    if (emptyLineCounter > 5) {
                        bool = false;
                    }
                }
            }
        }


    }

    /**
     * Reads new Line of the TXT-File and formats it Into a usable String
     * Reads one Line using the buffered-Reader(br)
     * Removes Spaces and Curly-Braces from the line and returns a more easily readable Line (uses removeChar)
     * When there aren't any Lines left to read it returns an Empty line
     *
     * @return String of a Line without Curly-Braces and Spaces
     * @throws IOException if (thrown by readLine())
     */
    private String readNewLine() throws IOException {
        String line = this.br.readLine();
        if (line != null) {
            line = removeChar(line, '{');
            line = removeChar(line, '}');
            line = removeChar(line, ' ');
            line = removeChar(line, '\t');
            return line;
        }
        return "";
    }

    /**
     * Handles Strings of the Type s0,sz.
     * Splits the String into a Name and an Array of Strings each containing one Predicate
     * Calls handlePredicate on the Array of Strings and gets an PlannerList of Predicates
     * Based on the Name the predicate ArrayList is copied into the Global PlannerLists
     *
     * @param s String which describes a Start- or an End-State
     */
    private void handleStartAndEndState(@NotNull String s) {
        String name = (s.split(":"))[0];
        String[] pred = (s.split(":"))[1].split(",(?![^()]*\\))");
        if (name.equals("S0")) {
            SOPlannerList = handleEffects(pred);
        } else if (name.equals("Sz")) {
            SzPlannerList = handlePreconditions(pred);
        }
    }

    /**
     * Handles Strings of the Type act.
     * Reads the Name of the Action
     * Calls readNewLine, when the LineType is PRE it makes a List of Strings
     * uses handlePredicates to create an PlannerList of Predicate-Objects and Saves the List as preconditionPlannerList
     * Calls readNewLine, when the LineType is EFF it makes a List of Strings
     * uses handlePredicates to create an PlannerList of Predicate-Objects and Saves the List as effectPlannerList
     * Uses Name, precondition PlannerList and effectPlannerList to Create an Action-Object and Adds it to the ActionPlannerList
     *
     * @param s String which describes an Operator.
     * @throws InputMismatchException   if (thrown by readNewLine())
     * @throws IllegalArgumentException if (s == null)
     */
    private void handleOperators(@NotNull String s) throws IOException, InputMismatchException {
        PlannerList<OperationPrecondition> preconditionPlannerList = new PlannerList<>();
        PlannerList<OperationEffect> effectPlannerList = new PlannerList<>();
        String name = s.split(":")[1];
        String preRow = readNewLine();
        if (getLineType(preRow) == LineType.pre) {
            if (preRow.split(":").length > 1 && !(preRow.split(":"))[1].equals("")) {
                String[] prePred = (preRow.split(":"))[1].split(",(?![^()]*\\))");
                preconditionPlannerList = handlePreconditions(prePred);
            }
        } else {
            throw new InputMismatchException("preconditions malformed");
        }
        String effRow = readNewLine();
        if (getLineType(effRow) == LineType.eff) {
            if (effRow.split(":").length > 1 && !(effRow.split(":"))[1].equals("")) {
                String[] effPred = (effRow.split(":"))[1].split(",(?![^()]*\\))");
                effectPlannerList = handleEffects(effPred);
            }
        } else {
            throw new InputMismatchException("effects malformed");
        }
        ActionPlannerList.add(new Operation(name, 0, preconditionPlannerList, effectPlannerList));
    }

    /**
     * Calls handlePredicate for each Element of predicateList and creates an PlannerList of Predicate-Objects
     *
     * @param predicateList Array of Predicates as Strings
     * @throws IllegalArgumentException if (predicateList == null)
     */
    private PlannerList<OperationEffect> handleEffects(@NotNull String[] predicateList) {
        PlannerList<OperationEffect> predicates = new PlannerList<>();
        for (String predicateString : predicateList) {
            OperationEffect predicate = handleEffect(predicateString);
            predicates.add(predicate);
        }
        return predicates;
    }

    /**
     * Calls handlePredicate for each Element of predicateList and creates an PlannerList of Predicate-Objects
     *
     * @param predicateList Array of Predicates as Strings
     * @throws IllegalArgumentException if (predicateList == null)
     */
    private PlannerList<OperationPrecondition> handlePreconditions(@NotNull String[] predicateList) {
        PlannerList<OperationPrecondition> predicates = new PlannerList<>();
        for (String predicateString : predicateList) {
            OperationPrecondition predicate = handlePrecondition(predicateString);
            predicates.add(predicate);
        }
        return predicates;
    }

    /**
     * Checks predicate-String for Negations
     * Splits String into the Name and an Array of Argument-Strings
     * Creates for each Element of the Array a new Literal and adds them to the arguments PlannerList
     * Create a new Predicate using the Name, the Negation and the arguments PlannerList
     *
     * @param predicate string of a single Predicate
     * @return Predicate-Object
     */
    private OperationPrecondition handlePrecondition(@NotNull String predicate) {
        boolean negation = false;
        String[] splitPredicate;
        String name;
        String[] strArguments = {};
        //handles Equals(==) predicates(name= EQUALS)
        if (predicate.contains("!=")) {
            negation = true;
            splitPredicate = predicate.split("!=");
            name = "neq";
            strArguments = new String[]{splitPredicate[0], splitPredicate[1]};
        } else {
            //check for negation
            if (predicate.charAt(0) == '!') {
                negation = true;
                predicate = removeChar(predicate, '!');
            }
            predicate = removeChar(predicate, ')');

            //split String into Predicate and Arguments
            splitPredicate = predicate.split("\\(");

            //get name
            name = splitPredicate[0];
            //get arguments

            strArguments = splitPredicate[1].split(",");
        }
        //get the arguments
        PlannerList<Argument> arguments = new PlannerList<>();
        for (String strArgument : strArguments) {
            //checks first Character for Lower Case if True it creates a Variable
            if (Character.isLowerCase(strArgument.charAt(0))) {
                Variable variable = new Variable(strArgument);
                arguments.add(variable);
            } else {
                Literal literal = new Literal(strArgument);
                arguments.add(literal);
            }
        }
        if (name.equals("neq")) {
            return new UnequalOperationPrecondition(arguments);
        }
        //create and return the new predicate
        return new OperationPrecondition(name, negation, arguments);
    }

    /**
     * Checks predicate-String for Negations
     * Splits String into the Name and an Array of Argument-Strings
     * Creates for each Element of the Array a new Literal and adds them to the arguments PlannerList
     * Create a new Predicate using the Name, the Negation and the arguments PlannerList
     *
     * @param predicate string of a single Predicate
     * @return Predicate-Object
     */
    private OperationEffect handleEffect(@NotNull String predicate) {
        boolean negation = false;
        String[] splitPredicate;
        String name;
        String[] strArguments = {};
        //check for negation
        if (predicate.charAt(0) == '!') {
            negation = true;
            predicate = removeChar(predicate, '!');
        }
        predicate = removeChar(predicate, ')');

        //split String into Predicate and Arguments
        splitPredicate = predicate.split("\\(");

        //get name
        name = splitPredicate[0];
        //get arguments
        strArguments = splitPredicate[1].split(",");
        //get the arguments
        PlannerList<Argument> arguments = new PlannerList<>();
        for (String strArgument : strArguments) {
            //checks first Character for Lower Case if True it creates a Variable
            if (Character.isLowerCase(strArgument.charAt(0))) {
                Variable variable = new Variable(strArgument);
                arguments.add(variable);
            } else {
                Literal literal = new Literal(strArgument);
                arguments.add(literal);
            }
        }
        //create and return the new predicate
        return new OperationEffect(name, negation, arguments);
    }

    /**
     * Removes a char from a given String
     *
     * @param s  String where a char needs to be Removed
     * @param ch Char which has to be Removed
     * @return Input string without the given char
     * @throws IllegalArgumentException if (s == null)
     */
    private String removeChar(@NotNull String s, char ch) {
        return s.replace(String.valueOf(ch), "");
    }

    /**
     * Identifies the type of read line
     *
     * @param s String of a read line
     * @return Type of the Line (see LineType-Enum)
     * @throws IllegalArgumentException if (s == null)
     */
    private LineType getLineType(@NotNull String s) {
        if (s.isEmpty()) {
            return LineType.emt;
        }
        String[] sl = s.split(":");
        return switch (sl[0]) {
            case "S0" -> LineType.s0;
            case "Sz","SZ" -> LineType.sz;
            case "ACT" -> LineType.act;
            case "PRE" -> LineType.pre;
            case "EFF" -> LineType.eff;
            default -> LineType.noType;
        };
    }

    /**
     * @return All Actions formulated in the input.txt File as an PlannerList of Action-Objects
     */
    public PlannerList<Operation> readOperations() {
        return ActionPlannerList;
    }

    /**
     * @return Predicates of the  S0-State formulated in the input.txt File as an PlannerList of Predicate-Objects
     */
    public PlannerList<OperationEffect> readS0() {
        return SOPlannerList;
    }

    /**
     * @return Predicates of the  Sz-State(Goals) formulated in the input.txt File as an PlannerList of Predicate-Objects
     */
    public PlannerList<OperationPrecondition> readSz() {

        return SzPlannerList;
    }
}
