package hu.educloud.main.config;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonConfig {
    // This class intentionally left minimal to avoid package/path mismatch errors.
    // The actual ObjectMapper bean for the application will be created under
    // hu.studentspace.main.config.JacksonConfig so Spring's component scan picks it up.

    public ObjectMapper createPlaceholder() {
        return new ObjectMapper();
    }
}
