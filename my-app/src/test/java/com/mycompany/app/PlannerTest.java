package com.mycompany.app;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.fail;

public class PlannerTest {

    @Test
    public void BasicStep1CLinks() {
        try {
            FileReader fileReader = new FileReader("src/test/resources/Basic.txt");
            Planner planner = new Planner(fileReader.readOperations(), Planner.toActionEffects(fileReader.readS0()), Planner.toActionPreconditions(fileReader.readSz()));
            planner.planStep(true);
            PlannerList<CLink> cLinks = planner.getCLinks();

            Assertions.assertEquals(cLinks.size(), 2);

            System.out.println(planner.toStringIdentifier());
            //todo

        } catch (IOException e) {
            throw new Error(e);
        } catch (PlannerUndoableException e) {
            fail("Planner is doable");
        } catch (PlannerCompleteException e) {
            fail("Planner is not complete");
        }
    }

    @Test
    public void BasicStep2CLinks() {
        try {
            FileReader fileReader = new FileReader("src/test/resources/Basic.txt");
            Planner planner = new Planner(fileReader.readOperations(), Planner.toActionEffects(fileReader.readS0()), Planner.toActionPreconditions(fileReader.readSz()));
            planner.planStep(true);
            try {
                planner.planStep(true);
            } catch (PlannerUndoableException e) {
                fail("Planner is doable");
            } catch (PlannerCompleteException e) {
                PlannerList<CLink> cLinks = planner.getCLinks();

                Assertions.assertEquals(cLinks.size(), 6);

                //todo
            }
        } catch (IOException e) {
            throw new Error(e);
        } catch (PlannerUndoableException e) {
            fail("Planner is doable");
        } catch (PlannerCompleteException e) {
            fail("Planner is not complete");
        }
    }

    @Test
    public void BasicStep1OpenGoals() {
        try {
            FileReader fileReader = new FileReader("src/test/resources/Basic.txt");
            Planner planner = new Planner(fileReader.readOperations(), Planner.toActionEffects(fileReader.readS0()), Planner.toActionPreconditions(fileReader.readSz()));
            planner.planStep(true);
            PlannerList<ActionPreconditionRef> openGoals = planner.getOpenGoals();

            Assertions.assertEquals(openGoals.size(), 4);

            //todo

        } catch (IOException e) {
            throw new Error(e);
        } catch (PlannerUndoableException e) {
            fail("Planner is doable");
        } catch (PlannerCompleteException e) {
            fail("Planner is not complete");
        }
    }

    @Test
    public void BasicStep2OpenGoals() {
        try {
            FileReader fileReader = new FileReader("src/test/resources/Basic.txt");
            Planner planner = new Planner(fileReader.readOperations(), Planner.toActionEffects(fileReader.readS0()), Planner.toActionPreconditions(fileReader.readSz()));
            planner.planStep(true);
            try {
                planner.planStep(true);
            } catch (PlannerUndoableException e) {
                fail("Planner is doable");
            } catch (PlannerCompleteException e) {
                PlannerList<ActionPreconditionRef> openGoals = planner.getOpenGoals();

                Assertions.assertEquals(openGoals.size(), 0);
            }
        } catch (IOException e) {
            throw new Error(e);
        } catch (PlannerUndoableException e) {
            fail("Planner is doable");
        } catch (PlannerCompleteException e) {
            fail("Planner is not complete");
        }
    }

