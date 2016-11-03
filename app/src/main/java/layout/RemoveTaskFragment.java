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
import java.util.Collections;
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
    private ArrayAdapter<String> dataAdapter;

    @BindView(R.id.task_spinner) Spinner taskSpinner;
    @BindView(R.id.sub_task_spinner) Spinner subTaskSpinner;

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
        populateTaskSpinner();
        return view;
    }

    @OnItemSelected(R.id.task_spinner)
    public void onSpinnerSelected(int index, AdapterView<?> parent) {
        Task t = realm.where(Task.class).equalTo("name", parent.getItemAtPosition(index).toString()).findFirst();
        populateSubTaskSpinner(t);
    }

    public void populateTaskSpinner() {
        RealmResults<Task> realmArray = realm.where(Task.class).findAllSorted("name");

        List<String> listOfTasks = new ArrayList<>();
        for(Task t:realmArray) {
            listOfTasks.add(t.getName());
        }

        dataAdapter = new ArrayAdapter<>(this.getActivity(), R.layout.support_simple_spinner_dropdown_item, listOfTasks);
        taskSpinner.setAdapter(dataAdapter);
    }

    public void populateSubTaskSpinner(Task t) {
        List<String> subList = new ArrayList<>();
        for (SubTask s : t.getSubTasks()) {
            subList.add(s.getName());
        }
        Collections.sort(subList);
        dataAdapter = new ArrayAdapter<>(this.getActivity(), R.layout.support_simple_spinner_dropdown_item, subList);
        subTaskSpinner.setAdapter(dataAdapter);
    }

    @OnClick(R.id.remove_task_button)
    public void onRemoveTaskClicked(View view) {
        Log.e("click", "clicked: " + taskSpinner.getSelectedItem() + ", " + subTaskSpinner.getSelectedItem());
        final Task task = realm.where(Task.class).equalTo("name", this.taskSpinner.getSelectedItem().toString()).findFirst();

        if(task.getSubTasks().size() > 0) {
            final Task sub = realm.where(Task.class).equalTo("subTasks.name", subTaskSpinner.getSelectedItem().toString()).findFirst();

            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    sub.getSubTasks().deleteFromRealm(subTaskSpinner.getSelectedItemPosition());
                }
            });
            populateSubTaskSpinner(task);
            dataAdapter.notifyDataSetChanged();

        } else {
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    task.deleteFromRealm();
                    populateTaskSpinner();
                }
            });
        }
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