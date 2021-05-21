package com.example.caloriecare.DBrequest;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class getUserDataRequest extends StringRequest {

    final static private String URL = "http://118.67.135.180/CalorieCare/getMyPage.php";
    private Map<String, String> map;

    public getUserDataRequest(String userID, Response.Listener<String> listener) {
        super(Method.POST, URL, listener, null);

        map = new HashMap<>();
        map.put("userID", userID);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return map;
    }
}