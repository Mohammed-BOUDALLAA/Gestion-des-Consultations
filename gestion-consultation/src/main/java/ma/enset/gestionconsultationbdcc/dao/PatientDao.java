package ma.enset.gestionconsultationbdcc.dao;

import ma.enset.gestionconsultationbdcc.entities.Patient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PatientDao implements IPatientDao {

    @Override
    public void create(Patient patient) throws SQLException {
        Connection con = DbConnection.getConnection();
        PreparedStatement pstm = con.prepareStatement(
                "INSERT INTO patients(nom, prenom, tel) VALUES (?, ?, ?)");
        pstm.setString(1, patient.getNom());
        pstm.setString(2, patient.getPrenom());
        pstm.setString(3, patient.getTel());
        pstm.executeUpdate();
        pstm.close();
    }

    @Override
    public void update(Patient patient) throws SQLException {
        Connection con = DbConnection.getConnection();
        PreparedStatement pstm = con.prepareStatement(
                "UPDATE patients SET nom=?, prenom=?, tel=? WHERE id = ?");
        pstm.setString(1, patient.getNom());
        pstm.setString(2, patient.getPrenom());
        pstm.setString(3, patient.getTel());
        pstm.setLong(4, patient.getId());
        pstm.executeUpdate();
        pstm.close();
    }

    @Override
    public void delete(Patient patient) throws SQLException {
        Connection con = DbConnection.getConnection();
        PreparedStatement pstm = con.prepareStatement(
                "DELETE FROM patients WHERE id = ?");
        pstm.setLong(1, patient.getId());
        pstm.executeUpdate();
        pstm.close();
    }

    @Override
    public Patient findById(Long id) throws SQLException {
        Connection con = DbConnection.getConnection();
        PreparedStatement pstm = con.prepareStatement(
                "SELECT * FROM patients WHERE id = ?");
        pstm.setLong(1, id);
        ResultSet rs = pstm.executeQuery();

        Patient patient = null;
        if (rs.next()) {
            patient = new Patient();
            patient.setId(rs.getLong("id"));
            patient.setNom(rs.getString("nom"));
            patient.setPrenom(rs.getString("prenom"));
            patient.setTel(rs.getString("tel"));
        }

        rs.close();
        pstm.close();
        return patient;  // This could be null if no patient found
    }

    @Override
    public List<Patient> findAll() throws SQLException {
        Connection con = DbConnection.getConnection();
        PreparedStatement pstm = con.prepareStatement(
                "SELECT * FROM patients");
        ResultSet rs = pstm.executeQuery();

        List<Patient> patients = new ArrayList<>();
        while (rs.next()) {
            Patient p = new Patient();
            p.setId(rs.getLong("id"));
            p.setNom(rs.getString("nom"));
            p.setPrenom(rs.getString("prenom"));
            p.setTel(rs.getString("tel"));
            patients.add(p);
        }

        rs.close();
        pstm.close();
        return patients;
    }

    @Override
    public List<Patient> searchByQuery(String query) throws SQLException {
        Connection con = DbConnection.getConnection();
        PreparedStatement pstm = con.prepareStatement(
                "SELECT * FROM patients WHERE nom LIKE ? OR prenom LIKE ?");
        pstm.setString(1, "%" + query + "%");
        pstm.setString(2, "%" + query + "%");
        ResultSet rs = pstm.executeQuery();

        List<Patient> patients = new ArrayList<>();
        while (rs.next()) {
            Patient p = new Patient();
            p.setId(rs.getLong("id"));
            p.setNom(rs.getString("nom"));
            p.setPrenom(rs.getString("prenom"));
            p.setTel(rs.getString("tel"));
            patients.add(p);
        }

        rs.close();
        pstm.close();
        return patients;
    }
}