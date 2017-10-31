package hg.hg_android_client.mainscreen.select_path;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import hg.hg_android_client.R;
import hg.hg_android_client.mainscreen.event.CancelTripSetup;
import hg.hg_android_client.mainscreen.event.ConfirmPath;
import hg.hg_android_client.mainscreen.event.PathResponse;
import hg.hg_android_client.mainscreen.event.ShowPath;
import hg.hg_android_client.mainscreen.repository.StateRepository;
import hg.hg_android_client.mainscreen.repository.StateRepositoryFactory;
import hg.hg_android_client.util.ListAdapter;
import hg.hg_android_client.util.LlevameFragment;

public class SelectPathFragment extends LlevameFragment {

    private ListView pathlist;
    private Path selected;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        View layout = inflater.inflate(R.layout.fragment_select_path, container, false);
        initializeLayout(layout);
        return layout;
    }

    private void initializeLayout(View layout) {
        initializeListView(layout);
        initializeCancelButton(layout);
        initializeConfirmButton(layout);
    }

    private void initializeListView(View layout) {
        pathlist = (ListView) layout.findViewById(R.id.path_list);
        queryPaths();
    }

    private void queryPaths() {
        String title = getString(R.string.finding_paths_title);
        String message = getString(R.string.finding_paths_message);
        showDialog(title, message);

        StateRepositoryFactory f = new StateRepositoryFactory();
        StateRepository r = f.get(getContext());

        PathQueryIntent intent = new PathQueryIntent(
                getContext(),
                r.getOrigin(),
                r.getDestination());

        getContext().startService(intent);
    }

    private void initializeCancelButton(View layout) {
        Button button = (Button) layout.findViewById(R.id.button_cancel);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new CancelTripSetup());
            }
        });
    }

    private void initializeConfirmButton(View layout) {
        Button button = (Button) layout.findViewById(R.id.button_confirm);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selected != null) {
                    ConfirmPath confirm = new ConfirmPath(selected);
                    EventBus.getDefault().post(confirm);
                } else {
                    String message = getString(R.string.please_select_path);
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPathQueryResponse(PathResponse event) {
        dismissDialog();
        PathListAdapter adapter = new PathListAdapter(getContext(), event.getRetrievedPaths());
        pathlist.setAdapter(adapter);
        pathlist.setOnItemClickListener(new PathItemClickListener());
    }

    private static final class PathListAdapter extends ListAdapter<Path> {
        public PathListAdapter(@NonNull Context context, List<Path> elements) {
            super(context, elements, R.layout.path_list_item);
        }
        @Override
        protected void handleItem(View view, Path current, int position) {
            setLabelText(view, getCurrentLabel(position));
        }

        private String getCurrentLabel(int position) {
            String base = getContext().getString(R.string.path_list_item);
            return base + Integer.toString(position);
        }

        private void setLabelText(View view, String label) {
            TextView textview = (TextView) view.findViewById(R.id.label);
            textview.setText(label);
        }

    }

    private final class PathItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
            Path path = (Path) adapterView.getItemAtPosition(position);
            selected = path;
            ShowPath event = new ShowPath(path);
            EventBus.getDefault().post(event);
        }
    }

}
