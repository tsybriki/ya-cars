/**
 * Created with IntelliJ IDEA.
 * User: macondo
 * Date: 7/13/13
 * Time: 1:21 PM
 * To change this template use File | Settings | File Templates.
 */
class RequestFile {
    String fileName;
    Closure<RequestEntry> newEntry;

    RequestFile(String fileName, Closure<RequestEntry> newEntry) {
        this.fileName = fileName
        this.newEntry = newEntry
    }

    def each(Closure closure) {
        new File(fileName).eachLine { line ->
            closure(newEntry(line))
        }
    }
}
