package com.fravier.travel.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fravier.travel.R;
import com.fravier.travel.global.Fonting;
import com.fravier.travel.models.Expense;
import com.fravier.travel.utilities.Utilities;

import java.util.List;

/**
 * Created by francis on 21/04/2016.
 */
public class ExpenseRecyclerAdapter extends RecyclerView.Adapter<ExpenseRecyclerAdapter.MyViewHolder> {

    private List<Expense> expenses;
    public Context ctx;

    public ExpenseRecyclerAdapter(List<Expense> expenses) {
        this.expenses = expenses;
    }

    @Override
    public int getItemCount() {
        return expenses.size();
    }

    @Override
    public long getItemId(int position) {
        long id = expenses.get(position).getId();

        return id;
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int i) {
        Expense ci = expenses.get(i);
        viewHolder.vName.setText(ci.getName());
        viewHolder.vDescription.setText(ci.getDescription());
        viewHolder.vCreatedAt.setText(ci.getCreated_at());
        viewHolder.vCreatedBy.setText(ci.getCreated_by());
        viewHolder.vThumbnail.setImageDrawable(Utilities.generateNameImage(ci.getName()));


        viewHolder.vName.setTypeface(Fonting.getFont(ctx, Fonting.KEY_LIGHT));
        viewHolder.vDescription.setTypeface(Fonting.getFont(ctx, Fonting.KEY_LIGHT));
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card_layout, viewGroup, false);
        ctx = viewGroup.getContext();


        return new MyViewHolder(itemView);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/ {
        protected TextView vName;
        protected TextView vDescription;
        protected TextView vCreatedAt;
        protected TextView vCreatedBy;
        protected ImageView vThumbnail;

        public MyViewHolder(View v) {
            super(v);
            vName = (TextView) v.findViewById(R.id.txName);
            vDescription = (TextView) v.findViewById(R.id.txDescription);
            vCreatedAt = (TextView) v.findViewById(R.id.txCreatedAt);
            vCreatedBy = (TextView) v.findViewById(R.id.txCreatedBy);
            vThumbnail = (ImageView) v.findViewById(R.id.imgThumbnail);

        }
    }


}