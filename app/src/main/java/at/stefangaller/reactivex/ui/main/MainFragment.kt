package at.stefangaller.reactivex.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import at.stefangaller.reactivex.R
import com.jakewharton.rxbinding4.view.clicks
import com.jakewharton.rxbinding4.view.visibility
import com.jakewharton.rxbinding4.widget.textChanges
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.main_fragment.*
import java.util.concurrent.TimeUnit

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // connect to ViewModel IN streams
        searchButton.clicks().subscribe(viewModel.clickSubject)
        searchField.textChanges().subscribe(viewModel.textSubject)

        // connect to ViewModel OUT streams
        viewModel.apply {
            errorLiveData.observe(viewLifecycleOwner, Observer {
                error.text = it
            })

            searchResultLiveData.observe(viewLifecycleOwner, Observer {
                result.text = it
            })

            showLoadingLiveData.observe(viewLifecycleOwner, Observer {
                loading.visibility = if (it) View.VISIBLE else View.GONE
            })
        }
    }

}