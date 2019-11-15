import Axios from "axios";
import {create, get, ItemProperty2Property22} from "expected";
import {LocalDateTime} from "@js-joda/core";

let axiosInstance = Axios.create({
    baseURL: "http://localhost:8080",
    // headers: {"content-type": "application/json"}
});

create(
    axiosInstance,
    {
        commonProperty1: "common property 1",
        property1: "property 1",
        property2: {
            commonProperty1: "common property 1",
            property21: "property 21",
            property22: ItemProperty2Property22.Value1
        },
        decimalProperty: 10,
        localDateTimeProperty: LocalDateTime.now(),
        stringArrayProperty: ["s1", "s2"],
        mapProperty: new Map([["v1", 1], ["v2", 2]])
    }
).then((value: string) =>
    console.log(value)
).catch((reason: any) =>
    console.log(reason)
);

get(
    axiosInstance,
    "id1"
).then(value =>
    console.log(value)
).catch(reason =>
    console.log(reason)
);
