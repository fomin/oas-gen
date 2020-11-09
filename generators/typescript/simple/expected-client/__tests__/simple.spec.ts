import {create, find, get, Item, ItemProperty2Property22, Param2OfFind} from '../src/simple'
import * as http from "http";
import {LocalDate, LocalDateTime, LocalTime, Month, OffsetDateTime, ZoneOffset} from "@js-joda/core";
import {IncomingMessage} from "http";

const testItemStr = '{"commonProperty1":"common property 1 value","property1":"property 1 value","property2":{"commonProperty1":"inner common property 1 value","property21":"property 21 value","property22":"value1"},"decimalProperty":1,"localDateTimeProperty":"2020-01-01T01:01:00","stringArrayProperty":["array value 1","array value 2"],"dateTimeArrayProperty":["2020-11-10T01:01:01+01:00"],"mapProperty":{"key 1":10},"true":{"property1":"property 1 value"},"1 with space-and+other_çhars":{"property1":"property 1 value"}}';
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
    "dateTimeArrayProperty": [OffsetDateTime.of(2020, 11, 10, 1, 1, 1, 0, ZoneOffset.ofHours(1))],
    "mapProperty": {"key 1": 10},
    "true": {property1: "property 1 value"},
    "1 with space-and+other_çhars": {property1: "property 1 value"},
};

function onContent(req: IncomingMessage, callback: (content: string) => void) {
    const chunks: Array<any> = []
    req.on('readable', () => {
        let chunk
        while (null !== (chunk = req.read())) {
            chunks.push(chunk)
        }
    });
    req.on('end', () => {
        const content = chunks.join('')
        callback(content)
    });

}

let server = http.createServer((req, res) => {
        res.setHeader("Access-Control-Allow-Origin", "*")
        if (req.url == "/idValue" && req.method == 'GET') {
            onContent(req, content => {
                expect(content).toEqual("")
                res.end(testItemStr)
            })
        } else if (req.url == "/find?param1=param1Value&param2=value2" && req.method == "GET") {
            onContent(req, content => {
                expect(content).toEqual("")
                res.end(testItemStr)
            })
        } else if (req.url == "/" && req.method == "POST") {
            onContent(req, content => {
                expect(content).toEqual(testItemStr)
                res.end('"idValue"')
            })
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
            expect(value).toEqual('idValue')
            done()
        } catch (error) {
            done(error)
        }
    });
})

afterAll(() => {
    server.close()
})
