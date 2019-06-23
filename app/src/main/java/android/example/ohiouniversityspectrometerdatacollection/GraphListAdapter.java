package android.example.ohiouniversityspectrometerdatacollection;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.Date;
import java.util.List;

public class GraphListAdapter extends RecyclerView.Adapter<GraphListAdapter.GraphViewHolder> {
    private GraphClickCallback mGraphClickCallback;

    private final LayoutInflater mInflater;
    //private List<Integer> mIds;  Cached copy of ids
    private List<String> mNames;
    private List<Date> mDates;

    GraphListAdapter(Context context, GraphClickCallback graphClickCallback) {
        mInflater = LayoutInflater.from(context);
        mGraphClickCallback = graphClickCallback;
    }

    @NonNull
    @Override
    public GraphViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, final int i) {
        final View itemView = mInflater.inflate(R.layout.saved_graph_listitem, viewGroup, false);

        return new GraphViewHolder(itemView);
    }



    public void onBindViewHolder(@NonNull GraphViewHolder viewHolder, int i) {
        if (mDates != null && mNames != null) {
            String currentName = mNames.get(i);
            Date currentDate = mDates.get(i);

            viewHolder.nameView.setText(currentName);
            viewHolder.dateView.setText(currentDate.toString());

        } else {
            // Covers the case of data not being ready yet.
            viewHolder.nameView.setText("");
            viewHolder.dateView.setText("0/0/0");
        }

    }

    void setMetaData(List<String> names, List<Date> dates) {
        mNames = names;
        mDates = dates;
    }

    void setDates(List<Date> dates) {
        mDates = dates;
    }

    void setNames(List<String> names) {
        mNames = names;
    }


    @Override
    public int getItemCount() {
        if (mDates != null)
            return mDates.size();
        else
            return 0;
    }

    public Date getDateAtPosition (int position) {
        return mDates.get(position);
    }

    class GraphViewHolder extends RecyclerView.ViewHolder {
        private final TextView nameView;
        private final TextView dateView;

        private GraphViewHolder(View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.text_view_spectrum_name);
            dateView = itemView.findViewById(R.id.text_view_spectrum_date);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mGraphClickCallback.onGraphClick(mDates.get(getAdapterPosition()));
                }
            });
        }
    }
}
