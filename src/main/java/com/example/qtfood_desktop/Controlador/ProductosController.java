package com.example.qtfood_desktop.Controlador;

import com.example.qtfood_desktop.Modelo.Categoria;
import com.example.qtfood_desktop.Modelo.Pedido;
import com.example.qtfood_desktop.Modelo.Producto;
import com.example.qtfood_desktop.Vista.App;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.example.qtfood_desktop.Vista.App.loadFXML;
import static com.example.qtfood_desktop.Vista.App.mostrarAlerta;

public class ProductosController {
    private static ObservableList<Producto> productos= FXCollections.observableArrayList();
    private static ObservableList<Categoria> categorias= FXCollections.observableArrayList();
    @FXML
    private TableView<Categoria> categoriasTableView;
    @FXML
    private TableView<Producto> productosTableView;
    @FXML
    private TextField searchCategoriaField, searchProductoField;

    private FilteredList<Categoria> categoriasFiltradas;
    private FilteredList<Producto> productosFiltradas;

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

            productosTableView.setItems(productos);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void eliminarProducto() {
        Producto productoSeleccionado = productosTableView.getSelectionModel().getSelectedItem();

        // Verificar que haya un producto seleccionado
        if (productoSeleccionado == null) {
            mostrarAlerta("Error", "Selecciona un producto para eliminar.");
            return;
        }

        if (App.showConfirmationDialog("Confirmación", "¿Estás seguro de que deseas eliminar este producto? Este cambio no puede deshacerse.")) {
            try {
                int idProducto = productoSeleccionado.getIdProducto();

                // SQL para eliminar el producto de la base de datos
                String sql = "DELETE FROM productos WHERE id_producto = ?";

                try (Connection conexion = App.getConnection();
                     PreparedStatement stmt = conexion.prepareStatement(sql)) {

                    stmt.setInt(1, idProducto);
                    int filasEliminadas = stmt.executeUpdate();

                    if (filasEliminadas > 0) {
                        // Eliminar de la lista observable base
                        productos.remove(productoSeleccionado);

                        // Eliminar del TableView si aún está allí (precaución adicional)
                        productosTableView.getItems().remove(productoSeleccionado);

                        mostrarAlerta("Producto Eliminado", "El producto ha sido eliminado correctamente.");
                    } else {
                        mostrarAlerta("Error", "No se pudo eliminar el producto.");
                    }
                }

            } catch (SQLException e) {
                mostrarAlerta("Error en la base de datos", "No se pudo eliminar el producto.");
                e.printStackTrace();
            }
        }
    }



