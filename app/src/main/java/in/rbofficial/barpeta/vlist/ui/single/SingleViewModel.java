package in.rbofficial.barpeta.vlist.ui.single;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SingleViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public SingleViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}