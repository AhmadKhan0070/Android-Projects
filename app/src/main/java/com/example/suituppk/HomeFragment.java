package com.example.suituppk;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import static com.example.suituppk.DBqueries.categoryModelList;
import static com.example.suituppk.DBqueries.lists;
import static com.example.suituppk.DBqueries.loadCategories;
import static com.example.suituppk.DBqueries.loadFragmentData;
import static com.example.suituppk.DBqueries.loadedCategoriesNames;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView categoryrecyclerview;
    private List<category_model> category_modelFakeList = new ArrayList<>();
    private List<HomePageModel> homePageModelFakeList = new ArrayList<>();
    private categoryAdapter categoryAdapter;
    private RecyclerView homePagerecyclerView;
    private HomePageAdapter adapter;
    private ImageView noIntenetConnection;
    private ConnectivityManager connectivityManager;
    private NetworkInfo networkInfo;
    private Button retrybtn;


    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home2, container, false);

        swipeRefreshLayout = view.findViewById(R.id.refresh_layout);
        noIntenetConnection = view.findViewById(R.id.no_internet_connection);
        categoryrecyclerview  = view.findViewById(R.id.catagory_recycler_view);
        homePagerecyclerView = view.findViewById(R.id.home_page_recyclerview);
        retrybtn = view.findViewById(R.id.retry_btn);

        swipeRefreshLayout.setColorSchemeColors(getContext().getResources().getColor(R.color.colorPrimary) ,
                getContext().getResources().getColor(R.color.colorPrimary) ,
                getContext().getResources().getColor(R.color.colorPrimary));


        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        categoryrecyclerview.setLayoutManager(layoutManager);


        LinearLayoutManager testingLayoutManager = new LinearLayoutManager(getContext());
        testingLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        homePagerecyclerView.setLayoutManager(testingLayoutManager);



        /////////////  Categories Fake list

        category_modelFakeList.add(new category_model("null" , ""));
        category_modelFakeList.add(new category_model("" , ""));
        category_modelFakeList.add(new category_model("" , ""));
        category_modelFakeList.add(new category_model("" , ""));
        category_modelFakeList.add(new category_model("" , ""));
        category_modelFakeList.add(new category_model("" , ""));
        category_modelFakeList.add(new category_model("" , ""));
        category_modelFakeList.add(new category_model("" , ""));
        category_modelFakeList.add(new category_model("" , ""));
        category_modelFakeList.add(new category_model("" , ""));

        /////////////  Categories Fake list

        //////////Home page fake list

        List<sliderModel> sliderModelFakeList = new ArrayList<>();
        sliderModelFakeList.add(new sliderModel("null" , "#dfdfdf"));
        sliderModelFakeList.add(new sliderModel("null" , "#dfdfdf"));
        sliderModelFakeList.add(new sliderModel("null" , "#dfdfdf"));
        sliderModelFakeList.add(new sliderModel("null" , "#dfdfdf"));
        sliderModelFakeList.add(new sliderModel("null" , "#dfdfdf"));

        List<HorizontalProductScrollModel> horizontalProductScrollModelFakeList = new ArrayList<>();
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("" , "" , "" , "" , ""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("" , "" , "" , "" , ""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("" , "" , "" , "" , ""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("" , "" , "" , "" , ""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("" , "" , "" , "" , ""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("" , "" , "" , "" , ""));
        horizontalProductScrollModelFakeList.add(new HorizontalProductScrollModel("" , "" , "" , "" , ""));


        homePageModelFakeList.add(new HomePageModel(0,sliderModelFakeList));
        homePageModelFakeList.add(new HomePageModel(1,"" ,"#dfdfdf"));
        homePageModelFakeList.add(new HomePageModel(2,"" , "#dfdfdf" , horizontalProductScrollModelFakeList , new ArrayList<WishlistModel>()));
        homePageModelFakeList.add(new HomePageModel(3,"" , "#dfdfdf" , horizontalProductScrollModelFakeList));


        //////////Home page fake list


        categoryAdapter = new categoryAdapter(category_modelFakeList);

        adapter = new HomePageAdapter(homePageModelFakeList);


         connectivityManager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
         networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected() == true) {


            retrybtn.setVisibility(View.GONE);
            noIntenetConnection.setVisibility(View.GONE);
            retrybtn.setVisibility(View.GONE);
            categoryrecyclerview.setVisibility(View.VISIBLE);
            homePagerecyclerView.setVisibility(View.VISIBLE);
            if (categoryModelList.size() == 0){

                loadCategories(categoryrecyclerview , getContext());

            }else{
                categoryAdapter = new categoryAdapter(categoryModelList);
                categoryAdapter.notifyDataSetChanged();
            }

            categoryrecyclerview.setAdapter(categoryAdapter);

            if (lists.size() == 0){

                loadedCategoriesNames.add("Home");
                lists.add(new ArrayList<HomePageModel>());

                loadFragmentData(homePagerecyclerView  , getContext() , 0 , "Home");

            }else{
                adapter = new HomePageAdapter(lists.get(0));
                adapter.notifyDataSetChanged();
            }

            homePagerecyclerView.setAdapter(adapter);
        }else {


            categoryrecyclerview.setVisibility(View.GONE);
            homePagerecyclerView.setVisibility(View.GONE);
            Glide.with(this).load(R.drawable.no_internet).into(noIntenetConnection);
            noIntenetConnection.setVisibility(View.VISIBLE);
            retrybtn.setVisibility(View.VISIBLE);
        }


        /////////////// refresh layout

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                reloadPage();
            }
        });
        /////////////// refresh layout



        retrybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadPage();
            }
        });

        return view;
    }


    private void reloadPage(){

        networkInfo = connectivityManager.getActiveNetworkInfo();
//        categoryModelList.clear();
 //       lists.clear();
//        loadedCategoriesNames.clear();

        DBqueries.clearData();

        if (networkInfo != null && networkInfo.isConnected() == true) {


            noIntenetConnection.setVisibility(View.GONE);
            retrybtn.setVisibility(View.GONE);
            categoryrecyclerview.setVisibility(View.VISIBLE);
            homePagerecyclerView.setVisibility(View.VISIBLE);

            categoryAdapter = new categoryAdapter(category_modelFakeList);
            adapter = new HomePageAdapter(homePageModelFakeList);
            categoryrecyclerview.setAdapter(categoryAdapter);
            homePagerecyclerView.setAdapter(adapter);

            loadCategories(categoryrecyclerview , getContext());
            loadedCategoriesNames.add("Home");
            lists.add(new ArrayList<HomePageModel>());
            loadFragmentData(homePagerecyclerView, getContext() , 0 , "Home");



        }else {



            Toast.makeText(getContext(), "Please check your internet connection", Toast.LENGTH_SHORT).show();
            categoryrecyclerview.setVisibility(View.GONE);
            homePagerecyclerView.setVisibility(View.GONE);
            Glide.with(getContext()).load(R.drawable.no_internet).into(noIntenetConnection);
            noIntenetConnection.setVisibility(View.VISIBLE);
            retrybtn.setVisibility(View.VISIBLE);
            swipeRefreshLayout.setRefreshing(false);
        }
    }

}