    @Test
    public void BasicStep1PreviousSteps() {
        try {
            FileReader fileReader = new FileReader("src/test/resources/Basic.txt");
            Planner planner = new Planner(fileReader.readOperations(), Planner.toActionEffects(fileReader.readS0()), Planner.toActionPreconditions(fileReader.readSz()));
            planner.planStep(true);
            PlannerList<PlanStep> previousSteps = planner.getPlanSteps();

            Assertions.assertEquals(previousSteps.size(), 3);

            Assertions.assertEquals(previousSteps.get(2).getIndex(), 2);
            Assertions.assertEquals(previousSteps.get(2).getSubstitutions().size(), 0);

            Assertions.assertEquals(previousSteps.get(2).getAction().getName(), "stack(B,A)");
            Assertions.assertEquals(previousSteps.get(2).getAction().getPlanStepIndex(), 2);
            Assertions.assertEquals(previousSteps.get(2).getAction().getNumPreconditions(), 4);
            Assertions.assertEquals(previousSteps.get(2).getAction().getNumEffects(), 3);

            ActionPrecondition precondition0 = previousSteps.get(2).getAction().getPrecondition(0);
            Assertions.assertEquals(precondition0.getPlanStepIndex(), 2);
            Assertions.assertEquals(precondition0.getName(), "on");
            Assertions.assertFalse(precondition0.isNegation());
            Assertions.assertEquals(precondition0.getNumArguments(), 2);
            Assertions.assertEquals(precondition0.getArgument(0).getName(), "A");
            Assertions.assertTrue(precondition0.getArgument(0) instanceof Literal);
            Assertions.assertEquals(precondition0.getArgument(1).getName(), "T");
            Assertions.assertTrue(precondition0.getArgument(1) instanceof Literal);

            ActionPrecondition precondition1 = previousSteps.get(2).getAction().getPrecondition(1);
            Assertions.assertEquals(precondition1.getPlanStepIndex(), 2);
            Assertions.assertEquals(precondition1.getName(), "on");
            Assertions.assertFalse(precondition1.isNegation());
            Assertions.assertEquals(precondition1.getNumArguments(), 2);
            Assertions.assertEquals(precondition1.getArgument(0).getName(), "B");
            Assertions.assertTrue(precondition1.getArgument(0) instanceof Literal);
            Assertions.assertEquals(precondition1.getArgument(1).getName(), "T");
            Assertions.assertTrue(precondition1.getArgument(1) instanceof Literal);

            ActionPrecondition precondition2 = previousSteps.get(2).getAction().getPrecondition(2);
            Assertions.assertEquals(precondition2.getPlanStepIndex(), 2);
            Assertions.assertEquals(precondition2.getName(), "clear");
            Assertions.assertFalse(precondition2.isNegation());
            Assertions.assertEquals(precondition2.getNumArguments(), 1);
            Assertions.assertEquals(precondition2.getArgument(0).getName(), "A");
            Assertions.assertTrue(precondition2.getArgument(0) instanceof Literal);

            ActionPrecondition precondition3 = previousSteps.get(2).getAction().getPrecondition(3);
            Assertions.assertEquals(precondition3.getPlanStepIndex(), 2);
            Assertions.assertEquals(precondition3.getName(), "clear");
            Assertions.assertFalse(precondition3.isNegation());
            Assertions.assertEquals(precondition3.getNumArguments(), 1);
            Assertions.assertEquals(precondition3.getArgument(0).getName(), "B");
            Assertions.assertTrue(precondition3.getArgument(0) instanceof Literal);

            ActionEffect effect0 = previousSteps.get(2).getAction().getEffect(0);
            Assertions.assertEquals(effect0.getPlanStepIndex(), 2);
            Assertions.assertEquals(effect0.getName(), "on");
            Assertions.assertFalse(effect0.isNegation());
            Assertions.assertEquals(effect0.getNumArguments(), 2);
            Assertions.assertEquals(effect0.getArgument(0).getName(), "B");
            Assertions.assertTrue(effect0.getArgument(0) instanceof Literal);
            Assertions.assertEquals(effect0.getArgument(1).getName(), "A");
            Assertions.assertTrue(effect0.getArgument(1) instanceof Literal);

            ActionEffect effect1 = previousSteps.get(2).getAction().getEffect(1);
            Assertions.assertEquals(effect1.getPlanStepIndex(), 2);
            Assertions.assertEquals(effect1.getName(), "clear");
            Assertions.assertTrue(effect1.isNegation());
            Assertions.assertEquals(effect1.getNumArguments(), 1);
            Assertions.assertEquals(effect1.getArgument(0).getName(), "A");
            Assertions.assertTrue(effect1.getArgument(0) instanceof Literal);

            ActionEffect effect2 = previousSteps.get(2).getAction().getEffect(2);
            Assertions.assertEquals(effect2.getPlanStepIndex(), 2);
            Assertions.assertEquals(effect2.getName(), "on");
            Assertions.assertTrue(effect2.isNegation());
            Assertions.assertEquals(effect2.getNumArguments(), 2);
            Assertions.assertEquals(effect2.getArgument(0).getName(), "B");
            Assertions.assertTrue(effect2.getArgument(0) instanceof Literal);
            Assertions.assertEquals(effect2.getArgument(1).getName(), "T");
            Assertions.assertTrue(effect2.getArgument(1) instanceof Literal);

        } catch (IOException e) {
            throw new Error(e);
        } catch (PlannerUndoableException e) {
            fail("Planner is doable");
        } catch (PlannerCompleteException e) {
            fail("Planner is not complete");
        }
    }

