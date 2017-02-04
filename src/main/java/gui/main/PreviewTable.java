package gui.main;

import com.sun.javafx.collections.ObservableSetWrapper;
import core.Record;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 *
 */
public class PreviewTable extends GridPane implements Initializable {

    @FXML private TableView<Record> tableView;
    @FXML private ToggleButton dateToggle;
    @FXML private ToggleButton timeToggle;
    @FXML private ToggleButton ignoreToggle;
    
    private boolean dataIsSet = false;
    private boolean headersAreSet = false;
    private IntegerProperty dateColumn= new SimpleIntegerProperty(0);
    private IntegerProperty timeColumn = new SimpleIntegerProperty(1);
    private ObservableSet<Integer> ignoredColumns = new ObservableSetWrapper<>(new HashSet<>());

    public PreviewTable() {

        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxmls/preview_table.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

    }

    /**
     * Called to initialize a controller after its root element has been
     * completely processed.
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  <tt>null</tt> if the location is not known.
     * @param resources The resources used to localize the root object, or <tt>null</tt> if
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // Ensure the selected columns are updated every time a new date/time/ignore column is selected
        dateColumn.addListener((observable, oldValue, newValue) -> tableView.refresh());
        timeColumn.addListener((observable, oldValue, newValue) -> tableView.refresh());
        ignoredColumns.addListener((SetChangeListener<? super Integer>) change -> tableView.refresh());
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *  Cell class definition
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    private static class PreviewTableCell extends TableCell<Record, String> {

        private final PreviewTableColumn column;

        private PreviewTableCell(PreviewTableColumn column) {
            this.column = column;
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (!isEmpty()) {

                if (column.isDateColumn() && column.isTimeColumn()) {
                    // make the cell blue
                    this.setStyle(
                            "-fx-background-color: #cfd9e8;" +
                                    "-fx-border-color: #dbe4f3; " +
                                    "-fx-border-width: 0.5"
                    );

                } else if (column.isDateColumn()) {
                    // make the cell yellow
                    this.setStyle(
                            "-fx-background-color: #eeefa4;" +
                                    "-fx-border-color: #fcffb3; " +
                                    "-fx-border-width: 0.5"
                    );

                } else if (column.isTimeColumn()) {
                    // make the cell green
                    this.setStyle(
                            "-fx-background-color: #b4e29b;" +
                                    "-fx-border-color: #c2ffbe; " +
                                    "-fx-border-width: 0.5"
                    );

                } else if (column.isIgnoredColumn()) {
                    // make the cell gray
                    this.setStyle(
                            "-fx-text-fill: rgb(176,175,175);"
                    );
                }

                setText(item);
            }
        }
    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *  Column class definition
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * Gives the table column an index that can be used by the cell value factory to to get the correct value
     * from the data model. Adds a context menu to set the column as a date, time or datetime column. Adds a
     */
    public class PreviewTableColumn extends TableColumn<Record, String> {

        private final Button button;
        private final int index;

        public PreviewTableColumn(String header, int index) {
            super();
            this.index = index;

            button = new Button(header);
            button.setMaxWidth(Double.MAX_VALUE);   // ensure button stretches for the entire column width
            button.setStyle("-fx-background-color:transparent;");   // make button transparent

            // Setup the logic to selecting the date, time, and ignored columns

            button.setOnMouseClicked(event -> {

                if (dateToggle.isSelected()) {
                    selectSingleColumn(dateColumn, dateToggle);

                } else if (timeToggle.isSelected()) {
                    selectSingleColumn(timeColumn, timeToggle);

                } else if (ignoreToggle.isSelected()) {

                    // select columns as ignored only if this is not the date and/or time column
                    if (index != dateColumn.getValue() && index != timeColumn.getValue()) {

                        // if the column is already ignored then this makes the column "un-ignored"
                        // if the column was not ignored then it becomes ignored
                        if (!ignoredColumns.remove(index)) {
                            ignoredColumns.add(index);
                        }
                    }

                }

            });

            setGraphic(button);

            // disable sorting and resizing the columns
            setSortable(false);

            // since each row stores a list, the cell value for each column is the item in the position given
            // by the column index
            setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(index)));

