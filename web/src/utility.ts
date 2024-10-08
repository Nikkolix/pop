export const numbersToString = function (list: number[]): String {
    let out = "";
    list.slice(0, list.length - 1).forEach((i) => out = out + (i + 1).toString() + ", ");
    out = out + (list[list.length - 1] + 1).toString();
    return out;
}

export const stringsToString = function (list: string[]): String {
    let out = "";
    list.slice(0, list.length - 1).forEach((str) => out = out + str + ", ");
    out = out + list[list.length - 1];
    return out;
}

export const max = function (i: number, i2: number) {
    if (i > i2) {
        return i;
    }
    return i2;
}