package com.fidoo.user.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;

import com.fidoo.user.R;


public class SliderAdapter extends RecyclerView.Adapter<SliderCard> {

    private final int count;
    private final int[] content;
   // private final View.OnClickListener listener;
    public ClickCart clickCart;

    public SliderAdapter(int[] content, int count, ClickCart clickCart) {
        this.content = content;
        this.count = count;
        this.clickCart = clickCart;
    }

    @Override
    public SliderCard onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.layout_slider_card, parent, false);

       // if (clickCart != null) {

       // }

        return new SliderCard(view);
    }

    @Override
    public void onBindViewHolder(SliderCard holder, int position) {
        holder.setContent(content[position % content.length]);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickCart.cartOnClick(view,position);
            }
        });
    }

    @Override
    public void onViewRecycled(SliderCard holder) {
        holder.clearContent();
    }

    @Override
    public int getItemCount() {
        return count;
    }

     public interface ClickCart{
        public void cartOnClick(View view,int position);
    }

}
