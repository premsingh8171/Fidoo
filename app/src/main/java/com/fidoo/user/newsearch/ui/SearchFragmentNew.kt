package com.fidoo.user.newsearch.ui

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.fidoo.user.R
import com.fidoo.user.dailyneed.ui.ServiceDailyNeedActivity
import com.fidoo.user.data.CheckConnectivity
import com.fidoo.user.data.session.SessionTwiclo
import com.fidoo.user.newsearch.adapter.SearchSuggestionsAdapter
import com.fidoo.user.newsearch.model.Suggestion
import com.fidoo.user.newsearch.viewmodel.SearchNewViewModel
import com.fidoo.user.sendpackages.activity.SendPackageActivity
import com.fidoo.user.store.activity.StoreListActivity
import com.fidoo.user.user_tracker.viewmodel.UserTrackerViewModel
import com.google.gson.Gson
import com.premsinghdaksha.startactivityanimationlibrary.AppUtils
import kotlinx.android.synthetic.main.fragment_search_new.view.*

@Suppress("DEPRECATION")
class SearchFragmentNew : Fragment() {
    var viewmodel: SearchNewViewModel? = null
    var viewmodelusertrack: UserTrackerViewModel? = null
    var storeID: String = ""
    var mmContext: Context? = null

    var tempType: String? = ""
    var search_value: String? = ""
    var customIdsList: ArrayList<String>? = null
    private var timer: CountDownTimer? = null
    lateinit var mView: View
    private var _progressDlg: ProgressDialog? = null
    lateinit var sessionTwiclo: SessionTwiclo
    var cartId: String = ""
    var changeLable1: Long = 2
    var changeLable2: Long = 4

    var searchSuggestionAdapter: SearchSuggestionsAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mView = inflater.inflate(R.layout.fragment_search_new, container, false)
        sessionTwiclo = SessionTwiclo(requireContext())
        viewmodel = ViewModelProviders.of(requireActivity()).get(SearchNewViewModel::class.java)
        viewmodelusertrack =
            ViewModelProviders.of(requireActivity()).get(UserTrackerViewModel::class.java)

//        mView.searchKeyETxt?.addTextChangedListener(object : TextWatcher {
//            override fun afterTextChanged(s: Editable) {
//                Log.d("afterTextChanged_", s.toString())
//            }
//            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
//            override fun onTextChanged(
//                s: CharSequence, start: Int,
//                before: Int, count: Int
//            ) {
//                search_value = s.toString()
//            }
//        })


        timer = object : CountDownTimer(6000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                Log.e("_Timer", "seconds remaining: " + millisUntilFinished / 1000)
                if (changeLable1 == (millisUntilFinished / 1000)) {
                    mView.searchKeyETxt.hint = "Search Products"
                } else if (changeLable2 == (millisUntilFinished / 1000)) {
                    mView.searchKeyETxt.hint = "Search Restaurants"
                } else {
                    mView.searchKeyETxt.hint = "Search Dishes"
                }
            }

            override fun onFinish() {
                timer!!.start()
            }
        }

        timer!!.start()

        apiCall()
        getResponse()
        onclick()

        return mView
    }


    private fun onclick() {

        mView.editTxt.setOnClickListener {}
        mView.editTxt.setTextColor(resources.getColor(R.color.colorTextGray))

        mView.searchKeyETxt.setOnClickListener {
            startActivity(Intent(context, NewSearchActivity::class.java).putExtra("service_id", ""))
        }

    }

    private fun apiCall() {
        showIOSProgress()
        if (sessionTwiclo.isLoggedIn) {
            viewmodel!!.searchSuggestionsApi(
                sessionTwiclo.loggedInUserDetail.accountId,
                sessionTwiclo.loggedInUserDetail.accessToken
            )
        } else {
            viewmodel!!.searchSuggestionsApi(
                "",
                ""
            )
        }
    }

    private fun searchSuggestions(suggestions: ArrayList<Suggestion>) {
        searchSuggestionAdapter = SearchSuggestionsAdapter(
            mmContext!!,
            suggestions,
            object : SearchSuggestionsAdapter.SuggestionsSearchItemClick {
                override fun onItemClick(pos: Int, model: Suggestion) {
                    Log.d("dfdmodelfdf",model.id)
                    if (model.id.equals("5")||model.id.equals("7")) {
                        startActivity(
                            Intent(
                                context,
                                StoreListActivity::class.java
                            )
                                .putExtra("serviceId", model.id)
                                .putExtra("serviceName", model.service_name)
                        )
                    } else if (model.id.equals("4")) {
                        startActivity(
                            Intent(
                                context,
                                ServiceDailyNeedActivity::class.java
                            )
                                .putExtra("serviceId", model.id)
                                .putExtra("serviceName", model.service_name)
                        )
                    } else {
                        AppUtils.startActivityRightToLeft(
                            requireActivity(),
                            Intent(requireActivity(), ServiceDailyNeedActivity::class.java).putExtra(
                                "serviceId", model.id
                            ).putExtra("serviceName", model.service_name)
                        )
                    }
//                    startActivity(
//                        Intent(
//                            context,
//                            NewSearchActivity::class.java
//                        ).putExtra("service_id", "")
//                    )
                }
            })
        mView.recyclerviewList.adapter = searchSuggestionAdapter
    }

    private fun getResponse() {
        viewmodel!!.searchSuggestionsRes!!.observe(requireActivity(), {
            Log.d("searchSuggestionsRes", Gson().toJson(it))
            dismissIOSProgress()
            if (it.error_code == 200) {
                searchSuggestions(it.suggestions as ArrayList)
            }

        })

        viewmodel!!.failureRes!!.observe(requireActivity(), {
            dismissIOSProgress()
        })

    }

    fun showIOSProgress() {
        closeProgress()
        _progressDlg = ProgressDialog(requireContext(), R.style.TransparentProgressDialog)
        _progressDlg!!.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        _progressDlg!!.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        _progressDlg!!.setCancelable(false)
        _progressDlg!!.show()
    }

    fun closeProgress() {
        if (_progressDlg == null) {
            return
        }

        _progressDlg!!.dismiss()
        _progressDlg = null

    }

    fun dismissIOSProgress() {
        if (_progressDlg == null) {
            return
        }

        _progressDlg!!.dismiss()
        _progressDlg = null
    }

    fun isNetworkConnected(): Boolean {
        return CheckConnectivity(requireContext()).isNetworkAvailable
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mmContext = context
    }

}