package com.sandy_rock_studios.macbookair.tictactoe.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sandy_rock_studios.macbookair.tictactoe.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GameOverFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GameOverFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GameOverFragment extends Fragment implements View.OnClickListener{
    private static final String RESULT_PARAM = "result_param";
    private String myResult;
    private OnFragmentInteractionListener myListener;

    public GameOverFragment() {
        // Required empty public constructor
    }

    public static GameOverFragment newInstance(String result) {
        GameOverFragment fragment = new GameOverFragment();
        Bundle args = new Bundle();
        args.putString(RESULT_PARAM, result);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            myResult = getArguments().getString(RESULT_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game_over, container, false);
        TextView resultTxt = view.findViewById(R.id.game_status_text);
        resultTxt.setText(myResult);
        view.findViewById(R.id.new_game_button).setOnClickListener(this);
        view.findViewById(R.id.home_button).setOnClickListener(this);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            myListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        myListener = null;
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.home_button:
                myListener.onHomeClicked();
                break;
            case R.id.new_game_button:
                myListener.onNewGameClicked();
                break;
        }
    }

    public interface OnFragmentInteractionListener {
        void onHomeClicked();
        void onNewGameClicked();
    }
}
