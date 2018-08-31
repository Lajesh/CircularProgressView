package com.lajesh.circularprogressview;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    ImageProgressView progressView;
    ImageProgressView progressView2;
    ImageProgressView progressView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressView = (ImageProgressView) findViewById(R.id.progressImageView);
        progressView.setIconUrl("http://2.bp.blogspot.com/-9J-2HjcDML8/VFcY0LSQkGI/AAAAAAAAArA/LetlZU8aCrY/s1600/stylish-girl-with-unique-hair-style-dp-for-facebook-profile-picture.jpg");
        progressView.setValue(85.0f);

        progressView2 = (ImageProgressView) findViewById(R.id.progressImageView2);
        progressView2.setIconUrl("http://2.bp.blogspot.com/-9J-2HjcDML8/VFcY0LSQkGI/AAAAAAAAArA/LetlZU8aCrY/s1600/stylish-girl-with-unique-hair-style-dp-for-facebook-profile-picture.jpg");
        progressView2.setValue(20.0f);


        progressView3 = (ImageProgressView) findViewById(R.id.progressImageView3);
        progressView3.setIconUrl("http://2.bp.blogspot.com/-9J-2HjcDML8/VFcY0LSQkGI/AAAAAAAAArA/LetlZU8aCrY/s1600/stylish-girl-with-unique-hair-style-dp-for-facebook-profile-picture.jpg");
        progressView3.setValue(50.0f);
    }
}
