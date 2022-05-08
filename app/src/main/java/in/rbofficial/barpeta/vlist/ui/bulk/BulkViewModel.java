package in.rbofficial.barpeta.vlist.ui.bulk;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class BulkViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public BulkViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is gallery fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}