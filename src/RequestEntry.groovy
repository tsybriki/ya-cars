/**
 * Created with IntelliJ IDEA.
 * User: macondo
 * Date: 7/13/13
 * Time: 1:21 PM
 * To change this template use File | Settings | File Templates.
 */
class RequestEntry {
    String mark;
    String model;
    String year;
    String rid;
    String yId;

    RequestEntry(String yId, String rid, String mark, String model, String year) {
        this.mark = mark
        this.model = model
        this.year = year
        this.rid = rid
        this.yId = yId
    }

    def asRequestParams() {
        return [rid: rid, mark: mark, model: model, year_from: year, year_to: year];
    }

    static RequestEntry fromTabSeparated(String rawLine) {
        def tokens = rawLine.split("\\t")
        if (tokens.length != 3) {
            throw new IllegalArgumentException("String should contain 3 space/tab separated values. Current entry: " + rawLine)
        }

        return new RequestEntry("213", tokens[1], tokens[2], "2008")
    }

    static RequestEntry fromCsv(String rawLine) {
        def tokens = rawLine.split(";")
        if (tokens.length != 5) {
            throw new IllegalArgumentException("String should contain 4 semicolon separate values. Current entry: " + rawLine)
        }

        return new RequestEntry(tokens[0], tokens[3], tokens[1], tokens[2], tokens[4])
    }

    @Override
    public java.lang.String toString() {
        return "RequestEntry{" +
                "mark='" + mark + '\'' +
                ", model='" + model + '\'' +
                ", year='" + year + '\'' +
                '}';
    }

    def String asCsv() {
        return "$yId;$mark;$model;$rid;$year"
    }
}
