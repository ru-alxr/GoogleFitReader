package mx.alxr.googlefitreader.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.fitness.Fitness
import dagger.android.AndroidInjection
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasAndroidInjector
import mx.alxr.googlefitreader.base.SingleEvent
import mx.alxr.googlefitreader.databinding.ActivityMainBinding
import mx.alxr.googlefitreader.models.BloodPressureItem
import mx.alxr.googlefitreader.models.MainModel
import mx.alxr.googlefitreader.models.PermissionRequestArguments
import mx.alxr.googlefitreader.viewmodel.MainActivityViewModel
import javax.inject.Inject

private const val GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 1

class MainActivity : AppCompatActivity(), HasAndroidInjector {

    @Inject
    lateinit var factory: ViewModelProvider.Factory

    @Inject
    lateinit var androidInjector: DispatchingAndroidInjector<Any?>

    private lateinit var viewModel: MainActivityViewModel
    private lateinit var binding: ActivityMainBinding

    override fun androidInjector(): AndroidInjector<Any?> {
        return androidInjector
    }

    override fun getDefaultViewModelProviderFactory(): ViewModelProvider.Factory {
        return factory
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        viewModel = ViewModelProvider(this, factory).get(MainActivityViewModel::class.java)
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(LayoutInflater.from(this))
        setContentView(binding.root)
        binding.enableGoogleFitView.setOnClickListener { viewModel.onEnableGoogleFitClicked() }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = BloodPressureAdapter()
        binding.swipeToRefresh.setOnRefreshListener(this::onRefreshRequested)
        viewModel.getMainModelLiveData().observe(
            this,
            this::onMainModelChanged
        )
        viewModel.getBloodPressureRecordListLiveData().observe(
            this,
            this::onBloodPressureRecordListChanged
        )
    }

    private fun onRefreshRequested() {
        viewModel.onRefreshRequested()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // TODO: I cannot find another way to receive permission request result
        // TODO: rather than deprecated onActivityResult
        // TODO: @mx_alxr
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_FIT_PERMISSIONS_REQUEST_CODE) {
            viewModel.onPermissionGranted(resultCode == RESULT_OK)
        }
    }

    private fun onMainModelChanged(model: MainModel?) {
        if (model == null) {
            return
        }
        binding.enableGoogleFitView.visibility = model.permissionRequestViewVisible.visibility()
        binding.swipeToRefresh.isEnabled = model.swipeEnabled
        binding.swipeToRefresh.isRefreshing = model.swipeRefreshing
        binding.googleFitSyncDate.text = model.lastSyncDate
        binding.root.visibility = model.isContentVisible.visibility(INVISIBLE)
        if (!model.isDemoConsumed) {
            binding.root.postDelayed({ viewModel.onDemoShown() }, 500)
        }
        binding.root.postDelayed({ binding.root.visibility = VISIBLE }, 500L)
        handleHistoryClientRequestEvent(model.requestHistoryClientEvent)
        handlePermissionRequestEvent(model.permissionRequestEvent)
        handleErrorMessageEvent(model.errorMessageEvent)
    }

    private fun handleHistoryClientRequestEvent(event: SingleEvent<GoogleSignInAccount>?) {
        event?.getValue()?.apply {
            // TODO: I haven't checked sources of Fitness, I only assume that client tied with activity lifecycle.
            // TODO: That's why I create History Client in this place
            // TODO: @mx_alxr
            viewModel.onHistoryClientCreated(
                Fitness.getHistoryClient(this@MainActivity, this)
            )
        }
    }

    private fun handlePermissionRequestEvent(event: SingleEvent<PermissionRequestArguments>?) {
        event?.getValue()?.apply {
            GoogleSignIn.requestPermissions(
                this@MainActivity,
                GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                this.account,
                this.fitnessOptions
            )
        }
    }

    private fun onBloodPressureRecordListChanged(list: List<BloodPressureItem>?) {
        println("MainActivity##onBloodPressureRecordListChanged: " + list?.size)
        val adapter: BloodPressureAdapter? = binding.recyclerView.adapter as BloodPressureAdapter?
        adapter?.update(list)
    }

    private fun handleErrorMessageEvent(event: SingleEvent<String>?) {
        event?.getValue()?.apply {
            Toast.makeText(this@MainActivity, this, Toast.LENGTH_SHORT).show()
        }
    }

    private fun Boolean.visibility(defaultValue: Int = View.GONE): Int {
        return if (this) {
            VISIBLE
        } else {
            defaultValue
        }
    }

}