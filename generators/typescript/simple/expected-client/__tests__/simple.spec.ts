import * as http from "http";
import {IncomingMessage} from "http";
import {Dto, Param2OfSimpleGet, simpleGet, simplePost} from "../out/simple";

const dtoJson = '{"property1":"value1"}'
const referenceDto: Dto = {property1: "value1"}

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
        } else {
            res.statusCode = 404
        }
    }
).listen(8080);

server.timeout = 100;

test('should get dto', (done) => {
    simpleGet(
        'http://localhost:8080',
        'HEADER',
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
        'http://localhost:8080',
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

afterAll(() => {
    server.close()
})
