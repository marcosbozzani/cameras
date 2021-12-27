package duck.cameras.android.model;

public class Result<T> {
    private T value;
    private boolean status;
    private Throwable throwable;

    public static <T> Result<T> ok(T value) {
        Result<T> result = new Result<>();
        result.value = value;
        result.status = true;
        result.throwable = null;
        return result;
    }

    public static <T> Result<T> error(T value) {
        return error(value, null);
    }

    public static <T> Result<T> error(T value, Throwable throwable) {
        Result<T> result = new Result<>();
        result.value = value;
        result.status = false;
        result.throwable = throwable;
        return result;
    }

    public boolean ok() {
        return status;
    }

    public Throwable error() {
        return throwable;
    }

    public T value() {
        return value;
    }
}
