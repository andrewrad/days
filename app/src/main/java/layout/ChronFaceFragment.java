package layout;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Spinner;
import android.widget.TextView;

import com.radicaldroids.days.ChronTime;
import com.radicaldroids.days.OnFragmentInteractionListener;
import com.radicaldroids.days.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;
import io.realm.Realm;
import io.realm.RealmResults;
import models.SubTask;
import models.Task;

import static java.util.Comparator.naturalOrder;

public class ChronFaceFragment extends Fragment implements View.OnClickListener{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TIME_KEY = "time";

    private String mParam1;
    private String mParam2;

    private Realm realm;

    private boolean isRunning = false;
    private long lastStop;

    private OnFragmentInteractionListener mListener;

    @BindView(R.id.textView3) TextView clock;
    @BindView(R.id.chron_time) ChronTime chronTimeView;
    @BindView(R.id.chronometer2) Chronometer chronView;
    @BindView(R.id.startButton) Button startButton;
    @BindView(R.id.task_spinner) Spinner taskSpinner;
    @BindView(R.id.sub_task_spinner) Spinner subTaskSpinner;

    public ChronFaceFragment() {
    }

    /**
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChronFaceFragment.
     */
    public static ChronFaceFragment newInstance(String param1, String param2) {
        ChronFaceFragment fragment = new ChronFaceFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blank, container, false);
        ButterKnife.bind(this, view);
        startButton.setText("Start");

        chronTimeView.enterTime(System.currentTimeMillis());
//        ChronTime chronTime = new ChronTime(this.getActivity());
//        chronTime.setText("hifehiehf");
        Log.e("Text", "chrontimeView text: " + chronTimeView.getText());

        if(savedInstanceState != null) {
            chronView.setBase(savedInstanceState.getLong(TIME_KEY));
        } else {
            chronView.setBase(SystemClock.elapsedRealtime());
        }
        startButton.setOnClickListener(this);
        addSpinner();

        return view;
    }

    private void addSpinner() {
        realm = Realm.getDefaultInstance();
        RealmResults<Task> realmArray = realm.where(Task.class).findAllSorted("name");

        List<String> listOfTasks = new ArrayList<>();
        for(Task t:realmArray) {
            listOfTasks.add(t.getName());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this.getActivity(), R.layout.support_simple_spinner_dropdown_item, listOfTasks);
        taskSpinner.setAdapter(dataAdapter);
    }

    private void deleteRealmObject(final Task t) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                if(t == null) {
                    realm.where(Task.class).findAll().deleteAllFromRealm();
                } else {
                    t.deleteFromRealm();
                }
            }
        });
    }

    @OnItemSelected(R.id.task_spinner)
    public void onSpinnerSelected(int index, AdapterView<?> parent) {
        Task t = realm.where(Task.class).equalTo("name", parent.getItemAtPosition(index).toString()).findFirst();
        List<String> subList = new ArrayList<>();
        for (SubTask s : t.getSubTasks()) {
            subList.add(s.getName());
        }

        Collections.sort(subList);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this.getActivity(), R.layout.support_simple_spinner_dropdown_item, subList);
        subTaskSpinner.setAdapter(dataAdapter);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        if(isRunning == false) {
            Log.e("time", "time: " + chronView.getBase() + ", " + SystemClock.elapsedRealtime() + ", " + lastStop + " total: " + (SystemClock.elapsedRealtime() - lastStop));
            chronView.setBase(SystemClock.elapsedRealtime() - lastStop);
            chronView.start();
            chronTimeView.start();
            startButton.setText("Stop");
            isRunning = true;
        } else {
            chronView.stop();
            chronTimeView.stop();
            lastStop = SystemClock.elapsedRealtime() - chronView.getBase();
            startButton.setText("Start");
            isRunning = false;
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putLong(TIME_KEY, chronView.getBase());
        super.onSaveInstanceState(outState);
    }
}
