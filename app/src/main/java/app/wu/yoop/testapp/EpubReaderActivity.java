package app.wu.yoop.testapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import app.wu.yoop.testapp.util.Utils;
import app.wu.yoop.testapp.widget.MViewFlipper;
import app.wu.yoop.testapp.widget.WrapWebView;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.domain.TableOfContents;
import nl.siegmann.epublib.epub.EpubReader;

import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static android.os.Environment.getExternalStoragePublicDirectory;

/**
 * Created by wuyan on 2017/7/8 0008.
 */

public class EpubReaderActivity extends AppCompatActivity implements View.OnClickListener,
        AdapterView.OnItemClickListener, MViewFlipper.OnViewFlipperListener {

    private static final int COLOR_DEFAULT = 0xffebf0f2;
    private static final int COLOR_PARCHMENT = 0xffe3ddcb;
    private static final int COLOR_CARE_EYE_GREEN = 0xffcce5d4;
    private static final int COLOR_NIGHT = 0xff262626;
    private static final int BG_COLOR_NIGHT = 0xff444545;
    private static final int BG_COLOR_NORMAL = 0xffffffff;

    LayoutInflater mInflater;

    ListView mListView;
    MViewFlipper mViewFlipper;

    MenuAdapter mMenuAdapter;

    boolean isMenuOpen;

    File mCurrentFile;

    private String basePath;

    private int currentSettingsColor = COLOR_DEFAULT;

    private List<File> mFileList;
    private List<TOCReference> mTOCReferences;
    private List<TOCReference> mFullTOCReferences;
    private int mCurrentIndex;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        requestWindowFeature(Window.FEATURE_ACTION_MODE_OVERLAY);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_epub_reader);

        mInflater = LayoutInflater.from(this);


        mListView = (ListView) findViewById(R.id.listView);
