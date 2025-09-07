package ma.enset.gestionconsultationbdcc.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Paint;
import javafx.util.Duration;
import ma.enset.gestionconsultationbdcc.dao.ConsultationDao;
import ma.enset.gestionconsultationbdcc.dao.PatientDao;
import ma.enset.gestionconsultationbdcc.entities.Patient;
import ma.enset.gestionconsultationbdcc.services.CabinetService;
import ma.enset.gestionconsultationbdcc.services.ICabinetService;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.Timer;

public class PatientController implements Initializable {

    @FXML private TextField textFieldNom;
    @FXML private TextField textFieldPrenom;
    @FXML private TextField textFieldTel;
    @FXML private TextField textFieldSearch;
    @FXML private TableView<Patient> tablePatients;
    @FXML private TableColumn<Patient,Long> columnId;
    @FXML private TableColumn<Patient,String> columnNom;
    @FXML private TableColumn<Patient,String> columnPrenom;
    @FXML private TableColumn<Patient,String> columnTel;
    @FXML private Label labelSearchState;
    private ICabinetService cabinetService;
    private ObservableList<Patient> patients = FXCollections.observableArrayList();
    private Patient selectedPatient;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cabinetService = new CabinetService(new ConsultationDao(),new PatientDao());

        columnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnNom.setCellValueFactory(new PropertyValueFactory<>("nom"));
        columnPrenom.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        columnTel.setCellValueFactory(new PropertyValueFactory<>("tel"));

        tablePatients.setItems(patients);
        loadPatients();
        textFieldSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            patients.setAll(cabinetService.searchPatientsByQuery(newValue));
        });

        tablePatients.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
           if(newValue != null) {
               textFieldNom.setText(newValue.getNom());
               textFieldPrenom.setText(newValue.getPrenom());
               textFieldTel.setText(newValue.getTel());
               selectedPatient = newValue;
           }
        });
    }

    private void loadPatients(){
        patients.setAll(cabinetService.getPatients());
    }

    public void ajouterPatient(){
        Patient patient = new Patient();
        patient.setNom(textFieldNom.getText());
        patient.setPrenom(textFieldPrenom.getText());
        patient.setTel(textFieldTel.getText());

        try {
            if(textFieldNom.getText().isEmpty() || textFieldPrenom.getText().isEmpty() || textFieldTel.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("ATTENTION ⛔📛💥");
                alert.setHeaderText("Veuillez remplir tous les champs du patient");
                alert.showAndWait();
                return;
            }
            else {
                cabinetService.addPatient(patient);
                labelSearchState.setText("Le patient a été ajouté.");
                // Réinitialiser le label après 3 secondes (3000 ms)
                Timeline timeline = new Timeline(new KeyFrame(
                        Duration.seconds(3),
                        event -> labelSearchState.setText("")
                ));
                timeline.setCycleCount(1);
                timeline.play();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        loadPatients();
        clearTextFields();
    }

    public void delPatient(){
        Patient patient = tablePatients.getSelectionModel().getSelectedItem();
        if(patient==null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ATTENTION ⛔📛💥");
            alert.setHeaderText("Veuillez selectionner un patient avant la suppression");
            alert.showAndWait();
            return;
        }
        cabinetService.deletePatient(patient);
        labelSearchState.setText("Le patient a été supprimé.");
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3),event->{
            labelSearchState.setText("");
        }));
        timeline.setCycleCount(1);
        timeline.play();
        loadPatients();
    }


        public void modifierPatient(){
            if(selectedPatient == null){
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ATTENTION ⛔📛💥");
                alert.setHeaderText("Veuillez selectionner un patient avant la modification");
                alert.showAndWait();
            }
            else {
                selectedPatient.setNom(textFieldNom.getText());
                selectedPatient.setPrenom(textFieldPrenom.getText());
                selectedPatient.setTel(textFieldTel.getText());
                cabinetService.updatePatient(selectedPatient);
                labelSearchState.setText("Le patient a été modifié.");
                Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3),event->{
                    labelSearchState.setText("");
                }));
                timeline.setCycleCount(1);
                timeline.play();
                loadPatients();
                clearTextFields();
                selectedPatient = null;
            }

        }


        private void clearTextFields(){
            textFieldNom.setText("");
            textFieldPrenom.setText("");
            textFieldTel.setText("");
        }


}
