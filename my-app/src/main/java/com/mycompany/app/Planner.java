package com.mycompany.app;

import org.jetbrains.annotations.NotNull;

/**
 * the Planner is the root component of this project and performs most of the logic
 */
public class Planner extends PlannerObject {
    //attributes
    /**
     * list of PlanSteps that are planned currently
     */
    private PlannerList<PlanStep> planSteps;

    /**
     * list of TLinks that are set currently
     */
    private PlannerList<TLink> tLinks;

    /**
     * list of all Actions that are available
     */
    private PlannerList<Operation> operations;

    /**
     * currently open goals that need to get reached
     */
    private PlannerList<ActionPreconditionRef> openGoals;

    /**
     * forbidden goals are a list of Predicates that can't exist in the end
     */
    private PlannerList<ActionPreconditionRef> forbiddenGoals;

    /**
     * list of CLinks that are set currently
     */
    private PlannerList<CLink> cLinks;

    /**
     * already tested actions for every planStep starting from start PlanStep
     */
    private PlannerList<PlannerList<PlannerInteger>> testedOperations;

    private PlannerList<PlannerList<Substitution>> testedSubstitutions;

    /**
     * the past open goals are the open goals of each plan step inserted into the planner
     */
    private PlannerList<PlannerList<ActionPreconditionRef>> pastOpenGoals;

    /**
     * if the next action of the planner should be an undo, undo is true (this is only used when the planStep method is called with returnOnUndo = true)
     */
    private boolean undo;

    /**
     * if the next action of the planner should be an undo, this undoType reports the type of the undo (this is only used when the planStep method is called with returnOnUndo = true)
     */
    private UndoType undoType;

    /**
     * describes hold information about the last execution of planStep/s() or new Planner
     */
    private PlannerList<PlannerString> descriptions;

    /**
     * a graph representation of the temporal links
     */
    private TLinkGraph tLinkGraph;

    /**
     * a graph representation of the plan step index of the causal links
     */
    private CLinkGraph cLinkGraph;

    private PlannerList<PlannerList<CLink>> testedCLinks;

    private final PlannerList<Planner> states;
    private final PlannerList<Planner> undoStates;

    //constructor

