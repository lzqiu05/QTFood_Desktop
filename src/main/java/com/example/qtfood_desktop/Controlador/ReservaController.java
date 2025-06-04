package com.example.qtfood_desktop.Controlador;


import com.example.qtfood_desktop.Modelo.Reserva;
import com.example.qtfood_desktop.Vista.App;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ReservaController implements RefreshableController {

    @FXML
    private TableView<Reserva> reservasConfirmadasTableView;

    @FXML
    private TableView<Reserva> reservasPendientesTableView;

    @FXML
    private TextField searchNombreField;

    @FXML
    private DatePicker searchFechaPicker;

    private final ObservableList<Reserva> reservasConfirmadasHoy = FXCollections.observableArrayList();
    private final ObservableList<Reserva> reservasPendientes = FXCollections.observableArrayList();
    private final ObservableList<Reserva> reservasConfirmadas = FXCollections.observableArrayList();


    private final FilteredList<Reserva> reservasConfirmadasFiltradas = new FilteredList<>(reservasConfirmadas, p -> true);
    public Timeline refrescoAutomatico;

    @Override
    public void activar() {
        boolean hayFiltros = !searchNombreField.getText().trim().isEmpty() ||
                searchFechaPicker.getValue() != null;

        if (!hayFiltros && refrescoAutomatico != null) {
            refrescoAutomatico.play();
            System.out.println("Timeline de reservas ACTIVADO");
        }
    }

    @Override
    public void desactivar() {
        if (refrescoAutomatico != null) {
            refrescoAutomatico.stop();
            System.out.println("Timeline de reservas DESACTIVADO");
        }
    }
    @FXML
    private void initialize() {
        crearTablaReservasConfirmadas();
        crearTablaReservasPendientes();
        cargarReservasPendientes();
        cargarReservasConfirmadasHoy();


        reservasPendientesTableView.setEditable(true);
        reservasConfirmadasTableView.setEditable(true);
        Platform.runLater(() -> {
            Scene scene = reservasConfirmadasTableView.getScene();
            if (scene != null) {
                scene.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                    if (!reservasConfirmadasTableView.localToScene(reservasConfirmadasTableView.getBoundsInLocal()).contains(event.getSceneX(), event.getSceneY())) {
                        reservasConfirmadasTableView.getSelectionModel().clearSelection();
                    }
                });
            }
        });

        Platform.runLater(() -> {
            Scene scene = reservasPendientesTableView.getScene();
            if (scene != null) {
                scene.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                    if (!reservasPendientesTableView.localToScene(reservasPendientesTableView.getBoundsInLocal()).contains(event.getSceneX(), event.getSceneY())) {
                        reservasPendientesTableView.getSelectionModel().clearSelection();
                    }
                });
            }
        });

        reservasConfirmadasTableView.setItems(reservasConfirmadasHoy); // por defecto, muestra HOY
        searchNombreField.textProperty().addListener((obs, oldVal, newVal) -> aplicarFiltros());
        searchFechaPicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            aplicarFiltros();
        });
        searchFechaPicker.getEditor().textProperty().addListener((obs, oldText, newText) -> {
            if (newText.trim().isEmpty()) {
                searchFechaPicker.setValue(null); // Asegura que se borre el valor
                aplicarFiltros();
            }
        });
        boolean filtroActivo = !searchNombreField.getText().trim().isEmpty() || searchFechaPicker.getValue() != null;
        if (!filtroActivo) {
            refrescoAutomatico = new Timeline(
                    new KeyFrame(Duration.seconds(10), event -> {
                        cargarReservasConfirmadasHoy();
                        cargarReservasPendientes();
                        // aplicarFiltros();
                    })
            );
            System.out.println("reserva timeline activo");

            refrescoAutomatico.setCycleCount(Animation.INDEFINITE);
        }

    }


    private void crearTablaReservasConfirmadas() {
        reservasConfirmadasTableView.getColumns().clear();


        TableColumn<Reserva, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));


        TableColumn<Reserva, String> colTelefono = new TableColumn<>("Teléfono");
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));


        TableColumn<Reserva, String> colFecha = new TableColumn<>("Fecha");
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaSolo"));


        TableColumn<Reserva, String> colHora = new TableColumn<>("Hora");
        colHora.setCellValueFactory(new PropertyValueFactory<>("horaSolo"));


        TableColumn<Reserva, Integer> colNPersonas = new TableColumn<>("Nº Personas");
        colNPersonas.setCellValueFactory(new PropertyValueFactory<>("nPersonas"));
        TableColumn<Reserva, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colEstado.setCellFactory(ComboBoxTableCell.forTableColumn("Confirmado", "Finalizado", "Cancelado"));
        colEstado.setOnEditCommit(event -> {
            Reserva reserva = event.getRowValue();
            String nuevoEstado = event.getNewValue();
            reserva.setEstado(nuevoEstado); // Actualiza el objeto en memoria
            actualizarEstadoEnBD(reserva.getIdReserva(), nuevoEstado);


            // Si ya no está "Confirmado", quítalo de esta lista
            if (!nuevoEstado.equalsIgnoreCase("Confirmado")) {
                reservasConfirmadasHoy.remove(reserva);
                reservasConfirmadas.remove(reserva);


            }
        });

        colNombre.prefWidthProperty().bind(reservasPendientesTableView.widthProperty().multiply(0.15));
        colTelefono.prefWidthProperty().bind(reservasPendientesTableView.widthProperty().multiply(0.15));
        colFecha.prefWidthProperty().bind(reservasPendientesTableView.widthProperty().multiply(0.190));
        colHora.prefWidthProperty().bind(reservasPendientesTableView.widthProperty().multiply(0.195));
        colNPersonas.prefWidthProperty().bind(reservasPendientesTableView.widthProperty().multiply(0.15));
        colEstado.prefWidthProperty().bind(reservasPendientesTableView.widthProperty().multiply(0.15));

        reservasConfirmadasTableView.getColumns().addAll(colNombre, colTelefono, colFecha, colHora, colNPersonas,colEstado);
        reservasConfirmadasTableView.setItems(reservasConfirmadasHoy);

    }


    private void crearTablaReservasPendientes() {
        reservasPendientesTableView.getColumns().clear();

        TableColumn<Reserva, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        TableColumn<Reserva, String> colTelefono = new TableColumn<>("Teléfono");
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));

        TableColumn<Reserva, String> colFecha = new TableColumn<>("Fecha");
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaSolo"));

        TableColumn<Reserva, String> colHora = new TableColumn<>("Hora");
        colHora.setCellValueFactory(new PropertyValueFactory<>("horaSolo"));

        TableColumn<Reserva, Integer> colNPersonas = new TableColumn<>("Nº Personas");
        colNPersonas.setCellValueFactory(new PropertyValueFactory<>("nPersonas"));

        TableColumn<Reserva, String> colEstado = new TableColumn<>("Estado");
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colEstado.setCellFactory(ComboBoxTableCell.forTableColumn("Pendiente", "Confirmado", "Rechazado"));
        colEstado.setOnEditCommit(event -> {
            Reserva reserva = event.getRowValue();
            String nuevoEstado = event.getNewValue();
            reserva.setEstado(nuevoEstado); // Actualiza el objeto en memoria
            actualizarEstadoEnBD(reserva.getIdReserva(), nuevoEstado);

            // Si ya no está "Pendiente", quítalo de esta lista
            if (!nuevoEstado.equalsIgnoreCase("Pendiente")) {
                reservasPendientes.remove(reserva);
                if (nuevoEstado.equalsIgnoreCase("Confirmado") ) {
                    if (!reservasConfirmadas.contains(reserva)) reservasConfirmadas.add(reserva);
                    if (reserva.getFechaHora().toLocalDate().equals(LocalDate.now())) {
                        reservasConfirmadasHoy.add(reserva);
                    }
                    // Si estás en modo de filtro activo, actualiza la vista
                    aplicarFiltros();
                }
            }
        });

        colNombre.prefWidthProperty().bind(reservasPendientesTableView.widthProperty().multiply(0.15));
        colTelefono.prefWidthProperty().bind(reservasPendientesTableView.widthProperty().multiply(0.15));
        colFecha.prefWidthProperty().bind(reservasPendientesTableView.widthProperty().multiply(0.195));
        colHora.prefWidthProperty().bind(reservasPendientesTableView.widthProperty().multiply(0.20));
        colNPersonas.prefWidthProperty().bind(reservasPendientesTableView.widthProperty().multiply(0.15));
        colEstado.prefWidthProperty().bind(reservasPendientesTableView.widthProperty().multiply(0.15));

        reservasPendientesTableView.getColumns().addAll(colNombre, colTelefono, colFecha, colHora, colNPersonas, colEstado);
        reservasPendientesTableView.setItems(reservasPendientes);
    }


    private void actualizarEstadoEnBD(int idReserva, String nuevoEstado) {
        String sql = "UPDATE Reserva SET estado = ? WHERE id_reserva = ?";
        try (Connection conexion = App.getConnection();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setString(1, nuevoEstado);
            stmt.setInt(2, idReserva);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void cargarReservasPendientes() {
        reservasPendientes.clear();
        String sql = "SELECT * FROM reserva WHERE estado = 'Pendiente'";

        try (Connection conexion = App.getConnection();
             PreparedStatement stmt = conexion.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id_reserva");
                String nombre = rs.getString("nombre");
                String telefono = rs.getString("telefono");
                Timestamp ts = rs.getTimestamp("fecha");
                LocalDateTime fechaHora = ts.toLocalDateTime();
                int nPersonas = rs.getInt("n_personas");
                String estado = rs.getString("estado");


                Reserva reserva = new Reserva(id, nombre, telefono, fechaHora, nPersonas, estado);
                reservasPendientes.add(reserva);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    private void cargarReservasConfirmadasHoy() {
        reservasConfirmadasHoy.clear();
        String sql = "SELECT * FROM reserva WHERE estado = 'Confirmado' AND DATE(fecha) = ?";


        try (Connection conexion = App.getConnection();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {


            stmt.setDate(1, Date.valueOf(LocalDate.now()));


            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id_reserva");
                    String nombre = rs.getString("nombre");
                    String telefono = rs.getString("telefono");
                    Timestamp ts = rs.getTimestamp("fecha");
                    LocalDateTime fechaHora = ts.toLocalDateTime();
                    int nPersonas = rs.getInt("n_personas");
                    String estado = rs.getString("estado");


                    Reserva reserva = new Reserva(id, nombre, telefono, fechaHora, nPersonas, estado);


                    reservasConfirmadasHoy.add(reserva);


                }
            }


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    private void aplicarFiltros() {
        String texto = searchNombreField.getText().toLowerCase().trim();
        LocalDate fecha = searchFechaPicker.getValue();

        boolean hayFiltro = !texto.isEmpty() || fecha != null;
        if (hayFiltro) {
            if (reservasConfirmadas.isEmpty()) {
                cargarTodasConfirmadas(); // carga solo una vez si está vacía
            }
            reservasConfirmadasFiltradas.setPredicate(r -> {
                boolean coincideTexto = texto.isEmpty() || r.getNombre().toLowerCase().contains(texto) || r.getTelefono().toLowerCase().contains(texto);
                boolean coincideFecha = (fecha == null) || r.getFechaHora().toLocalDate().equals(fecha);
                return coincideTexto && coincideFecha;
            });
            reservasConfirmadasTableView.setItems(reservasConfirmadasFiltradas);
        } else {
            reservasConfirmadasTableView.setItems(reservasConfirmadasHoy);
        }
    }



    private void cargarTodasConfirmadas() {
        reservasConfirmadas.clear();

        String sql = "SELECT * FROM reserva WHERE estado = 'Confirmado'";

        try (Connection conexion = App.getConnection();
             PreparedStatement stmt = conexion.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("id_reserva");
                String nombre = rs.getString("nombre");
                String telefono = rs.getString("telefono");
                Timestamp ts = rs.getTimestamp("fecha");
                LocalDateTime fechaHora = ts.toLocalDateTime();
                int nPersonas = rs.getInt("n_personas");
                String estado = rs.getString("estado");


                Reserva reserva = new Reserva(id, nombre, telefono, fechaHora, nPersonas, estado);
                reservasConfirmadas.add(reserva);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void exportarReservasFinalizadasPDF(ActionEvent actionEvent) {
        LocalDate hoy = LocalDate.now();
        LocalDate haceUnMes = hoy.minusMonths(1);
        LocalDateTime fechaGeneracion = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        String sql = "SELECT * FROM reserva WHERE estado = 'Finalizado' AND fecha BETWEEN ? AND ?";
        List<Reserva> reservasFinalizadas = new ArrayList<>();

        try (Connection conexion = App.getConnection();
            PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(haceUnMes.atStartOfDay()));
            stmt.setTimestamp(2, Timestamp.valueOf(hoy.atTime(23, 59)));

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id_reserva");
                String nombre = rs.getString("nombre");
                String telefono = rs.getString("telefono");
                LocalDateTime fechaHora = rs.getTimestamp("fecha").toLocalDateTime();
                int nPersonas = rs.getInt("n_personas");
                String estado = rs.getString("estado");

                reservasFinalizadas.add(new Reserva(id, nombre, telefono, fechaHora, nPersonas, estado));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        if (reservasFinalizadas.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "No hay reservas finalizadas en el último mes.");
            alert.show();
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar PDF de reservas");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivo PDF", "*.pdf"));
        fileChooser.setInitialFileName("reservas_finalizadas.pdf ("+haceUnMes+" - "+hoy+")");

        fileChooser.setInitialDirectory(new File("C:\\Users\\Liang Zhi\\Desktop\\s"));
        File file = fileChooser.showSaveDialog(((Node) actionEvent.getSource()).getScene().getWindow());
        if (file == null) return;

        try (FileOutputStream fos = new FileOutputStream(file)) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, fos);
            document.open();

            Font titleFont = new Font(Font.HELVETICA, 16, Font.BOLD);
            Font bodyFont = new Font(Font.HELVETICA, 12);

            // Título
            document.add(new Paragraph("Reporte de reservas finalizadas (último mes): ",titleFont));
            document.add(Chunk.NEWLINE);

            // Resumen
            Paragraph resumen = new Paragraph();
            resumen.setFont(bodyFont);
            resumen.add(String.format("Desde: %s\n", haceUnMes));
            resumen.add(String.format("Hasta: %s\n", hoy));
            resumen.add(String.format("Generado el: %s\n", fechaGeneracion.format(formatter)));
            resumen.add(String.format("Total de reservas finalizadas: %d\n", reservasFinalizadas.size()));
            document.add(resumen);

            document.add(Chunk.NEWLINE);

            // Tabla
            PdfPTable table = new PdfPTable(6);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{3, 3, 3, 3, 2, 3});

            Stream.of("Nombre", "Teléfono", "Fecha", "Hora", "Personas", "Estado")
                    .forEach(col -> {
                        PdfPCell cell = new PdfPCell(new Phrase(col, bodyFont));
                        cell.setBackgroundColor(Color.LIGHT_GRAY);
                        table.addCell(cell);
                    });

            for (Reserva r : reservasFinalizadas) {
                table.addCell(r.getNombre());
                table.addCell(r.getTelefono());
                table.addCell(r.getFechaHora().toLocalDate().toString());
                table.addCell(r.getFechaHora().toLocalTime().withSecond(0).withNano(0).toString());
                table.addCell(String.valueOf(r.getNPersonas()));
                table.addCell(r.getEstado());
            }

            document.add(table);
            document.close();

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "PDF exportado correctamente.");
            alert.show();

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error al generar el PDF.");
            alert.show();
        }
    }


    public void refrescarTabla(ActionEvent actionEvent) {
        searchFechaPicker.setValue(null);
        searchFechaPicker.getEditor().clear();
        searchNombreField.setText("");
        cargarReservasConfirmadasHoy();
        cargarReservasPendientes();

    }
}
