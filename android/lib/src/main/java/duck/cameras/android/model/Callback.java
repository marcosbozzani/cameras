package duck.cameras.android.model;

public interface Callback<T> {
    void execute(Result<T> result);
    static <T> Callback<T> empty() {
        return result -> {};
    }
}
