package com.example.george.sportnews;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.ItemViewHolder> {

    private List<MainActivity.Events> newsList;
    public Integer selected_id=-1;
    Context context;

    public NewsListAdapter(Context context, MainActivity mainActivity, List<MainActivity.Events> listNews) {
        this.context = context;
        newsList = listNews;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_list_adater, parent, false);
        return new NewsListAdapter.ItemViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, int position) {

        MainActivity.Events item = newsList.get(position);
        holder.article = item.article;
        if(position == selected_id)
        {
            holder.left.setBackgroundResource(R.color.colorAccent);
        }
        else
        {
            holder.left.setBackgroundResource(R.color.white);
        }

        if(item.descritionIsOn)
            holder.descrition.setVisibility(View.VISIBLE);
        else
            holder.descrition.setVisibility(View.GONE);
        if(item.category.equalsIgnoreCase(MainActivity.selectedCategory) || MainActivity.selectedCategory.equalsIgnoreCase("")) {
            holder.whenDate.setText(item.time);
            holder.titleNews.setText(item.title);
            holder.descritionNews.setText(item.preview);
            holder.coefficient.setText(item.coefficient);
            holder.place.setText(item.place);


            holder.showDescrition.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    newsList.get(holder.getAdapterPosition()).descritionIsOn = !newsList.get(holder.getAdapterPosition()).descritionIsOn;
                    notifyItemChanged(holder.getAdapterPosition());
                }
            });

            holder.left.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selected_id= (int)holder.getAdapterPosition();
                    Intent intent = new Intent(MainActivity.context, DisplayNews.class);
                    intent.putExtra("url", holder.article);
                    context.getApplicationContext().startActivity(intent);
                    //open new activity
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }


    public class ItemViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener{

        private RelativeLayout root_layout;
        private RelativeLayout left;
        private  LinearLayout descrition;
        private TextView whenDate;
        private TextView titleNews;
        private TextView place;
        private TextView coefficient;
        private TextView descritionNews;
        private String article;

        public View marker;
        private   RelativeLayout showDescrition;


        private ItemViewHolder(final View v) {
            super(v);
            root_layout = v.findViewById(R.id.root_layout);
            left = v.findViewById(R.id.container);
            whenDate = v.findViewById(R.id.whenDate);
            titleNews = v.findViewById(R.id.titleNews);
            place = v.findViewById(R.id.place);
            coefficient = v.findViewById(R.id.coefficient);
            descritionNews = v.findViewById(R.id.descritionNews);
            showDescrition = v.findViewById(R.id.showDescrition);
            descrition = v.findViewById(R.id.descrition);
        }

        @Override
        public void onClick(View view) {
            descrition.setVisibility(View.VISIBLE);
        }
    }

}
