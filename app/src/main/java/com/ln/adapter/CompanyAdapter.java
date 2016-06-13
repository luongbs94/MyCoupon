package com.ln.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ln.model.Company1;
import com.ln.mycoupon.R;

import java.util.List;

/**
 * Created by luongnguyen on 6/7/16.
 */
public class CompanyAdapter extends RecyclerView.Adapter<CompanyAdapter.ViewHolder> {

    private List<Company1> listCompany;
    private Context mContext;

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
            holder.companyAdress.setText(item.getAddress());

//            Glide.with(mContext).load(MainApplication
//                    .convertToBytes(item.getLogo()))
//                    .asBitmap()
//                    .placeholder(R.drawable.ic_profile)
//                    .into(holder.mImgLogo);
        }
    }

    @Override
    public int getItemCount() {
        return listCompany.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImgLogo;
        private TextView companyName, companyAdress;

        public ViewHolder(View itemView) {
            super(itemView);

            mImgLogo = (ImageView) itemView.findViewById(R.id.app_icon);
            companyName = (TextView) itemView.findViewById(R.id.company_name);
            companyAdress = (TextView) itemView.findViewById(R.id.company_address);
        }
    }
}
