package com.example.lmrs.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lmrs.R;
import com.example.lmrs.model.vieworders.Order;
import com.example.lmrs.model.vieworders.OrdersRecyclerAdapter;
import com.example.lmrs.model.vieworders.ViewOrdersModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ViewOrdersFragment extends Fragment {

    private RecyclerView ordersRecyclerView;
    private RecyclerView.Adapter ordersRecyclerAdapter;
    private RecyclerView.LayoutManager orderRecyclerLayoutMgr;
    List<Order> orders;
    ViewOrdersModel viewOrdersModel;

    private static final String TAG = "ViewOrdersFragment";
;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_view_orders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Objects.requireNonNull(getActivity()).setTitle("View Orders");

        viewOrdersModel = new ViewOrdersModel();

        orders = new ArrayList<>();

//        List<OrderItem> orderItemList = new ArrayList<>();
//        orderItemList.add(new OrderItem("Item 1", 1));
//        orderItemList.add(new OrderItem("Item 2", 2));
//        orderItemList.add(new OrderItem("Item 3", 3));
//        orderItemList.add(new OrderItem("Item 4", 4));
//
//        orders.add(new Order("T1", "O1", orderItemList));
//        orders.add(new Order("T2", "O2", orderItemList));
//        orders.add(new Order("T3", "O3", orderItemList));
//        orders.add(new Order("T4", "O4", orderItemList));
//        orders.add(new Order("T4", "O5", orderItemList));

        ordersRecyclerView = view.findViewById(R.id.rv_view_orders);

        ordersRecyclerView.setHasFixedSize(true);
        orderRecyclerLayoutMgr = new LinearLayoutManager(getContext());
        ordersRecyclerAdapter = new OrdersRecyclerAdapter(orders);

        ordersRecyclerView.setLayoutManager(orderRecyclerLayoutMgr);
        ordersRecyclerView.setAdapter(ordersRecyclerAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
        itemTouchHelper.attachToRecyclerView(ordersRecyclerView);

        getOrders();


    }

    private void getOrders() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                String[] err = {""};
                List<Order> orderList = viewOrdersModel.getPendingOrders(err);
                orders.addAll(orderList);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ordersRecyclerAdapter.notifyDataSetChanged();
                    }
                });

            }
        };

        thread.start();
    }

    ItemTouchHelper.SimpleCallback itemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();

            new MaterialAlertDialogBuilder(getContext())
                    .setTitle("Order Complete")
                    .setMessage("Are you sure you want to mark this order complete?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO: Send request to server to delete the order
                            Log.i(TAG, orders.get(position).getOrderId());

                            ordersRecyclerAdapter.notifyItemRemoved(position);
                            orders.remove(position);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ordersRecyclerAdapter.notifyItemChanged(position);
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    };

}
