package com.example.answer.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.answer.R;
import com.example.answer.activity.AnalogyExaminationActivity;
import com.example.answer.activity.VideoListActivity;
import com.example.answer.bean.AnSwerInfo;
import com.example.answer.bean.ErrorQuestionInfo;
import com.example.answer.database.DBManager;
import com.example.answer.util.ConstantUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者：Jacky
 * 邮箱：550997728@qq.com
 * 时间：2017/1/2 14:11
 */
public class SimulationExaminationAdapter extends PagerAdapter {

          AnalogyExaminationActivity mContext;
          // 传递过来的页面view的集合
          List<View> viewItems;
          // 每个item的页面view
          View convertView;
          // 传递过来的所有数据
          List<AnSwerInfo> dataItems;

          String imgServerUrl = "";

          private Map<Integer, Boolean> map = new HashMap<Integer, Boolean>();
          private Map<Integer, Boolean> mapClick = new HashMap<Integer, Boolean>();
          private Map<Integer, String> mapMultiSelect = new HashMap<Integer, String>();

          boolean isClick = false;

          boolean isNext = false;

          StringBuffer answer = new StringBuffer();
          StringBuffer answerLast = new StringBuffer();
          StringBuffer answer1 = new StringBuffer();
          // 多选控制
          int flagA = 0;
          int flagB = 0;
          int flagC = 0;
          int flagD = 0;
          int flagE = 0;

          DBManager dbManager;

          String isCorrect = ConstantUtil.isCorrect;//1对，0错

          int errortopicNum = 0;
          int multerrortopicNum = 0;
          int judgeerrortopicNum = 0;
          int singleerrortopicNum = 0;
          String resultA = "";
          String resultB = "";
          String resultC = "";
          String resultD = "";
          String resultE = "";
          private final SharedPreferences sp;
          private final SharedPreferences.Editor edit;

          public SimulationExaminationAdapter(AnalogyExaminationActivity context, List<View> viewItems, List<AnSwerInfo> dataItems, String imgServerUrl) {
                    mContext = context;
                    this.viewItems = viewItems;
                    this.dataItems = dataItems;
                    this.imgServerUrl = imgServerUrl;
                    dbManager = new DBManager(context);
                    dbManager.openDB();
                    //获取SP编辑器
                    // TODO: 2017/1/3
                    sp = mContext.getSharedPreferences("mySelect", Context.MODE_PRIVATE);
                    edit = sp.edit();
          }

          public long getItemId(int position) {
                    return position;
          }

          @Override
          public void destroyItem(ViewGroup container, int position, Object object) {
                    container.removeView(viewItems.get(position));
          }