    public Planner(@NotNull PlannerList<Operation> operations, @NotNull PlannerList<ActionEffect> startEffects, @NotNull PlannerList<ActionPrecondition> endPreconditions) {
        this.descriptions = new PlannerList<>();
        this.describe("Create a new planner instance.");

        this.describe("Initialize the empty list of all plan steps.");
        this.planSteps = new PlannerList<>();

        this.describe("Add all operations to the list of available operations and sort them for descending priority.");
        this.operations = new PlannerList<>(operations);
        this.operations.sort((a1, a2) -> a2.getPriority() - (a1.getPriority()));
        for (int i = 0; i < this.operations.size();i=i+1) {
            this.operations.get(i).init(i);
        }
        this.operations.forEach(operation -> this.describe(operation.toString()));

        this.describe("Create a new plan step with virtual action A0 with the start state as effects and inserted it into the list of plan step at position 0.");
        this.planSteps.add(new PlanStep(new Action("A0", new PlannerList<>(), new PlannerList<>(startEffects)), new PlannerList<>()));
        this.planSteps.get(0).initIndex(0);
        this.describe(this.planSteps.get(0).toString());

        this.describe("Create a new plan step with virtual action AZ with the end state as preconditions and inserted it into the list of plan step at position 1.");
        this.planSteps.add(new PlanStep(new Action("AZ", new PlannerList<>(endPreconditions), new PlannerList<>()), new PlannerList<>()));
        this.planSteps.get(1).initIndex(1);
        this.describe(this.planSteps.get(1).toString());

        this.describe("Initialize the list of open goals with all non-negative and non-neq predicates of the given end state.");
        this.openGoals = new PlannerList<>();
        PlannerList<ActionPrecondition> preconditions = this.planSteps.get(1).getAction().getPreconditions();
        for (int i = 0; i < preconditions.size(); i++) {
            ActionPrecondition pre = preconditions.get(i);
            if (!pre.isNegation() && !(pre instanceof UnequalActionPrecondition)) {
                this.openGoals.add(new ActionPreconditionRef(i, pre.getPlanStepIndex()));
            }
        }
        this.openGoals.forEach(openGoal -> this.describe(openGoal.toString()));

        this.describe("Initialize the list of forbidden goals with all negative and non-neq predicates of the given end state.");
        this.forbiddenGoals = new PlannerList<>();
        PlannerList<ActionPrecondition> actionPreconditions = this.planSteps.get(1).getAction().getPreconditions();
        for (int i = 0; i < actionPreconditions.size(); i++) {
            ActionPrecondition pre = actionPreconditions.get(i);
            if (pre.isNegation() && !(pre instanceof UnequalActionPrecondition)) {
                this.forbiddenGoals.add(new ActionPreconditionRef(i, pre.getPlanStepIndex()));
            }
        }
        this.forbiddenGoals.forEach(forbiddenGoal -> this.describe(forbiddenGoal.toString()));

        this.describe("Initialize the list of causal links as empty list.");
        this.cLinks = new PlannerList<>();

        this.describe("Initialize the graph of causal links empty.");
        this.cLinkGraph = new CLinkGraph(this.planSteps.size());

        this.describe("Initialize the list of lists of tested operations with one empty list entry for the next picked plan steps operation.");
        this.testedOperations = new PlannerList<>();
        this.testedOperations.add(new PlannerList<>());

        this.describe("Initialize the list of lists of tested causal links with one empty list entry for the next picked plan step.");
        this.testedCLinks = new PlannerList<>();
        this.testedCLinks.add(new PlannerList<>());

        this.describe("Initialize the list of lists of open goals with the newly set open goals.");
        this.pastOpenGoals = new PlannerList<>();
        this.pastOpenGoals.add(new PlannerList<>());
        this.openGoals.forEach(openGoal -> this.pastOpenGoals.get(0).add(openGoal));

        this.describe("Initialize the list of temporal links with one temporal link from A0 to AZ.");
        this.tLinks = new PlannerList<>();
        this.tLinks.add(new TLink(0, 1));
        this.tLinks.forEach(tLink -> this.describe(tLink.toString()));

        this.describe("Initialize the graph of temporal links with one temporal link from A0 to AZ.");
        this.tLinkGraph = new TLinkGraph(this.planSteps.size());
        this.tLinkGraph.addTLink(this.tLinks.get(0));

        this.describe("Set the undo boolean to false, cause next step should search for available actions first.");
        this.undo = false;

        this.describe("Set the type of undo to invalid, cause next step is not an undo.");
        this.undoType = UndoType.invalid;

        this.states = new PlannerList<>();
        this.undoStates = new PlannerList<>();
    }


    public Planner(@NotNull Planner planner) {
        this.planSteps = new PlannerList<>(planner.planSteps);
        this.tLinks = new PlannerList<>(planner.tLinks);
        this.operations = new PlannerList<>(planner.operations);
        this.openGoals = new PlannerList<>(planner.openGoals);
        this.forbiddenGoals = new PlannerList<>(planner.forbiddenGoals);
        this.cLinks = new PlannerList<>(planner.cLinks);
        this.testedOperations = new PlannerList<>(planner.testedOperations);
        this.pastOpenGoals = new PlannerList<>(planner.pastOpenGoals);
        this.undo = planner.undo;
        this.undoType = planner.undoType;
        this.descriptions = new PlannerList<>();
        this.descriptions.addAll(planner.descriptions);
        this.tLinkGraph = new TLinkGraph(planner.tLinkGraph);
        this.cLinkGraph = new CLinkGraph(planner.cLinkGraph);
        this.testedCLinks = new PlannerList<>(planner.testedCLinks);
        this.states = planner.states; // no copy
        this.undoStates = planner.undoStates; // no copy
    }

    //getter

    /**
     * get a copy of the list of all PlanSteps that are planned currently
     *
     * @return copy of the list of all PlanSteps that are planned currently
     */
    public PlannerList<PlanStep> getPlanSteps() {
        return this.planSteps;
    }

    /**
     * get a copy of the list of TLinks that are set currently
     *
     * @return copy of the list of TLinks that are set currently
     */
    public PlannerList<TLink> getTLinks() {
        return this.tLinks;
    }

