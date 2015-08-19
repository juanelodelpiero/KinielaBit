package main.kinielabit.com.kinielabit.main.kinielabit.com.game;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import main.kinielabit.com.kinielabit.main.kinielabit.com.game.kiniela.KinielaFragment;

/**
 * Created by juanelo on 08/08/15.
 */
public class GamePagerAdapter extends FragmentPagerAdapter {

    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[] { "KinielaBit", "Grupos", "Mi posicion" };
    private Context context;


    public GamePagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        return KinielaFragment.newInstance("1","2");
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