          @Override
          /**
           * 监听
           */
          public Object instantiateItem(ViewGroup container, final int position) {
                    final SimulationExaminationAdapter.ViewHolder holder = new SimulationExaminationAdapter.ViewHolder();
                    convertView = viewItems.get(position);
                    holder.questionType = (TextView) convertView.findViewById(R.id.activity_prepare_test_no);
                    holder.question = (TextView) convertView.findViewById(R.id.activity_prepare_test_question);
                    holder.previousBtn = (LinearLayout) convertView.findViewById(R.id.activity_prepare_test_upLayout);
                    holder.nextBtn = (LinearLayout) convertView.findViewById(R.id.activity_prepare_test_nextLayout);
                    holder.nextText = (TextView) convertView.findViewById(R.id.menu_bottom_nextTV);
                    holder.errorBtn = (LinearLayout) convertView.findViewById(R.id.activity_prepare_test_errorLayout);
                    holder.totalText = (TextView) convertView.findViewById(R.id.activity_prepare_test_totalTv);
                    holder.nextImage = (ImageView) convertView.findViewById(R.id.menu_bottom_nextIV);
                    holder.wrongLayout = (LinearLayout) convertView.findViewById(R.id.activity_prepare_test_wrongLayout);
                    holder.explaindetailTv = (TextView) convertView.findViewById(R.id.activity_prepare_test_explaindetail);
                    holder.layoutA = (LinearLayout) convertView.findViewById(R.id.activity_prepare_test_layout_a);
                    holder.layoutB = (LinearLayout) convertView.findViewById(R.id.activity_prepare_test_layout_b);
                    holder.layoutC = (LinearLayout) convertView.findViewById(R.id.activity_prepare_test_layout_c);
                    holder.layoutD = (LinearLayout) convertView.findViewById(R.id.activity_prepare_test_layout_d);
                    holder.layoutE = (LinearLayout) convertView.findViewById(R.id.activity_prepare_test_layout_e);
                    holder.ivA = (ImageView) convertView.findViewById(R.id.vote_submit_select_image_a);
                    holder.ivB = (ImageView) convertView.findViewById(R.id.vote_submit_select_image_b);
                    holder.ivC = (ImageView) convertView.findViewById(R.id.vote_submit_select_image_c);
                    holder.ivD = (ImageView) convertView.findViewById(R.id.vote_submit_select_image_d);
                    holder.ivE = (ImageView) convertView.findViewById(R.id.vote_submit_select_image_e);
                    holder.tvA = (TextView) convertView.findViewById(R.id.vote_submit_select_text_a);
                    holder.tvB = (TextView) convertView.findViewById(R.id.vote_submit_select_text_b);
                    holder.tvC = (TextView) convertView.findViewById(R.id.vote_submit_select_text_c);
                    holder.tvD = (TextView) convertView.findViewById(R.id.vote_submit_select_text_d);
                    holder.tvE = (TextView) convertView.findViewById(R.id.vote_submit_select_text_e);
                    holder.ivA_ = (ImageView) convertView.findViewById(R.id.vote_submit_select_image_a_);
                    holder.ivB_ = (ImageView) convertView.findViewById(R.id.vote_submit_select_image_b_);
                    holder.ivC_ = (ImageView) convertView.findViewById(R.id.vote_submit_select_image_c_);
                    holder.ivD_ = (ImageView) convertView.findViewById(R.id.vote_submit_select_image_d_);
                    holder.ivE_ = (ImageView) convertView.findViewById(R.id.vote_submit_select_image_e_);

                    holder.errertv = (TextView) convertView.findViewById(R.id.menu_bottom_errorTV);
                    holder.totalText.setText(position + 1 + "/" + dataItems.size());
                    // TODO: 2017/1/5

                    //观看视频响应事件
                    holder.errorBtn.setOnClickListener(new View.OnClickListener() {

                              @Override
                              public void onClick(View arg0) {
                                        // TODO: 2017/1/4
                                        if (dataItems.get(position).getVideoName().length() < 3) {
                                                  Toast.makeText(mContext, "暂无视频！", Toast.LENGTH_SHORT).show();
                                        } else {
                                                  Intent intent = new Intent(mContext, VideoListActivity.class);
                                                  intent.putExtra("videoName", dataItems.get(position).videoName);
                                                  intent.putExtra("mode", 1);
                                                  mContext.startActivity(intent);
                                        }
                              }
                    });

                    if (dataItems.get(position).getOptionA().equals("")) {
                              holder.layoutA.setVisibility(View.GONE);
                    }
                    if (dataItems.get(position).getOptionB().equals("")) {
                              holder.layoutB.setVisibility(View.GONE);
                    }
                    if (dataItems.get(position).getOptionC().equals("")) {
                              holder.layoutC.setVisibility(View.GONE);
                    }
                    if (dataItems.get(position).getOptionD().equals("")) {
                              holder.layoutD.setVisibility(View.GONE);
                    }
                    if (dataItems.get(position).getOptionE().equals("")) {
                              holder.layoutE.setVisibility(View.GONE);
                    }
                    holder.errertv.setText("观看视频");

                    if (position != 0) {
                              if (dataItems.get(position-1).getVideoName().length()>2) {
                                        //有视频选题，弹出提示
                                        Toast.makeText(mContext, "点击播放视频！", Toast.LENGTH_SHORT).show();
                              }
                    }

                    //判断是否文字图片题目
                    //文字题目
                    holder.ivA_.setVisibility(View.GONE);
                    holder.ivB_.setVisibility(View.GONE);
                    holder.ivC_.setVisibility(View.GONE);
                    holder.ivD_.setVisibility(View.GONE);
                    holder.ivE_.setVisibility(View.GONE);
                    holder.tvA.setVisibility(View.VISIBLE);
                    holder.tvB.setVisibility(View.VISIBLE);
                    holder.tvC.setVisibility(View.VISIBLE);
                    holder.tvD.setVisibility(View.VISIBLE);
                    holder.tvE.setVisibility(View.VISIBLE);
                    holder.tvA.setText("A." + dataItems.get(position).getOptionA());
                    holder.tvB.setText("B." + dataItems.get(position).getOptionB());
                    holder.tvC.setText("C." + dataItems.get(position).getOptionC());
                    holder.tvD.setText("D." + dataItems.get(position).getOptionD());
                    holder.tvE.setText("E." + dataItems.get(position).getOptionE());
                    //判断题型
                    if (dataItems.get(position).getQuestionType().equals("0")) {
                              //单选题
                              // TODO: 2017/1/2   
                              holder.question.setText("(单选题)" + dataItems.get(position).getQuestionName());

                              holder.layoutA.setOnClickListener(new View.OnClickListener() {


                                        @Override
                                        public void onClick(View arg0) {
                                                  edit.putString("mySelect" + position, "A");
                                                  edit.commit();
                                                  map.put(position, true);
                                                  holder.ivA.setImageResource(R.drawable.ic_practice_test_select);
                                                  holder.tvA.setTextColor(Color.parseColor("#2b89e9"));
                                                  holder.ivB.setImageResource(R.drawable.ic_practice_test_normal);
                                                  holder.tvB.setTextColor(Color.parseColor("#9a9a9a"));
                                                  holder.ivC.setImageResource(R.drawable.ic_practice_test_normal);
                                                  holder.tvC.setTextColor(Color.parseColor("#9a9a9a"));
                                                  holder.ivD.setImageResource(R.drawable.ic_practice_test_normal);
                                                  holder.tvD.setTextColor(Color.parseColor("#9a9a9a"));
                                                  holder.ivE.setImageResource(R.drawable.ic_practice_test_normal);
                                                  holder.tvE.setTextColor(Color.parseColor("#9a9a9a"));
                                        }
                              });
                              holder.layoutB.setOnClickListener(new View.OnClickListener() {

                                        @Override
                                        public void onClick(View arg0) {
                                                  edit.putString("mySelect" + position, "B");
                                                  edit.commit();
                                                  map.put(position, true);
                                                  holder.ivA.setImageResource(R.drawable.ic_practice_test_normal);
                                                  holder.tvA.setTextColor(Color.parseColor("#9a9a9a"));
                                                  holder.ivB.setImageResource(R.drawable.ic_practice_test_select);
                                                  holder.tvB.setTextColor(Color.parseColor("#2b89e9"));
                                                  holder.ivC.setImageResource(R.drawable.ic_practice_test_normal);
                                                  holder.tvC.setTextColor(Color.parseColor("#9a9a9a"));
                                                  holder.ivD.setImageResource(R.drawable.ic_practice_test_normal);
                                                  holder.tvD.setTextColor(Color.parseColor("#9a9a9a"));
                                                  holder.ivE.setImageResource(R.drawable.ic_practice_test_normal);
                                                  holder.tvE.setTextColor(Color.parseColor("#9a9a9a"));
                                        }
                              });
                              holder.layoutC.setOnClickListener(new View.OnClickListener() {

                                        @Override
                                        public void onClick(View arg0) {
                                                  edit.putString("mySelect" + position, "C");
                                                  edit.commit();
                                                  map.put(position, true);
                                                  holder.ivA.setImageResource(R.drawable.ic_practice_test_normal);
                                                  holder.tvA.setTextColor(Color.parseColor("#9a9a9a"));
                                                  holder.ivB.setImageResource(R.drawable.ic_practice_test_normal);
                                                  holder.tvB.setTextColor(Color.parseColor("#9a9a9a"));
                                                  holder.ivC.setImageResource(R.drawable.ic_practice_test_select);
                                                  holder.tvC.setTextColor(Color.parseColor("#2b89e9"));
                                                  holder.ivD.setImageResource(R.drawable.ic_practice_test_normal);
                                                  holder.tvD.setTextColor(Color.parseColor("#9a9a9a"));
                                                  holder.ivE.setImageResource(R.drawable.ic_practice_test_normal);
                                                  holder.tvE.setTextColor(Color.parseColor("#9a9a9a"));
                                        }
                              });
                              holder.layoutD.setOnClickListener(new View.OnClickListener() {

                                        @Override
                                        public void onClick(View arg0) {
                                                  edit.putString("mySelect" + position, "D");
                                                  edit.commit();
                                                  map.put(position, true);
                                                  holder.ivA.setImageResource(R.drawable.ic_practice_test_normal);
                                                  holder.tvA.setTextColor(Color.parseColor("#9a9a9a"));
                                                  holder.ivB.setImageResource(R.drawable.ic_practice_test_normal);
                                                  holder.tvB.setTextColor(Color.parseColor("#9a9a9a"));
                                                  holder.ivC.setImageResource(R.drawable.ic_practice_test_normal);
                                                  holder.tvC.setTextColor(Color.parseColor("#9a9a9a"));
                                                  holder.ivD.setImageResource(R.drawable.ic_practice_test_select);
                                                  holder.tvD.setTextColor(Color.parseColor("#2b89e9"));
                                                  holder.ivE.setImageResource(R.drawable.ic_practice_test_normal);
                                                  holder.tvE.setTextColor(Color.parseColor("#9a9a9a"));
                                        }
                              });
                              holder.layoutE.setOnClickListener(new View.OnClickListener() {

                                        @Override
                                        public void onClick(View arg0) {
                                                  //保存选择结果
                                                  edit.putString("mySelect" + position, "E");
                                                  edit.commit();
                                                  map.put(position, true);
                                                  holder.ivA.setImageResource(R.drawable.ic_practice_test_normal);
                                                  holder.tvA.setTextColor(Color.parseColor("#9a9a9a"));
                                                  holder.ivB.setImageResource(R.drawable.ic_practice_test_normal);
                                                  holder.tvB.setTextColor(Color.parseColor("#9a9a9a"));
                                                  holder.ivC.setImageResource(R.drawable.ic_practice_test_normal);
                                                  holder.tvC.setTextColor(Color.parseColor("#9a9a9a"));
                                                  holder.ivD.setImageResource(R.drawable.ic_practice_test_normal);
                                                  holder.tvD.setTextColor(Color.parseColor("#9a9a9a"));
                                                  holder.ivE.setImageResource(R.drawable.ic_practice_test_select);
                                                  holder.tvE.setTextColor(Color.parseColor("#2b89e9"));
                                        }
                              });
                    } else if (dataItems.get(position).getQuestionType().equals("1")) {
                              //多选题
                              // TODO: 2017/1/2  
                              holder.question.setText("(多选题)" + dataItems.get(position).getQuestionName());

                              holder.layoutA.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View arg0) {
                                                  //判断是否选择了多个
                                                  if ((flagA + flagB + flagC + flagD + flagE) >= 1) {
                                                            map.put(position, true);
                                                  }
                                                  if (flagA == 0) {
                                                            holder.ivA.setImageResource(R.drawable.ic_practice_test_select);
                                                            holder.tvA.setTextColor(Color.parseColor("#2b89e9"));
                                                            flagA = 1;
                                                  } else {
                                                            holder.ivA.setImageResource(R.drawable.ic_practice_test_normal);
                                                            holder.tvA.setTextColor(Color.parseColor("#9a9a9a"));
                                                            flagA = 0;
                                                  }
                                                  //保存选择结果
                                                  resultSave(position);
                                        }
                              });
                              holder.layoutB.setOnClickListener(new View.OnClickListener() {

                                        @Override
                                        public void onClick(View arg0) {
                                                  if ((flagA + flagB + flagC + flagD + flagE) >= 1) {
                                                            map.put(position, true);
                                                  }
                                                  if (flagB == 0) {
                                                            holder.ivB.setImageResource(R.drawable.ic_practice_test_select);
                                                            holder.tvB.setTextColor(Color.parseColor("#2b89e9"));
                                                            flagB = 1;
                                                  } else {
                                                            holder.ivB.setImageResource(R.drawable.ic_practice_test_normal);
                                                            holder.tvB.setTextColor(Color.parseColor("#9a9a9a"));
                                                            flagB = 0;
                                                  }
                                                  //保存选择结果
                                                  resultSave(position);
                                        }
                              });
                              holder.layoutC.setOnClickListener(new View.OnClickListener() {

                                        @Override
                                        public void onClick(View arg0) {
                                                  if ((flagA + flagB + flagC + flagD + flagE) >= 1) {
                                                            map.put(position, true);
                                                  }
                                                  if (flagC == 0) {
                                                            holder.ivC.setImageResource(R.drawable.ic_practice_test_select);
                                                            holder.tvC.setTextColor(Color.parseColor("#2b89e9"));
                                                            flagC = 1;
                                                  } else {
                                                            holder.ivC.setImageResource(R.drawable.ic_practice_test_normal);
                                                            holder.tvC.setTextColor(Color.parseColor("#9a9a9a"));
                                                            flagC = 0;
                                                  }
                                                  //保存选择结果
                                                  resultSave(position);
                                        }
                              });
                              holder.layoutD.setOnClickListener(new View.OnClickListener() {

                                        @Override
                                        public void onClick(View arg0) {
                                                  if ((flagA + flagB + flagC + flagD + flagE) >= 1) {
                                                            map.put(position, true);
                                                  }
                                                  if (flagD == 0) {
                                                            holder.ivD.setImageResource(R.drawable.ic_practice_test_select);
                                                            holder.tvD.setTextColor(Color.parseColor("#2b89e9"));
                                                            flagD = 1;
                                                  } else {
                                                            holder.ivD.setImageResource(R.drawable.ic_practice_test_normal);
                                                            holder.tvD.setTextColor(Color.parseColor("#9a9a9a"));
                                                            flagD = 0;
                                                  }
                                                  //保存选择结果
                                                  resultSave(position);
                                        }
                              });
                              holder.layoutE.setOnClickListener(new View.OnClickListener() {

                                        @Override
                                        public void onClick(View arg0) {
                                                  if ((flagA + flagB + flagC + flagD + flagE) >= 1) {
                                                            map.put(position, true);
                                                  }
                                                  if (flagE == 0) {
                                                            holder.ivE.setImageResource(R.drawable.ic_practice_test_select);
                                                            holder.tvE.setTextColor(Color.parseColor("#2b89e9"));
                                                            flagE = 1;
                                                  } else {
                                                            holder.ivE.setImageResource(R.drawable.ic_practice_test_normal);
                                                            holder.tvE.setTextColor(Color.parseColor("#9a9a9a"));
                                                            flagE = 0;
                                                  }
                                                  //保存选择结果
                                                  resultSave(position);
                                        }
                              });
                    } else {
                              //判断题
                              // TODO: 2017/1/2  
                              holder.question.setText("(判断题)" + dataItems.get(position).getQuestionName());

                              holder.layoutA.setOnClickListener(new View.OnClickListener() {

                                        @Override
                                        public void onClick(View arg0) {
                                                  edit.putString("mySelect" + position, "A");
                                                  edit.commit();
                                                  map.put(position, true);
                                                  holder.ivA.setImageResource(R.drawable.ic_practice_test_select);
                                                  holder.tvA.setTextColor(Color.parseColor("#2b89e9"));
                                                  holder.ivB.setImageResource(R.drawable.ic_practice_test_normal);
                                                  holder.tvB.setTextColor(Color.parseColor("#9a9a9a"));
                                        }
                              });
                              holder.layoutB.setOnClickListener(new View.OnClickListener() {

                                        @Override
                                        public void onClick(View arg0) {
                                                  edit.putString("mySelect" + position, "B");
                                                  edit.commit();
                                                  map.put(position, true);
                                                  holder.ivA.setImageResource(R.drawable.ic_practice_test_normal);
                                                  holder.tvA.setTextColor(Color.parseColor("#9a9a9a"));
                                                  holder.ivB.setImageResource(R.drawable.ic_practice_test_select);
                                                  holder.tvB.setTextColor(Color.parseColor("#2b89e9"));
                                        }
                              });
                    }