    @Test
    public void BasicStep2PreviousSteps() {
        try {
            FileReader fileReader = new FileReader("src/test/resources/Basic.txt");
            Planner planner = new Planner(fileReader.readOperations(), Planner.toActionEffects(fileReader.readS0()), Planner.toActionPreconditions(fileReader.readSz()));
            planner.planStep(true);
            try {
                planner.planStep(true);
            } catch (PlannerUndoableException e) {
                fail("Planner is doable");
            } catch (PlannerCompleteException e) {
                PlannerList<PlanStep> previousSteps = planner.getPlanSteps();

                Assertions.assertEquals(previousSteps.size(), 3);

                Assertions.assertEquals(previousSteps.get(2).getIndex(), 2);
                Assertions.assertEquals(previousSteps.get(2).getSubstitutions().size(), 0);

                Assertions.assertEquals(previousSteps.get(2).getAction().getName(), "stack(B,A)");
                Assertions.assertEquals(previousSteps.get(2).getAction().getPlanStepIndex(), 2);
                Assertions.assertEquals(previousSteps.get(2).getAction().getNumPreconditions(), 4);
                Assertions.assertEquals(previousSteps.get(2).getAction().getNumEffects(), 3);

                ActionPrecondition precondition0 = previousSteps.get(2).getAction().getPrecondition(0);
                Assertions.assertEquals(precondition0.getPlanStepIndex(), 2);
                Assertions.assertEquals(precondition0.getName(), "on");
                Assertions.assertFalse(precondition0.isNegation());
                Assertions.assertEquals(precondition0.getNumArguments(), 2);
                Assertions.assertEquals(precondition0.getArgument(0).getName(), "A");
                Assertions.assertTrue(precondition0.getArgument(0) instanceof Literal);
                Assertions.assertEquals(precondition0.getArgument(1).getName(), "T");
                Assertions.assertTrue(precondition0.getArgument(1) instanceof Literal);

                ActionPrecondition precondition1 = previousSteps.get(2).getAction().getPrecondition(1);
                Assertions.assertEquals(precondition1.getPlanStepIndex(), 2);
                Assertions.assertEquals(precondition1.getName(), "on");
                Assertions.assertFalse(precondition1.isNegation());
                Assertions.assertEquals(precondition1.getNumArguments(), 2);
                Assertions.assertEquals(precondition1.getArgument(0).getName(), "B");
                Assertions.assertTrue(precondition1.getArgument(0) instanceof Literal);
                Assertions.assertEquals(precondition1.getArgument(1).getName(), "T");
                Assertions.assertTrue(precondition1.getArgument(1) instanceof Literal);

                ActionPrecondition precondition2 = previousSteps.get(2).getAction().getPrecondition(2);
                Assertions.assertEquals(precondition2.getPlanStepIndex(), 2);
                Assertions.assertEquals(precondition2.getName(), "clear");
                Assertions.assertFalse(precondition2.isNegation());
                Assertions.assertEquals(precondition2.getNumArguments(), 1);
                Assertions.assertEquals(precondition2.getArgument(0).getName(), "A");
                Assertions.assertTrue(precondition2.getArgument(0) instanceof Literal);

                ActionPrecondition precondition3 = previousSteps.get(2).getAction().getPrecondition(3);
                Assertions.assertEquals(precondition3.getPlanStepIndex(), 2);
                Assertions.assertEquals(precondition3.getName(), "clear");
                Assertions.assertFalse(precondition3.isNegation());
                Assertions.assertEquals(precondition3.getNumArguments(), 1);
                Assertions.assertEquals(precondition3.getArgument(0).getName(), "B");
                Assertions.assertTrue(precondition3.getArgument(0) instanceof Literal);

                ActionEffect effect0 = previousSteps.get(2).getAction().getEffect(0);
                Assertions.assertEquals(effect0.getPlanStepIndex(), 2);
                Assertions.assertEquals(effect0.getName(), "on");
                Assertions.assertFalse(effect0.isNegation());
                Assertions.assertEquals(effect0.getNumArguments(), 2);
                Assertions.assertEquals(effect0.getArgument(0).getName(), "B");
                Assertions.assertTrue(effect0.getArgument(0) instanceof Literal);
                Assertions.assertEquals(effect0.getArgument(1).getName(), "A");
                Assertions.assertTrue(effect0.getArgument(1) instanceof Literal);

                ActionEffect effect1 = previousSteps.get(2).getAction().getEffect(1);
                Assertions.assertEquals(effect1.getPlanStepIndex(), 2);
                Assertions.assertEquals(effect1.getName(), "clear");
                Assertions.assertTrue(effect1.isNegation());
                Assertions.assertEquals(effect1.getNumArguments(), 1);
                Assertions.assertEquals(effect1.getArgument(0).getName(), "A");
                Assertions.assertTrue(effect1.getArgument(0) instanceof Literal);

                ActionEffect effect2 = previousSteps.get(2).getAction().getEffect(2);
                Assertions.assertEquals(effect2.getPlanStepIndex(), 2);
                Assertions.assertEquals(effect2.getName(), "on");
                Assertions.assertTrue(effect2.isNegation());
                Assertions.assertEquals(effect2.getNumArguments(), 2);
                Assertions.assertEquals(effect2.getArgument(0).getName(), "B");
                Assertions.assertTrue(effect2.getArgument(0) instanceof Literal);
                Assertions.assertEquals(effect2.getArgument(1).getName(), "T");
                Assertions.assertTrue(effect2.getArgument(1) instanceof Literal);
            }
        } catch (IOException e) {
            throw new Error(e);
        } catch (PlannerUndoableException e) {
            fail("Planner is doable");
        } catch (PlannerCompleteException e) {
            fail("Planner is not complete");
        }
    }

