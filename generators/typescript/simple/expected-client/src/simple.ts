import {DateTimeFormatter, LocalDate} from "@js-joda/core";
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
        value => value,
        "json",
        JSON.stringify(body),
        timeout,
        onLoadCallback,
        onErrorCallback,
        onTimeoutCallback,
        onAbortCallback
    )
}

export function simpleGet(
    baseUrl: string,
    id: string,
    param1: string,
    param2?: Param2OfSimpleGet,
    timeout?: number,
    onLoadCallback?: (value: Dto) => void,
    onErrorCallback?: (reason: any) => void,
    onTimeoutCallback?: () => void,
    onAbortCallback?: () => void
): RestRequest<Dto> {
    let queryParameters = [
        (param1) ? "param1=" + encodeURIComponent(param1) : null,
        (param2) ? "param2=" + encodeURIComponent(param2) : null
    ].filter(value => value != null).join("&")
    return new RestRequest<Dto>(
        `${baseUrl}/path2/${id}?${queryParameters}`,
        "GET",
        value => value,
        "json",
        undefined,
        timeout,
        onLoadCallback,
        onErrorCallback,
        onTimeoutCallback,
        onAbortCallback
    )
}

export function testNullableParameter(
    baseUrl: string,
    param1?: LocalDate,
    timeout?: number,
    onLoadCallback?: (value: void) => void,
    onErrorCallback?: (reason: any) => void,
    onTimeoutCallback?: () => void,
    onAbortCallback?: () => void
): RestRequest<void> {
    let queryParameters = [
        (param1) ? "param1=" + encodeURIComponent((param1 as LocalDate).format(DateTimeFormatter.ISO_LOCAL_DATE)) : null
    ].filter(value => value != null).join("&")
    return new RestRequest<void>(
        `${baseUrl}/path3?${queryParameters}`,
        "POST",
        value => undefined,
        "text",
        undefined,
        timeout,
        onLoadCallback,
        onErrorCallback,
        onTimeoutCallback,
        onAbortCallback
    )
}

export function returnOctetStream(
    baseUrl: string,
    timeout?: number,
    onLoadCallback?: (value: Blob) => void,
    onErrorCallback?: (reason: any) => void,
    onTimeoutCallback?: () => void,
    onAbortCallback?: () => void
): RestRequest<Blob> {

    return new RestRequest<Blob>(
        `${baseUrl}/path4`,
        "GET",
        value => value,
        "blob",
        undefined,
        timeout,
        onLoadCallback,
        onErrorCallback,
        onTimeoutCallback,
        onAbortCallback
    )
}

export function sendOctetStream(
    baseUrl: string,
    body: Blob,
    timeout?: number,
    onLoadCallback?: (value: void) => void,
    onErrorCallback?: (reason: any) => void,
    onTimeoutCallback?: () => void,
    onAbortCallback?: () => void
): RestRequest<void> {

    return new RestRequest<void>(
        `${baseUrl}/path5`,
        "POST",
        value => undefined,
        "text",
        body,
        timeout,
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
    "property1"?: string;
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
