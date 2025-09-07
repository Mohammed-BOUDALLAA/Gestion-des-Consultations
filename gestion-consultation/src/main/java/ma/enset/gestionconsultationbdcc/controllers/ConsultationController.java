package ma.enset.gestionconsultationbdcc.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;
import ma.enset.gestionconsultationbdcc.dao.ConsultationDao;
import ma.enset.gestionconsultationbdcc.dao.PatientDao;
import ma.enset.gestionconsultationbdcc.entities.Consultation;
import ma.enset.gestionconsultationbdcc.entities.Patient;
import ma.enset.gestionconsultationbdcc.services.CabinetService;
import ma.enset.gestionconsultationbdcc.services.ICabinetService;

import java.net.URL;
import java.sql.Date;
import java.util.List;
import java.util.ResourceBundle;

public class ConsultationController implements Initializable {


    @FXML private TableView<Consultation> tableConsultation;
    @FXML private TableColumn<Consultation, Long> columnId;
    @FXML private TableColumn<Consultation, Date> columnDateConsultation;
    @FXML private TableColumn<Consultation, String> columnDescription ;
    @FXML private TableColumn<Consultation, Patient> columnPatient;
    @FXML private DatePicker datePickerConsultation;
    @FXML private ComboBox<Patient> comboBoxPatient;
    @FXML private TextArea textareaDescription;
    @FXML private TextField textFieldSearch;
    @FXML private Label statusLabel;
    private ICabinetService cabinetService;
    private ObservableList<Patient> patients = FXCollections.observableArrayList();
    private ObservableList<Consultation> consultations = FXCollections.observableArrayList();
    private Consultation selectedConsultation;

    private void clearFields(){
        datePickerConsultation.setValue(null);
        textareaDescription.clear();
        comboBoxPatient.setValue(null);
    }

    private void loadConsultations() {
        consultations.setAll(cabinetService.getConsultations());
    }

    public void loadPatients() {
        patients.setAll(cabinetService.getPatients());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cabinetService = new CabinetService(new ConsultationDao(),new PatientDao());
        loadPatients();
        comboBoxPatient.setItems(patients);


        columnId.setCellValueFactory(new PropertyValueFactory<>("id"));
        columnDateConsultation.setCellValueFactory(new PropertyValueFactory<>("date"));
        columnDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        columnPatient.setCellValueFactory(new PropertyValueFactory<>("patient"));

        //Load Patient to comboBox
        loadConsultations();
        tableConsultation.setItems(consultations);

        textFieldSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            consultations.setAll(cabinetService.searchConsultationsByQuery(newValue));
        });

        tableConsultation.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue!=null){
                textareaDescription.setText(newValue.getDescription());
                datePickerConsultation.setValue(newValue.getDate().toLocalDate());
                comboBoxPatient.setValue(newValue.getPatient());
                selectedConsultation = newValue;
            }
        });




    }
    public void ajouterConsultation() {
        if (textareaDescription.getText().isEmpty() || datePickerConsultation.getValue() == null || comboBoxPatient.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("ATTENTION ⛔📛💥");
            alert.setHeaderText("Veuillez remplir tous les champs de la consultation !");
            alert.showAndWait();
            return;
        }

        Consultation consultation = new Consultation();
        consultation.setDate(Date.valueOf(datePickerConsultation.getValue()));
        consultation.setPatient(comboBoxPatient.getSelectionModel().getSelectedItem());
        consultation.setDescription(textareaDescription.getText());

        try {
            cabinetService.addConsultation(consultation);
            statusLabel.setText("La consultation a été ajoutée.");

            // Réinitialiser le label après 3 secondes
            Timeline timeline = new Timeline(new KeyFrame(
                    Duration.seconds(3),
                    event -> statusLabel.setText("")
            ));
            timeline.setCycleCount(1);
            timeline.play();
        } catch (Exception e) {
            e.printStackTrace(); // Afficher l'erreur en console pour diagnostic
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur ❌");
            alert.setHeaderText("Une erreur s'est produite lors de l'ajout de la consultation.");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }

        loadConsultations();
        clearFields();
    }

    public void modifierConsultation(){
        if(selectedConsultation==null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ATTENTION ⛔📛💥");
            alert.setHeaderText("Veuillez selectionner une consultation avant la modification");
            alert.showAndWait();
        }
        else {
            selectedConsultation.setDate(Date.valueOf(datePickerConsultation.getValue()));
            selectedConsultation.setPatient(comboBoxPatient.getSelectionModel().getSelectedItem());
            selectedConsultation.setDescription(textareaDescription.getText());
            cabinetService.updateConsultation(selectedConsultation);
            statusLabel.setText("La consultation a été modifiée.");
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3), event->{
                statusLabel.setText("");
            }));
            timeline.setCycleCount(1);
            timeline.play();
            loadConsultations();
            clearFields();
            selectedConsultation=null;
        }

    }

    public void supprimerConsultation(){
        Consultation consultation = tableConsultation.getSelectionModel().getSelectedItem();
        if(consultation==null){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("ATTENTION ⛔📛💥");
            alert.setHeaderText("Veuillez selectionner une consultation avant la suppression");
            alert.showAndWait();
            return;
        }
        cabinetService.deleteConsultation(consultation);
        statusLabel.setText("La consultation a été supprimée.");
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(3),event->{
            statusLabel.setText("");
        }));
        timeline.setCycleCount(1);
        timeline.play();
        loadConsultations();

    }



}



