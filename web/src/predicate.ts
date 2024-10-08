import type {Action} from "@/action";
import {calcActionResult, validateAction} from "@/action";
import {numbersToString} from "@/utility";

export interface Predicate {
    result: string,
    resultClass: string,
    id: string,
    name: string,
    nameClass: string,
    negation: boolean,
    numArgs: string,
    numArgsClass: string,
    args: string[],
    argsClass: string[],
    kind: string[],
    literal: string[],
    variable: string[],
    valid: boolean,
    validStatus: string,
}

export const resultInput = function (event: Event, actions: Map<String, Action>, predicates: Map<String, Predicate>, predicate: Predicate,start: string[],end: string[]) {
    let value = ((event.target as HTMLInputElement).value);
    predicate.result = value;
    if (value.search(new RegExp('^¬?[a-zA-Z0-9]+\\(([a-zA-Z0-9]+, ?)*[a-zA-Z0-9]+\\)$')) == 0) {
        value = value.replace(/ /g, "");
        value = value.replace(/\)/g, "|");
        value = value.replace(/\(/g, "|");
        let parts = value.split("|");
        if (parts[0].includes("¬")) {
            predicate.negation = true;
            parts[0] = parts[0].replace(/¬/g, "");
        } else {
            predicate.negation = false;
        }
        predicate.name = parts[0]
        let args = parts[1].split(",")
        predicate.args = Object.assign([], args);
        predicate.numArgs = args.length.toString();
        for (let i = 0; i < args.length; i = i + 1) {
            if (args[i].charAt(0) >= 'A' && args[i].charAt(0) <= 'Z' || args[i].charAt(0) >= '0' && args[i].charAt(0) <= '9') {
                predicate.literal[i] = "literal";
                predicate.variable[i] = "";
                predicate.kind[i] = "literal";
            } else {
                predicate.variable[i] = "variable";
                predicate.literal[i] = "";
                predicate.kind[i] = "variable";
            }
        }

        predicates.forEach((value, key) => {
            validatePredicate(actions, predicates, value, key.toString(),start,end);
            propagateChanges(actions, predicates, value, key.toString());
        });
    }
}

export const calcResult = function (actions: Map<String, Action>, predicates: Map<String, Predicate>, predicate: Predicate,start: string[],end: string[]) {
    let name = predicate.name;
    if (name == "") {
        name = "name";
    }

    let argsString = "";
    if (Number(predicate.numArgs) > 0) {
        predicate.args.slice(0, Number(predicate.numArgs) - 1).forEach((arg) => argsString = argsString + arg + ", ");
        argsString = argsString + predicate.args[Number(predicate.numArgs) - 1];
    }

    let negation = "";
    if (predicate.negation) {
        negation = "¬"
    }
    predicate.result = negation + name + "(" + argsString + ")";

    predicates.forEach((value, key) => {
        validatePredicate(actions, predicates, value, key.toString(),start,end);
        propagateChanges(actions, predicates, value, key.toString());
    });
}

export const propagateChanges = function (actions: Map<String, Action>, predicates: Map<String, Predicate>, predicate: Predicate, id: string) {
    const actionIds: string[] = [];
    actions.forEach((value, key) => {
        value.eff.forEach((eff) => {
            if (eff == id) {
                if (!actionIds.includes(key.toString())) {
                    actionIds.push(key.toString())
                }
            }
        });
        value.pre.forEach((pre) => {
            if (pre == id) {
                if (!actionIds.includes(key.toString())) {
                    actionIds.push(key.toString())
                }
            }
        })
    });
    actionIds.forEach((actionId) => {
        calcActionResult(actions, predicates, actions.get(actionId)!);
        validateAction(actions, predicates, actions.get(actionId)!, actionId);
    });
}

export const validatePredicate = function (actions: Map<String, Action>, predicates: Map<String, Predicate>, predicate: Predicate, id: string, start: string[], end: string[]) {
    //INIT ALL VALID
    let valid = true;
    let resultClass = "valid";
    let validStatus = "";
    let nameClass = "valid";
    let numArgsClass = "valid";
    let argsClass = [] as string[];
    predicate.args.forEach(() => argsClass.push("valid"));

    //NAME
    if (predicate.name.search(new RegExp('^[a-zA-Z0-9]+$')) !== 0) {
        nameClass = "invalid";
        valid = false;
        resultClass = "invalid";
        validStatus = "name: {aA0-zZ9, length > 0}";
    }

    //NUM ARGS
    if (predicate.numArgs == undefined || predicate.numArgs.search(new RegExp('^[0-9]+$')) != 0 || Number(predicate.numArgs) < 0 || Number(predicate.numArgs) > 25 || isNaN(Number(predicate.numArgs))) {
        valid = false;
        resultClass = "invalid";
        numArgsClass = "invalid";
        if (validStatus.length > 0) {
            validStatus = validStatus + " & number of arguments: {0-25}";
        } else {
            validStatus = "number of arguments: {0-25}";
        }
    }

    //ARGS
    let indexes = [] as number[];
    for (let i = 0; i < Number(predicate.numArgs); i = i + 1) {
        if (predicate.args[i] == undefined || predicate.args[i].search(new RegExp('^[a-zA-Z0-9]+$')) != 0) {
            valid = false;
            resultClass = "invalid"
            argsClass[i] = "invalid"
            indexes.push(i);
        }
    }
    if (indexes.length > 0) {
        if (validStatus.length > 0) {
            validStatus = validStatus + " & argument ";

            validStatus = validStatus + ": {aA0-zZ9, x -> variable, X -> literal}";
        } else {
            validStatus = "argument " + numbersToString(indexes) + ": {aA0-zZ9, x -> variable, X -> literal}";
        }
    }

    //DUPLICATES
    let duplicateCounter = 0;
    predicates.forEach((value, key) => {
        if (Number(key.slice(1, key.length)) < Number(id.slice(1, id.length))) {
            if (value.name == predicate.name && value.numArgs == predicate.numArgs && value.negation == predicate.negation) {
                let argsEqual = true;
                for (let i = 0; i < value.args.length; i = i + 1) {
                    if (value.args[i] != predicate.args[i]) {
                        argsEqual = false;
                    }
                }
                if (argsEqual) {
                    duplicateCounter = duplicateCounter + 1;
                }
            }
        }
    });
    if (duplicateCounter > 0) {
        valid = false;
        resultClass = "invalid";
        if (validStatus.length > 0) {
            validStatus = validStatus + " & duplicate (x" + duplicateCounter.toString() + ")";
        } else {
            validStatus = "duplicate (x" + duplicateCounter.toString() + ")";
        }
    }

    //NEGATION IN START
    if (start.includes(id) && predicate.negation) {
        valid = false;
        resultClass = "invalid";
        if (validStatus.length > 0) {
            validStatus = validStatus + " & invalid negation in start";
        } else {
            validStatus = "invalid negation in start";
        }
    }

    //SET VALID STATES
    predicate.nameClass = nameClass;
    predicate.numArgsClass = numArgsClass;
    argsClass.forEach((clas, index) => predicate.argsClass[index] = clas);
    predicate.valid = valid;
    predicate.resultClass = resultClass;
    predicate.validStatus = validStatus;
}