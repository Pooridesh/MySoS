package meepoohbc.mysos;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    //Explicit การประกาศตัวแปร
    private EditText userEditText, passEditText;
    private TextView textView;
    private Button button;
    private String userString, passString;
    private MyAlert myAlert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Initital View
        initialView();

        //TextView Controller จะทำให้ Textview กด คลิ๊กได้
        textViewController();

        //button controller
        buttonController();


    }//Method Main กกกำห

    private void buttonController() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get value from edit text
                userString = userEditText.getText().toString().trim();
                passString = passEditText.getText().toString();

                //check space
                if (userString.length() == 0 || passString.length() == 0) {
                    //have space มีช่องว่าง
                   myAlert.MyDialog(getResources().getString(R.string.titleHaveSpace),
                           getResources().getString(R.string.MessageHaveSpace));
                } else {
                    //no space ไม่มีช่องว่าง
                    checkUserAndPass();
                }
            }
        });
    }

    private void checkUserAndPass() {

        try {

            String urlPHP = "http://androidthai.in.th/siam/getAllDataPooh.php";
            GetDataToServer getDataToServer = new GetDataToServer(MainActivity.this);
            getDataToServer.execute(urlPHP);
            String StrJSON = getDataToServer.get();
            Log.d("SiamOne", "JSON >>>" + StrJSON);

            JSONArray jsonArray = new JSONArray(StrJSON);
            boolean b = true;

            String[] strings = new String[]{"id", "Name", "User", "Pass"};

            String[] loginStrings1 = new String[strings.length];
            for (int i =0; i<jsonArray.length();i++) {

                JSONObject jsonObject = jsonArray.getJSONObject(i);

                if (userString.equals(jsonObject.getString("User"))) {


                    b = false;

                    for (int i1=0; i<strings.length;i1++) {
                        loginStrings1[i1] = jsonObject.getString(strings[i1]);
                        Log.d("SiamOne", "loginString[" + i1 + "] >>> " + loginStrings1[i1]);

                    }// for เล็ก

                }// if
            }// for ใหญ่ นะเว้ย
            if (b) {
                myAlert.MyDialog(getResources().getString(R.string.titleUserFalse),
                        getResources().getString(R.string.MessageUserFalse));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void textViewController() {
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //intent to RegisterActivity โค้ดไปลิ้งไปหน้า สทัครสมาชิก
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);

            }
        });
    }

    private void initialView() {
        myAlert = new MyAlert(MainActivity.this);
        userEditText = (EditText) findViewById(R.id.edtUser);
        passEditText = (EditText) findViewById(R.id.edtPass);
        textView = (TextView) findViewById(R.id.txtRegister);
        button = (Button) findViewById(R.id.btnLogin);
    }
}//Main Class นี่คือ คลาสหลัก
