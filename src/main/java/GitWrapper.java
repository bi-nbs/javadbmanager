import java.util.ArrayList;
import java.util.List;

public class GitWrapper {


    private List<GitProject> gitProjects = new ArrayList<>();

    public GitWrapper() {
    }

    public List<GitProject> getGitProjects() {
        return gitProjects;
    }

    public void setGitProjects(List<GitProject> gitProjects) {
        this.gitProjects = gitProjects;
    }
}
