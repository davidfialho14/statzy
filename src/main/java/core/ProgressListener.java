package core;

/**
 *
 */
public interface ProgressListener {

    /**
     * Notifies the progress listener that a new period is beginning to be processed. Tells the progress
     * listener what are the lower-bound and upper-bound timestamps of the period being processed. Not the
     * upper-bound value is not included in the period, for instance if the period is of 5 seconds and the
     * upper-bound is 10:12:05 then the period comprises only data relative to time before that time.
     *
     * @param lowerBound the lower-bound value of the period currently being processed.
     * @param upperBound the upper-bound value of the period currently being processed.
     */
    void notifyProcessingPeriod(Timestamp lowerBound, Timestamp upperBound);

    /**
     * Notifies the listener that the processing has finished.
     */
    void notifyFinished();

}
