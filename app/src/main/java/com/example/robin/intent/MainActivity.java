package com.example.thoma.intent;

import android.Manifest;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

public class MainActivity extends Activity {
    @Bind(R.id.btnSpeak)
    ImageButton btnStart;
    @Bind(R.id.txtSpeechInput)
    TextView txtOutput;
    @Bind(R.id.btnCall)
    Button btnCall;
    @Bind(R.id.btnContact)
    Button btnContact;
    @Bind(R.id.btnGoogle)
    Button btnGoogle;
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 100;

 


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

       

    }

    @OnClick(R.id.btnSpeak
    )
    public void onClick(View v) {
        if (checkIfSpeechIsActive() == true) {
            startVoiceRecognition();
        }
    }

    @OnClick(R.id.btnGoogle
    )
    public void onClickGoogle(View v) {
        Intent search = new Intent(Intent.ACTION_VIEW);
        search.setData(Uri.parse("http://www.google.be"));
        startActivity(search);

    }

    @OnClick(R.id.btnCall
    )
    public void onClickCall(View v) {
        Intent call = new Intent(Intent.ACTION_CALL);
        call.setData(Uri.parse("tel:111"));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            startActivity(call);
            return;
        }



    }

    @OnClick(R.id.btnContact)
    public void onClickContact(View v) {
        Intent call = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivity(call);

    }


    private boolean checkIfSpeechIsActive() {
        PackageManager pm = getPackageManager();
        List<ResolveInfo> activities = pm.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (activities.size() != 0) {
            return true;
        }
        return false;
    }

    private void startVoiceRecognition() {
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say something");
        try {
            startActivityForResult(i, VOICE_RECOGNITION_REQUEST_CODE);
        } catch (Error e) {
            Toast.makeText(getApplicationContext(), getString(R.string.speech_not_supported), Toast.LENGTH_SHORT).show();

        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            txtOutput.setText(result.get(0));
            if (result.get(0).contains("search")) {
                Intent search = new Intent(Intent.ACTION_WEB_SEARCH);
                search.putExtra(SearchManager.QUERY, result.get(0).replace("search", ""));
                startActivity(search);
            } else if (result.get(0).contains("contact")) {
                Intent call = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                call.putExtra(SearchManager.QUERY, result.get(0).replace("contact", ""));
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    startActivity(call);
                }

            } else if (result.get(0).contains("call")) {

                Intent call = new Intent(Intent.ACTION_CALL);
                call.setData(Uri.parse(result.get(0).replace("call", "tel: ")));
                startActivityForResult(call, 2);

            }
        } else if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            Uri contact = data.getData();
            String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME};

            Cursor cursor = getContentResolver().query(contact, projection,
                    null, null, null);
            cursor.moveToFirst();

            int numberColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            String number = cursor.getString(numberColumnIndex);

            int nameColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            String name = cursor.getString(nameColumnIndex);
        }


    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onStop() {
        super.onStop();

    }
}
