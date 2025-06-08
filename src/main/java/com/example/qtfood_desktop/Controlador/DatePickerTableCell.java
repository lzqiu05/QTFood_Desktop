package com.example.qtfood_desktop.Controlador;

import javafx.scene.control.TableCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ContentDisplay;

import java.time.LocalDate;

import javafx.scene.control.TableCell;
import javafx.scene.control.DatePicker;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

import java.time.LocalDate;
/**
 * Celda personalizada para una {TableView} que permite la edición directa de fechas mediante un {@link DatePicker}.
 * <p>
 * Esta celda se activa con doble clic y permite seleccionar una fecha desde un control gráfico.
 * Al perder el foco, se cancela la edición si no se ha confirmado.
 * </p>
 *
 * @param <S> el tipo del objeto (fila) al que pertenece la celda.
 */
public class DatePickerTableCell<S> extends TableCell<S, LocalDate> {
    private final DatePicker datePicker;

    public DatePickerTableCell() {
        this.datePicker = new DatePicker();

        // Usar el setOnAction para commit, no el valueProperty listener
        datePicker.setOnAction(event -> {
            commitEdit(datePicker.getValue());
        });

        datePicker.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                cancelEdit();
            }
        });

        setGraphic(null);
        setContentDisplay(ContentDisplay.TEXT_ONLY);

        this.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (!isEmpty() && event.getClickCount() == 2) {
                startEdit();
                event.consume();
            }
        });
    }

    @Override
    public void startEdit() {
        if (!isEmpty()) {
            super.startEdit();
            datePicker.setValue(getItem());
            setGraphic(datePicker);
            setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            datePicker.requestFocus();
        }
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setContentDisplay(ContentDisplay.TEXT_ONLY);
        setGraphic(null);
    }

    @Override
    protected void updateItem(LocalDate item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            if (isEditing()) {
                datePicker.setValue(item);
                setGraphic(datePicker);
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            } else {
                setText(item.toString());
                setContentDisplay(ContentDisplay.TEXT_ONLY);
                setGraphic(null);
            }
        }
    }
}
