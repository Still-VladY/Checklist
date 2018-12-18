package com.inrusinvest.checklist;

import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.List;

public class QuestionAdapter extends PagerAdapter {
    private List<View> pages;

    QuestionAdapter(List<View> pages) {
        this.pages = pages;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull View collection, int position) {
        View v = pages.get(position);
        ((ViewPager) collection).addView(v, 0);
        notifyDataSetChanged();
        return v;
    }

    @Override
    public void destroyItem(@NonNull View collection, int position, @NonNull Object view) {
        ((ViewPager) collection).removeView((View) view);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return pages.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view.equals(o);
    }
}
