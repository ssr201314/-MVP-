package com.lind.mvp_forssr;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.lind.mvp_forssr.http.AlbatrossService;
import com.lind.mvp_forssr.http.ServiceGenerator;



public class BaseActivity extends AppCompatActivity {
    protected AlbatrossService service ;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

service= ServiceGenerator.createService(AlbatrossService.class, this.getApplicationContext());


}}