package Javadbmanager;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.ArrayList;
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

    public void openGUI(){
        main(new String[]{});
    }


    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Java Javadbmanager.Database Manager By Magic Martin!");

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
        primaryStage.setScene(new Scene(root, 1250, 480));
        primaryStage.show();

        // Trigger the default window
        this.managerView();

    }

    private void managerView(){

        ListView<String> gitCommitList = new ListView<>();
        gitCommitList.setMinWidth(350);
        ListView<String> gitProjectList = new ListView<>();
        ListView<String> gitQueryList = new ListView<>();
        gitQueryList.setMinWidth(700);
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
        Button buttonQueryEdit = new Button("Edit script");
        Button buttonOverrideDBButton = new Button("Apply to database");
        Button buttonCommitCopyToClipboard = new Button("Copy ID");
        buttonQueryEdit.setOnAction(event -> editQueryScript());
        buttonCommitNew.setOnAction(event -> addNewCommitWindow());
        buttonCommitDelete.setOnAction(event -> deleteCommit());
        buttonOverrideDBButton.setOnAction(event -> overrideDBByCommitWindows());

        buttonCommitCopyToClipboard.setOnAction(event -> {
            if (currentCommit != null){
                Clipboard clipboard = Clipboard.getSystemClipboard();
                ClipboardContent clipboardContent = new ClipboardContent();
                clipboardContent.putString(currentCommit.getCommitID());
                clipboard.setContent(clipboardContent);
            }
        });
        HBox commitButtons = new HBox(buttonCommitNew, buttonCommitDelete, buttonCommitCopyToClipboard, buttonQueryEdit, buttonOverrideDBButton);
        mainPane.add(commitButtons, 1, 2);




        gitProjectList.getSelectionModel().select(0);

    }

    private void overrideDBByCommitWindows() {

        if (currentCommit == null) return;

        Stage overrideConfirmation = new Stage();
        VBox wrapper = new VBox();

        Label warningLabel = new Label("This will run the script on the specified database!");
        warningLabel.setFont(new Font("Cambria", 32));
        warningLabel.setTextFill(Color.RED);

        Button overrideButton = new Button("Override");
        overrideButton.setOnAction(event ->
                {
                    this.javadbmanager.buildDatabaseFromCommit(currentCommit);
                    Alert aler = new Alert(Alert.AlertType.INFORMATION);
                    aler.setHeaderText("Done!");
                    aler.show();
                    overrideConfirmation.close();
                }
        );

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(event -> overrideConfirmation.close()  );

        HBox buttonBox = new HBox(overrideButton, cancelButton);

        wrapper.getChildren().add(warningLabel);
        wrapper.getChildren().add(buttonBox);

        Scene overrideConfirmationScrene = new Scene(wrapper, 700, 80);
        overrideConfirmation.setScene(overrideConfirmationScrene);
        overrideConfirmation.show();


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
        inputField.setMinHeight(800);
        for (Query query: currentCommit.getQueries() ) {
            inputField.appendText(query.getQuery());
            inputField.appendText("\n");
        }

        Button saveButton = new Button("Save");
        saveButton.setOnAction(event ->
                {
                    saveTextAreaToQuery(inputField);
                    queryEditor.close();
                    managerView();
                }
        );

        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(event -> queryEditor.close());

        HBox buttonMenu = new HBox(saveButton, cancelButton);


        wrapper.getChildren().add(inputField);
        wrapper.getChildren().add(buttonMenu);

        Scene queryEditorScene = new Scene(wrapper, 600, 830);
        queryEditor.setScene(queryEditorScene);
        queryEditor.show();
    }

    private void saveTextAreaToQuery(TextArea area){
        List<String> newQueries = new ArrayList<>(Arrays.asList(area.getText().split(";" )));
        newQueries.remove(newQueries.size() -1);

        if (currentCommit != null){
            currentCommit.setQueriesByListOfStrings(newQueries.stream().map(string -> string + ";").collect(Collectors.toList()));
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

        VBox dataVbox = new VBox();
        Label databaseHeaderLabel = new Label("Javadbmanager.Database");
        databaseHeaderLabel.setFont(new Font("Cambria", 32));

        HBox hostnameOptionBox = new HBox();
        Label hostnameOptionLabel = new Label("Hostname: ");
        hostnameOptionLabel.setMinWidth(100);
        TextField hostnameOptionField = new TextField(this.javadbmanager.getDb().getHostname());
        hostnameOptionBox.getChildren().add(hostnameOptionLabel);
        hostnameOptionBox.getChildren().add(hostnameOptionField);

        HBox portOptionBox = new HBox();
        Label portOptionLabel = new Label("Port: ");
        portOptionLabel.setMinWidth(100);
        TextField portOptionField = new TextField(this.javadbmanager.getDb().getPort());
        portOptionBox.getChildren().add(portOptionLabel);
        portOptionBox.getChildren().add(portOptionField);

        HBox usernameOptionBox = new HBox();
        Label usernameOptionLabel = new Label("Username: ");
        usernameOptionLabel.setMinWidth(100);
        TextField usernameOptionField = new TextField(this.javadbmanager.getDb().getUsername());
        usernameOptionBox.getChildren().add(usernameOptionLabel);
        usernameOptionBox.getChildren().add(usernameOptionField);

        HBox passwordOptionBox = new HBox();
        Label passwordOptionLabel = new Label("Password: ");
        passwordOptionLabel.setMinWidth(100);
        TextField passwordOptionField = new TextField(this.javadbmanager.getDb().getPassword());
        passwordOptionBox.getChildren().add(passwordOptionLabel);
        passwordOptionBox.getChildren().add(passwordOptionField);

        HBox databaseOptionBox = new HBox();
        Label databaseOptionLabel = new Label("Javadbmanager.Database: ");
        databaseOptionLabel.setMinWidth(100);
        TextField databaseOptionField = new TextField(this.javadbmanager.getDb().getDatabase());
        databaseOptionBox.getChildren().add(databaseOptionLabel);
        databaseOptionBox.getChildren().add(databaseOptionField);

        Button save = new Button("Save");
        save.setOnAction(event -> {
            this.javadbmanager.getDb().setHostname(hostnameOptionField.getText());
            this.javadbmanager.getDb().setPort(portOptionField.getText());
            this.javadbmanager.getDb().setUsername( usernameOptionField.getText());
            this.javadbmanager.getDb().setPassword(passwordOptionField.getText());
            this.javadbmanager.getDb().setDatabase(databaseOptionField.getText());
            this.managerView();
        });


        dataVbox.getChildren().add(databaseHeaderLabel);
        dataVbox.getChildren().add(hostnameOptionBox);
        dataVbox.getChildren().add(portOptionBox);
        dataVbox.getChildren().add(usernameOptionBox);
        dataVbox.getChildren().add(passwordOptionBox);
        dataVbox.getChildren().add(databaseOptionBox);
        dataVbox.getChildren().add(save);





        mainPane.add(dataVbox, 0, 0);

    }

    private void saveEverything(){
        javadbmanager.save();
    }
}
