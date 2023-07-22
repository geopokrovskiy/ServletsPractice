package com.geopokrovskiy;

import com.geopokrovskiy.util.HibernateUtil;
import org.hibernate.Session;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        String[] strings = "/ServletsPractice/v1/users".split("/");
        System.out.println(Arrays.toString(strings));
    }
}