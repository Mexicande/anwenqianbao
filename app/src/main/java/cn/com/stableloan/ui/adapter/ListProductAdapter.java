package cn.com.stableloan.ui.adapter;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.coorchice.library.SuperTextView;

import java.util.List;

import cn.com.stableloan.R;
import cn.com.stableloan.model.Class_ListProductBean;
import cn.com.stableloan.model.home.Hot_New_Product;
import cn.com.stableloan.utils.LogUtils;
import cn.com.stableloan.view.countdownview.CountdownView;


/**
 * Created by apple on 2017/5/22.
 */

public class ListProductAdapter  extends BaseQuickAdapter<Hot_New_Product.DataBean,BaseViewHolder>{
    private CountdownView mCountdownView;
    public ListProductAdapter(List<Hot_New_Product.DataBean> data) {
        super(R.layout.news_product_trem, data);
    }


    @Override
    protected void convert(BaseViewHolder helper, Hot_New_Product.DataBean item) {

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.NONE);

        Glide.with(mContext).load(item.getProduct_logo())
                    .apply(options).into((ImageView) helper.getView(R.id.ic_product_logo));

        SuperTextView view = (SuperTextView) helper.getView(R.id.label1);
        SuperTextView view1 = (SuperTextView) helper.getView(R.id.label2);
        SuperTextView view2 = (SuperTextView) helper.getView(R.id.label3);
        SuperTextView view3 = (SuperTextView) helper.getView(R.id.label4);
        TextView view5 = (TextView) helper.getView(R.id.shengluehao);
        View news = helper.getView(R.id.iv_news);
        View hots = helper.getView(R.id.iv_hots);
        if(item.getActivity()==1){
                hots.setVisibility(View.VISIBLE);
            }else {
            hots.setVisibility(View.GONE);
        }
        if(item.getOnline()==1){
            news.setVisibility(View.VISIBLE);
            }else {
            news.setVisibility(View.GONE);
        }


        if(item.getLabels()!=null){
            int size = item.getLabels().size();
            List<Hot_New_Product.DataBean.LabelsBean> lables = item.getLabels();
            switch (size){
                case 0:
                    view.setVisibility(View.GONE);
                    view1.setVisibility(View.GONE);
                    view2.setVisibility(View.GONE);
                    view3.setVisibility(View.GONE);
                    view5.setVisibility(View.GONE);
                    break;
                case 1:
                    view.setVisibility(View.VISIBLE);
                    view.setTextColor(Color.parseColor(lables.get(0).getFont()));
                    view.setStrokeColor(Color.parseColor(lables.get(0).getFont()));
                    view.setText(lables.get(0).getName());

                    view1.setVisibility(View.GONE);
                    view2.setVisibility(View.GONE);
                    view3.setVisibility(View.GONE);
                    view5.setVisibility(View.GONE);

                    break;
                case 2:
                    view.setVisibility(View.VISIBLE);
                    view.setTextColor(Color.parseColor(lables.get(0).getFont()));
                    view.setStrokeColor(Color.parseColor(lables.get(0).getFont()));
                    view.setText(lables.get(0).getName());
                    view1.setVisibility(View.VISIBLE);
                    view1.setTextColor(Color.parseColor(lables.get(1).getFont()));
                    view1.setStrokeColor(Color.parseColor(lables.get(1).getFont()));
                    view1.setText(lables.get(1).getName());

                    view2.setVisibility(View.GONE);
                    view3.setVisibility(View.GONE);
                    view5.setVisibility(View.GONE);

                    break;
                case 3:
                    view.setVisibility(View.VISIBLE);
                    view.setTextColor(Color.parseColor(lables.get(0).getFont()));
                    view.setStrokeColor(Color.parseColor(lables.get(0).getFont()));
                    view.setText(lables.get(0).getName());
                    view1.setVisibility(View.VISIBLE);
                    view1.setTextColor(Color.parseColor(lables.get(1).getFont()));
                    view1.setStrokeColor(Color.parseColor(lables.get(1).getFont()));
                    view1.setText(lables.get(1).getName());
                    view2.setVisibility(View.VISIBLE);
                    view2.setTextColor(Color.parseColor(lables.get(2).getFont()));
                    view2.setStrokeColor(Color.parseColor(lables.get(2).getFont()));
                    view2.setText(lables.get(2).getName());


                    view3.setVisibility(View.GONE);
                    view5.setVisibility(View.GONE);
                    break;
                case 4:
                    view.setVisibility(View.VISIBLE);
                    view.setTextColor(Color.parseColor(lables.get(0).getFont()));
                    view.setStrokeColor(Color.parseColor(lables.get(0).getFont()));
                    view.setText(lables.get(0).getName());

                    view1.setVisibility(View.VISIBLE);
                    view1.setTextColor(Color.parseColor(lables.get(1).getFont()));
                    view1.setStrokeColor(Color.parseColor(lables.get(1).getFont()));
                    view1.setText(lables.get(1).getName());


                    view2.setVisibility(View.VISIBLE);
                    view2.setTextColor(Color.parseColor(lables.get(2).getFont()));
                    view2.setStrokeColor(Color.parseColor(lables.get(2).getFont()));
                    view2.setText(lables.get(2).getName());

                    view3.setVisibility(View.VISIBLE);
                    view3.setTextColor(Color.parseColor(lables.get(3).getFont()));
                    view3.setStrokeColor(Color.parseColor(lables.get(3).getFont()));
                    view3.setText(lables.get(3).getName());

                    view5.setVisibility(View.GONE);
                default:
                    view.setVisibility(View.VISIBLE);
                    view.setTextColor(Color.parseColor(lables.get(0).getFont()));
                    view.setStrokeColor(Color.parseColor(lables.get(0).getFont()));
                    view.setText(lables.get(0).getName());

                    view1.setVisibility(View.VISIBLE);
                    view1.setTextColor(Color.parseColor(lables.get(1).getFont()));
                    view1.setStrokeColor(Color.parseColor(lables.get(1).getFont()));
                    view1.setText(lables.get(1).getName());

                    view2.setVisibility(View.VISIBLE);
                    view2.setTextColor(Color.parseColor(lables.get(2).getFont()));
                    view2.setStrokeColor(Color.parseColor(lables.get(2).getFont()));
                    view2.setText(lables.get(2).getName());

                    view3.setVisibility(View.VISIBLE);
                    view3.setTextColor(Color.parseColor(lables.get(3).getFont()));
                    view3.setStrokeColor(Color.parseColor(lables.get(3).getFont()));
                    view3.setText(lables.get(3).getName());
                    view5.setVisibility(View.VISIBLE);
                    break;
            }
        }
            helper.setText(R.id.product_list_desc,item.getProduct_introduction())
                    .setText(R.id.tv_Pname,item.getPname());

    }

}
