export class RestRequest<T> {
    private readonly request: XMLHttpRequest

    constructor(
        url: string,
        method: string,
        responseConverter: (response: any) => T,
        body?: any,
        timeout?: number,
        onLoadCallback?: (value: T) => void,
        onErrorCallback?: (reason: any) => void,
        onTimeoutCallback?: () => void,
        onAbortCallback?: () => void,
    ) {
        this.request = new XMLHttpRequest();
        this.request.open(method, url)
        this.request.responseType = "json"
        if (timeout) this.request.timeout = timeout

        if (onLoadCallback) {
            this.request.onload = () => {
                onLoadCallback(responseConverter(this.request.response))
            }
        }

        if (onErrorCallback) {
            this.request.onerror = () => {
                onErrorCallback(this.request.status)
            }
        }

        if (onTimeoutCallback) {
            this.request.ontimeout = () => {
                onTimeoutCallback()
            }
        }

        if (onAbortCallback) {
            this.request.onabort = () => {
                onAbortCallback()
            }
        }

        this.request.send(body)
    }

    abort(): void {
        this.request.abort()
    }
}

export function mapObjectProperties(source: any, mapper: (key: string, value: any) => any): any {
    let result: any = {};
    for (let prop in source) {
        let mappedValue = mapper(prop, source[prop]);
        result[prop] = mappedValue;
    }
    return result;
}
