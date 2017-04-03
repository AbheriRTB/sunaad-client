package com.abheri.sunaad.view.directory;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.abheri.sunaad.R;
import com.abheri.sunaad.model.Artiste;
import com.abheri.sunaad.model.ArtisteListDataCache;
import com.abheri.sunaad.model.CloudDataFetcherAsyncTask;
import com.abheri.sunaad.model.ModifiedFlagFetcherAsyncTask;
import com.abheri.sunaad.model.SQLStrings;
import com.abheri.sunaad.view.HandleModifiedFlagServiceResponse;
import com.abheri.sunaad.view.HandleServiceResponse;
import com.abheri.sunaad.view.MainActivity;
import com.abheri.sunaad.view.SunaadViews;
import com.abheri.sunaad.view.Util;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ArtisteDirectoryFragment extends Fragment implements HandleServiceResponse, HandleModifiedFlagServiceResponse {

    ViewAnimator viewAnimator;
    Context context;
    String jsonstring;
    View rootView;
    ListView listView;
    ProgressBar progressBar;
    TextView errTextView;
    Activity myActivity;
    List<Artiste> cachedArtisteList;
    int scrollPos=0;
    Artiste noticePrgObj=null;
    public boolean doScroll=true;
    boolean refreshRunning=false;

    public ArtisteDirectoryFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.dir_fragment_artiste_directory, container,
                false);

        if(null == context){
            context = rootView.getContext();
        }

        //setHasOptionsMenu(true);

        Log.i("PRAS", "In Artiste Directory Fragment");

        // Get ListView object from xml
        listView = (ListView) rootView.findViewById(R.id.artisteDirList);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setSelector(android.R.color.holo_blue_light);
        listView.setVisibility(View.GONE);

        progressBar = (ProgressBar)rootView.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        errTextView = (TextView)rootView.findViewById(R.id.serviceErrorText);
        errTextView.setVisibility(View.GONE);

        final android.support.v4.app.FragmentManager fragmentManager = getFragmentManager();
        myActivity = getActivity();

        getData(this, false);


        //Onclick listener not required for initial implementation
        //Implemented here just for reference
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {


                // ListView Clicked item value
                Artiste itemValue = (Artiste) parent.getItemAtPosition(position);

                /*Toast.makeText(
                        view.getContext(),
                        itemValue.getTitle() + "  Selected...",
                        Toast.LENGTH_SHORT).show(); */

                openArtisteDetails(itemValue, fragmentManager);
            }

        });

        ((MainActivity)getActivity()).setActionBarTitle(getString(R.string.title_section2));

        if(doScroll) {
            timerDelayRunForScroll(500l);
        }

        ((MainActivity)getActivity()).setActionBarTitle(getString(R.string.title_dir_submenu1));
        // Obtain the shared Tracker instance.
        Util.logToGA(Util.ARTISTE_DIR_SCREEN);

        return rootView;
    }

    private void openArtisteDetails(Artiste artisteToOpen,
                                    android.support.v4.app.FragmentManager fragmentManager){
        Intent artIntent = new Intent();
        artIntent.putExtra("ArtisteDetails", artisteToOpen);
        myActivity.setIntent(artIntent);

        ArtisteDetailsFragment adf = new ArtisteDetailsFragment();

        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.container, adf,"ArtisteDetailsFragment");
        ft.addToBackStack(null);
        ft.commit();

    }

    public void timerDelayRunForScroll(final long time) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                try {
                    int h1 = listView.getHeight();
                    int h2 = rootView.getHeight();
                    //scrollPos = Util.getScrollPosition(values);
                    listView.smoothScrollToPositionFromTop(scrollPos, h1 / 2 - h2 / 2, 500);
                } catch (Exception e) {
                }
            }
        }, time);
    }


    public void getData(ArtisteDirectoryFragment fragmentThis, boolean doRefresh) {

        ArtisteListDataCache alc = new ArtisteListDataCache(context);
        Util ut = new Util();

        cachedArtisteList = alc.RetrieveArtisteDataFromCache();

        //First render the screen with cached data
        if(cachedArtisteList != null) {
            updateViewFromData(cachedArtisteList);
        }

        //If network is available refresh the cache and the view
        if (ut.isNetworkAvailable(context)) {
            ModifiedFlagFetcherAsyncTask ft = new ModifiedFlagFetcherAsyncTask(fragmentThis,
                    SQLStrings.COLUMN_ARTISTE_LAST_REFRESH, context);
            ft.execute(Util.getServiceUrl(SunaadViews.SETTINGS));
        }

        //If cache is null & network is not available, show error
        else if(cachedArtisteList == null) {
            errTextView.setText("Connect to network to get Sunaad Data");
            errTextView.setVisibility(View.VISIBLE);
        }

    }


    //Succes & Failure handlers of the isModifiedFlagFetch Async Task
    @Override
    public void onModifiedFlagFetchSuccess(Object result) {

        if(((String)result).contains("true")){
            getListData(this);
        }
    }

    @Override
    public void onModifiedFlagFetchError(Object result) {
        //If couldn't get the flag for some reason, assume it is true
        getListData(this);
    }

    void getListData(ArtisteDirectoryFragment fragmentThis){
        progressBar.setVisibility(View.VISIBLE);
        CloudDataFetcherAsyncTask rt = new CloudDataFetcherAsyncTask(fragmentThis, SunaadViews.ARTISTE_DIR, context);
        rt.execute(Util.getServiceUrl(SunaadViews.ARTISTE_DIR));
        refreshRunning = true;
    }




    void updateArtisteList(View rootView, ListView artisteList, List<Artiste> values) {

        //scrollPos = Util.getScrollPosition(values);

        ArtisteListAdapter adapter;
        adapter = (ArtisteListAdapter)artisteList.getAdapter();

        if(adapter == null){
            adapter = new ArtisteListAdapter(
                    rootView.getContext(), R.layout.dir_artiste_dir_child_item, values);
            artisteList.setAdapter(adapter);
        }else{
            adapter.setNotifyOnChange(false);
            adapter.clear();
            adapter.addAll(values);
            adapter.notifyDataSetChanged();
        }

        if(doScroll) {
            timerDelayRunForScroll(500l);
        }

    }

    public void onSuccess(Object result) {
        refreshRunning=false;

        List<Artiste> values = (List<Artiste>) result;

        ArtisteListDataCache alc = new ArtisteListDataCache(context);
        alc.SaveArtisteDataInCache((List<Artiste>) result);

        updateViewFromData(values);
    }

    public void updateViewFromData(List<Artiste> values){
        progressBar.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);

        //Filter old programs from the list
        //List<Program> fValues = ArtisteDataHelper.filterOldPrograms(values, Util.HOW_OLD);

        updateArtisteList(rootView, listView, values);
    }

    public void onError(Object result){
        refreshRunning=false;

        Exception e = (Exception)result;
        String st = "";
        if(null != e)
            st = e.toString();

        progressBar.setVisibility(View.GONE);
        errTextView.setText("Service Error Occurred:" + st);
        errTextView.setTextColor(Color.parseColor("#FF0000"));
        errTextView.setVisibility(View.VISIBLE);

    }

    /*
    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_refresh).setVisible(!refreshRunning);
        super.onPrepareOptionsMenu(menu);

    }
    */

}
