package com.n0texpecterr0r.rhapsody.model;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.MediaStore.Images.Media;
import com.n0texpecterr0r.rhapsody.SelectConfig;
import com.n0texpecterr0r.rhapsody.bean.Floder;
import com.n0texpecterr0r.rhapsody.bean.ImageType;
import com.n0texpecterr0r.rhapsody.dao.GalleryDao;
import com.n0texpecterr0r.rhapsody.view.SelectView;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;

/**
 * @author Created by Nullptr
 * @date 2018/7/26 9:28
 * @describe 加载数据处理的Model层
 */
public class SelectModel {

    private GalleryDao mGalleryDao;     // Dao层，与ContentProvider交互，获取Cursor
    private SelectConfig mConfig;       // 选择配置
    private SelectView mSelectView;     // 选择图片的View的接口，用于回调数据

    public SelectModel(ContentResolver resolver, SelectView selectView) {
        mGalleryDao = new GalleryDao(resolver);
        mConfig = SelectConfig.getInstance();
        mSelectView = selectView;
    }

    /**
     * 获取文件夹列表，通过接口回调
     */
    public void getFloderList() {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                List<Floder> floders = new ArrayList<>();   // 用于返回文件夹列表
                Cursor cursor = mGalleryDao.getGalleryCursor();
                // 防止重复扫描路径，用一个set保存
                Set<String> dirPaths = new HashSet<>();
                while (cursor.moveToNext()) {
                    // 扫描图片，获取图片路径
                    String path = cursor.getString(cursor.getColumnIndex(Media.DATA));  //获取图片路径
                    File parentFile = new File(path).getParentFile();

                    // 获取父文件夹
                    if (parentFile == null) {
                        continue;
                    }
                    String dirPath = parentFile.getAbsolutePath();
                    Floder floder = null;

                    // 检查是否已经保存
                    if (dirPaths.contains(dirPath)) {
                        // 已经保存过，不再继续添加
                        continue;
                    } else {
                        //未保存，添加并初始化floder
                        dirPaths.add(dirPath);
                        floder = new Floder();
                        floder.setDir(dirPath);
                        floder.setCoverPath(path);
                    }

                    // 获取图片个数
                    if (parentFile.list() == null) {
                        // 可能为空
                        continue;
                    }
                    // 过滤文件名，获取图片个数
                    int picCount = parentFile.list(new ImageNameFilter(mConfig.imageTypes)).length;

                    floder.setPictureCount(picCount);
                    floders.add(floder);
                }
                cursor.close(); // 关闭cursor
                mSelectView.onFloder(floders);
            }
        });
    }

    /**
     * 异步获取指定文件夹的图片路径列表
     *
     * @param floder 指定文件夹
     */
    public void getImageFromFloderSync(final Floder floder) {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                File floderFile = new File(floder.getDir());
                String[] imageNames = floderFile.list(new ImageNameFilter(mConfig.imageTypes));
                List<String> paths = new ArrayList<>();
                for (String imageName : imageNames) {
                    paths.add(floder.getDir() + "/" + imageName);
                }
                mSelectView.onImages(paths);
            }
        });
    }

    /**
     * 同步获取指定文件夹的图片路径列表
     *
     * @param floder 指定文件夹
     * @return 该文件夹包含所有图片路径
     */
    public List<String> getImageFromFloder(final Floder floder) {

        File floderFile = new File(floder.getDir());
        String[] imageNames = floderFile.list(new ImageNameFilter(mConfig.imageTypes));
        List<String> paths = new ArrayList<>();
        for (String imageName : imageNames) {
            paths.add(floder.getDir() + "/" + imageName);
        }
        return paths;
    }

    /**
     * 获取文件夹列表中所有图片的路径列表
     *
     * @param floders 文件夹的集合
     */
    public void getImageFromFloderList(final List<Floder> floders) {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                List<String> paths = new ArrayList<>();
                for (Floder floder : floders) {
                    List<String> tempList = getImageFromFloder(floder);
                    paths.addAll(tempList);
                }
                mSelectView.onImages(paths);
            }
        });
    }

    /**
     * 图片名称过滤器
     */
    class ImageNameFilter implements FilenameFilter {
        private Set<ImageType> mImageTypes;

        public ImageNameFilter(Set<ImageType> imageTypes){
            mImageTypes = imageTypes;
        }

        @Override
        public boolean accept(File dir, String name) {
            // 遍历所有选择的后缀名
            for (ImageType imageType : mImageTypes) {
                for (String extension : imageType.getExtensions()) {
                    if (name.endsWith(extension)) {
                        //如果是以该后缀名结尾，返回true
                        return true;
                    }
                }
            }
            return false;
        }
    }
}
