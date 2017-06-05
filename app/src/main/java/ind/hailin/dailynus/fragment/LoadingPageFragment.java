package ind.hailin.dailynus.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import ind.hailin.dailynus.R;

public class LoadingPageFragment extends Fragment {
    public static final String TAG = "LoadingPageFragment";
    public static final String TAG_TRANSPARENT = "LoadingPageTransparent";

    public static final int STYLE_DEFAULT = 1;
    public static final int STYLE_TRANSPARENT = 2;

    private ProgressBar progressBar;

    private int style;
    private int actionCode;
    private OnFragmentInteractionListener mListener;

    public LoadingPageFragment() {
    }

    public static LoadingPageFragment newInstance() {
        LoadingPageFragment fragment = new LoadingPageFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mListener.onFragmentInteraction(actionCode);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = null;
        switch (style) {
            case STYLE_TRANSPARENT:
                view = inflater.inflate(R.layout.content_loading_page_transparent, container, false);
                break;
            default:
                view = inflater.inflate(R.layout.content_loading_page, container, false);

                view.findViewById(R.id.loadingpage_layout).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onFragmentInteraction(actionCode);
                    }
                });
                break;
        }

        progressBar = (ProgressBar) view.findViewById(R.id.loadingpage_progressbar);

        return view;
    }

    public void setOnFragmentInteractionListener(OnFragmentInteractionListener listener, int actionCode){
        this.mListener = listener;
        this.actionCode = actionCode;
    }

    public void setStyle(int style){
        this.style = style;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(int actionCode);
    }
}
