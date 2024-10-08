package com.mycompany.app;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.stream.Stream;

public class FileReaderTest {

    @Test
    void TestReadS0() throws IOException {
        FileReader fileReader = new FileReader("src/test/resources/Basic.txt");
        PlannerList<OperationEffect> InputS0 = fileReader.readS0();

        Assertions.assertEquals(InputS0.size(), 4);

        //on(A, T) (ACTION ID: -1)
        Assertions.assertEquals(InputS0.get(0).getName(), "on");
        Assertions.assertFalse(InputS0.get(0).isNegation());
        Assertions.assertEquals(InputS0.get(0).getNumArguments(), 2);
        Assertions.assertEquals(InputS0.get(0).getArgument(0).getName(), "A");
        Assertions.assertTrue(InputS0.get(0).getArgument(0) instanceof Literal);
        Assertions.assertEquals(InputS0.get(0).getArgument(1).getName(), "T");
        Assertions.assertTrue(InputS0.get(0).getArgument(1) instanceof Literal);

        //on(B, T) (ACTION ID: -1)
        Assertions.assertEquals(InputS0.get(1).getName(), "on");
        Assertions.assertFalse(InputS0.get(1).isNegation());
        Assertions.assertEquals(InputS0.get(1).getNumArguments(), 2);
        Assertions.assertEquals(InputS0.get(1).getArgument(0).getName(), "B");
        Assertions.assertTrue(InputS0.get(1).getArgument(0) instanceof Literal);
        Assertions.assertEquals(InputS0.get(1).getArgument(1).getName(), "T");
        Assertions.assertTrue(InputS0.get(1).getArgument(1) instanceof Literal);

        //clear(A) (ACTION ID: -1)
        Assertions.assertEquals(InputS0.get(2).getName(), "clear");
        Assertions.assertFalse(InputS0.get(2).isNegation());
        Assertions.assertEquals(InputS0.get(2).getNumArguments(), 1);
        Assertions.assertEquals(InputS0.get(2).getArgument(0).getName(), "A");
        Assertions.assertTrue(InputS0.get(2).getArgument(0) instanceof Literal);

        //clear(B) (ACTION ID: -1)
        Assertions.assertEquals(InputS0.get(3).getName(), "clear");
        Assertions.assertFalse(InputS0.get(3).isNegation());
        Assertions.assertEquals(InputS0.get(3).getNumArguments(), 1);
        Assertions.assertEquals(InputS0.get(3).getArgument(0).getName(), "B");
        Assertions.assertTrue(InputS0.get(3).getArgument(0) instanceof Literal);
    }

    @Test
    void TestReadSz() throws IOException {
        FileReader fileReader = new FileReader("src/test/resources/Basic.txt");
        PlannerList<OperationPrecondition> InputSz = fileReader.readSz();

        Assertions.assertEquals(InputSz.size(), 2);

        //on(A, T) (ACTION ID: -1)
        Assertions.assertEquals(InputSz.get(0).getName(), "on");
        Assertions.assertFalse(InputSz.get(0).isNegation());
        Assertions.assertEquals(InputSz.get(0).getNumArguments(), 2);
        Assertions.assertEquals(InputSz.get(0).getArgument(0).getName(), "A");
        Assertions.assertTrue(InputSz.get(0).getArgument(0) instanceof Literal);
        Assertions.assertEquals(InputSz.get(0).getArgument(1).getName(), "T");
        Assertions.assertTrue(InputSz.get(0).getArgument(1) instanceof Literal);

        //on(B, A) (ACTION ID: -1)
        Assertions.assertEquals(InputSz.get(1).getName(), "on");
        Assertions.assertFalse(InputSz.get(1).isNegation());
        Assertions.assertEquals(InputSz.get(1).getNumArguments(), 2);
        Assertions.assertEquals(InputSz.get(1).getArgument(0).getName(), "B");
        Assertions.assertTrue(InputSz.get(1).getArgument(0) instanceof Literal);
        Assertions.assertEquals(InputSz.get(1).getArgument(1).getName(), "A");
        Assertions.assertTrue(InputSz.get(1).getArgument(1) instanceof Literal);
    }

