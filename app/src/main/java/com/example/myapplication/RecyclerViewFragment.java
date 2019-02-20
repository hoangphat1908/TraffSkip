package com.example.myapplication;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import java.util.ArrayList;
import android.widget.Toast;
import android.view.Menu;
import android.support.v7.widget.SearchView;
import android.support.v4.view.MenuItemCompat;
import android.app.SearchManager;
import android.widget.EditText;
import android.graphics.Color;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.ViewGroup;
import android.view.MenuInflater;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecyclerViewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */


public class RecyclerViewFragment extends Fragment {

    //  Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;


    private RecyclerView recyclerView;



    private RecyclerViewAdapter mAdapter;

    private ArrayList<RecyclerViewFragmentAbstractModel> modelList = new ArrayList<>();


    public RecyclerViewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecyclerViewFragment.
     */
    //  Rename and change types and number of parameters
    public static RecyclerViewFragment newInstance(String param1, String param2) {
        RecyclerViewFragment fragment = new RecyclerViewFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static RecyclerViewFragment newInstance() {
        RecyclerViewFragment fragment = new RecyclerViewFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_recycler_view, container, false);

        // ButterKnife.bind(this);
        findViews(view);

        return view;

    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setAdapter();


    }


    private void findViews(View view) {

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_search, menu);

        // Retrieve the SearchView and plug it into SearchManager
        final SearchView searchView = (SearchView) MenuItemCompat
                .getActionView(menu.findItem(R.id.action_search));

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(getActivity().SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        //changing edittext color
        EditText searchEdit = ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text));
        searchEdit.setTextColor(Color.WHITE);
        searchEdit.setHintTextColor(Color.WHITE);
        searchEdit.setBackgroundColor(Color.TRANSPARENT);
        searchEdit.setHint("Search");

        InputFilter[] fArray = new InputFilter[2];
        fArray[0] = new InputFilter.LengthFilter(40);
        fArray[1] = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

                for (int i = start; i < end; i++) {

                    if (!Character.isLetterOrDigit(source.charAt(i)))
                        return "";
                }


                return null;


            }
        };
        searchEdit.setFilters(fArray);
        View v = searchView.findViewById(android.support.v7.appcompat.R.id.search_plate);
        v.setBackgroundColor(Color.TRANSPARENT);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                ArrayList<RecyclerViewFragmentAbstractModel> filterList = new ArrayList<RecyclerViewFragmentAbstractModel>();
                if (s.length() > 0) {
                    for (int i = 0; i < modelList.size(); i++) {
                        if (modelList.get(i).getTitle().toLowerCase().contains(s.toString().toLowerCase())) {
                            filterList.add(modelList.get(i));
                            mAdapter.updateList(filterList);
                        }
                    }

                } else {
                    mAdapter.updateList(modelList);
                }
                return false;
            }
        });

    }


    private void setAdapter() {

        modelList.add(new RecyclerViewFragmentAbstractModel("North Garage", "500/1500 " + " Parking Space Used"));
        modelList.add(new RecyclerViewFragmentAbstractModel("South Garage", "1000/1500 " + " Parking Space Used"));
        modelList.add(new RecyclerViewFragmentAbstractModel("West Garage", "1500/1500 " + " Parking Space Used"));

        modelList.add(new RecyclerViewFragmentAbstractModel("Android", "Hello " + " Android"));
        modelList.add(new RecyclerViewFragmentAbstractModel("Beta", "Hello " + " Beta"));
        modelList.add(new RecyclerViewFragmentAbstractModel("Cupcake", "Hello " + " Cupcake"));
        modelList.add(new RecyclerViewFragmentAbstractModel("Donut", "Hello " + " Donut"));
        modelList.add(new RecyclerViewFragmentAbstractModel("Eclair", "Hello " + " Eclair"));
        modelList.add(new RecyclerViewFragmentAbstractModel("Froyo", "Hello " + " Froyo"));
        modelList.add(new RecyclerViewFragmentAbstractModel("Gingerbread", "Hello " + " Gingerbread"));
        modelList.add(new RecyclerViewFragmentAbstractModel("Honeycomb", "Hello " + " Honeycomb"));
        modelList.add(new RecyclerViewFragmentAbstractModel("Ice Cream Sandwich", "Hello " + " Ice Cream Sandwich"));
        modelList.add(new RecyclerViewFragmentAbstractModel("Jelly Bean", "Hello " + " Jelly Bean"));
        modelList.add(new RecyclerViewFragmentAbstractModel("KitKat", "Hello " + " KitKat"));
        modelList.add(new RecyclerViewFragmentAbstractModel("Lollipop", "Hello " + " Lollipop"));
        modelList.add(new RecyclerViewFragmentAbstractModel("Marshmallow", "Hello " + " Marshmallow"));
        modelList.add(new RecyclerViewFragmentAbstractModel("Nougat", "Hello " + " Nougat"));
        modelList.add(new RecyclerViewFragmentAbstractModel("Android O", "Hello " + " Android O"));


        mAdapter = new RecyclerViewAdapter(getActivity(), modelList);

        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);


        recyclerView.setAdapter(mAdapter);


        mAdapter.SetOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, RecyclerViewFragmentAbstractModel model) {

                //handle item click events here
                Toast.makeText(getActivity(), "Hey " + model.getTitle(), Toast.LENGTH_SHORT).show();


            }
        });


    }

}
