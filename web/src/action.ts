import type {Predicate} from "@/predicate";
import {validatePredicate} from "@/predicate";
import {numbersToString} from "@/utility";

export interface Action {
    result: string,
    resultPre: string,
    resultEff: string,
    id: string,
    name: string,
    nameClass: string,
    priority: string,
    priorityClass: string,
    numPre: string,
    numPreClass: string,
    numEff: string,
    numEffClass: string,
    pre: string[],
    preClass: string[],
    eff: string[],
    effClass: string[],
    valid: boolean,
    validStatus: string,
    numNeq: string,
    numNeqArgs: string[],
    neqArgs: string[][],
    numNeqClass: string,
    numNeqArgsClass: string[],
    neqArgsClass: string[][],
}

export const validateAll = function (actions: Map<String, Action>, predicates: Map<String, Predicate>,start: string[],end: string[]) {
    predicates.forEach((value) => {
        validatePredicate(actions,predicates,value,value.id,start,end);
    });
    actions.forEach((value) => {
       validateAction(actions,predicates,value,value.id);
    });
}

export const allValid = function (actions: Map<String, Action>, predicates: Map<String, Predicate>): boolean {
    let valid = true;
    predicates.forEach((value) => {
        if (!value.valid) {
            valid = false;
        }
    });
    actions.forEach((value) => {
        if (!value.valid) {
            valid = false;
        }
    })
    return valid;
}

export const calcActionResult = function (actions: Map<String, Action>, predicates: Map<String, Predicate>, action: Action) {
    let name = action.name;
    if (name == "") {
        name = "name";
    }

    let args = [] as string[];
    action.pre.forEach((preId) => {
        if (predicates.has(preId)) {
            predicates.get(preId)!.args.forEach((arg, index) => {
                if (predicates.get(preId)!.kind[index] == "variable") {
                    if (!args.includes(arg)) {
                        args.push(arg)
                    }
                }
            })
        }
    });
    action.eff.forEach((preId) => {
        if (predicates.has(preId)) {
            predicates.get(preId)!.args.forEach((arg, index) => {
                if (predicates.get(preId)!.kind[index] == "variable") {
                    if (!args.includes(arg)) {
                        args.push(arg)
                    }
                }
            })
        }
    });
    let argsString = "";
    if (args.length > 0) {
        args.slice(0, args.length-1).forEach((arg) => argsString = argsString + arg + ", ");
        argsString = argsString + args[args.length - 1];
    }
    action.result = name + "(" + argsString + ")";

    calcActionResultPre(actions, predicates, action);
    calcActionResultEff(actions, predicates, action);
    actions.forEach((value) => validateAction(actions, predicates, value, value.id));
}

export const calcActionResultPre = function (actions: Map<String, Action>, predicates: Map<String, Predicate>, action: Action) {
    let preString = "";
    let preDub = [] as string[];
    if (parseInt(action.numPre) > 0) {
        action.pre.slice(0, parseInt(action.numPre)  - 1).forEach((pre) => {
            if (!preDub.includes(pre) && predicates.has(pre)) preString = preString + predicates.get(pre)!.result + ", ";
            preDub.push(pre)
        });
        let last = action.pre[parseInt(action.numPre) - 1];
        if (!preDub.includes(last) && predicates.has(last)) {
            preString = preString + predicates.get(last)!.result;
        } else if (preString.length > 2) {
            preString = preString.slice(0, preString.length - 2);
        }
    }
    for (let i = 0; i < parseInt(action.numNeq);i=i+1) {
        let unequal = "≠(";
        for (let j = 0; j < parseInt(action.numNeqArgs[i]);j=j+1) {
            if (unequal == "≠(") {
                unequal = unequal + action.neqArgs[i][j];
            } else {
                unequal = unequal + ", " + action.neqArgs[i][j];
            }
        }
        unequal = unequal + ")";

        if (preString == "") {
            preString = preString + unequal;
        } else {
            preString = preString + ", " + unequal;
        }
    }

    action.resultPre = "{" + preString + "}";
}

export const calcActionResultEff = function (actions: Map<String, Action>, predicates: Map<String, Predicate>, action: Action) {
    let preString = "";
    let preDub = [] as string[];
    if (parseInt(action.numEff) > 0) {
        action.eff.slice(0, parseInt(action.numEff) - 1).forEach((pre) => {
            if (!preDub.includes(pre) && predicates.has(pre)) preString = preString + predicates.get(pre)!.result + ", ";
            preDub.push(pre)
        });
        let last = action.eff[parseInt(action.numEff) - 1];
        if (!preDub.includes(last) && predicates.has(last)) {
            preString = preString + predicates.get(last)!.result;
        } else if (preString.length > 2) {
            preString = preString.slice(0, preString.length - 2);
        }
    }
    action.resultEff = "{" + preString + "}";
}