            // use PreviewTableCells instead of the default TableCells
            setCellFactory(param -> new PreviewTableCell(this));
        }

        public void setHeader(String header) {
            button.setText(header);
        }

        /**
         * Checks if this column is the date column.
         *
         * @return true if this column is the date column and false is otherwise.
         */
        public boolean isDateColumn() {
            return index == dateColumn.getValue();
        }

        /**
         * Checks if this column is the time column.
         *
         * @return true if this column is the time column and false is otherwise.
         */
        public boolean isTimeColumn() {
            return index == timeColumn.getValue();
        }

        /**
         * Checks if this column is being ignored.
         *
         * @return true if this column is being ignored and false if otherwise.
         */
        public boolean isIgnoredColumn() {
            return ignoredColumns.contains(index);
        }

        private void selectSingleColumn(IntegerProperty columnIndex, ToggleButton toggle) {
            // update the index to the index of this column
            columnIndex.setValue(index);
            // if the column was being ignored now it is not
            ignoredColumns.remove(columnIndex.getValue());

            // un-select the toggle automatically
            // This is a good solution since the user can only select column with this toggle
            toggle.setSelected(false);
        }

    }

    /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
     *
     *  Public Interface
     *
     * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

    /**
     * Sets the headers to be previewed. Headers are shown in the same order they are in the list.
     *
     * @param headers the headers to be preview.
     * @throws PreviewException if the number of headers does not correspond to the number of columns of
     * the data being previewed.
     */
    public void setHeaders(List<String> headers) throws PreviewException {
        // unset preview headers - ensure headers preview is unset if some error occurs
        headersAreSet = false;

        if (dataIsSet) {

            if (headers.size() != tableView.getColumns().size()) {
                // clear headers from all columns
                tableView.getColumns().forEach(column -> preview(column).setHeader(""));

                throw new PreviewException("Headers does not correspond to current data file: headers file " +
                        "has " + headers.size() + " headers and the data file has " +
                        tableView.getColumns().size() + " columns.");
            }

            for (int i = 0; i < headers.size(); i++) {
                preview(tableView.getColumns().get(i)).setHeader(headers.get(i));
            }

        } else {

            tableView.getColumns().clear();

            for (int i = 0; i < headers.size(); i++) {
                tableView.getColumns().add(new PreviewTableColumn(headers.get(i), i));
            }

        }

        headersAreSet = true;
    }

    /**
     * Sets the data to be previewed. Data is shown in the same order it is in the list.
     *
     * @param dataRecords the data to be preview.
     * @throws PreviewException if the number of data values in each record does not correspond to the number
     * of headers being previewed.
     */
    public void setData(List<Record> dataRecords) throws PreviewException {
        // unset preview data - ensure data preview is unset if some error occurs
        dataIsSet = false;

        // NOTE: clearing just the columns does not clear the item from the table view!

        // clear all current data being previewed
        tableView.getItems().clear();

        if (headersAreSet) {

            // ensure the number of columns occupied by the data matches the number of headers
            int recordSize = dataRecords.get(0).size();
            if (recordSize != tableView.getColumns().size()) {
                throw new PreviewException("Data does not correspond to current headers file: data file " +
                        "has " + recordSize + " columns and the headers file has " + tableView.getColumns().size() +
                        " headers.");
            }

        } else {

            tableView.getColumns().clear();

            // create new columns
            int recordSize = dataRecords.get(0).size();
            for (int i = 0; i < recordSize; i++) {
                tableView.getColumns().add(new PreviewTableColumn("", i));
            }

        }

        // fill table with data
        tableView.getItems().addAll(dataRecords.stream().collect(Collectors.toList()));

        dataIsSet = true;
    }

    /**
     * Clears the headers from the preview table. If preview data is set then the column headers are
     * cleared but the data preview is kept. If no preview data is set then all columns cleared.
     */
    public void clearHeaders() {

        if (dataIsSet) {
            tableView.getColumns().forEach(column -> column.setText(""));
        } else {
            tableView.getColumns().clear();
        }

        headersAreSet = false;
    }

    /**
     * Clears the data from the preview table. If preview headers are set then all data is cleared from the
     * columns but the headers are kept. If no preview headers are set then all columns cleared.
     */
    public void clearData() {

        if (headersAreSet) {
            tableView.getItems().clear();
        } else {
            tableView.getColumns().clear();
        }

        dataIsSet = false;
    }

    private static PreviewTableColumn preview(TableColumn<Record, ?> column) {
        return (PreviewTableColumn) column;
    }

}
