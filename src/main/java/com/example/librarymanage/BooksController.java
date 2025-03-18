package com.example.librarymanage;

import com.example.librarymanage.database.Database;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BooksController {

    @FXML private TextField bookIdField;
    @FXML private TextField titleField;
    @FXML private TextField authorField;
    @FXML private Button fetchBookButton;
    @FXML private Button saveBookButton;
    @FXML private Button updateBookButton;
    @FXML private Button deleteBookButton;
    @FXML private Button closeBookButton;

    @FXML
    public void initialize() {
        System.out.println("✅ BooksController initialized.");
    }

    // 🔍 Kitabı ID, başlık veya yazara göre getir
    @FXML
    private void fetchBook() {
        String input = bookIdField.getText();
        String query;

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(
                     input.matches("\\d+") ? "SELECT * FROM books WHERE id = ?" :
                             "SELECT * FROM books WHERE title ILIKE ? OR author ILIKE ?")) {

            if (input.matches("\\d+")) { // Eğer sadece rakam içeriyorsa ID olarak arama yap
                pstmt.setInt(1, Integer.parseInt(input));
            } else { // Eğer metinse, başlık veya yazar adıyla arama yap
                pstmt.setString(1, "%" + input + "%"); // Başlıkta geçenleri bul
                pstmt.setString(2, "%" + input + "%"); // Yazar adında geçenleri bul
            }

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                bookIdField.setText(String.valueOf(rs.getInt("id"))); // ID'yi göster
                titleField.setText(rs.getString("title"));
                authorField.setText(rs.getString("author"));

                System.out.println("✅ Book fetched: " + rs.getString("title") + " by " + rs.getString("author"));
            } else {
                System.out.println("⚠ No book found with ID, Title, or Author: " + input);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 💾 Yeni kitap ekle
    @FXML
    private void saveBook() {
        String title = titleField.getText();
        String author = authorField.getText();
        String query = "INSERT INTO books (title, author) VALUES (?, ?)";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, title);
            pstmt.setString(2, author);
            pstmt.executeUpdate();
            System.out.println("✅ Book saved: " + title + " by " + author);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 📝 Kitap bilgilerini güncelle
    @FXML
    private void updateBook() {
        String bookId = bookIdField.getText();
        String title = titleField.getText();
        String author = authorField.getText();
        String query = "UPDATE books SET title = ?, author = ? WHERE id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, title);
            pstmt.setString(2, author);
            pstmt.setInt(3, Integer.parseInt(bookId));
            pstmt.executeUpdate();
            System.out.println("✅ Book updated: " + title + " by " + author);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 🗑 Kitabı sil
    @FXML
    private void deleteBook() {
        String bookId = bookIdField.getText();
        String query = "DELETE FROM books WHERE id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, Integer.parseInt(bookId));
            pstmt.executeUpdate();
            System.out.println("✅ Book deleted with ID: " + bookId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ❌ Pencereyi kapat
    @FXML
    private void closeWindow() {
        System.out.println("❌ Closing book window.");
        Stage stage = (Stage) closeBookButton.getScene().getWindow();
        stage.close();
    }
}