    private void cargarCategoria(){
        String sql = "SELECT * FROM categoria";
        try (Connection conexion = App.getConnection();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String nombre = rs.getString("nombre");

                Categoria categoria = new Categoria( nombre);
                categorias.add(categoria);
            }

            // Establecer las categorías a la tabla
            categoriasTableView.setItems(categorias);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void initialize(){
        productosTableView.setEditable(true);
        crearTablaProducto();
        crearTablaCategoria();
        cargarCategoria();
        cargarProductos();
        categoriasFiltradas = new FilteredList<>(categorias, p -> true);
        categoriasTableView.setItems(categoriasFiltradas);
        searchCategoriaField.textProperty().addListener((obs, oldVal, newVal) -> {
            String filtro = newVal.toLowerCase().trim();
            categoriasFiltradas.setPredicate(cat -> cat.getNombre().toLowerCase().contains(filtro));
        });
        productosFiltradas = new FilteredList<>(productos, p -> true);
        productosTableView.setItems(productosFiltradas);
        searchProductoField.textProperty().addListener((obs, oldVal, newVal) -> {
            String filtro = newVal.toLowerCase().trim();
            productosFiltradas.setPredicate(cat -> cat.getNombre().toLowerCase().contains(filtro));
        });

        productosTableView.setOnMousePressed(event -> {
            if (event.getTarget() == productosTableView) {
                productosTableView.getSelectionModel().clearSelection();
            }
        });

        // Deseleccionar si se hace clic fuera de la tabla
        productosTableView.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnMousePressed(event -> {
                    if (!productosTableView.localToScene(productosTableView.getBoundsInLocal()).contains(event.getSceneX(), event.getSceneY())) {
                        productosTableView.getSelectionModel().clearSelection();
                    }
                });
            }
        });
    }

    private void crearTablaProducto() {
        productosTableView.getColumns().clear();

        TableColumn<Producto, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        TableColumn<Producto, String> colDescripcion = new TableColumn<>("Descripción");
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        TableColumn<Producto, Double> colPrecio = new TableColumn<>("Precio");
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));

        TableColumn<Producto, Integer> colStock = new TableColumn<>("Stock");
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));

        TableColumn<Producto, String> colImagen = new TableColumn<>("Imagen URL");
        colImagen.setCellValueFactory(new PropertyValueFactory<>("imagenUrl"));

        TableColumn<Producto, String> colCategoria = new TableColumn<>("Categoría");
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));

        TableColumn<Producto, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        colNombre.prefWidthProperty().bind(productosTableView.widthProperty().multiply(0.15));
        colDescripcion.prefWidthProperty().bind(productosTableView.widthProperty().multiply(0.165));
        colPrecio.prefWidthProperty().bind(productosTableView.widthProperty().multiply(0.10));
        colStock.prefWidthProperty().bind(productosTableView.widthProperty().multiply(0.10));
        colImagen.prefWidthProperty().bind(productosTableView.widthProperty().multiply(0.17));
        colCategoria.prefWidthProperty().bind(productosTableView.widthProperty().multiply(0.15));
        colEstado.prefWidthProperty().bind(productosTableView.widthProperty().multiply(0.15));
        colEstado.setCellFactory(ComboBoxTableCell.forTableColumn("ACTIVO", "INACTIVO"));
        colEstado.setOnEditCommit(event -> {
            Producto producto = event.getRowValue();
            String nuevoEstado = event.getNewValue();
            producto.setEstado(nuevoEstado); // Actualiza el objeto en memoria
            actualizarEstadoEnBD(producto.getIdProducto(), nuevoEstado); // Llama a un método que actualiza la BD
        });

        productosTableView.getColumns().addAll(colNombre, colDescripcion, colPrecio, colStock, colImagen, colCategoria,colEstado);

        // Asignar los datos si ya se cargaron
        productosTableView.setItems(productos);
    }

    private void actualizarEstadoEnBD(int idProducto, String nuevoEstado) {
        String sql = "UPDATE productos SET estado = ? WHERE id_producto = ?";
        try (Connection conexion = App.getConnection();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, nuevoEstado);
            stmt.setInt(2, idProducto);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void crearTablaCategoria() {
        categoriasTableView.getColumns().clear();

        TableColumn<Categoria, String> colCategoriaNombre = new TableColumn<>("Nombre");
        colCategoriaNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        colCategoriaNombre.prefWidthProperty().bind(categoriasTableView.widthProperty().subtract(17));
        colCategoriaNombre.setResizable(false);

        categoriasTableView.getColumns().add(colCategoriaNombre);

    }

    @FXML
    private void añadirCategoria() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Añadir Categoría");
        dialog.setHeaderText(null);
        dialog.setContentText("Introduce el nombre de la nueva categoría:");

        Optional<String> result = dialog.showAndWait();

        result.ifPresent(nombre -> {
            nombre = nombre.trim();

            // Validación: campo vacío
            if (nombre.isEmpty()) {
                mostrarAlerta("Error", "El nombre no puede estar vacío.");
                return;
            }

            String finalNombre = nombre;
            boolean existe = categorias.stream()
                    .anyMatch(cat -> cat.getNombre().equalsIgnoreCase(finalNombre));

            if (existe) {
                mostrarAlerta("Categoría existente", "Ya existe una categoría con ese nombre.");
                return;
            }

            String sql = "INSERT INTO categoria(nombre) VALUES(?)";
            try (Connection conexion = App.getConnection();
                 PreparedStatement stmt = conexion.prepareStatement(sql)) {

                stmt.setString(1, nombre);
                stmt.executeUpdate();

                // Añadir localmente a la lista y refrescar tabla
                Categoria nueva = new Categoria(nombre);
                categorias.add(nueva);
                categoriasTableView.refresh();

            } catch (SQLException e) {
                e.printStackTrace();
                mostrarAlerta("Error de base de datos", "No se pudo añadir la categoría.");
            }
        });


    }

    @FXML
    private void EliminarCategoria() {
       Categoria seleccionado = (Categoria) categoriasTableView.getSelectionModel().getSelectedItem();
        if (seleccionado == null) { mostrarAlerta("Seleccionar categoría", "No has elegido ninguna categoria que eliminar" ); return;}


        if (App.showConfirmationDialog("Confirmacion", "Eliminar esta categoría? ")){
            String sql = "DELETE FROM categoria WHERE nombre = ?;";
            try (Connection conexion = App.getConnection();
                 PreparedStatement stmt = conexion.prepareStatement(sql)) {

                stmt.setString(1, seleccionado.getNombre());
                stmt.executeUpdate();

                categorias.remove(seleccionado);
                mostrarAlerta("OK", "Eliminado correctamente");
                categoriasTableView.refresh();

            } catch (SQLException e) {
                e.printStackTrace();
                mostrarAlerta("Error", "No se pudo eliminar la categoría.");
            }
        }

    }

    @FXML
    public void aniadirProducto(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/qtfood_desktop/button/aniadirProducto.fxml"));
        Parent root = loader.load();

        AñadirProductoController controller = loader.getController();
        controller.setProductosController(this);

        Stage secondStage = new Stage();
        secondStage.initModality(Modality.APPLICATION_MODAL);
        secondStage.setScene(new Scene(root));
        secondStage.showAndWait();
    }

    public void refrescarTableView() {
        productos.clear();
        productos.addAll(obtenerProductosDesdeBaseDeDatos());

        // Esto ya actualizará la tabla automáticamente, ya que estamos usando el mismo ObservableList.
        productosTableView.refresh();
    }

    private List<Producto> obtenerProductosDesdeBaseDeDatos() {
        List<Producto> productosRecargados = new ArrayList<>();
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
                productosRecargados.add(producto);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return productosRecargados;
    }
    @FXML
    private void modificarProducto() throws IOException {

        Producto seleccionado= (Producto) productosTableView.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {App.mostrarAlerta("Seleccionar pedido", "No has elegido ningun pedido que mostrar" );return;}
        App.setProductoSeleccionado(seleccionado);
        Stage secondStage = new Stage();
        secondStage.initModality(Modality.APPLICATION_MODAL);
        secondStage.setScene(new Scene(loadFXML("button/modificarProducto")));
        secondStage.setOnCloseRequest(event -> {
            refrescarTableView();
        });
        secondStage.showAndWait();
    }


}
