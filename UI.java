/**
 * Text File Search Engine
 * Upload text files to search for keywords
 * Can only search for strings containing numbers or alphabetical characters
 * By Aaron Brzowski and Michelle Schapmire
 */


import javafx.scene.input.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


public final class UI extends Application {

    private ArrayList<Document> docList = new ArrayList<>(); //stores list of Document objects

    @Override
    public void start(final Stage stage) {

        stage.setTitle("Search Engine"); //sets main stage for GUI

        List<File> fileList = new ArrayList<File>();    // list of files

        final FileChooser fileChooser = new FileChooser();

        //creates all Buttons for use in the GUI
        final Button openMultipleButton = new Button("Upload .txt Files");
        final Button searchButton = new Button("Search");
        final Button returnButton = new Button("Search Again");
        final Button clearFilesButton = new Button ("Clear Stored Files");
        final Button quitButton = new Button("Quit");
        final Button quitButtonScene2 = new Button("Quit");

        //creates GridPane in which all graphics objects will be arranged in the first window
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(12);

        ColumnConstraints column1 = new ColumnConstraints();
        column1.setHalignment(HPos.RIGHT);
        grid.getColumnConstraints().add(column1);

        ColumnConstraints column2 = new ColumnConstraints();
        column2.setHalignment(HPos.LEFT);
        grid.getColumnConstraints().add(column2);

        //HBox will store the label, and the search box in a horizonta configuration
        HBox hbButtons = new HBox();
        hbButtons.setSpacing(60.0);

        Label searchLabel = new Label("Enter Search Terms: ");
        final TextField tfName = new TextField();

        grid.add(searchLabel, 0, 0);
        grid.add(tfName, 1, 0);

        hbButtons.setAlignment(Pos.BOTTOM_CENTER);
        hbButtons.getChildren().addAll(openMultipleButton, clearFilesButton, quitButton, searchButton);

       //creates a grid inside a grid for proper arrangement of buttons w/ search box and labels
        GridPane innergrid = new GridPane();
        innergrid.setAlignment(Pos.CENTER);
        innergrid.add(hbButtons, 0, 0);
        grid.add(innergrid, 0, 2, 2, 1);

        //creates the first Scene to be displayed on startup using the "grid" GridPane
        Scene inputScene = new Scene(grid, 640, 480);

        stage.setScene(inputScene);
        stage.show();

        //creates button action to allow user to upload files
        openMultipleButton.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(final ActionEvent e) {
                        List<File> list =
                                //opens the FileChooser object defined earlier to allow the user to select files to upload to the program
                                fileChooser.showOpenMultipleDialog(stage);
                        if (list != null) {
                            for (File file : list) { //cycles through loop based on how many files are uploaded
                                fileList.add(file); //adds the file in the current iteration to the list of files
                                docList.add(new Document(file.toString())); //creates Document object from file and adds to docList for proper formatting
                            }
                        }
                    }
                });

        //returns the user to the first scene in which files are uploaded and search queries are entered
        returnButton.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(final ActionEvent e) {

                    stage.setScene(inputScene); //sets scene back to original scene to allow user to search again w/ different files or

                    }
                });

        //clears the stored files uploaded by the user for the previous search
        clearFilesButton.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(final ActionEvent e) {

                        fileList.clear();
                        docList.clear();

                    }
                });

        //closes the window
        quitButton.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(final ActionEvent e) {

                        stage.close();

                    }
                });

        quitButtonScene2.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(final ActionEvent e) {

                        stage.close();

                    }
                });

        //allows the user to utilize the enter key to search along with the search button
        tfName.setOnKeyPressed(new EventHandler<KeyEvent>()
        {
            @Override
            public void handle(KeyEvent ke)
            {
                if (ke.getCode().equals(KeyCode.ENTER))
                {

                    //prevents user from switching to second scene without something entered as a search query
                    if(!tfName.getText().equalsIgnoreCase("")) {
                        // create scene to display results

                        ScrollPane sp = new ScrollPane();
                        VBox content = new VBox();
                        sp.setContent(content);

                        //creates SplitPane to display results on the left, and show the return button on the right
                        SplitPane splitPane = new SplitPane();

                        //sets the return button size and placement
                        VBox returnButtonBox = new VBox();
                        returnButtonBox.setPrefHeight(returnButtonBox.getPrefHeight());
                        returnButtonBox.getChildren().addAll(returnButton,quitButtonScene2);
                        returnButtonBox.setAlignment(Pos.CENTER);
                        returnButtonBox.setSpacing(20);

                        //creates an array of String objects split by whitespace in the search query
                        String[] finalSearchQuery = tfName.getText().toLowerCase().split("\\s+"); //splits search query by whitespace into array of substrings for search terms;

                        //runs doSearch method which takes the search query list and searches the Documents in the document list for matches and sorts the list
                        doSearch(finalSearchQuery);

                        String currentFile;

                        boolean filesFound = false;

                        //formats file names in the sorted Document list for easy viewing when displayed in the SplitPane
                        for (int i = 0; i < fileList.size(); i++) {

                            currentFile = docList.get(i).getFileName();

                            int slashPos = currentFile.lastIndexOf("/");

                            if (slashPos == -1)
                                slashPos = currentFile.lastIndexOf("\\");

                            currentFile = currentFile.substring(slashPos + 1);

                            docList.get(i).setScore(finalSearchQuery);

                            //only displays documents that contain at least one of the search query terms
                            if (docList.get(i).getScore() != 0) {
                                Label label = new Label(i + 1 + ". " + currentFile /*docList.get(i).getScore()*/);
                                content.setPrefHeight(content.getPrefHeight() + label.getPrefHeight());
                                content.getChildren().add(label);
                                filesFound = true;
                            }

                        }

                        //"error handling" in a basic sense. alerts the user if there are no documents uploaded to search
                        if (docList.size() == 0) {
                            Label emptyLabel = new Label("No files found. Click the Search Again button to return and upload files.");
                            content.setPrefHeight(content.getPrefHeight() + emptyLabel.getPrefHeight());
                            content.getChildren().add(emptyLabel);
                        }
                        else if(!filesFound){                             //if there are no matches to the search query, alerts user
                            Label noMatch = new Label("Search query not found in any uploaded files.");
                            content.setPrefHeight(content.getPrefHeight() + noMatch.getPrefHeight());
                            content.getChildren().add(noMatch);
                        }

                        splitPane.getItems().addAll(sp, returnButtonBox);

                        splitPane.setDividerPosition(0, 0.7);


                        Scene resultsScene = new Scene(splitPane, 640, 480);

                        stage.setScene(resultsScene);
                    }
                }
            }
        });

        //creates and changes scene to second scene to display results based on input from uploaded files and user queries
        searchButton.setOnAction(
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(final ActionEvent e) {

                        //prevents user from switching to second scene without something entered as a search query
                        if(!tfName.getText().equalsIgnoreCase("")) {
                            // create scene to display results

                            ScrollPane sp = new ScrollPane();
                            VBox content = new VBox();
                            sp.setContent(content);

                            //creates SplitPane to display results on the left, and show the return button on the right
                            SplitPane splitPane = new SplitPane();

                            //sets the return button size and placement
                            VBox returnButtonBox = new VBox();
                            returnButtonBox.setPrefHeight(returnButtonBox.getPrefHeight());
                            returnButtonBox.getChildren().addAll(returnButton,quitButtonScene2);
                            returnButtonBox.setAlignment(Pos.CENTER);
                            returnButtonBox.setSpacing(20);

                            //creates an array of String objects split by whitespace in the search query
                            String[] finalSearchQuery = tfName.getText().toLowerCase().split("\\s+"); //splits search query by whitespace into array of substrings for search terms;

                            //runs doSearch method which takes the search query list and searches the Documents in the document list for matches and sorts the list
                            doSearch(finalSearchQuery);

                            String currentFile;

                            boolean filesFound = false;

                            //formats file names in the sorted Document list for easy viewing when displayed in the SplitPane
                            for (int i = 0; i < fileList.size(); i++) {

                                currentFile = docList.get(i).getFileName();

                                int slashPos = currentFile.lastIndexOf("/");

                                if (slashPos == -1)
                                    slashPos = currentFile.lastIndexOf("\\");

                                currentFile = currentFile.substring(slashPos + 1);

                                docList.get(i).setScore(finalSearchQuery);

                                //only displays documents that contain at least one of the search query terms
                                if (docList.get(i).getScore() != 0) {
                                    Label label = new Label(i + 1 + ". " + currentFile /*docList.get(i).getScore()*/);
                                    content.setPrefHeight(content.getPrefHeight() + label.getPrefHeight());
                                    content.getChildren().add(label);
                                    filesFound = true;
                                }


                            }


                            //"error handling" in a basic sense. alerts the user if there are no documents uploaded to search
                            if (docList.size() == 0) {
                                Label emptyLabel = new Label("No files found. Click the Search Again button to return and upload files.");
                                content.setPrefHeight(content.getPrefHeight() + emptyLabel.getPrefHeight());
                                content.getChildren().add(emptyLabel);
                            }
                            else if(!filesFound){                             //if there are no matches to the search query, alerts user
                                Label noMatch = new Label("Search query not found in any uploaded files.");
                                content.setPrefHeight(content.getPrefHeight() + noMatch.getPrefHeight());
                                content.getChildren().add(noMatch);
                            }

                            splitPane.getItems().addAll(sp, returnButtonBox);

                            splitPane.setDividerPosition(0, 0.7);

                            Scene resultsScene = new Scene(splitPane, 640, 480);

                            stage.setScene(resultsScene);
                        }
                        //add an else statement to alert the user to enter a search query in the UI
                    }
                });
    }


    // Searches through the documents
    // Sorts them from best match to worst
    public void doSearch(String[] words){
        for (int i = 0; i < docList.size(); i++){
            docList.get(i).setScore(words);
        }
        Collections.sort(docList, Collections.reverseOrder());
    }

    // main
    public static void main(String[] args) {
        Application.launch(args);
    }

}