//                    ForegroundColorSpan blueSpan = new ForegroundColorSpan(Color.parseColor("#2b89e9"));
//
//                    SpannableStringBuilder builder1 = new SpannableStringBuilder(holder.question.getText().toString());
//                    // TODO: 2017/1/2
//                    builder1.setSpan(blueSpan, 0, 5, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    holder.question.setText(builder1);

                    // 最后一页修改"下一步"按钮文字
                    if (position == viewItems.size() - 1) {
                              holder.nextText.setText("提交");
                              holder.nextImage.setImageResource(R.drawable.vote_submit_finish);
                    }
                    holder.previousBtn.setOnClickListener(new SimulationExaminationAdapter.LinearOnClickListener(position - 1, false, position, holder));
                    holder.nextBtn.setOnClickListener(new SimulationExaminationAdapter.LinearOnClickListener(position + 1, true, position, holder));
                    container.addView(viewItems.get(position));
                    return viewItems.get(position);
          }

          /**
           * @author 设置上一步和下一步按钮监听
           */
          class LinearOnClickListener implements View.OnClickListener {

                    private int mPosition;
                    private int mPosition1;
                    private boolean mIsNext;
                    private SimulationExaminationAdapter.ViewHolder viewHolder;

                    public LinearOnClickListener(int position, boolean mIsNext, int position1, SimulationExaminationAdapter.ViewHolder viewHolder) {
                              mPosition = position;
                              mPosition1 = position1;
                              this.viewHolder = viewHolder;
                              this.mIsNext = mIsNext;
                             
                              flagA = 0;
                              flagB = 0;
                              flagC = 0;
                              flagD = 0;
                              flagE = 0;
                    }

                    @Override
                    public void onClick(View v) {
                              if (mPosition == viewItems.size()) {
                                        String result = "";
                                        //计算错题，并存入数据库
                                        for (int i = 0; i < dataItems.size(); i++) {
                                                  String mySelect = sp.getString("mySelect" + i, "");
                                                  //如果是单选题
                                                  if (dataItems.get(i).getQuestionType().equals("0")) {
                                                            //如果我选择的答案与标准答案不同，存储错题
                                                            if (!dataItems.get(i).getCorrectAnswer().equals(mySelect)) {
                                                                      isCorrect = ConstantUtil.isError;
                                                                      errortopicNum += 1;
                                                                      singleerrortopicNum += 1;
                                                                      //自动添加错误题目
                                                                      ErrorQuestionInfo errorQuestionInfo = new ErrorQuestionInfo();
                                                                      errorQuestionInfo.setQuestionName(dataItems.get(i).getQuestionName());
                                                                      errorQuestionInfo.setQuestionType(dataItems.get(i).getQuestionType());
                                                                      errorQuestionInfo.setQuestionAnswer(dataItems.get(i).getCorrectAnswer());
                                                                      errorQuestionInfo.setIsRight(isCorrect);
                                                                      errorQuestionInfo.setQuestionSelect(mySelect);
                                                                      errorQuestionInfo.setAnalysis(dataItems.get(i).getAnalysis());
                                                                      errorQuestionInfo.setOptionType(dataItems.get(i).getOption_type());
                                                                      if (dataItems.get(i).getOption_type().equals("0")) {
                                                                                errorQuestionInfo.setOptionA(dataItems.get(i).getOptionA());
                                                                                errorQuestionInfo.setOptionB(dataItems.get(i).getOptionB());
                                                                                errorQuestionInfo.setOptionC(dataItems.get(i).getOptionC());
                                                                                errorQuestionInfo.setOptionD(dataItems.get(i).getOptionD());
                                                                                errorQuestionInfo.setOptionE(dataItems.get(i).getOptionE());
                                                                      } else {
                                                                                errorQuestionInfo.setOptionA(dataItems.get(i).getOptionA().equals("") ? "" : imgServerUrl + dataItems.get(i).getOptionA());
                                                                                errorQuestionInfo.setOptionB(dataItems.get(i).getOptionB().equals("") ? "" : imgServerUrl + dataItems.get(i).getOptionB());
                                                                                errorQuestionInfo.setOptionC(dataItems.get(i).getOptionC().equals("") ? "" : imgServerUrl + dataItems.get(i).getOptionC());
                                                                                errorQuestionInfo.setOptionD(dataItems.get(i).getOptionD().equals("") ? "" : imgServerUrl + dataItems.get(i).getOptionD());
                                                                                errorQuestionInfo.setOptionE(dataItems.get(i).getOptionE().equals("") ? "" : imgServerUrl + dataItems.get(i).getOptionE());
                                                                      }
                                                                      dbManager.insertErrorQuestion(errorQuestionInfo);
                                                            }
                                                  } else if (dataItems.get(i).getQuestionType().equals("1")) {//如果是多选
                                                            //标志是否做错
                                                            int flag = 0;
                                                            //若我选答案与标准答案长度相等，则逐项比较
                                                            if (mySelect.length() == dataItems.get(i).getCorrectAnswer().length()) {
                                                                      for (int j = 0; j < mySelect.length(); j++) {
                                                                                // TODO: 2017/1/4
                                                                                String mySelect1 = mySelect.substring(0 + j, 1 + j);
                                                                                //如果我选择的选项在正确答案中没有，则错误
                                                                                if (!dataItems.get(i).getCorrectAnswer().contains(mySelect1)) {
                                                                                          isCorrect = ConstantUtil.isError;
                                                                                          //自动添加错误题目
                                                                                          ErrorQuestionInfo errorQuestionInfo = new ErrorQuestionInfo();
                                                                                          errorQuestionInfo.setQuestionName(dataItems.get(i).getQuestionName());
                                                                                          errorQuestionInfo.setQuestionType(dataItems.get(i).getQuestionType());
                                                                                          errorQuestionInfo.setQuestionAnswer(dataItems.get(i).getCorrectAnswer());
                                                                                          errorQuestionInfo.setIsRight(isCorrect);
                                                                                          errorQuestionInfo.setQuestionSelect(mySelect1);
                                                                                          errorQuestionInfo.setAnalysis(dataItems.get(i).getAnalysis());
                                                                                          errorQuestionInfo.setOptionType(dataItems.get(i).getOption_type());
                                                                                          if (dataItems.get(i).getOption_type().equals("0")) {
                                                                                                    errorQuestionInfo.setOptionA(dataItems.get(i).getOptionA());
                                                                                                    errorQuestionInfo.setOptionB(dataItems.get(i).getOptionB());
                                                                                                    errorQuestionInfo.setOptionC(dataItems.get(i).getOptionC());
                                                                                                    errorQuestionInfo.setOptionD(dataItems.get(i).getOptionD());
                                                                                                    errorQuestionInfo.setOptionE(dataItems.get(i).getOptionE());
                                                                                          } else {
                                                                                                    errorQuestionInfo.setOptionA(dataItems.get(i).getOptionA().equals("") ? "" : imgServerUrl + dataItems.get(i).getOptionA());
                                                                                                    errorQuestionInfo.setOptionB(dataItems.get(i).getOptionB().equals("") ? "" : imgServerUrl + dataItems.get(i).getOptionB());
                                                                                                    errorQuestionInfo.setOptionC(dataItems.get(i).getOptionC().equals("") ? "" : imgServerUrl + dataItems.get(i).getOptionC());
                                                                                                    errorQuestionInfo.setOptionD(dataItems.get(i).getOptionD().equals("") ? "" : imgServerUrl + dataItems.get(i).getOptionD());
                                                                                                    errorQuestionInfo.setOptionE(dataItems.get(i).getOptionE().equals("") ? "" : imgServerUrl + dataItems.get(i).getOptionE());
                                                                                          }
                                                                                          dbManager.insertErrorQuestion(errorQuestionInfo);
                                                                                          flag = 1;
                                                                                          // TODO: 2017/1/3
                                                                                }


                                                                      }
                                                            } else {//或不相等，则错误
                                                                      ErrorQuestionInfo errorQuestionInfo = new ErrorQuestionInfo();
                                                                      errorQuestionInfo.setQuestionSelect("");//默认少选
                                                                      for (int j = 0; j < mySelect.length(); j++) {
                                                                                String mySelect1 = mySelect.substring(0 + j, 1 + j);
                                                                                //如果我选择的选项在正确答案中没有，则错误
                                                                                if (!dataItems.get(i).getCorrectAnswer().contains(mySelect1)) {
                                                                                          errorQuestionInfo.setQuestionSelect(mySelect1);//少选并且选错
                                                                                }
                                                                      }
                                                                      isCorrect = ConstantUtil.isError;
                                                                      //自动添加错误题目
                                                                      errorQuestionInfo.setQuestionName(dataItems.get(i).getQuestionName());
                                                                      errorQuestionInfo.setQuestionType(dataItems.get(i).getQuestionType());
                                                                      errorQuestionInfo.setQuestionAnswer(dataItems.get(i).getCorrectAnswer());
                                                                      errorQuestionInfo.setIsRight(isCorrect);
                                                                      errorQuestionInfo.setAnalysis(dataItems.get(i).getAnalysis());
                                                                      errorQuestionInfo.setOptionType(dataItems.get(i).getOption_type());
                                                                      if (dataItems.get(i).getOption_type().equals("0")) {
                                                                                errorQuestionInfo.setOptionA(dataItems.get(i).getOptionA());
                                                                                errorQuestionInfo.setOptionB(dataItems.get(i).getOptionB());
                                                                                errorQuestionInfo.setOptionC(dataItems.get(i).getOptionC());
                                                                                errorQuestionInfo.setOptionD(dataItems.get(i).getOptionD());
                                                                                errorQuestionInfo.setOptionE(dataItems.get(i).getOptionE());
                                                                      } else {
                                                                                errorQuestionInfo.setOptionA(dataItems.get(i).getOptionA().equals("") ? "" : imgServerUrl + dataItems.get(i).getOptionA());
                                                                                errorQuestionInfo.setOptionB(dataItems.get(i).getOptionB().equals("") ? "" : imgServerUrl + dataItems.get(i).getOptionB());
                                                                                errorQuestionInfo.setOptionC(dataItems.get(i).getOptionC().equals("") ? "" : imgServerUrl + dataItems.get(i).getOptionC());
                                                                                errorQuestionInfo.setOptionD(dataItems.get(i).getOptionD().equals("") ? "" : imgServerUrl + dataItems.get(i).getOptionD());
                                                                                errorQuestionInfo.setOptionE(dataItems.get(i).getOptionE().equals("") ? "" : imgServerUrl + dataItems.get(i).getOptionE());
                                                                      }
                                                                      dbManager.insertErrorQuestion(errorQuestionInfo);
                                                                      flag = 1;
                                                            }
                                                            if (flag == 1) {
                                                                      errortopicNum += 1;
                                                                      multerrortopicNum += 1;
                                                            }

                                                  } else {//如果是判断
                                                            //如果我选择的答案与标准答案不同，存储错题
                                                            if (!dataItems.get(i).getCorrectAnswer().equals(mySelect)) {
                                                                      isCorrect = ConstantUtil.isError;
                                                                      errortopicNum += 1;
                                                                      judgeerrortopicNum += 1;//判断题错题数
                                                                      //自动添加错误题目
                                                                      ErrorQuestionInfo errorQuestionInfo = new ErrorQuestionInfo();
                                                                      errorQuestionInfo.setQuestionName(dataItems.get(i).getQuestionName());
                                                                      errorQuestionInfo.setQuestionType(dataItems.get(i).getQuestionType());
                                                                      errorQuestionInfo.setQuestionAnswer(dataItems.get(i).getCorrectAnswer());
                                                                      errorQuestionInfo.setIsRight(isCorrect);
                                                                      errorQuestionInfo.setQuestionSelect(mySelect);
                                                                      errorQuestionInfo.setAnalysis(dataItems.get(i).getAnalysis());
                                                                      errorQuestionInfo.setOptionType(dataItems.get(i).getOption_type());
                                                                      if (dataItems.get(i).getOption_type().equals("0")) {
                                                                                errorQuestionInfo.setOptionA(dataItems.get(i).getOptionA());
                                                                                errorQuestionInfo.setOptionB(dataItems.get(i).getOptionB());
                                                                                errorQuestionInfo.setOptionC(dataItems.get(i).getOptionC());
                                                                                errorQuestionInfo.setOptionD(dataItems.get(i).getOptionD());
                                                                                errorQuestionInfo.setOptionE(dataItems.get(i).getOptionE());
                                                                      } else {
                                                                                errorQuestionInfo.setOptionA(dataItems.get(i).getOptionA().equals("") ? "" : imgServerUrl + dataItems.get(i).getOptionA());
                                                                                errorQuestionInfo.setOptionB(dataItems.get(i).getOptionB().equals("") ? "" : imgServerUrl + dataItems.get(i).getOptionB());
                                                                                errorQuestionInfo.setOptionC(dataItems.get(i).getOptionC().equals("") ? "" : imgServerUrl + dataItems.get(i).getOptionC());
                                                                                errorQuestionInfo.setOptionD(dataItems.get(i).getOptionD().equals("") ? "" : imgServerUrl + dataItems.get(i).getOptionD());
                                                                                errorQuestionInfo.setOptionE(dataItems.get(i).getOptionE().equals("") ? "" : imgServerUrl + dataItems.get(i).getOptionE());
                                                                      }
                                                                      dbManager.insertErrorQuestion(errorQuestionInfo);
                                                            }

                                                  }

                                        }


                                        //单选
                                        // TODO: 2017/1/2  
                                        if (dataItems.get(mPosition1).getQuestionType().equals("0")) {
                                                  if (!map.containsKey(mPosition1)) {
                                                            Toast.makeText(mContext, "请选择选项", Toast.LENGTH_SHORT).show();
                                                            return;
                                                  }
                                                  mContext.uploadExamination(errortopicNum);
                                        } else if (dataItems.get(mPosition1).getQuestionType().equals("1")) {
                                                  //判断多选时的点击
                                                  if (!map.containsKey(mPosition1)) {
                                                            if (!mapClick.containsKey(mPosition1)) {
                                                                      Toast.makeText(mContext, "请选择选项", Toast.LENGTH_SHORT).show();
                                                                      return;
                                                            }
                                                  }
                                                  map.put(mPosition1, true);

                                                  if (mapMultiSelect.containsKey(mPosition1)) {
                                                            //提交答题
                                                            mContext.uploadExamination(errortopicNum);
                                                  } else {
                                                            String ssStr = dataItems.get(mPosition1).getCorrectAnswer();

                                                  }
                                        } else {
                                                  if (!map.containsKey(mPosition1)) {
                                                            Toast.makeText(mContext, "请选择选项", Toast.LENGTH_SHORT).show();
                                                            return;
                                                  }
                                                  mContext.uploadExamination(errortopicNum);
                                        }
                                        // TODO: 2017/1/3 计算总成绩
                                        int total_score = 0;//计算总分数
                                        int multtotal = 0;
                                        int judgetotal = 0;
                                        int singletotal = 0;
                                        int multscore=1;
                                        for (int i = 0; i < dataItems.size(); i++) {
                                                  total_score += Integer.parseInt(dataItems.get(i).getScore());
                                                  if (dataItems.get(i).getQuestionType().equals("0")) {
                                                            singletotal++;
                                                  } else if (dataItems.get(i).getQuestionType().equals("1")) {
                                                            multtotal++;
                                                            multscore=Integer.parseInt(dataItems.get(i).getScore());
                                                  } else {
                                                            judgetotal++;
                                                  }
                                        }
                                        // TODO: 2017/1/5
                                        result = "题数：" + dataItems.size() + "题" + "\n错题：" + errortopicNum + "题"
                                             + "\n单选题：" + singletotal + "错" + singleerrortopicNum + "\n多选题：" + multtotal + "错" + multerrortopicNum + "\n判断题：" + judgetotal + "错" + judgeerrortopicNum
                                             + "\n总分：" + total_score + "\n得分：" + (total_score - singleerrortopicNum - multerrortopicNum*multscore-judgeerrortopicNum) + "分" + "\n正确率：" + (dataItems.size() - errortopicNum) * 100 / dataItems.size() + "%";
                                        edit.putString("result", result);
                                        edit.commit();
                                        System.out.println(result);
                              } else {
                                        if (mPosition == -1) {
                                                  Toast.makeText(mContext, "已经是第一页", Toast.LENGTH_SHORT).show();
                                                  return;
                                        } else {
                                                  //单选
                                                  if (dataItems.get(mPosition1).getQuestionType().equals("0")) {
                                                            if (mIsNext) {
                                                                      if (!map.containsKey(mPosition1)) {
                                                                                Toast.makeText(mContext, "请选择选项", Toast.LENGTH_SHORT).show();
                                                                                return;
                                                                      }
                                                            }
                                                            isNext = mIsNext;
                                                            mContext.setCurrentView(mPosition);
                                                  } else if (dataItems.get(mPosition1).getQuestionType().equals("1")) {
                                                            if (mIsNext) {
                                                                      //判断多选时的点击
                                                                      if (!map.containsKey(mPosition1)) {
                                                                                if (!mapClick.containsKey(mPosition1)) {
                                                                                          Toast.makeText(mContext, "请选择选项", Toast.LENGTH_SHORT).show();
                                                                                          return;
                                                                                }
                                                                      }
                                                                      map.put(mPosition1, true);

                                                                      if (mapMultiSelect.containsKey(mPosition1)) {
                                                                                //清除答案
                                                                                answer.delete(0, answer.length());
                                                                                //选过的，直接跳转下一题
                                                                                isNext = mIsNext;
                                                                                mContext.setCurrentView(mPosition);
                                                                      } else {
                                                                                //选过的，直接跳转下一题
                                                                                isNext = mIsNext;
                                                                                //下一步的响应事件
                                                                                mContext.setCurrentView(mPosition);
                                                                      }
                                                            } else {
                                                                      mContext.setCurrentView(mPosition);
                                                            }

                                                  } else {
                                                            if (mIsNext) {
                                                                      if (!map.containsKey(mPosition1)) {
                                                                                Toast.makeText(mContext, "请选择选项", Toast.LENGTH_SHORT).show();
                                                                                return;
                                                                      }
                                                            }

                                                            isNext = mIsNext;
                                                            mContext.setCurrentView(mPosition);
                                                  }
                                        }
                              }

                    }

          }

          @Override
          public int getCount() {
                    if (viewItems == null)
                              return 0;
                    return viewItems.size();
          }

          @Override
          public boolean isViewFromObject(View arg0, Object arg1) {
                    return arg0 == arg1;
          }

          //错题数
          public int errorTopicNum() {
                    if (errortopicNum != 0) {
                              return errortopicNum;
                    }
                    return 0;
          }

          public class ViewHolder {
                    TextView errertv;
                    TextView questionType;
                    TextView question;
                    LinearLayout previousBtn, nextBtn, errorBtn;
                    TextView nextText;
                    TextView totalText;
                    ImageView nextImage;
                    LinearLayout wrongLayout;
                    TextView explaindetailTv;
                    LinearLayout layoutA;
                    LinearLayout layoutB;
                    LinearLayout layoutC;
                    LinearLayout layoutD;
                    LinearLayout layoutE;
                    ImageView ivA;
                    ImageView ivB;
                    ImageView ivC;
                    ImageView ivD;
                    ImageView ivE;
                    TextView tvA;
                    TextView tvB;
                    TextView tvC;
                    TextView tvD;
                    TextView tvE;
                    ImageView ivA_;
                    ImageView ivB_;
                    ImageView ivC_;
                    ImageView ivD_;
                    ImageView ivE_;
          }

          /**
           * 保存答题结果
           *
           * @param position 题号
           */
          public void resultSave(int position) {
                    //多选结果保存
                    String result = "";
                    if (flagA == 1) {
                              result += "A";
                    }
                    if (flagB == 1) {
                              result += "B";
                    }
                    if (flagC == 1) {
                              result += "C";
                    }
                    if (flagD == 1) {
                              result += "D";
                    }
                    if (flagE == 1) {
                              result += "E";
                    }
                    System.out.println(result);

                    //保存选择结果
                    edit.putString("mySelect" + position, result);
                    edit.commit();
          }

}
