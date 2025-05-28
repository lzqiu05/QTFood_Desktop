package com.example.qtfood_desktop.Controlador;

import com.example.qtfood_desktop.Modelo.Admin;
import com.example.qtfood_desktop.Modelo.Categoria;
import com.example.qtfood_desktop.Vista.App;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Optional;

import static com.example.qtfood_desktop.Vista.App.mostrarAlerta;

public class pantallaPrincipalController {

    @FXML
    private BorderPane rootPane;
    @FXML
    private MenuButton menuReservas;

    @FXML
    private void gestionarPedido() throws IOException {
        loadFXML(getClass().getResource("/com/example/qtfood_desktop/Vistas/PedidoSearch.fxml"));

    }
    @FXML
    private void gestionarProducto() throws IOException {
        loadFXML(getClass().getResource("/com/example/qtfood_desktop/Vistas/ProductoSearch.fxml"));

    }
    @FXML
    private void gestionarUsuarios() throws IOException {
        loadFXML(getClass().getResource("/com/example/qtfood_desktop/Vistas/UsuarioSearch.fxml"));

    }
    @FXML
    private void gestionarOferta() throws IOException {
        loadFXML(getClass().getResource("/com/example/qtfood_desktop/Vistas/OfertasSearch.fxml"));

    }
    @FXML
    public void gestionarReservas() throws IOException {
        loadFXML(getClass().getResource("/com/example/qtfood_desktop/Vistas/ReservasSearch.fxml"));
    }


    @FXML
    private void initialize() {

    }
    private void loadFXML(URL url) throws IOException {

        FXMLLoader loader = new FXMLLoader(url);
        rootPane.setCenter(loader.load());

    }

    public void setBottom(Node nodo) {
        rootPane.setBottom(nodo);
    }

    public void cambiarContrasena(ActionEvent actionEvent) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Cambiar contraseña");
        dialog.setHeaderText(null);
        dialog.setContentText("Introduce la nueva contraseña:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(contrasenia -> {
            contrasenia = contrasenia.trim();

            // Validación: campo vacío
            if (contrasenia.isEmpty()) {
                mostrarAlerta("Error", "La contraseña no puede estar vacía.");
                return;
            }

            try {
                // Cargar el objeto Admin existente
                Admin admin;
                File archivo = new File("admin.dat");
                if (archivo.exists()) {
                    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
                        admin = (Admin) ois.readObject();
                    }
                } else {
                    // Si no existe, creamos uno nuevo
                    admin = new Admin();
                }

                admin.setPassword(contrasenia);

                // Guardar el objeto actualizado
                try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("admin.dat"))) {
                    oos.writeObject(admin);
                }

                mostrarAlerta("Éxito", "Contraseña actualizada correctamente.");

            } catch (Exception e) {
                e.printStackTrace();
                mostrarAlerta("Error", "Ocurrió un error al guardar la contraseña.");
            }
        });
    }

    public void cerrarSesion(ActionEvent actionEvent) throws IOException {
        App.setRoot("Login");
    }


}
