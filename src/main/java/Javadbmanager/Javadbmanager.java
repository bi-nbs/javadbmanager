package Javadbmanager;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public final class Javadbmanager {

    private static Logger logger = LogManager.getLogger();
    private GitWrapper gitWrapper = new GitWrapper();
    private MySQLDatabase db = new MySQLDatabase();

    public Javadbmanager() {
        this.load();
    }

    private void load(){
        ObjectMapper mapper = new ObjectMapper();
        try {
            this.gitWrapper = mapper.readValue(new File("./gits.json"), GitWrapper.class);
            this.db = mapper.readValue(new File("./database.json"), MySQLDatabase.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

     void save(){
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(new File("./gits.json"), this.gitWrapper);
            mapper.writeValue(new File("./database.json"), this.db);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }


     GitProject getProjectByName(String name){
        for (GitProject project : this.gitWrapper.getGitProjects() ) {
            if (project.getProjectName().equals(name)){
                return project;
            }
        }
        return null;
    }

     void deleteProjectByName(String name){
        for (int i = 0; i < this.gitWrapper.getGitProjects().size(); i++) {
            if (this.gitWrapper.getGitProjects().get(i).getProjectName().equals(name)){
                this.gitWrapper.getGitProjects().remove(i);
            }
        }
    }

     void buildDatabaseFromCommit(GitCommit commit){

        db.rebuildDatabaseFromQueries(commit.getQueries());
    }

    public void buildDatabaseFromCommitID(String projectName, String commitID){
        if (this.getProjectByName(projectName) != null){
            GitCommit commit = this.getProjectByName(projectName).getCommitByID(commitID);
            if (commit != null) {
                db.rebuildDatabaseFromQueries(commit.getQueries());

            }else {
                logger.error("Could not find the specified commit!");
            }
        }   else {
            logger.error("Could not find the specified project!");
        }




    }


     List<String> getQueriesByCommitID(String commitID, String projectName){
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

     GitWrapper getGitWrapper() {
        return gitWrapper;
    }

     void setGitWrapper(GitWrapper gitWrapper) {
        this.gitWrapper = gitWrapper;
    }

     MySQLDatabase getDb() {
        return db;
    }

     void setDb(MySQLDatabase db) {
        this.db = db;
    }
}
