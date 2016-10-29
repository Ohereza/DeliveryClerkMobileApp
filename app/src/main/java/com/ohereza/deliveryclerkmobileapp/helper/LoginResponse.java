package com.ohereza.deliveryclerkmobileapp.helper;

/**
 * Created by rkabagamba on 10/9/2016.
 */

public class LoginResponse {

    public String home_page, message, full_name;

    public LoginResponse(String home_page, String message, String full_name) {
        this.home_page = home_page;
        this.message = message;
        this.full_name = full_name;
    }
}
