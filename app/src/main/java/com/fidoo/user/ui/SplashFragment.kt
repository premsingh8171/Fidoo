package com.fidoo.user.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.fidoo.user.R
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.utils.BaseFragment
import kotlinx.android.synthetic.main.fragment_splash.*


class SplashFragment : BaseFragment() {

    lateinit var mView: View
    lateinit var mSessionTwiclo: SessionTwiclo

    override fun provideYourFragmentView(
        inflater: LayoutInflater?,
        parent: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mView = inflater!!.inflate(R.layout.fragment_splash, parent, false)
//        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
//        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        //splashBinding.fidooSplashLogo.startAnimation(animation)\
        mSessionTwiclo = SessionTwiclo(requireContext())
        val fadeanim: ObjectAnimator = ObjectAnimator.ofFloat(fidooSplashLogo, "alpha" , 0.0f, 1f ).apply {
            duration = 500
        }
        val scaleXAnim: ObjectAnimator = ObjectAnimator.ofFloat(fidooSplashLogo, "scaleX", 0.4f, 0.6f ).apply {
            duration = 1000

        }

        val scaleYAnim: ObjectAnimator = ObjectAnimator.ofFloat(fidooSplashLogo, "scaleY", 0.4f, 0.6f ).apply {
            duration = 1000
        }


        val scaleAnimation = AnimatorSet().apply {
            play(fadeanim).before(scaleXAnim).before(scaleYAnim)
            play(scaleXAnim).with(scaleYAnim)

        }
        AnimatorSet().apply {
            play(scaleAnimation)
            start()
        }

//        Handler().postDelayed({
//            // This method will be executed once the timer is over
//            // Start your app main activity
//
//            findNavController().navigate(R.id.action_splashFragment_to_authFragment)
////            startActivity(Intent(requireContext(), LoginActivity::class.java))
////            // close this activity
////            finish()
//        }, 2000)

        Handler().postDelayed({
            // This method will be executed once the timer is over
            // Start your app main activity

            // Check for the logged in status
            if (mSessionTwiclo.isLoggedIn) {
                goForVerificationScreen(MainActivity::class.java,"","","","")
                return@postDelayed
            }else{
                try {
                    findNavController().navigate(R.id.action_splashFragment_to_authFragment)
                }catch (e:Exception){
                    e.printStackTrace()
                }

            }

            // close this activity
//            finish()
        }, 3000)

        return mView
    }



}