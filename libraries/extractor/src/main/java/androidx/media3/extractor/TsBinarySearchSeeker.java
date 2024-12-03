package androidx.media3.extractor;

public class TsBinarySearchSeeker {
    public TsBinarySearchSeeker(Object pcrTimestampAdjuster, long durationUs, long inputLength, int pcrPid, int timestampSearchBytes) {
    }

    public void setSeekTargetUs(long timeUs) {
    }

    public boolean isSeeking() {
        return false;
    }

    public int handlePendingSeek(ExtractorInput input, PositionHolder seekPosition) {
        return 0;
    }

    public SeekMap getSeekMap() {
        return null;
    }
}
