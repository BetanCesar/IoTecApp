package brandon.example.com.iotecapp;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import brandon.example.com.iotecapp.AcercaDe.ImagenDos;
import brandon.example.com.iotecapp.AcercaDe.ImagenTres;
import brandon.example.com.iotecapp.AcercaDe.ImagenUno;

public class AcercaDeActivity extends AppCompatActivity {

    private ImageView circulo1, circulo2, circulo3;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acerca_de);

        circulo1 = (ImageView)findViewById(R.id.circulo1);
        circulo2 = (ImageView)findViewById(R.id.circulo2);
        circulo3 = (ImageView)findViewById(R.id.circulo3);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

       // FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        //fab.setOnClickListener(new View.OnClickListener() {
          //  @Override
            //public void onClick(View view) {
              //  Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
              //          .setAction("Action", null).show();
            //}
        //});

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_acerca_de, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    ImagenUno uno = new ImagenUno();

                    circulo1.setImageDrawable(getDrawable(R.drawable.circulolleno));
                    circulo2.setImageDrawable(getDrawable(R.drawable.ic_brightness_1_black_24dp));
                    circulo3.setImageDrawable(getDrawable(R.drawable.ic_brightness_1_black_24dp));
                    //circulo2.setImageResource(R.drawable.ic_brightness_1_black_24dp);
                    //circulo3.setImageResource(R.drawable.ic_brightness_1_black_24dp);
                    return uno;
                case 1:
                    ImagenDos dos = new ImagenDos();
                    circulo2.setImageDrawable(getDrawable(R.drawable.circulolleno));
                    circulo1.setImageDrawable(getDrawable(R.drawable.ic_brightness_1_black_24dp));
                    circulo3.setImageDrawable(getDrawable(R.drawable.ic_brightness_1_black_24dp));

                   // circulo2.setImageResource(R.drawable.circulolleno);
                    //circulo1.setImageResource(R.drawable.ic_brightness_1_black_24dp);
                    //circulo3.setImageResource(R.drawable.ic_brightness_1_black_24dp);
                    return dos;
                case 2:
                    ImagenTres tres = new ImagenTres();
                    circulo3.setImageDrawable(getDrawable(R.drawable.circulolleno));
                    circulo1.setImageDrawable(getDrawable(R.drawable.ic_brightness_1_black_24dp));
                    circulo2.setImageDrawable(getDrawable(R.drawable.ic_brightness_1_black_24dp));

                   // circulo3.setImageResource(R.drawable.circulolleno);
                    //circulo1.setImageResource(R.drawable.ic_brightness_1_black_24dp);
                    //circulo2.setImageResource(R.drawable.ic_brightness_1_black_24dp);
                    return tres;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }
    }
}
