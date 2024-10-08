export interface Output {
    actionsPreconditions: string[][];
    actionsEffects: string[][];
    actionsName: string[];
    cLinksFromPre: boolean[];
    cLinksFrom: number[];
    cLinksFromAction: number[];
    cLinksToPre: boolean[];
    cLinksTo: number[];
    cLinksToAction: number[];
    tLinksFrom: number[];
    tLinksTo: number[];
    complete: boolean;
    undoable: boolean;
    undo: boolean;
    undoType: string;
    descriptions: string[];
    previousStepExists: boolean;
}

export function newOutput(): Output {
    return {
        actionsPreconditions: [],
        actionsEffects: [],
        actionsName: [],
        cLinksFromPre: [],
        cLinksFrom: [],
        cLinksFromAction: [],
        cLinksToPre: [],
        cLinksTo: [],
        cLinksToAction: [],
        tLinksFrom: [],
        tLinksTo: [],
        complete: false,
        undoable: false,
        undo: false,
        undoType: "",
        descriptions: [],
        previousStepExists: false,
    }
}