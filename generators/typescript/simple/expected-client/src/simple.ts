import {RestRequest, mapObjectProperties} from "@andrey.n.fomin/oas-gen-typescript-dto-runtime";

export function simplePost(
    baseUrl: string,
    body: Dto,
    timeout?: number,
    onLoadCallback?: (value: string) => void,
    onErrorCallback?: (reason: any) => void,
    onTimeoutCallback?: () => void,
    onAbortCallback?: () => void
): RestRequest<string> {
    return new RestRequest<string>(
        `${baseUrl}/path1`,
        "POST",
        (value: any) => value,
        "json",
        JSON.stringify(body),
        timeout,
        undefined,
        onLoadCallback,
        onErrorCallback,
        onTimeoutCallback,
        onAbortCallback
    )
}

export function simpleGet(
    baseUrl: string,
    xHeader: string,
    id: string,
    param1: string,
    param2: Param2OfSimpleGet,
    timeout?: number,
    onLoadCallback?: (value: Dto) => void,
    onErrorCallback?: (reason: any) => void,
    onTimeoutCallback?: () => void,
    onAbortCallback?: () => void
): RestRequest<Dto> {
    return new RestRequest<Dto>(
        `${baseUrl}/path2/${id}?param1=${encodeURIComponent(param1)}&param2=${encodeURIComponent(param2)}`,
        "GET",
        (value: any) => value,
        "json",
        undefined,
        timeout,
        new Map([['X-header', xHeader]]),
        onLoadCallback,
        onErrorCallback,
        onTimeoutCallback,
        onAbortCallback
    )
}

/**
 *
 */
export interface Dto {
    /**
     *
     */
    readonly "property1"?: string;
}

/**
 * Dto title
 *
 * Dto description
 */
export enum Param2OfSimpleGet {
    Value1 = "value1",
    Value2 = "value2"
}
