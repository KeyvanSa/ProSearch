package ir.ebookline.search;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends Activity
{
    EditText etUserEntry ;
    TextView tvmainText , tvCounter ;
    ArrayList<Integer> Startpos,Endpos;
    SpannableStringBuilder sb ;
    int Position = 0;
    LinearLayout Layout ;
    ScrollView sc ;
    String BackgroundColorSpan = "#FF03DAC5" ;
    String ForegroundColorSpan = "#F6073F";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initalization Views
        init();

        // Read Sample Text From Raw Folder
        ReadText();


        // Text Watcher For Edittext
        etUserEntry.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(
                    CharSequence arg0, int arg1, int arg2,  int arg3) {}
            @Override
            public void afterTextChanged(Editable arg0) {}
            @Override
            public void onTextChanged(CharSequence cs,int arg1, int arg2, int arg3)
            {
                if(etUserEntry.getText().toString().length()>0)
                {
                    Search();
                    // If the search contains a result, it changes the first word found.
                    if(Startpos.size()>0)
                    {
                        sb.setSpan(new BackgroundColorSpan(Color.parseColor(BackgroundColorSpan)),Startpos.get(Position),Endpos.get(Position),Spannable.SPAN_INCLUSIVE_INCLUSIVE);
                        tvCounter.setText((Position+1)+"/"+Startpos.size());
                        tvmainText.setText(sb);
                        Layout.setVisibility(View.VISIBLE);

                        int line = tvmainText.getLayout().getLineForOffset(Startpos.get(Position));
                        int pos = tvmainText.getLayout().getLineBaseline(line);
                        sc.smoothScrollTo(0, pos - (sc.getHeight()/2));
                    }

                }else{
                    Startpos=null;
                    Endpos=null;
                    Position=0;
                    tvCounter.setText("");
                    Layout.setVisibility(View.GONE);
                    ReadText();
                    sc.smoothScrollTo(0,0);
                }
            }
        });
    }

    // Specify the previously found word by changing the text field
    public void up (View v)
    {
        if(Position>0 && Startpos.size()>0)
        {
            Search();
            Position--;
            sb.setSpan(new BackgroundColorSpan(Color.parseColor(BackgroundColorSpan)),Startpos.get(Position),Endpos.get(Position),Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            tvCounter.setText((Position+1)+"/"+Startpos.size());
            tvmainText.setText(sb);

            int line = tvmainText.getLayout().getLineForOffset(Startpos.get(Position));
            int pos = tvmainText.getLayout().getLineBaseline(line);
            sc.smoothScrollTo(0, pos - (sc.getHeight()/2));
        }
    }

    // Specify the next word found in the text by changing the background
    public void down (View v)
    {
        if(Position<(Endpos.size()-1)&&Endpos.size()>0)
        {
            Search();
            Position++;
            sb.setSpan(new BackgroundColorSpan(Color.parseColor(BackgroundColorSpan)),Startpos.get(Position),Endpos.get(Position),Spannable.SPAN_INCLUSIVE_INCLUSIVE);
            tvCounter.setText((Position+1)+"/"+Startpos.size());
            tvmainText.setText(sb);

            int line = tvmainText.getLayout().getLineForOffset(Startpos.get(Position));
            int pos = tvmainText.getLayout().getLineBaseline(line);
            sc.smoothScrollTo(0, pos - (sc.getHeight()/2));
        }
    }


    // Search for all phrases similar to the word entered.
    public void Search ()
    {
        String FullText = tvmainText.getText().toString();
        String FindText = etUserEntry.getText().toString().trim();

        Startpos=new ArrayList<Integer>();
        Endpos=new ArrayList<Integer>();

        try{

            if (FindText.length()>0)
            {
                sb = new SpannableStringBuilder(FullText);
                Pattern p = Pattern.compile(FindText, Pattern.CASE_INSENSITIVE);
                Matcher m = p.matcher(FullText);
                while (m.find()){
                    Startpos.add(m.start());
                    Endpos.add(m.end());

                    sb.setSpan(new ForegroundColorSpan(Color.parseColor(ForegroundColorSpan)),m.start(),m.end(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

                }
                tvmainText.setText(sb);
            }
        } catch(Exception e) {
            new AlertDialog.Builder(this).setMessage(e.toString()).show();
        }

    }


    private void ReadText()
    {
        try {
            InputStream is = this.getResources().openRawResource(R.raw.text);
            byte[] buffer = new byte[is.available()];
            while (is.read(buffer) != -1);
            String jsontext = new String(buffer);
            tvmainText.setText(jsontext);
        } catch (Exception e) {
            new AlertDialog.Builder(this).setMessage(e.toString()).show();
        }
    }

    public void init ()
    {
        etUserEntry=(EditText) findViewById(R.id.EditText);
        tvmainText=(TextView) findViewById(R.id.MainText);
        tvCounter=(TextView) findViewById(R.id.TextView);
        Layout=(LinearLayout) findViewById(R.id.Layout);
        sc=(ScrollView) findViewById(R.id.ScrollView);
    }


}