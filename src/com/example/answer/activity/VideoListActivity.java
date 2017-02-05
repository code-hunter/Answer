package com.example.answer.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.answer.R;
import com.example.answer.bean.VideoBean;
import com.example.answer.util.ConstanUrl;
import com.example.answer.util.MediaHelp;
import com.example.answer.util.VideoSuperPlayer;
import com.example.answer.util.VideoSuperPlayer.VideoPlayCallbackImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.answer.R.id.i_image;


public class VideoListActivity extends Activity {
          private String url = "3-1.mp4";
          private List<VideoBean> mList;
          private ListView mListView;
          private boolean isPlaying;
          private int indexPostion = -1;
          private MAdapter mAdapter;

          //标题控件
          private ImageView leftIv;
          private TextView titleTv;
          private MediaMetadataRetriever mediaMetadata;
          private AssetFileDescriptor fileDescriptor = null;
          private AssetManager assetMg;
          private String title;
          private Bitmap m_bitmp;
          private String m_tvtTime;
          private int mode=0;
          private String videoName=null;

          @Override
          protected void onDestroy() {
                    MediaHelp.release();
                    super.onDestroy();
          }

          @Override
          protected void onResume() {
                    MediaHelp.resume();
                    super.onResume();
          }

          @Override
          protected void onPause() {
                    MediaHelp.pause();
                    super.onPause();
          }

          @Override
          protected void onActivityResult(int requestCode, int resultCode, Intent data) {
                    super.onActivityResult(requestCode, resultCode, data);
                    MediaHelp.getInstance().seekTo(data.getIntExtra("position", 0));
          }

          @Override
          protected void onCreate(Bundle savedInstanceState) {
                    super.onCreate(savedInstanceState);
                    requestWindowFeature(Window.FEATURE_NO_TITLE);
                    setContentView(R.layout.list_video_activity);
                    videoName=getIntent().getStringExtra("videoName");
                    mode=getIntent().getIntExtra("mode",0);
                    //初始化视图
                    initView();
                    mListView = (ListView) findViewById(R.id.list);
                    mList = new ArrayList<VideoBean>();
                    if (mode == 1) {//做题跳转
                              mList.add(new VideoBean(videoName+".mp4"));
                    } else {
                              for (int i = 0; i < ConstanUrl.url.length; i++) {
                                        mList.add(new VideoBean(ConstanUrl.url[i]+".mp4"));
                              }
                    }
                    mAdapter = new MAdapter(this);
                    mListView.setAdapter(mAdapter);
                    mListView.setOnScrollListener(new OnScrollListener() {

                              @Override
                              public void onScrollStateChanged(AbsListView view, int scrollState) {

                              }

                              @Override
                              public void onScroll(AbsListView view, int firstVisibleItem,
                                                   int visibleItemCount, int totalItemCount) {
                                        if ((indexPostion < mListView.getFirstVisiblePosition() || indexPostion > mListView
                                             .getLastVisiblePosition()) && isPlaying) {
                                                  indexPostion = -1;
                                                  isPlaying = false;
                                                  mAdapter.notifyDataSetChanged();
                                                  MediaHelp.release();
                                        }
                              }
                    });
          }


          public void initView() {
                    leftIv = (ImageView) findViewById(R.id.left);
                    titleTv = (TextView) findViewById(R.id.title);
                    titleTv.setText("观看视频");


                    leftIv.setOnClickListener(new OnClickListener() {
                              @Override
                              public void onClick(View arg0) {
                                        finish();
                              }
                    });
          }

          class MAdapter extends BaseAdapter {
                    private Context context;
                    LayoutInflater inflater;

                    public MAdapter(Context context) {
                              this.context = context;
                              inflater = LayoutInflater.from(context);
                    }

                    @Override
                    public VideoBean getItem(int position) {
                              return mList.get(position);
                    }

                    @Override
                    public long getItemId(int position) {
                              return position;
                    }

                    @Override
                    public int getCount() {
                              return mList.size();
                    }

