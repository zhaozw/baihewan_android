package com.morln.app.lbstask.ui.image;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.*;
import com.morln.app.lbstask.R;
import com.morln.app.lbstask.controls.XZoomHolder;
import com.morln.app.lbstask.utils.img.ImageLoader;
import com.morln.app.media.image.XImageLocalMgr;
import com.morln.app.system.ui.XBackType;
import com.morln.app.system.ui.XBaseLayer;
import com.morln.app.system.ui.XUIFrame;

import java.util.List;

/**
 * 专门浏览一串图片的界面
 * Created by jasontujun.
 * Date: 12-5-13
 * Time: 下午6:08
 */
public class LImageDetail extends XBaseLayer implements
        AdapterView.OnItemSelectedListener, ViewSwitcher.ViewFactory {

    private static final String TAG = "IMG_DETAIL";

    private ImageSwitcher mSwitcher;
    private XZoomHolder mZoomHolder;
    private Gallery mGallery;

    private List<String> imageUrls;

    /**
     * 构造函数，记得调用setContentView()哦
     *
     * @param uiFrame
     */
    public LImageDetail(XUIFrame uiFrame, List<String> urls, int selectedItemIndex) {
        super(uiFrame);
        this.imageUrls = urls;

        setContentView(R.layout.image_detail);
        mGallery = (Gallery) findViewById(R.id.gallery);
        mZoomHolder = (XZoomHolder) findViewById(R.id.zoom_holder);
        mSwitcher = (ImageSwitcher) findViewById(R.id.switcher);

        mSwitcher.setFactory(this);
        mSwitcher.setInAnimation(AnimationUtils.loadAnimation(getContext(),
                android.R.anim.fade_in));
        mSwitcher.setOutAnimation(AnimationUtils.loadAnimation(getContext(),
                android.R.anim.fade_out));

        mZoomHolder.setOverScroll(false);
        mZoomHolder.setInterceptTouchEvent(true);
        mZoomHolder.setListener(new XZoomHolder.ZoomViewListener() {
            @Override
            public void onSettle(View view) {
            }

            @Override
            public void onFlingLeft(View view) {
                int currentIndex = mGallery.getSelectedItemPosition();
                currentIndex++;
                if(currentIndex >= imageUrls.size()) {
                    Toast.makeText(getContext(), "后面木有图片了~", Toast.LENGTH_SHORT).show();
                    return;
                }
                mZoomHolder.setScale(XZoomHolder.MIN_SCALE, true);
                mGallery.setSelection(currentIndex);
            }

            @Override
            public void onFlingRight(View view) {
                int currentIndex = mGallery.getSelectedItemPosition();
                currentIndex--;
                if(currentIndex < 0) {
                    Toast.makeText(getContext(), "前面木有图片了~", Toast.LENGTH_SHORT).show();
                    return;
                }
                mZoomHolder.setScale(XZoomHolder.MIN_SCALE, true);
                mGallery.setSelection(currentIndex);
            }

            @Override
            public void onFlingUp(View view) {
            }

            @Override
            public void onFlingDown(View view) {
            }

            @Override
            public void onLongPress(View view) {
            }

            @Override
            public void onDoubleClick(View view, int x, int y) {
                if(mZoomHolder.getScale() != XZoomHolder.MIN_SCALE)
                    mZoomHolder.setScale(x, y, XZoomHolder.MIN_SCALE, true);
                else if(mZoomHolder.getScale() != XZoomHolder.MAX_SCALE)
                    mZoomHolder.setScale(x, y, XZoomHolder.MAX_SCALE, true);
            }
        });


        mGallery.setAdapter(new ImageAdapter(getContext()));
        mGallery.setOnItemSelectedListener(this);
        mGallery.setSelection(selectedItemIndex);
    }


    @Override
    public int back() {
        return XBackType.SELF_BACK;
    }

    @Override
    public void onItemSelected(AdapterView parent, View v, int position, long id) {
        String imageUrl = imageUrls.get(position);
        // 异步方式加载图片
        ImageLoader.getInstance().asyncLoadBitmap(getContext(),
                imageUrl, mSwitcher, XImageLocalMgr.ImageSize.SCREEN);
    }

    @Override
    public void onNothingSelected(AdapterView parent) {
    }

    @Override
    public View makeView() {
        ImageView i = new ImageView(getContext());
        i.setBackgroundColor(0xFF000000);
        i.setScaleType(ImageView.ScaleType.FIT_CENTER);
        i.setLayoutParams(new ImageSwitcher.LayoutParams(Gallery.LayoutParams.MATCH_PARENT,
                Gallery.LayoutParams.MATCH_PARENT));
        return i;
    }

    @Override
    public Handler getLayerHandler() {
        return null;
    }

    private class ImageAdapter extends BaseAdapter {

        private Context mContext;

        public ImageAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return imageUrls.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null) {
                convertView =new ImageView(mContext);
            }
            ImageView imageView = (ImageView) convertView;
            imageView.setAdjustViewBounds(true);
            imageView.setLayoutParams(new Gallery.LayoutParams(
                    Gallery.LayoutParams.WRAP_CONTENT, Gallery.LayoutParams.WRAP_CONTENT));

            String imageUrl = imageUrls.get(position);


//            imageView.setImageDrawable(ImageUtil.getSmallLocalImage(getContext(), imageUrl));
//            // 设置图片资源（异步加载）
//            XImageLoader.getInstance().asyncLoadBitmap(
//                    getContext(), imageUrl, imageView,
//                    XImageLocalMgr.ImageSize.SMALL);
//            // 设置图片资源（同步加载）
            ImageLoader.getInstance().syncLoadBitmap(
                    getContext(), imageUrl, imageView,
                    XImageLocalMgr.ImageSize.SMALL);

            return convertView;
        }

    }

}