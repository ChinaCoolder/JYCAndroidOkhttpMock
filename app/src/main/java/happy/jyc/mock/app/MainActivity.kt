package happy.jyc.mock.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import happy.jyc.mock.app.databinding.ActivityMainBinding
import happy.jyc.mock.app.viewmodel.MainViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val mainViewModel by viewModels<MainViewModel>()

    @Inject
    lateinit var picasso: Picasso

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main).run {
            request.setOnClickListener {
                mainViewModel.fetchUser()
            }
            requestWithResponseHeader.setOnClickListener {
                mainViewModel.fetchUserWithResponseHeader()
            }
            disableRequest.setOnClickListener {
                mainViewModel.fetchUserDisable()
            }
            getUserByParam.setOnClickListener {
                mainViewModel.fetchUserByParam()
            }
            getUserByHeader.setOnClickListener {
                mainViewModel.fetchUserByHeader()
            }
            getUserWait.setOnClickListener {
                mainViewModel.fetchWaitUser()
            }
            loadImageWithPicasso.setOnClickListener {
                picasso
                    .load("https://www.google.com/images/sample1")
                    .into(image)
            }
            loadImageWithGlide.setOnClickListener {
                Glide.with(applicationContext)
                    .load("https://www.google.com/images/sample")
                    .into(image)
            }
            loadImageWithFresco.setOnClickListener {
                image.setImageURI("https://www.google.com/images/sample2")
            }
        }
        lifecycleScope.run {
            launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    mainViewModel.user.filter {
                        it != null
                    }.collect {
                        showDialog(it!!.toString())
                    }
                }
            }
            launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    mainViewModel.userWithHeader.filter {
                        it != null
                    }.collect {
                        showDialog(it!!.toString())
                    }
                }
            }
            launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    mainViewModel.error.filter {
                        it != null
                    }.collect {
                        showDialog(it!!)
                    }
                }
            }
            launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    mainViewModel.disableUser.filter {
                        it != null
                    }.collect {
                        showDialog(it!!.toString())
                    }
                }
            }
            launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    mainViewModel.userByParam.filter {
                        it != null
                    }.collect {
                        showDialog(it!!.toString())
                    }
                }
            }
            launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    mainViewModel.userByHeader.filter {
                        it != null
                    }.collect {
                        showDialog(it!!.toString())
                    }
                }
            }
            launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    mainViewModel.userWait.filter {
                        it != null
                    }.collect {
                        showDialog(it!!.toString())
                    }
                }
            }
        }

    }

    private fun showDialog(message: String) {
        AlertDialog.Builder(this@MainActivity)
            .setMessage(message)
            .create()
            .show()
    }
}