package com.guilhermeb.mymoney.view.money.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.google.android.play.core.review.ReviewManagerFactory
import com.guilhermeb.mymoney.BuildConfig
import com.guilhermeb.mymoney.R
import com.guilhermeb.mymoney.common.constant.Constants
import com.guilhermeb.mymoney.common.helper.SharedPreferencesHelper
import com.guilhermeb.mymoney.common.helper.getSharedPreferencesKey
import com.guilhermeb.mymoney.common.util.getAppVersion
import com.guilhermeb.mymoney.databinding.ActivityMoneyHostBinding
import com.guilhermeb.mymoney.databinding.NavHeaderDrawerBinding
import com.guilhermeb.mymoney.view.app.activity.AbstractActivity
import com.guilhermeb.mymoney.viewmodel.money.MoneyViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MoneyHostActivity : AbstractActivity() {

    private lateinit var binding: ActivityMoneyHostBinding

    private lateinit var appBarConfiguration: AppBarConfiguration

    private lateinit var mDrawerToggle: ActionBarDrawerToggle
    private lateinit var mDrawerLayout: DrawerLayout

    private lateinit var mDrawerMenuMonths: List<String>

    @Inject
    lateinit var moneyViewModel: MoneyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMoneyHostBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setTitle(R.string.app_name)

        if (!BuildConfig.IS_FREE) {
            fetchDataFromFirebaseRTDB()
        }
        initScreen()
        observeProperties()
        fetchDrawerData()
        // == -- == -- == -- == -- == -- == --
    }

    private fun fetchDataFromFirebaseRTDB() {
        val useRealTimeListener = SharedPreferencesHelper.getInstance()
            .getValue(getSharedPreferencesKey(Constants.UPDATE_CHANGES_IN_REAL_TIME), false)
        if (useRealTimeListener) {
            moneyViewModel.observeMoneyItemsFirebaseRTDB()
        } else {
            val intentActivity = intent
            if (intentActivity.hasExtra(Constants.INTENT_EXTRA_KEY_FETCH_DATA_FROM_FIREBASE_RTDB) &&
                intentActivity.getBooleanExtra(
                    Constants.INTENT_EXTRA_KEY_FETCH_DATA_FROM_FIREBASE_RTDB,
                    false
                )
            ) {
                moneyViewModel.fetchDataFromFirebaseRTDB()
            }
        }
    }

    private fun initScreen() {
        // AppBar ----------
        setSupportActionBar(binding.toolbar)

        binding.toolbar.setNavigationOnClickListener {
            if (mDrawerToggle.isDrawerIndicatorEnabled) {
                mDrawerLayout.openDrawer(GravityCompat.START)
            } else {
                onBackPressed()
            }
        }

        // == -- == -- == -- Drawer Menu == -- == -- == --
        mDrawerLayout = binding.drawerLayout
        mDrawerToggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.open_slide_menu,
            R.string.close_slide_menu
        )
        mDrawerToggle.setToolbarNavigationClickListener {
            if (mDrawerToggle.isDrawerIndicatorEnabled) {
                mDrawerLayout.openDrawer(GravityCompat.START)
            } else {
                onBackPressed()
            }
        }
        binding.drawerLayout.addDrawerListener(mDrawerToggle)
        mDrawerToggle.syncState()

        val navHeaderDrawerBinding: NavHeaderDrawerBinding =
            NavHeaderDrawerBinding.bind(binding.navViewDrawer.getHeaderView(0))

        navHeaderDrawerBinding.txtAppVersion.text = getAppVersion()
        navHeaderDrawerBinding.txtEmail.text = moneyViewModel.getCurrentUserEmail()

        setupFeaturesFlavors(navHeaderDrawerBinding)
        handleEvents(navHeaderDrawerBinding)

        binding.navViewDrawer.setNavigationItemSelectedListener {
            if (this::mDrawerMenuMonths.isInitialized) {
                moneyViewModel.setSelectedYearAndMonthName(mDrawerMenuMonths[it.itemId])
                moneyViewModel.setDrawerMenuItemChecked(it.order)
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            return@setNavigationItemSelectedListener true
        }
    }

    private fun setupFeaturesFlavors(navHeaderDrawerBinding: NavHeaderDrawerBinding) {
        if (!BuildConfig.IS_FREE) {
            navHeaderDrawerBinding.txtKnowProVersion.visibility = View.GONE
        }
    }

    private fun handleEvents(navHeaderDrawerBinding: NavHeaderDrawerBinding) {
        navHeaderDrawerBinding.txtRateTheApp.setOnClickListener {
            val manager = ReviewManagerFactory.create(this)
            val request = manager.requestReviewFlow()
            request.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // We got the ReviewInfo object
                    val reviewInfo = task.result
                    val flow = manager.launchReviewFlow(this, reviewInfo)
                    flow.addOnCompleteListener {
                        // The flow has finished. The API does not indicate whether the user
                        // reviewed or not, or even whether the review dialog was shown. Thus, no
                        // matter the result, we continue our app flow.
                    }
                } else {
                    // There was some problem.
                    task.exception?.let { exception ->
                        exception.localizedMessage?.let { message ->
                            Log.e("REVIEW", message)
                        }
                    }
                    goToPlayStore(packageName)
                }
            }
        }

        navHeaderDrawerBinding.txtKnowProVersion.setOnClickListener {
            val packageNameProVersion = "com.guilhermeb.mymoney.pro"
            goToPlayStore(packageNameProVersion)
        }
    }

    private fun goToPlayStore(appPackageName: String) {
        val appUri = Uri.parse("market://details?id=$appPackageName")
        val goToMarketIntent = Intent(Intent.ACTION_VIEW, appUri)

        if (goToMarketIntent.resolveActivity(packageManager) != null) {
            // To count with Play market backstack, After pressing back button, to taken back to our application, we need to add following flags to intent.
            goToMarketIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            startActivity(goToMarketIntent)
        } else {
            val uri =
                Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }
    }

    private fun observeProperties() {
        moneyViewModel.drawerMonths.observe(this) { drawerMenuMonths ->
            mDrawerMenuMonths = drawerMenuMonths

            binding.navViewDrawer.menu.clear()

            for (i in drawerMenuMonths.indices) {
                val textMenuDrawer = drawerMenuMonths[i]
                // groupId, itemId, order, title
                binding.navViewDrawer.menu.add(R.id.group_menu_drawer, i, i, textMenuDrawer)
                    .setIcon(R.drawable.ic_baseline_calendar_month_24)
            }

            // After adding the menus
            binding.navViewDrawer.menu.setGroupCheckable(R.id.group_menu_drawer, true, true)

            moneyViewModel.drawerMenuItemChecked.value?.let {
                if (it >= 0 && binding.navViewDrawer.menu.size() > it) {
                    binding.navViewDrawer.menu.getItem(it).isChecked = true
                }
            }
        }

        moneyViewModel.drawerMenuItemChecked.observe(this) {
            if (it >= 0 && binding.navViewDrawer.menu.size() > it) {
                binding.navViewDrawer.menu.getItem(it).isChecked = true
            }
        }
    }

    private fun fetchDrawerData() {
        moneyViewModel.getAllMonthsByUser(moneyViewModel.getCurrentUserEmail()!!)
    }

    fun showBackButton(isBack: Boolean) {
        mDrawerToggle.isDrawerIndicatorEnabled = !isBack
        supportActionBar!!.setDisplayHomeAsUpEnabled(isBack)
        mDrawerToggle.syncState()
        if (isBack) {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        } else {
            mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
        }
    }

    override fun onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_item_detail)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}