                    @Override
                    public View getView(int position, View v, ViewGroup parent) {
                              GameVideoViewHolder holder = null;
                              if (v == null) {
                                        holder = new GameVideoViewHolder();
                                        v = inflater.inflate(R.layout.list_video_item, parent, false);
                                        holder.mVideoViewLayout = (VideoSuperPlayer) v
                                             .findViewById(R.id.video);
                                        holder.mPlayBtnView = (ImageView) v.findViewById(R.id.play_btn);

                                        //获取第一帧数据
                                        //获取Assets资源
                                        assetMg = context.getApplicationContext().getAssets();
                                        fileDescriptor = null;
                                        try {
                                                  fileDescriptor = assetMg.openFd(ConstanUrl.url[position]+".mp4");
                                        } catch (IOException e) {
                                                  e.printStackTrace();
                                        }
                                        //获取播放时间
                                        mediaMetadata = new MediaMetadataRetriever();
                                        mediaMetadata.setDataSource(fileDescriptor.getFileDescriptor(),
                                             fileDescriptor.getStartOffset(), fileDescriptor.getLength());
                                        holder.m_viewById = (ImageView) v.findViewById(i_image);

                                        holder.m_tvTime=(TextView) v.findViewById(R.id.tv_time);
                                        //获取第一帧图片
                                        m_bitmp = mediaMetadata.getFrameAtTime();
                                        //设置第一帧图片
                                        holder.m_viewById.setImageBitmap(m_bitmp);
                                        m_tvtTime = mediaMetadata.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                                        // TODO: 2017/1/2
                                        //设置播放时间
                                        holder.m_tvTime.setText(Integer.parseInt(m_tvtTime)/1000+"s");


                                        v.setTag(holder);

                              } else {
                                        holder = (GameVideoViewHolder) v.getTag();
                              }




                              holder.m_tvTitle = (TextView) v.findViewById(R.id.info_title);
                              if (mode != 1) {//做题跳转，不显示视频名称
                                        holder.m_tvTitle.setText(ConstanUrl.url[position]);
                              }
                              holder.mPlayBtnView.setOnClickListener(new MyOnclick(
                                   holder.mPlayBtnView, holder.mVideoViewLayout, position));
                              if (indexPostion == position) {
                                        holder.mVideoViewLayout.setVisibility(View.VISIBLE);
                              } else {
                                        holder.mVideoViewLayout.setVisibility(View.GONE);
                                        holder.mVideoViewLayout.close();
                              }

                              return v;
                    }

                    class MyOnclick implements OnClickListener {
                              VideoSuperPlayer mSuperVideoPlayer;
                              ImageView mPlayBtnView;
                              int position;

                              public MyOnclick(ImageView mPlayBtnView,
                                               VideoSuperPlayer mSuperVideoPlayer, int position) {
                                        this.position = position;
                                        this.mSuperVideoPlayer = mSuperVideoPlayer;
                                        this.mPlayBtnView = mPlayBtnView;
                              }

                              @Override
                              public void onClick(View v) {
                                        MediaHelp.release();
                                        indexPostion = position;
                                        isPlaying = true;
                                        mSuperVideoPlayer.setVisibility(View.VISIBLE);
                                        mSuperVideoPlayer.loadAndPlay(MediaHelp.getInstance(), mList
                                             .get(position).getUrl(), 0, false, context);
                                        mSuperVideoPlayer.setVideoPlayCallback(new MyVideoPlayCallback(
                                             mPlayBtnView, mSuperVideoPlayer, mList.get(position)));
                                        notifyDataSetChanged();
                              }
                    }

                    class MyVideoPlayCallback implements VideoPlayCallbackImpl {
                              ImageView mPlayBtnView;
                              VideoSuperPlayer mSuperVideoPlayer;
                              VideoBean info;

                              public MyVideoPlayCallback(ImageView mPlayBtnView,
                                                         VideoSuperPlayer mSuperVideoPlayer, VideoBean info) {
                                        this.mPlayBtnView = mPlayBtnView;
                                        this.info = info;
                                        this.mSuperVideoPlayer = mSuperVideoPlayer;
                              }

                              @Override
                              public void onCloseVideo() {
                                        closeVideo();
                              }

                              @Override
                              public void onSwitchPageType() {
                                        if (((Activity) context).getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                                                  Intent intent = new Intent(new Intent(context,
                                                       FullVideoActivity.class));
                                                  intent.putExtra("video", info);
                                                  intent.putExtra("position",
                                                       mSuperVideoPlayer.getCurrentPosition());
                                                  ((Activity) context).startActivityForResult(intent, 1);
                                        }
                              }

                              @Override
                              public void onPlayFinish() {
                                        closeVideo();
                              }

                              private void closeVideo() {
                                        isPlaying = false;
                                        indexPostion = -1;
                                        mSuperVideoPlayer.close();
                                        MediaHelp.release();
                                        mPlayBtnView.setVisibility(View.VISIBLE);
                                        mSuperVideoPlayer.setVisibility(View.GONE);
                              }

                    }

                    class GameVideoViewHolder {

                              private VideoSuperPlayer mVideoViewLayout;
                              private ImageView mPlayBtnView;
                              private TextView m_tvTitle;
                              private ImageView m_viewById;
                              private TextView m_tvTime;

                    }

          }

}
