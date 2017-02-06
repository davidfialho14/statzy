package gui.main;

import core.*;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.apache.commons.lang3.text.WordUtils;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Set;

/**
 * Task to perform the statistics processing. It is based on a statistics generator.
 */
public class StatisticsTask extends Task<Integer> implements ProgressListener {

    private static final StatisticsGenerator statisticsGenerator = new StatisticsGenerator();
    private static final TimestampFormatter formatter = TimestampFormatter.ofPattern("uuuu-MM-dd HH:mm:ss");

    private final File headersFile;
    private final int dateColumn;
    private final int timeColumn;
    private final Set<Integer> ignoredColumns;
    private final DataRecordReader.Builder readerBuilder;
    private final DataFileWriter.Builder writerBuilder;
    private final Period period;

    public StatisticsTask(File headersFile, int dateColumn, int timeColumn, Set<Integer> ignoredColumns,
                          DataRecordReader.Builder readerBuilder, DataFileWriter.Builder writerBuilder,
                          Period period) {
        this.headersFile = headersFile;
        this.dateColumn = dateColumn;
        this.timeColumn = timeColumn;
        this.ignoredColumns = ignoredColumns;
        this.readerBuilder = readerBuilder;
        this.writerBuilder = writerBuilder;
        this.period = period;
    }

    @Override
    public Integer call() throws IOException, ParseException {

        Headers headers;
        try (HeadersReader headersReader = new HeadersReader(headersFile, dateColumn, timeColumn, ignoredColumns)) {
            headers = headersReader.read();
        }

        try (
                DataRecordReader reader = readerBuilder.build();
                DataFileWriter writer = writerBuilder.withHeaders(headers).build();
        ) {
            statisticsGenerator.addListener(this);
            statisticsGenerator.process(reader, writer, period);
            statisticsGenerator.removeListener(this);

        }

        return null;
    }

    /**
     * Notifies the progress listener that a new period is beginning to be processed. Tells the progress
     * listener what are the lower-bound and upper-bound timestamps of the period being processed. Not the
     * upper-bound value is not included in the period, for instance if the period is of 5 seconds and the
     * upper-bound is 10:12:05 then the period comprises only data relative to time before that time.
     *
     * @param lowerBound the lower-bound value of the period currently being processed.
     * @param upperBound the upper-bound value of the period currently being processed.
     */
    @Override
    public void notifyProcessingPeriod(Timestamp lowerBound, Timestamp upperBound) {
        updateMessage("Period between " + formatter.format(lowerBound) + " and " +
                formatter.format(upperBound));
    }

}