package com.example.qtfood_desktop.Controlador;

import com.example.qtfood_desktop.Vista.App;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.example.qtfood_desktop.Vista.App.getProductoSeleccionado;
import static com.example.qtfood_desktop.Vista.App.mostrarAlerta;

/**
 * Controlador encargado de manejar la modificación de un producto existente.
 * <p>
 * Permite modificar los siguientes atributos:
 * nombre, descripción, precio, stock, categoría e imagen del producto.
 * Además, se encarga de validar y actualizar los datos en la base de datos.
 * </p>
 *
 * Utiliza JavaFX para la interfaz gráfica y JDBC para operaciones en la base de datos.
 */
public class ModificarProductoController {
    @FXML
    private TextField nombreField;
    @FXML private TextArea descripcionField;
    @FXML private TextField precioField;
    @FXML private TextField stockField;
    @FXML private ComboBox<String> categoriaComboBox;
    @FXML private ImageView imagenProductoView;
    private File imagenSeleccionada;

    private int idProducto = getProductoSeleccionado().getIdProducto();
    private String imagenUrlExistente;

    @FXML
    private void initialize() {
        cargarCategoria(categoriaComboBox);
        nombreField.setText(getProductoSeleccionado().getNombre());
        descripcionField.setText(getProductoSeleccionado().getDescripcion());
        precioField.setText(String.valueOf(getProductoSeleccionado().getPrecio()));
        stockField.setText(String.valueOf(getProductoSeleccionado().getStock()));
        categoriaComboBox.setValue(getProductoSeleccionado().getCategoria());
        imagenUrlExistente = getProductoSeleccionado().getImagenUrl();

        if (getProductoSeleccionado().getImagenUrl() != null && !getProductoSeleccionado().getImagenUrl().isEmpty()) {
            imagenProductoView.setImage(new Image(getProductoSeleccionado().getImagenUrl()));
        }
    }

    private void cargarCategoria(ComboBox<String> comboBox) {
        String sql = "SELECT nombre FROM categoria";
        try (Connection conexion = App.getConnection();
             PreparedStatement stmt = conexion.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                comboBox.getItems().add(rs.getString("nombre"));
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudieron cargar las categorías: " + e.getMessage());
        }
    }

    @FXML
    private void seleccionarImagen() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar nueva imagen del producto");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Imágenes", "*.jpg", "*.png"));
        fileChooser.setInitialDirectory(new File("C:\\Users\\Liang Zhi\\Desktop\\s\\Imagenes"));
        imagenSeleccionada = fileChooser.showOpenDialog(nombreField.getScene().getWindow());

        if (imagenSeleccionada != null) {
            Image image = new Image(imagenSeleccionada.toURI().toString());
            imagenProductoView.setImage(image);
        }
    }

    @FXML
    private void modificarProducto() {
        try {
            String nombre = nombreField.getText();
            String descripcion = descripcionField.getText();
            double precio = Double.parseDouble(precioField.getText());
            int stock = Integer.parseInt(stockField.getText());
            String categoria = categoriaComboBox.getValue();

            if (nombre.isEmpty() || descripcion.isEmpty() || categoria == null) {
                mostrarAlerta("Campos incompletos", "Completa todos los campos.");
                return;
            }
            if (stock <= 0) {
                mostrarAlerta("Stock inválido", "El stock debe ser mayor que cero.");
                return;
            }
            // Obtener id_categoria
            int idCategoria = 0;
            String sqlCategoria = "SELECT id_categoria FROM categoria WHERE nombre = ?";
            try (Connection conexion = App.getConnection();
                 PreparedStatement stmt = conexion.prepareStatement(sqlCategoria)) {
                stmt.setString(1, categoria);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    idCategoria = rs.getInt("id_categoria");
                }
            }

            // Copiar nueva imagen si se seleccionó
            String urlImagenFinal = imagenUrlExistente;
            if (imagenSeleccionada != null) {
                String nombreImagen = imagenSeleccionada.getName();
                File destino = new File("C:/xampp/htdocs/QTFood/imagenes/" + nombreImagen);
                Files.copy(imagenSeleccionada.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);
                urlImagenFinal = "http://localhost/QTFood/imagenes/" + nombreImagen;
            }

            // Actualizar producto
            String sqlUpdate = "UPDATE productos SET nombre = ?, descripcion = ?, precio = ?, stock = ?, imagen_url = ?, id_categoria = ? WHERE id_producto = ?";
            try (Connection conexion = App.getConnection();
                 PreparedStatement stmt = conexion.prepareStatement(sqlUpdate)) {
                stmt.setString(1, nombre);
                stmt.setString(2, descripcion);
                stmt.setDouble(3, precio);
                stmt.setInt(4, stock);
                stmt.setString(5, urlImagenFinal);
                stmt.setInt(6, idCategoria);
                stmt.setInt(7, idProducto);
                stmt.executeUpdate();
            }

            mostrarAlerta("Éxito", "Producto modificado correctamente.");

        } catch (NumberFormatException e) {
            mostrarAlerta("Error de formato", "Verifica que precio y stock sean números válidos.");
        } catch (IOException e) {
            mostrarAlerta("Error al copiar imagen", "No se pudo guardar la imagen.");
            e.printStackTrace();
        } catch (SQLException e) {
            mostrarAlerta("Error en la base de datos", "No se pudo actualizar el producto.");
            e.printStackTrace();
        }
    }


}
