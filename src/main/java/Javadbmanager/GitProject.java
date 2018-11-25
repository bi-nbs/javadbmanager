package Javadbmanager;

import java.util.ArrayList;
import java.util.List;

final class GitProject {

    private List<GitCommit> commits = new ArrayList<>();
    private String projectName;


    public GitProject() {
    }

    public GitProject(String projectName) {
        this.projectName = projectName;
    }

    public GitCommit getCommitByID(String ID){
        for (GitCommit commit : this.commits ) {
            if (commit.getCommitID().equals(ID)){
                return commit;
            }
        }
        return null;
    }

    public void deleteCommitByID(String ID){
        for (int i = 0; i < this.commits.size(); i++) {
            if (this.commits.get(i).getCommitID().equals(ID)){
                this.commits.remove(i);
            }
        }
    }

    public List<GitCommit> getCommits() {
        return commits;
    }

    public void setCommits(List<GitCommit> commits) {
        this.commits = commits;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}