    /**
     * get a copy of the list of all Actions that are available
     *
     * @return copy of the list of all Actions that are available
     */
    public PlannerList<Operation> getOperations() {
        return this.operations;
    }

    /**
     * get the virtual start PlanStep that is done first of all steps
     *
     * @return virtual start PlanStep that is done first of all steps
     */
    public PlanStep getStart() {
        return this.planSteps.get(0);
    }

    /**
     * get virtual end PlanStep that is done last of all steps
     *
     * @return virtual end PlanStep that is done last of all steps
     */
    public PlanStep getEnd() {
        return this.planSteps.get(1);
    }

    /**
     * get a copy of the currently open goals that need to get reached
     *
     * @return copy of the currently open goals that need to get reached
     */
    public PlannerList<ActionPreconditionRef> getOpenGoals() {
        return this.openGoals;
    }

    /**
     * get a copy of the forbidden goals
     *
     * @return copy of the forbidden goals
     */
    public PlannerList<ActionPreconditionRef> getForbiddenGoals() {
        return this.forbiddenGoals;
    }

    /**
     * get a copy of the list of CLinks that are set currently
     *
     * @return copy of the list of CLinks that are set currently
     */
    public PlannerList<CLink> getCLinks() {
        return this.cLinks;
    }

    /**
     * get the undo boolean, that is true if the next thing to do is an undo
     *
     * @return this undo
     */
    public boolean getUndo() {
        return this.undo;
    }


    /**
     * get the type of the undo that should be done next
     *
     * @return type of the undo
     */
    public UndoType getUndoType() {
        return this.undoType;
    }

    public PlannerList<PlannerString> getDescriptions() {
        return this.descriptions;
    }

    //setter

    //public methods

    public void previousPlanStep() throws PlannerUndoableException {
        this.planSteps = new PlannerList<>(this.states.getLast().planSteps);
        this.tLinks = new PlannerList<>(this.states.getLast().tLinks);
        this.operations = new PlannerList<>(this.states.getLast().operations);
        this.openGoals = new PlannerList<>(this.states.getLast().openGoals);
        this.forbiddenGoals = new PlannerList<>(this.states.getLast().forbiddenGoals);
        this.cLinks = new PlannerList<>(this.states.getLast().cLinks);
        this.testedOperations = new PlannerList<>(this.states.getLast().testedOperations);
        this.pastOpenGoals = new PlannerList<>(this.states.getLast().pastOpenGoals);
        this.undo = this.states.getLast().undo;
        this.undoType = this.states.getLast().undoType;
        this.descriptions = new PlannerList<>();
        this.descriptions.addAll(this.states.getLast().descriptions);
        this.tLinkGraph = new TLinkGraph(this.states.getLast().tLinkGraph);
        this.cLinkGraph = new CLinkGraph(this.states.getLast().cLinkGraph);
        this.testedCLinks = new PlannerList<>(this.states.getLast().testedCLinks);
        this.states.remove(this.states.size()-1);
    }

    /**
     * plan the next step in the plan
     *
     * @param returnOnUndo if true planStep() will return the planned step or parts of it/null if an undo is needed, or null if undo was done
     * @return new step / null if (returnOnUndo) and undo was done
     * @throws PlannerCompleteException if (plan was complete)
     * @throws PlannerUndoableException if (thrown by undoPlanStep)
     */
    public PlanStep planStep(boolean returnOnUndo) throws PlannerCompleteException, PlannerUndoableException {
        this.describe("");

        if (this.undo && returnOnUndo) {
            this.describe("Start undo the before PlanStep.");
            this.undoPlanStep(this.undoType);
            this.undo = false;
            this.undoType = UndoType.invalid;
            return null;
        }

        this.states.add(this);
        this.undoStates.add(this);
        this.describe("Start planning the next PlanStep.");

        this.tryAddCLinks();

        if (this.complete()) {
            throw new PlannerCompleteException("plan already complete");
        }

        PlanStep planStep = null;
        try {
            ActionPreconditionRef selectedGoal = this.selectOpenGoal();
            Operation operation = this.selectGeneratingOperation(selectedGoal);
            Action action = new Action(operation);
            planStep = this.addPlanStep(action);

            if (!this.tryAddCLinks(selectedGoal, planStep.getAction())) {
                throw new Error("inserted action but cant bind causal link");
            }

            this.updateCurrentGoals(planStep.getAction(), selectedGoal);

            this.checkThreats();

            return planStep;
        } catch (PlannerUndoException e) {
            if (returnOnUndo) {
                this.undo = true;
                this.undoType = e.getUndoType();
                return planStep;
            }
            this.undoPlanStep(e.getUndoType());
            return this.planStep(false);
        }
    }

