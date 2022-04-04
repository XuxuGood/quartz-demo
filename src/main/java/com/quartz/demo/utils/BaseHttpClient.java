package com.quartz.demo.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * @author xiaoxuxuy
 * @date 2022/4/4 9:17 下午
 */
public class BaseHttpClient {

    private static final Logger LOG = LoggerFactory.getLogger(BaseHttpClient.class.getName());
    protected static final ObjectMapper MAPPER = new ObjectMapper();
    private boolean urlParamsEncoding = true;
    protected String endpoint = "";
    protected String path = "";
    protected int retry = 1;
    protected int timeout = 5000;
    protected String encoding = "UTF-8";
    protected Map<String, String> params = new HashMap();
    protected Map<String, String> headers = new HashMap();
    protected String contentType = "application/json";
    protected String body = "";
    protected boolean errorResponseBody = false;
    protected String publicKey = "";
    protected String accessKey = "";

    private static void allowHttpMethods(String... methods) {
        try {
            Field methodsField = HttpURLConnection.class.getDeclaredField("methods");
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(methodsField, methodsField.getModifiers() & -17);
            methodsField.setAccessible(true);
            String[] oldMethods = (String[]) ((String[]) methodsField.get((Object) null));
            Set<String> methodsSet = new LinkedHashSet(Arrays.asList(oldMethods));
            methodsSet.addAll(Arrays.asList(methods));
            String[] newMethods = (String[]) methodsSet.toArray(new String[0]);
            methodsField.set((Object) null, newMethods);
        } catch (Exception var6) {
            LOG.error("init http methods failed,error:", var6);
        }

    }

    public BaseHttpClient(String endpoint) {
        this.endpoint = endpoint;
    }

    public BaseHttpClient urlParamsEncoding(boolean urlParamsEncoding) {
        this.urlParamsEncoding = urlParamsEncoding;
        return this;
    }

    public BaseHttpClient path(String path) {
        this.path = path;
        return this;
    }

    public BaseHttpClient param(String key, String value) {
        this.params.put(key, value);
        return this;
    }

    public BaseHttpClient param(Map<String, String> map) {
        if (MapUtils.isNotEmpty(map)) {
            this.params.putAll(map);
        }

        return this;
    }

    public BaseHttpClient header(String key, String value) {
        this.headers.put(key, value);
        return this;
    }

    public BaseHttpClient header(Map<String, String> map) {
        if (MapUtils.isNotEmpty(map)) {
            this.headers.putAll(map);
        }

        return this;
    }

    public BaseHttpClient body(String contentType, String body) {
        if (StringUtils.isNotBlank(contentType)) {
            this.contentType = contentType;
        }

        this.body = body;
        return this;
    }

    public BaseHttpClient jsonBody(String body) {
        this.body("application/json;charset=UTF-8", body);
        return this;
    }

    public BaseHttpClient jsonBody(Object object) {
        try {
            this.body("application/json;charset=UTF-8", MAPPER.writeValueAsString(object));
            return this;
        } catch (JsonProcessingException var3) {
            LOG.error("JsonProcessingException when serialize json body!", var3);
            throw new RuntimeException("JsonProcessingException when serialize json body!");
        }
    }

    public BaseHttpClient sign(String accessKey, String publicKey) {
        this.accessKey = accessKey;
        this.publicKey = publicKey;
        return this;
    }

    public BaseHttpClient retry(int retry) {
        this.retry = retry > 1 ? retry : 1;
        return this;
    }

    public BaseHttpClient timeout(int timeout) {
        this.timeout = timeout;
        return this;
    }

    public BaseHttpClient errorResponseBody(boolean errorResponseBody) {
        this.errorResponseBody = errorResponseBody;
        return this;
    }

    public BaseHttpClient encoding(String encoding) {
        if (StringUtils.isNotBlank(encoding)) {
            this.encoding = encoding;
        }

        return this;
    }

    public <T> T get(Class<T> returnType) {
        return this.requestRetryTimes("GET", returnType);
    }

    public <T> T put(Class<T> returnType) {
        return this.requestRetryTimes("PUT", returnType);
    }

    public <T> T post(Class<T> returnType) {
        return this.requestRetryTimes("POST", returnType);
    }

    public <T> T delete(Class<T> returnType) {
        return this.requestRetryTimes("DELETE", returnType);
    }

    public <T> T patch(Class<T> returnType) {
        return this.requestRetryTimes("PATCH", returnType);
    }

    private <T> T requestRetryTimes(String method, Class<T> returnType) {
        for (int i = 0; i < this.retry; ++i) {
            T t = this.request(method, returnType);
            if (null != t) {
                return t;
            }
        }

        return null;
    }

