package ind.hailin.dailynus.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Created by hailin on 2017/5/29.
 */

public class MyJsonParsers {

    public static Map<String, List<String>> getFacultyMajor(InputStream jsonStream) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, List<String>> map = objectMapper.readValue(jsonStream, new TypeReference<Map<String, List<String>>>(){});
        return map;
    }
}
