package core;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * The statistics generator is the 'engine' behind statzy. Reads the data records from the input file,
 * computes the statistics and outputs the results into the data file writer.
 */
public class StatisticsGenerator {

    private final List<ProgressListener> listeners = new ArrayList<>();

    /**
     * This method is the entry point to process an input file, compute the statistics and output the
     * results. It takes a pre-configured data file reader to read the input data records, groups this
     * records according to the specified period and computes the statistics for each of this groups.
     * Outputs every result to a files using a pre-configured data file writer.
     *
     * @param dataReader the pre-configured data file reader used to read the input data records.
     * @param dataWriter the pre-configured data file writer used to output the results.
     * @param period     the period defined for each data group.
     * @throws ParseException if the input data file is not valid or some line in the file is corrupted.
     */
    public void process(DataRecordReader dataReader, DataFileWriter dataWriter, Period period)
            throws ParseException, IOException {

        DataRecord record = dataReader.read();  // read first record
        if (record == null) return;

        int valueCount = record.getDataValues().size();
        GroupStatistics groupStatistics = new GroupStatistics(valueCount);

        Timestamp currentPeriod = record.getTimestamp().truncatedTo(period.getUnit());
        Timestamp nextPeriod = currentPeriod.plus(period);
        notifyProcessingPeriod(currentPeriod, nextPeriod);

        while (record != null) {

            while (!record.getTimestamp().predates(nextPeriod)) {
                dataWriter.write(currentPeriod, groupStatistics.getCount(), groupStatistics.getMeans(),
                        groupStatistics.getStandardDeviations());

                groupStatistics.clear();

                currentPeriod = nextPeriod;
                nextPeriod = currentPeriod.plus(period);
                notifyProcessingPeriod(currentPeriod, nextPeriod);
            }

            groupStatistics.addEntry(record.getDataValues());
            record = dataReader.read();
        }

        if (groupStatistics.getCount() > 0) {
            dataWriter.write(currentPeriod, groupStatistics.getCount(), groupStatistics.getMeans(),
                    groupStatistics.getStandardDeviations());
        }

    }

    /**
     * Adds a new progress listener to be notified of progress updates.
     *
     * @param listener the listener to add.
     */
    public void addListener(ProgressListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes a progress listener from the list of listeners to be notified of progress updates.
     *
     * @param listener the listener to remove.
     */
    public void removeListener(ProgressListener listener) {
        listeners.remove(listener);
    }

    private void notifyProcessingPeriod(Timestamp lowerBound, Timestamp upperBound) {
        listeners.forEach(listener -> listener.notifyProcessingPeriod(lowerBound, upperBound));
    }

}