    /**
     * plan steps till plan complete
     *
     * @throws PlannerUndoableException if (thrown by planStep)
     */
    public void planSteps() throws PlannerUndoableException {
        while (!this.complete()) {
            try {
                this.planStep(false);
            } catch (PlannerCompleteException e) {
                break;
            }
        }
    }

    public boolean complete() {
        return this.openGoals.size() == 0;
    }

    public boolean previousStepExists() {
        return this.testedOperations.size() != 1 || this.testedOperations.getLast().size() != 0;
    }

    /**
     * get a string representation of this Planner
     *
     * @return string representation of this Planner
     */
    public String toString() {
        StringBuilder out = new StringBuilder();

        for (int i = 0; i < this.operations.size(); i = i + 1) {
            out.append("OPERATION ").append(i + 1).append(": \r\n");
            out.append(this.operations.get(i).toString(1));
        }

        out.append("START: \r\n").append(this.getStart().toString(1));

        out.append("STEPS: \r\n");
        for (int i = 2; i < this.planSteps.size(); i = i + 1) {
            out.append("-STEP ").append(i + 1).append(": \r\n");
            out.append(this.planSteps.get(i).toString(1));
        }

        out.append("END: \r\n").append(this.getEnd().toString(1));

        out.append("OPEN GOALS: {");
        for (int i = 0; i < this.openGoals.size() - 1; i = i + 1) {
            out.append(this.openGoals.get(i).toString()).append(", ");
        }
        if (!this.openGoals.isEmpty()) {
            out.append(this.openGoals.get(this.openGoals.size() - 1).toString());
        }
        out.append("}\r\n");

        out.append("FORBIDDEN GOALS: {");
        for (int i = 0; i < this.forbiddenGoals.size() - 1; i = i + 1) {
            out.append(this.forbiddenGoals.get(i).toString()).append(", ");
        }
        if (!this.forbiddenGoals.isEmpty()) {
            out.append(this.forbiddenGoals.get(this.forbiddenGoals.size() - 1).toString());
        }
        out.append("}\r\n");

        out.append("CLINKS: {");
        for (int i = 0; i < this.cLinks.size() - 1; i = i + 1) {
            out.append(this.cLinks.get(i).toString()).append(", ");
        }
        if (!this.cLinks.isEmpty()) {
            out.append(this.cLinks.get(this.cLinks.size() - 1).toString());
        }
        out.append("}\r\n");

        out.append("TLINKS: {");
        for (int i = 0; i < this.tLinks.size() - 1; i = i + 1) {
            out.append(this.tLinks.get(i).toString()).append(", ");
        }
        if (!this.tLinks.isEmpty()) {
            out.append(this.tLinks.get(this.tLinks.size() - 1).toString());
        }
        out.append("}\r\n");

        out.append("DESCRIPTIONS: {");
        for (int i = 0; i < this.descriptions.size() - 1; i = i + 1) {
            out.append(this.descriptions.get(i)).append(", ");
        }
        if (!this.descriptions.isEmpty()) {
            out.append(this.descriptions.get(this.descriptions.size() - 1));
        }
        out.append("}\r\n");

        return out.toString();
    }

    public ActionEffect getFromRef(@NotNull ActionEffectRef ref) {
        return this.planSteps.get(ref.getPlanStepIndex()).getAction().getEffects().get(ref.getEffectIndex());
    }

    public ActionPrecondition getFromRef(@NotNull ActionPreconditionRef ref) {
        return this.planSteps.get(ref.getPlanStepIndex()).getAction().getPreconditions().get(ref.getPreconditionIndex());
    }

    @Override
    public PlannerObject copy() {
        return new Planner(this);
    }

