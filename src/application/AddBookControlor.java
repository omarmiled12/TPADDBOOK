package application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DefaultStringConverter;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class AddBookControlor implements Initializable {

    @FXML
    private TextField tfLastName;
    @FXML
    private TextField tfFirstName;
    @FXML
    private TextField tfEmail;
    @FXML
    private Button addBtn;
    @FXML
    private Button exportBtn;
    @FXML
    private Button importBtn;
    @FXML
    private Button removeBtn;
    @FXML
    private Button quitBtn;
    @FXML
    private TableView<Person> table;
    @FXML
    private TableColumn<Person, String> emailCol;
    @FXML
    private TableColumn<Person, String> firstNameCol;
    @FXML
    private TableColumn<Person, String> lastNameCol;

    private DataClass data;
    private ObservableList<Person> observableList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        data = new DataClass();
        observableList = FXCollections.observableArrayList();
        
        // Configuration des colonnes
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        
        // Rendre la table éditable
        table.setEditable(true);
        
        // Configurer l'édition des cellules
        firstNameCol.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        lastNameCol.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        emailCol.setCellFactory(TextFieldTableCell.forTableColumn(new DefaultStringConverter()));
        
        // Gestion des modifications
        firstNameCol.setOnEditCommit(event -> {
            Person person = event.getTableView().getItems().get(event.getTablePosition().getRow());
            person.setFirstName(event.getNewValue());
        });
        
        lastNameCol.setOnEditCommit(event -> {
            Person person = event.getTableView().getItems().get(event.getTablePosition().getRow());
            person.setLastName(event.getNewValue());
        });
        
        emailCol.setOnEditCommit(event -> {
            if (isEmailAdress(event.getNewValue())) {
                Person person = event.getTableView().getItems().get(event.getTablePosition().getRow());
                person.setEmail(event.getNewValue());
            } else {
                showAlert("Email invalide", "L'adresse email n'est pas valide.");
                table.refresh();
            }
        });
    }

    @FXML
    private void handleAddButton() {
        String firstName = tfFirstName.getText().trim();
        String lastName = tfLastName.getText().trim();
        String email = tfEmail.getText().trim();
        
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
            showAlert("Champs vides", "Tous les champs doivent être remplis.");
            return;
        }
        
        if (!isEmailAdress(email)) {
            showAlert("Email invalide", "L'adresse email n'est pas valide.");
            return;
        }
        
        Person person = new Person(firstName, lastName, email);
        observableList.add(person);
        table.setItems(observableList);
        
        // Vider les champs
        tfFirstName.clear();
        tfLastName.clear();
        tfEmail.clear();
    }

    @FXML
    private void handleRemoveButton() {
        Person selectedPerson = table.getSelectionModel().getSelectedItem();
        if (selectedPerson != null) {
            observableList.remove(selectedPerson);
        } else {
            showAlert("Aucune sélection", "Veuillez sélectionner une personne à supprimer.");
        }
    }

    @FXML
    private void handleImportButton() {
        observableList.addAll(data.getImportList());
        table.setItems(observableList);
    }

    @FXML
    private void handleExportButton() {
        data.setExportList(observableList);
    }

    @FXML
    private void handleQuitButton() {
        System.exit(0);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static boolean isEmailAdress(String email) {
        Pattern p = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}$", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(email);
        return m.matches();
    }
}