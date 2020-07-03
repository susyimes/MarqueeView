package com.sunfusheng.marqueeview.demo

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.text.PrecomputedTextCompat
import android.support.v4.widget.TextViewCompat
import android.support.v7.widget.AppCompatTextView
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.StyleSpan
import android.text.style.URLSpan
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.AdapterViewFlipper
import android.widget.BaseAdapter
import android.widget.ViewFlipper
import com.sunfusheng.marqueeview.MarqueeView

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

/**
 * @author by sunfusheng on 2017/8/8.
 */
class CommonFragment : Fragment() {
    private var marqueeView: MarqueeView<Any>? = null
    //    private MarqueeView marqueeView1;
//    private MarqueeView marqueeView2;
//    private MarqueeView marqueeView3;
//    private MarqueeView marqueeView4;
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_tab, container, false)
        marqueeView = view.findViewById(R.id.marqueeView)
        val viewFlipper = view.findViewById<AdapterViewFlipper>(R.id.viewflip)
        val viewFlipper1 = view.findViewById<ViewFlipper>(R.id.viewflip1)
        //        marqueeView1 = view.findViewById(R.id.marqueeView1);
//        marqueeView2 = view.findViewById(R.id.marqueeView2);
//        marqueeView3 = view.findViewById(R.id.marqueeView3);
//        marqueeView4 = view.findViewById(R.id.marqueeView4);
//        AppBarLayout appBarLayout= view.findViewById(R.id.app_bar);
//        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
//            @Override
//            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
//                Log.e("marqueee",i+"///");
//            }
//        });
        val list: MutableList<CharSequence> = ArrayList()
        val ss1 = SpannableString("1、MarqueeView开源项目MarqueeView开源项目MarqueeView开源项目MarqueeView开源项目MarqueeView开源项目" +
                "MarqueeView开源项目MarqueeView开源项目MarqueeView开源项目")
        ss1.setSpan(StyleSpan(Typeface.BOLD), 2, 13, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        list.add(ss1)
        val ss2 = SpannableString("2、GitHub：sunfusheng+GitHub：sunfusheng+GitHub：sunfusheng+GitHub：sunfusheng+GitHub：sunfusheng\n$ss1")
        ss2.setSpan(StyleSpan(Typeface.BOLD), 2, 13, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        //ss2.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.blue)), 9, 19, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        list.add(ss2)
//        val ss3 = SpannableString("3、个人博客：sunfusheng.com")
//        ss3.setSpan(URLSpan("http://sunfusheng.com/"), 7, 21, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
//        list.add(ss3)
        list.add("4、新浪微博：@孙福生微博")
        list.add(ss1)
        list.add(ss1)
        list.add(ss1)
        //set Custom font
//marqueeView.setTypeface(ResourcesCompat.getFont(getContext(), R.font.huawenxinwei));
        marqueeView?.startWithList(list as List<Any>?)
        marqueeView?.setFliListener {
            Log.e("fliiiiii",it.toString()+"////")
        }
        //marqueeView.setOnItemClickListener((position, textView) -> Toast.makeText(getContext(), textView.getText() + "", Toast.LENGTH_SHORT).show());
        val flipAdapter = FlipAdapter(list)
        viewFlipper.adapter = flipAdapter
        //viewFlipper.setInAnimation(new ObjectAnimator());
        val p1 = PropertyValuesHolder.ofFloat("translationY", 225f, 0f)
        val p2 = PropertyValuesHolder.ofFloat("alpha", 0f, 1f)
        val anim_in = ObjectAnimator.ofPropertyValuesHolder(viewFlipper, p1, p2).setDuration(1000)
        anim_in.interpolator = AccelerateDecelerateInterpolator()
        viewFlipper.inAnimation = anim_in
        val p3 = PropertyValuesHolder.ofFloat("translationY", 0f, -225f)
        val p4 = PropertyValuesHolder.ofFloat("alpha", 1f, 0f)
        val anim_out = ObjectAnimator.ofPropertyValuesHolder(viewFlipper, p3, p4).setDuration(1000)
        anim_out.interpolator = AccelerateDecelerateInterpolator()
        viewFlipper.outAnimation = anim_out

//                viewFlipper.setFlipInterval(3000);
//        viewFlipper.setAutoStart(true);
//ObjectAnimator animator = ObjectAnimator.ofFloat(, "translationY", 0, 200, -100,0);
//        marqueeView1.startWithText(getString(R.string.marquee_texts), R.anim.anim_top_in, R.anim.anim_bottom_out);
//        marqueeView1.setOnItemClickListener((position, textView) -> Toast.makeText(getContext(), String.valueOf(position) + ". " + textView.getText(), Toast.LENGTH_SHORT).show());
//
//        marqueeView2.startWithText(getString(R.string.marquee_text));
//
//        marqueeView3.startWithText(getString(R.string.marquee_texts));
//        marqueeView3.setOnItemClickListener((position, textView) -> {
//            CharSequence model = (CharSequence) marqueeView3.getMessages().get(position);
//            Toast.makeText(getContext(), model, Toast.LENGTH_SHORT).show();
//        });
//
//        List<CustomModel> models = new ArrayList<>();
//        models.add(new CustomModel(10000, ss1, "设置自定义的Model数据类型"));
//        models.add(new CustomModel(10001, "GitHub：sunfusheng", "新浪微博：@孙福生微博"));
//        models.add(new CustomModel(10002, "MarqueeView开源项目", "个人博客：sunfusheng.com"));
//        marqueeView4.startWithList(models);
//        marqueeView4.setOnItemClickListener((position, textView) -> {
//            CustomModel model = (CustomModel) marqueeView4.getMessages().get(position);
//            Toast.makeText(getContext(), "ID:" + model.id, Toast.LENGTH_SHORT).show();
//        });
        return view
    }

    private inner class FlipAdapter(private val list: List<CharSequence>) : BaseAdapter() {
        var textViews: MutableList<AppCompatTextView> = ArrayList()
        //TextView textView;
        var j = 0

        override fun getCount(): Int {
            return list.size
        }

        override fun getItem(i: Int): Any {
            return textViews[i]
        }

        override fun getItemId(i: Int): Long {
            return i.toLong()
        }

        override fun getView(i: Int, view: View?, viewGroup: ViewGroup?): View { //            if (view == null) {
//                Log.e("susyimesss","num"+j++);
//                textView = new TextView(getContext());
//                textView.setMaxLines(2);
//                textView.setEllipsize(TextUtils.TruncateAt.END);
//            } else {
//                textView = (TextView) view;
//            }
//            if (view==null){
//                textView =textViews.get(i);
//                textView.setText();
//            }else {
//                textView = (TextView) view;
//            }
            if (j++ < 5) { //textViews.get(i).setText(list.get(i));
                textViews[i].gravity = Gravity.CENTER_VERTICAL
                textViews[i].maxLines = 2
                textViews[i].ellipsize = TextUtils.TruncateAt.END
                textViews[i].textSize = 20f
                textViews[i].setTextColor(Color.BLACK)
//                textViews[i].setTextFuture(
//                        PrecomputedTextCompat.getTextFuture(
//                                list[i],  //文本
//                                textViews[i].textMetricsParamsCompat,  //PrecomputedTextCompat.Params
//                                null) //线程池,
//                )
                GlobalScope.launch(Dispatchers.Main) {
                    val text = withContext(Dispatchers.IO)
                    {
                        val params = TextViewCompat.getTextMetricsParams( textViews[i])
                        PrecomputedTextCompat.create(list[i], params)
                    }
                    TextViewCompat.setPrecomputedText(textViews[i], text)
                }
                Log.e("susyimesss", "num$j")
            }
            return textViews[i]
        }

        init {
            for (i in list.indices) {
                textViews.add(AppCompatTextView(context))
            }
        }
    }
}