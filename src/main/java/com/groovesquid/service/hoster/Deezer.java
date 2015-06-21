package com.groovesquid.service.hoster;

import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.groovesquid.model.Track;
import com.groovesquid.util.Utils;
import org.apache.http.*;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.bouncycastle.crypto.BufferedBlockCipher;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.generators.OpenSSLPBEParametersGenerator;
import org.bouncycastle.crypto.modes.CBCBlockCipher;
import org.bouncycastle.crypto.paddings.PaddedBufferedBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.String.format;

public class Deezer extends Hoster {

    public Deezer() {
        setName("Deezer");
    }

    private static final Pattern urlPattern = Pattern.compile(
            "(?:^|[\\W])((ht|f)tp(s?):\\/\\/|www\\.)"
                    + "(([\\w\\-]+\\.){1,}?([\\w\\-.~]+\\/?)*"
                    + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]\\*$~@!:/{};']*)",
            Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
    byte[] blowfishKey = new byte[16];

    public String getDownloadUrl(Track track) {
        String query = "";
        if (track.getSong().getArtists().size() <= 2) {
            query += track.getSong().getArtistNames().replaceAll(",", "");
        } else {
            query += track.getSong().getArtists().get(0);
        }
        query += " " + track.getSong().getName();

        String searchResponse = null;
        try {
            searchResponse = get("http://api.deezer.com/search?q=" + URLEncoder.encode(query, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (searchResponse != null) {
            try {
                JsonArray data = JsonObject.readFrom(searchResponse).get("data").asArray();
                if (data.isEmpty()) {
                    return null;
                }
                Long trackId = data.get(0).asObject().get("id").asLong();
                String trackResponse = get("https://api.deezer.com/track/" + trackId + "?output=json&strict=on");
                JsonObject trackJson = JsonObject.readFrom(trackResponse);

                JsonObject jsonObject = new JsonObject().add("id", trackJson.get("id").asLong()).add("title", trackJson.get("title").asString()).add("artist", trackJson.get("artist").asObject().get("name").asString()).add("format", 3);
                String[] previewSplit = trackJson.get("preview").asString().split("/");
                String md5 = previewSplit[previewSplit.length - 1].split("-")[0];
                String enc = encryptAes(jsonObject.toString(), md5);
                String url = "https://cdn-proxy-{0}.rezeed.cc/stream/1/{1}.mp3".replace("{0}", md5.substring(0, 1)).replace("{1}", md5 + enc);
                String api = "https://cdn-proxy-{0}.rezeed.cc/api/1/{1}.mp3".replace("{0}", md5.substring(0, 1)).replace("{1}", md5 + enc);

                List<Header> headers = new ArrayList<Header>(Arrays.asList(browserHeaders));
                headers.add(new BasicHeader("Referer", "https://deezer.link/"));
                String rezeedResponse = get(api, headers);
                //System.out.println(rezeedResponse);

                Matcher matcher = urlPattern.matcher(rezeedResponse);
                matcher.find();
                int matchStart = matcher.start(1);
                int matchEnd = matcher.end();
                String mp3Url = rezeedResponse.substring(matchStart, matchEnd);

                Pattern p = Pattern.compile("\\[(.*?)\\]");
                Matcher m = p.matcher(rezeedResponse);
                m.find();
                String[] keyStrSplit = m.group(1).split(",");
                for (int i = 0; i < keyStrSplit.length; i++) {
                    blowfishKey[i] = (byte) Integer.parseInt(keyStrSplit[i]);
                }

                return mp3Url;
            } catch (Exception ignored) {

            }

        }

        return null;
    }

    public void download(Track track, OutputStream outputStream) throws IOException {
        HttpGet httpGet = new HttpGet(track.getDownloadUrl());
        httpGet.setHeaders(browserHeaders);
        HttpResponse httpResponse = httpClient.execute(httpGet);
        HttpEntity httpEntity = httpResponse.getEntity();
        try {
            StatusLine statusLine = httpResponse.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == HttpStatus.SC_OK) {
                track.setTotalBytes(httpEntity.getContentLength());

                int chunkSize = 2048;
                int intervalChunk = 3;
                InputStream inputStream = httpEntity.getContent();

                byte[] chunk = new byte[chunkSize];
                int chunks = (int) Math.ceil(httpEntity.getContentLength() / chunkSize);
                int read;
                int i = 0;
                while ((read = inputStream.read(chunk, 0, chunkSize)) != -1) {
                    if (read < chunkSize && (i < chunks - 1)) {
                        ByteBuffer buffer = ByteBuffer.allocate(chunkSize);
                        buffer.put(ByteBuffer.wrap(chunk, 0, read));
                        while (buffer.hasRemaining()) {
                            byte[] temp = new byte[buffer.remaining()];
                            int tempRead = inputStream.read(temp, 0, buffer.remaining());
                            read += tempRead;
                            buffer.put(temp, 0, tempRead);
                        }
                        chunk = buffer.array();
                    }
                    if (i % intervalChunk == 0) {
                        chunk = decryptBlowfish(chunk, blowfishKey);
                    }

                    outputStream.write(chunk, 0, read);
                    i++;
                }

                // need to close immediately otherwise we cannot write ID tags
                outputStream.close();
                outputStream = null;
                // write ID tags
                track.getStore().writeTrackInfo(track);
            } else {
                throw new HttpResponseException(statusCode, format("%s: %d %s", track.getDownloadUrl(), statusCode, statusLine.getReasonPhrase()));
            }
        } finally {
            try {
                EntityUtils.consume(httpEntity);
            } catch (IOException ignore) {
                // ignored
            }
            Utils.closeQuietly(outputStream, track.getStore().getDescription());
        }
    }

    private byte[] decryptBlowfish(byte[] data, byte[] key) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(key, "Blowfish");
            Cipher cipher = Cipher.getInstance("Blowfish/CBC/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(new byte[]{0, 1, 2, 3, 4, 5, 6, 7}));
            return cipher.doFinal(data);
        } catch (Exception ex) {
            log.log(Level.SEVERE, ex.getMessage(), ex);
        }
        return null;
    }

    public String encryptAes(String text, String key) {
        byte[] encryptedConfigData = null;
        try {
            AESEngine blockCipher = new AESEngine();
            blockCipher.reset();
            CBCBlockCipher cbcCipher = new CBCBlockCipher(blockCipher);
            BufferedBlockCipher bbc = new PaddedBufferedBlockCipher(cbcCipher);

            byte[] salt = new byte[8];
            SecureRandom secure = new SecureRandom();
            secure.nextBytes(salt);

            //intialising in the encryption mode with Key and IV
            bbc.init(true, getKeyParamWithIv(key, salt));
            byte[] encryptedData = new byte[bbc.getOutputSize(text.getBytes().length)];

            //process array of bytes
            int noOfBytes = bbc.processBytes(text.getBytes(), 0, text.getBytes().length, encryptedData, 0);

            //process the last block in the buffer
            bbc.doFinal(encryptedData, noOfBytes);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            //writing encrypted data along with the salt in the format readable by open ssl api
            bos.write("Salted__".getBytes());
            bos.write(salt);
            bos.write(encryptedData);
            encryptedConfigData = bos.toByteArray();
            bos.close();
        } catch (Exception ex) {
            log.log(Level.SEVERE, ex.getMessage(), ex);
        }

        return DatatypeConverter.printHexBinary(encryptedConfigData).toLowerCase();
    }

    private ParametersWithIV getKeyParamWithIv(String keyphrase, byte[] salt) {
        int iterationCount = 1;
        //creating generator for PBE derived keys and ivs as used by open ssl
        PBEParametersGenerator generator = new OpenSSLPBEParametersGenerator();

        //intialse the PBE generator with password, salt and iteration count
        generator.init(PBEParametersGenerator.PKCS5PasswordToBytes(keyphrase.toCharArray()), salt, iterationCount);

        //Generate a key with initialisation vector parameter derived from the password, salt and iteration count
        ParametersWithIV paramWithIv = (ParametersWithIV) generator.generateDerivedParameters(256, 128);
        KeyParameter keyParam = (KeyParameter) paramWithIv.getParameters();

        /*byte[] key = keyParam.getKey();
        StringBuilder sbKey = new StringBuilder("");
        for (byte aKey : key) {
            sbKey.append(Integer.toHexString(0xff & aKey));
        }

        byte[] ivBytes = paramWithIv.getIV();
        StringBuilder sbIV = new StringBuilder("");
        for (byte ivByte : ivBytes) {
            sbIV.append(Integer.toHexString(0xff & ivByte));
        }*/

        return paramWithIv;
    }


}
