package com.example.answer.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.answer.bean.AnSwerInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者：Jacky
 * 邮箱：550997728@qq.com
 * 时间：2017/1/1 09:29
 */
public class DataBaseManager {


          private SQLiteDatabase db;//数据库

          public DataBaseManager(SQLiteDatabase db) {
                    //获取db
                    this.db = db;
          }

          /**
           * 返回一个list问题集
           * @param mode 1 顺序考试题目 2随机获取考试题目 3错题练习
           * @return
           */
          public List<AnSwerInfo> getAnSwers(int mode,String number,String testNo){
                    //定义查询结果集
                    List<AnSwerInfo> anSwers = new ArrayList();
                    //课表查询的逻辑
                    String sql;
                    // TODO: 2017/1/1  
                    switch (mode) {//questionType='1'and
                              case 0:sql="select questionId,questionName,optionA,optionB,optionC,optionD,optionE,correctAnswer,questionType,answerAnalysis,video_url from TestData where test_No='"+testNo+"' order by questionType desc";break;
                              case 1:sql="select questionId,questionName,optionA,optionB,optionC,optionD,optionE,correctAnswer,questionType,answerAnalysis,video_url FROM TestData  where  video_url='' ORDER BY RANDOM() limit "+number;break;
                              case 2:sql="select questionId,questionName,optionA,optionB,optionC,optionD,optionE,correctAnswer,questionType,answerAnalysis,video_url from TestData where errornumber>0";break;
                              default:sql=null;
                    }
                    Cursor cursor = db.rawQuery(sql, null);
                    System.out.println(sql);
                    //封装查询到的数据
                    if (cursor != null && cursor.getCount() > 0) {
                              while (cursor.moveToNext()) {
                                        AnSwerInfo anSwer = new AnSwerInfo();
                                        //columnIndex打表列的索引
                                        anSwer.setQuestionId(cursor.getString(0));
                                        anSwer.setQuestionName(cursor.getString(1));

                                        anSwer.setOptionA(cursor.getString(2));
                                        anSwer.setOptionB(cursor.getString(3));
                                        if ("0".equals(cursor.getString(8)) || "1".equals(cursor.getString(8))) {//单选和多选
                                                  anSwer.setOptionC(cursor.getString(4));
                                                  anSwer.setOptionD(cursor.getString(5));
                                                  anSwer.setOptionE(cursor.getString(6));
                                        } else {
                                                  anSwer.setOptionC("");
                                                  anSwer.setOptionD("");
                                                  anSwer.setOptionE("");
                                        }
                                        anSwer.setCorrectAnswer(cursor.getString(7));
                                        anSwer.setQuestionType(cursor.getString(8));
                                        anSwer.setAnalysis(cursor.getString(9));
                                        anSwer.setVideoName(cursor.getString(10));
                                        anSwer.setQuestionFor("0");// （0模拟试题，1竞赛试题）
                                        anSwer.setScore("1");// 分值
                                        anSwer.setOption_type("0");
                                        anSwers.add(anSwer);
                              }
                    } else {
                              System.out.println("查询数据为空");
                              return null;
                    }
                    return anSwers;
          }

          /**
           * 记录错题 题目比对
           * @param questionName
           */
          public void recordWroing(String questionName,int mode,int flag){
                    String sql,sql2;
                    if (mode == 2) {
                              if (flag ==1) {//如果所有错题都答对
                                        sql="update TestData set errornumber=errornumber-1 where errornumber>0";
                                        db.execSQL(sql);
                                        System.out.println(sql);
                              } else {//部分错题答对
                                        sql="update TestData set errornumber=errornumber+1 where questionName=\'" + questionName + "\'";
                                        sql2="update TestData set errornumber=errornumber-1 where (questionName<>\'"+questionName+"\') and (errornumber>0)";
                                        db.execSQL(sql);
                                        db.execSQL(sql2);
                                        System.out.println(sql);
                                        System.out.println(sql2);

                              }
                    } else {
                              sql = "update TestData set errornumber=1 where questionName=\'" + questionName + "\'";
                              db.execSQL(sql);
                    }
          }
          public String[] gettes_no() {
                    String str[];
                    String sql = "select distinct test_No from TestData";
                    Cursor cursor = db.rawQuery(sql, null);
                    //查询班级数据
                    str=new String[cursor.getCount()];
                    //cursor.getColumnCount()
                    if (cursor != null && cursor.getCount() > 0) {
                              for (int i = 0; cursor.moveToNext(); i++) {
                                        str[i] = cursor.getString(0);
                              }
                    }
                    return str;
          }

          public void insertData(AnSwerInfo anSwerInfo) {
                    if (anSwerInfo.getAnalysis() == null) {
                              anSwerInfo.setAnalysis("暂无解析");
                    }
                    if (anSwerInfo.getScore() == null) {
                              anSwerInfo.setScore("1");
                              if (anSwerInfo.getQuestionType().equals("1")) {
                                        anSwerInfo.setScore("2");
                              }
                    }
                    if (anSwerInfo.getOptionD() == null) {
                              anSwerInfo.setOptionD("");
                    }
                    if (anSwerInfo.getOptionE() == null) {
                              anSwerInfo.setOptionE("");
                    }
                    String sql;
                    sql="insert into TestData values ("+anSwerInfo.getQuestionId()+",'"+anSwerInfo.getQuestionName()+"','"+anSwerInfo.getOptionA()+
                         "','"+anSwerInfo.getOptionB()+"','"+anSwerInfo.getOptionC()+"','"+anSwerInfo.getOptionD()+"','"+anSwerInfo.getOptionE()+"','"+ anSwerInfo.getCorrectAnswer()+
                         "','"+anSwerInfo.getQuestionType()+"','"+anSwerInfo.getAnalysis()+"','"+anSwerInfo.getScore()+"',0,'"+anSwerInfo.getTestNo()+"','')";
                    System.out.println(sql);
                    db.execSQL(sql);

          }

}
