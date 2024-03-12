package it.unipi.lsmd.gastronomate.service.interfaces;

import it.unipi.lsmd.gastronomate.dto.LoggedUserDTO;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

public interface CookieService {

    LoggedUserDTO getCookie(String name) throws Exception;

    void setCookie(String name, List<String> values, Integer expiry, String path, HttpServletResponse response);

    void deleteCookie(String name, HttpServletResponse response);

}
