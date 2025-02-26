package com.hsbc.common.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.PackageVersion;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.Serial;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JsonUtils {

    private JsonUtils() {
    }

    @Getter
    private static final ObjectMapper objectMapper;

    @Getter
    private static final ObjectMapper excludeNullObjMapper;

    static {
        objectMapper = configObjectMapper(JsonMapper.builder().disable(MapperFeature.IGNORE_DUPLICATE_MODULE_REGISTRATIONS).build(), false);
        excludeNullObjMapper = configObjectMapper(JsonMapper.builder().disable(MapperFeature.IGNORE_DUPLICATE_MODULE_REGISTRATIONS).build(), true);
    }

    public static ObjectMapper configObjectMapper(ObjectMapper objectMapper, boolean excludeNull) {
        objectMapper.registerModule(new ParameterNamesModule());
        objectMapper.registerModule(new Jdk8Module());
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        if (excludeNull) {
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        }
        objectMapper.enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.registerModule(simpleModule());
        return objectMapper;
    }

    public static SimpleModule simpleModule() {
        return simpleModule("baseModule");
    }

    public static SimpleModule simpleModule(String name) {
        SimpleModule module = new SimpleModule(name, PackageVersion.VERSION);

        module.addSerializer(LocalDateTime.class, new StdSerializer<>(LocalDateTime.class) {
            @Serial
            private static final long serialVersionUID = 3983102809524366420L;

            @Override
            public void serialize(LocalDateTime localDateTime, JsonGenerator jsonGenerator, SerializerProvider provider) {
                try {
                    jsonGenerator.writeNumber(localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
                } catch (IOException e) {
                    throw new RuntimeException("日期时间转换错误!");
                }
            }

            @Override
            public void serializeWithType(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
                serialize(value, gen, serializers);
            }
        });

        module.addDeserializer(LocalDateTime.class, new StdDeserializer<>(LocalDateTime.class) {
            @Serial
            private static final long serialVersionUID = -7765313664546764043L;

            @Override
            public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
                if (StringUtils.isBlank(jsonParser.getText())) {
                    return null;
                }
                try {
                    Instant instant = Instant.ofEpochMilli(Long.parseLong(jsonParser.getText()));
                    return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
                } catch (Exception ex) {
                    try {
                        return LocalDateTime.parse(jsonParser.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    } catch (Exception e) {
                        throw new RuntimeException("传入参数:" + jsonParser.getText() + "既不是合法日期秒数,也不是! yyyy-MM-dd HH:mm:ss");
                    }
                }
            }
        });

        module.addDeserializer(LocalTime.class, new JsonDeserializer<>() {
            @Override
            public LocalTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
                    throws IOException {
                if (StringUtils.isBlank(jsonParser.getText())) {
                    return null;
                }
                try {
                    return LocalTime.parse(jsonParser.getValueAsString(), DateTimeFormatter.ofPattern("HH:mm:ss"));
                } catch (Exception e) {
                    throw new RuntimeException("传入参数:" + jsonParser.getText() + "不是合法 HH:mm:ss");
                }
            }

        });

        module.addSerializer(LocalTime.class, new JsonSerializer<>() {
            @Override
            public void serialize(LocalTime localTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) {
                try {
                    jsonGenerator.writeString(DateTimeFormatter.ofPattern("HH:mm:ss").format(localTime));
                } catch (Exception ex) {
                    throw new RuntimeException("时间转换错误!");
                }
            }

            @Override
            public void serializeWithType(LocalTime value, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
                serialize(value, gen, serializers);
            }
        });

        module.addSerializer(LocalDate.class, new StdSerializer<>(LocalDate.class) {
            @Serial
            private static final long serialVersionUID = -7929121892882698436L;

            @Override
            public void serialize(LocalDate localDate, JsonGenerator jsonGenerator, SerializerProvider provider) {
                try {
                    jsonGenerator.writeNumber(localDate.atTime(0, 0, 0).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
                } catch (IOException e) {
                    throw new RuntimeException("日期转换错误!");
                }
            }

            @Override
            public void serializeWithType(LocalDate value, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
                serialize(value, gen, serializers);
            }
        });

        module.addDeserializer(LocalDate.class, new StdDeserializer<>(LocalDate.class) {
            @Serial
            private static final long serialVersionUID = 3697721414363137776L;

            @Override
            public LocalDate deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
                if (StringUtils.isBlank(jsonParser.getText())) {
                    return null;
                }
                try {
                    Instant instant = Instant.ofEpochMilli(Long.parseLong(jsonParser.getText()));
                    return LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate();
                } catch (Exception ex) {
                    try {
                        return LocalDate.parse(jsonParser.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                    } catch (Exception var5) {
                        throw new RuntimeException("传入参数:" + jsonParser.getText() + "既不是合法日期秒数,也不是! yyyy-MM-dd");
                    }
                }
            }

        });

        module.addSerializer(Date.class, new StdSerializer<>(Date.class) {
            @Serial
            private static final long serialVersionUID = -3155313320490990299L;

            @Override
            public void serialize(Date date, JsonGenerator jsonGenerator, SerializerProvider provider) {
                try {
                    jsonGenerator.writeNumber(date.getTime());
                } catch (IOException e) {
                    throw new RuntimeException("时间转换错误!", e);
                }
            }

            @Override
            public void serializeWithType(Date date, JsonGenerator gen, SerializerProvider serializers, TypeSerializer typeSer) throws IOException {
                serialize(date, gen, serializers);
            }
        });

        module.addDeserializer(Date.class, new StdDeserializer<>(Date.class) {
            @Serial
            private static final long serialVersionUID = -4595415096209435223L;

            @Override
            public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
                if (StringUtils.isBlank(jsonParser.getText())) {
                    return null;
                }
                try {
                    try {
                        return new Date(Long.parseLong(jsonParser.getText()));
                    } catch (Exception ex) {
                        // SimpleDateFormat 不是线程安全的，这里选择先转LocalDateTime
                        LocalDateTime parse = LocalDateTime.parse(jsonParser.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                        return Date.from(parse.atZone(ZoneId.systemDefault()).toInstant());
                    }
                } catch (Exception ex) {
                    throw new RuntimeException("传入参数:" + jsonParser.getText() + "不是合法日期秒数!");
                }
            }

        });
        return module;
    }

    /**
     * Object to JSON
     */
    public static String toJsonExcludeNull(Object obj) {
        if (null == obj)
            return null;
        try {
            return excludeNullObjMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("转换异常", e);
        }
    }

    /**
     * Object to JSON
     */
    public static String toJson(Object obj) {
        if (null == obj)
            return null;
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("转换异常", e);
        }
    }

    /**
     * JSON to Object
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        if (StringUtils.isBlank(json))
            return null;
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("转换异常", e);
        }
    }

    /**
     * 转化为json bytes
     */
    public static byte[] writeValueAsBytes(Object value) {
        try {
            return value == null ? null : objectMapper.writeValueAsBytes(value);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
            // TIP:
            // 原则上，不对异常包装，这里为什么要包装？因为正常情况不会发生IOException
        }
    }

    /**
     * 对象转换为map，如果是字符串，先转成json对象再转为map
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> toMap(Object value) throws IllegalArgumentException {
        return convertValue(value, Map.class);
    }

    /**
     * 读取为List或者Map
     */
    @SuppressWarnings("unchecked")
    public static <T> T readValue(String content) {
        if (StringUtils.isBlank(content))
            return null;

        char ch = content.charAt(0);
        try {
            if (ch == '[')
                return (T) objectMapper.readValue(content, List.class);
            return (T) objectMapper.readValue(content, Map.class);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static <T> T readValue(String content, Class<T> type) {
        if (StringUtils.isBlank(content))
            return null;
        try {
            return objectMapper.readValue(content, type);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static <T> T readValue(byte[] content, Class<T> type) {
        if (content == null)
            return null;
        try {
            return objectMapper.readValue(content, type);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @SuppressWarnings("rawtypes")
    public static <T> T readMap(String content, Class<? extends Map> mapClass, Class<?> keyClass, Class<?> valueClass) {
        if (content == null)
            return null;
        try {
            return objectMapper.readValue(content, objectMapper.getTypeFactory().constructMapType(mapClass, keyClass, valueClass));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @SuppressWarnings("rawtypes")
    public static <T> T readCollection(String content, Class<? extends Collection> collectionClass, Class<?> elementClass) {
        if (content == null)
            return null;
        try {
            return objectMapper.readValue(content, objectMapper.getTypeFactory().constructCollectionType(collectionClass, elementClass));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static <T> List<T> readListValue(String content, Class<T> elementClass) {
        if (StringUtils.isBlank(content))
            return null;
        try {
            return objectMapper.readValue(content, objectMapper.getTypeFactory().constructCollectionType(List.class, elementClass));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static <T> Set<T> readSetValue(String content, Class<T> elementClass) {
        if (StringUtils.isBlank(content))
            return null;
        try {
            return objectMapper.readValue(content, objectMapper.getTypeFactory().constructCollectionType(Set.class, elementClass));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static <T> T[] readArrayValue(String content, Class<T> elementClass) {
        if (StringUtils.isBlank(content))
            return null;
        try {
            return objectMapper.readValue(content, objectMapper.getTypeFactory().constructArrayType(elementClass));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 转换为目标类，如果value是字符串，将被认为是json串<br>
     * 所以特别注意：'"abc"'是json字符串，目标类型是String时，转换结果为'abc'而不是'"abc"'<br>
     */
    @SuppressWarnings("unchecked")
    public static <T> T convertValue(Object value, Class<T> clazz) throws IllegalArgumentException {
        if (value == null)
            return null;
        try {
            if (value instanceof String strVal) {
                if (!String.class.equals(clazz) && StringUtils.isBlank(strVal)) {
                    return null;
                }
                if (String.class.equals(clazz) && StringUtils.isBlank(strVal)) {
                    return (T) value;
                }
                value = objectMapper.readTree(strVal);
            }
            return objectMapper.convertValue(value, clazz);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public static <T> T getCollectionType(String json, TypeReference<T> typeRef) {
        if (StringUtils.isBlank(json))
            return null;
        try {
            return objectMapper.readValue(json, typeRef);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * 范型readValue json ==> Pager&lt;MyBean&gt;: readValue(json, Pager.class,
     * MyBean.class)<br>
     * json ==> List<Set<Integer>>: readValue(json, List.class, Integer.class)<br>
     */
    public static <T> T readValue(String json, Class<?> parametrized, Class<?> parametersFor, Class<?>... parameterClasses) {
        if (StringUtils.isBlank(json)) {
            return null;
        }

        JavaType type;
        if (parameterClasses == null || parameterClasses.length == 0) {
            type = objectMapper.getTypeFactory().constructParametricType(parametrized, parametrized, parametersFor);
        } else {
            type = objectMapper.getTypeFactory().constructParametricType(parametrized, parameterClasses);
        }

        try {
            return objectMapper.readValue(json, type);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 判断对象是否为合法JSON字符串
     */
    public static boolean mayBeJSON(Object object) {
        if (object == null || !String.class.isAssignableFrom(object.getClass())) {
            return false;
        }
        String string = (String) object;
        if (string.isEmpty()) {
            return false;
        }
        char head = string.charAt(0);
        char tail = string.charAt(string.length() - 1);
        return (head == '[' && tail == ']') || (head == '{' && tail == '}');
    }

    /**
     * 判断对象是否为合法JSON Object的字符串
     */
    public static boolean mayBeJSONObject(Object object) {
        if (object == null || !String.class.isAssignableFrom(object.getClass())) {
            return false;
        }
        String string = (String) object;
        if (string.isEmpty()) {
            return false;
        }
        char head = string.charAt(0);
        char tail = string.charAt(string.length() - 1);
        return head == '{' && tail == '}';
    }

    /**
     * 判断对象是否为合法JSON Array的字符串
     */
    public static boolean mayBeJSONArray(Object object) {
        if (object == null || !String.class.isAssignableFrom(object.getClass())) {
            return false;
        }
        String string = (String) object;
        if (string.isEmpty()) {
            return false;
        }
        char head = string.charAt(0);
        char tail = string.charAt(string.length() - 1);
        return head == '[' && tail == ']';
    }

}
