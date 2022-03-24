package com.fidoo.user.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.fidoo.user.R
import com.fidoo.user.utils.BaseActivity
import com.mixpanel.android.mpmetrics.MixpanelAPI
import com.premsinghdaksha.startactivityanimationlibrary.AppUtils
import kotlinx.android.synthetic.main.activity_slider_screen.*
import kotlinx.android.synthetic.main.screen1.*
import kotlinx.android.synthetic.main.screen2.*


@Suppress("DEPRECATION")
class SliderScreenActivity : BaseActivity() {
    var sliderViewPagerAdapter: SliderViewPagerAdapter? = null

    var currentPage: Int = 0

    private var mMixpanel: MixpanelAPI? = null

    private val layouts: IntArray = intArrayOf(
        R.layout.screen1,
        R.layout.screen2,
        R.layout.screen3
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            val w: Window = window
//            w.setFlags(
//                WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN
//            )
//        }
        val window: Window = this.getWindow()
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
//        window.setFlags(
//            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
//        )
        setContentView(R.layout.activity_slider_screen)

        mMixpanel = MixpanelAPI.getInstance(this, "defeff96423cfb1e8c66f8ba83ab87fd")

        val viewPagerPageChangeListener: ViewPager.OnPageChangeListener =
            object : ViewPager.OnPageChangeListener {
                override fun onPageSelected(position: Int) {
                    activeDot(position)
                    currentPage = position
                    checkVisible(position)
                }

                override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {}
                override fun onPageScrollStateChanged(arg0: Int) {}
            }

        sliderViewPagerAdapter = SliderViewPagerAdapter(this, layouts!!)
        sliderScreen.setAdapter(sliderViewPagerAdapter)
        sliderScreen.addOnPageChangeListener(viewPagerPageChangeListener)

        letsBegin_fml.setOnClickListener {
            currentPage++
            activeDot(currentPage)
            sliderScreen.setCurrentItem(currentPage, true)

            if (currentPage == 1) {
                letsBegin_fml1.setOnClickListener{
                    currentPage--
                    activeDot(currentPage)
                    sliderScreen.setCurrentItem(currentPage, true)

                }
                //letsBegin_txt.text = "Next"

            } else if (currentPage == 2) {
               // letsBegin_txt.text = "Let's Begin"
                letsBegin_fml1.setOnClickListener{
                    currentPage--
                    activeDot(currentPage)
                    sliderScreen.setCurrentItem(currentPage, true)
                }

            } else {
                finish()
                AppUtils.startActivityRightToLeft(this, Intent(this,AuthActivity::class.java))
              //  goForVerificationScreen(AuthActivity::class.java,"","","","")

            }
        }
        skip1.setOnClickListener{
            finish()
            AppUtils.startActivityRightToLeft(this, Intent(this,AuthActivity::class.java))
        }

    }

    fun activeDot(position: Int) {
        currentPage = position
        if (position == 0) {
            dot_one.setImageResource(R.drawable.selected_dot)
            dot_two.setImageResource(R.drawable.unselect_dot)
            dot_three.setImageResource(R.drawable.unselect_dot)
        } else if (position == 1) {
            dot_one.setImageResource(R.drawable.unselect_dot)
            dot_two.setImageResource(R.drawable.selected_dot)
            dot_three.setImageResource(R.drawable.unselect_dot)
        } else if (position == 2) {
            dot_one.setImageResource(R.drawable.unselect_dot)
            dot_two.setImageResource(R.drawable.unselect_dot)
            dot_three.setImageResource(R.drawable.selected_dot)
        } else {
        }
    }


    private fun checkVisible(position: Int) {
        if (position == 0) {
            letsBegin_fml1.visibility = View.GONE

          //  letsBegin_txt.text = "Next"
        }
        if (position == 1) {
          //  letsBegin_txt.text = "Next"
            letsBegin_fml1.setOnClickListener{
                currentPage--
                activeDot(currentPage)
                sliderScreen.setCurrentItem(currentPage, true)
            }

            letsBegin_fml1.visibility = View.VISIBLE
        }
        if (position == 2) {
          //  letsBegin_txt.text = "Let's Begin"\
            letsBegin_fml1.setOnClickListener{
                currentPage--
                activeDot(currentPage)
                sliderScreen.setCurrentItem(currentPage, true)
            }
            letsBegin_fml1.visibility = View.VISIBLE
        }
    }


    class SliderViewPagerAdapter(val context: Context, val layouts: IntArray) : PagerAdapter() {
        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view: View =
                LayoutInflater.from(context).inflate(layouts.get(position), container, false)
            container.addView(view)
            return view
        }

        override fun getCount(): Int {
            return layouts.size
        }

        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return view === obj
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            val view = `object` as View
            container.removeView(view)
        }


    }

}