    @Override
    public String toStringIdentifier() {
        String out = "[PL" + this.planSteps.toStringIdentifier() + this.tLinks.toStringIdentifier() + this.operations.toStringIdentifier() + this.openGoals.toStringIdentifier() + this.cLinks.toStringIdentifier();
        out = out + this.testedOperations.toStringIdentifier();
        out = out + this.pastOpenGoals.toStringIdentifier();
        if (undo) {
            out = out + "U";
        } else {
            out = out + "-";
        }
        out = out + undoType;
        out = out + descriptions.toStringIdentifier();
        out = out + tLinkGraph.toStringIdentifier();
        out = out + cLinkGraph.toStringIdentifier();
        out = out + "]";
        return out;
    }

    /**
     * another Object is equal to this Planner if it is a Planner and all of its values are the equal
     *
     * @param obj to check if equal
     * @return true/false
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Planner planner)) {
            return false;
        }
        if (!this.operations.equals(planner.operations)) {
            return false;
        }
        if (!this.descriptions.equals(planner.descriptions)) {
            return false;
        }
        if (!this.cLinks.equals(planner.cLinks)) {
            return false;
        }
        if (!this.openGoals.equals(planner.openGoals)) {
            return false;
        }
        if (!this.forbiddenGoals.equals(planner.forbiddenGoals)) {
            return false;
        }
        if (!this.planSteps.equals(planner.planSteps)) {
            return false;
        }
        if (this.undo != planner.undo) {
            return false;
        }
        if (this.undoType != planner.undoType) {
            return false;
        }
        if (!this.tLinks.equals(planner.tLinks)) {
            return false;
        }
        if (!this.pastOpenGoals.equals(planner.pastOpenGoals)) {
            return false;
        }
        return this.testedOperations.equals(planner.testedOperations);
    }

    //public statics

    public static @NotNull PlannerList<ActionEffect> toActionEffects(@NotNull PlannerList<OperationEffect> effects) {
        PlannerList<ActionEffect> actionEffects = new PlannerList<>();
        for (OperationEffect operationEffect : effects) {
            actionEffects.add(new ActionEffect(operationEffect));
        }
        return actionEffects;
    }

    public static @NotNull PlannerList<ActionPrecondition> toActionPreconditions(@NotNull PlannerList<OperationPrecondition> preconditions) {
        PlannerList<ActionPrecondition> actionPreconditions = new PlannerList<>();
        for (OperationPrecondition operationPrecondition : preconditions) {
            if (operationPrecondition instanceof UnequalOperationPrecondition unequalOperationPrecondition) {
                actionPreconditions.add(new UnequalActionPrecondition(unequalOperationPrecondition));
            } else {
                actionPreconditions.add(new ActionPrecondition(operationPrecondition));
            }
        }
        return actionPreconditions;
    }

    //private methods
    private void substitute(@NotNull Substitution substitution) {
        for (PlanStep planStep : this.planSteps) {
            planStep.substitute(substitution);
        }
    }

    private void substitute(@NotNull PlannerList<Substitution> substitutions) {
        for (PlanStep planStep : this.planSteps) {
            planStep.substitute(substitutions);
        }
    }

    /**
     * try adding as many causal links as possible, removes the preconditions connected with a new CLink from open goals
     */
    private void tryAddCLinks() {
        PlannerList<ActionPreconditionRef> doneGoals = new PlannerList<>();

        this.describe("Try to add as many causal links as possible.");
        int addedCLinkNumber = 0;
        for (ActionPreconditionRef goal : this.openGoals) {
            for (PlanStep planStep : this.planSteps) {
                Action action = planStep.getAction();
                if (this.tryAddCLinks(goal, action)) {
                    addedCLinkNumber = addedCLinkNumber + 1;
                    doneGoals.add(goal);
                    break;
                }
            }
        }

        this.describe("Removed all open goals that are now connected to with a causal link.");
        this.openGoals.removeAll(doneGoals);

        this.describe("Added " + addedCLinkNumber + " causal links.");
    }

