package com.example.qtfood_desktop.Controlador;

import com.example.qtfood_desktop.Modelo.Admin;
import com.example.qtfood_desktop.Modelo.Categoria;
import com.example.qtfood_desktop.Vista.App;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;

import static com.example.qtfood_desktop.Vista.App.mostrarAlerta;

public class pantallaPrincipalController {

    @FXML
    private BorderPane rootPane;
    @FXML
    private ProductosController productosController;

    @FXML
    private void gestionarPedido() throws IOException {
        loadFXML(getClass().getResource("/com/example/qtfood_desktop/Vistas/PedidoSearch.fxml"));

    }
    @FXML
    private void gestionarProducto() throws IOException {
        loadFXML(getClass().getResource("/com/example/qtfood_desktop/Vistas/ProductoSearch.fxml"));

    }
    @FXML
    private void gestionarUsuarios() throws IOException {
        loadFXML(getClass().getResource("/com/example/qtfood_desktop/Vistas/UsuarioSearch.fxml"));

    }
    @FXML
    private void gestionarOferta() throws IOException {
        loadFXML(getClass().getResource("/com/example/qtfood_desktop/Vistas/OfertasSearch.fxml"));

    }
    @FXML
    public void gestionarReservas() throws IOException {
        loadFXML(getClass().getResource("/com/example/qtfood_desktop/Vistas/ReservasSearch.fxml"));
    }


    @FXML
    private void initialize() {

    }
    private void loadFXML(URL url) throws IOException {

        FXMLLoader loader = new FXMLLoader(url);
        rootPane.setCenter(loader.load());

    }

    public void setBottom(Node nodo) {
        rootPane.setBottom(nodo);
    }

    public void cambiarContrasena(ActionEvent actionEvent) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Cambiar contraseña");
        dialog.setHeaderText(null);
        dialog.setContentText("Introduce la nueva contraseña:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(contrasenia -> {
            contrasenia = contrasenia.trim();

            // Validación: campo vacío
            if (contrasenia.isEmpty()) {
                mostrarAlerta("Error", "La contraseña no puede estar vacía.");
                return;
            }

            try {
                // Cargar el objeto Admin existente
                Admin admin;
                File archivo = new File("admin.dat");
                if (archivo.exists()) {
                    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(archivo))) {
                        admin = (Admin) ois.readObject();
                    }
                } else {
                    // Si no existe, creamos uno nuevo
                    admin = new Admin();
                }

                admin.setPassword(contrasenia);

                // Guardar el objeto actualizado
                try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("admin.dat"))) {
                    oos.writeObject(admin);
                }

                mostrarAlerta("Éxito", "Contraseña actualizada correctamente.");

            } catch (Exception e) {
                e.printStackTrace();
                mostrarAlerta("Error", "Ocurrió un error al guardar la contraseña.");
            }
        });
    }

    public void cerrarSesion(ActionEvent actionEvent) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar cierre de sesión");
        alert.setHeaderText(null);
        alert.setContentText("¿Estás seguro de que deseas cerrar sesión?");
    
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            App.setRoot("Login");
        }
    }


    @FXML
    private void realizarCopiaDeSeguridad(ActionEvent event) {
        try {
            LocalDate hoy = LocalDate.now();
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Guardar copia de seguridad");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Archivo SQL", "*.sql")
            );
            fileChooser.setInitialFileName("Copia_seguridad.sql("+hoy+")");
            fileChooser.setInitialDirectory(new File("C:\\Users\\Liang Zhi\\Desktop\\s\\Copias de seguridad"));
            File file = fileChooser.showSaveDialog(((Node) event.getSource()).getScene().getWindow());
            if (file == null) {
                System.out.println("Operación cancelada por el usuario.");
                return;
            }

            String filePath = file.getAbsolutePath();

            String command = "\"C:\\xampp\\mysql\\bin\\mysqldump.exe\" -u root qtfood2 -r \"" + filePath + "\"";

            Process process = Runtime.getRuntime().exec(command);
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("Copia de seguridad realizada en: " + filePath);
            } else {
                System.err.println("Error en la copia de seguridad");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void importarCopiaDeSeguridad(ActionEvent event) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Seleccionar archivo de respaldo");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Archivos SQL", "*.sql")
            );
            fileChooser.setInitialDirectory(new File("C:\\Users\\Liang Zhi\\Desktop\\s\\Copias de seguridad"));

            File file = fileChooser.showOpenDialog(((Node) event.getSource()).getScene().getWindow());

            if (file == null) {
                System.out.println("Operación cancelada por el usuario.");
                return;
            }

            String filePath = file.getAbsolutePath();

            ProcessBuilder pb = new ProcessBuilder("C:\\xampp\\mysql\\bin\\mysql.exe", "-u", "root", "qtfood2");
            Process process = pb.start();

            // Abrimos el archivo .sql y lo enviamos al proceso mysql
            try (OutputStream os = process.getOutputStream();
                 FileInputStream fis = new FileInputStream(file)) {
                byte[] buffer = new byte[1024];
                int read;
                while ((read = fis.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                os.flush();
            }

            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("Importación completada correctamente desde: " + filePath);
            } else {
                System.err.println("Error durante la importación. Código de salida: " + exitCode);

                // Opcional: Leer el error de la consola
                try (BufferedReader br = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        System.err.println(line);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
