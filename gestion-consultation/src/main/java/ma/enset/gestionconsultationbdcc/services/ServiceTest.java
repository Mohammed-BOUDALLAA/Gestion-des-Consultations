package ma.enset.gestionconsultationbdcc.services;

import ma.enset.gestionconsultationbdcc.dao.ConsultationDao;
import ma.enset.gestionconsultationbdcc.dao.PatientDao;
import ma.enset.gestionconsultationbdcc.entities.Patient;

import java.sql.SQLException;
import java.util.List;

public class ServiceTest {
    public static void main(String[] args) {
        try {
            PatientDao patientDao = new PatientDao();
            Patient patient = patientDao.findById(1L);  // Example ID

            if (patient != null) {
                System.out.println("Patient found:");
                System.out.println("ID: " + patient.getId());
                System.out.println("Nom: " + patient.getNom());
                System.out.println("Prenom: " + patient.getPrenom());
                System.out.println("Tel: " + patient.getTel());
            } else {
                System.out.println("No patient found with that ID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