    @Test
    public void BasicStep1TLinks() {
        try {
            FileReader fileReader = new FileReader("src/test/resources/Basic.txt");
            Planner planner = new Planner(fileReader.readOperations(), Planner.toActionEffects(fileReader.readS0()), Planner.toActionPreconditions(fileReader.readSz()));
            planner.planStep(true);
            PlannerList<TLink> tLinks = planner.getTLinks();

            Assertions.assertEquals(tLinks.size(), 1);

            //todo

        } catch (IOException e) {
            throw new Error(e);
        } catch (PlannerUndoableException e) {
            fail("Planner is doable");
        } catch (PlannerCompleteException e) {
            fail("Planner is not complete");
        }
    }

    @Test
    public void BasicStep2TLinks() {
        try {
            FileReader fileReader = new FileReader("src/test/resources/Basic.txt");
            Planner planner = new Planner(fileReader.readOperations(), Planner.toActionEffects(fileReader.readS0()), Planner.toActionPreconditions(fileReader.readSz()));
            planner.planStep(true);
            try {
                planner.planStep(true);
            } catch (PlannerUndoableException e) {
                fail("Planner is doable");
            } catch (PlannerCompleteException e) {
                PlannerList<TLink> tLinks = planner.getTLinks();

                Assertions.assertEquals(tLinks.size(), 1);

                //todo
            }
        } catch (IOException e) {
            throw new Error(e);
        } catch (PlannerUndoableException e) {
            fail("Planner is doable");
        } catch (PlannerCompleteException e) {
            fail("Planner is not complete");
        }
    }

