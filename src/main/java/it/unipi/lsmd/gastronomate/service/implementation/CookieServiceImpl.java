package it.unipi.lsmd.gastronomate.service.implementation;

import it.unipi.lsmd.gastronomate.dto.LoggedUserDTO;
import it.unipi.lsmd.gastronomate.model.enums.UserTypeEnum;
import it.unipi.lsmd.gastronomate.service.ServiceLocator;
import it.unipi.lsmd.gastronomate.service.interfaces.CookieService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.logging.Logger;

public class CookieServiceImpl implements CookieService {

    private static final String SECRET_KEY = "MySecr3tKey@192BitL3ngth"; //24 chars -> 192 bit
    private static final Logger applicationLogger = ServiceLocator.getApplicationLogger();

    @Override
    public LoggedUserDTO getCookie(String value) throws Exception{
        try {
            String val = decrypt(value);
            LoggedUserDTO loggedUserDTO = new LoggedUserDTO();

            List<String> list = splitCookie(val);

            if(list.size() == 1){

                loggedUserDTO.setUsername(list.get(0));
                return loggedUserDTO;

            } else{
                loggedUserDTO.setUsername(list.get(0));

                if(list.get(1).equals(UserTypeEnum.ADMIN.toString())){
                    loggedUserDTO.setUserType(UserTypeEnum.ADMIN);
                }

                else{
                    loggedUserDTO.setProfilePicture(list.get(1));
                }

                return loggedUserDTO;
            }

        } catch (Exception e) {
            applicationLogger.severe("Error while decrypting cookie");
          throw e;
        }
    }

    public List<String> splitCookie(String cookie){

        if (cookie.contains(":")) {
            String[] parts = cookie.split(":");
            return List.of(parts[0], parts[1]);

        } else {
            return List.of(cookie);
        }
    }

    public String buildCookie(String username, String val){

        if(val == null || val.isEmpty()){
            return username;
        }

        return username + ":" + val;
    }

    @Override
    public void setCookie(String name, List<String> values, Integer expiry, String path, HttpServletResponse response){
        try {

            String value;

            if(values.size() == 2 ){

                 value = buildCookie(values.get(0), values.get(1));
            }

            else{
                value = buildCookie(values.get(0), null);
            }


            String encryptedValue = encrypt(value);

            Cookie cookie = new Cookie(name, encryptedValue);
            if (expiry != null)
                cookie.setMaxAge(expiry); // in seconds (1 week = 604800 seconds)

            cookie.setPath(path);
            response.addCookie(cookie);

        } catch (Exception e) {
           applicationLogger.severe("Error while encrypting cookie");
        }
    }

    @Override
    public void deleteCookie(String name, HttpServletResponse response) {
        Cookie cookie = new Cookie(name, "");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    //Encryption and decryption methods

    /*
    *
    * Cipher is a Java class for encrypting and decrypting data. AES (Advanced Encryption Standard) is a widely used cryptographic standard.
    * AES operates with symmetric keys of different lengths (128, 192, 256 bits) and is known for its security. In the Cipher class,
    * it is used to implement encryption and decryption operations based on AES.
    *
    */
    private static String encrypt(String value) throws Exception {
        SecretKey key = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedBytes = cipher.doFinal(value.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    private static String decrypt(String encryptedValue) throws Exception {
        SecretKey key = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedValue);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes);
    }
}