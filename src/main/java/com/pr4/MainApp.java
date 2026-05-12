package com.pr4;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.Year;
import java.util.UUID;

public class MainApp extends Application {
    private static final int CURRENT_YEAR = Year.now().getValue();

    private final Library library = new Library();
    private final ObservableList<String> listItems = FXCollections.observableArrayList();

    @Override
    public void start(Stage stage) {
        TextField titleField = new TextField();
        TextField authorField = new TextField();
        TextField yearField = new TextField();
        TextField pagesField = new TextField();
        ComboBox<BookGenre> genreBox = new ComboBox<>();
        genreBox.getItems().addAll(BookGenre.values());
        genreBox.setValue(BookGenre.GENERAL);
        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("GeneralBook", "EBook", "PaperBook", "AudioBook", "TextBook");
        typeBox.setValue("GeneralBook");
        Label extraOneLabel = new Label("Format");
        TextField extraOneField = new TextField();
        Label extraTwoLabel = new Label("File size (MB)");
        TextField extraTwoField = new TextField();
        updateTypeFields("GeneralBook", extraOneLabel, extraOneField, extraTwoLabel, extraTwoField);

        Button addButton = new Button("Add");
        addButton.setMaxWidth(Double.MAX_VALUE);

        ListView<String> listView = new ListView<>(listItems);

        TextField uuidSearchField = new TextField();
        uuidSearchField.setPromptText("Enter UUID");
        Button findButton = new Button("Find");
        TextArea detailsArea = new TextArea();
        detailsArea.setEditable(false);
        detailsArea.setWrapText(true);

        typeBox.setOnAction(event -> updateTypeFields(typeBox.getValue(), extraOneLabel, extraOneField, extraTwoLabel, extraTwoField));

        addButton.setOnAction(event -> {
            try {
                Book book = createBook(
                        typeBox.getValue(),
                        titleField.getText(),
                        authorField.getText(),
                        parseInt(yearField.getText(), "Year", 1, CURRENT_YEAR),
                        parseInt(pagesField.getText(), "Pages", 1, Integer.MAX_VALUE),
                        genreBox.getValue(),
                        extraOneField.getText(),
                        extraTwoField.getText()
                );
                library.addBook(book);
                refreshList();
                titleField.clear();
                authorField.clear();
                yearField.clear();
                pagesField.clear();
                extraOneField.clear();
                extraTwoField.clear();
                detailsArea.setText("Created:\n" + book);
            } catch (RuntimeException e) {
                showError(e.getMessage());
            }
        });

        findButton.setOnAction(event -> {
            String rawUuid = uuidSearchField.getText();
            if (rawUuid == null || rawUuid.isBlank()) {
                showError("UUID must not be blank");
                return;
            }
            try {
                UUID uuid = UUID.fromString(rawUuid.trim());
                Book found = library.findByUuid(uuid);
                if (found == null) {
                    detailsArea.setText("Object was not found.");
                } else {
                    detailsArea.setText(found.toString());
                }
            } catch (IllegalArgumentException e) {
                showError("Invalid UUID format");
            }
        });

        GridPane form = new GridPane();
        form.setHgap(8);
        form.setVgap(8);
        form.addRow(0, new Label("Type"), typeBox);
        form.addRow(1, new Label("Title"), titleField);
        form.addRow(2, new Label("Author"), authorField);
        form.addRow(3, new Label("Year"), yearField);
        form.addRow(4, new Label("Pages"), pagesField);
        form.addRow(5, new Label("Genre"), genreBox);
        form.addRow(6, extraOneLabel, extraOneField);
        form.addRow(7, extraTwoLabel, extraTwoField);
        form.add(addButton, 1, 8);
        GridPane.setHgrow(titleField, Priority.ALWAYS);
        GridPane.setHgrow(authorField, Priority.ALWAYS);
        GridPane.setHgrow(yearField, Priority.ALWAYS);
        GridPane.setHgrow(pagesField, Priority.ALWAYS);
        GridPane.setHgrow(typeBox, Priority.ALWAYS);
        GridPane.setHgrow(genreBox, Priority.ALWAYS);
        GridPane.setHgrow(extraOneField, Priority.ALWAYS);
        GridPane.setHgrow(extraTwoField, Priority.ALWAYS);

        HBox searchRow = new HBox(8, new Label("UUID"), uuidSearchField, findButton);
        HBox.setHgrow(uuidSearchField, Priority.ALWAYS);

        VBox rightPane = new VBox(10, new Label("Search by UUID"), searchRow, new Label("Details"), detailsArea);
        VBox.setVgrow(detailsArea, Priority.ALWAYS);

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(12));
        root.setLeft(form);
        root.setCenter(new VBox(8, new Label("Collection (title + UUID)"), listView));
        root.setRight(rightPane);
        BorderPane.setMargin(form, new Insets(0, 12, 0, 0));
        BorderPane.setMargin(rightPane, new Insets(0, 0, 0, 12));
        VBox.setVgrow(listView, Priority.ALWAYS);

