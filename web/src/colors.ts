export interface Colors {
    backgroundNav: string
    background: string
    unselected: string
    selected: string
    valid: string
    invalid: string
    underlineInput: string
    underline: string
    hoverBackground: string
    actionBorder: string
    outputBorder: string
    text: string
}

export function defaultColors(): Colors {
    return {
        backgroundNav: "#253441",
        background: "#162a37",
        unselected: "#4A6782",
        selected: "#5ca2c7",
        valid: "#048f31",
        invalid: "#dd2e2e",
        underlineInput: "#5ca2c7",
        underline: "#5ca2c7",
        hoverBackground: "#4A6782",
        actionBorder: "#c0bc29",
        outputBorder: "#000",
        text: "#fff",
    }
}