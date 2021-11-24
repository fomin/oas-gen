import * as http from "http";
import {IncomingMessage} from "http";
import {Dto, Param2OfSimpleGet, simpleGet, simplePost} from "../out/simple";
import {returnOctetStream, sendOctetStream, testNullableParameter} from "../src/simple";

const dtoJson = '{"property1":"value1"}'
const referenceDto: Dto = {property1: "value1"}
const referenceBlob = "123456789"

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
        if (req.url == "/path2/idValue?param1=param1Value&param2=value1" && req.method == 'GET') {
            onContent(req, content => {
                expect(content).toEqual("")
                res.end(dtoJson)
            })
        } else if (req.url == "/path1" && req.method == "POST") {
            onContent(req, content => {
                expect(content).toEqual(dtoJson)
                res.end('"postResponseValue"')
            })
        } else if (req.url == "/path3" && req.method == 'POST') {
            onContent(req, content => {
                expect(content).toEqual("")
                res.end()
            })
        } else if (req.url == "/path4" && req.method == "GET") {
            onContent(req, content => {
                expect(content).toEqual("")
                res.end(referenceBlob)
            })
        } else if (req.url == "/path5" && req.method == "POST") {
            onContent(req, content => {
                expect(content).toEqual(referenceBlob)
                res.end()
            })
        } else {
            res.statusCode = 404
        }
    }
).listen(9080, "localhost");

server.timeout = 100;

test('should get dto', (done) => {
    simpleGet(
        'http://localhost:9080',
        'idValue',
        'param1Value',
        Param2OfSimpleGet.Value1,
        1000,
        value => {
            try {
                expect(value).toEqual(referenceDto)
                done()
            } catch (error) {
                done(error)
            }
        }
    )
})

test('should post dto', (done) => {
    simplePost(
        'http://localhost:9080',
        referenceDto,
        1000,
        value => {
            try {
                expect(value).toEqual('postResponseValue')
                done()
            } catch (error) {
                done(error)
            }
        }
    );
})

test('should send empty parameters', (done) => {
    testNullableParameter(
        'http://localhost:9080',
        undefined,
        1000,
        value => {
            try {
                expect(value).toEqual(undefined)
                done()
            } catch (error) {
                done(error)
            }
        }
    );
})

test('should return octet stream', (done) => {
    returnOctetStream(
        'http://localhost:9080',
        1000,
        value => {
            try {
                expect(value.size).toEqual(9)
                done()
            } catch (error) {
                done(error)
            }
        }
    );
})

test('should send octet stream', (done) => {
    sendOctetStream(
        'http://localhost:9080',
        new Blob([referenceBlob]),
        1000,
        value => {
            try {
                expect(value).toEqual(undefined)
                done()
            } catch (error) {
                done(error)
            }
        }
    );
})

afterAll(() => {
    server.close()
})
