package duck.cameras.android.model;

public class LoginToken {
    private final String value;

    public LoginToken(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public boolean isEmpty() {
        return value == null || value.isEmpty();
    }
}
