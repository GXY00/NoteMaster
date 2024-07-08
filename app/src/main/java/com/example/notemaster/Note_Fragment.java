package com.example.notemaster;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.List;

import tools.NoteAdapter;

public class Note_Fragment extends Fragment {

    private ListView lv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View noteList = inflater.inflate(R.layout.note_fragment,null);
        lv = noteList.findViewById(R.id.lv);
        return noteList;
    }

    public ListView getLv() {
        return lv;
    }
}