    @Test
    public void BasicPlanStepsComplete() {
        try {
            FileReader fileReader = new FileReader("src/test/resources/Basic.txt");
            Planner planner = new Planner(fileReader.readOperations(), Planner.toActionEffects(fileReader.readS0()), Planner.toActionPreconditions(fileReader.readSz()));
            planner.planSteps();
        } catch (IOException e) {
            throw new Error(e);
        } catch (PlannerUndoableException e) {
            fail("Planner is doable");
        }
    }

    /*@Test
    public void BasicCopyEquals() {
        try {
            FileReader fileReader = new FileReader("src/test/resources/Basic.txt");
            Planner planner = new Planner(fileReader.readOperations(), Planner.toActionEffects(fileReader.readS0()), Planner.toActionPreconditions(fileReader.readSz()));
            Planner copy = new Planner(planner);

            Assertions.assertEquals(planner, copy);

        } catch (IOException e) {
            throw new Error(e);
        }
    }*/

    @Test
    public void BasicGetPlanSteps() {
        try {
            FileReader fileReader = new FileReader("src/test/resources/Basic.txt");
            Planner planner = new Planner(fileReader.readOperations(), Planner.toActionEffects(fileReader.readS0()), Planner.toActionPreconditions(fileReader.readSz()));

            Assertions.assertEquals(planner.getPlanSteps().size(), 2);

            //todo

        } catch (IOException e) {
            throw new Error(e);
        }
    }

    @Test
    public void BasicGetTLink() {
        try {
            FileReader fileReader = new FileReader("src/test/resources/Basic.txt");
            Planner planner = new Planner(fileReader.readOperations(), Planner.toActionEffects(fileReader.readS0()), Planner.toActionPreconditions(fileReader.readSz()));

            PlannerList<TLink> tLinks = planner.getTLinks();
            Assertions.assertEquals(tLinks.size(), 1);

            //todo

        } catch (IOException e) {
            throw new Error(e);
        }
    }

    @Test
    public void BasicGetActions() {
        try {
            FileReader fileReader = new FileReader("src/test/resources/Basic.txt");
            Planner planner = new Planner(fileReader.readOperations(), Planner.toActionEffects(fileReader.readS0()), Planner.toActionPreconditions(fileReader.readSz()));

            PlannerList<Operation> operations = planner.getOperations();
            Assertions.assertEquals(operations.size(), 1);

            Assertions.assertEquals(operations.get(0).getName(), "stack(B,A)");
            Assertions.assertEquals(operations.get(0).getPriority(), 0);
            Assertions.assertEquals(operations.get(0).getNumPreconditions(), 4);
            Assertions.assertEquals(operations.get(0).getNumEffects(), 3);

            Predicate precondition0 = operations.get(0).getPrecondition(0);
            Assertions.assertEquals(precondition0.getName(), "on");
            Assertions.assertFalse(precondition0.isNegation());
            Assertions.assertEquals(precondition0.getNumArguments(), 2);
            Assertions.assertEquals(precondition0.getArgument(0).getName(), "A");
            Assertions.assertTrue(precondition0.getArgument(0) instanceof Literal);
            Assertions.assertEquals(precondition0.getArgument(1).getName(), "T");
            Assertions.assertTrue(precondition0.getArgument(1) instanceof Literal);

            Predicate precondition1 = operations.get(0).getPrecondition(1);
            Assertions.assertEquals(precondition1.getName(), "on");
            Assertions.assertFalse(precondition1.isNegation());
            Assertions.assertEquals(precondition1.getNumArguments(), 2);
            Assertions.assertEquals(precondition1.getArgument(0).getName(), "B");
            Assertions.assertTrue(precondition1.getArgument(0) instanceof Literal);
            Assertions.assertEquals(precondition1.getArgument(1).getName(), "T");
            Assertions.assertTrue(precondition1.getArgument(1) instanceof Literal);

            Predicate precondition2 = operations.get(0).getPrecondition(2);
            Assertions.assertEquals(precondition2.getName(), "clear");
            Assertions.assertFalse(precondition2.isNegation());
            Assertions.assertEquals(precondition2.getNumArguments(), 1);
            Assertions.assertEquals(precondition2.getArgument(0).getName(), "A");
            Assertions.assertTrue(precondition2.getArgument(0) instanceof Literal);

            Predicate precondition3 = operations.get(0).getPrecondition(3);
            Assertions.assertEquals(precondition3.getName(), "clear");
            Assertions.assertFalse(precondition3.isNegation());
            Assertions.assertEquals(precondition3.getNumArguments(), 1);
            Assertions.assertEquals(precondition3.getArgument(0).getName(), "B");
            Assertions.assertTrue(precondition3.getArgument(0) instanceof Literal);

            Predicate effect0 = operations.get(0).getEffect(0);
            Assertions.assertEquals(effect0.getName(), "on");
            Assertions.assertFalse(effect0.isNegation());
            Assertions.assertEquals(effect0.getNumArguments(), 2);
            Assertions.assertEquals(effect0.getArgument(0).getName(), "B");
            Assertions.assertTrue(effect0.getArgument(0) instanceof Literal);
            Assertions.assertEquals(effect0.getArgument(1).getName(), "A");
            Assertions.assertTrue(effect0.getArgument(1) instanceof Literal);

            Predicate effect1 = operations.get(0).getEffect(1);
            Assertions.assertEquals(effect1.getName(), "clear");
            Assertions.assertTrue(effect1.isNegation());
            Assertions.assertEquals(effect1.getNumArguments(), 1);
            Assertions.assertEquals(effect1.getArgument(0).getName(), "A");
            Assertions.assertTrue(effect1.getArgument(0) instanceof Literal);

            Predicate effect2 = operations.get(0).getEffect(2);
            Assertions.assertEquals(effect2.getName(), "on");
            Assertions.assertTrue(effect2.isNegation());
            Assertions.assertEquals(effect2.getNumArguments(), 2);
            Assertions.assertEquals(effect2.getArgument(0).getName(), "B");
            Assertions.assertTrue(effect2.getArgument(0) instanceof Literal);
            Assertions.assertEquals(effect2.getArgument(1).getName(), "T");
            Assertions.assertTrue(effect2.getArgument(1) instanceof Literal);


        } catch (IOException e) {
            throw new Error(e);
        }
    }

