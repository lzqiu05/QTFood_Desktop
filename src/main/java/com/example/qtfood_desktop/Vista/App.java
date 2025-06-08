package com.example.qtfood_desktop.Vista;

import com.example.qtfood_desktop.Modelo.Admin;
import com.example.qtfood_desktop.Modelo.Pedido;
import com.example.qtfood_desktop.Modelo.Producto;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.sql.*;

/**
 * Clase principal de la aplicación QTFood basada en JavaFX.
 * Gestiona la conexión a la base de datos, la navegación entre vistas
 * y el control de objetos globales como el usuario, pedido y producto seleccionados.
 */
public class App extends Application {
    private static Scene scene;
    private static final String URL = "jdbc:mysql://localhost:3306/qtfood2";
    private static final String USER = "root"; // Cambia esto por tu usuario
    private static final String PASSWORD = "";
    private static Admin usuarioActual;
    private static Pedido pedidoSeleccionado;
    private static Producto productoSeleccionado;

    public static Producto getProductoSeleccionado() {
        return productoSeleccionado;
    }

    public static void setProductoSeleccionado(Producto productoSeleccionado) {
        App.productoSeleccionado = productoSeleccionado;
    }

    public static Pedido getPedidoSeleccionado() {
        return pedidoSeleccionado;
    }

    public static void setPedidoSeleccionado(Pedido pedidoSeleccionado) {
        App.pedidoSeleccionado = pedidoSeleccionado;
    }

    public static Admin getUsuarioActual() {
        return usuarioActual;
    }

    public static void setUsuarioActual(Admin usuarioActual) {

        App.usuarioActual = usuarioActual;
    }
    public static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public static boolean showConfirmationDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        ButtonType buttonYes = new ButtonType("Sí", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonNo = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);

        alert.getButtonTypes().setAll(buttonYes, buttonNo);

        return alert.showAndWait().orElse(buttonNo) == buttonYes;
    }
    public static void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }


    public static Connection getConnection() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Conexión exitosa a la base de datos.");
        } catch (SQLException e) {
            System.err.println("Error de conexión a la base de datos: " + e.getMessage());
        }
        return connection;
    }
    @Override
    public void start(Stage stage) throws IOException {
        // Cargar la escena inicial
        scene = new Scene(loadFXML("Login"), 600, 400);
        stage.setTitle("QTFood!");
        stage.setScene(scene);
        stage.show();
    }

    public static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    public static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("/com/example/qtfood_desktop/" + fxml + ".fxml"));
        return fxmlLoader.load();
    }
    public static void main(String[] args) {
        Font.loadFont(App.class.getResourceAsStream("/com/example/qtfood_desktop/font/fantasia.ttf"), 14);

        launch(); // Iniciar la aplicación JavaFX
    }

}
