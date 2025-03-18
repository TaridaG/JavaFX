package com.example.librarymanage;

import com.example.librarymanage.database.Database;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MembersController {

    @FXML private TextField memberIdField;
    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private Button fetchMemberButton;
    @FXML private Button saveMemberButton;
    @FXML private Button updateMemberButton;
    @FXML private Button deleteMemberButton;
    @FXML private Button closeMemberButton;

    @FXML
    public void initialize() {
        System.out.println("✅ MembersController initialized.");
    }

    // 🔍 Kullanıcıyı ID’ye göre getir
    @FXML
    private void fetchMember() {
        String input = memberIdField.getText();
        String query;

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(
                     input.matches("\\d+") ? "SELECT * FROM members WHERE id = ?" :
                             "SELECT * FROM members WHERE name ILIKE ?")) {

            if (input.matches("\\d+")) { // Eğer sadece rakam içeriyorsa ID olarak arama yap
                pstmt.setInt(1, Integer.parseInt(input));
            } else { // Eğer metinse, isim ile arama yap
                pstmt.setString(1, "%" + input + "%"); // İsmin geçtiği yerleri bul
            }

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                nameField.setText(rs.getString("name"));
                emailField.setText(rs.getString("email"));
                memberIdField.setText(String.valueOf(rs.getInt("id"))); // ID'yi de gösterebiliriz
                System.out.println("✅ Member fetched: " + rs.getString("name"));
            } else {
                System.out.println("⚠ No member found with ID or Name: " + input);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    // 💾 Yeni üye ekle
    @FXML
    private void saveMember() {
        String name = nameField.getText();
        String email = emailField.getText();
        String query = "INSERT INTO members (name, email) VALUES (?, ?)";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.executeUpdate();
            System.out.println("✅ Member saved: " + name);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 📝 Üye bilgilerini güncelle
    @FXML
    private void updateMember() {
        String memberId = memberIdField.getText();
        String name = nameField.getText();
        String email = emailField.getText();
        String query = "UPDATE members SET name = ?, email = ? WHERE id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setInt(3, Integer.parseInt(memberId));
            pstmt.executeUpdate();
            System.out.println("✅ Member updated: " + name);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 🗑 Üyeyi sil
    @FXML
    private void deleteMember() {
        String memberId = memberIdField.getText();
        String query = "DELETE FROM members WHERE id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, Integer.parseInt(memberId));
            pstmt.executeUpdate();
            System.out.println("✅ Member deleted with ID: " + memberId);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
    @FXML
    private void closeWindow() {
        System.out.println("Closing member window.");
        closeMemberButton.getScene().getWindow().hide(); // Pencereyi kapatır
    }

}