    @Test
    public void BasicGetStart() {
        try {
            FileReader fileReader = new FileReader("src/test/resources/Basic.txt");
            Planner planner = new Planner(fileReader.readOperations(), Planner.toActionEffects(fileReader.readS0()), Planner.toActionPreconditions(fileReader.readSz()));

            PlanStep start = planner.getStart();

            Assertions.assertEquals(start.getIndex(), 0);
            Assertions.assertEquals(start.getSubstitutions().size(), 0);

            Action action = start.getAction();

            Assertions.assertEquals(action.getNumPreconditions(), 0);
            Assertions.assertEquals(action.getName(), "A0");
            Assertions.assertEquals(action.getPlanStepIndex(), 0);
            Assertions.assertEquals(action.getNumEffects(), 4);

            PlannerList<ActionEffect> effects = action.getEffects();

            //on(A, T) (ACTION ID: 1)
            Assertions.assertEquals(effects.get(0).getPlanStepIndex(), 0);
            Assertions.assertEquals(effects.get(0).getName(), "on");
            Assertions.assertFalse(effects.get(0).isNegation());
            Assertions.assertEquals(effects.get(0).getNumArguments(), 2);
            Assertions.assertEquals(effects.get(0).getArgument(0).getName(), "A");
            Assertions.assertTrue(effects.get(0).getArgument(0) instanceof Literal);
            Assertions.assertEquals(effects.get(0).getArgument(1).getName(), "T");
            Assertions.assertTrue(effects.get(0).getArgument(1) instanceof Literal);

            //on(B, T) (ACTION ID: 1)
            Assertions.assertEquals(effects.get(1).getPlanStepIndex(), 0);
            Assertions.assertEquals(effects.get(1).getName(), "on");
            Assertions.assertFalse(effects.get(1).isNegation());
            Assertions.assertEquals(effects.get(1).getNumArguments(), 2);
            Assertions.assertEquals(effects.get(1).getArgument(0).getName(), "B");
            Assertions.assertTrue(effects.get(1).getArgument(0) instanceof Literal);
            Assertions.assertEquals(effects.get(1).getArgument(1).getName(), "T");
            Assertions.assertTrue(effects.get(1).getArgument(1) instanceof Literal);

            //clear(A) (ACTION ID: 1)
            Assertions.assertEquals(effects.get(2).getPlanStepIndex(), 0);
            Assertions.assertEquals(effects.get(2).getName(), "clear");
            Assertions.assertFalse(effects.get(2).isNegation());
            Assertions.assertEquals(effects.get(2).getNumArguments(), 1);
            Assertions.assertEquals(effects.get(2).getArgument(0).getName(), "A");
            Assertions.assertTrue(effects.get(2).getArgument(0) instanceof Literal);

            //clear(B) (ACTION ID: 1)
            Assertions.assertEquals(effects.get(3).getPlanStepIndex(), 0);
            Assertions.assertEquals(effects.get(3).getName(), "clear");
            Assertions.assertFalse(effects.get(3).isNegation());
            Assertions.assertEquals(effects.get(3).getNumArguments(), 1);
            Assertions.assertEquals(effects.get(3).getArgument(0).getName(), "B");
            Assertions.assertTrue(effects.get(3).getArgument(0) instanceof Literal);


        } catch (IOException e) {
            throw new Error(e);
        }
    }

