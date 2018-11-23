import java.util.ArrayList;
import java.util.List;

final class GitCommit {

    private String commitID;
    private List<Query> queries = new ArrayList<>();

    public GitCommit() {
    }

    public void setQueriesByListOfStrings(List<String> queries){
        this.queries.clear();
        for (String query: queries) {
            this.queries.add(new Query(query));
        }
    }


    public GitCommit(String commitID) {
        this.commitID = commitID;
    }


    public String getCommitID() {
        return commitID;
    }

    public void setCommitID(String commitID) {
        this.commitID = commitID;
    }

    public List<Query> getQueries() {
        return queries;
    }

    public void setQueries(List<Query> queries) {
        this.queries = queries;
    }
}
