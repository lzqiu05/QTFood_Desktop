package com.example.qtfood_desktop.Controlador;

import com.example.qtfood_desktop.Modelo.Pedido;
import com.example.qtfood_desktop.Vista.App;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.example.qtfood_desktop.Vista.App.loadFXML;
/**
 * Controlador de la vista de usuarios y pedidos.
 * Permite buscar pedidos por correo, fecha y estado, y mostrar detalles de un pedido seleccionado.
 */
public class UsuariosController {
    private static ObservableList<Pedido> datos = FXCollections.observableArrayList();

    @FXML
    private TableView<Pedido> tableView;
    @FXML
    private TableColumn<Pedido, Integer> colIDPedido;
    @FXML
    private TableColumn<Pedido, String> colNombreUsuario;
    @FXML
    private TableColumn<Pedido, LocalDateTime> colFecha;
    @FXML
    private TableColumn<Pedido, Double> colTotal;
    @FXML
    private TableColumn<Pedido, String> colMetodoPago;
    @FXML
    private TableColumn<Pedido, String> colDireccion;
    @FXML
    private TableColumn<Pedido, String> colEstado;

    @FXML
    private TextField CorreoField;

    @FXML
    private DatePicker FechaField;

    @FXML
    private ComboBox<String> estadoComboBox;


    @FXML
    private void initialize() {
        // Inicializar la tabla vacía
        tableView.setItems(datos);

        // Inicializar ComboBox con los estados posibles
        estadoComboBox.getItems().addAll("pendiente", "enviado", "entregado", "cancelado");
        estadoComboBox.getItems().add(0, "Todos");


        colIDPedido.setCellValueFactory(new PropertyValueFactory<>("idPedido"));
        colNombreUsuario.setCellValueFactory(new PropertyValueFactory<>("Usuario"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        colFecha.setCellFactory(column -> new TableCell<Pedido, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.format(formatter));
                }
            }
        });
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colMetodoPago.setCellValueFactory(new PropertyValueFactory<>("metodoPago"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        // Deseleccionar si se hace clic fuera de la tabla
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

    @FXML
    private void buscarUsuarios() {
        String correo = CorreoField.getText().trim();

        if (correo.isEmpty()) {
            App.mostrarAlerta("Error", "El correo es obligatorio.");
            datos.clear();
            tableView.setItems(datos); // Actualizar la vista con la tabla vacía
            return;
        }


        String sql = "SELECT p.*, u.nombre AS nombre_usuario FROM pedidos p " +
                "JOIN usuarios u ON p.id_usuario = u.id_usuario " +
                "WHERE u.correo = ? AND p.fecha IS NOT NULL";

        List<Object> parametros = new ArrayList<>();
        parametros.add(correo); // Añadir correo a los parámetros

        // Agregar filtros por fecha si es necesario
        LocalDate fechaSeleccionada = FechaField.getValue();
        if (fechaSeleccionada != null && !FechaField.getEditor().getText().isEmpty()) {
            sql += " AND DATE(p.fecha) = ?";
            parametros.add(Date.valueOf(fechaSeleccionada));
        }

        // Agregar filtro de estado si está seleccionado
        String estadoSeleccionado = estadoComboBox.getValue();
        if (estadoSeleccionado != null && !"Todos".equals(estadoSeleccionado)) {
            sql += " AND p.estado = ?";
            parametros.add(estadoSeleccionado);
        }

        // Ejecutar la consulta
        try (Connection conexion = App.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            // Configurar los parámetros en la consulta
            for (int i = 0; i < parametros.size(); i++) {
                sentencia.setObject(i + 1, parametros.get(i));
            }

            ResultSet resultSet = sentencia.executeQuery();
            datos.clear(); // Limpiar la lista de datos antes de llenarla

            while (resultSet.next()) {
                Pedido pedido = new Pedido(
                        resultSet.getInt("id_pedido"),
                        resultSet.getString("nombre_usuario"),
                        resultSet.getTimestamp("fecha").toLocalDateTime(),
                        resultSet.getDouble("total"),
                        resultSet.getString("metodo_pago"),
                        resultSet.getString("direccion"),
                        resultSet.getString("estado")
                );
                datos.add(pedido); // Añadir los pedidos encontrados
            }

            tableView.setItems(datos); // Actualizar la tabla

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void mostrar() throws IOException {
        Pedido seleccionado = tableView.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            App.mostrarAlerta("Seleccionar pedido", "No has elegido ningún pedido para mostrar.");
            return;
        }
        App.setPedidoSeleccionado(seleccionado);
        Stage secondStage = new Stage();
        secondStage.initModality(Modality.APPLICATION_MODAL);
        secondStage.setScene(new Scene(loadFXML("button/mostrarPedido")));
        secondStage.showAndWait();
    }
}
