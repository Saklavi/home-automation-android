package com.wolty.homeautomation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.wolty.homeautomation.deviceModel.Device;
import com.wolty.homeautomation.userModel.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class DevicesAdapter extends ArrayAdapter<Device> {
    private Context mContext;
    private Utils utils;
    private RequestQueue requestQueue;
    //private Switch enabled;
    //private ArrayList<Device> devices;
    //protected Device device;

    public DevicesAdapter(@NonNull Context context, @NonNull ArrayList<Device> devices) {
        super(context, 0, devices);
        mContext = context;
        requestQueue = Volley.newRequestQueue(mContext);
        utils = new Utils(mContext);
        //this.devices = devices;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Device device = getItem(position);
        //Toast.makeText(mContext, String.format("%s", position), Toast.LENGTH_SHORT).show();
        int itemType = getItemViewType(position);

        if(convertView == null) {
            if(itemType == 1) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.switch_device, parent, false);
            } else if(itemType == 2) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.switchpc_device, parent, false);
            }
        }

        TextView name = convertView.findViewById(R.id.name);
        TextView type = convertView.findViewById(R.id.type);

        if(itemType == 1) {
            final Switch enabled = convertView.findViewById(R.id.enabled);
            enabled.setChecked(intToBool(device.status));

            enabled.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                            Request.Method.GET,
                            String.format(utils.getBaseUrl() + "/devices/%s/status/%s", device.id, enabled.isChecked() ? 1 : 0),
                            null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        JSONObject dev = response.getJSONObject("device");
                                        int status = dev.getInt("status");
                                        enabled.setChecked(intToBool(status));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    Toast.makeText(mContext, response.toString(), Toast.LENGTH_SHORT).show();

                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(mContext, error.toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                    ) {
                        @Override
                        public Map<String, String> getHeaders() {
                            return utils.setHeaders(User.getToken());
                        }
                    };

                    requestQueue.add(jsonObjectRequest);
                }
            });
        } else if(itemType == 2) {
            Button enableBtn = convertView.findViewById(R.id.enableBtn);
            enableBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                            Request.Method.GET,
                            String.format(utils.getBaseUrl() + "/devices/%s/status/%s", device.id, 1),
                            null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Toast.makeText(mContext, response.toString(), Toast.LENGTH_SHORT).show();
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(mContext, error.toString(), Toast.LENGTH_SHORT).show();
                                }
                            }
                    ) {
                        @Override
                        public Map<String, String> getHeaders() {
                            return utils.setHeaders(User.getToken());
                        }
                    };

                    requestQueue.add(jsonObjectRequest);
                }
            });
        }

        name.setText(device.name);
        type.setText(device.type);

        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        //return super.getItemViewType(position);
        String device_type;
        device_type = getItem(position).type;

        switch(device_type) {
            case "SwitchPC":
                return 2;
            default:
                return 1;
        }
    }

    private boolean intToBool(int status) {
        if(status == 1) return true;
        else return false;
    }
}