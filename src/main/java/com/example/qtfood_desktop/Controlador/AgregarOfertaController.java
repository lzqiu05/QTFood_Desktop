package com.example.qtfood_desktop.Controlador;

import com.example.qtfood_desktop.Modelo.Producto;
import com.example.qtfood_desktop.Vista.App;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;
import java.util.stream.Collectors;

import static com.example.qtfood_desktop.Vista.App.mostrarAlerta;

public class AgregarOfertaController {

    @FXML private TextField campoBusqueda;
    @FXML private TableView<Producto> tablaProductos;

    @FXML private TextField campoDescuento;
    @FXML private DatePicker pickerFechaInicio;
    @FXML private DatePicker pickerFechaFin;
    @FXML private TextArea campoDescripcion;

    private ObservableList<Producto> productos = FXCollections.observableArrayList();
    private OfertaController ofertaController;

    public void setOfertaController(OfertaController controller) {
        this.ofertaController = controller;
    }

    @FXML
    public void initialize() {
        cargarTabla();
        cargarProductos();
        tablaProductos.setOnMousePressed(event -> {
            if (event.getTarget() == tablaProductos) {
                tablaProductos.getSelectionModel().clearSelection();
            }
        });
        // Deseleccionar si se hace clic fuera de la tabla
        tablaProductos.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnMousePressed(event -> {
                    if (!tablaProductos.localToScene(tablaProductos.getBoundsInLocal()).contains(event.getSceneX(), event.getSceneY())) {
                        tablaProductos.getSelectionModel().clearSelection();
                    }
                });
            }
        });
        // Filtrado dinámico
        campoBusqueda.addEventHandler(KeyEvent.KEY_RELEASED, event -> filtrarProductos());
    }

    private void cargarTabla() {
        tablaProductos.getColumns().clear();

        TableColumn<Producto, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        TableColumn<Producto, String> colCategoria = new TableColumn<>("Categoría");
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));

        TableColumn<Producto, Double> colPrecio = new TableColumn<>("Precio");
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colNombre.prefWidthProperty().bind(tablaProductos.widthProperty().multiply(0.49));
        colCategoria.prefWidthProperty().bind(tablaProductos.widthProperty().multiply(0.25));
        colPrecio.prefWidthProperty().bind(tablaProductos.widthProperty().multiply(0.25));

        tablaProductos.getColumns().addAll(colNombre, colCategoria, colPrecio);
        tablaProductos.setItems(productos);
    }

    private void cargarProductos() {
        productos.clear(); // Limpiar por si ya había datos

        String sql = "SELECT * FROM productos";

        try (Connection conexion = App.getConnection();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id_producto");
                String nombre = rs.getString("nombre");
                String descripcion = rs.getString("descripcion");
                double precio = rs.getDouble("precio");
                int stock = rs.getInt("stock");
                String imagenUrl = rs.getString("imagen_url");
                int idCategoria = rs.getInt("id_categoria");
                String estado = rs.getString("estado");

                // Obtener el nombre de la categoría
                String nombreCategoria = "";
                String sqlCategoria = "SELECT nombre FROM categoria WHERE id_categoria = ?";

                try (PreparedStatement stmt2 = conexion.prepareStatement(sqlCategoria)) {
                    stmt2.setInt(1, idCategoria);  // Aquí se establece el parámetro correctamente
                    ResultSet rs2 = stmt2.executeQuery();

                    if (rs2.next()) {
                        nombreCategoria = rs2.getString("nombre");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                Producto producto = new Producto(id, nombre, descripcion, precio, stock, imagenUrl, nombreCategoria, estado);
                productos.add(producto);
            }

            tablaProductos.setItems(productos);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void filtrarProductos() {
        String filtro = campoBusqueda.getText().toLowerCase();
        tablaProductos.setItems(productos.filtered(p ->
                p.getNombre().toLowerCase().contains(filtro) ||
                        p.getCategoria().toLowerCase().contains(filtro)
        ));
    }

    @FXML
    private void onGuardar() {
        Producto productoSeleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        String descuentoStr = campoDescuento.getText();
        String descripcion = campoDescripcion.getText();
        LocalDate fechaInicio = pickerFechaInicio.getValue();
        LocalDate fechaFin = pickerFechaFin.getValue();

        if (productoSeleccionado == null || descuentoStr.isEmpty() || descripcion.isEmpty() || fechaInicio == null || fechaFin == null) {
            mostrarAlerta("Todos los campos son obligatorios.");
            return;
        }

        try {
            double descuento = Double.parseDouble(descuentoStr);
            if (descuento > 100 ) {
                App.mostrarAlerta("Descuento inválido", "El descuento debe ser menor o igual que el 100 % .");
                return;
            }
            if (descuento <= 0 ) {
                App.mostrarAlerta("Descuento inválido", "El descuento debe ser mayor que 0 .");
                return;
            }
            insertarOferta(productoSeleccionado.getIdProducto(), descripcion, descuento, fechaInicio, fechaFin);
            // Refrescar directamente desde aquí
            if (ofertaController != null) {
                ofertaController.refrescarTableView();
            }

            cerrarVentana();
        } catch (NumberFormatException e) {
            mostrarAlerta("El descuento debe ser un número.");
        }
    }

    private void limpiarFormulario() {
        tablaProductos.getSelectionModel().clearSelection();
        campoBusqueda.clear();
        campoDescuento.clear();
        campoDescripcion.clear();
        pickerFechaInicio.setValue(null);
        pickerFechaFin.setValue(null);
    }


    private void insertarOferta(int idProducto, String descripcion, double descuento, LocalDate inicio, LocalDate fin) {
        String sql = "INSERT INTO oferta (id_producto, descripcion, descuento, fecha_inicio, fecha_fin, estado) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = App.getConnection();
             PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setInt(1, idProducto);
            stmt.setString(2, descripcion);
            stmt.setDouble(3, descuento);
            stmt.setDate(4, Date.valueOf(inicio));
            stmt.setDate(5, Date.valueOf(fin));
            stmt.setString(6, "Inactivo");

            stmt.executeUpdate();
            App.mostrarAlerta( "OK", "Oferta añadido correctamente.");

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error al guardar la oferta.");
        }
    }

    @FXML
    private void onCancelar() {
        cerrarVentana();
    }
    private void cerrarVentana() {
        Stage stage = (Stage) campoBusqueda.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle("Advertencia");
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
