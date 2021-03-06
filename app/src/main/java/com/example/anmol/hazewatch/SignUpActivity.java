package com.example.anmol.hazewatch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.R;
import com.example.anmol.hazewatch.Communication.Communication;
import com.example.anmol.hazewatch.Communication.DBConnect;
import com.example.anmol.hazewatch.Communication.Request;
import com.example.anmol.hazewatch.JSONClasses.UserSignUpModel;
import com.example.jaskirat.hazewatch.SensorActivity;
import com.google.gson.Gson;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity implements Communication {

    private EditText mName;
    private EditText mEmail;
    private EditText mPhone;
    private EditText mPassword;
    private EditText mConfirmPassword;
    private EditText mHeight;
    private EditText mWeight;
    private EditText mAge;
    private EditText mGender;

    private static final String LOGIN = "isLogin";
    private static final String PREFERENCE_NAME = "LoginActivity";
    private SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mName = (EditText)findViewById(R.id.name);
        mEmail = (EditText)findViewById(R.id.email);
        mPhone = (EditText)findViewById(R.id.phone);
        mPassword = (EditText)findViewById(R.id.password);
        mConfirmPassword = (EditText)findViewById(R.id.password2);
        mHeight = (EditText) findViewById(R.id.height);
        mWeight = (EditText) findViewById(R.id.weight);
        mAge = (EditText) findViewById(R.id.age);
        mGender = (EditText) findViewById(R.id.gender);

        mPrefs = getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
    }

    public void processForm(View v){
        boolean isConnectedToInternet = checkInternetConnection();
        if(!isConnectedToInternet){
            Toast.makeText(this,"Check internet connection",Toast.LENGTH_SHORT).show();
        }
        else {
            String name = mName.getText().toString();
            String email = mEmail.getText().toString();
            String phone = mPhone.getText().toString();
            String password = mPassword.getText().toString();
            String confirmPassword = mConfirmPassword.getText().toString();
            String height = mHeight.getText().toString();
            String weight = mWeight.getText().toString();
            String age = mAge.getText().toString();
            String gender = mGender.getText().toString();

            if (validateName(name)) {
                if (validateEmail(email)) {
                    if (validatePhoneNumber(phone)) {
                        if (validatePassword(password)) {
                            if (passwordsMatch(password, confirmPassword)) {

                                UserSignUpModel userSignUp = new UserSignUpModel(name, email, phone, password, height, weight, age, gender);
                                signUpUser(userSignUp);

                            } else {
                                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                                mConfirmPassword.setText("");
                                mConfirmPassword.requestFocus();
                            }
                        } else {
                            Toast.makeText(this, "Password must be atleast 8 characters long", Toast.LENGTH_SHORT).show();
                            mPassword.requestFocus();
                        }
                    } else {
                        Toast.makeText(this, "Please enter a valid Phone Number", Toast.LENGTH_SHORT).show();
                        mPhone.requestFocus();
                    }
                } else {
                    Toast.makeText(this, "Please enter a valid Email Id", Toast.LENGTH_SHORT).show();
                    mEmail.requestFocus();
                }
            } else {
                Toast.makeText(this, "Please enter a valid name", Toast.LENGTH_SHORT).show();
                mName.requestFocus();
            }
        }
    }

    private void signUpUser(UserSignUpModel userSignUp) {
        //UserSignUpModel userSignUp = new UserSignUpModel(name, email, phone, password);
        Request request = new Request("userSignUp");
        Gson gson = new Gson();
        request.setRequest(gson.toJson(userSignUp));
        String requestObject = gson.toJson(request);
        new DBConnect(this, requestObject).execute();
    }

    private boolean passwordsMatch(String password, String confirmPassword) {
        if(password.equals(confirmPassword))
            return true;
        return false;
    }

    private boolean validatePassword(String password) {
        if(password.length()>=8)
            return true;
        return false;
    }

    private boolean validateEmail(String email) {
        Pattern VALID_EMAIL_ADDRESS_REGEX =
                Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX .matcher(email);
        return matcher.find();
    }

    private boolean validateName(String name) {
        for (int i = 0; i < name.length(); i++)
            if (((int) name.charAt(i) >= 65 && (int) name.charAt(i) <= 90) || ((int) name.charAt(i) >= 97 && (int) name.charAt(i) <= 122) || (name.charAt(i) == ' ' && i>=3)){
                return true;
            }
        return false;
    }

    public boolean validatePhoneNumber(String phone) {
        try{
            String value = "";
            for(int i = 0 ; i<10 ; i++) {
                value = phone.substring(i,i+1);
                Integer.parseInt(value);
            }
            if(phone.length() == 10 && (phone.charAt(0) == '7' || phone.charAt(0) == '8' || phone.charAt(0) == '9'))
                return true;
            else
                return false;
        }
        catch(Exception e)
        {
            return false;
        }
    }

    @Override
    public void onCompletion(String response) {
        //Toast.makeText(this,response,Toast.LENGTH_SHORT).show();
        Gson gson = new Gson();
        UserSignUpModel userSignUp = gson.fromJson(response, UserSignUpModel.class);
        if(userSignUp.getAlreadyExists().equals("1")){
            Toast.makeText(this,"Already exists",Toast.LENGTH_SHORT).show();
        }
        else if(userSignUp.getSignUp().equals("1")){
            Toast.makeText(this,"Successfully Signed Up",Toast.LENGTH_SHORT).show();
            mPrefs.edit().putBoolean(LOGIN, true).commit();
            mPrefs.edit().putString("Name", userSignUp.getName()).commit();
            mPrefs.edit().putString("Phone", userSignUp.getPhone()).commit();
            Intent readings = new Intent(this, SensorActivity.class);
            readings.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(readings);
            finish();
        }
        else{
            Toast.makeText(this,"Some error",Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkInternetConnection() {
        ConnectivityManager connec = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo ni = connec.getActiveNetworkInfo();

        if(ni==null){
            return false;
        }
        else{
            if(ni.isConnected()){
                return true;
            }
            else{
                return false;
            }
        }
    }
}
