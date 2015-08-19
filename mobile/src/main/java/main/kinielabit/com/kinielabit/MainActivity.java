package main.kinielabit.com.kinielabit;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import main.kinielabit.com.kinielabit.main.kinielabit.com.database.DBHelper;
import main.kinielabit.com.kinielabit.main.kinielabit.com.database.tables.Usuario;
import main.kinielabit.com.kinielabit.main.kinielabit.com.game.Game;
import main.kinielabit.com.kinielabit.main.kinielabit.com.kinielabit.beans.UsuarioBeans;
import main.kinielabit.com.kinielabit.main.kinielabit.com.task.GenericTaskFragment;

public class MainActivity extends AppCompatActivity implements GenericTaskFragment.TaskCallBacks {

    private GenericTaskFragment taskFragment;
    private DBHelper dbHelper = null;
    private SQLiteDatabase dataBase = null;

    private final String key = "IS_LOGIN";
    private final String ISSHOWPROGRESS = "isShowProgress";
    private boolean isShowProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.str_signup);

        findViewById(R.id.button_login).setOnClickListener(signUp);


        FragmentManager fm = getSupportFragmentManager();
        taskFragment = (GenericTaskFragment) fm.findFragmentByTag(GenericTaskFragment.TAG_TASK_FRAGMENT);
        if (taskFragment == null) {
            taskFragment = new GenericTaskFragment();
            fm.beginTransaction().add(taskFragment, GenericTaskFragment.TAG_TASK_FRAGMENT).commit();
        }

        if(isLogin()){
            Intent intent = new Intent(this, Game.class);
            startActivity(intent);
            this.finish();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(ISSHOWPROGRESS, isShowProgress);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState.getBoolean(ISSHOWPROGRESS)){
            showProgress(true);
        }
    }

    View.OnClickListener signUp = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (!taskFragment.isRunning()) {
                hideKeyBoard((TextView) findViewById(R.id.username));
                UsuarioBeans usuario = new UsuarioBeans();
                usuario.setUsername(((EditText) findViewById(R.id.username)).getText().toString());
                usuario.setPassword(((EditText) findViewById(R.id.password)).getText().toString());
                usuario.setCelular(((EditText) findViewById(R.id.phone)).getText().toString());

                Gson gson = new Gson();
                taskFragment.setData("http://192.168.50.150:8080/KinielaWS/webresources/usuario/registroMovil", "PUT", gson.toJson(usuario));
                taskFragment.start();
            }
        }
    };


    private boolean isLogin(){
        SharedPreferences sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        boolean isLogin = sharedPreferences.getBoolean(key, false);

        return isLogin;

    }

    private void openDataBase(){
        if (dbHelper == null){
            dbHelper = new DBHelper(this);
            dataBase = dbHelper.getWritableDatabase();

        }
    }

    private boolean saveInDataBase(UsuarioBeans usuarioBeans){
        openDataBase();

        ContentValues cv = new ContentValues();
        cv.put(Usuario.USERNAME, usuarioBeans.getUsername());
        cv.put(Usuario.CELULAR, usuarioBeans.getCelular());
        cv.put(Usuario.ID_USUARIO, usuarioBeans.getIdusuarios());
        Long i = dataBase.insert(Usuario.TAB_NAME,Usuario.NOMBRE,cv);


        if (i > 0){
            SharedPreferences sharedPreferences = this.getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(key,true);
            editor.commit();

            return true;
        }

        return false;
    }

    private void showProgress(final boolean show) {
        findViewById(R.id.search_status_).setVisibility(
                show ? View.VISIBLE : View.GONE);
        if (findViewById(R.id.signup) != null) {
            ( findViewById(R.id.signup)).setVisibility(show ? View.GONE
                    : View.VISIBLE);

        }
        isShowProgress = show;
    }

    private void hideKeyBoard(TextView textView){
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(textView.getWindowToken(), 0);


    }

    @Override
    protected void onDestroy() {
        if (dbHelper != null){
            dbHelper.close();
        }

        if (dataBase != null){
            dataBase.close();
        }
        super.onDestroy();
    }

    @Override
    public void onPreExcute() {
        showProgress(true);
    }

    @Override
    public void onProgressUpdate() {

    }

    @Override
    public void onCancelled() {

    }

    @Override
    public void onPostExcute(String result) {
        try {

            Gson gson = new Gson();
            UsuarioBeans usuarioBeans = gson.fromJson(result, UsuarioBeans.class);

            saveInDataBase(usuarioBeans);

            Intent intent = new Intent(this, Game.class);
            startActivity(intent);
            this.finish();

        }catch (JsonSyntaxException ex){
            ex.printStackTrace();

        }finally {
            showProgress(false);
        }
    }
}