    protected <T> T request(String method, Class<T> returnType) {
        HttpURLConnection conn = null;
        OutputStream out = null;
        URL url = null;
        StopWatch sw = new StopWatch();
        sw.start();
        boolean var24 = false;

        int responseCode;
        String var38;
        label245:
        {
            Object var37;
            label246:
            {
                try {
                    var24 = true;
                    url = new URL(this.endpoint + this.path + this.urlParamString());
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(5000);
                    conn.setReadTimeout(this.timeout);
                    conn.setRequestMethod(method);
                    if (StringUtils.isNotBlank(this.accessKey) && StringUtils.isNotBlank(this.publicKey)) {
                        String sign = this.signParams();
                        this.headers.put("Authorization", this.accessKey + " " + sign);
                    }

                    if (MapUtils.isNotEmpty(this.headers)) {
                        Iterator var35 = this.headers.entrySet().iterator();

                        while (var35.hasNext()) {
                            Map.Entry<String, String> header = (Map.Entry) var35.next();
                            conn.addRequestProperty((String) header.getKey(), (String) header.getValue());
                        }
                    }

                    conn.setRequestProperty("Content-Type", this.contentType);
                    LOG.info("URL[{}], URL_PARAMS [{}], PARAMS [{}] ", new Object[]{url, this.urlParamString(), this.body});
                    if (StringUtils.isNotBlank(this.body)) {
                        conn.setDoOutput(true);
                        out = conn.getOutputStream();
                        out.write(this.body.getBytes(this.encoding));
                        out.flush();
                    }

                    InputStream in;
                    if (conn.getResponseCode() >= 400 && this.errorResponseBody) {
                        in = conn.getErrorStream();
                    } else {
                        in = conn.getInputStream();
                    }

                    if (returnType == String.class) {
                        var38 = IOUtils.toString(in, this.encoding);
                        var24 = false;
                        break label245;
                    }

                    var37 = MAPPER.readValue(new InputStreamReader(in, this.encoding), returnType);
                    var24 = false;
                    break label246;
                } catch (Exception var33) {
                    LOG.error("url:{} , http client exception:{}", url, ExceptionUtils.getFullStackTrace(var33));
                    var24 = false;
                } finally {
                    if (var24) {
                        sw.stop();
                        if (null != out) {
                            try {
                                out.close();
                            } catch (Exception var28) {
                                LOG.error(var28.getMessage());
                            }
                        }

                        responseCode = 0;
                        if (null != conn) {
                            try {
                                responseCode = conn.getResponseCode();
                                conn.disconnect();
                            } catch (Exception var27) {
                                LOG.error(var27.getMessage());
                            }
                        }

                        LOG.info("URL[{}] ResponseCode[{}] Time[{}ms]", new Object[]{url, responseCode, sw.getTotalTimeMillis()});
                    }
                }

                sw.stop();
                if (null != out) {
                    try {
                        out.close();
                    } catch (Exception var26) {
                        LOG.error(var26.getMessage());
                    }
                }

                responseCode = 0;
                if (null != conn) {
                    try {
                        responseCode = conn.getResponseCode();
                        conn.disconnect();
                    } catch (Exception var25) {
                        LOG.error(var25.getMessage());
                    }
                }

                LOG.info("URL[{}] ResponseCode[{}] Time[{}ms]", new Object[]{url, responseCode, sw.getTotalTimeMillis()});
                return null;
            }

            sw.stop();
            if (null != out) {
                try {
                    out.close();
                } catch (Exception var32) {
                    LOG.error(var32.getMessage());
                }
            }

            responseCode = 0;
            if (null != conn) {
                try {
                    responseCode = conn.getResponseCode();
                    conn.disconnect();
                } catch (Exception var31) {
                    LOG.error(var31.getMessage());
                }
            }

            LOG.info("URL[{}] ResponseCode[{}] Time[{}ms]", new Object[]{url, responseCode, sw.getTotalTimeMillis()});
            return (T) var37;
        }

        sw.stop();
        if (null != out) {
            try {
                out.close();
            } catch (Exception var30) {
                LOG.error(var30.getMessage());
            }
        }

        responseCode = 0;
        if (null != conn) {
            try {
                responseCode = conn.getResponseCode();
                conn.disconnect();
            } catch (Exception var29) {
                LOG.error(var29.getMessage());
            }
        }

        LOG.info("URL[{}] ResponseCode[{}] Time[{}ms]", new Object[]{url, responseCode, sw.getTotalTimeMillis()});
        return (T) var38;
    }

    protected String urlParamString() throws UnsupportedEncodingException {
        if (MapUtils.isEmpty(this.params)) {
            return "";
        } else {
            StringBuffer paramsSb = new StringBuffer("?");
            Iterator var2 = this.params.entrySet().iterator();

            while (var2.hasNext()) {
                Map.Entry<String, String> param = (Map.Entry) var2.next();
                if (StringUtils.isNotBlank((String) param.getKey())) {
                    String paramValue = (String) param.getValue();
                    if (this.urlParamsEncoding) {
                        paramValue = URLEncoder.encode(paramValue, this.encoding);
                    }

                    paramsSb.append((String) param.getKey()).append('=').append(paramValue).append('&');
                }
            }

            if (paramsSb.length() > 0) {
                return paramsSb.deleteCharAt(paramsSb.length() - 1).toString();
            } else {
                return "";
            }
        }
    }

    protected String signParams() throws Exception {
        StringBuilder paramSb = new StringBuilder();
        if (MapUtils.isNotEmpty(this.params)) {
            Map<String, String> sortParams = (Map) this.params.entrySet().stream().sorted(Map.Entry.comparingByKey()).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oleValue, newValue) -> {
                return oleValue;
            }, LinkedHashMap::new));
            Iterator it = sortParams.keySet().iterator();

            while (it.hasNext()) {
                String key = (String) it.next();
                String value = sortParams.get(key);
                paramSb.append(key).append("=").append(value).append("&");
            }

            paramSb = paramSb.deleteCharAt(paramSb.lastIndexOf("&"));
        }

        String md5Str = DigestUtils.md5Hex(paramSb.toString());
        String sign = RSAUtil.rsaEncrypt(md5Str, this.publicKey);
        return sign;
    }

    static {
        MAPPER.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        allowHttpMethods("PATCH");
    }

}
