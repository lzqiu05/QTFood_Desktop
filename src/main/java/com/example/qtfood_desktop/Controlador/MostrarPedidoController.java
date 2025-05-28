package com.example.qtfood_desktop.Controlador;

import com.example.qtfood_desktop.Vista.App;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.print.PrinterJob;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;

public class MostrarPedidoController {

    @FXML
    private Label labelIdPedido, labelFecha, labelCliente, labelTotal;
    @FXML
    private ListView<String> listaProductos;

    @FXML
    private void initialize(){
        labelIdPedido.setText("Código: "+String.valueOf(App.getPedidoSeleccionado().getIdPedido()));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        labelFecha.setText("Fecha: "+App.getPedidoSeleccionado().getFecha().format(formatter));
       // getCliente(App.getPedidoSeleccionado().getIdUsuario());
        labelCliente.setText("Cliente: "+App.getPedidoSeleccionado().getUsuario());
        cargarProductosPorPedido(App.getPedidoSeleccionado().getIdPedido());
        labelTotal.setText(String.valueOf(App.getPedidoSeleccionado().getTotal()));

    }

    private void getCliente(int idUsuario) {
        String sql = "SELECT nombre FROM usuarios WHERE id_usuario = ?";
        String nombreCliente = null;

        try (Connection conexion = App.getConnection();
             PreparedStatement stmt = conexion.prepareStatement(sql)) {

            stmt.setInt(1, idUsuario);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                nombreCliente = rs.getString("nombre");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (nombreCliente != null) {
            labelCliente.setText(nombreCliente);
        } else {
            labelCliente.setText("Cliente no encontrado");
        }
    }

    private void cargarProductosPorPedido(int idPedido) {
        ObservableList<String> productos = FXCollections.observableArrayList();

        // Consulta para obtener los productos del pedido
        String sqlDetallePedido = "SELECT id_producto, cantidad, precio_unitario FROM detalle_pedido WHERE id_pedido = ?";

        try (Connection conexion = App.getConnection();
             PreparedStatement stmt = conexion.prepareStatement(sqlDetallePedido)) {

            stmt.setInt(1, idPedido);
            ResultSet rsDetallePedido = stmt.executeQuery();

            while (rsDetallePedido.next()) {
                int idProducto = rsDetallePedido.getInt("id_producto");
                int cantidad = rsDetallePedido.getInt("cantidad");
                double precioUnitario = rsDetallePedido.getDouble("precio_unitario");

                String sqlProducto = "SELECT nombre FROM productos WHERE id_producto = ?";
                try (PreparedStatement stmtProducto = conexion.prepareStatement(sqlProducto)) {
                    stmtProducto.setInt(1, idProducto);
                    ResultSet rsProducto = stmtProducto.executeQuery();

                    if (rsProducto.next()) {
                        String nombreProducto = rsProducto.getString("nombre");

                        String linea = String.format("%s x%d - %.2f€ c/u", nombreProducto, cantidad, precioUnitario);
                        productos.add(linea);
                    }
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        listaProductos.setItems(productos);
    }
    @FXML
    private void imprimir() {
        imprimirTicket();
    }

    public void imprimirTicket() {
        // Crear un VBox para el ticket a imprimir
        VBox ticketVBox = new VBox();
        ticketVBox.setSpacing(10);

        // Añadir información del pedido al ticket
        ticketVBox.getChildren().add(new Text("QTFOOD"));
        ticketVBox.getChildren().add(new Text("********** TICKET DE PEDIDO **********"));
        ticketVBox.getChildren().add(new Text("Pedido ID: " + App.getPedidoSeleccionado().getIdPedido()));
        ticketVBox.getChildren().add(new Text("Fecha: " + App.getPedidoSeleccionado().getFecha().toString()));
        ticketVBox.getChildren().add(new Text("Cliente: " + labelCliente.getText()));

        ticketVBox.getChildren().add(new Text("Productos:"));
        for (String producto : listaProductos.getItems()) {
            ticketVBox.getChildren().add(new Text(producto));
        }

        ticketVBox.getChildren().add(new Text("Total: " + labelTotal.getText()));

        // Crear un trabajo de impresión
        PrinterJob printerJob = PrinterJob.createPrinterJob();

        if (printerJob != null && printerJob.showPrintDialog(null)) {
            // Si el usuario acepta la impresión, enviamos el nodo (ticketVBox) a la impresora
            boolean printed = printerJob.printPage(ticketVBox);

            if (printed) {
                printerJob.endJob();
            }
        }
    }



}
