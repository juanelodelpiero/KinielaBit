package main.kinielabit.com.kinielabit;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;

import main.kinielabit.com.kinielabit.main.kinielabit.com.game.Game;
import main.kinielabit.com.kinielabit.main.kinielabit.com.kinielabit.beans.UsuarioBeans;
import main.kinielabit.com.kinielabit.main.kinielabit.com.task.GenericTaskFragment;

public class MainActivity extends AppCompatActivity implements GenericTaskFragment.TaskCallBacks {

    GenericTaskFragment taskFragment;


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
    }

    View.OnClickListener signUp = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (!taskFragment.isRunning()) {

                UsuarioBeans usuario = new UsuarioBeans();
                usuario.setUsername(((EditText) findViewById(R.id.username)).getText().toString());
                usuario.setPassword(((EditText) findViewById(R.id.password)).getText().toString());
                usuario.setCelular(((EditText) findViewById(R.id.phone)).getText().toString());

                Gson gson = new Gson();
                taskFragment.setData("http://192.168.43.242:8080/KinielaWS/webresources/usuario/registroMovil", "PUT", gson.toJson(usuario));
                taskFragment.start();
            }
        }
    };


    @Override
    public void onPreExcute() {

    }

    @Override
    public void onProgressUpdate() {

    }

    @Override
    public void onCancelled() {

    }

    @Override
    public void onPostExcute(String result) {
        Intent intent = new Intent(this, Game.class);
        startActivity(intent);
        this.finish();
    }
}
