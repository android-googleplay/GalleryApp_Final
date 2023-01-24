package image.gallery.organize.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import image.gallery.organize.R;

import java.util.ArrayList;

public class FragPagerAdapter extends FragmentPagerAdapter {

    public ArrayList<Fragment> mFragmentList = new ArrayList<>();
    public ArrayList<String> mFragmentTitleList = new ArrayList<>();
    public ArrayList<Integer> mFragmentIconList = new ArrayList<>();
    public Context context;

    private final String[] titles = {
            "Photos",
            "Albums",
            "Hide"
    };

    private final int[] icons = {
            R.drawable.ic_photos,
            R.drawable.ic_album,
            R.drawable.ic_hide
    };

    public FragPagerAdapter(FragmentManager manager, Context context) {
        super(manager);
        this.context = context;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFrag(Fragment fragment, int position) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(titles[position]);
        mFragmentIconList.add(icons[position]);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Nullable
    public Drawable getPageDrawable(int position) {
        return ResourcesCompat.getDrawable(context.getResources(), icons[position], null);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }
}


