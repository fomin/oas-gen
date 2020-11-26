import {DateTimeFormatter, LocalDateTime, OffsetDateTime} from "@js-joda/core";
import {RestRequest, mapObjectProperties} from "@andrey.n.fomin/oas-gen-typescript-dto-runtime";

export function create(
    baseUrl: string,
    body: Item,
    timeout?: number,
    onLoadCallback?: (value: string) => void,
    onErrorCallback?: (reason: any) => void,
    onTimeoutCallback?: () => void,
    onAbortCallback?: () => void
): RestRequest<string> {
    return new RestRequest<string>(
        `${baseUrl}/`,
        "POST",
        value => value,
        "json",
        JSON.stringify(itemToJson(body)),
        timeout,
        onLoadCallback,
        onErrorCallback,
        onTimeoutCallback,
        onAbortCallback
    )
}

export function postWithoutRequestBody(
    baseUrl: string,
    timeout?: number,
    onLoadCallback?: (value: string) => void,
    onErrorCallback?: (reason: any) => void,
    onTimeoutCallback?: () => void,
    onAbortCallback?: () => void
): RestRequest<string> {
    return new RestRequest<string>(
        `${baseUrl}/post-without-request-body`,
        "POST",
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

export function find(
    baseUrl: string,
    param1: string,
    param2: Param2OfFind,
    timeout?: number,
    onLoadCallback?: (value: Item) => void,
    onErrorCallback?: (reason: any) => void,
    onTimeoutCallback?: () => void,
    onAbortCallback?: () => void
): RestRequest<Item> {
    return new RestRequest<Item>(
        `${baseUrl}/find?param1=${encodeURIComponent(param1)}&param2=${encodeURIComponent(param2)}`,
        "GET",
        value => itemFromJson(value),
        "json",
        undefined,
        timeout,
        onLoadCallback,
        onErrorCallback,
        onTimeoutCallback,
        onAbortCallback
    )
}

export function get(
    baseUrl: string,
    id: string,
    timeout?: number,
    onLoadCallback?: (value: ComponentItem) => void,
    onErrorCallback?: (reason: any) => void,
    onTimeoutCallback?: () => void,
    onAbortCallback?: () => void
): RestRequest<ComponentItem> {
    return new RestRequest<ComponentItem>(
        `${baseUrl}/${id}`,
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

/**
 * Item
 *
 * This is a very long *description* of item.
 * This is a very long *description* of item.
 *
 * This is a very long *description* of item.
 * This is a very long *description* of item.
 */
export interface Item {
    /**
     * Common property 1
     */
    readonly "commonProperty1"?: string;

    /**
     * Property 1
     *
     * This is a very long *description* of property 1
     * This is a very long *description* of property 1
     *
     * This is a very long *description* of property 1
     * This is a very long *description* of property 1
     */
    readonly "property1": string;

    /**
     * Property 2
     */
    readonly "property2": ItemProperty2;

    /**
     * Decimal property
     */
    readonly "decimalProperty"?: string;

    /**
     * Local date time property
     */
    readonly "localDateTimeProperty"?: LocalDateTime;

    /**
     * String array property
     */
    readonly "stringArrayProperty"?: readonly string[];

    /**
     * Date-time array property
     */
    readonly "dateTimeArrayProperty"?: readonly OffsetDateTime[];

    /**
     * Map property
     */
    readonly "mapProperty"?: Record<string, number>;

    /**
     * Date-time map property
     */
    readonly "dateTimeMapProperty"?: Record<string, OffsetDateTime>;

    /**
     * Schema with reserved word in name
     */
    readonly "true": True;

    /**
     * Property with space and other chars in name
     */
    readonly "1 with space-and+other_çhars": $1WithSpaceAndOtherÇhars;
}

// @ts-ignore
function itemFromJson(json: any): Item {
    return mapObjectProperties(json, (key, value) => {
        switch (key) {
            case "localDateTimeProperty":
                return LocalDateTime.parse(value);

            case "dateTimeArrayProperty":
                return value.map((it: any) => OffsetDateTime.parse(it));

            case "dateTimeMapProperty":
                return mapObjectProperties(value, (_, v) => OffsetDateTime.parse(v));
            default:
                return value;
        }
    });
}

// @ts-ignore
function itemToJson(obj: Item): any {
    return mapObjectProperties(obj, (key, value) => {
        switch (key) {
            case "localDateTimeProperty":
                return (value as LocalDateTime).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);

            case "dateTimeArrayProperty":
                return value.map((it: any) => (it as OffsetDateTime).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));

            case "dateTimeMapProperty":
                return mapObjectProperties(value, (_, v) => (v as OffsetDateTime).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            default:
                return value;
        }
    });
}

/**
 * query parameter 2
 */
export enum Param2OfFind {
    Value1 = "value1",
    Value2 = "value2"
}

/**
 * Component item
 */
export interface ComponentItem {

}

/**
 * Property 2
 */
export interface ItemProperty2 {
    /**
     * Common property 1
     */
    readonly "commonProperty1"?: string;

    /**
     * Property 21
     */
    readonly "property21"?: string;

    /**
     * Property 22
     */
    readonly "property22"?: ItemProperty2Property22;
}

/**
 * Schema with reserved word in name
 */
export interface True {
    /**
     * Property 1
     */
    readonly "property1"?: string;
}

/**
 * Property with space and other chars in name
 */
export interface $1WithSpaceAndOtherÇhars {
    /**
     * Property 1
     */
    readonly "property1"?: string;
}

/**
 * Property 22
 */
export enum ItemProperty2Property22 {
    Value1 = "value1",
    Value2 = "value2",
    Value3 = "value3"
}
