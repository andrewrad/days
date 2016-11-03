package layout;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.radicaldroids.days.OnFragmentInteractionListener;
import com.radicaldroids.days.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddTaskFragment extends Fragment implements View.OnClickListener, TextView.OnEditorActionListener{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private List<String> tasks;

    @BindView(R.id.add_new_task) EditText addTask;
    @BindView(R.id.add_new_subtask) EditText addSubTask;
    @BindView(R.id.create_task_button) Button createTask;

    public AddTaskFragment() {
    }

    public static AddTaskFragment newInstance(String param1, String param2) {
        AddTaskFragment fragment = new AddTaskFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tasks = new ArrayList<>();
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_task, container, false);
        ButterKnife.bind(this, view);
        addTask.setOnEditorActionListener(this);
        addTask.setLines(1);
        createTask.setOnClickListener(this);
        return view;
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
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        Log.e("", "event: " + actionId + "event: " + event);
        boolean handled = false;
        if(actionId == EditorInfo.IME_ACTION_SEND) {
            Log.e("enter button", "enter button pushed");
            handled = true;
        }
        return handled;
    }

    @Override
    public void onClick(View v) {
        tasks.add(addTask.getText().toString());
        String task = addTask.getText().toString();
        String subTask = addSubTask.getText().toString();
        if(task != null) {
            mListener.createTask(task, subTask);
        }
        Log.e("tasks", "Tasks: " + tasks);
    }
}