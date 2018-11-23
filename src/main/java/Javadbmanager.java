import Properties.PropertiesShop;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

final class Javadbmanager {

    private static Logger logger = LogManager.getLogger();
    private GitWrapper gitWrapper = new GitWrapper();

    public Javadbmanager() {
        this.load();
    }

    public void load(){
        ObjectMapper mapper = new ObjectMapper();
        try {
            this.gitWrapper = mapper.readValue(new File("./test.json"), GitWrapper.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save(){
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(new File("./test.json"), this.gitWrapper);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }


    public GitProject getProjectByName(String name){
        for (GitProject project : this.gitWrapper.getGitProjects() ) {
            if (project.getProjectName().equals(name)){
                return project;
            }
        }
        return null;
    }

    public void deleteProjectByName(String name){
        for (int i = 0; i < this.gitWrapper.getGitProjects().size(); i++) {
            if (this.gitWrapper.getGitProjects().get(i).getProjectName().equals(name)){
                this.gitWrapper.getGitProjects().remove(i);
            }
        }
    }




    public List<String> getQueriesByCommitID(String commitID, String projectName){
        GitProject project = this.getProjectByName(projectName);
        if(project != null){
            for (GitCommit commit: project.getCommits()) {
                if (commit.getCommitID().equals(commitID) ){
                    return commit.getQueries().stream().map(Query::getQuery).collect(Collectors.toList());
                }
            }
        }

        return null;
    }

    public GitWrapper getGitWrapper() {
        return gitWrapper;
    }

    public void setGitWrapper(GitWrapper gitWrapper) {
        this.gitWrapper = gitWrapper;
    }
}