        Scene scene = new Scene(root, 1200, 550);
        stage.setTitle("Library UUID Search");
        stage.setScene(scene);
        stage.show();
    }

    private Book createBook(String type, String title, String author, int year, int pages, BookGenre genre, String extraOne, String extraTwo) {
        if ("EBook".equals(type)) {
            return new EBook(title, author, year, pages, genre, extraOne, parseDouble(extraTwo, "File size", 0.0));
        }
        if ("PaperBook".equals(type)) {
            return new PaperBook(title, author, year, pages, genre, extraOne, parseInt(extraTwo, "Print run", 1, Integer.MAX_VALUE));
        }
        if ("AudioBook".equals(type)) {
            return new AudioBook(title, author, year, pages, genre, parseInt(extraOne, "Duration (minutes)", 1, Integer.MAX_VALUE), extraTwo);
        }
        if ("TextBook".equals(type)) {
            return new TextBook(title, author, year, pages, genre, extraOne, parseInt(extraTwo, "Grade level", 1, Integer.MAX_VALUE));
        }
        return new GeneralBook(title, author, year, pages, genre);
    }

    private void updateTypeFields(String type, Label extraOneLabel, TextField extraOneField, Label extraTwoLabel, TextField extraTwoField) {
        extraOneField.setVisible(true);
        extraOneField.setManaged(true);
        extraOneLabel.setVisible(true);
        extraOneLabel.setManaged(true);
        extraTwoField.setVisible(true);
        extraTwoField.setManaged(true);
        extraTwoLabel.setVisible(true);
        extraTwoLabel.setManaged(true);
        if ("EBook".equals(type)) {
            extraOneLabel.setText("Format");
            extraTwoLabel.setText("File size (MB)");
            return;
        }
        if ("PaperBook".equals(type)) {
            extraOneLabel.setText("Publisher");
            extraTwoLabel.setText("Print run");
            return;
        }
        if ("AudioBook".equals(type)) {
            extraOneLabel.setText("Duration (minutes)");
            extraTwoLabel.setText("Narrator");
            return;
        }
        if ("TextBook".equals(type)) {
            extraOneLabel.setText("Subject");
            extraTwoLabel.setText("Grade level");
            return;
        }
        extraOneLabel.setVisible(false);
        extraOneLabel.setManaged(false);
        extraOneField.setVisible(false);
        extraOneField.setManaged(false);
        extraTwoLabel.setVisible(false);
        extraTwoLabel.setManaged(false);
        extraTwoField.setVisible(false);
        extraTwoField.setManaged(false);
    }

    private void refreshList() {
        listItems.clear();
        for (Book book : library.getBooks()) {
            listItems.add(book.getTitle() + " | UUID: " + book.getUuid());
        }
    }

    private int parseInt(String rawValue, String fieldName, int min, int max) {
        if (rawValue == null || rawValue.isBlank()) {
            throw new InvalidBookDataException(fieldName + " must not be blank");
        }
        try {
            int value = Integer.parseInt(rawValue.trim());
            if (value < min || value > max) {
                throw new InvalidBookDataException(fieldName + " must be between " + min + " and " + max);
            }
            return value;
        } catch (NumberFormatException e) {
            throw new InvalidBookDataException(fieldName + " must be a valid integer");
        }
    }

    private double parseDouble(String rawValue, String fieldName, double minExclusive) {
        if (rawValue == null || rawValue.isBlank()) {
            throw new InvalidBookDataException(fieldName + " must not be blank");
        }
        try {
            double value = Double.parseDouble(rawValue.trim());
            if (value <= minExclusive) {
                throw new InvalidBookDataException(fieldName + " must be greater than " + minExclusive);
            }
            return value;
        } catch (NumberFormatException e) {
            throw new InvalidBookDataException(fieldName + " must be a valid number");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
