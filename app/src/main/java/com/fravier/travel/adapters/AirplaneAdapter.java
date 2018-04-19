package com.fravier.travel.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.fravier.travel.R;
import com.fravier.travel.SeatItems.SelectableAdapter;
import com.fravier.travel.global.GlobalVars;

import java.util.ArrayList;

public class AirplaneAdapter extends SelectableAdapter<RecyclerView.ViewHolder> {



    private static class AllViewHolder extends RecyclerView.ViewHolder {

        ImageView imgSeat;
        private final ImageView imgSeatSelected;

        public AllViewHolder(View itemView) {
            super(itemView);
            imgSeat = (ImageView) itemView.findViewById(R.id.img_seat);
            imgSeatSelected = (ImageView) itemView.findViewById(R.id.img_seat_selected);


        }

    }

    private Context mContext;
    private LayoutInflater mLayoutInflater;

    private ArrayList<String> mItems;
    TextView txt_selected;
    ArrayList<String> abstractItemArrayList=new ArrayList<>();

    public AirplaneAdapter(Context context, ArrayList<String> items, TextView txt_selected) {

        mContext = context;
        this.txt_selected=txt_selected;
        mLayoutInflater = LayoutInflater.from(context);
        mItems = items;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mLayoutInflater.inflate(R.layout.list_item_seat, parent, false);
        return new AllViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {


            AllViewHolder holder = (AllViewHolder) viewHolder;

            holder.imgSeat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    toggleSelection(position);
                    if(abstractItemArrayList.contains(mItems))
                    {
                        //this is a deselection

                        abstractItemArrayList.remove(mItems);


                    }else {

                        GlobalVars.steppedSeatNo = mItems.get(position);
                        abstractItemArrayList.add(String.valueOf(mItems));
                    }
                    txt_selected.setText("Book "+ abstractItemArrayList.size()+" seats");

                }
            });

            holder.imgSeatSelected.setVisibility(isSelected(position) ? View.VISIBLE : View.INVISIBLE);

        }
    }


