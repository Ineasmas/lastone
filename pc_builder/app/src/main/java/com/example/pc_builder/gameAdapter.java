package com.example.pc_builder;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.text.DecimalFormat;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pc_builder.Game;
import com.example.pc_builder.Data;
import com.example.pc_builder.R;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class gameAdapter extends RecyclerView.Adapter<gameAdapter .ViewHolder> {

    private Context context;
    private static OnItemClickListener itemClickListener;
    private List<Game> dataList; // List для хранения данных

    public gameAdapter (Context context) {
        this.context = context;
        dataList = new ArrayList<>();
    }

    @Override
    public int getItemCount() {
        return dataList.size(); // Возвращаем размер списка данных
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gameslot, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Game game = dataList.get(position);
        TextView textView;
        textView = holder.itemView.findViewById(R.id.title);
        textView.setText(game.getName());
        ImageView imageView = holder.itemView.findViewById(R.id.image);
        Picasso.get().load(game.getImagePath()).into(imageView);


    }


    public interface OnItemClickListener {
        void onItemClick(int position);
    }
    public void setItemClickListener(OnItemClickListener listener) {
        this.itemClickListener = listener;
    }

    public void addData(Game data) {
        dataList.add(data);

        notifyDataSetChanged(); // Уведомляем RecyclerView об изменении данных
    }
    public void clearData() {
        dataList.clear();
        notifyDataSetChanged();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder {


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });
        }
    }
}
