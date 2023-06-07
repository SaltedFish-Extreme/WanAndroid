package com.example.wanAndroid.util.cookie;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import androidx.annotation.NonNull;
import okhttp3.Cookie;
import okhttp3.HttpUrl;

/**
 * Cookie缓存持久化实现类
 *
 * @author linzhiyong
 * @email wflinzhiyong@163.com
 * @blog <a href="https://www.jianshu.com/p/23b35d403148">...</a>
 * @time 2018/7/20
 */
@SuppressWarnings("unused")
public class PersistentCookieStore implements CookieStore {

    private static final String LOG_TAG = "PersistentCookieStore";
    private static final String COOKIE_PREFS = "CookiePrefsFile";
    private static final String HOST_NAME_PREFIX = "host_";
    private static final String COOKIE_NAME_PREFIX = "cookie_";
    private final HashMap<String, ConcurrentHashMap<String, Cookie>> cookies;
    private final SharedPreferences cookiePrefs;
    private boolean omitNonPersistentCookies = false;

    /**
     * Construct a persistent cookie store.
     */
    public PersistentCookieStore(Context context) {
        this.cookiePrefs = context.getSharedPreferences(COOKIE_PREFS, 0);
        this.cookies = new HashMap<>();

        Map<Object, Object> tempCookieMap = new HashMap<>(cookiePrefs.getAll());
        for (Object key : tempCookieMap.keySet()) {
            if (!(key instanceof String) || !((String) key).contains(HOST_NAME_PREFIX)) {
                continue;
            }

            String cookieNames = (String) tempCookieMap.get(key);
            if (TextUtils.isEmpty(cookieNames)) {
                continue;
            }

            if (!this.cookies.containsKey(key)) {
                this.cookies.put((String) key, new ConcurrentHashMap<>());
            }

            String[] cookieNameArr = cookieNames.split(",");
            for (String name : cookieNameArr) {
                String encodedCookie = this.cookiePrefs.getString("cookie_" + name, null);
                if (encodedCookie == null) {
                    continue;
                }

                Cookie decodedCookie = this.decodeCookie(encodedCookie);
                if (decodedCookie != null) {
                    Objects.requireNonNull(this.cookies.get(key)).put(name, decodedCookie);
                }
            }
        }
        tempCookieMap.clear();

        clearExpired();
    }

    /**
     * 移除失效cookie
     */
    private void clearExpired() {
        SharedPreferences.Editor prefsWriter = cookiePrefs.edit();

        for (String key : this.cookies.keySet()) {
            boolean changeFlag = false;

            for (ConcurrentHashMap.Entry<String, Cookie> entry : Objects.requireNonNull(cookies.get(key)).entrySet()) {
                String name = entry.getKey();
                Cookie cookie = entry.getValue();
                if (isCookieExpired(cookie)) {
                    // Clear cookies from local store
                    Objects.requireNonNull(cookies.get(key)).remove(name);

                    // Clear cookies from persistent store
                    prefsWriter.remove(COOKIE_NAME_PREFIX + name);

                    changeFlag = true;
                }
            }

            // Update names in persistent store
            if (changeFlag) {
                prefsWriter.putString(key, TextUtils.join(",", cookies.keySet()));
            }
        }

        prefsWriter.apply();
    }

    @Override
    public void add(@NonNull HttpUrl httpUrl, @NonNull Cookie cookie) {
        if (omitNonPersistentCookies && !cookie.persistent()) {
            return;
        }

        String name = this.cookieName(cookie);
        String hostKey = this.hostName(httpUrl);

        // Save cookie into local store, or remove if expired
        if (!this.cookies.containsKey(hostKey)) {
            this.cookies.put(hostKey, new ConcurrentHashMap<>());
        }
        Objects.requireNonNull(cookies.get(hostKey)).put(name, cookie);

        // Save cookie into persistent store
        SharedPreferences.Editor prefsWriter = cookiePrefs.edit();
        // 保存httpUrl对应的全部cookie的name
        prefsWriter.putString(hostKey, TextUtils.join(",", Objects.requireNonNull(cookies.get(hostKey)).keySet()));
        // 保存cookie
        prefsWriter.putString(COOKIE_NAME_PREFIX + name, encodeCookie(new SerializableCookie(cookie)));
        prefsWriter.apply();
    }

