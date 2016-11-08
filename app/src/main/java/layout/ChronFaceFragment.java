package layout;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import io.realm.RealmList;
import io.realm.RealmResults;
import models.EpochSSTimes;
import models.SubTask;
import models.Task;

public class ChronFaceFragment extends Fragment implements View.OnClickListener{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TIME_KEY = "time";

    private Realm realm;

    private boolean isRunning = false;
    private long lastStop;
    private long start;

    private OnFragmentInteractionListener mListener;

    @BindView(R.id.textView3) TextView clock;
    @BindView(R.id.chron_time) ChronTime chronTimeView;
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
            String mParam1 = getArguments().getString(ARG_PARAM1);
            String mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blank, container, false);
        ButterKnife.bind(this, view);
        startButton.setText(R.string.start_button_text);

//        chronTimeView.enterTime(0);

        if(savedInstanceState != null) {
            chronTimeView.setTotalTime(savedInstanceState.getLong(TIME_KEY));
        } else {
            chronTimeView.setTotalTime(0);
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

    /**
     * onClick() starts and stops the clock
     * when stopped, it will create a Realm entry for start/stop times
     * @param view
     */
    @Override
    public void onClick(View view) {
        if(!isRunning) {
            start = chronTimeView.start();
            startButton.setText(R.string.stop_button_text);
            isRunning = true;
        } else {
            final long stop = chronTimeView.stop();
            Realm.init(this.getActivity());
            Realm realm = Realm.getDefaultInstance();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    EpochSSTimes epochSSTimes = realm.createObject(EpochSSTimes.class);
                    epochSSTimes.setStartTime(start);
                    epochSSTimes.setEndTime(stop);
                    String selectedName = taskSpinner.getSelectedItem().toString();
                    Task t = realm.where(Task.class).equalTo("name", selectedName).findFirst();
                    t.getEpochSSTimes().add(epochSSTimes);

                    readEpochSSTimes(selectedName);
                }
            });

            startButton.setText(R.string.start_button_text);
            isRunning = false;
        }
    }

    private void readEpochSSTimes(String selectedName) {
        Task t = realm.where(Task.class).equalTo("name", selectedName).findFirst();
        RealmList<EpochSSTimes> realmList = t.getEpochSSTimes();
        for(EpochSSTimes r:realmList) {
            Log.e("times", "start: " + r.getStartTime() + ", end: " + r.getEndTime());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putLong(TIME_KEY, chronTimeView.getTotalTime());
        super.onSaveInstanceState(outState);
    }
}
