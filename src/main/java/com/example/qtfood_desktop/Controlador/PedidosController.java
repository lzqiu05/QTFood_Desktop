package com.example.qtfood_desktop.Controlador;

import com.example.qtfood_desktop.Modelo.Categoria;
import com.example.qtfood_desktop.Modelo.Pedido;
import com.example.qtfood_desktop.Vista.App;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;


import static com.example.qtfood_desktop.Vista.App.loadFXML;

public class PedidosController {
    private static ObservableList<Pedido> datos= FXCollections.observableArrayList();
    @FXML
    private TableView tableView;
    @FXML
    private TextField PrecioField;
    @FXML
    private DatePicker FechaField;
    @FXML
    private ComboBox<String> estadoComboBox;
    private boolean filtroActivo = false;


    @FXML
    private Button btnmostrar;

    private void rellenarTabla() {
        if (filtroActivo) return;
        String sql = "SELECT p.*, u.nombre AS nombre_usuario " +
                "FROM pedidos p " +
                "JOIN usuarios u ON p.id_usuario = u.id_usuario " +
                "WHERE DATE(p.fecha) = CURRENT_DATE AND estado = 'Pendiente' " +
                "ORDER BY p.fecha DESC";

        try (Connection conexion= App.getConnection()){

            PreparedStatement sentencia=conexion.prepareStatement(sql);

            ResultSet resultSet=sentencia.executeQuery();

            datos.clear();
            while (resultSet.next()) {
                int idPedido = resultSet.getInt("id_pedido");
                String nombreUsuario = resultSet.getString("nombre_usuario");
                Timestamp fecha = resultSet.getTimestamp("fecha");
                double total = resultSet.getDouble("total");
                String metodoPago = resultSet.getString("metodo_pago");
                String direccion = resultSet.getString("direccion");
                String estado = resultSet.getString("estado");

                Pedido pedido = new Pedido(
                        idPedido,
                        nombreUsuario,
                        fecha.toLocalDateTime(),
                        total,
                        metodoPago,
                        direccion,
                        estado
                );

                datos.add(pedido);
            }

            tableView.setItems(datos);


        } catch (SQLException e) {
            e.printStackTrace();

        }
    }

