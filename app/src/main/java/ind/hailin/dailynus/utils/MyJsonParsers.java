package ind.hailin.dailynus.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import ind.hailin.dailynus.entity.Users;
import ind.hailin.dailynus.exception.DesException;

/**
 * Created by hailin on 2017/5/29.
 */

public class MyJsonParsers {

    public static Map<String, List<String>> getFacultyMajor(InputStream jsonStream) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, List<String>> map = objectMapper.readValue(jsonStream, new TypeReference<Map<String, List<String>>>(){});
        return map;
    }

    public static Map<String, List<String>> getAllUsersName(InputStream jsonStream) throws IOException {
        return getFacultyMajor(jsonStream);
    }

    public static Users getEncryptedUserInfo(InputStream encryptStream) throws IOException, DesException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = encryptStream.read(buffer)) != -1) {
            baos.write(buffer, 0, len);
        }
        baos.flush();

        String jsonStr = DesEncryption.decryption(baos.toString());

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(jsonStr, Users.class);
    }
}