    /**
     * try adding a causal link from an effect of the given action to the specified goal Predicate, this doesn't remove the goal Predicate from the openGoals
     *
     * @param goal   where the new CLink should point to
     * @param action which should be searched for matching effects for a new CLink
     * @return true if a causal link was found for this goal from any effect of the predicate
     */
    private boolean tryAddCLinks(@NotNull ActionPreconditionRef goal, @NotNull Action action) {
        if (action.getPlanStepIndex() == goal.getPlanStepIndex()) {
            return false;
        }

        //search matching effect
        ActionEffect matchingEff = null;
        for (int i = 0; i < action.getEffects().size(); i++) {
            ActionEffect eff = action.getEffects().get(i);
            ActionPredicate goalPredicate = this.planSteps.get(goal.getPlanStepIndex()).getAction().getPrecondition(goal.getPreconditionIndex());
            if (eff.canSubstitute(goalPredicate,true)) {
                if (!this.testedCLinks.getLast().contains(new CLink(new ActionEffectRef(eff),goal))) {
                    matchingEff = eff;
                }
            }
        }
        if (matchingEff == null) {
            return false;
        }

        //get substitutions
        PlannerList<Substitution> substitutions = matchingEff.calcSubstitution(getFromRef(goal));

        //substitute all preconditions and effects from the plan step of the goal
        this.substitute(substitutions);

        //add substitutions to matching effect plan step
        PlanStep matchingEffectPlanStep = this.planSteps.get(matchingEff.getPlanStepIndex());
        for (Substitution substitution : substitutions) {
            matchingEffectPlanStep.addSubstitution(substitution);
        }

        //add CLink
        CLink cLink = new CLink(new ActionEffectRef(matchingEff), goal);
        this.addCLink(cLink);

        if (this.cLinkGraph.isCyclic()) {
            this.cLinkGraph.removeCLink(this.cLinks.get(this.cLinks.size() - 1));
            this.cLinks.remove(this.cLinks.size() - 1);
            this.describe("Try to add causal link " + cLink + " failed because of cyclic causal links.");
            return false;
        }

        if (this.tLinkGraph.hasPath(cLink.getTo().getPlanStepIndex(), cLink.getFrom().getPlanStepIndex())) {
            this.cLinkGraph.removeCLink(this.cLinks.get(this.cLinks.size() - 1));
            this.cLinks.remove(this.cLinks.size() - 1);
            this.describe("Try to add causal link " + cLink + " failed because of restricting temporal links.");
            return false;
        }

        this.describe("Added causal link: " + cLink + ".");
        return true;
    }

    /**
     * the first openGoal will be picked (planStep makes sure that this won't be called with an empty openGoals list)
     *
     * @return the selected Goal predicate
     */
    private ActionPreconditionRef selectOpenGoal() {
        this.describe("Selected " + this.openGoals.get(0) + " as current goal predicate.");
        return this.openGoals.get(0);
    }

    /**
     * Select the action with the greatest priority generating the given goal, that has not been tested already in this exact situation
     *
     * @param goal to generate
     * @return the Action that is found
     * @throws PlannerUndoException if (no action is available)
     */
    private @NotNull Operation selectGeneratingOperation(@NotNull ActionPreconditionRef goal) throws PlannerUndoException {
        this.describe("Searched for available not yet tested action to generate the current goal.");
        for (int i = 0; i < this.operations.size(); i++) {
            if (this.testedOperations.get(testedOperations.size() - 1).contains(new PlannerInteger(i))) {
                continue;
            }

            for (OperationEffect effect : this.operations.get(i).getEffects()) {
                ActionPredicate goalPredicate = getFromRef(goal);
                if (effect.canSubstitute(goalPredicate,true)) {
                    this.testedOperations.get(testedOperations.size() - 1).add(new PlannerInteger(i));
                    this.testedOperations.add(new PlannerList<>());
                    this.testedCLinks.add(new PlannerList<>());
                    Operation selectedOperation = new Operation(this.operations.get(i).getName(), this.operations.get(i).getPriority(), this.operations.get(i).getPreconditions(), this.operations.get(i).getEffects());
                    this.describe("Selected " + selectedOperation + " as generating action.");
                    return selectedOperation;
                }
            }
        }
        this.describe("No generating action could be found for " + goal + ".");
        throw new PlannerUndoException(UndoType.noAvailableOperation, "no available not yet tested operation found");
    }

