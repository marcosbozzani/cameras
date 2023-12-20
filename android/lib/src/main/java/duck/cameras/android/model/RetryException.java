package duck.cameras.android.model;

import java.util.ArrayList;

public class RetryException extends RuntimeException {
    private final ArrayList<Exception> originalExceptions;

    public RetryException(ArrayList<Exception> originalExceptions) {
        this.originalExceptions = originalExceptions;
    }

    public ArrayList<Exception> getOriginalExceptions() {
        return originalExceptions;
    }
}
