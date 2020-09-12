import {create, find, get, Item, ItemProperty2Property22, Param2OfFind} from '../src/simple'
import * as http from "http";
import {LocalDate, LocalDateTime, LocalTime, Month} from "@js-joda/core";

const testItemStr = '{"commonProperty1":"common property 1 value","property1":"property 1 value","property2":{"commonProperty1":"inner common property 1 value","property21":"property 21 value","property22":"value1"},"decimalProperty":1,"localDateTimeProperty":"2020-01-01T01:01:00","stringArrayProperty":["array value 1","array value 2"],"mapProperty":{"key 1":10}}';
const testItem: Item = {
    "commonProperty1": "common property 1 value",
    "property1": "property 1 value",
    "property2": {
        "commonProperty1": "inner common property 1 value",
        "property21": "property 21 value",
        "property22": ItemProperty2Property22.Value1
    },
    "decimalProperty": 1,
    "localDateTimeProperty": LocalDateTime.of(LocalDate.of(2020, Month.JANUARY, 1), LocalTime.of(1, 1)),
    "stringArrayProperty": ["array value 1", "array value 2"],
    "mapProperty": {"key 1": 10}
};

let server = http.createServer((req, res) => {
        res.setHeader("Access-Control-Allow-Origin", "*")
        if (req.url == "/idValue" && req.method == 'GET') {
            res.end(testItemStr)
        } else if (req.url == "/find?param1=param1Value&param2=value2" && req.method == "GET") {
            res.end(testItemStr)
        } else if (req.url == "/" && req.method == "POST") {
            res.statusCode = 404
            res.end('"idValue"')
        } else {
            res.statusCode = 404
        }
    }
).listen(8080);

server.timeout = 100;

test('should find item', (done) => {
    find('http://localhost:8080', 'param1Value', Param2OfFind.Value2, 1000, value => {
        try {
            expect(value).toEqual(testItem)
            done()
        } catch (error) {
            done(error)
        }
    })
})

test('should get item', (done) => {
    get('http://localhost:8080', 'idValue', 1000, value => {
        try {
            expect(value).toEqual(testItem)
            done()
        } catch (error) {
            done(error)
        }
    })
})

test('should create item', (done) => {
    create('http://localhost:8080', testItem, 1000, value => {
        try {
            expect(value).toEqual("idValue")
            done()
        } catch (error) {
            done(error)
        }
    })
})

afterAll(() => {
    server.close()
})
