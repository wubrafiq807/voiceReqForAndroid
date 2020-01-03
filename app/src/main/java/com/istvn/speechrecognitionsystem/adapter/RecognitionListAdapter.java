package com.istvn.speechrecognitionsystem.adapter;


import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.istvn.speechrecognitionsystem.R;

import com.istvn.speechrecognitionsystem.activity.RecognitionView;

import com.istvn.speechrecognitionsystem.model.LoginResponse;

import com.istvn.speechrecognitionsystem.model.RecognitionListResponse;
import com.istvn.speechrecognitionsystem.model.ResultList;
import com.istvn.speechrecognitionsystem.network.GetDataService;
import com.istvn.speechrecognitionsystem.network.RetrofitClientInstance;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecognitionListAdapter extends RecyclerView.Adapter<RecognitionListAdapter.CustomViewHolder> {


    private RecognitionListResponse recognitionObject;
    private List<ResultList> recognitionResult;
    private Context context;
    private ProgressDialog progressDialog;

    public RecognitionListAdapter(Context ctx, RecognitionListResponse model) {
        this.context = ctx;
        this.recognitionObject = model;
        this.recognitionResult = model.getError() ? null : model.getResult();
        this.progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(context.getResources().getString(R.string.loading));
    }

    /**
     * Called when RecyclerView needs a new RecyclerView.ViewHolder of the given type to represent an item.
     * 【新しい見えるリストitemのためにこれははじまります。】
     *
     * @param viewGroup -> A ViewGroup is a special view that can contain other views (called children.)
     *                  The view group is the base class for layouts and views containers.
     *                  【これは特殊(とくしゅ)ビューです。このビューのなかにほかのビューがあります。これはルートビューです。】
     * @param i         -> Default Value (viewType)【ビュータイプです。】
     * @return -> The created view【さくせいしたビューです。】
     */
    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        /**
         * Is a class that Instantiates a layout XML file into
         * its corresponding View objects
         * 【このクラスはlayoutとビューのかんけあるオブジェクトを見えることをできます。】
         */
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());

        //The layout resource ID of the layout to inflate.【layoutのリソースID inflateのために】
        View view = layoutInflater.inflate(R.layout.recognition_list_single_item, viewGroup, false);
        return new CustomViewHolder(view);
    }


    /**
     * Called by RecyclerView to display the data at the specified position.
     * This method should update the contents of the itemView to reflect
     * the item at the given position.
     * 【この方法はRecyclerViewは呼ば（よば）れられます、データをみえるのために特定の位置(とくていのいち)に】
     *
     * @param customViewHolder -> Hold the view【ビューがホールドをします。】
     * @param i                -> View Position【ビューのポジション】
     */
    @Override
    public void onBindViewHolder(@NonNull final CustomViewHolder customViewHolder, final int i) {

        if (!recognitionObject.getError()) {
            customViewHolder.recognitionDate.setText(recognitionResult.get(i).getCreatedDate());
            customViewHolder.recognitionText.setText(recognitionResult.get(i).getText());

            customViewHolder.recognitionDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Log.d("RRR", "onClick: Delete " + i);
                    removeItemAlert(customViewHolder, i);
                    //removeItem(customViewHolder, i);
                }
            });

            customViewHolder.recognitionView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Log.d("RRR", "onClick: View " + i);
                    recognitionView(i);
                }
            });
        } else {

        }
    }

    /**
     * Recognition View Method
     *
     * @param position -> Position of the view
     */
    private void recognitionView(int position) {

        context.startActivity(new Intent(context.getApplicationContext(),
                RecognitionView.class).putExtra("voice_req", recognitionResult.get(position).getVoiceReqId()));
    }

     /** Alert Dialogue For Remove Item
     * @param customViewHolder
     * @param i -> Position
     */
    private void removeItemAlert(final CustomViewHolder customViewHolder, final int i) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setMessage(context.getResources().getString(R.string.delete_confirmation_message));
        dialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Remove The Item
                removeItem(customViewHolder, i);
            }
        });

        dialog.setNegativeButton(context.getResources().getString(R.string.no),null);// if user press on cancel button we will close alert dialog
        dialog.show();// show alert dialog

    }

    private void removeItem(CustomViewHolder rowView, final int position) {

        final Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_out_right);
        rowView.mView.startAnimation(animation);
        Handler handle = new Handler();
        handle.postDelayed(new Runnable() {
            @Override
            public void run() {
                recognitionResult.remove(position);
                notifyItemChanged(position);
                notifyDataSetChanged();
                notifyItemRangeChanged(position, recognitionObject.getError() ? 0 : recognitionObject.getResult().size());

            }
        }, 250);

        // Remove Voice Recognition From Server
        networkCallRemoveItem(recognitionResult.get(position).getVoiceReqId());
    }

    /**
     * Remove Voice Recognition From Server
     * @param voiceReqId
     */
    private void networkCallRemoveItem(String voiceReqId) {
        progressDialog.show();
        GetDataService service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        Call<LoginResponse> call = service.deleteVoiceRecognition("/api-v1/voiceReqs/" + voiceReqId + "/");
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                progressDialog.dismiss();
                Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                progressDialog.dismiss();
            }
        });
    }

    @Override
    public int getItemCount() {
        return recognitionObject.getError() ? 0 : recognitionObject.getResult().size();
    }

    /**
     * Initialize the custom View
     */
    public static class CustomViewHolder extends RecyclerView.ViewHolder {
        public final View mView;

        private Button recognitionView;
        private Button recognitionDelete;
        private TextView recognitionDate;
        private TextView recognitionText;

        public CustomViewHolder(View itemView) {
            super(itemView);
            mView = itemView;

            recognitionDate = mView.findViewById(R.id.recognitionDateTextView);
            recognitionText = mView.findViewById(R.id.recognitionTextTextView);
            recognitionDelete = mView.findViewById(R.id.recognitionDeleteButton);
            recognitionView = mView.findViewById(R.id.recognitionViewButton);
        }
    }
}
