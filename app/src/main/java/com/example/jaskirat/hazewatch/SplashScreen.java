package com.example.jaskirat.hazewatch;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.R;
import com.example.anmol.hazewatch.MainActivity;

/**
 * Created by jaskirat singh on 19-10-2016.
 */

    public class SplashScreen extends Activity {
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            Window window = getWindow();
            window.setFormat(PixelFormat.RGBA_8888);
        }
        /** Called when the activity is first created. */
        Thread splashTread;
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            overridePendingTransition(R.transition.fadein, R.transition.fadeout);
            setContentView(R.layout.activity_splashscreen);
            StartAnimations();
        }
        private void StartAnimations() {
            Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
            anim.reset();
            LinearLayout l=(LinearLayout) findViewById(R.id.lin_lay);
            l.clearAnimation();
            l.startAnimation(anim);

           // anim = AnimationUtils.loadAnimation(this, R.transition.lefttoright);
            anim.reset();
            ImageView iv = (ImageView) findViewById(R.id.splash);
            iv.clearAnimation();
            iv.startAnimation(anim);

            splashTread = new Thread() {
                @Override
                public void run() {
                    try {
                        int waited = 0;
                        // Splash screen pause time
                        while (waited < 3500) {
                            sleep(100);
                            waited += 100;
                        }
                        Intent intent = new Intent(SplashScreen.this,
                                MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        startActivity(intent);
                        SplashScreen.this.finish();
                    } catch (InterruptedException e) {
                        // do nothing
                    } finally {
                        SplashScreen.this.finish();
                    }

                }
            };
            splashTread.start();

        }

    }

