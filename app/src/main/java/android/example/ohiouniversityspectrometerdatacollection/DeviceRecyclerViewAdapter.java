package android.example.ohiouniversityspectrometerdatacollection;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class DeviceRecyclerViewAdapter extends RecyclerView.Adapter<DeviceRecyclerViewAdapter.ViewHolder>{
    private static final String TAG = "DeviceRecyclerView";

    private ArrayList<BluetoothDevice> mDevices;
    private Context mContext;

    // Provide a reference to the view for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView deviceName;
        RelativeLayout parentLayout;

        private ViewHolder(@NonNull View itemView) {
            super(itemView);
            deviceName = itemView.findViewById(R.id.device_name);
            parentLayout = itemView.findViewById(R.id.parent_layout);
        }
    }

    public DeviceRecyclerViewAdapter(Context context, ArrayList<BluetoothDevice> devices) {
        mDevices = devices;
        mContext = context;
    }

    // Create new views (invoked by the layout manager
    @NonNull
    @Override
    public DeviceRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
       // Create a new view
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_listitem, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final DeviceRecyclerViewAdapter.ViewHolder viewHolder, int i) {
        Log.d(TAG, "onBindViewHolder: called");

        if (mDevices.get(i).getName() == null) {
            viewHolder.deviceName.setText(mDevices.get(i).getAddress());
        } else {
            viewHolder.deviceName.setText(mDevices.get(i).getName());
        }

        viewHolder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: item selected: "
                        + mDevices.get(viewHolder.getAdapterPosition()).getName());

                Toast.makeText(mContext,
                        mDevices.get(viewHolder.getAdapterPosition()).getName(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mDevices.size();
    }


}
