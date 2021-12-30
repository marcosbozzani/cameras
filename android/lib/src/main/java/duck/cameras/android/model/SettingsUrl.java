package duck.cameras.android.model;

public class SettingsUrl {
    private String value;

    public SettingsUrl(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public boolean isEmpty() {
        return value == null || value.isEmpty();
    }
}