    /**
     * create a new PlanStep with a given Action and add it in the previous steps
     *
     * @param action to do in the PlanStep
     * @return the new PlanStep
     */
    private PlanStep addPlanStep(@NotNull Action action) {
        PlanStep planStep = new PlanStep(action, new PlannerList<>());
        planStep.initIndex(this.planSteps.size());
        this.planSteps.add(planStep);
        this.cLinkGraph.setPlanStepNum(this.planSteps.size());
        this.tLinkGraph.setPlanStepNum(this.planSteps.size());
        planStep.initIndex(planSteps.size() - 1);
        this.describe("Added " + planStep + " to the list of previous PlanSteps.");
        return this.planSteps.getLast();
    }

    /**
     * remove the goal that is generated by the action of the new PlanStep from the openGoals and add all preconditions of this Action to the openGoals
     * check if the new openGoals are a subset of any previous openGoals
     * add the new openGoals to the previous openGoals
     *
     * @param action       that generated the selected goal
     * @param selectedGoal to generated
     * @throws PlannerUndoException if (the new openGoals are a subset of any previous openGoals)
     */
    private void updateCurrentGoals(@NotNull Action action, @NotNull ActionPreconditionRef selectedGoal) throws PlannerUndoException {
        this.describe("Removed the selected goal " + selectedGoal + " from the list of open goals.");
        this.openGoals.remove(selectedGoal);
        this.describe("Added all non-negative and non-neq preconditions of the new PlanSteps Action to the current openGoals.");
        for (int i = 0; i < action.getPreconditions().size(); i = i + 1) {
            if (!action.getPrecondition(i).isNegation() && !(action.getPrecondition(i) instanceof UnequalActionPrecondition)) {
                this.openGoals.add(new ActionPreconditionRef(i, action.getPlanStepIndex()));
            }
        }
        for (PlannerList<ActionPreconditionRef> pastOpenGoal : this.pastOpenGoals) {
            if (this.subsetEqGoals(pastOpenGoal)) {
                this.describe("The current open goals are a subset of previous open goals, so an undo is needed.");
                throw new PlannerUndoException(UndoType.subsetGoals, "past goals are a subset or equal to open goals");
            }
        }
        this.describe("Added the open goals list to the previous open goals list.");
        this.pastOpenGoals.add(new PlannerList<>());
        this.openGoals.forEach((goal) -> this.pastOpenGoals.get(this.pastOpenGoals.size() - 1).add(new ActionPreconditionRef(goal)));
    }

    /**
     * check if the openGoals are a subset of given pastGoals
     *
     * @param pastGoals to check
     * @return true if this.openGoals is a subset of the given pastGoals
     */
    private boolean subsetEqGoals(@NotNull PlannerList<ActionPreconditionRef> pastGoals) {
        PlannerList<ActionPrecondition> goals = new PlannerList<>();
        for (ActionPreconditionRef goal : this.openGoals) {
            goals.add(this.getFromRef(goal));
        }

        for (ActionPreconditionRef pastGoal : pastGoals) {
            boolean contains = false;
            for (ActionPrecondition goal : goals) {
                if (getFromRef(pastGoal).canSubstituteTo(goal)) {
                    contains = true;
                    break;
                }
            }
            if (!contains) {
                return false;
            }
        }
        return true;
    }

