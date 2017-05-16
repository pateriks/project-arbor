package se.kth.projectarbor.project_arbor.tutorial;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.*;
import android.widget.Button;

import se.kth.projectarbor.project_arbor.R;

/**
 * Created by Lazar Cerovic and Johan Andersson on 2017-05-16.
 */

public class TutorialThree extends Fragment {
    View view;
    private Button mNextButton;
    private Button mPreviousButton;
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.tutorial_3, container, false);


        mNextButton = (Button) view.findViewById(R.id.next);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TutorialArbor)getActivity()).next_fragment(view);

            }
        });
        mPreviousButton = (Button) view.findViewById(R.id.previous);
        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TutorialArbor)getActivity()).previous_fragment(view);
            }
        });

        return view;
    }

}