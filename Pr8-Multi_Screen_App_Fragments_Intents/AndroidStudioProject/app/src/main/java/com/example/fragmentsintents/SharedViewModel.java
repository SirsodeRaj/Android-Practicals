package com.example.fragmentsintents;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/** Lets fragments communicate through the host activity (Form -> Home). */
public class SharedViewModel extends ViewModel {
    private final MutableLiveData<String> savedProfile = new MutableLiveData<>();

    public LiveData<String> getSavedProfile() {
        return savedProfile;
    }

    public void setSavedProfile(String text) {
        savedProfile.setValue(text);
    }
}