    /**
     * check for threats with the new PlanStep
     * add TLinks if needed
     * check is TLinks building a circle with a directed graph
     *
     * @throws PlannerUndoException if (the TLinks build a circle)
     */
    //todo negated preconditions
    private void checkThreats() throws PlannerUndoException {
        PlanStep planStep = this.planSteps.getLast();

        this.describe("Searched for threats in the current plan.");
        int tLinkNumber = 0;
        for (Predicate eff : planStep.getAction().getEffects()) {
            if (eff.isNegation()) {
                for (PlanStep step : this.planSteps) {
                    if (planStep.equals(step)) {
                        continue;
                    }
                    for (Predicate pre : step.getAction().getPreconditions()) {
                        if (eff.equalsValue(pre)) {
                            TLink tLink = new TLink(step.getIndex(), planStep.getIndex());
                            this.describe("Added temporal link " + tLink + ".");
                            this.addTLink(tLink);
                            tLinkNumber = tLinkNumber + 1;
                        }
                    }
                }
            } else {
                //todo
            }
        }
        for (Predicate pre : planStep.getAction().getPreconditions()) {
            if (!pre.isNegation()) {
                for (PlanStep step : this.planSteps) {
                    if (planStep.equals(step)) {
                        continue;
                    }
                    for (Predicate eff : step.getAction().getEffects()) {
                        if (eff.isNegation()) {
                            if (pre.equalsValue(eff)) {
                                TLink tLink = new TLink(planStep.getIndex(), step.getIndex());
                                this.describe("Added temporal link " + tLink + ".");
                                this.addTLink(tLink);
                                tLinkNumber = tLinkNumber + 1;
                            }
                        }
                    }
                }
            } else {
                //todo
            }
        }

        if (tLinkGraph.isCyclic()) {
            this.describe("The added temporal links form a cycle, so an undo is needed.");
            throw new PlannerUndoException(UndoType.invalidThreats, "cyclic temporal links");
        }

        DirectedGraph directedGraph = new DirectedGraph(this.planSteps.size());
        for (TLink tLink : this.tLinks) {
            directedGraph.addEdge(tLink.getBeforePlanStepIndex(),tLink.getAfterPlanStepIndex());
        }
        for (CLink cLink : this.cLinks) {
            directedGraph.addEdge(cLink.getFrom().getPlanStepIndex(), cLink.getTo().getPlanStepIndex());
        }
        if (directedGraph.isCyclic()) {
            this.describe("The added temporal links and causal links form a cycle, so an undo is needed.");
            throw new PlannerUndoException(UndoType.invalidThreats, "cyclic temporal and causal links");
        }

        this.describe("Added " + tLinkNumber + " temporal links.");
    }

    private void undoPlanStep(@NotNull UndoType undoType) throws PlannerUndoableException {
        //todo clear maybe
        if (this.undoStates.size() == 0) {
            throw new PlannerUndoableException("Plan is undoable");
        }
        if (undoType == UndoType.noAvailableOperation) {
            this.undoStates.removeLast();
            if (this.undoStates.size() == 0) {
                throw new PlannerUndoableException("Plan is undoable");
            }
        }
        this.describe("Undo Plan Step");
        this.planSteps = new PlannerList<>(this.undoStates.getLast().planSteps);
        this.tLinks = new PlannerList<>(this.undoStates.getLast().tLinks);
        this.operations = new PlannerList<>(this.undoStates.getLast().operations);
        this.openGoals = new PlannerList<>(this.undoStates.getLast().openGoals);
        this.forbiddenGoals = new PlannerList<>(this.undoStates.getLast().forbiddenGoals);
        this.cLinks = new PlannerList<>(this.undoStates.getLast().cLinks);
        this.pastOpenGoals = new PlannerList<>(this.undoStates.getLast().pastOpenGoals);
        this.undo = this.undoStates.getLast().undo;
        this.undoType = this.undoStates.getLast().undoType;
        this.tLinkGraph = new TLinkGraph(this.undoStates.getLast().tLinkGraph);
        this.cLinkGraph = new CLinkGraph(this.undoStates.getLast().cLinkGraph);
        this.testedOperations.removeLast();
        this.testedCLinks.removeLast();
        this.undoStates.removeLast();
        this.states.add(this);
    }

    /**
     * add a copy of a TLink to the currently set tLinks
     *
     * @param tLink to add
     */
    private void addTLink(@NotNull TLink tLink) {
        TLink add = new TLink(tLink);
        this.tLinks.add(add);
        this.tLinkGraph.addTLink(add);
    }

    /**
     * add a copy of a CLink to the currently set cLinks
     *
     * @param cLink to add
     * @throws IllegalArgumentException if (cLink == null)
     */
    private void addCLink(@NotNull CLink cLink) {
        CLink add = new CLink(cLink);
        this.cLinks.add(add);
        this.cLinkGraph.addCLink(add);
        this.testedCLinks.getLast().add(cLink);
    }

    private void describe(@NotNull String description) {
        this.descriptions.add(new PlannerString(description));
    }
}

