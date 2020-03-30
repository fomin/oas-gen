import {AxiosInstance} from "axios";
import {LocalDateTime} from "@js-joda/core";
import {mapObjectProperties} from "oas-gen-support";

export function create(axios: AxiosInstance, body: Item): Promise<string> {
    return axios.post(`/`, itemToJson(body)).then(value => value.data);
}

export function get(axios: AxiosInstance, id: string): Promise<Item> {
    return axios.get(`/${id}`).then(value => itemFromJson(value.data));
}

/**
 * Item
 */
export interface Item {
    /**
     * Common property 1
     */
    readonly commonProperty1: string;

    /**
     * Property 1
     */
    readonly property1: string;

    /**
     * Property 2
     */
    readonly property2: ItemProperty2;

    /**
     * Decimal property
     */
    readonly decimalProperty: number;

    /**
     * Local date time property
     */
    readonly localDateTimeProperty: LocalDateTime;

    /**
     * String array property
     */
    readonly stringArrayProperty: readonly string[];

    /**
     * Map property
     */
    readonly mapProperty: Map<string, number>;
}

function itemFromJson(json: any): Item {
    return mapObjectProperties(json, (key, value) => {
        switch (key) {
            case "localDateTimeProperty":
                return LocalDateTime.parse(value);
            default:
                return value;
        }
    });
}

function itemToJson(obj: Item): any {
    return mapObjectProperties(obj, (key, value) => {
        switch (key) {
            case "localDateTimeProperty":
                return value.toString();
            default:
                return value;
        }
    });
}

/**
 * Property 2
 */
export interface ItemProperty2 {
    /**
     * Common property 1
     */
    readonly commonProperty1: string;

    /**
     * Property 21
     */
    readonly property21: string;

    /**
     * Property 22
     */
    readonly property22: ItemProperty2Property22;
}

/**
 * Property 22
 */
export const enum ItemProperty2Property22 {
    Value1 = "value1",
    Value2 = "value2",
    Value3 = "value3"
}
