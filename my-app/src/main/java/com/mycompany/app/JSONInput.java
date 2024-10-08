package com.mycompany.app;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * a JSONInput is constructed of a json String and converts its data to a Planner instance
 */
public class JSONInput {
    //attributes
    /**
     * the json data as String passed in the constructor
     */
    private final String json;

    //constructor

    /**
     * construct a new JSONInput from json String
     *
     * @param json with the data as String
     */
    public JSONInput(@NotNull String json) {
        this.json = json;
    }

    /**
     * convert the data to a planner instance
     *
     * @return the planner instance filled with data from json string
     */
    public Planner toPlanner() {
        JSONObject obj = new JSONObject(this.json);

        Map<String, Predicate> predicateMap = new HashMap<>();
        JSONArray predicatesArray = obj.getJSONArray("predicates");
        for (int i = 0; i < predicatesArray.length(); i = i + 1) {
            JSONArray predicateArray = predicatesArray.getJSONArray(i);
            String id = predicateArray.getString(0);
            JSONObject predicate = predicateArray.getJSONObject(1);
            String name = predicate.getString("name");
            boolean negation = predicate.getBoolean("negation");
            int numArgs = predicate.getInt("numArgs");
            JSONArray argsArray = predicate.getJSONArray("args");
            PlannerList<PlannerString> args = new PlannerList<>();
            for (int j = 0; j < numArgs; j = j + 1) {
                args.add(new PlannerString(argsArray.getString(j)));
            }
            JSONArray kindArray = predicate.getJSONArray("kind");
            PlannerList<PlannerString> kind = new PlannerList<>();
            for (int j = 0; j < numArgs; j = j + 1) {
                kind.add(new PlannerString(kindArray.getString(j)));
            }

            PlannerList<Argument> arguments = new PlannerList<>();
            for (int j = 0; j < numArgs; j = j + 1) {
                if (kind.get(j).equals("literal")) {
                    arguments.add(new Literal(args.get(j).toString()));
                } else {
                    arguments.add(new Variable(args.get(j).toString()));
                }
            }
            predicateMap.put(id, new ActionEffect(name, negation, arguments)); //todo
        }

        PlannerList<Operation> operations = new PlannerList<>();
        JSONArray actionsArray = obj.getJSONArray("actions");
        for (int i = 0; i < actionsArray.length(); i = i + 1) {
            JSONArray actionArray = actionsArray.getJSONArray(i);

            JSONObject action = actionArray.getJSONObject(1);
            String name = action.getString("name");
            int priority = action.getInt("priority");

            int numPre = action.getInt("numPre");
            JSONArray presArray = action.getJSONArray("pre");
            PlannerList<OperationPrecondition> predicatesPre = new PlannerList<>();
            for (int j = 0; j < numPre; j = j + 1) {
                predicatesPre.add(new OperationPrecondition(predicateMap.get(presArray.getString(j)).getName(), predicateMap.get(presArray.getString(j)).isNegation(), predicateMap.get(presArray.getString(j)).getArguments()));
            }

            int numEff = action.getInt("numEff");
            JSONArray effsArray = action.getJSONArray("eff");
            PlannerList<OperationEffect> predicatesEff = new PlannerList<>();
            for (int j = 0; j < numEff; j = j + 1) {
                predicatesEff.add(new OperationEffect(predicateMap.get(effsArray.getString(j)).getName(), predicateMap.get(effsArray.getString(j)).isNegation(), predicateMap.get(effsArray.getString(j)).getArguments()));
            }

            int numNeq = action.getInt("numNeq");
            JSONArray numNeqArgs = action.getJSONArray("numNeqArgs");
            JSONArray neqArgs = action.getJSONArray("neqArgs");
            for (int j = 0; j < numNeq;j=j+1) {
                PlannerList<Argument> arguments = new PlannerList<>();
                for (int k = 0; k < numNeqArgs.getInt(j);k=k+1) {
                    String arg = neqArgs.getJSONArray(j).getString(k);
                    if (arg.charAt(0) <= 'z' && arg.charAt(0) >= 'a') {
                        arguments.add(new Variable(arg));
                    } else {
                        arguments.add(new Literal(arg));
                    }
                }
                predicatesPre.add(new UnequalOperationPrecondition(arguments));
            }

            operations.add(new Operation(name, priority, predicatesPre, predicatesEff));
        }

        JSONArray start = obj.getJSONArray("start");
        PlannerList<ActionEffect> startPredicates = new PlannerList<>();
        for (int i = 0; i < start.length(); i = i + 1) {
            startPredicates.add(new ActionEffect(predicateMap.get(start.getString(i)).getName(), predicateMap.get(start.getString(i)).isNegation(), predicateMap.get(start.getString(i)).getArguments()));
        }

        JSONArray end = obj.getJSONArray("end");
        PlannerList<ActionPrecondition> endPredicates = new PlannerList<>();
        for (int i = 0; i < end.length(); i = i + 1) {
            endPredicates.add(new ActionPrecondition(predicateMap.get(end.getString(i)).getName(), predicateMap.get(end.getString(i)).isNegation(), predicateMap.get(end.getString(i)).getArguments()));
        }

        return new Planner(operations, startPredicates, endPredicates);
    }
}
