module com.example.qtfood_desktop {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.github.librepdf.openpdf;
    requires java.desktop;

    // Abre los paquetes Modelo, Vista y Controlador para que FXML los pueda usar.
    opens com.example.qtfood_desktop.Modelo to javafx.fxml;
    opens com.example.qtfood_desktop.Vista to javafx.fxml;
    opens com.example.qtfood_desktop.Controlador to javafx.fxml;

    // Exporta el paquete Modelo para ser utilizado por otros módulos si es necesario.
    exports com.example.qtfood_desktop.Modelo;
    // Exporta Vista y Controlador si necesitas que estos paquetes sean accesibles fuera del módulo.
    exports com.example.qtfood_desktop.Vista;
    exports com.example.qtfood_desktop.Controlador;
}