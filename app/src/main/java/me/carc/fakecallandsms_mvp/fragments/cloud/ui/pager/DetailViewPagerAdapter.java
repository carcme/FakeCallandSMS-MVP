package me.carc.fakecallandsms_mvp.fragments.cloud.ui.pager;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.bumptech.glide.Glide;

import java.util.List;

import me.carc.fakecallandsms_mvp.R;
import me.carc.fakecallandsms_mvp.common.ImageSize;
import me.carc.fakecallandsms_mvp.common.utils.ViewUtils;
import me.carc.fakecallandsms_mvp.databinding.CloudDetailViewBinding;
import me.carc.fakecallandsms_mvp.fragments.cloud.data.model.Photo;
import me.carc.fakecallandsms_mvp.fragments.cloud.ui.DetailSharedElementEnterCallback;


/**
 * Adapter for paging detail views.
 */

public class DetailViewPagerAdapter extends PagerAdapter {

    private final List<Photo> allPhotos;
    private final LayoutInflater layoutInflater;
    private final Activity host;
    private DetailSharedElementEnterCallback sharedElementCallback;

    public DetailViewPagerAdapter(@NonNull Activity activity, @NonNull List<Photo> photos,
                                  @NonNull DetailSharedElementEnterCallback callback) {
        layoutInflater = LayoutInflater.from(activity);
        allPhotos = photos;
//        photoWidth = activity.getResources().getDisplayMetrics().widthPixels;
        host = activity;
        sharedElementCallback = callback;
    }

    @Override
    public int getCount() {
        if(allPhotos != null)
            return allPhotos.size();
        return 0;
    }

    public Photo getPhoto(int index) {
        return allPhotos.get(index);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        CloudDetailViewBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.cloud_detail_view, container, false);
        binding.setData(allPhotos.get(position));

//        binding.toggleFavourite.setOnClickListener(favListener);
        setFavIcon(binding.toggleFavourite, checkIsFavourite(allPhotos.get(position)));

        onViewBound(binding);
        binding.executePendingBindings();
        container.addView(binding.getRoot());
        return binding;
    }

    private boolean checkIsFavourite(Photo image) {

        for(Photo photo : FavDB.getFavDB().getList()) {
            if(photo.id == image.id)
                return true;
        }
        return false;
    }

    private void setFavIcon(ImageButton btn, boolean isFav) {
        if(isFav)
            btn.setImageDrawable(ViewUtils.changeIconColour(host, R.drawable.ic_heart, R.color.colorAccent));
        else
            btn.setImageDrawable(ViewUtils.changeIconColour(host, R.drawable.ic_heart, android.R.color.white));
    }

/*

    private View.OnClickListener  favListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ImageButton btn = (ImageButton) v;
            Photo image = getPhoto(pos);

            Log.d("DEAD", "onClick: " + image.id);

            if(checkIsFavourite()) {
                setFavIcon(btn, false);
                FavDB.getFavDB().del(image);
            } else {
                setFavIcon(btn, true);
                FavDB.getFavDB().add(image);
            }

            List ss = FavDB.getFavDB().getList();
            Log.d("DEAD", "onClick: " + ss.size());
        }
    };
*/

    private void onViewBound(CloudDetailViewBinding binding) {
        Glide.with(host)
                .load(binding.getData().getPhotoUrl(ImageSize.SCREEN[0])) /* photoWidth     ImageSize.SCREEN[0]  */
                .placeholder(R.color.placeholder)
                .override(ImageSize.SCREEN[0], ImageSize.SCREEN[1])
                .into(binding.photo);
    }



    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        if (object instanceof CloudDetailViewBinding) {
            sharedElementCallback.setBinding((CloudDetailViewBinding) object);
        }
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return object instanceof CloudDetailViewBinding
                && view.equals(((CloudDetailViewBinding) object).getRoot());
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(((CloudDetailViewBinding) object).getRoot());
    }
}
