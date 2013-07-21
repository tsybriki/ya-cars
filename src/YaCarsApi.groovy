import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.*;

/**
 * Created with IntelliJ IDEA.
 * User: macondo
 * Date: 7/13/13
 * Time: 1:17 PM
 * To change this template use File | Settings | File Templates.
 */
if (args.length != 2) {
    println "Usage: YaCarsApi.bat <input.file> <output.file>"
    return;
}

def inputFileName = args[0]
def outputFileName = args[1]

def http = new HTTPBuilder('http://auto2.yandex.ru')

def attemptsRemaining = 5;
def success = false
def sleepTime = 2000;

http.handler.failure = { resp ->
    println "Unexpected failure: ${resp.statusLine}"
    attemptsRemaining --;
    Thread.sleep(sleepTime)
}

def int cnt = 0;

new File(outputFileName).withWriter {out ->
    new RequestFile(inputFileName, RequestEntry.&fromCsv).each {RequestEntry entry ->
        success = false;
        attemptsRemaining = 5;

        while (!success && attemptsRemaining > 0) {
            http.get(
                    path: '/api/1.0/stats/', query: entry.asRequestParams(),
                    contentType: JSON
            ) { resp, json ->
                success = true;
                out.writeLine("${entry.asCsv()};${json.price.min};${json.price.avg};${json.price.max}")
            }
            cnt++;

            if (cnt % 200 == 0) {
                println "processed $cnt"
            }
        }

        if (attemptsRemaining == 0) {
            System.exit(-1)
        }
    }
}