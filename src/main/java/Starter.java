import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Button;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class Starter extends Application {

    private GridPane mainPane;

    private Javadbmanager javadbmanager = new Javadbmanager();
    private String currentProjectName;
    private GitCommit currentCommit;

    public static void main(String[] args) {
        launch(args);
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Java Database Manager By Magic Martin!");

        this.mainPane = new GridPane();

        Button buttonManager = new Button();
        buttonManager.setText("Manager");
        buttonManager.setOnAction(event -> this.managerView());

        Button buttonSettings = new Button();
        buttonSettings.setText("Settings");
        buttonSettings.setOnAction(event -> this.settingsView(mainPane));

        Button buttonSave = new Button();
        buttonSave.setText("Save");
        buttonSave.setOnAction(event -> this.saveEverything());

        HBox menu = new HBox();
        menu.getChildren().add(buttonManager);
        menu.getChildren().add(buttonSettings);
        menu.getChildren().add(buttonSave);


        VBox root = new VBox();
        root.getChildren().add(menu);
        root.getChildren().add(mainPane);
        primaryStage.setScene(new Scene(root, 1000, 800));
        primaryStage.show();
    }

    private void managerView(){

        ListView<String> gitCommitList = new ListView<>();
        ListView<String> gitProjectList = new ListView<>();
        ListView<String> gitQueryList = new ListView<>();
        ObservableList<String> observableProjectList = FXCollections.observableArrayList(
                this.javadbmanager.getGitWrapper().getGitProjects().stream().map(GitProject::getProjectName).collect(Collectors.toList())
        );
        gitProjectList.setItems(observableProjectList);



        gitProjectList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue != null){
                    currentProjectName = newValue;
                    currentCommit = null;
                    updateCommitListByGitProjectName(gitCommitList, newValue);
                    updateQueryListByCommitID(gitQueryList);
                }
            }
        });

        gitCommitList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue != null && currentProjectName != null){
                    currentCommit = javadbmanager.getProjectByName(currentProjectName).getCommitByID(newValue);

                    updateQueryListByCommitID(gitQueryList);
                }
            }
        });




        mainPane.getChildren().clear();


        mainPane.add(new TextField("Git project"), 0, 0);
        mainPane.add(new TextField("Commit"), 1, 0);
        mainPane.add(new TextField("SQL Script"), 2, 0);

        mainPane.add(gitProjectList, 0, 1);
        mainPane.add(gitCommitList, 1, 1);
        mainPane.add(gitQueryList, 2, 1);




        //Button adds for all of the lists
        Button buttonProjectNew = new Button("New");
        Button buttonProjectDelete = new Button("Delete");
        buttonProjectNew.setOnAction(event -> addNewProjectWindow());
        buttonProjectDelete.setOnAction(event -> deleteProjectByName());
        HBox projectButtons = new HBox();
        projectButtons.getChildren().add(buttonProjectNew);
        projectButtons.getChildren().add(buttonProjectDelete);
        mainPane.add(projectButtons, 0, 2);




        Button buttonCommitNew = new Button("New");
        Button buttonCommitDelete = new Button("Delete");
        Button buttonQueryEdit = new Button("Override script");
        buttonQueryEdit.setOnAction(event -> editQueryScript());
        buttonCommitNew.setOnAction(event -> addNewCommitWindow());
        buttonCommitDelete.setOnAction(event -> deleteCommit());
        HBox commitButtons = new HBox();
        commitButtons.getChildren().add(buttonCommitNew);
        commitButtons.getChildren().add(buttonCommitDelete);
        commitButtons.getChildren().add(buttonQueryEdit);
        mainPane.add(commitButtons, 1, 2);




        gitProjectList.getSelectionModel().select(0);

    }

    private void updateCommitListByGitProjectName(ListView<String> listView ,String name){
        GitProject project = this.javadbmanager.getProjectByName(name);
        if (project != null){
            ObservableList<String> observableCommitList = FXCollections.observableArrayList(
                project.getCommits().stream().map(GitCommit::getCommitID).collect(Collectors.toList())
            );
            listView.setItems(observableCommitList);
        }
    }

    private void updateQueryListByCommitID(ListView list){

        if (currentCommit != null ){
            List<String> queries = currentCommit.getQueries().stream().map(Query::getQuery).collect(Collectors.toList());
            ObservableList<String> observableQueryList = FXCollections.observableArrayList(queries);
            list.setItems(observableQueryList);
            return;
        }else{
            list.setItems(FXCollections.observableArrayList());
        }

    }

    private void editQueryScript(){

        if (currentCommit == null) return;

        Stage queryEditor = new Stage();
        VBox wrapper = new VBox();

        TextArea inputField = new TextArea();

        Button saveButton = new Button("Save");
        saveButton.setOnAction(event ->
                {
                    saveTextAreaToQuery(inputField);
                    queryEditor.close();
                    managerView();
                }
        );


        wrapper.getChildren().add(inputField);
        wrapper.getChildren().add(saveButton);

        Scene queryEditorScene = new Scene(wrapper, 500, 500);
        queryEditor.setScene(queryEditorScene);
        queryEditor.show();
    }

    private void saveTextAreaToQuery(TextArea area){
        List<String> newQueries = Arrays.asList(area.getText().split("\n"));

        if (currentCommit != null){
            currentCommit.setQueriesByListOfStrings(newQueries);
        }

    }

    private void addNewProjectWindow(){
        Stage queryEditor = new Stage();
        VBox wrapper = new VBox();

        TextField inputField = new TextField();

        Button saveButton = new Button("Save");
        saveButton.setOnAction(event ->
                {
                    addNewProject(inputField.textProperty().getValue());
                    queryEditor.close();
                }
        );


        wrapper.getChildren().add(inputField);
        wrapper.getChildren().add(saveButton);

        Scene queryEditorScene = new Scene(wrapper, 500, 500);
        queryEditor.setScene(queryEditorScene);
        queryEditor.show();
    }

    private void addNewProject(String projectName){
        javadbmanager.getGitWrapper().getGitProjects().add(new GitProject(projectName));
        this.managerView();
    }

    private void deleteProjectByName(){
        this.javadbmanager.deleteProjectByName(currentProjectName);
        managerView();
    }




    private void addNewCommitWindow(){
        Stage queryEditor = new Stage();
        VBox wrapper = new VBox();

        TextField inputField = new TextField();

        Button saveButton = new Button("Save");
        saveButton.setOnAction(event ->
                {
                    addNewCommit(inputField.textProperty().getValue());
                    queryEditor.close();
                }
        );


        wrapper.getChildren().add(inputField);
        wrapper.getChildren().add(saveButton);

        Scene queryEditorScene = new Scene(wrapper, 500, 500);
        queryEditor.setScene(queryEditorScene);
        queryEditor.show();
    }

    private void addNewCommit(String commitID){
        javadbmanager.getProjectByName(currentProjectName).getCommits().add(new GitCommit(commitID));
        this.managerView();
    }

    private void deleteCommit(){
        this.javadbmanager.getProjectByName(currentProjectName).deleteCommitByID(currentCommit.getCommitID());
        managerView();
    }


    private void settingsView(GridPane mainPane){

        mainPane.getChildren().clear();
        TextArea test = new TextArea("Hello there number 2!");
        mainPane.add(test, 0, 0);

    }

    private void saveEverything(){
        javadbmanager.save();
    }
}
