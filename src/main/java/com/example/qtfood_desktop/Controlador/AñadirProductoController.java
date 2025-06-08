package com.example.qtfood_desktop.Controlador;

import com.example.qtfood_desktop.Modelo.Categoria;
import com.example.qtfood_desktop.Vista.App;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.util.UUID;

import static com.example.qtfood_desktop.Vista.App.mostrarAlerta;
/**
 * Controlador para la interfaz de añadir un nuevo producto.
 * <p>
 * Permite ingresar los datos del producto, seleccionar una imagen,
 * validar campos y guardar el producto en la base de datos.
 * También notifica a {@link ProductosController} para refrescar la tabla de productos.
 * </p>
 */
public class AñadirProductoController {
    @FXML private TextField nombreField;
    @FXML private TextArea descripcionField;
    @FXML private TextField precioField;
    @FXML private TextField stockField;
    @FXML private ComboBox<String> categoriaComboBox;
    @FXML private ImageView imagenProductoView;
    private File imagenSeleccionada;
    private ProductosController productosController;

    public void setProductosController(ProductosController productosController) {
        this.productosController = productosController;
    }

    @FXML
    private void initialize (){

        cargarCategoria(categoriaComboBox);
    }

    private void cargarCategoria(ComboBox<String> categoriaComboBox) {
        String sql = "SELECT nombre FROM categoria";
        try (Connection conexion = App.getConnection();
             PreparedStatement stmt = conexion.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                categoriaComboBox.getItems().add(rs.getString("nombre"));
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudieron cargar las categorías: " + e.getMessage());
        }
    }

    @FXML
    private void seleccionarImagen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar imagen del producto");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes", "*.jpg", "*.png"));
        imagenSeleccionada = fileChooser.showOpenDialog(nombreField.getScene().getWindow());

        if (imagenSeleccionada != null) {
            Image image = new Image(imagenSeleccionada.toURI().toString());
            imagenProductoView.setImage(image);
        }
    }

    // Método para agregar el producto a la base de datos
    @FXML
    private void añadirProducto() {
        try {
            String nombre = nombreField.getText();
            String descripcion = descripcionField.getText();
            double precio = Double.parseDouble(precioField.getText());
            int stock = Integer.parseInt(stockField.getText());
            String categoria = categoriaComboBox.getValue();

            if (stock <= 0) {
                mostrarAlerta("Stock inválido", "El stock debe ser mayor que cero.");
                return;
            }
            if (precio <= 0) {
                mostrarAlerta("Precui inválido", "El precio debe ser mayor que cero.");
                return;
            }

            String estado = "Inactivo";
            int idCategoria = 0;
            String sql2 = "SELECT id_categoria FROM categoria WHERE nombre = ?";
            try (Connection conexion = App.getConnection();
                 PreparedStatement stmt = conexion.prepareStatement(sql2)) {
                stmt.setString(1, categoria);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    idCategoria = rs.getInt("id_categoria");
                }
            }

            if (nombre.isEmpty() || descripcion.isEmpty() || categoria == null) {
                mostrarAlerta("Campos incompletos", "Completa todos los campos obligatorios.");
                return;
            }

            // Preparar URL de imagen, si no hay imagen seleccionada, dejar vacío
            String urlImagen = "";
            if (imagenSeleccionada != null) {
                String nombreImagen = imagenSeleccionada.getName();
                File destino = new File("C:/xampp/htdocs/imagenes/" + nombreImagen);
                Files.copy(imagenSeleccionada.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);
                urlImagen = "http://localhost/imagenes/" + nombreImagen;
            }

            String sql = "INSERT INTO productos(nombre, descripcion, precio, stock, imagen_url, id_categoria, estado) VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (Connection conexion = App.getConnection();
                 PreparedStatement stmt = conexion.prepareStatement(sql)) {

                stmt.setString(1, nombre);
                stmt.setString(2, descripcion);
                stmt.setDouble(3, precio);
                stmt.setInt(4, stock);
                stmt.setString(5, urlImagen);  // cadena vacía si no hay imagen
                stmt.setInt(6, idCategoria);
                stmt.setString(7, estado);
                stmt.executeUpdate();
            }

            mostrarAlerta("OK", "Producto añadido correctamente.");
            if (productosController != null) {
                productosController.refrescarTableView();
            }
            cerrarVentana();

        } catch (NumberFormatException e) {
            mostrarAlerta("Error de formato", "Precio o stock no válido.");
        } catch (IOException e) {
            mostrarAlerta("Error al copiar imagen", "No se pudo guardar la imagen en el servidor.");
            e.printStackTrace();
        } catch (SQLException e) {
            mostrarAlerta("Error en la base de datos", "No se pudo guardar el producto.");
            e.printStackTrace();
        }
    }


    // Método para limpiar los campos del formulario
    private void limpiarFormulario() {
        nombreField.clear();
        descripcionField.clear();
        precioField.clear();
        stockField.clear();
        categoriaComboBox.getSelectionModel().clearSelection();
        imagenProductoView.setImage(null);
    }

    private void cerrarVentana() {
        Stage stage = (Stage) nombreField.getScene().getWindow();
        stage.close();
    }

}
