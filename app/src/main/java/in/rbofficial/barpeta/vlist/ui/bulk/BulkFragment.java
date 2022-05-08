package in.rbofficial.barpeta.vlist.ui.bulk;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import in.rbofficial.barpeta.vlist.databinding.FragmentBulkBinding;

public class BulkFragment extends Fragment {

    private FragmentBulkBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        BulkViewModel bulkViewModel =
                new ViewModelProvider(this).get(BulkViewModel.class);

        binding = FragmentBulkBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        bulkViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}