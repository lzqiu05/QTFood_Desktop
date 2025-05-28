package com.example.qtfood_desktop.Controlador;

import com.example.qtfood_desktop.Modelo.Oferta;
import com.example.qtfood_desktop.Modelo.Pedido;
import com.example.qtfood_desktop.Modelo.Producto;
import com.example.qtfood_desktop.Vista.App;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.example.qtfood_desktop.Vista.App.loadFXML;
import static com.example.qtfood_desktop.Vista.App.mostrarAlerta;

public class OfertaController {

    private static ObservableList<Oferta> ofertas = FXCollections.observableArrayList();

    @FXML
    private TableView<Oferta> tableView;
    @FXML
    private TextField nombreProductoField;
    @FXML
    private ComboBox<String> estadoComboBox;

    @FXML
    private void initialize() {
        estadoComboBox.getItems().setAll("Inactivo","Activo");
        estadoComboBox.getItems().add(0,"Todos");

        crearTablaOferta();
        cargarOfertasDesdeBD(); // Asumiendo que necesitas cargar los datos
        nombreProductoField.textProperty().addListener((obs, oldValue, newValue) -> buscarOfertas());

        estadoComboBox.valueProperty().addListener((obs, oldValue, newValue) -> buscarOfertas());

        // Deseleccionar si se hace clic en una parte vacía de la tabla
        tableView.setOnMousePressed(event -> {
            if (event.getTarget() == tableView) {
                tableView.getSelectionModel().clearSelection();
            }
        });

        // Deseleccionar si se hace clic fuera de la tabla
        tableView.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setOnMousePressed(event -> {
                    if (!tableView.localToScene(tableView.getBoundsInLocal()).contains(event.getSceneX(), event.getSceneY())) {
                        tableView.getSelectionModel().clearSelection();
                    }
                });
            }
        });

    }

    private void crearTablaOferta() {
        tableView.getColumns().clear();

        TableColumn<Oferta, String> colProducto = new TableColumn<>("Producto");
        colProducto.setCellValueFactory(new PropertyValueFactory<>("productoOferta"));

        TableColumn<Oferta, String> colDescripcion = new TableColumn<>("Descripción");
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        TableColumn<Oferta, Double> colDescuento = new TableColumn<>("Descuento (%)");
        colDescuento.setCellValueFactory(new PropertyValueFactory<>("descuento"));

        TableColumn<Oferta, LocalDate> colFechaInicio = new TableColumn<>("Fecha Inicio");
        colFechaInicio.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        colFechaInicio.setCellFactory(column -> new DatePickerTableCell<>());
        colFechaInicio.setOnEditCommit(event -> {
            Oferta oferta = event.getRowValue();
            LocalDate nuevaFecha = event.getNewValue();
            oferta.setFechaInicio(nuevaFecha);
            actualizarFechaInicioEnBD(oferta.getId_Oferta(), nuevaFecha);
            tableView.refresh();
        });

        TableColumn<Oferta, LocalDate> colFechaFin = new TableColumn<>("Fecha Fin");
        colFechaFin.setCellValueFactory(new PropertyValueFactory<>("fechaFin"));
        colFechaFin.setCellFactory(column -> new DatePickerTableCell<>());
        colFechaFin.setOnEditCommit(event -> {
            Oferta oferta = event.getRowValue();
            LocalDate nuevaFecha = event.getNewValue();
            oferta.setFechaFin(nuevaFecha);
            actualizarFechaFinEnBD(oferta.getId_Oferta(), nuevaFecha);
            tableView.refresh();
        });

        TableColumn<Oferta, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colEstado.setCellFactory(ComboBoxTableCell.forTableColumn("Activo", "Inactivo"));
        colEstado.setOnEditCommit(event -> {
            Oferta oferta = event.getRowValue();
            String nuevoEstado = event.getNewValue();
            oferta.setEstado(nuevoEstado);
            actualizarEstadoOfertaEnBD(oferta.getId_Oferta(), nuevoEstado);
            tableView.refresh();
        });

        colProducto.prefWidthProperty().bind(tableView.widthProperty().multiply(0.185));
        colDescripcion.prefWidthProperty().bind(tableView.widthProperty().multiply(0.30));
        colDescuento.prefWidthProperty().bind(tableView.widthProperty().multiply(0.10));
        colFechaInicio.prefWidthProperty().bind(tableView.widthProperty().multiply(0.13));
        colFechaFin.prefWidthProperty().bind(tableView.widthProperty().multiply(0.12));
        colEstado.prefWidthProperty().bind(tableView.widthProperty().multiply(0.15));

        tableView.getColumns().addAll(colProducto, colDescripcion, colDescuento, colFechaInicio, colFechaFin, colEstado);
        tableView.setItems(ofertas);
        tableView.setEditable(true); // Habilita la edición


    }

    private void actualizarFechaInicioEnBD(int idOferta, LocalDate nuevaFecha) {
        String sql = "UPDATE oferta SET fecha_inicio = ? WHERE id_oferta = ?";
        try (Connection conexion = App.getConnection();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setDate(1, java.sql.Date.valueOf(nuevaFecha));
            stmt.setInt(2, idOferta);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void actualizarFechaFinEnBD(int idOferta, LocalDate nuevaFecha) {
        String sql = "UPDATE oferta SET fecha_fin = ? WHERE id_oferta = ?";
        try (Connection conexion = App.getConnection();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setDate(1, java.sql.Date.valueOf(nuevaFecha));
            stmt.setInt(2, idOferta);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void actualizarEstadoOfertaEnBD(int idOferta, String nuevoEstado) {
        String sql = "UPDATE oferta SET estado = ? WHERE id_oferta = ?";
        try (Connection conexion = App.getConnection();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, nuevoEstado);
            stmt.setInt(2, idOferta);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void cargarOfertasDesdeBD() {
        ofertas.clear();  // limpiar la lista antes de recargar
        String sql = """
        SELECT o.id_oferta, p.nombre AS producto, o.descripcion, o.descuento, o.fecha_inicio, o.fecha_fin, o.estado
        FROM oferta o
        JOIN productos p ON o.id_producto = p.id_producto
    """;
        try (Connection conexion = App.getConnection();
             PreparedStatement stmt = conexion.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Oferta oferta = new Oferta(
                        rs.getInt("id_oferta"),
                        rs.getString("producto"),
                        rs.getString("descripcion"),
                        rs.getDouble("descuento"),
                        rs.getDate("fecha_inicio").toLocalDate(),
                        rs.getDate("fecha_fin").toLocalDate(),
                        rs.getString("estado")
                );
                ofertas.add(oferta);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void buscarOfertas() {
        String textoBusqueda = nombreProductoField.getText();
        String estadoSeleccionado = estadoComboBox.getValue();

        // Normalizamos los valores
        String nombreBusqueda = textoBusqueda != null ? textoBusqueda.toLowerCase().trim() : "";
        boolean buscarPorTexto = !nombreBusqueda.isEmpty();
        boolean filtrarPorEstado = estadoSeleccionado != null && !estadoSeleccionado.equals("Todos");

        ObservableList<Oferta> filtradas = FXCollections.observableArrayList(ofertas.stream()
                .filter(oferta -> {
                    boolean coincideTexto = true;
                    boolean coincideEstado = true;

                    // Filtro por texto (nombre o descripción)
                    if (buscarPorTexto) {
                        coincideTexto = oferta.getProductoOferta().toLowerCase().contains(nombreBusqueda)
                                || oferta.getDescripcion().toLowerCase().contains(nombreBusqueda);
                    }

                    // Filtro por estado
                    if (filtrarPorEstado) {
                        coincideEstado = oferta.getEstado().equalsIgnoreCase(estadoSeleccionado);
                    }

                    return coincideTexto && coincideEstado;
                })
                .toList());

        tableView.setItems(filtradas);
    }
    @FXML
    private void eliminarOferta() {
        Oferta ofertaSeleccionado = tableView.getSelectionModel().getSelectedItem();

        if (ofertaSeleccionado == null) {
            mostrarAlerta("Error", "Selecciona una oferta para eliminar.");
            return;
        }

        if (App.showConfirmationDialog("Confirmación","¿Estás seguro de que deseas eliminar esta oferta? Este cambio no puede deshacerse." )) {
            try {
                int idOferta = ofertaSeleccionado.getId_Oferta();

                String sql = "DELETE FROM oferta WHERE id_oferta = ?";

                try (Connection conexion = App.getConnection();
                     PreparedStatement stmt = conexion.prepareStatement(sql)) {

                    stmt.setInt(1, idOferta);
                    int filasEliminadas = stmt.executeUpdate();

                    if (filasEliminadas > 0) {
                        tableView.getItems().remove(ofertaSeleccionado); // Solo la tabla
                        ofertas.remove(ofertaSeleccionado); // También quitar de la lista base
                        mostrarAlerta("Oferta Eliminada", "La oferta ha sido eliminada correctamente.");
                        buscarOfertas(); // refrescar vista filtrada si es necesario
                    } else {
                        mostrarAlerta("Error", "No se pudo eliminar la oferta.");
                    }
                }

            } catch (SQLException e) {
                mostrarAlerta("Error en la base de datos", "No se pudo eliminar la oferta.");
                e.printStackTrace();
            }
        }
    }
    @FXML
    public void aniadirOferta(ActionEvent actionEvent) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/qtfood_desktop/button/aniadirOferta.fxml"));
        Parent root = loader.load();

        AgregarOfertaController controller = loader.getController();
        controller.setOfertaController(this);

        Stage secondStage = new Stage();
        secondStage.initModality(Modality.APPLICATION_MODAL);
        secondStage.setScene(new Scene(root));
        secondStage.showAndWait();
    }


    public void refrescarTableView() {
        ofertas.clear();
        ofertas.addAll(obtenerOfertasDesdeBaseDeDatos());
        tableView.refresh();
    }


    private List<Oferta> obtenerOfertasDesdeBaseDeDatos() {
        List<Oferta> ofertasRecargadas = new ArrayList<>();
        String sql = """
        SELECT o.id_oferta, p.nombre AS producto, o.descripcion, o.descuento, o.fecha_inicio, o.fecha_fin, o.estado
        FROM oferta o
        JOIN productos p ON o.id_producto = p.id_producto
    """;
        try (Connection conexion = App.getConnection();
             PreparedStatement stmt = conexion.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Oferta oferta = new Oferta(
                        rs.getInt("id_oferta"),
                        rs.getString("producto"),
                        rs.getString("descripcion"),
                        rs.getDouble("descuento"),
                        rs.getDate("fecha_inicio").toLocalDate(),
                        rs.getDate("fecha_fin").toLocalDate(),
                        rs.getString("estado")
                );
                ofertasRecargadas.add(oferta);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ofertasRecargadas;
    }

}
