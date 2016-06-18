package com.ln.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ln.app.MainApplication;
import com.ln.model.Company1;
import com.ln.mycoupon.R;
import com.ln.views.CircleImageView;

import java.util.List;

/**
 * Created by luongnguyen on 6/7/16.
 *<></>
 */
public class CompanyAdapter extends RecyclerView.Adapter<CompanyAdapter.ViewHolder> {

    private List<Company1> listCompany;
    private Context mContext;
    private String TAG = getClass().getSimpleName();

    public CompanyAdapter(Context context, List<Company1> company) {
        mContext = context;
        listCompany = company;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_company, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final Company1 item = listCompany.get(position);
        if (item != null) {
            holder.companyName.setText(item.getName());
            holder.companyAddress.setText(item.getAddress());

            if (item.getLogo() != null) {
                Glide.with(mContext).load(MainApplication.convertToBytes(item.getLogo()))
                        .asBitmap()
                        .placeholder(R.drawable.ic_logo_blank)
                        .into(holder.mImgLogo);
            }
        }
    }

    @Override
    public int getItemCount() {
        return listCompany.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView mImgLogo;
        private TextView companyName, companyAddress;

        public ViewHolder(View itemView) {
            super(itemView);

            mImgLogo = (CircleImageView) itemView.findViewById(R.id.app_icon);
            companyName = (TextView) itemView.findViewById(R.id.company_name);
            companyAddress = (TextView) itemView.findViewById(R.id.company_address);
        }
    }
}
