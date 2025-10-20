package app.pooi.workflow.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JacksonUtil {

    public static final ObjectMapper SHARE_MAPPER = new ObjectMapper();

    @SneakyThrows
    public static String writeValueAsString(Object obj) {
        return SHARE_MAPPER.writeValueAsString(obj);
    }

    @SneakyThrows
    public static <T> T readValue(String content, Class<T> clazz) {
        return SHARE_MAPPER.readValue(content, clazz);
    }

}
