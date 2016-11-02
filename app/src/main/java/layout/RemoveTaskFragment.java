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
import android.widget.Spinner;

import com.radicaldroids.days.OnFragmentInteractionListener;
import com.radicaldroids.days.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemSelected;
import io.realm.Realm;
import io.realm.RealmResults;
import models.SubTask;
import models.Task;

public class RemoveTaskFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private Realm realm;
    private List<String> listOfTasks;
    private RealmResults<Task> realmArray;

    @BindView(R.id.task_spinner) Spinner task;
    @BindView(R.id.sub_task_spinner) Spinner subTask;

    public RemoveTaskFragment() {
    }

    public static RemoveTaskFragment newInstance(String param1, String param2) {
        RemoveTaskFragment fragment = new RemoveTaskFragment();
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
        View view = inflater.inflate(R.layout.fragment_remove_task, container, false);
        ButterKnife.bind(this, view);

        realm = Realm.getDefaultInstance();
        realmArray = realm.where(Task.class).findAllSorted("name");

        listOfTasks = new ArrayList<>();
        for(Task t:realmArray) {
            listOfTasks.add(t.getName());
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this.getActivity(), R.layout.support_simple_spinner_dropdown_item, listOfTasks);
        task.setAdapter(dataAdapter);
        return view;
    }

    @OnItemSelected(R.id.task_spinner)
    public void onSpinnerSelected(int index, AdapterView<?> parent) {
        Task t = realm.where(Task.class).equalTo("name", parent.getItemAtPosition(index).toString()).findFirst();
        List<String> sub = new ArrayList<>();
        for (SubTask s : t.getSubTasks()) {
            sub.add(s.getName());
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this.getActivity(), R.layout.support_simple_spinner_dropdown_item, sub);
        subTask.setAdapter(dataAdapter);
    }

    @OnClick(R.id.remove_task_button)
    public void onRemoveTaskClicked(View view) {
        Log.e("click", "clicked: " + task.getSelectedItem());
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
}