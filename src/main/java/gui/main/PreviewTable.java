package gui.main;

import core.Record;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.apache.commons.csv.CSVRecord;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
public class PreviewTable extends TableView<ObservableList<String>> {

    private boolean dataIsSet = false;
    private boolean headersAreSet = false;

    /**
     *
     */
    private static class PreviewCell extends TableCell<ObservableList<String>, String> {

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (!isEmpty()) {
                setText(item);
            }
        }

    }

    /**
     * Gives the table column an index that can be used by the cell value factory to to get the correct value
     * from the data model. Adds a context menu to set the column as a date, time or datetime column. Adds a
     */
    public static class PreviewTableColumn extends TableColumn<ObservableList<String>, String> {

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

    public void setHeaders(List<String> headers) throws PreviewException {

        if (dataIsSet) {

            if (headers.size() != getColumns().size()) {

                for (TableColumn<ObservableList<String>, ?> column : getColumns()) {
                    column.setText("");
                }


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

    public void setData(List<Record> dataRecords) throws PreviewException {

        if (headersAreSet) {

            getItems().clear();

            int recordSize = dataRecords.get(0).size();
            if (recordSize != getColumns().size()) {
                throw new PreviewException("Data does not correspond to current headers file: data file " +
                        "has " + recordSize + " columns and the headers file has " + getColumns().size() +
                        " headers.");
            }

            getItems().addAll(dataRecords.stream()
                    .map(dataRecord -> FXCollections.observableArrayList(dataRecord.toCollection()))
                    .collect(Collectors.toList()));
        } else {

            getColumns().clear();

            int recordSize = dataRecords.get(0).size();
            for (int i = 0; i < recordSize; i++) {
                getColumns().add(new PreviewTableColumn("", i));
            }

            getItems().addAll(dataRecords.stream()
                    .map(dataRecord -> FXCollections.observableArrayList(dataRecord.toCollection()))
                    .collect(Collectors.toList()));
        }

        dataIsSet = true;
    }

    public void clearHeaders() {

        if (dataIsSet) {

            for (TableColumn<ObservableList<String>, ?> column : getColumns()) {
                column.setText("");
            }

        } else {
            getColumns().clear();
        }

        headersAreSet = false;
    }

    public void clearData() {

        if (headersAreSet) {
            getItems().clear();
        } else {
            getColumns().clear();
        }

        dataIsSet = false;
    }

}
