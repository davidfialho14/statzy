package gui.main;

import core.Record;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
public class PreviewTable extends TableView<Record> {

    private boolean dataIsSet = false;
    private boolean headersAreSet = false;

    /**
     * Gives the table column an index that can be used by the cell value factory to to get the correct value
     * from the data model. Adds a context menu to set the column as a date, time or datetime column. Adds a
     */
    public static class PreviewTableColumn extends TableColumn<Record, String> {

        public PreviewTableColumn(String header, int index) {
            super();
            setText(header);

            // disable sorting and resizing the columns
            setSortable(false);

            // since each row stores a list, the cell value for each column is the item in the position given
            // by the column index
            setCellValueFactory(param -> new SimpleStringProperty(param.getValue().get(index)));
        }

    }

    /**
     * Sets the headers to be previewed. Headers are shown in the same order they are in the list.
     *
     * @param headers the headers to be preview.
     * @throws PreviewException if the number of headers does not correspond to the number of columns of
     * the data being previewed.
     */
    public void setHeaders(List<String> headers) throws PreviewException {

        if (dataIsSet) {

            if (headers.size() != getColumns().size()) {
                // clear headers from all columns
                getColumns().forEach(column -> column.setText(""));

                throw new PreviewException("Headers does not correspond to current data file: headers file " +
                        "has " + headers.size() + " headers and the data file has " + getColumns().size() +
                        " columns.");
            }

            for (int i = 0; i < headers.size(); i++) {
                getColumns().get(i).setText(headers.get(i));
            }

        } else {

            getColumns().clear();

            for (int i = 0; i < headers.size(); i++) {
                getColumns().add(new PreviewTableColumn(headers.get(i), i));
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

        // NOTE: clearing just the columns does not clear the item from the table view!

        // clear all current data being previewed
        getItems().clear();

        if (headersAreSet) {

            // ensure the number of columns occupied by the data matches the number of headers
            int recordSize = dataRecords.get(0).size();
            if (recordSize != getColumns().size()) {
                throw new PreviewException("Data does not correspond to current headers file: data file " +
                        "has " + recordSize + " columns and the headers file has " + getColumns().size() +
                        " headers.");
            }

        } else {

            getColumns().clear();

            // create new columns
            int recordSize = dataRecords.get(0).size();
            for (int i = 0; i < recordSize; i++) {
                getColumns().add(new PreviewTableColumn("", i));
            }

        }

        // fill table with data
        getItems().addAll(dataRecords.stream().collect(Collectors.toList()));

        dataIsSet = true;
    }

    /**
     * Clears the headers from the preview table. If preview data is set then the column headers are
     * cleared but the data preview is kept. If no preview data is set then all columns cleared.
     */
    public void clearHeaders() {

        if (dataIsSet) {
            getColumns().forEach(column -> column.setText(""));
        } else {
            getColumns().clear();
        }

        headersAreSet = false;
    }

    /**
     * Clears the data from the preview table. If preview headers are set then all data is cleared from the
     * columns but the headers are kept. If no preview headers are set then all columns cleared.
     */
    public void clearData() {

        if (headersAreSet) {
            getItems().clear();
        } else {
            getColumns().clear();
        }

        dataIsSet = false;
    }

}