export const validateAction = function (actions: Map<String, Action>, predicates: Map<String, Predicate>, action: Action, id: string) {
    //INIT ALL VALID
    let valid = true;
    let validStatus = "";
    let nameClass = "valid";
    let priorityClass = "valid";
    let numPreClass = "valid";
    let numEffClass = "valid";
    let preClass = [] as string[];
    action.pre.forEach(() => preClass.push("valid"));
    let effClass = [] as string[];
    action.eff.forEach(() => effClass.push("valid"));
    let numNeqClass: string = "valid";
    let numNeqArgsClass: string[] = [];
    action.numNeqArgs.forEach(() => numNeqArgsClass.push("valid"));
    let neqArgsClass: string[][] = [];
    action.neqArgsClass.forEach(() => neqArgsClass.push([]));
    action.neqArgsClass.forEach((x,index) => x.forEach(() => neqArgsClass[index].push("valid")));

    //NAME
    if (action.name.search(new RegExp('^[a-zA-Z]+$')) !== 0) {
        nameClass = "invalid";
        valid = false;
        validStatus = "name: {aA-zZ, length > 0}";
    }

    //PRIORITY
    if (action.priority == undefined || action.priority.search(new RegExp('^[0-9]+$')) != 0 || Number(action.priority) < 0 || Number(action.priority) > 1000 || isNaN(Number(action.priority))) {
        valid = false;
        priorityClass = "invalid";
        if (validStatus.length > 0) {
            validStatus = validStatus + " & priority: {0-1000}";
        } else {
            validStatus = "priority: {0-1000}";
        }
    }

    //NUMBER OF PRECONDITIONS
    if (action.numPre == undefined || action.numPre.search(new RegExp('^[0-9]+$')) != 0 || Number(action.numPre) < 0 || Number(action.numPre) > 50 || isNaN(Number(action.numPre))) {
        valid = false;
        numPreClass = "invalid";
        if (validStatus.length > 0) {
            validStatus = validStatus + " & number of preconditions: {0-50}";
        } else {
            validStatus = "number of preconditions: {0-50}";
        }
    }

    //NUMBER OF EFFECTS
    if (action.numEff == undefined || action.numEff.search(new RegExp('^[0-9]+$')) != 0 || Number(action.numEff) < 0 || Number(action.numEff) > 50 || isNaN(Number(action.numEff))) {
        valid = false;
        numEffClass = "invalid";
        if (validStatus.length > 0) {
            validStatus = validStatus + " & number of effects: {0-50}";
        } else {
            validStatus = "number of effects: {0-50}";
        }
    }

    //PRECONDITIONS
    let indexes = [] as number[];
    for (let i = 0; i < Number(action.numPre); i = i + 1) {
        if (action.pre[i] == undefined || action.pre[i].search(new RegExp('^[0-9a-zA-Z]+$')) != 0 || !predicates.has(action.pre[i])) {
            valid = false;
            preClass[i] = "invalid"
            indexes.push(i);
        }
    }
    if (indexes.length > 0) {
        if (validStatus.length > 0) {
            validStatus = validStatus + " & precondition " + numbersToString(indexes) + ": {aA0-zZ1, is predicate id}";
        } else {
            validStatus = "precondition " + numbersToString(indexes) + ": {aA0-zZ1, is predicate id}";
        }
    }

    //EFFECTS
    let indexesEff = [] as number[];
    for (let i = 0; i < Number(action.numEff); i = i + 1) {
        if (action.eff[i] == undefined || action.eff[i].search(new RegExp('^[0-9a-zA-Z]+$')) != 0 || !predicates.has(action.eff[i])) {
            valid = false;
            effClass[i] = "invalid"
            indexesEff.push(i);
        }
    }
    if (indexesEff.length > 0) {
        if (validStatus.length > 0) {
            validStatus = validStatus + " & effect " + numbersToString(indexes) + ": {aA0-zZ1, is predicate id}";
        } else {
            validStatus = "effect " + numbersToString(indexes) + ": {aA0-zZ1, is predicate id}";
        }
    }

    //DUPLICATES
    let duplicateCounter = 0;
    actions.forEach((value, key) => {
        if (Number(key.slice(1, key.length)) < Number(id.slice(1, id.length))) {
            if (value.name == action.name && value.priority == action.priority && value.numPre == action.numPre && value.numEff == action.numEff) {
                let argsEqual = true;
                for (let i = 0; i < Number(value.numPre); i = i + 1) {
                    if (value.pre[i] != action.pre[i]) {
                        argsEqual = false;
                    }
                }
                for (let i = 0; i < Number(value.numEff); i = i + 1) {
                    if (value.eff[i] != action.eff[i]) {
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
        if (validStatus.length > 0) {
            validStatus = validStatus + " & duplicate (x" + duplicateCounter.toString() + ")";
        } else {
            validStatus = "duplicate (x" + duplicateCounter.toString() + ")";
        }
    }

    //INVALID PRECONDITIONS
    let invalidPres = [] as number[];
    action.pre.forEach((pre, index) => {
        if (predicates.has(pre)) {
            const predicate = predicates.get(pre);
            if (!predicate || !predicate.valid) {
                invalidPres.push(index);
                preClass[index] = "invalid";
            }
        }
    });
    if (invalidPres.length > 0) {
        valid = false;
        if (validStatus.length > 0) {
            validStatus = validStatus + " & invalid precondition " + numbersToString(invalidPres) + ": {change precondition}";
        } else {
            validStatus = "invalid precondition " + numbersToString(invalidPres) + ": {change precondition}";
        }
    }

    //INVALID EFFECTS
    let invalidEffs = [] as number[];
    action.eff.forEach((eff, index) => {
        if (predicates.has(eff)) {
            const predicate = predicates.get(eff);
            if (!predicate || !predicate.valid) {
                invalidEffs.push(index);
                effClass[index] = "invalid";
            }
        }
    });
    if (invalidEffs.length > 0) {
        valid = false;
        if (validStatus.length > 0) {
            validStatus = validStatus + " & invalid effect " + numbersToString(invalidEffs) + ": {change precondition}";
        } else {
            validStatus = "invalid effect " + numbersToString(invalidEffs) + ": {change precondition}";
        }
    }

    if (validStatus == "") {
        if (valid) {
            validStatus = "OK"
        } else {
            validStatus = "unknown error"
        }
    }

    //NUMBER OF UNEQUALS
    if (action.numNeq == undefined || action.numNeq.search(new RegExp('^[0-9]+$')) != 0 || Number(action.numNeq) < 0 || Number(action.numNeq) > 50 || isNaN(Number(action.numNeq))) {
        valid = false;
        numNeqClass = "invalid";
        if (validStatus.length > 0) {
            validStatus = validStatus + " & number of unequals: {0-50}";
        } else {
            validStatus = "number of unequals: {0-50}";
        }
    }

    //NUMBER OF UNEQUALS ARGS
    action.numNeqArgs.forEach((x,index) => {
        if (x == undefined || x.search(new RegExp('^[0-9]+$')) != 0 || Number(x) < 2 || Number(x) > 50 || isNaN(Number(x))) {
            valid = false;
            numNeqArgsClass[index] = "invalid";
            if (validStatus.length > 0) {
                validStatus = validStatus + " & number of unequal arguments: {2-50}";
            } else {
                validStatus = "number of unequal arguments: {2-50}";
            }
        }
    });

    //UNEQUALS ARGS
    action.neqArgs.forEach((args, index) => args.forEach((arg, j) => {
        if (arg.search(new RegExp('^[a-zA-Z]+$')) !== 0) {
            valid = false;
            neqArgsClass[index][j] = "invalid";
            if (validStatus.length > 0) {
                validStatus = validStatus + " & invalid unequal arg: {aA-zZ}";
            } else {
                validStatus = "invalid unequal arg: {aA-zZ}";
            }
        }
    }));


    //SET VALID STATES
    action.numNeqClass = numNeqClass;
    numNeqArgsClass.forEach((nnac, index) => action.numNeqArgsClass[index] = nnac);
    neqArgsClass.forEach((nac,index) => nac.forEach((naci,j) => action.neqArgsClass[index][j] = naci));
    action.numNeqArgsClass
    action.valid = valid;
    action.validStatus = validStatus;
    action.nameClass = nameClass;
    action.priorityClass = priorityClass;
    action.numPreClass = numPreClass;
    action.numEffClass = numEffClass;
    preClass.forEach((pre, index) => action.preClass[index] = pre);
    effClass.forEach((eff, index) => action.effClass[index] = eff);
}