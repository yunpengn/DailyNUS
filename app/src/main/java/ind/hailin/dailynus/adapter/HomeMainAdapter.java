package ind.hailin.dailynus.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ind.hailin.dailynus.R;
import ind.hailin.dailynus.entity.ChatDialogues;
import ind.hailin.dailynus.utils.MyUtils;
import ind.hailin.dailynus.view.SmallSlideMenuView;

/**
 * Created by hailin on 2017/6/2.
 * This is an adapter for recyclerHomeMain
 */

public class HomeMainAdapter extends RecyclerView.Adapter {
    public static final String TAG = "HomeMainAdapter";

    private List<ChatDialogues> list;
    private Map<Integer, Bitmap> bitmapMap;
    private OnItemClickListener listener;

    public HomeMainAdapter() {
        list = new ArrayList<>();
        bitmapMap = new HashMap<>();
    }

    public HomeMainAdapter(List<ChatDialogues> list, Map<Integer, Bitmap> bitmapMap) {
        this.list = list;
        this.bitmapMap = bitmapMap;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_home, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        MyViewHolder holder = (MyViewHolder) viewHolder;

        ChatDialogues chatDialogues = list.get(position);
        if (chatDialogues != null) {
            holder.tvName.setText(chatDialogues.getBak1());

            if (chatDialogues.getMessage() != null)
                holder.tvDesc.setText(chatDialogues.getMessage());
            else if (chatDialogues.getFilename() != null)
                holder.tvDesc.setText(chatDialogues.getFilename());

            holder.tvTime.setText(MyUtils.convertDateToTime(chatDialogues.getUpdatedAt()));

            if (bitmapMap != null && bitmapMap.containsKey(chatDialogues.getSenderId()))
                holder.ivAvatar.setImageBitmap(bitmapMap.get(chatDialogues.getSenderId()));

            if (!chatDialogues.getIsRead())
                holder.ssmv.setMenuShow(Integer.parseInt(chatDialogues.getBak2()), Color.RED);
            else
                holder.ssmv.setMenuShow(Integer.parseInt(chatDialogues.getBak2()));

            holder.ssmv.openMenu(1);
            holder.ssmv.closeNoAnimate();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void insertData(ChatDialogues chatDialogues) {
        for (int i = 0; i < list.size(); i++) {
            if (chatDialogues.getBak1().equals(list.get(i).getBak1())) {
                list.remove(i);
                notifyDataSetChanged();
                break;
            }
        }
        list.add(0, chatDialogues);
        notifyItemInserted(0);
    }

    public void insertRange(@NonNull List<ChatDialogues> subList) {
        for (int i = 0; i < list.size(); i++) {
            for (int j = 0; j < subList.size(); j++) {
                if (subList.get(j).getBak1().equals(list.get(i).getBak1())) {
                    list.remove(i);
                    i--;
                    break;
                }
            }
        }
        notifyDataSetChanged();

        list.addAll(0, subList);
        notifyItemRangeInserted(0, subList.size());
    }

    public void removeData(int position) {
        list.remove(position);
        notifyItemRemoved(position);
    }

    public void addBitmap(Map<Integer, Bitmap> map) {
        this.bitmapMap.putAll(map);
        notifyDataSetChanged();
    }

    public void addBitmap(int id, Bitmap bitmap) {
        bitmapMap.put(id, bitmap);
        notifyDataSetChanged();
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {
        View layoutItem;
        TextView tvName, tvDesc, tvTime;
        ImageView ivAvatar;
        SmallSlideMenuView ssmv;

        private MyViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView) itemView.findViewById(R.id.item_tv_name);
            tvDesc = (TextView) itemView.findViewById(R.id.item_tv_desc);
            tvTime = (TextView) itemView.findViewById(R.id.item_tv_time);
            ivAvatar = (ImageView) itemView.findViewById(R.id.item_iv_avatar);
            ssmv = (SmallSlideMenuView) itemView.findViewById(R.id.item_ssmv);
            layoutItem = itemView.findViewById(R.id.item_layout);

            ssmv.setOnDeleteItemListener(new SmallSlideMenuView.onDeleteItemListener() {
                @Override
                public void onDelete() {
                    removeData(getAdapterPosition());
                }
            });

            layoutItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(list.get(getAdapterPosition()));
                }
            });
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(ChatDialogues chatDialogues);
    }
}