//        mWebView = (WebView) findViewById(R.id.webView);
        mViewFlipper = (MViewFlipper) findViewById(R.id.viewFlipper);
        findViewById(R.id.iv_menu).setOnClickListener(this);
        mViewFlipper.setOnViewFlipperListener(this);

        mFileList = new ArrayList<>();
        mTOCReferences = new ArrayList<>();
        mFullTOCReferences = new ArrayList<>();

        String downloadPath = getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS).getPath();

        File downloadFile = new File(downloadPath);
        if (downloadFile.isDirectory()) {
            File[] files = downloadFile.listFiles();
            for (File file : files) {
                if (!file.isDirectory() && file.getAbsolutePath().endsWith(".epub")) {
                    mFileList.add(file);
                }
            }
        }

        unzipAllFiles();

        mMenuAdapter = new MenuAdapter();
        mListView.setAdapter(mMenuAdapter);
        mListView.setOnItemClickListener(this);

        currentSettingsColor = COLOR_NIGHT;

    }

    private void unzipAllFiles() {
        for (File file : mFileList) {
            String filePath = file.getAbsolutePath();
            String unzipFilePath = filePath.substring(0, filePath.lastIndexOf("."));
            File unzipFile = new File(unzipFilePath);
            if (!unzipFile.exists()) {
                Utils.unzipFile(filePath, unzipFilePath);
            }
        }
    }

    boolean isFirst = true;

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.iv_menu:
                if (isFirst) {
                    toggleMenu();
                    isFirst = false;
                } else {
                    //更换背景颜色
                    Toast.makeText(this, "更换背景颜色", Toast.LENGTH_SHORT).show();
                    int currentIndex = mCurrentIndex;
                    WrapWebView wrapWebView = (WrapWebView) mHtmlViews.get(currentIndex);
                    wrapWebView.onWebBgChanged();
                    for (int i = 0; i < mHtmlViews.size(); i++) {
                        if (i != currentIndex) {
                            WrapWebView webView = (WrapWebView) mHtmlViews.get(i);
                            webView.onWebBgChanged();
                        }
                    }
                }
                break;
            default:
                break;
        }
    }

    private void toggleMenu() {
        isMenuOpen = !isMenuOpen;
        mListView.setVisibility(isMenuOpen ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onItemClick(final AdapterView<?> parent, final View view, final int position, final long id) {
        toggleMenu();
        mCurrentFile = mFileList.get(position);
        loadEpubFile();
    }

    private void loadEpubFile() {
        String filePath = mCurrentFile.getAbsolutePath();
        basePath = "file://" + filePath.substring(0, filePath.lastIndexOf(".")) + "/";
        Book book = null;
        try {
            EpubReader reader = new EpubReader();
            InputStream in = new BufferedInputStream(new FileInputStream(filePath));
            book = reader.readEpub(in);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (book != null) {
            filterEpubFileAbsolutePath(book);
            TableOfContents contents = book.getTableOfContents();
            List<TOCReference> tocReferences = contents.getTocReferences();
            if (tocReferences != null && tocReferences.size() > 0) {
                for (TOCReference resource : tocReferences) {
                    mTOCReferences.add(resource);
                    mFullTOCReferences.add(resource);
                    if (resource.getChildren() != null && resource.getChildren().size() > 0) {
                        for (TOCReference child : resource.getChildren()) {
                            mTOCReferences.add(child);
                        }
                    }
                }
            }
            mViewFlipper.removeAllViews();
            mViewFlipper.addView(getHtmlView(mFullTOCReferences.get(mCurrentIndex)));
            mViewFlipper.addView(getHtmlView(mFullTOCReferences.get(mCurrentIndex + 1)));
            mViewFlipper.addView(getHtmlView(mFullTOCReferences.get(mCurrentIndex + 2)));
            mViewFlipper.setDisplayedChild(0);
            createHtmlView();
        }
    }

    private SparseArray<View> mHtmlViews = new SparseArray<>();

    private void createHtmlView() {
        for (int i = 0; i < mFullTOCReferences.size(); i++) {
            TOCReference reference = mFullTOCReferences.get(i);
            View view = mHtmlViews.get(i);
            if (view == null) {
                view = getHtmlView(reference);
                mHtmlViews.put(i, view);
            }
//            mViewFlipper.addView(view);
//            mViewFlipper.setInAnimation(this, R.anim.left_slip_in);
//            mViewFlipper.setOutAnimation(this, R.anim.left_slip_out);
        }

    }

    private View getHtmlView(TOCReference reference) {
        final WrapWebView mWebView = new WrapWebView(this);

        String url = basePath + reference.getCompleteHref();
        mWebView.loadUrl(url);

        return mWebView;
    }


    private void changePageBackgroundColor(WebView mWebView) {

        String cssName = "normal";
        if (currentSettingsColor == COLOR_NIGHT) {
            cssName = "night";
        } else if (currentSettingsColor == COLOR_CARE_EYE_GREEN) {
            cssName = "eye_care_green";
        } else if (currentSettingsColor == COLOR_PARCHMENT) {
            cssName = "parchment";
        }

        String cssPath = "file:///android_asset/" + cssName + ".css";

        mWebView.loadUrl(getOutCss(cssPath));
    }

    public static String getOutCss(String url) {

        String js = "javascript:var d=document;" +
                "var s=d.createElement('link');" +
                "s.setAttribute('rel', 'stylesheet');" +
                "s.setAttribute('href', '" + url + "');" +
                "d.head.appendChild(s);";
        return js;
    }


    private void filterEpubFileAbsolutePath(Book book) {
        String opfHref = book.getOpfResource().getHref();
        int index = opfHref.lastIndexOf("/");
        if (index != -1) {
            basePath += opfHref.substring(0, index + 1);
        }
    }

    @Override
    public View getNextView() {
        if (mCurrentIndex < mFullTOCReferences.size() - 1) {
            mCurrentIndex++;
        }
        mViewFlipper.setIsLastPage(mCurrentIndex == mFullTOCReferences.size() - 1);
        mViewFlipper.setIsFirstPage(false);
        if  (mCurrentIndex < mFullTOCReferences.size() - 1 && mCurrentIndex > 1) {
            WrapWebView child = (WrapWebView) mViewFlipper.getChildAt(0);
            mViewFlipper.removeView(child);
            String url = basePath + mFullTOCReferences.get(mCurrentIndex + 1).getCompleteHref();
            child.loadUrl(url);
            mViewFlipper.addView(child);
            mViewFlipper.setDisplayedChild(1);
        } else if (mCurrentIndex == 1){
            mViewFlipper.setDisplayedChild(1);
        } else {
            mViewFlipper.setDisplayedChild(2);
        }
//        return mHtmlViews.get(mCurrentIndex);
//        mViewFlipper.showNext();
        return null;
    }

    @Override
    public View getPreviousView() {
        if (mCurrentIndex > 0) {
            mCurrentIndex--;
        }
        mViewFlipper.setIsFirstPage(mCurrentIndex == 0);
        mViewFlipper.setIsLastPage(false);
       if (mCurrentIndex == mFullTOCReferences.size() - 2){
            mViewFlipper.setDisplayedChild(1);
        } else if (mCurrentIndex == 0){
            mViewFlipper.setDisplayedChild(0);
        } else  if (mCurrentIndex != 0 && mCurrentIndex != (mFullTOCReferences.size() - 1)) {
           WrapWebView child = (WrapWebView) mViewFlipper.getChildAt(2);
           mViewFlipper.removeView(child);
           String url = basePath + mFullTOCReferences.get(mCurrentIndex - 1).getCompleteHref();
           child.loadUrl(url);
           mViewFlipper.addView(child, 0);
           mViewFlipper.setDisplayedChild(1);
       }
           return null;
//        mViewFlipper.showPrevious();
//        return null;
    }

    private class MenuAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mFileList.size();
        }

        @Override
        public File getItem(int position) {
            return mFileList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.list_item_file, parent, false);
            }
            TextView tvFile = (TextView) convertView.findViewById(R.id.tv_file);
            File file = getItem(position);

            tvFile.setText(file.getName());

            return convertView;
        }
    }

    @Override
    public boolean dispatchTouchEvent(final MotionEvent ev) {
        System.out.println("==============" + ev.getAction());
        return super.dispatchTouchEvent(ev);
    }
}
