package com.istvn.speechrecognitionsystem.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.istvn.speechrecognitionsystem.R;
import com.istvn.speechrecognitionsystem.interfaces.ClickListener;
import com.istvn.speechrecognitionsystem.model.Audio;
import com.istvn.speechrecognitionsystem.utility.Utils;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Call list recycler view adapter
 * Handle item click listener
 * View audio item into each view
 */
public class CallListRvAdapter extends RecyclerView.Adapter<CallListRvAdapter.ViewHolder> {

    private ArrayList<Audio> audioList;
    private Context context;
    private ClickListener listener;

    private SparseBooleanArray selected_items;
    private int current_selected_idx = -1;

    /**
     * Constructor
     * @param context: current uses activity
     * @param audioList: all audio list
     */
    public CallListRvAdapter(Context context, ArrayList<Audio> audioList) {
        this.audioList = audioList;
        this.context = context;
        this.listener = (ClickListener) context;
        selected_items = new SparseBooleanArray();
    }

    /**
     * initiate each audio item with our item_audio_view
     * @param viewGroup: view container
     * @param i: audio position
     * @return viewHolder
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());

        View view = layoutInflater.inflate(R.layout.recording_list_single_item, viewGroup, false);
        return new ViewHolder(view);
    }

    /**
     * Bind each view with audio data
     * @param viewHolder: audio view class
     * @param i: audio position
     */
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int i) {

        viewHolder.phoneNo.setText(Utils.getContactName(context, audioList.get(i).getPhoneNo()));
        viewHolder.duration.setText(audioList.get(i).getDuration());
        viewHolder.dateTime.setText(audioList.get(i).getDateTime());

        viewHolder.lyt_parent.setActivated(selected_items.get(i, false));

        if (audioList.get(i).getCallType().equals("IN")){
            viewHolder.callIc.setImageDrawable(context.getResources().getDrawable(R.drawable.call_in));
        }else if(audioList.get(i).getCallType().equals("MANUAL")){
            viewHolder.callIc.setImageDrawable(context.getResources().getDrawable(R.drawable.auto_record_icon));
        }
        else{
            viewHolder.callIc.setImageDrawable(context.getResources().getDrawable(R.drawable.calls_out));
        }

        viewHolder.lyt_parent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listener.onLongClick(i);
                return true;
            }
        });

        viewHolder.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(i);
            }
        });

        toggleCheckedIcon(viewHolder, i);
    }

    private void toggleCheckedIcon(ViewHolder holder, int position) {
        if (selected_items.get(position, false)) {
            holder.lyt_checked.setVisibility(View.VISIBLE);
            if (current_selected_idx == position) resetCurrentIndex();
        } else {
            holder.lyt_checked.setVisibility(View.GONE);
            if (current_selected_idx == position) resetCurrentIndex();
        }
    }

    private void resetCurrentIndex() {
        current_selected_idx = -1;
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>(selected_items.size());
        for (int i = 0; i < selected_items.size(); i++) {
            items.add(selected_items.keyAt(i));
        }
        return items;
    }

    public void removeData(int position) {
        audioList.remove(position);
        resetCurrentIndex();
    }

    public void clearSelections() {
        selected_items.clear();
        notifyDataSetChanged();
    }

    public void deleteItemFromStorage(int position){

        File file = new File(audioList.get(position).getPath());
        file.delete();
    }

    public int getSelectedItemCount() {
        return selected_items.size();
    }

    public void toggleSelection(int pos) {
        current_selected_idx = pos;
        if (selected_items.get(pos, false)) {
            selected_items.delete(pos);
        } else {
            selected_items.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    /**
     * Total size of record
     * @return record size
     */
    @Override
    public int getItemCount() {
        return audioList.size();
    }

    /**
     * Audio view class
     * Used butter knife to inject view
     */
    public class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;

        private TextView phoneNo, duration, dateTime;
        private ImageView callIc;
        private CircularImageView image;
        private RelativeLayout lyt_checked;
        private View lyt_parent;

        public ViewHolder(View itemView) {
            super(itemView);
            this.mView = itemView;

            phoneNo = mView.findViewById(R.id.phoneTv);
            duration = mView.findViewById(R.id.durationTv);
            callIc = mView.findViewById(R.id.callTypeIc);
            dateTime = mView.findViewById(R.id.dateTv);
            image = mView.findViewById(R.id.image);
            lyt_checked = mView.findViewById(R.id.lyt_checked);
            lyt_parent = mView.findViewById(R.id.lyt_parent);
        }
    }
}