    @FXML
    private void initialize() {
        // Rellenar ComboBox con los estados del ENUM
        estadoComboBox.getItems().addAll( "enviado", "entregado", "cancelado");

        crearTabla();
        tableView.setEditable(true);
        btnmostrar.setVisible(false);

        // Mostrar u ocultar el botón según selección
        tableView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            btnmostrar.setVisible(newValue != null);
        });

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

        rellenarTabla();

        Timeline refrescoAutomatico = new Timeline(
                new KeyFrame(Duration.seconds(5), event -> {
                    rellenarTabla();
                })
        );
        refrescoAutomatico.setCycleCount(Animation.INDEFINITE);
        refrescoAutomatico.play();

    }


    private void crearTabla() {
        tableView.getColumns().clear();

        TableColumn<Pedido, Integer> colIDPedido = new TableColumn<>("ID Pedido");
        colIDPedido.setCellValueFactory(new PropertyValueFactory<>("idPedido"));

        TableColumn<Pedido, String> colIDUsuario = new TableColumn<>("Usuario");
        colIDUsuario.setCellValueFactory(new PropertyValueFactory<>("Usuario"));

        TableColumn<Pedido, LocalDateTime> colFecha = new TableColumn<>("Fecha");
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
        TableColumn<Pedido, Double> colTotal = new TableColumn<>("Total");
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        TableColumn<Pedido, String> colMetodoPago = new TableColumn<>("Método de Pago");
        colMetodoPago.setCellValueFactory(new PropertyValueFactory<>("metodoPago"));

        TableColumn<Pedido, String> colDireccion = new TableColumn<>("Dirección");
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));

        TableColumn<Pedido, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colEstado.setCellFactory(ComboBoxTableCell.forTableColumn("pendiente", "enviado", "entregado", "cancelado"));
        colEstado.setOnEditCommit(event -> {
            Pedido pedido = event.getRowValue();
            String nuevoEstado = event.getNewValue();
            pedido.setEstado(nuevoEstado); // Actualiza el objeto en memoria
            actualizarEstadoEnBD(pedido.getIdPedido(), nuevoEstado); // Llama a un método que actualiza la BD
        });

        colIDPedido.prefWidthProperty().bind(tableView.widthProperty().multiply(0.10));
        colIDUsuario.prefWidthProperty().bind(tableView.widthProperty().multiply(0.10));
        colFecha.prefWidthProperty().bind(tableView.widthProperty().multiply(0.165));
        colTotal.prefWidthProperty().bind(tableView.widthProperty().multiply(0.10));
        colMetodoPago.prefWidthProperty().bind(tableView.widthProperty().multiply(0.15));
        colDireccion.prefWidthProperty().bind(tableView.widthProperty().multiply(0.22));
        colEstado.prefWidthProperty().bind(tableView.widthProperty().multiply(0.15));
        tableView.getColumns().addAll(
                colIDPedido,
                colIDUsuario,
                colFecha,
                colTotal,
                colMetodoPago,
                colDireccion,
                colEstado
        );
    }

    private void actualizarEstadoEnBD(int idPedido, String nuevoEstado) {

        String sql = "UPDATE pedidos SET estado = ? WHERE id_pedido = ?";
        try (Connection conexion = App.getConnection();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, nuevoEstado);
            stmt.setInt(2, idPedido);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void mostrar() throws IOException {

        Pedido seleccionado= (Pedido) tableView.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {App.mostrarAlerta("Seleccionar pedido", "No has elegido ningun pedido que mostrar" );return;}
        App.setPedidoSeleccionado(seleccionado);
        Stage secondStage = new Stage();
        secondStage.initModality(Modality.APPLICATION_MODAL);
        secondStage.setScene(new Scene(loadFXML("button/mostrarPedido")));
        secondStage.showAndWait();

    }

    @FXML
    private void buscarPedidos() {
        filtroActivo = true;
        String sql = "SELECT p.*, u.nombre AS nombre_usuario FROM pedidos p JOIN usuarios u ON p.id_usuario = u.id_usuario WHERE 1=1";
        List<Object> parametros = new ArrayList<>();

        LocalDate fechaSeleccionada = FechaField.getValue();
        if (fechaSeleccionada != null && !FechaField.getEditor().getText().isEmpty()) {
            sql += " AND DATE(fecha) = ?";
            parametros.add(Date.valueOf(fechaSeleccionada));
        }


        if (!PrecioField.getText().isEmpty()) {
            try {
                double precio = Double.parseDouble(PrecioField.getText());
                sql += " AND total LIKE ?";
                parametros.add(precio + "%");
            } catch (NumberFormatException e) {
                System.out.println("Precio inválido");
                return;
            }
        }


        String estadoSeleccionado = estadoComboBox.getValue();
        if (estadoSeleccionado != null ) {
            sql += " AND estado = ?";
            parametros.add(estadoSeleccionado);
        }

        try (Connection conexion = App.getConnection();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            for (int i = 0; i < parametros.size(); i++) {
                sentencia.setObject(i + 1, parametros.get(i));
            }

            ResultSet resultSet = sentencia.executeQuery();
            datos.clear();

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
                datos.add(pedido);
            }

            tableView.setItems(datos);
            FechaField.setValue(null);
            FechaField.getEditor().clear();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void refrescarTabla(ActionEvent actionEvent) {
        filtroActivo = false;
        FechaField.setValue(null);
        FechaField.getEditor().clear();
        PrecioField.clear();
        estadoComboBox.setValue(null); // o null si prefieres
        rellenarTabla();

    }
    public void exportarPedidosFinalizadasPDF(ActionEvent actionEvent) {
        LocalDate hoy = LocalDate.now();
        LocalDate haceUnMes = hoy.minusMonths(1);
        LocalDateTime fechaGeneracion = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        String sql = "SELECT p.*, u.nombre AS nombre_usuario " +
                "FROM pedidos p " +
                "JOIN usuarios u ON p.id_usuario = u.id_usuario " +
                "WHERE p.estado = 'entregado' AND p.fecha BETWEEN ? AND ?";

        List<Pedido> pedidosEntregados = new ArrayList<>();

        try (Connection conexion = App.getConnection();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {

            stmt.setTimestamp(1, Timestamp.valueOf(haceUnMes.atStartOfDay()));
            stmt.setTimestamp(2, Timestamp.valueOf(hoy.atTime(23, 59)));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Pedido pedido = new Pedido(
                        rs.getInt("id_pedido"),
                        rs.getString("nombre_usuario"),
                        rs.getTimestamp("fecha").toLocalDateTime(),
                        rs.getDouble("total"),
                        rs.getString("metodo_pago"),
                        rs.getString("direccion"),
                        rs.getString("estado")
                );
                pedidosEntregados.add(pedido);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        if (pedidosEntregados.isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION, "No hay pedidos entregados en el último mes.").show();
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar PDF de pedidos entregados");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo PDF", "*.pdf"));
        fileChooser.setInitialFileName("pedidos_entregados.pdf ("+haceUnMes+" - "+hoy+")");

        // Directorio por defecto (puedes cambiar esta ruta)
        fileChooser.setInitialDirectory(new File("C:\\Users\\Liang Zhi\\Desktop\\s"));

        File file = fileChooser.showSaveDialog(((Node) actionEvent.getSource()).getScene().getWindow());
        if (file == null) return;

        try (FileOutputStream fos = new FileOutputStream(file)) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, fos);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD);
            Font bodyFont = new Font(Font.HELVETICA, 12);

            document.add(new Paragraph("Reporte de pedidos entregados (último mes):", titleFont));
            document.add(Chunk.NEWLINE);

            // Calcular el total de dinero
            double totalDinero = pedidosEntregados.stream()
                    .mapToDouble(Pedido::getTotal)
                    .sum();

            Paragraph resumen = new Paragraph();
            resumen.setFont(bodyFont);
            resumen.add(String.format("Desde: %s\n", haceUnMes));
            resumen.add(String.format("Hasta: %s\n", hoy));
            resumen.add(String.format("Generado el: %s\n", fechaGeneracion.format(formatter)));
            resumen.add(String.format("Total de pedidos entregados: %d\n", pedidosEntregados.size()));
            resumen.add(String.format("Total de dinero: %.2f\n", totalDinero));
            document.add(resumen);
            document.add(Chunk.NEWLINE);


            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{2.5f, 3, 3, 2, 3, 2.5f});

            Stream.of("ID Pedido", "Usuario", "Fecha", "Total", "Método de Pago", "Estado").forEach(col -> {
                PdfPCell cell = new PdfPCell(new Phrase(col, bodyFont));
                cell.setBackgroundColor(Color.LIGHT_GRAY);
                table.addCell(cell);
            });

            for (Pedido p : pedidosEntregados) {
                table.addCell(String.valueOf(p.getIdPedido()));
                table.addCell(p.getUsuario());
                table.addCell(p.getFecha().toString());
                table.addCell(String.format("%.2f", p.getTotal()));
                table.addCell(p.getMetodoPago());
                table.addCell(p.getEstado());
            }

            document.add(table);
            document.close();

            new Alert(Alert.AlertType.INFORMATION, "PDF exportado correctamente.").show();

        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error al generar el PDF.").show();
        }
    }
}
