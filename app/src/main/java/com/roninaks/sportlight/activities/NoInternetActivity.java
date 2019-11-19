package com.roninaks.sportlight.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.roninaks.sportlight.R;
import com.roninaks.sportlight.helpers.EmailHelper;
import com.roninaks.sportlight.helpers.SqlHelper;
import com.roninaks.sportlight.helpers.StringHelper;


public class NoInternetActivity extends AppCompatActivity {
    public static SqlHelper sqlHelper;
    Button btnretry;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_no_internet);
            btnretry = (Button) findViewById(R.id.btn_Retry);
            btnretry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sqlHelper.executeUrl(sqlHelper.isShowLoading());
                    finish();
                }
            });
        }catch (Exception e){
            EmailHelper emailHelper = new EmailHelper(NoInternetActivity.this, EmailHelper.TECH_SUPPORT, "Error: NoInternetActivity", e.getMessage() + "\n" + StringHelper.convertStackTrace(e));
            emailHelper.sendEmail();
        }
    }
}
