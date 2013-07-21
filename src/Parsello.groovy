import groovyx.net.http.HTTPBuilder

import static groovyx.net.http.ContentType.*
/**
 * Created with IntelliJ IDEA.
 * User: macondo
 * Date: 5/6/13
 * Time: 6:35 PM
 * To change this template use File | Settings | File Templates.
 */
class Entry {
    def id;
    def String uri;
    def String caption;
    def String price;
    def double score = 0;

    public def display() {
        return "$price => $caption"
    }


    @Override
    public java.lang.String toString() {
        return "Entry{" +
                "id=" + id +
                ", uri='" + uri + '\'' +
                ", caption='" + caption + '\'' +
                ", price='" + price + '\'' +
                ", score=" + score +
                '}';
    }
}

def http = new HTTPBuilder('http://www.avito.ru')

def getAWithTitle(div) {
    return div.depthFirst().findAll {it.name() == 'A' && it.'@class'.text().contains('second-link')}[0]
}

def getDivWithPrice(div) {
    return div.depthFirst().findAll {it.'@class'.text().contains('t_i_description')}[0]
}

def final EXCLUDE_SCORE = -1000000;

def NOTE_2_NAMES = ["ote2", "ote 2", "ote II", "7100"]
def note2Name = {Entry e ->
    def q = 0
    NOTE_2_NAMES.each { if (e.caption.contains(it)) {q++}}
    if (q > 0) {
        e.score += 100000;
    }
}

def priceScore = { Entry e ->
    if (!e.price.matches("\\d*")) {
        e.score += EXCLUDE_SCORE;
    } else {
        e.score -= Integer.parseInt(e.price)
    }
}

def ensurePrice(p, c) {
    if (p.length() == 0) {
        if (c.toLowerCase().contains('мен')) {
            "Обмен"
        } else {
            "N/A"
        }
    } else {
        p.replaceAll(~/\D/, "")
    }
}

def phones = [];

http.get( path : '/moskva/telefony',
        query: [name: 'galaxy note', params: '143_628'],
        contentType : HTML) { resp, xml ->

    xml.'**'[0].depthFirst().findAll {
        it.name() == 'DIV' && it.'@class'.text().contains('t_i_i')
    }.each {
        def a = getAWithTitle(it)
        def p = getDivWithPrice(it)
        def e = new Entry()
        e.id = a.'@name'.text()
        e.uri = "http://www.avito.ru${a.'@href'.text()}"
        e.caption = a.text().trim()
        e.price = ensurePrice("${p}", e.caption)

        phones << e
    }
}

phones.each priceScore
phones.each note2Name

phones.sort {-it.score}

phones.each {println it.display()}