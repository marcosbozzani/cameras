package duck.cameras.android.service;

import android.content.res.Resources;

import androidx.annotation.RawRes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class ResourceLoader {
    private final Resources resources;
    private final HashMap<Integer, String> cache = new HashMap<>();

    public ResourceLoader(Resources resources) {
        this.resources = resources;
    }

    public String loadString(@RawRes int id, Object... params) {
        if (!cache.containsKey(id)) {
            try (InputStream inputStream = resources.openRawResource(id)) {
                final ByteArrayOutputStream result = new ByteArrayOutputStream();
                final byte[] buffer = new byte[8192];
                for (int length; (length = inputStream.read(buffer)) != -1; ) {
                    result.write(buffer, 0, length);
                }
                cache.put(id, result.toString(StandardCharsets.UTF_8.name()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return String.format(Locale.ROOT, Objects.requireNonNull(cache.get(id)), params);
    }

}