    @Override
    public void add(@NonNull HttpUrl httpUrl, List<Cookie> cookies) {
        for (Cookie cookie : cookies) {
            if (isCookieExpired(cookie)) {
                continue;
            }
            this.add(httpUrl, cookie);
        }
    }

    @NonNull
    @Override
    public List<Cookie> get(@NonNull HttpUrl httpUrl) {
        return this.get(this.hostName(httpUrl));
    }

    @NonNull
    @Override
    public List<Cookie> getCookies() {
        ArrayList<Cookie> result = new ArrayList<>();
        for (String hostKey : this.cookies.keySet()) {
            result.addAll(this.get(hostKey));
        }
        return result;
    }

    /**
     * 获取cookie集合
     */
    private List<Cookie> get(String hostKey) {
        ArrayList<Cookie> result = new ArrayList<>();

        if (this.cookies.containsKey(hostKey)) {
            Collection<Cookie> cookies = Objects.requireNonNull(this.cookies.get(hostKey)).values();
            for (Cookie cookie : cookies) {
                if (isCookieExpired(cookie)) {
                    this.remove(hostKey, cookie);
                } else {
                    result.add(cookie);
                }
            }
        }
        return result;
    }

    @Override
    public boolean remove(@NonNull HttpUrl httpUrl, @NonNull Cookie cookie) {
        return this.remove(this.hostName(httpUrl), cookie);
    }

    /**
     * 从缓存中移除cookie
     */
    private boolean remove(String hostKey, Cookie cookie) {
        String name = this.cookieName(cookie);
        if (this.cookies.containsKey(hostKey) && Objects.requireNonNull(this.cookies.get(hostKey)).containsKey(name)) {
            // 从内存中移除httpUrl对应的cookie
            Objects.requireNonNull(this.cookies.get(hostKey)).remove(name);

            SharedPreferences.Editor prefsWriter = cookiePrefs.edit();

            // 从本地缓存中移出对应cookie
            prefsWriter.remove(COOKIE_NAME_PREFIX + name);

            // 保存httpUrl对应的全部cookie的name
            prefsWriter.putString(hostKey, TextUtils.join(",", Objects.requireNonNull(this.cookies.get(hostKey)).keySet()));

            prefsWriter.apply();
            return true;
        }
        return false;
    }

    @Override
    public boolean removeAll() {
        SharedPreferences.Editor prefsWriter = cookiePrefs.edit();
        prefsWriter.clear();
        prefsWriter.apply();
        this.cookies.clear();
        return true;
    }

    public void setOmitNonPersistentCookies(boolean omitNonPersistentCookies) {
        this.omitNonPersistentCookies = omitNonPersistentCookies;
    }

    /**
     * 判断cookie是否失效
     */
    private boolean isCookieExpired(Cookie cookie) {
        return cookie.expiresAt() < System.currentTimeMillis();
    }

    private String hostName(HttpUrl httpUrl) {
        return httpUrl.host().startsWith(HOST_NAME_PREFIX) ? httpUrl.host() : HOST_NAME_PREFIX + httpUrl.host();
    }

    private String cookieName(Cookie cookie) {
        return cookie == null ? null : cookie.name() + cookie.domain();
    }

    protected String encodeCookie(SerializableCookie cookie) {
        if (cookie == null) return null;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(os);
            outputStream.writeObject(cookie);
        } catch (IOException e) {
            Log.d(LOG_TAG, "IOException in encodeCookie", e);
            return null;
        }

        return byteArrayToHexString(os.toByteArray());
    }

    protected Cookie decodeCookie(String cookieString) {
        byte[] bytes = hexStringToByteArray(cookieString);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        Cookie cookie = null;
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
            cookie = ((SerializableCookie) objectInputStream.readObject()).getCookie();
        } catch (IOException e) {
            Log.d(LOG_TAG, "IOException in decodeCookie", e);
        } catch (ClassNotFoundException e) {
            Log.d(LOG_TAG, "ClassNotFoundException in decodeCookie", e);
        }
        return cookie;
    }

    protected String byteArrayToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte element : bytes) {
            int v = element & 0xff;
            if (v < 16) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(v));
        }
        return sb.toString().toUpperCase(Locale.US);
    }

    protected byte[] hexStringToByteArray(String hexString) {
        int len = hexString.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4) + Character.digit(hexString.charAt(i + 1), 16));
        }
        return data;
    }
}