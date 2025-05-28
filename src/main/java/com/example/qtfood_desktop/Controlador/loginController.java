package com.example.qtfood_desktop.Controlador;

import com.example.qtfood_desktop.BD.Consultas;
import com.example.qtfood_desktop.Modelo.Admin;
import com.example.qtfood_desktop.Vista.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class loginController {
    @FXML
    private Label generalError;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private ImageView logoImageView;

    @FXML
    private void login(ActionEvent actionEvent) {
        String user = username.getText();
        String pass = password.getText();

        if (user.isEmpty() || pass.isEmpty()) {
            generalError.setText("Por favor, completa todos los campos.");
            generalError.setVisible(true);
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("admin.dat"))) {
            Admin admin = (Admin) ois.readObject();
            if (user.equals(admin.getUsuario()) && pass.equals(admin.getPassword())) {
                generalError.setText("Usuario o contraseña incorrectos.");
                OpenDashboard();
            } else {
                generalError.setText("Usuario o contraseña incorrectos.");
                generalError.setVisible(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void OpenDashboard() throws IOException {
        App.setRoot("PantallaPrincipal");
    }


    @FXML
    public void initialize() {
        generalError.setVisible(false);
        try {
            String imagePath = "/com/example/qtfood_desktop/img/logo-Photoroom.png";
            URL imageURL = getClass().getResource(imagePath);

            if (imageURL == null) {
                System.err.println("No se encontró el archivo de imagen en la ruta: " + imagePath);
            } else {
                Image logo = new Image(imageURL.toString());
                logoImageView.setImage(logo);
            }
        } catch (Exception e) {
            System.err.println("Error al cargar el logo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
