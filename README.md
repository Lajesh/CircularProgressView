# CircularProgressView

A Circular image with progress indication around. This control can be used for showing user progress in a contest.

# Screenshot

<img src="/screenshots/progressview.png" width="346" height="615" alt="Progress"/>

# How to use ?

a) Add control in the xml layout

```<com.lajesh.circularprogressview.ImageProgressView
            android:id="@+id/progressImageView"
            android:layout_width="80dp"
            android:layout_height="80dp"
            android:layout_gravity="center"
            android:layout_marginBottom="8dp"
            android:layout_marginEnd="8dp"
            android:layout_marginStart="8dp"
            android:layout_marginTop="8dp"
            android:layout_weight="1.3"
            android:src="@mipmap/ic_launcher_round"
            app:border_color="@android:color/transparent"
            app:border_width="5dp"
            app:centercircle_diammterer=".97"
            app:draw_anticlockwise="false"
            app:progress_color="#fc550e"
            app:progress_startAngle="-90"
            app:progressvalue="0" />```
        
            
 You can configure almost all the properties from xml, like the progress ring color, the default profile image,
 initial progress value, progress border width, etc.
 
 b) Set the imageurl and progress in code.
 
 ```progressView = (ImageProgressView) findViewById(R.id.progressImageView);
        progressView.setIconUrl("");
        progressView.setValue(85.0f);
        ```
