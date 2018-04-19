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
import com.fravier.travel.models.Users;
import com.fravier.travel.textdrawable.TextDrawable;
import com.fravier.travel.textdrawable.util.ColorGenerator;

import java.util.List;

/**
 * Created by francis on 21/04/2016.
 */
public class UserRecyclerAdapter extends RecyclerView.Adapter<UserRecyclerAdapter.MyViewHolder> {

    private List<Users> users;
    public Context ctx;

    public UserRecyclerAdapter(List<Users> users) {
        this.users = users;
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    @Override
    public long getItemId(int position) {
        long id = users.get(position).getId();

        return id;
    }

    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int i) {
        Users ci = users.get(i);
        viewHolder.vName.setText(ci.getFirst_name()+" "+ci.getLast_name());
        viewHolder.vDescription.setText(ci.getLast_login());
        viewHolder.vCreatedAt.setText(ci.getCreated_at());
        viewHolder.vCreatedBy.setText("");
        viewHolder.vThumbnail.setImageDrawable(generateNameImage(ci.getFirst_name()+" "+ci.getLast_name()));


        viewHolder.vName.setTypeface(Fonting.getFont(ctx, Fonting.KEY_LIGHT));
        viewHolder.vDescription.setTypeface(Fonting.getFont(ctx, Fonting.KEY_LIGHT));
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.card_layout_users, viewGroup, false);
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

    public TextDrawable generateNameImage(String fullname) {
        ColorGenerator generator = ColorGenerator.DEFAULT;
        int color = generator.getRandomColor();

        TextDrawable.IBuilder builder = TextDrawable.builder()
                .beginConfig()
                .withBorder(1)
                .endConfig()
                .rect();
        try {
            String firstname = fullname.split(" ")[0];
            String secondname = fullname.split(" ")[1];

            char letter1 = firstname.toUpperCase().charAt(0);
            char letter2 = secondname.toUpperCase().charAt(0);

            TextDrawable drawable = builder.build(letter1 + "" + letter2, color);
            return drawable;
        } catch (Exception ex) {
            char letter1 = fullname.toUpperCase().charAt(0);
            TextDrawable drawable = builder.build(letter1 + "", color);
            return drawable;
        }
    }

}