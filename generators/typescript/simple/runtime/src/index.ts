export class RestRequest<T> {
    private readonly request: XMLHttpRequest

    constructor(
        url: string,
        method: string,
        responseConverter: (response: any) => T,
        responseType: XMLHttpRequestResponseType,
        body?: Blob | BufferSource | FormData | URLSearchParams | string,
        timeout?: number,
        headers?: Map<string, string>,
        onLoadCallback?: (value: T) => void,
        onErrorCallback?: (reason: any) => void,
        onTimeoutCallback?: () => void,
        onAbortCallback?: () => void,
    ) {
        this.request = new XMLHttpRequest();
        this.request.open(method, url)
        this.request.responseType = responseType
        if (timeout) this.request.timeout = timeout
        if (headers) {
            headers.forEach((value: string, key: string) => {
                this.request.setRequestHeader(key, value)
            })
        }

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
    let result: any = {}
    for (let prop in source) {
        if (source.hasOwnProperty(prop)) {
            result[prop] = mapper(prop, source[prop])
        }
    }
    return result
}
