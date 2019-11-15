export function mapObjectProperties(source: any, mapper: (key: string, value: any) => any): any {
    let result: any = {};
    for (let prop in source) {
        let mappedValue = mapper(prop, source[prop]);
        result[prop] = mappedValue;
    }
    return result;
}
