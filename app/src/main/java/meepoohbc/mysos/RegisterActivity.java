package meepoohbc.mysos;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class RegisterActivity extends AppCompatActivity
{

        //ประกาศตัวแปร
    private ImageView imageView;
    private EditText nameEditText, userEditText, passEditText;
    private Button button;
    private String nameString, userString, passString;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Initail View ผูกไอดีกับคอรโทลต่างๆ
        initailView();

        //Back Controller ปุ่มปิดหน้า
        backController();

        //Register Controller
        registerController();


    }   //Main method

    private void registerController() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get Value From Edit Text ฉันจะดึงค่าจาก Edit Text มาเป็น String
                nameString = nameEditText.getText().toString().trim();
                userString = userEditText.getText().toString().trim();
                passString = passEditText.getText().toString().trim();

                //Check Space
                if (nameString.equals("") ||userString.equals(("")) ||passString.equals(""))
                {
                    //Have Space มีช่องว่าง
                    MyAlert myAlert = new MyAlert(RegisterActivity.this);
                    myAlert.MyDialog("มีช่องว่าง","กรุณากรอกข้อมูลให้ครบถ้วน");

                }
                else
                {
                      //No Space ไม่มีชองว่าง
                }



            }
        });
    }

    private void backController() {
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();;
            }
        });
    }

    private void initailView() {
        imageView = (ImageView) findViewById(R.id.btnBack);
        nameEditText = (EditText) findViewById(R.id.edtName);
        userEditText = (EditText) findViewById(R.id.edtUser);
        passEditText = (EditText) findViewById(R.id.edtPass);
        button = (Button) findViewById(R.id.btnRegister);
    }
}   //Main class