    @Test
    public void BasicGetEnd() {
        try {
            FileReader fileReader = new FileReader("src/test/resources/Basic.txt");
            Planner planner = new Planner(fileReader.readOperations(), Planner.toActionEffects(fileReader.readS0()), Planner.toActionPreconditions(fileReader.readSz()));

            PlanStep end = planner.getEnd();

            Assertions.assertEquals(end.getIndex(), 1);
            Assertions.assertEquals(end.getSubstitutions().size(), 0);

            Action action = end.getAction();

            Assertions.assertEquals(action.getNumEffects(), 0);
            Assertions.assertEquals(action.getName(), "AZ");
            Assertions.assertEquals(action.getPlanStepIndex(), 1);
            Assertions.assertEquals(action.getNumPreconditions(), 2);

            PlannerList<ActionPrecondition> effects = action.getPreconditions();

            //on(A, T) (ACTION ID: 2)
            Assertions.assertEquals(effects.get(0).getPlanStepIndex(), 1);
            Assertions.assertEquals(effects.get(0).getName(), "on");
            Assertions.assertFalse(effects.get(0).isNegation());
            Assertions.assertEquals(effects.get(0).getNumArguments(), 2);
            Assertions.assertEquals(effects.get(0).getArgument(0).getName(), "A");
            Assertions.assertTrue(effects.get(0).getArgument(0) instanceof Literal);
            Assertions.assertEquals(effects.get(0).getArgument(1).getName(), "T");
            Assertions.assertTrue(effects.get(0).getArgument(1) instanceof Literal);

            //on(B, A) (ACTION ID: 2)
            Assertions.assertEquals(effects.get(1).getPlanStepIndex(), 1);
            Assertions.assertEquals(effects.get(1).getName(), "on");
            Assertions.assertFalse(effects.get(1).isNegation());
            Assertions.assertEquals(effects.get(1).getNumArguments(), 2);
            Assertions.assertEquals(effects.get(1).getArgument(0).getName(), "B");
            Assertions.assertTrue(effects.get(1).getArgument(0) instanceof Literal);
            Assertions.assertEquals(effects.get(1).getArgument(1).getName(), "A");
            Assertions.assertTrue(effects.get(1).getArgument(1) instanceof Literal);


        } catch (IOException e) {
            throw new Error(e);
        }
    }

    @Test
    public void VariableTest() {
        Planner planner = null;
        try {
            FileReader fileReader = new FileReader("src/test/resources/Variable.txt");
            planner = new Planner(fileReader.readOperations(), Planner.toActionEffects(fileReader.readS0()), Planner.toActionPreconditions(fileReader.readSz()));
            System.out.println(planner);

            planner.planStep(true);
            System.out.println(planner.toStringIdentifier());

        } catch (IOException e) {
            throw new Error(e);
        } catch (PlannerException e) {
            e.printStackTrace();
            System.out.println(planner.toStringIdentifier());
        }
    }
}
