package net.liucs.tabfragments.app;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class CityGuideFragment extends Fragment {
    private static final String ARG_CITY = "param1";

    private City city;

    public static CityGuideFragment newInstance(City city) {
        CityGuideFragment fragment = new CityGuideFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CITY, city);
        fragment.setArguments(args);
        return fragment;
    }

    public CityGuideFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            city = getArguments().getParcelable(ARG_CITY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_city_guide, container, false);
        TextView blurbView = (TextView) v.findViewById(R.id.blurbView);
        ImageView imageView = (ImageView) v.findViewById(R.id.imageView);
        blurbView.setText(city.blurb);
        imageView.setImageResource(city.drawable);
        return v;
    }


}
