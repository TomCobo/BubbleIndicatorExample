package pandiandcode.bubbleindicatorexample.ui.adapters;

import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import pandiandcode.bubbleindicatorexample.R;

/**
 * Created by Rocio Ortega on 30/01/16.
 */
public class ExamplePageAdapter extends PagerAdapter {

    private int[] mColorReferences;
    private int[] mTextColorReferences;

    public ExamplePageAdapter(){
        mColorReferences = new int[]{R.color.pink, R.color.lime, R.color.yellow, R.color.orange};
        mTextColorReferences = new int[]{R.color.indigo, R.color.green, R.color.purple, R.color.brown};
    }

    public int getTextColorReference(int position){
        return mTextColorReferences[position];
    }

    @Override public int getCount() {
        return mColorReferences != null ? mColorReferences.length : 0;
    }

    @Override public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override public Object instantiateItem(ViewGroup container, int position) {
        View rootView = LayoutInflater.from(container.getContext())
                .inflate(R.layout.layout_page, null);

        container.addView(rootView);

        LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.layout);
        TextView title = (TextView) rootView.findViewById(R.id.textView);

        layout.setBackgroundResource(mColorReferences[position]);
        title.setText(String.valueOf(position+1));
        title.setTextColor(rootView.getResources().getColor(mTextColorReferences[position]));
        return rootView;
    }

    @Override public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

}
