package com.mycompany.app;

import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class JSONOutput {
    //attributes
    private final Planner planner;
    private final boolean undoable;

    //constructor
    public JSONOutput(@NotNull Planner planner, boolean undoable) {
        this.planner = planner;
        this.undoable = undoable;
    }

    //public attributes
    public JSONObject toJSON() {
        PlannerList<PlanStep> steps = new PlannerList<>(planner.getPlanSteps());

        int size = steps.size();

        String[][] actionsPreconditions = new String[size][];
        String[][] actionsEffects = new String[size][];
        String[] actionsName = new String[size];
        boolean[] cLinksFromPre = new boolean[this.planner.getCLinks().size()];
        int[] cLinksFrom = new int[this.planner.getCLinks().size()];
        int[] cLinksFromAction = new int[this.planner.getCLinks().size()];
        boolean[] cLinksToPre = new boolean[this.planner.getCLinks().size()];
        int[] cLinksTo = new int[this.planner.getCLinks().size()];
        int[] cLinksToAction = new int[this.planner.getCLinks().size()];
        int[] tLinksFrom = new int[this.planner.getTLinks().size()];
        int[] tLinksTo = new int[this.planner.getTLinks().size()];

        for (int i = 0; i < steps.size(); i = i + 1) {
            Action action = steps.get(i).getAction();
            actionsName[i] = action.getName() + "(";
            PlannerList<Variable> variables = action.calcVariableSet();
            for (int j = 0; j < variables.size() - 1; j = j + 1) {
                actionsName[i] = actionsName[i] + variables.get(j).getName() + ", ";
            }
            if (variables.size() > 0) {
                actionsName[i] = actionsName[i] + variables.get(variables.size() - 1).getName();
            }
            actionsName[i] = actionsName[i] + ")";

            PlannerList<ActionPrecondition> preconditions = action.getPreconditions();
            actionsPreconditions[i] = preconditionsToStrings(preconditions);

            PlannerList<ActionEffect> effects = action.getEffects();
            actionsEffects[i] = effectsToStrings(effects);

        }

        for (int i = 0; i < this.planner.getCLinks().size(); i = i + 1) {
            CLink cLink = this.planner.getCLinks().get(i);


            cLinksFromPre[i] = false;
            cLinksToPre[i] = true;

            int from = -1;
            int to = -1;
            for (int j = 0; j < steps.size(); j = j + 1) {
                if (steps.get(j).getAction().getPlanStepIndex() == cLink.getFrom().getPlanStepIndex()) {
                    from = j;
                }
                if (steps.get(j).getAction().getPlanStepIndex() == cLink.getTo().getPlanStepIndex()) {
                    to = j;
                }
            }
            cLinksFromAction[i] = from;
            cLinksToAction[i] = to;

            cLinksFrom[i] = cLink.getFrom().getEffectIndex();
            cLinksTo[i] = cLink.getTo().getPreconditionIndex();
        }

        for (int i = 0; i < this.planner.getTLinks().size(); i = i + 1) {
            TLink tLink = this.planner.getTLinks().get(i);
            tLinksFrom[i] = tLink.getBeforePlanStepIndex();
            tLinksTo[i] = tLink.getAfterPlanStepIndex();
        }

        Map<String, Object> out = new HashMap<>();
        out.put("actionsPreconditions", actionsPreconditions);
        out.put("actionsEffects", actionsEffects);
        out.put("actionsName", actionsName);
        out.put("cLinksFromPre", cLinksFromPre);
        out.put("cLinksFrom", cLinksFrom);
        out.put("cLinksFromAction", cLinksFromAction);
        out.put("cLinksToPre", cLinksToPre);
        out.put("cLinksTo", cLinksTo);
        out.put("cLinksToAction", cLinksToAction);
        out.put("tLinksFrom", tLinksFrom);
        out.put("tLinksTo", tLinksTo);
        out.put("complete", this.planner.complete());
        out.put("undoable", this.undoable);
        out.put("undo", this.planner.getUndo());
        out.put("previousStepExists", this.planner.previousStepExists());
        if (this.planner.getUndo()) {
            switch (this.planner.getUndoType()) {
                case noAvailableOperation -> out.put("undoType", "noAvailableAction");
                case subsetGoals -> out.put("undoType", "subsetGoals");
                case invalidThreats -> out.put("undoType", "invalidThreats");
                default -> throw new IllegalStateException("Unexpected value: " + this.planner.getUndoType());
            }
        } else {
            out.put("undoType", "noUndo");
        }
        String[] descriptions = new String[this.planner.getDescriptions().size()];
        for (int i = 0; i < this.planner.getDescriptions().size(); i = i + 1) {
            descriptions[i] = this.planner.getDescriptions().get(i).toString();
        }
        out.put("descriptions", descriptions);

        return new JSONObject(out);
    }

    //private
    private String @NotNull [] effectsToStrings(@NotNull PlannerList<ActionEffect> predicates) {
        String[] rt = new String[predicates.size()];
        for (int i = 0; i < predicates.size(); i = i + 1) {
            StringBuilder out = new StringBuilder();
            if (predicates.get(i).isNegation()) {
                out.append("¬");
            }
            out.append(predicates.get(i).getName());
            out.append("(");
            for (int j = 0; j < predicates.get(i).getArguments().size(); j = j + 1) {

                out.append(predicates.get(i).getArgument(j).toString());

                if (j < predicates.get(i).getArguments().size() - 1) {
                    out.append(",");
                }
            }
            out.append(")");
            rt[i] = out.toString();
        }
        return rt;
    }

    private String @NotNull [] preconditionsToStrings(@NotNull PlannerList<ActionPrecondition> predicates) {
        String[] rt = new String[predicates.size()];
        for (int i = 0; i < predicates.size(); i = i + 1) {
            StringBuilder out = new StringBuilder();
            if (predicates.get(i).isNegation()) {
                out.append("¬");
            }
            out.append(predicates.get(i).getName());
            out.append("(");
            for (int j = 0; j < predicates.get(i).getArguments().size() ; j = j + 1) {

                out.append(predicates.get(i).getArgument(j).toString());

                if (j < predicates.get(i).getArguments().size()-1) {
                    out.append(",");
                }
            }
            out.append(")");
            rt[i] = out.toString();
        }
        return rt;
    }
}