    @Test
    void TestReadActions() throws IOException {
        FileReader fileReader = new FileReader("src/test/resources/Basic.txt");
        PlannerList<Operation> actions = fileReader.readOperations();

        //PRIORITY: 0
        //ID: 0
        //PLAN STEP ID: -1
        //ACT: stack(B,A)()
        //PRE: {on(A, T) (ACTION ID: 0), on(B, T) (ACTION ID: 0), clear(A) (ACTION ID: 0), clear(B) (ACTION ID: 0)}
        //EFF: {on(B, A) (ACTION ID: 0), !clear(A) (ACTION ID: 0), !on(B, T) (ACTION ID: 0)}
        Assertions.assertEquals(actions.size(), 1);

        Assertions.assertEquals(actions.get(0).getName(), "stack(B,A)");
        Assertions.assertEquals(actions.get(0).getPriority(), 0);
        Assertions.assertEquals(actions.get(0).getNumPreconditions(), 4);
        Assertions.assertEquals(actions.get(0).getNumEffects(), 3);

        Predicate precondition0 = actions.get(0).getPrecondition(0);
        Assertions.assertEquals(precondition0.getName(), "on");
        Assertions.assertFalse(precondition0.isNegation());
        Assertions.assertEquals(precondition0.getNumArguments(), 2);
        Assertions.assertEquals(precondition0.getArgument(0).getName(), "A");
        Assertions.assertTrue(precondition0.getArgument(0) instanceof Literal);
        Assertions.assertEquals(precondition0.getArgument(1).getName(), "T");
        Assertions.assertTrue(precondition0.getArgument(1) instanceof Literal);

        Predicate precondition1 = actions.get(0).getPrecondition(1);
        Assertions.assertEquals(precondition1.getName(), "on");
        Assertions.assertFalse(precondition1.isNegation());
        Assertions.assertEquals(precondition1.getNumArguments(), 2);
        Assertions.assertEquals(precondition1.getArgument(0).getName(), "B");
        Assertions.assertTrue(precondition1.getArgument(0) instanceof Literal);
        Assertions.assertEquals(precondition1.getArgument(1).getName(), "T");
        Assertions.assertTrue(precondition1.getArgument(1) instanceof Literal);

        Predicate precondition2 = actions.get(0).getPrecondition(2);
        Assertions.assertEquals(precondition2.getName(), "clear");
        Assertions.assertFalse(precondition2.isNegation());
        Assertions.assertEquals(precondition2.getNumArguments(), 1);
        Assertions.assertEquals(precondition2.getArgument(0).getName(), "A");
        Assertions.assertTrue(precondition2.getArgument(0) instanceof Literal);

        Predicate precondition3 = actions.get(0).getPrecondition(3);
        Assertions.assertEquals(precondition3.getName(), "clear");
        Assertions.assertFalse(precondition3.isNegation());
        Assertions.assertEquals(precondition3.getNumArguments(), 1);
        Assertions.assertEquals(precondition3.getArgument(0).getName(), "B");
        Assertions.assertTrue(precondition3.getArgument(0) instanceof Literal);

        Predicate effect0 = actions.get(0).getEffect(0);
        Assertions.assertEquals(effect0.getName(), "on");
        Assertions.assertFalse(effect0.isNegation());
        Assertions.assertEquals(effect0.getNumArguments(), 2);
        Assertions.assertEquals(effect0.getArgument(0).getName(), "B");
        Assertions.assertTrue(effect0.getArgument(0) instanceof Literal);
        Assertions.assertEquals(effect0.getArgument(1).getName(), "A");
        Assertions.assertTrue(effect0.getArgument(1) instanceof Literal);

        Predicate effect1 = actions.get(0).getEffect(1);
        Assertions.assertEquals(effect1.getName(), "clear");
        Assertions.assertTrue(effect1.isNegation());
        Assertions.assertEquals(effect1.getNumArguments(), 1);
        Assertions.assertEquals(effect1.getArgument(0).getName(), "A");
        Assertions.assertTrue(effect1.getArgument(0) instanceof Literal);

        Predicate effect2 = actions.get(0).getEffect(2);
        Assertions.assertEquals(effect2.getName(), "on");
        Assertions.assertTrue(effect2.isNegation());
        Assertions.assertEquals(effect2.getNumArguments(), 2);
        Assertions.assertEquals(effect2.getArgument(0).getName(), "B");
        Assertions.assertTrue(effect2.getArgument(0) instanceof Literal);
        Assertions.assertEquals(effect2.getArgument(1).getName(), "T");
        Assertions.assertTrue(effect2.getArgument(1) instanceof Literal);

    }

    //This Test should just Test that there is no Exception thrown when reading the data
    @DisplayName("FileReaderNoExceptions")
    @Test
    void FileReaderTest_NoException() {
        Assertions.assertDoesNotThrow(() -> {
            FileReader f1 = new FileReader("src/test/resources/testInput1.txt");
        }, "An Exception occurred");

    }


    //Testing the "testNoneSense.txt" should throw an input mismatch
    @DisplayName("TestNoneSense")
    @Test
    void FileReaderTest_NoneSense() {
        //assertThrows(InputMismatchException.class, () -> {
        //    FileReader NoneSenseInput = new FileReader("resources/testNoneSense.txt");
        //}, "InputMismatchException has to be thrown");
    }

    /*@DisplayName("TestInput")
    @MethodSource("fileReaderSimpleData")
    @ParameterizedTest
    void FileReaderInputTest(FileReader file){
        //TODO

    }*/

}
