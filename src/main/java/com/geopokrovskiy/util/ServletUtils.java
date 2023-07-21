package com.geopokrovskiy.util;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class ServletUtils {
    /**
     * sets the utf-8 encoding for req and resp, sets application/json content-type
     * @param req
     * @param resp
     * @throws IOException
     */
    public static void setServletParams(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("utf-8");
        resp.setCharacterEncoding("utf-8");
        resp.setContentType("application/json");
    }

    /**
     * returns 0 if there is no id in URI, a positive value if the id is correct, -1 if the id is incorrect
     * @param req
     * @param entityName
     * @return
     */
    public static Long processURI(HttpServletRequest req, String entityName) {
        String[] parsedURI = req.getRequestURI().split("/");
        int sizeURI = parsedURI.length;
        if (parsedURI[sizeURI - 1].equals(entityName)) return 0L;
        else {
            try {
                return Long.parseLong(parsedURI[sizeURI - 1]);
            } catch (NumberFormatException e) {
                return -1L;
            }
        }
    }
}
